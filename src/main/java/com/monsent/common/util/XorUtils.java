package com.monsent.common.util;

/**
 * Created by lj on 2017/7/8.
 * 异或加密
 */

public class XorUtils {

    /**
     * 加密
     * @param source
     * @param key
     * @return
     */
    public static String encrypt(String source, String key){
        byte[] data = source.getBytes();
        byte[] keyBytes = key.getBytes();
        int keyIndex = 0;
        for (int i = 0; i < source.length(); i++){
            data[i] = (byte)(data[i] ^ keyBytes[keyIndex]);
            if (++keyIndex == keyBytes.length){
                keyIndex = 0;
            }
        }
        return new String(data);
    }

    /**
     * 解密
     * @param source
     * @param key
     * @return
     */
    public static String decrypt(String source, String key){
        return encrypt(source, key);
    }

}
