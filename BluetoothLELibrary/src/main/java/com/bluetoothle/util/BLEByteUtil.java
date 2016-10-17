package com.bluetoothle.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 正则通用验证
     * @param res   验证字符串
     * @param regex 验证规则
     * @return
     */
    public static boolean regexCheck(String res, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(res);
        return matcher.matches();
    }

    /**
     * 将以空格分割的每两位相连的指定进制字(10进制或16进制)符串转换成字节数组
     * @param src   源字符串
     * @param radix 进制数
     * @return
     */
    public static byte[] radixStringToBytes(String src, int radix){
        if (src == null || src.length() <= 0) {
            return null;
        }
        String[] srcarray;
        try {
            srcarray = src.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if(srcarray == null || srcarray.length == 0){
            return null;
        }
        for(int i=0;i<srcarray.length;i++){
            if(srcarray[i].length() > 2){
                return null;
            }
            if(srcarray[i].length() == 1){
                srcarray[i] = 0 + srcarray[i];
            }
            if(radix == 16 && !regexCheck(srcarray[i], "^[0-9a-fA-F]{2}$")){
                return null;
            }else if(radix == 10 && !regexCheck(srcarray[i], "^[0-9]{2}$")){
                return null;
            }
        }
        byte[] srcbytes = new byte[srcarray.length];
        for(int i=0;i<srcarray.length;i++){
            if(radix == 16){
                srcbytes[i] = (byte) Integer.parseInt(srcarray[i], 16);
            }else if(radix == 10){
                srcbytes[i] = (byte) Integer.parseInt(srcarray[i]);
            }else{
                BLELogUtil.i("hexStringToBytes,radix error");
                return null;
            }
        }
        return srcbytes;
    }
}
