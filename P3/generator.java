import java.math.BigInteger;
import java.util.Scanner;

public class generator {

    public static String divide(String dividend, String divisor) {
        int k = divisor.length();
        BigInteger div = new BigInteger(divisor, 2);
        while (dividend.length() >= divisor.length()) {
            if (dividend.charAt(0) == '0') {
                dividend = new BigInteger(dividend, 2).toString(2);
                if (dividend.length() < divisor.length())
                    break;
            }
            dividend = div.xor(new BigInteger(dividend.substring(0, k), 2)).toString(2) + dividend.substring(k);
        }
        dividend = new BigInteger(dividend, 2).toString(2);
        return dividend;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String message = sc.nextLine();
        String key = sc.nextLine();
        key = new BigInteger(key, 2).toString(2);

        // Checking the polynomial
        if ((divide(key, "10").equals("0")) || (divide(key, "11").equals("0"))) {
            System.out.println("[ERROR] Invalid polynomial.");
            System.exit(0);
        }

        message = message + new String(new char[key.length() - 1]).replace("\0", "0");
        String remainder = divide(message, key);
        if (remainder.length() < key.length() - 1) {
            remainder = new String(new char[key.length() - 1 - remainder.length()]).replace("\0", "0") + remainder;
        }
        message = message.substring(0, message.length() - key.length() + 1) + remainder;

        System.out.println(message);
        System.out.println(key);

        sc.close();
    }
}
