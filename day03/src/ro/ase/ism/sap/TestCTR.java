package ro.ase.ism.sap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TestCTR {
    public static void encrypt(String inputFileName, byte[] key, String algorithm, String outputFileName) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
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

        Cipher cipher = Cipher.getInstance(algorithm + "/CTR/NoPadding");

        // init IV with a random value and put it in the file as the first block
        // for CTR mdoe this is mandatory - DONT reuse IV
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] IV = new byte[cipher.getBlockSize()];
        secureRandom.nextBytes(IV);

        fos.write(IV);

        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, algorithm), ivSpec);
        byte[] block = new byte[cipher.getBlockSize()];
        while(true) {
            int noBytes = fis.read(block);
            if (noBytes == -1) {
                break;
            }
            byte[] cipherBlock = cipher.update(block, 0, noBytes);
            fos.write(cipherBlock);
        }

        byte[] cipherBlock = cipher.doFinal();
        fos.write(cipherBlock);

        fos.close();
        fis.close();
    }

    public static void decrypt(String inputFileName, byte[] key, String algorithm, String outputFileName) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
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

        Cipher cipher = Cipher.getInstance(algorithm + "/CTR/NoPadding");

        byte[] IV = new byte[cipher.getBlockSize()];
        fis.read(IV);

        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, algorithm), ivSpec);

        byte[] block = new byte[cipher.getBlockSize()];
        while(true) {
            int noBytes = fis.read(block);
            if (noBytes == -1) {
                break;
            }
            byte[] cipherBlock = cipher.update(block, 0, noBytes);
            fos.write(cipherBlock);
        }

        byte[] cipherBlock = cipher.doFinal();
        fos.write(cipherBlock);

        fos.close();
        fis.close();
    }
    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String password = "password";
        encrypt("message.txt", password.getBytes(), "DES", "message.ctr");
        System.out.println("Done");
        decrypt("message.ctr", password.getBytes(), "DES", "message4CTR.txt");
    }
}
