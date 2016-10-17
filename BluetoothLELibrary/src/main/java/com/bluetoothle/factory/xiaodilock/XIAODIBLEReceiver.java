package com.bluetoothle.factory.xiaodilock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluetoothle.BLEByteUtil;
import com.bluetoothle.BluetoothLeManage;
import com.dsm.dsmodule.util.LogUtil;
import com.dsm.dsmodule.util.StringUtil;
import com.dsm.secondlock.app.ble.guard.GUARDBLEProtocolFactory;
import com.dsm.secondlock.app.ble.hainan.HAINANBLEProtocolFactory;

/**
 * 小嘀普通蓝牙交互广播接收器
 */
public class XIAODIBLEReceiver extends BroadcastReceiver {
	private final static String tag = XIAODIBLEReceiver.class.getSimpleName();

	private byte[] previewBytes = null;
    
    private Object responseObj;

	public void setResponseObj(Object responseObj) {
		this.responseObj = responseObj;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		 if(BluetoothLeManage.ACTION_BLE_ERROR.equalsIgnoreCase(intent.getAction())){//小嘀接收到蓝牙错误消息
			String error = intent.getExtras().getString(BluetoothLeManage.ACTION_BLE_ERROR);
			if(StringUtil.isEmpty(error)){
				LogUtil.d(tag, "错误广播消息,没有消息体");
				return;
			}
			LogUtil.d(tag, "error=" + error);
			if(responseObj instanceof OnXIAODIBLEListener.XIAODIBLEDataWritten){//小嘀写数据接口出错
				((OnXIAODIBLEListener.XIAODIBLEDataWritten)responseObj).writeFailure(error);
			}else if(responseObj instanceof XIAODIBLEHandler){//小嘀消息接口出错
				XIAODIBLEHandler xiaodibleHandler = (XIAODIBLEHandler) responseObj;
				xiaodibleHandler.obtainMessage(XIAODIBLEHandler.MSG_BLE_ERROR, error).sendToTarget();
			}else if(responseObj instanceof GUARDBLEProtocolFactory.GUARDBLEDataWritten){//小嘀门禁设备写数据出错
				GUARDBLEProtocolFactory.GUARDBLEDataWritten guardbleDataWritten = (GUARDBLEProtocolFactory.GUARDBLEDataWritten) responseObj;
				guardbleDataWritten.writeFailure(error);
			}else if(responseObj instanceof HAINANBLEProtocolFactory.HAINANBleDataWritten){
				HAINANBLEProtocolFactory.HAINANBleDataWritten hainanBleDataWritten = (HAINANBLEProtocolFactory.HAINANBleDataWritten) responseObj;
				hainanBleDataWritten.writeFailure(error);
			}
		}else if(BluetoothLeManage.ACTION_DATA_AVAILABLE.equalsIgnoreCase(intent.getAction())){//小嘀接收到蓝牙返回数据
			byte[] data = intent.getByteArrayExtra(BluetoothLeManage.ACTION_DATA_AVAILABLE);
			 if(!BLEByteUtil.compareTwoBytes(previewBytes, data)){
				 if(responseObj instanceof XIAODIBLEHandler){//小嘀消息接口接收到蓝牙返回数据
					 XIAODIBLEHandler xiaodibleHandler = (XIAODIBLEHandler) responseObj;
					 xiaodibleHandler.obtainMessage(XIAODIBLEHandler.MSG_DATA_AVAILABLE, data).sendToTarget();
				 }
				 previewBytes = data;
			 }
//			if(responseObj instanceof XIAODIBLEHandler){//小嘀消息接口接收到蓝牙返回数据
//				XIAODIBLEHandler xiaodibleHandler = (XIAODIBLEHandler) responseObj;
//				xiaodibleHandler.obtainMessage(XIAODIBLEHandler.MSG_DATA_AVAILABLE, data).sendToTarget();
//			}
		}else if(BluetoothLeManage.ACTION_WRITTEN_SUCCESS.equalsIgnoreCase(intent.getAction())){//小嘀接收到蓝牙写数据成功消息
			LogUtil.d(tag, "数据写入完成");
			if(responseObj instanceof OnXIAODIBLEListener.XIAODIBLEDataWritten){//小嘀写数据接口写数据成功
				((OnXIAODIBLEListener.XIAODIBLEDataWritten)responseObj).writeSuccess();
			}else if(responseObj instanceof GUARDBLEProtocolFactory.GUARDBLEDataWritten){//小嘀门禁设备写数据成功
				GUARDBLEProtocolFactory.GUARDBLEDataWritten guardbleDataWritten = (GUARDBLEProtocolFactory.GUARDBLEDataWritten) responseObj;
				guardbleDataWritten.writeSuccess();
			}else if(responseObj instanceof HAINANBLEProtocolFactory.HAINANBleDataWritten){
				HAINANBLEProtocolFactory.HAINANBleDataWritten hainanBleDataWritten = (HAINANBLEProtocolFactory.HAINANBleDataWritten) responseObj;
				hainanBleDataWritten.writeSuccess();
			}
		}
	}

}
