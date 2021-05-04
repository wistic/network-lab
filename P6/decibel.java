import java.util.InputMismatchException;
import java.util.Scanner;

public class decibel {
    public static void main(String[] args) {
        System.out.println("[Decibel Calculator]");
        System.out.println("Select the input unit:");
        System.out.println("1. Watt (W)\n2. decibel Watt (dBW)\n3. decibel MilliWatt (dBm)");

        Scanner sc = new Scanner(System.in);

        int selection = 0;
        try {
            selection = sc.nextInt();
        } catch (Exception e) {
            System.out.println("[ERROR] Wrong unit selection.");
            System.exit(0);
        }
        if ((selection < 1) || (selection > 3)) {
            System.out.println("[ERROR] Wrong unit selection.");
            System.exit(0);
        }

        System.out.println("Enter transmit power.");
        double power = 0;
        try {
            power = sc.nextDouble();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid input power.");
            System.exit(0);
        }

        if (selection == 1) {
            double dBWpower = 10 * Math.log10(power);
            double dBmpower = dBWpower + 30;

            System.out.println("[OUTPUT] Transmit power in decibel Watts (dBw): " + dBWpower);
            System.out.println("[OUTPUT] Transmit power in decibel MilliWatts (dBm): " + dBmpower);
        } else if (selection == 2) {
            double Wpower = Math.pow(10.0, power / 10);
            System.out.println("[OUTPUT] Transmit power in Watts (W): " + Wpower);
        } else {
            double Wpower = Math.pow(10.0, (power - 30) / 10);
            System.out.println("[OUTPUT] Transmit power in Watts (W): " + Wpower);
        }

        sc.close();
    }
}
