package com.bluetoothle.factory.xiaodilock.util;


import com.bluetoothle.util.BLEStringUtil;

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
}
