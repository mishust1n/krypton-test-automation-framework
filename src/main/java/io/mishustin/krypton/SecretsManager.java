package io.mishustin.krypton;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.Destroyable;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

public class SecretsManager {

    private static final int AUTH_TAG_SIZE = 128;
    private static final int IV_LEN = 12;
    private static final double PRNG_RESEED_INTERVAL = Math.pow(2, 16);
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final List<Integer> ALLOWED_KEY_SIZES = Arrays.asList(128, 192, 256);
    private static SecureRandom prng;
    private static int bytesGenerated = 0;
    private static final String KEY_FACTORY_INSTANCE = "PBKDF2WithHmacSHA256";
    private static final String ALGORITHM = "AES";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;

    private static Properties secrets;

    public static String get(String key) {
        return secrets.getProperty(key);
    }

    private static SecretKeySpec createKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY_INSTANCE);
        KeySpec keySpec = new PBEKeySpec(key.toCharArray(), key.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
    }

    public static void readSecrets(Path encryptedFile, String stringKey) throws Exception {
        SecretKeySpec secretKey = createKey(stringKey);

        byte[] encrypted = Files.readAllBytes(encryptedFile);
        byte[] decrypted = decrypt(encrypted, secretKey);
        secrets = new Properties();
        secrets.load(new ByteArrayInputStream(decrypted));

        clearSecret(secretKey);
    }

    public static void writeSecrets(Path decryptedFile, Path outputFile, String stringKey) throws Exception {
        SecretKeySpec secretKey = createKey(stringKey);

        byte[] decrypted = Files.readAllBytes(decryptedFile);
        byte[] encrypted = encrypt(decrypted, secretKey);

        Files.write(outputFile, encrypted);

        clearSecret(secretKey);
    }

    public static byte[] encrypt(byte[] input, SecretKeySpec key) throws Exception {

        Objects.requireNonNull(input, "Input message cannot be null");
        Objects.requireNonNull(key, "key cannot be null");

        if (input.length == 0) {
            throw new IllegalArgumentException("Length of message cannot be 0");
        }

        if (!ALLOWED_KEY_SIZES.contains(key.getEncoded().length * 8)) {
            throw new IllegalArgumentException("Size of key must be 128, 192 or 256");
        }

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

        byte[] iv = getIV(IV_LEN);

        GCMParameterSpec gcmParamSpec = new GCMParameterSpec(AUTH_TAG_SIZE, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParamSpec);
        byte[] messageCipher = cipher.doFinal(input);

        byte[] cipherText = new byte[messageCipher.length + IV_LEN];
        System.arraycopy(iv, 0, cipherText, 0, IV_LEN);
        System.arraycopy(messageCipher, 0, cipherText, IV_LEN,
                messageCipher.length);
        return cipherText;
    }

    public static byte[] decrypt(byte[] input, SecretKeySpec key) throws Exception {
        Objects.requireNonNull(input, "Input message cannot be null");
        Objects.requireNonNull(key, "key cannot be null");

        if (input.length == 0) {
            throw new IllegalArgumentException("Input array cannot be empty");
        }

        byte[] iv = new byte[IV_LEN];
        System.arraycopy(input, 0, iv, 0, IV_LEN);

        byte[] messageCipher = new byte[input.length - IV_LEN];
        System.arraycopy(input, IV_LEN, messageCipher, 0, input.length - IV_LEN);

        GCMParameterSpec gcmParamSpec = new GCMParameterSpec(AUTH_TAG_SIZE, iv);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParamSpec);

        return cipher.doFinal(messageCipher);
    }

    public static byte[] getIV(int bytesNum) {

        if (bytesNum < 1) throw new IllegalArgumentException(
                "Number of bytes must be greater than 0");

        byte[] iv = new byte[bytesNum];

        prng = Optional.ofNullable(prng).orElseGet(() -> {
            try {
                prng = SecureRandom.getInstanceStrong();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Wrong algorithm name", e);
            }
            return prng;
        });

        if (bytesGenerated > PRNG_RESEED_INTERVAL || bytesGenerated == 0) {
            prng.setSeed(prng.generateSeed(bytesNum));
            bytesGenerated = 0;
        }

        prng.nextBytes(iv);
        bytesGenerated = bytesGenerated + bytesNum;

        return iv;
    }

    private static void clearSecret(Destroyable key)
            throws IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException, SecurityException {
        Field keyField = key.getClass().getDeclaredField("key");
        keyField.setAccessible(true);
        byte[] encodedKey = (byte[]) keyField.get(key);
        Arrays.fill(encodedKey, Byte.MIN_VALUE);
    }
}
