import java.util.Arrays;
import java.util.Scanner;

public class cidr {

    public static int[] getSubnetMask(int netmask_bit_count) {
        String binary_subnet = new String(new char[netmask_bit_count]).replace("\0", "1");
        binary_subnet += new String(new char[32 - netmask_bit_count]).replace("\0", "0");

        int subnet_mask[] = new int[4];
        for (int i = 0; i < 4; i++) {
            subnet_mask[i] = Integer.parseInt(binary_subnet.substring(8 * i, 8 * (i + 1)), 2);
        }
        return subnet_mask;
    }

    public static int[] getReverseNetMask(int netmask_bit_count) {
        String binary_subnet = new String(new char[netmask_bit_count]).replace("\0", "0");
        binary_subnet += new String(new char[32 - netmask_bit_count]).replace("\0", "1");

        int subnet_mask[] = new int[4];
        for (int i = 0; i < 4; i++) {
            subnet_mask[i] = Integer.parseInt(binary_subnet.substring(8 * i, 8 * (i + 1)), 2);
        }
        return subnet_mask;
    }

    public static String getDecimalNotation(int[] data) {
        if (data.length != 4)
            return "";
        String outString = "";
        for (int i = 0; i < 4; i++) {
            outString += data[i];
            if (i != 3)
                outString += ".";
        }
        return outString;
    }

    public static int[] getNetworkAddress(int[] ip, int[] subnet_mask) {
        if ((ip.length != 4) || (subnet_mask.length != 4))
            return null;
        int netAddress[] = new int[4];
        for (int i = 0; i < 4; i++) {
            netAddress[i] = ip[i] & subnet_mask[i];
        }
        return netAddress;
    }

    public static int[] getIPRange(int[] netAddress, int[] reverse_subnet_mask) {
        int range[] = new int[8];
        for (int i = 0; i < 4; i++) {
            range[i] = netAddress[i];
            if (i == 3) {
                range[i] += 1;
            }
        }

        for (int i = 0; i < 4; i++) {
            range[i + 4] = netAddress[i] | reverse_subnet_mask[i];
            if (i == 3) {
                range[i + 4] -= 1;
            }
        }

        return range;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the IP Address in CIDR notation.");
        String input = sc.nextLine();
        String temp[] = input.split("/");

        if (temp.length != 2) {
            System.out.println("[ERROR] Bad Input Format: Use CIDR notation.");
            System.exit(0);
        }

        String ip_string = temp[0];
        String ip_split[] = ip_string.split("\\.");
        if (ip_split.length != 4) {
            System.out.println("[ERROR] Bad Input: Bad IP address.");
            System.exit(0);
        }
        int ip[] = new int[4];
        try {
            for (int i = 0; i < 4; i++) {
                ip[i] = Integer.parseInt(ip_split[i]);
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Bad Input: Bad IP address.");
            System.exit(0);
        }
        for (int i = 0; i < 4; i++) {
            if ((ip[i] < 0) || (ip[i] > 255)) {
                System.out.println("[ERROR] Bad Input: Bad IP address.");
                System.exit(0);
            }
        }

        int netmask_bit_count = 0;
        try {
            netmask_bit_count = Integer.parseInt(temp[1]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Bad Input: Improper CIDR notation.");
            System.exit(0);
        }
        if ((netmask_bit_count < 0) || (netmask_bit_count > 32)) {
            System.out.println("[ERROR] Bad Input: Bad netmask.");
            System.exit(0);
        }

        int subnet_mask[] = getSubnetMask(netmask_bit_count);
        int netAddress[] = getNetworkAddress(ip, subnet_mask);
        int reverse_netmask[] = getReverseNetMask(netmask_bit_count);
        int ip_range[] = getIPRange(netAddress, reverse_netmask);

        System.out.println("[OUTPUT] Required information:");
        System.out.println("[OUTPUT] Subnet Mask in dotted decimal notation: " + getDecimalNotation(subnet_mask));
        System.out.println("[OUTPUT] Network Address in dotted decimal notation: " + getDecimalNotation(netAddress));
        System.out.print("[OUTPUT] Usable Host IP Range: ");
        System.out.print("Starting IP " + getDecimalNotation(Arrays.copyOfRange(ip_range, 0, 4)));
        System.out.println(" --- Ending IP " + getDecimalNotation(Arrays.copyOfRange(ip_range, 4, 8)));

        sc.close();
    }
}
