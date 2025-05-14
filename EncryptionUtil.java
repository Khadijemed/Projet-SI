package prisonersdilemma;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    // مفتاح تشفير ثابت (لأغراض تعليمية فقط)
    private static final byte[] KEY = "MySecretKey12345".getBytes(); // 16 بايت

    // تشفير نص عادي
    public static String encrypt(String data) {
        try {
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // فك التشفير
    public static String decrypt(String encryptedData) {
        try {
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // اختبار مدمج مع خيار اللعب
    public static void logEncryptedChoice(String label, String choice) {
        String encrypted = encrypt(choice);
        String decrypted = decrypt(encrypted);
        System.out.println("[" + label + "] Choix original: " + choice);
        System.out.println("[" + label + "] Crypté : " + encrypted);
        System.out.println("[" + label + "] Décrypté : " + decrypted);
    }

    // اختبار
    public static void main(String[] args) {
        String original = "COOPERATE";
        String encrypted = encrypt(original);
        String decrypted = decrypt(encrypted);

        System.out.println("Original : " + original);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}
