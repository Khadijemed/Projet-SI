package prisonersdilemma;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;

public class IntegrityChecker {
    private static final String LOG_FILE = "game_log.txt";
    private static final String CHECKSUM_FILE = "game_log.checksum";

    // Calcule le hash SHA-256 d’un fichier
    public static String computeHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] content = Files.readAllBytes(file.toPath());
        byte[] hash = digest.digest(content);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // Vérifie l’intégrité du fichier log
    public static boolean verifyIntegrity() {
        try {
            File logFile = new File(LOG_FILE);
            File checksumFile = new File(CHECKSUM_FILE);
            if (!logFile.exists() || !checksumFile.exists()) return true;

            String currentHash = computeHash(logFile);
            String savedHash = new String(Files.readAllBytes(checksumFile.toPath())).trim();
            return currentHash.equals(savedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sauvegarde l'empreinte actuelle du fichier
    public static void saveCurrentChecksum() {
        try {
            String currentHash = computeHash(new File(LOG_FILE));
            Files.write(new File(CHECKSUM_FILE).toPath(), currentHash.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
