package ionescu.franco.ism.sap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.List;

public class Main {

    public static String computeHMAC(Path file, Mac mac, SecretKeySpec secretKeySpec) throws Exception {
        mac.init(secretKeySpec);
        byte[] messageBytes = Files.readAllBytes(file);
        byte[] hmacBytes = mac.doFinal(messageBytes);

        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static Map<String, String> loadHMAC(Path hmacFile) throws IOException {
        Map<String, String> hmacMap = new HashMap<>();

        if (!Files.exists(hmacFile)) {
            return hmacMap;
        }

        List<String> lines = Files.readAllLines(hmacFile);
        for (int i = 0; i < lines.size() - 1; i += 2) {
            String fileName = lines.get(i).trim();
            String hmacValue = lines.get(i + 1).trim();
            hmacMap.put(fileName, hmacValue);
        }

        return hmacMap;
    }

    public static void saveHMAC(Path hmacFile, Map<String, String> hmacMap) throws IOException {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, String> entry : hmacMap.entrySet()) {
            content.append(entry.getKey()).append("\n");
            content.append(entry.getValue()).append("\n");
        }
        Files.writeString(hmacFile, content.toString());
    }

    public static void scanFolder(String folderPath, Mac mac, SecretKeySpec secretKeySpec, Map<String, String> hmacMap) throws Exception {
        Path folder = Path.of(folderPath);

        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Path-ul nu este un folder valid: " + folderPath);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    System.out.println("Scanez: " + file.getFileName());

                    String currentHMAC = computeHMAC(file, mac, secretKeySpec);
                    String fileName = file.getFileName().toString();

                    hmacMap.put(fileName, currentHMAC);
                    System.out.println("HMAC calculat si salvat pentru: " + fileName);
                }
                else if (Files.isDirectory(file)) {
                    System.out.println("Intru in subfolder: " + file.getFileName());
                    scanFolder(file.toString(), mac, secretKeySpec, hmacMap);
                }
            }
        }
    }

    public static void checkFolder(String folderPath, Mac mac, SecretKeySpec secretKeySpec, Map<String, String> hmacMap) throws Exception {
        Path folder = Path.of(folderPath);

        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Path-ul nu este un folder valid: " + folderPath);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    System.out.println("Verific: " + file.getFileName());

                    String currentHMAC = computeHMAC(file, mac, secretKeySpec);
                    String fileName = file.getFileName().toString();

                    if (hmacMap.containsKey(fileName)) {
                        String storedHMAC = hmacMap.get(fileName);
                        if (currentHMAC.equals(storedHMAC)) {
                            System.out.println("Fisierul NU s-a schimbat: " + fileName);
                        } else {
                            System.out.println("ATENTIE! Fisierul S-A SCHIMBAT: " + fileName);
                        }
                    } else {
                        System.out.println("ATENTIE! Fisier nou (nu exista HMAC anterior): " + fileName);
                    }
                }
                else if (Files.isDirectory(file)) {
                    System.out.println("Intru in subfolder: " + file.getFileName());
                    checkFolder(file.toString(), mac, secretKeySpec, hmacMap);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Check if we have the minimum required arguments
        if (args.length != 4) {
            System.out.println("Usage: java Main <scan|check> <secret_key> <folder_path> <hmac_file>");
            System.out.println("Example: java Main scan mysecretkey D:\\files hmacs.txt");
            System.out.println("Example: java Main check mysecretkey D:\\files hmacs.txt");
            return;
        }

        String mode = args[0].toLowerCase();
        String key = args[1];
        String folderPath = args[2];
        String hmacFileName = args[3];

        // Validate mode
        if (!mode.equals("scan") && !mode.equals("check")) {
            System.out.println("Eroare: Modul trebuie sa fie 'scan' sau 'check'");
            return;
        }

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");

        Path hmacDatabaseFile = Path.of(hmacFileName);

        if (mode.equals("scan")) {
            System.out.println("=== MOD SCANARE ===");
            System.out.println("Folder: " + folderPath);
            System.out.println("Fisier HMAC: " + hmacFileName);
            System.out.println();

            Map<String, String> hmacMap = new HashMap<>();
            scanFolder(folderPath, mac, secretKeySpec, hmacMap);
            saveHMAC(hmacDatabaseFile, hmacMap);

            System.out.println("\nScanare completa.");
            System.out.println("Baza de date HMAC salvata in: " + hmacDatabaseFile);
            System.out.println("Total fisiere scanate: " + hmacMap.size());

        } else { // check mode
            System.out.println("=== MOD VERIFICARE ===");
            System.out.println("Folder: " + folderPath);
            System.out.println("Fisier HMAC: " + hmacFileName);
            System.out.println();

            Map<String, String> hmacMap = loadHMAC(hmacDatabaseFile);

            if (hmacMap.isEmpty()) {
                System.out.println("Eroare: Fisierul HMAC nu exista sau este gol: " + hmacFileName);
                return;
            }

            checkFolder(folderPath, mac, secretKeySpec, hmacMap);

            System.out.println("\nVerificare completa.");
            System.out.println("Total fisiere verificate: " + hmacMap.size());
        }
    }
}