package ro.ase.ism.sap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TestECB {
    public static void encrypt(String inputFileName, byte[] key, String algorithm, String outputFileName) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new RuntimeException("**** NO FILE ****");
        }
        File outputFile = new File(outputFileName);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        FileInputStream fis = new FileInputStream(inputFile);
        FileOutputStream fos = new FileOutputStream(outputFile);

        Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
        // init the cipher
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] block = new byte[cipher.getBlockSize()];

        while(true) {
            int noBytes = fis.read(block);
            if (noBytes == -1) {
                break;
            }
            byte[] cipherBlock = cipher.update(block, 0, noBytes);
            fos.write(cipherBlock);
        }

        // IMPORTANT - get the last cipher block
        byte[] cipherBlock = cipher.doFinal();
        fos.write(cipherBlock);

        fis.close();
        fos.close();
    }

    public static void decrypt(String inputFileName, byte[] key, String algorithm, String outputFileName) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new RuntimeException("**** NO Input File for decrypt ****");
        }
        File outputFile = new File(outputFileName);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        FileInputStream fis = new FileInputStream(inputFile);
        FileOutputStream fos = new FileOutputStream(outputFile);

        Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, algorithm));

        byte[] block = new byte[cipher.getBlockSize()];
        while (true) {
            int noBytes = fis.read(block);
            if (noBytes == -1) {
                break;
            }
            byte[] cipherBlock = cipher.update(block, 0, noBytes);
            fos.write(cipherBlock);
        }

        byte[] cipherBlock = cipher.doFinal();
        fos.write(cipherBlock);

        fis.close();
        fos.close();

    }

    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String key = "password12345678";
        encrypt("message.txt", key.getBytes(), "AES", "message.enc");
        System.out.println("Done");
        decrypt("message.enc", key.getBytes(), "AES", "message2.txt");
    }
}
