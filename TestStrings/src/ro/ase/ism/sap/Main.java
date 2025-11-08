package ro.ase.ism.sap;

import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static String toHexString(byte[] values) {
        StringBuilder sb = new StringBuilder();
        for (byte value: values) {
            sb.append(String.format("%02x", value));
        }
        return sb.toString();
    }

    public static byte[] toByteArray(String hexString) {
        if (hexString.length() % 2 != 0) {
            throw new RuntimeException("The hex string must have a size multiple of 2");
        }
        byte[] result = new byte[hexString.length()/2];
        for (int i = 0;i < result.length - 1; i++) {
            String currentPair = hexString.substring(i*2, (i+1)*2);
            result[i] = (byte)Integer.parseInt(currentPair.toUpperCase(), 16);
        }
        return result;
    }

    public static void main(String[] args) {
        // remember strings
        String hash1 = "FA23D4";
        String hash2 = "FA23D4";

        hash2 = new String("FA23D4");

        if (hash1 == hash2) {
            System.out.println("The same hash");
        }
        else {
            System.out.println("Different hash values");
        }

        if (hash1.equals(hash2)) {
            System.out.println("The same hash");
        }
        else {
            System.out.println("Different hash values");
        }

        hash2 = new String("FA23D4").toLowerCase();

        if (hash1.equals(hash2)) {
            System.out.println("The same hash");
        }
        else {
            System.out.println("Different hash values");
        }

        // small integers, up to 127, are managed by a constant integers pool
        Integer vb1 = 127;
        Integer vb2 = 127;

        if (vb1 == vb2) {
            System.out.println("Same numbers");
        }
        else {
            System.out.println("Different numbers");
        }

        // hex strings
        byte byteValue  = 23;
        byteValue = 0x17; // 23 as hexadecimal
        byteValue = 10;
        System.out.println(String.format("%x", byteValue));
        System.out.println(Integer.toHexString(byteValue));
        System.out.println(byteValue);
        System.out.printf("%x \n", byteValue);

        byte[] values = {(byte)23, (byte)30, (byte) 0xF2, (byte)10};
        String hexValue = toHexString(values);
        System.out.println(hexValue);
        System.out.println(hexValue.toUpperCase());

        // converting strings to bytes
        String password = "password";
        byte[] passAsByteArrays = password.getBytes(); // each character as 1 byte
        System.out.println(passAsByteArrays.length);

        byte[] binaryPassword = {(byte)0x30, (byte)0x39, (byte)0x00, (byte)0x05};
        byte[] binaryPassword2 = {(byte)0x30, (byte)0x39, (byte)0x00, (byte)0x06};
        password = new String(binaryPassword);
        System.out.println("Password is: " + password);
        System.out.println("Password is: " + new String(binaryPassword2));

        System.out.println(toHexString(binaryPassword));
        System.out.println(toHexString(binaryPassword2));

        // convert hex strings to values or byte arrays
        String anotherHexValue = "23";
        byteValue = Byte.parseByte(anotherHexValue, 16);
        System.out.println("The value is "+ byteValue);

        System.out.println(hexValue);
        System.out.println(Arrays.toString(toByteArray(hexValue)));



    }
}