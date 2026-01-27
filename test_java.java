package ro.ase.ism.sap.exam.ionescu.franco;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class IonescuFranco {

    public static void main(String[] args) throws Exception {
        String fingerprintsPath = "fingerprints.txt";
        String system32Path = "system32"; 
        String encryptedFilePath = "financialdata.enc";
        String decryptedFilePath = "financialdata.txt";
        String responseFilePath = "myresponse.txt";
        String responseFileMacPath = "myresponse_mac.txt";
        String macPassword = "secretism";

        // Step 1
        File modifiedFile = findModifiedFile(fingerprintsPath, system32Path);

        // Step 2 - extract key from file and reverse the virus obfuscation
        SecretKeySpec aesKey = extractKeyFromFile(modifiedFile);
        
        // Step 3
        decryptFile(encryptedFilePath, decryptedFilePath, aesKey);

        // Step 4
        String firstIban = extractFirstIban(decryptedFilePath);
        writeToFile(responseFilePath, firstIban);
        generateMac(responseFilePath, responseFileMacPath, macPassword);
    }

    public static File findModifiedFile(String fingerprintsPath, String system32Path) throws Exception {
        Map<String, String> knownHashes = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(fingerprintsPath));
        String line;
        int index = 0;
        String filePath = null;
        while ((line = reader.readLine()) != null) {
            String hash = null;
            if (index % 2 == 0) {
                filePath = line;
            }
            else {
                hash = line;
            }
            index++;
            knownHashes.put(filePath, hash);
        }
        reader.close();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        File folder = new File(system32Path);
        if (folder.exists()) {
            String[] entries = folder.list();
            for (String entry : entries) {
                File file = new File(folder, entry);
                byte[] fileBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                byte[] hashBytes = md.digest(fileBytes);
                String currentHash = Base64.getEncoder().encodeToString(hashBytes);
                String storedHash = knownHashes.get("system32\\" + entry);
                if (storedHash == null || !storedHash.equals(currentHash)) {
                    System.out.println("Changed file: " + file.getName());
                    return file;
                }
            }
        }
        return null;
    }

    public static SecretKeySpec extractKeyFromFile(File modifiedFile) throws Exception {
        FileInputStream fis = new FileInputStream(modifiedFile);
        byte[] fisBytes = fis.readNBytes(16);
        fis.close();

        byte[] obfuscatedKey = new byte[16];
        System.arraycopy(fisBytes, 0, obfuscatedKey, 0, 16);

        // Reverse the obfuscation process
        byte[] originalKey = new byte[16];
        for (int i = 0; i < 16; i++) {
            byte b = obfuscatedKey[i];

            int xor = (b & 0xFF) ^ 0x3C;

            originalKey[i] = (byte) (((xor << 1) | (xor >>> 7)));
        }
        System.out.println(bytesToHex(originalKey));

        return new SecretKeySpec(originalKey, "AES");
    }

    public static void decryptFile(String encPath, String outPath, SecretKey key) throws Exception {
        byte[] iv = new byte[] {
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x03, 0x01, 0x13, 0x16
        };

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        try (FileInputStream fis = new FileInputStream(encPath);
             FileOutputStream fos = new FileOutputStream(outPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] decrypted = cipher.update(buffer, 0, bytesRead);
                if (decrypted != null) {
                    fos.write(decrypted);
                }
            }
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                fos.write(finalBlock);
            }
        }
    }

    public static String extractFirstIban(String filePath) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String IBAN = "";

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.matches("MC[0-9]{2}[A-Z0-9]+") && line.length() >= 15) {
                IBAN = line;
                reader.close();
                return IBAN;
            }
        }
        reader.close();
        return IBAN;
    }

    public static void writeToFile(String path, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(content);
        fileWriter.close();
    }

    public static void generateMac(String inputFile, String macFile, String macPass) throws Exception {
        byte[] fileBytes = Files.readAllBytes(Paths.get(inputFile));

        SecretKeySpec keySpec = new SecretKeySpec(macPass.getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);

        byte[] macBytes = mac.doFinal(fileBytes);
        String macBase64 = Base64.getEncoder().encodeToString(macBytes);

        writeToFile(macFile, macBase64);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02X", b));
        return sb.toString();
    }
}
