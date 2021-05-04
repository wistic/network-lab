import java.net.Inet4Address;
import java.util.Scanner;

public class alter {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String message = sc.nextLine();
        if (message.startsWith("[ERROR]")) {
            System.out.println(message);
            System.exit(0);
        }

        String key = sc.nextLine();
        sc.close();

        int index = 0;

        if (args.length != 1) {
            System.out.println(message);
            System.out.println(key);
            System.exit(0);
        } else {
            try {
                index = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println(message);
                System.out.println(key);
                System.exit(0);
            }
            if ((index >= message.length()) || (index < 0)) {
                System.out.println(message);
                System.out.println(key);
                System.exit(0);
            } else {
                StringBuilder changed_message = new StringBuilder(message);
                int bit = (changed_message.charAt(index) - '0') ^ 1;
                changed_message.setCharAt(index, (char) (bit + '0'));
                System.out.println(changed_message.toString());
                System.out.println(key);
            }
        }
    }
}
