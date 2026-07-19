package hotel.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtil {
    private static final int SALT_LENGTH = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        String saltStr = Base64.getEncoder().encodeToString(salt);
        String hash = pbkdf2Hash(password, salt);
        return saltStr + ":" + hash;
    }

    public static boolean verifyPassword(String password, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            return password.equals(storedHash);
        }
        String salt = parts[0];
        String hash = parts[1];

        if (isOldFormat(hash)) {
            return verifyOldFormat(password, salt, hash);
        }
        return verifyNewFormat(password, salt, hash);
    }

    public static boolean needsRehash(String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) return true;
        return isOldFormat(parts[1]);
    }

    private static boolean isOldFormat(String hash) {
        return hash.length() == 64 && hash.matches("[0-9a-fA-F]+");
    }

    private static boolean verifyOldFormat(String password, String salt, String expectedHash) {
        try {
            String computed = sha256(salt + password);
            return MessageDigest.isEqual(
                computed.getBytes(StandardCharsets.UTF_8),
                expectedHash.getBytes(StandardCharsets.UTF_8)
            );
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private static boolean verifyNewFormat(String password, String salt, String expectedHash) {
        String computedHash = pbkdf2Hash(password, Base64.getDecoder().decode(salt));
        return MessageDigest.isEqual(
            computedHash.getBytes(StandardCharsets.UTF_8),
            expectedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static String pbkdf2Hash(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATION_COUNT,
                KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 not available", e);
        }
    }

    private static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
