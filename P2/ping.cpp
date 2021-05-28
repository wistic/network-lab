#include <string>
#include <iostream>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/ip_icmp.h>
#include <cstdint>
#include <chrono>
#include <unistd.h>
#include <csignal>
#include <stdexcept>
#include <arpa/inet.h>

using namespace std;

bool end_loop = false;

bool get_ip_address(const string &host, sockaddr_in &address)
{
    addrinfo *results;

    addrinfo hints = {0};
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;

    if (getaddrinfo(host.c_str(), nullptr, &hints, &results))
    {
        return false;
    }

    for (addrinfo *i = results; i != nullptr; i = i->ai_next)
    {
        if (i->ai_addr->sa_family == AF_INET)
        {
            address = *reinterpret_cast<sockaddr_in *>(i->ai_addr);
            return true;
        }
    }
    return false;
}

void update_statistics(double value, double &min, double &max, double &avg, double &total, size_t &count)
{
    if (count == 0)
    {
        min = value;
        max = value;
        avg = value;
        total = value;
    }
    else
    {
        if (value < min)
        {
            min = value;
        }
        if (value > max)
        {
            max = value;
        }
        total += value;
        avg = ((avg * double(count)) + value) / double(count + 1);
    }

    count++;
}

void sigint_handler(int signum)
{
    end_loop = true;
}

uint64_t current_time()
{
    using namespace std::chrono;
    return duration_cast<microseconds>(steady_clock::now().time_since_epoch()).count();
}

int open_socket(int ttl_limit, int rec_limit)
{
    int handler = socket(AF_INET, SOCK_RAW, IPPROTO_ICMP);
    if (handler < 0)
    {
        cerr << "Can't open ICMP Raw Socket." << endl;
        exit(-1);
    }

    if (setsockopt(handler, SOL_IP, IP_TTL, &ttl_limit, sizeof(ttl_limit)) != 0)
    {
        close(handler);
        cerr << "Can't open ICMP Raw Socket." << endl;
        exit(-1);
    }

    timeval time{};
    time.tv_sec = rec_limit;
    time.tv_usec = 0;
    if (setsockopt(handler, SOL_SOCKET, SO_RCVTIMEO, reinterpret_cast<char *>(&time), sizeof(time)))
    {
        close(handler);
        cerr << "Can't open ICMP Raw Socket." << endl;
        exit(-1);
    }
    return handler;
}

uint16_t checksum(const uint8_t *buffer, size_t size)
{
    auto *buf = const_cast<uint16_t *>(reinterpret_cast<const uint16_t *>(buffer));
    size_t len = size;
    uint32_t sum = 0;
    uint16_t result;

    for (sum = 0; len > 1; len -= 2)
    {
        sum += *buf;
        buf++;
    }
    if (len == 1)
        sum += *(unsigned char *)buf;
    sum = (sum >> 16) + (sum & 0xFFFF);
    sum += (sum >> 16);
    result = ~sum;
    return result;
}

size_t pack(uint8_t buffer[4096], uint16_t sequence, uint16_t id, uint64_t timestamp)
{
    icmp *icmp_packet = reinterpret_cast<icmp *>(buffer);
    icmp_packet->icmp_type = ICMP_ECHO;
    icmp_packet->icmp_code = 0;
    icmp_packet->icmp_cksum = 0;
    icmp_packet->icmp_seq = sequence;
    icmp_packet->icmp_id = id;
    for (size_t i = 0; i < 8; i++)
    {
        reinterpret_cast<uint8_t *>(icmp_packet->icmp_data)[i] = uint8_t(timestamp & 0xFF);
        timestamp >>= 8;
    }

    icmp_packet->icmp_cksum = checksum(buffer, 64);
    return 64;
}

bool send_packet(int handler, const sockaddr_in &address, uint16_t sequence, uint16_t id, uint64_t timestamp)
{
    using namespace std::chrono;
    uint8_t buffer[4096];
    size_t packet_size = pack(buffer, sequence, id, timestamp);
    const auto *socket_address = reinterpret_cast<const sockaddr *>(&address);
    if (sendto(handler, buffer, packet_size, 0, socket_address, sizeof(address)) == -1)
    {
        return false;
    }

    return true;
}

