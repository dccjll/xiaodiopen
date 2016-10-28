package com.bluetoothle.factory.xiaodilock.util;


import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;
import com.bluetoothle.util.BLEStringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 小滴管家蓝牙工具类
 */
public class XIAODIBLEUtil {
	/**
	 * 将一个给定的字符串转换成字节数组,字节数组第一个字节表示字符串长度
	 * @param src
	 * @param length
     * @return
     */
	public static byte[] convertStringToBytesWithLength(String src, byte length){
		if(src == null || src.length() == 0){
			return null;
		}
		if(length <= 0){
			return null;
		}
		if(src.length() >= length){
			return null;
		}
		byte[] targetBytes = new byte[length];
		System.arraycopy(new byte[]{(byte) src.length()}, 0, targetBytes, 0, 1);
		System.arraycopy(src.getBytes(), 0, targetBytes, 1, src.length());
		return targetBytes;
	}

	/**
	 * 验证手机账号
	 * @param mobileaccount
	 * @return
     */
	public static boolean checkMobileAccount(String mobileaccount){
		if(BLEStringUtil.isEmpty(mobileaccount)){
			return false;
		}
		if(mobileaccount.length() <= 0 || mobileaccount.length() > 12){
			return false;
		}
		return true;
	}

	/**
	 * 验证开锁方式
	 * @param openlocktype
	 * @return
     */
	public static boolean checkOpenLockType(byte[] openlocktype){
		if(openlocktype == null || openlocktype.length != 1){
			return false;
		}
		if(openlocktype[0] != 0x01 && openlocktype[0] != 0x02 && openlocktype[0] != 0x03 && openlocktype[0] != 0x04 && openlocktype[0] != 0x05 && openlocktype[0] != 0x06 && openlocktype[0] != 0x07){
			return false;
		}
		return true;
	}

	/**
	 * 验证亲情/紧急标志
	 * @param lovealarmflag
	 * @return
     */
	public static boolean checkLoveAlarmFlag(byte[] lovealarmflag){
		if(lovealarmflag == null || lovealarmflag.length != 1){
			return false;
		}
		if(lovealarmflag[0] != 0x00 && lovealarmflag[0] != (byte)0xF0 && lovealarmflag[0] != (byte)0x0F && lovealarmflag[0] != (byte)0xFF){
			return false;
		}
		return true;
	}

	/**
	 * 验证时效功能开关状态
	 * @param timestatus
	 * @return
     */
	public static boolean checkTimeStatus(byte[] timestatus){
		if(timestatus == null || timestatus.length != 1){
			return false;
		}
		if(timestatus[0] != (byte)0xF0 && timestatus[0] != 0x00){
			return false;
		}
		return true;
	}

	/**
	 * 将服务器的时间转换为固定协议的字节数组
	 * @param time
	 * @return
     */
	public static byte[] parseServerTimeToProtocolBytes(String time) {
		//2016-02-25 03:41:50
		if (BLEStringUtil.isEmpty(time)) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		byte[] timebytes = new byte[7];
		String[] timearray = new String[7];
		timearray[0] = (Calendar.getInstance().get(Calendar.YEAR) + "").substring(2);
		timearray[1] = calendar.get(Calendar.MONTH) + 1 + "";
		timearray[2] = calendar.get(Calendar.DAY_OF_MONTH) + "";
		timearray[3] = calendar.get(Calendar.DAY_OF_WEEK) == 1 ? "7" : (calendar.get(Calendar.DAY_OF_WEEK) - 1 + "");
		timearray[4] = calendar.get(Calendar.HOUR_OF_DAY) + "";
		timearray[5] = calendar.get(Calendar.MINUTE) + "";
		timearray[6] = calendar.get(Calendar.SECOND) + "";

		for (int i = 0; i < timearray.length; i++) {
			if (timearray[i].length() == 1) {
				timearray[i] = "0" + timearray[i];
			}
			BLELogUtil.d("timearray[" + i + "]=" + timearray[i]);
		}
		for (int i = 0; i < timebytes.length; i++) {
			timebytes[i] = BLEByteUtil.parseTenDescToDescByte(timearray[i]);
		}
		BLELogUtil.d("time from server:" + time);
		BLELogUtil.d("time to   ble:" + BLEByteUtil.bytesToHexString(timebytes));
		return timebytes;
	}
}
