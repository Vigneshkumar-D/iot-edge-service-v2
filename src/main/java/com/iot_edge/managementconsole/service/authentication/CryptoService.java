package com.iot_edge.managementconsole.service.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@Slf4j
public class CryptoService {
    @Value("${crypto.key}")
    private String key;

    public String encrypt(String data) {
        try {
            if (data == null || data.isEmpty()) {
                return data;
            }

            // Generate a random IV
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Return the IV and encrypted data as a base64-encoded string separated by a colon
            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return null;
        }
    }

//    public String decrypt(String data) {
//        try {
//            if (data == null || data.isEmpty()) {
//                return data;
//            }
//
//            String ivBase64 = data.split(":")[0];
//            data = data.split(":")[1];
//
//            // Decode the IV from a base64 string
//            byte[] iv = Base64.getDecoder().decode(ivBase64);
//            if (iv.length != 16) {
//                throw new IllegalArgumentException("Invalid IV length");
//            }
//
//            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
//            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
//
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
//
//            byte[] original = cipher.doFinal(Base64.getDecoder().decode(data));
//
//            return new String(original);
//        } catch (Exception e) {
//            return null;
//        }
//    }
public String decrypt(String data) {
    try {
        if (data == null || data.isEmpty()) {
            return data;
        }

        String[] parts = data.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted data format");
        }

        String ivBase64 = parts[0];
        String encryptedBase64 = parts[1];

        byte[] iv = Base64.getDecoder().decode(ivBase64);
        if (iv.length != 16) {
            throw new IllegalArgumentException("Invalid IV length");
        }

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));

        return new String(original);
    } catch (Exception e) {
        log.error("Decryption failed: {}", e.getMessage(), e);
        return null;
    }
}

}