bool unpack(uint8_t buffer[4096], size_t size, uint16_t &sequence, uint16_t &id, uint64_t &timestamp)
{
    ip *ip_packet = reinterpret_cast<ip *>(buffer);
    size_t ip_header_size = ip_packet->ip_hl << 2u;
    icmp *icmp_packet = reinterpret_cast<icmp *>(&buffer[ip_header_size]);
    size -= ip_header_size;

    if (size < 8 || icmp_packet->icmp_type != ICMP_ECHOREPLY)
    {
        return false;
    }

    id = icmp_packet->icmp_id;
    sequence = icmp_packet->icmp_seq;
    uint64_t data = 0;
    uint64_t shift = 0;
    for (size_t i = 0; i < 8; i++)
    {
        data |= uint64_t(reinterpret_cast<uint8_t *>(icmp_packet->icmp_data)[i]) << shift;
        shift += 8;
    }
    timestamp = data;

    return true;
}

bool receive_packet(int handler, sockaddr_in &address, uint16_t &sequence, uint16_t &id, uint64_t &timestamp)
{
    uint8_t buffer[4096];
    address = sockaddr_in{};

    auto *socket_address = reinterpret_cast<sockaddr *>(&address);
    socklen_t socket_address_length = sizeof(address);
    size_t length = recvfrom(handler, buffer, 4096, 0, socket_address, &socket_address_length);
    if (length <= 0)
    {
        return false;
    }
    return unpack(buffer, length, sequence, id, timestamp);
}

string reverse_dns_lookup(sockaddr_in *address)
{
    sockaddr_in temp_addr;
    socklen_t len;
    char buf[NI_MAXHOST];

    temp_addr.sin_family = AF_INET;
    temp_addr.sin_addr.s_addr = address->sin_addr.s_addr;
    len = sizeof(sockaddr_in);

    if (getnameinfo((sockaddr *)&temp_addr, len, buf,
                    sizeof(buf), NULL, 0, NI_NAMEREQD))
    {
        return "";
    }
    string rev_address = string(buf);
    return rev_address;
}

int main(int argc, char *argv[])
{
    signal(SIGINT, sigint_handler);
    uint16_t pid = getpid();
    double min = 0;
    double max = 0;
    double avg = 0;
    double total = 0;
    size_t count = 0;

    if (argc < 2)
    {
        cerr << "Specify the domain name or IP Address." << endl;
        return -1;
    }

    string hostname = string(argv[1]);

    sockaddr_in address = {0};
    if (!get_ip_address(hostname, address))
    {
        cerr << "DNS resolution failed." << endl;
        return -1;
    }
    cout << "PING " << hostname << endl;

    string rev_address = reverse_dns_lookup(&address);
    string display_address;

    if (rev_address != "")
    {
        cout << "Reverse DNS Lookup success: " << rev_address << endl;
        display_address = rev_address + " (" + inet_ntoa(address.sin_addr) + ")";
    }
    else
    {
        display_address = inet_ntoa(address.sin_addr);
        cout << "Reverse DNS Lookup failed." << endl;
    }

    int ping_socket = open_socket(64, 1);
    uint16_t seq = 0;
    while (!end_loop)
    {
        if (!send_packet(ping_socket, address, seq, pid, current_time()))
        {
            continue;
        }

        uint16_t rseq, rpid;
        uint64_t rtimestamp;
        sockaddr_in raddress{};
        if (!receive_packet(ping_socket, raddress, rseq, rpid, rtimestamp))
        {
            continue;
        }
        uint64_t difference = current_time() - rtimestamp;
        double delay = double(difference) / double(1000);
        update_statistics(delay, min, max, avg, total, count);
        cout << "64 bytes from " << display_address << ": icmp_seq=" << rseq << " time=" << delay << " ms" << endl;
        seq++;
    }
    if (count > 0)
    {
        cout << "\n---" << hostname << " ping statistics ---" << endl;
        double loss = (double(seq - count) / double(seq)) * 100;
        cout << seq << " packets transmitted, " << count << " received, " << loss << "% loss, time " << total
             << "ms\nmin/avg/max = " << min << "/" << avg << "/" << max << " ms" << endl;
    }
    return 0;
}
