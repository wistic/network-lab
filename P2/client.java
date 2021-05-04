import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class client {
    static int packet_sent_count = 0;
    static int packet_received_count = 0;
    static long total_time = 0;
    static long min_time = 0;
    static long max_time = 0;

    public static void main(String[] args) throws Exception {

        int sequence_no = 1;

        Socket s = new Socket("127.0.0.1", 8888);

        ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    System.out.println("\n\n----------------Ping Statistics-----------------");
                    double loss = ((packet_sent_count - packet_received_count) * 1.0 / packet_sent_count) * 100;
                    System.out.println(packet_sent_count + " packets transmitted, " + packet_received_count
                            + " packets received, " + loss + "% packet loss, time " + total_time + " ms");
                    double avg_time = total_time * 1.0 / packet_received_count;
                    System.out.println("rtt avg/min/max = " + avg_time + "/" + min_time + "/" + max_time + " ms");
                    inStream.close();
                    outStream.close();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        while (true) {
            Packet packet = new Packet(8, 12, sequence_no);
            outStream.writeObject(packet);
            sequence_no++;
            packet_sent_count++;

            Packet pkt = (Packet) inStream.readObject();
            if (pkt == null)
                break;
            if (pkt.type == 0) {
                packet_received_count++;
                long ping_time = System.currentTimeMillis() - pkt.payload.getTime();
                total_time += ping_time;
                if (ping_time > max_time)
                    max_time = ping_time;
                else if (ping_time < min_time)
                    min_time = ping_time;
                System.out.println("Reply received from localhost (127.0.0.1) seq=" + pkt.sequence_no + " ttl=119 time="
                        + ping_time + " ms");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("Closing connection ");
        inStream.close();
        outStream.close();
        s.close();
    }
}
