package com.bluetoothle.factory.doorguard.hainan;

import com.bluetoothle.factory.doorguard.DoorGuardCrcUtil;

import java.io.ByteArrayOutputStream;

/**
 * 海南门禁设备通讯协议
 */
public class HaiNanDoorGuardProtocol {

	/**
	 * 发送数据开门命令
	 * @param cmd   命令字节号
	 * @param mobile    手机号码字节数组
     * @return
     */
	public static byte[] buildOpenBLEData(byte cmd, byte[] mobile) {
		byte[] data = new byte[15];

		try {
			//包头
			data[0] = (byte) 0xFF;

			//指令
			data[1] = cmd;

			//数据区长度
			int dataLength = mobile.length;
			data[2] = (byte) dataLength;

			//数据
			System.arraycopy(mobile, 0, data, 3, mobile.length);

			//CRC校验
			data[14] = DoorGuardCrcUtil.getCRCByteValue(data)[1];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return data;
	}

	/**
	 * 发送配置命令,不监听接收数据
	 * @param cmd   命令字节号
	 * @param data    需要配置的数据字节数组
     * @return
     */
	public static byte[] buildSettingBLEData(byte cmd, byte[] data) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		try{
			buf.write(0xFF);//包头
			buf.write(cmd);//指令
			buf.write(data==null?0:data.length);//数据区长度
			if(data!=null){
				buf.write(data);
			}
			buf.write(0);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		// 设置校验位
        int cks = 0;
        byte[] result = buf.toByteArray();
        for (int i = 0; i < result.length-1; i++){
            cks += (int)result[i];
        }
        result[result.length-1] = (byte)(cks & 0xFF);
        
		return result;
	}
}
