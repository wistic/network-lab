import java.util.HashMap;
import java.util.Scanner;

public class ipheader {

    public static HashMap<String, Long> getInput() {
        Scanner sc = new Scanner(System.in);
        HashMap<String, Long> header = new HashMap<String, Long>();

        System.out.println("[INFO] Enter every value in decimal notation.");

        System.out.println("Enter the IP version.");
        long ip_version = 0;
        try {
            ip_version = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid IP version.");
            System.exit(0);
        }
        if ((ip_version != 4) && (ip_version != 6)) {
            System.out.println("[ERROR] Invalid IP version.");
            System.exit(0);
        }
        header.put("ip_version", ip_version);

        System.out.println("Enter the Header length.");
        long header_length = 0;
        try {
            header_length = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Header length.");
            System.exit(0);
        }
        if (header_length < 5) {
            System.out.println("[ERROR] Invalid Header length.");
            System.exit(0);
        }
        header.put("header_length", header_length);

        System.out.println("Enter Type of Service.");
        long service_type = 0;
        try {
            service_type = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Service Type.");
            System.exit(0);
        }
        header.put("service_type", service_type);

        System.out.println("Enter Total length.");
        long total_length = 0;
        try {
            total_length = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Total length.");
            System.exit(0);
        }
        if ((total_length > 65535) || (total_length < 0)) {
            System.out.println("[ERROR] Invalid Total length.");
            System.exit(0);
        }
        header.put("total_length", total_length);

        System.out.println("Enter Identification.");
        long identification = 0;
        try {
            identification = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Identification.");
            System.exit(0);
        }
        header.put("identification", identification);

        System.out.println("Enter Don't Fragment bit.");
        long df_bit = 0;
        try {
            df_bit = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid DF bit.");
            System.exit(0);
        }
        if ((df_bit != 1) && (df_bit != 0)) {
            System.out.println("[ERROR] Invalid DF bit.");
            System.exit(0);
        }
        header.put("df_bit", df_bit);

        System.out.println("Enter More Fragment bit.");
        long mf_bit = 0;
        try {
            mf_bit = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid MF bit.");
            System.exit(0);
        }
        if ((mf_bit != 1) && (mf_bit != 0)) {
            System.out.println("[ERROR] Invalid MF bit.");
            System.exit(0);
        }
        header.put("mf_bit", mf_bit);

        System.out.println("Enter Fragment offset.");
        long offset = 0;
        try {
            offset = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Fragment offset.");
            System.exit(0);
        }
        if (offset < 0) {
            System.out.println("[ERROR] Invalid Fragment offset.");
            System.exit(0);
        }
        header.put("offset", offset);

        System.out.println("Enter Time to Live.");
        long ttl = 0;
        try {
            ttl = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Time to Live.");
            System.exit(0);
        }
        if (ttl < 0) {
            System.out.println("[ERROR] Invalid Time to Live.");
            System.exit(0);
        }
        header.put("ttl", ttl);

        System.out.println("Enter Protocol.");
        long protocol = 0;
        try {
            protocol = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Protocol.");
            System.exit(0);
        }
        header.put("protocol", protocol);

        System.out.println("Enter checksum sent by source.");
        long source_checksum = 0;
        try {
            source_checksum = sc.nextLong();
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Source Checksum.");
            System.exit(0);
        }
        if ((source_checksum > 65535) || (source_checksum < 0)) {
            System.out.println("[ERROR] Invalid Source Checksum.");
            System.exit(0);
        }
        header.put("source_checksum", source_checksum);

        sc.nextLine();
        System.out.println("Enter source IP in dotted decimal notation.");
        String temp = sc.nextLine();
        String arr[] = temp.split("\\.");
        if (arr.length != 4) {
            System.out.println("[ERROR] Invalid Source IP.");
            System.exit(0);
        }
        temp = "";
        try {
            for (String i : arr) {
                temp += getQualifiedValue(Long.parseLong(i), 8);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid Source IP.");
            System.exit(0);
        }
        header.put("source_ip", Long.parseLong(temp, 2));

        System.out.println("Enter destination IP in dotted decimal notation.");
        temp = sc.nextLine();
        arr = temp.split("\\.");
        if (arr.length != 4) {
            System.out.println("[ERROR] Invalid destination IP.");
            System.exit(0);
        }
        temp = "";
        try {
            for (String i : arr) {
                temp += getQualifiedValue(Long.parseLong(i), 8);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid destination IP.");
            System.exit(0);
        }
        header.put("destination_ip", Long.parseLong(temp, 2));

        sc.close();
        return header;
    }

    public static String toHexString(String binaryString[]) {
        String hexString = "";
        for (String s : binaryString) {
            String temp = Long.toHexString(Long.parseLong(s, 2));
            if (temp.length() < 4) {
                hexString += new String(new char[4 - temp.length()]).replace("\0", "0") + temp + " ";
            } else if (temp.length() == 4)
                hexString += temp + " ";
        }
        return hexString;
    }

    public static String getQualifiedValue(long value, int length) {
        String temp = Long.toBinaryString(value);
        if (temp.length() < length) {
            return new String(new char[length - temp.length()]).replace("\0", "0") + temp;
        }
        return temp;
    }

    public static String[] toBinaryWords(HashMap<String, Long> header) {
        String arr[] = new String[10];
        arr[0] = getQualifiedValue(header.get("ip_version"), 4) + getQualifiedValue(header.get("header_length"), 4)
                + getQualifiedValue(header.get("service_type"), 8);
        arr[1] = getQualifiedValue(header.get("total_length"), 16);
        arr[2] = getQualifiedValue(header.get("identification"), 16);
        arr[3] = "0" + header.get("df_bit") + header.get("mf_bit") + getQualifiedValue(header.get("offset"), 13);
        arr[4] = getQualifiedValue(header.get("ttl"), 8) + getQualifiedValue(header.get("protocol"), 8);
        arr[5] = getQualifiedValue(header.get("source_checksum"), 16);
        String temp = getQualifiedValue(header.get("source_ip"), 32);
        arr[6] = temp.substring(0, 16);
        arr[7] = temp.substring(16, 32);
        temp = getQualifiedValue(header.get("destination_ip"), 32);
        arr[8] = temp.substring(0, 16);
        arr[9] = temp.substring(16, 32);
        return arr;
    }

    public static void showAddition(String arr[]) {
        String first = arr[0];
        for (int i = 1; i < arr.length; i++) {
            System.out.println("[" + i + "]\t  " + first);
            System.out.println("\t+ " + arr[i]);
            System.out.println("\t  ----------------");
            Long additive = Long.parseLong(first, 2);
            Long adder = Long.parseLong(arr[i], 2);
            Long sum = adder + additive;
            String sumBinary = getQualifiedValue(sum, 16);
            if (sumBinary.length() > 16) {
                System.out.println("\t " + sumBinary);
                System.out.println("\t ^");
                System.out.println("\t One extra bit\n");
                System.out.println("\tWe need to remove this bit and add 1 to the sum.\n");
                System.out.println("\t  " + sumBinary.substring(1));
                System.out.println("\t+ 0000000000000001");
                System.out.println("\t  ----------------");
                sumBinary = getQualifiedValue(sum - 65535, 16);
                System.out.println("\t  " + sumBinary + "\n");
            } else
                System.out.println("\t  " + sumBinary + "\n");
            first = sumBinary;
        }
        System.out.println("Now we need to take the one's compliment of the sum.\n");
        System.out.print("\t 1's compliment of " + first + " : ");
        String compliment = Long.toBinaryString(~(Long.parseLong(first, 2)));
        System.out.println(compliment.substring(48) + "\n");
        System.out.println("Final checksum: " + compliment.substring(48) + "\n");
    }

    public static void removeCheckSum(String arr[]) {
        arr[5] = new String(new char[16]).replace("\0", "0");
    }

    public static void main(String[] args) {
        HashMap<String, Long> h = getInput();
        String arr[] = toBinaryWords(h);

        System.out.println("\nLets find the checksum.");
        System.out.println("Aggregating all bits in 16 bit words and displaying in hexadecimal format: \n");
        System.out.println("\t" + toHexString(arr) + "\n");
        System.out.println("But first let us clear the source checksum. Now: \n");
        removeCheckSum(arr);
        System.out.println("\t" + toHexString(arr) + "\n");
        System.out.println("Now we need to add each of these 16 bit words.\n");
        showAddition(arr);
    }
}
