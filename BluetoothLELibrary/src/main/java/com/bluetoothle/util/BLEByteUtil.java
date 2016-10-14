package com.bluetoothle.util;

import java.util.Locale;

/**
 * Created by dessmann on 16/7/27.
 * BLE字节数据辅助工具
 */
public class BLEByteUtil {
    /**
     * 截取一部分字节
     * @param bytes
     * @param start
     * @param len
     * @return
     */
    public static byte[] getSubbytes(byte[] bytes, int start, int len) {
        if (bytes.length < len || len == 0) {
            return null;
        }else if (bytes.length == len){
            return bytes;
        }
        byte[] bs = new byte[len];
        for (int i = 0; i < len; i++) {
            bs[i] = bytes[start++];
        }
        return bs;
    }

    /**
     * 以十六进制字符输出字节数组到日志,每两位字符进行分隔
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            if (i == src.length - 1) {
                stringBuilder.append(hv.toUpperCase(Locale.US));
            }else{
                stringBuilder.append(hv.toUpperCase(Locale.US) + " ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 比较两个字节数组值是否相等
     * @param buffer1
     * @param buffer2
     * @return
     */
    public static boolean compareTwoBytes(byte[] buffer1, byte[] buffer2){
        if(buffer1 == null || buffer2 == null || buffer1.length != buffer2.length){
            return false;
        }
        for(int i=0;i<buffer1.length;i++){
            if(buffer1[i] != buffer2[i]){
                return false;
            }
        }
        return true;
    }
}
