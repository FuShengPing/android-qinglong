package auto.base.util;

import java.security.MessageDigest;

/**
 * @author wsfsp4
 * @version 2023.03.01
 */
public class EncryptUtil {

    public static String md5(String input) {
        try {
            // 创建MD5加密算法的实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将输入数据转换为字节数组
            byte[] inputBytes = input.getBytes();

            // 计算MD5摘要
            byte[] hashBytes = md.digest(inputBytes);

            // 将摘要转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                // 按位与运算，将byte转换为int，并确保使用两位的十六进制表示
                sb.append(String.format("%02x", b & 0xff));
            }

            // 返回加密后的字符串
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
