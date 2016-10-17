package com.bluetoothle.factory.doorguard.hainan;

import com.bluetoothle.factory.doorguard.DoorGuardCrcUtil;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

import java.io.ByteArrayOutputStream;

public class HaiNanDoorGuardProtocol {
	private final static String TAG = HaiNanDoorGuardProtocol.class.getSimpleName();
	public final static String SERVICE_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID = "0000fff9-0000-1000-8000-00805f9b34fb";
	
	public interface HAINANBleDataWritten{
		void writeSuccess();
		void writeFailure(String error);
    }
	
	public static byte[] buildBleData(byte cmd, byte[] mobile) {
		byte[] data = new byte[15];
		
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
	    BLELogUtil.d(TAG, "data=" + BLEByteUtil.bytesToHexString(data));
	    
		return data;
	}
	public static byte[] buildSettingBleData(byte cmd, byte[] data) {
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
			BLELogUtil.i(TAG, e.getMessage());
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
