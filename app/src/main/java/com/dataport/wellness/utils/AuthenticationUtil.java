package com.dataport.wellness.utils;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * <p>Class       : com.dataport.auth.util.AuthenticationUtil
 * <p>Descdription: 认证工具类
 */
public class AuthenticationUtil {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationUtil.class);

    public static final String FIELD_SIGN = "sign";

    /**
     * 验证时间戳
     *
     * @param timeStamp       时间戳
     * @param floatingSeconds 浮动秒数
     * @return
     */
    public static boolean verifyTimeStamp(String timeStamp, int floatingSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date now = new Date();
            long targetTime = sdf.parse(timeStamp).getTime();
            long minTime = now.getTime() - floatingSeconds * 1000;
            long maxTime = now.getTime() + floatingSeconds * 1000;
            return targetTime >= minTime && targetTime <= maxTime;
        } catch (ParseException e) {
            logger.error("timeStamp=[" + timeStamp + "]");
            return false;
        }
    }

    /**
     * 验证签名
     *
     * @param jsonBody 请求体
     * @param signType 签名类型
     * @param sign     签名
     * @param key      密钥
     * @return
     */
    public static boolean verifySignature(JSONObject jsonBody, GlobalConstants.SignType signType, String sign, String key) {
        try {
            String mySign = generateSignature(jsonBody, key, signType);
            logger.info("sign=[" + sign + "]");
            logger.info("mySign=[" + mySign + "]");
            return mySign.equals(sign);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成签名
     *
     * @param reqJson 请求体
     * @param key     密钥
     * @param type    签名类型
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String generateSignature(JSONObject reqJson, String key,
                                           GlobalConstants.SignType type) throws Exception {
        reqJson.remove("sign");
        reqJson.remove("host");
        reqJson.remove("api");
        Map<String, Object> data = (Map<String, Object>) JSON.parse(reqJson.toJSONString());
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(FIELD_SIGN)) {
                continue;
            }
            if (null != data.get(k) && !data.get(k).equals("")) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k)).append("&");
        }
        sb.append("key=").append(key);
        if (GlobalConstants.SignType.MD5.equals(type)) {
            return MD5(sb.toString()).toUpperCase();
        } else if (GlobalConstants.SignType.HMACSHA256.equals(type)) {
            return HMACSHA256(sb.toString(), key);
        } else {
            throw new Exception(String.format("Invalid sign_type: %s", type));
        }

    }

    public static String getRandomCode() {
        String randomcode = "";
        // 用字符数组的方式随机
        String model = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] m = model.toCharArray();
        for (int j = 0; j < 10; j++) {
            char c = m[(int) (Math.random() * 36)];
            // 保证六位随机数之间没有重复的
            if (randomcode.contains(String.valueOf(c))) {
                j--;
                continue;
            }
            randomcode = randomcode + c;
        }
        return randomcode;
    }

    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 生成 HMACSHA256
     *
     * @param data 待处理数据
     * @param key  密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
}
