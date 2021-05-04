import java.util.Scanner;

public class verifier {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String message = sc.nextLine();

        if (message.startsWith("[ERROR]")) {
            System.out.println(message);
            System.exit(0);
        }

        String key = sc.nextLine();

        String remainder = generator.divide(message, key);
        if (remainder.equals("0")) {
            System.out.println("Message correctly received.");
        } else {
            System.out.println("Message is incorrect.");

        }
        sc.close();
    }
}
