package com.bluetoothle.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import java.util.Locale;

/**
 * Created by dessmann on 16/7/11.
 * BLE相关的字符串操作类
 */
public class BLEStringUtil {
    public static boolean isNotEmpty(String str) {
        return (str != null) && (!"null".equalsIgnoreCase(str)) && (str.length() > 0);
    }

    public static boolean isEmpty(String str) {
        return (str == null) || ("null".equalsIgnoreCase(str)) || (str.length() == 0);
    }

    /**
     * 新建一个可以添加属性的文本对象
     * @param text
     * @param size
     * @return
     */
    public static SpannableString buildSpannableString(String text, int size){
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(text);

        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size,true);

        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    /**
     * 长字符串转换
     * @param str
     * @param maxLength
     * @return
     */
    public static String changeLongStr(String str, int maxLength){
        if(str.length()>maxLength&&str.length()>2){
            str = str.substring(0,maxLength-2)+"..."+str.substring(str.length()-1, str.length());
        }
        return str;
    }

    /**
     * 将一个用":"连接的字符串以":"为分隔反转
     * @param st
     * @param toUpperCase
     * @return
     */
    public static String reserveString(String st, boolean toUpperCase) {
        String[] starr = st.split(":");
        StringBuilder sBuffer = new StringBuilder();
        for (int i = starr.length - 1; i >= 0; i--) {
            sBuffer.append(starr[i] + ":");
        }
        String reserveString = sBuffer.toString().substring(0, sBuffer.toString().lastIndexOf(":"));
        if(toUpperCase){
            reserveString = reserveString.toUpperCase(Locale.US);
        }
        return reserveString;
    }
}
