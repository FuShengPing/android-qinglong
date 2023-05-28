package auto.qinglong.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author wsfsp4
 * @version 2023.03.01
 */
public class EncryptUtil {

    public static String md5(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
