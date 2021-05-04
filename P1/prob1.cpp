#include <iostream>
#include <unistd.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <ifaddrs.h>
#include <linux/if.h>
#include <cstdio>
#include <cstring>

using namespace std;

int main()
{
    char host_buffer[256];
    char *ip_buffer;
    struct hostent *host_entry;
    int host_name;

    // retrieves hostname
    host_name = gethostname(host_buffer, sizeof(host_buffer));

    // retrieves host information
    host_entry = gethostbyname(host_buffer);

    // converts network address to ASCII string
    ip_buffer = inet_ntoa(*((struct in_addr *)host_entry->h_addr_list[0]));

    cout << "[OUTPUT] Hostname: " << host_buffer << endl;
    cout << "[OUTPUT] Host IP: " << ip_buffer << endl;
    if (strcmp(ip_buffer, "127.0.1.1") == 0)
    {
        cout << "[INFO] This is not your real ip address. Modify /etc/hosts to get the real address.\n[INFO] Refer https://www.debian.org/doc/manuals/debian-reference/ch05.en.html#_the_hostname_resolution." << endl;
    }

    struct ifreq s{};
    int sockfd = socket(PF_INET, SOCK_DGRAM, IPPROTO_IP);

    struct ifaddrs *ifaddr, *ifa;

    if (getifaddrs(&ifaddr) != 0)
    {
        cout << "[ERROR] Unable to resolve network interfaces." << endl;
        return -1;
    }

    for (ifa = ifaddr; ifa != nullptr; ifa = ifa->ifa_next)
    {
        if (ifa->ifa_addr == nullptr || ifa->ifa_addr->sa_family != AF_PACKET)
            continue;

        strcpy(s.ifr_name, ifa->ifa_name);
        if (0 == ioctl(sockfd, SIOCGIFHWADDR, &s))
        {
            auto *hardware_address = (unsigned char *)s.ifr_hwaddr.sa_data;
            printf("[OUTPUT] MAC Address for interface %s: %02X:%02X:%02X:%02X:%02X:%02X\n", ifa->ifa_name,
                   hardware_address[0], hardware_address[1], hardware_address[2],
                   hardware_address[3], hardware_address[4], hardware_address[5]);
        }
    }

    freeifaddrs(ifaddr);

    return 0;
}
