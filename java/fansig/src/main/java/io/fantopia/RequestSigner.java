/**
 * author: yqq
 * date: 2023-12
 * description: fantopia
 */
package io.fantopia;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class RequestSigner {

    /**
     *
     * @param data : 请求参数
     * @param timestamp : 时间戳， 毫秒级别
     * @param nonce  :  16字符长度字符串， 可以用上面的 generateRandomString(16)
     * @param httpMethod  :  http请求方法  GET  或  POST
     * @param userSessionToken  : 用户会话 token
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Map<String, Object> signRequest(Map<String, Object> data, long timestamp, String nonce, String httpMethod, String userSessionToken) throws NoSuchAlgorithmException, InvalidKeyException {

        Map<String, Object> r = new HashMap<>();
        r.put("platform", "web");
        r.put("timestamp", timestamp);
        r.put("nonce", nonce);
        r.put("token", userSessionToken);

        // 将 None 设置为空字符串
        for (String key : new HashMap<>(data).keySet()) {
            if (data.get(key) == null) {
                data.put(key, "");
            }
        }

        // 如果是 GET 请求
        Map<String, Object> n = new HashMap<>(r);
        if (httpMethod.equals("POST")) {
            n.putAll(data); // 把参数也加进去
        }

        // 对 dict 进行排序
        Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedMap.putAll(n);

        StringBuilder msg = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            msg.append(entry.getKey().toLowerCase())
                    .append('=')
                    .append(entry.getValue())
                    .append('&');
        }
        // System.out.println(msg);
        msg.deleteCharAt(msg.length() - 1);

        // 这里的key根据签名消息生成出来
        byte[] bzKey = generateKey(msg.toString());
        byte[] bzSig = hmacSha256(bytesToHexString(bzKey), msg.toString());

        //将key附加在sig后面
        byte[] bzSigKey = concatByteArrays(bzSig, bzKey);
        String s = Base64.getEncoder().encodeToString(bzSigKey);

        Map<String, Object> result = new HashMap<>();
        result.put("base64String", s);
        result.put("commonParams", r);
        result.put("msg", msg);// 带出去，方便测试

        return result;
    }



    /**
     * 验证前端传来的签名
     * TODO: 请先对msg中的时间戳和其他参数合法性进行验证，请自行完成，本方法只对sig和key的有效性验证
     *
     * @param msg 签名消息字符串
     * @param base64SigKey  前端传来的base64签名，base64解码后是48字节byte[]: 32字节签名 + 16字节的key
     * @return  验证成功: true , 验证失败: false
     */
    public static boolean verifySig(String msg, String base64SigKey)  {
        try{
            // TODO: 假设你已经完成对msg参数内容的验证，如：时间戳有效性，参数合法性

            // 验证合法的base64字符串, 防止瞎传参数, 48字节通过base64编码后的字符串长度是 64
            if(base64SigKey.length() != 64){
                return false;
            }

            // 解码 base64SigKey
            byte[] bzSigKey = Base64.getDecoder().decode(base64SigKey);
            if(bzSigKey.length != 48) {
                System.out.println("长度非法: " + bzSigKey.length);
                return false;
            }

            // 从bzSigKey获取sig
            byte[] bzSig = new byte[32];
            System.arraycopy(bzSigKey, 0, bzSig, 0, 32 );
            System.out.println("sig = " + bytesToHexString(bzSig));

            // 从bzSigKey获取key
            byte[] bzKey = new byte[16];
            System.arraycopy(bzSigKey, bzSigKey.length - 16, bzKey, 0, 16 );
            System.out.println("key = " + bytesToHexString(bzKey));


            MessageDigest sha3 = MessageDigest.getInstance("SHA3-256");
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            // 验证key是否合法,
            byte[] strBytes = (msg + "+").getBytes();
            byte[] combinedBytes = concatByteArrays(strBytes,  bzKey);
            System.out.println("combined = " + combinedBytes.toString() );
            byte[] hash1 = sha3.digest(combinedBytes);
            byte[] hash2 = sha256.digest(hash1);
            // 与上0xFF解决byte的负数问题
            if (hash2[0] == 0 && (0xFF & hash2[1]) < 5) {
                // 验证签名是否合法
                byte[] hmacSig = hmacSha256(  bytesToHexString(bzKey), msg );
                if (Arrays.equals(hmacSig,  bzSig)) {
                    return true;
                }
            }

            //System.out.printf("%s", bytesToHex(hash2));
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * 生成签名的key
     *
     * @param input 消息
     * @return 生成的key
     */
    public static byte[] generateKey(String input) {
        // long start = System.currentTimeMillis();
        try {
            MessageDigest sha3 = MessageDigest.getInstance("SHA3-256");
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            byte[] strBytes = (input + "+").getBytes();

            for (long i = 1; i < (1 << 20); i++) {
                String randomHexStr = generateRandomHexString(32);
                byte[] bzKey = hexStringToByteArray( randomHexStr);
                byte[] bzMsg = concatByteArrays(strBytes, bzKey);
                byte[] hash1 = sha3.digest(bzMsg);
                byte[] hash2 = sha256.digest(hash1);

                // key需要满足的条件
                if (hash2[0] == 0 && (0xFF & hash2[1]) < 5) {
                    return bzKey;
                }
            }
        } catch (NoSuchAlgorithmException e) {
             // e.printStackTrace();
            return null;
        }
        return null;
    }



    /** 签名
     * @param key     签名的key, 可以用  generateKey  来生成
     * @param message  请求参数字符串
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static byte[] hmacSha256(String key, String message) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        hmacSha256.init(secretKey);

        return hmacSha256.doFinal(messageBytes);
    }

    /** 将字节数组转为16进制字符串
     * @param bytes
     * @return
     */
    protected static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /** 将16进制字符串转为字节数组
     * @param hexString
     * @return
     */
    protected static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }


    /** 拼接两个字节数组
     * @param a
     * @param b
     * @return 拼接后的数组
     */
    protected static byte[] concatByteArrays(byte[] a, byte[] b) {
        byte[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }


    /**
     * 生成随机字符串
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(letters.length());
            randomString.append(letters.charAt(index));
        }

        return randomString.toString();
    }

    /** 生成随机16进制字符串
     * @param length
     * @return
     */
    protected static String generateRandomHexString(int length) {
        String HEX_CHARS = "0123456789abcdef";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(HEX_CHARS.length());
            char randomChar = HEX_CHARS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

}