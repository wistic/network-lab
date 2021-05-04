import java.net.*;
import java.io.*;

public class server {

    private Socket connection = null;
    private ServerSocket ss = null;
    private ObjectInputStream inStream = null;
    private ObjectOutputStream outStream = null;

    public server(int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            connection = ss.accept();
            System.out.println("Client with IP address " + connection.getInetAddress() + " connected.");

            outStream = new ObjectOutputStream(connection.getOutputStream());
            inStream = new ObjectInputStream(connection.getInputStream());

            while (true) {
                Packet packet = null;
                try {
                    packet = (Packet) inStream.readObject();
                } catch (Exception e) {
                    System.out.println("Client closed the connection.");
                    inStream.close();
                    outStream.close();
                    connection.close();
                    System.exit(0);
                }
                if (packet == null)
                    break;
                packet.type = 0;
                outStream.writeObject(packet);
            }

            System.out.println("Closing connection with " + connection.getInetAddress());
            inStream.close();
            outStream.close();
            connection.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new server(8888);
    }
}
