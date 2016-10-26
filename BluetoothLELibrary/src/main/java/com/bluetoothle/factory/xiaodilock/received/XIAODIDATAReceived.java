package com.bluetoothle.factory.xiaodilock.received;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.bluetoothle.core.response.OnBLEResponseListener;
import com.bluetoothle.factory.xiaodilock.OnXIAODIBLEListener;
import com.bluetoothle.factory.xiaodilock.util.XIAODIBLECRCUtil;
import com.bluetoothle.factory.xiaodilock.util.XIAODIConstants;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

/**
 * 小嘀数据接收器
 */
public class XIAODIDataReceived implements OnBLEResponseListener {

	private final static String TAG = XIAODIDataReceived.class.getSimpleName();
	private boolean dataReceiveFinished = false;//对一个完整包来说,数据是否接收完成
	private int dataReceivedCount = 0;//对一个完整包来说,接收了多少次数据
	private int dataReceivedTotalLength = 0;//对一个完整包来说,该包总共有多少字节的数据
	private byte[] receivedALLBLEData;//一个完整包数据对象字节数组

	private byte[] sendCMD;//发送的命令字
	private OnXIAODIBLEListener.OnCommonListener onCommonListener;//小嘀蓝牙接收数据通用监听器
	private OnXIAODIBLEListener.OnCheckManagePwdListener onCheckManagePwdListener;//小嘀验证管理密码接收数据监听器
	private OnXIAODIBLEListener.OnAddFingerListener onAddFingerListener;//小嘀添加指纹接收数据监听器

	public XIAODIDataReceived() {
	}

	public XIAODIDataReceived(byte[] sendCMD, OnXIAODIBLEListener.OnCommonListener onCommonListener) {
		this.sendCMD = sendCMD;
		this.onCommonListener = onCommonListener;
	}

	public XIAODIDataReceived(byte[] sendCMD, OnXIAODIBLEListener.OnCheckManagePwdListener onCheckManagePwdListener) {
		this.sendCMD = sendCMD;
		this.onCheckManagePwdListener = onCheckManagePwdListener;
	}

	public XIAODIDataReceived(byte[] sendCMD, OnXIAODIBLEListener.OnAddFingerListener onAddFingerListener) {
		this.sendCMD = sendCMD;
		this.onAddFingerListener = onAddFingerListener;
	}

	/**
	 * 小滴管家蓝牙数据接收
	 * @param gatt	连接的gatt服务器
	 * @param characteristic	数据特征集
     */
	@Override
	public void receiveData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		byte[] receivedData = characteristic.getValue();
		BLELogUtil.d(TAG, "接收到的数据:" + BLEByteUtil.bytesToHexString(receivedData) + ",dataReceiveFinished=" + dataReceiveFinished + ",dataReceivedCount=" + dataReceivedCount + ",dataReceivedTotalLength=" + dataReceivedTotalLength + ",receivedALLBLEData=" + BLEByteUtil.bytesToHexString(receivedALLBLEData));
		if(!dataReceiveFinished){
			if(dataReceivedCount == 0){
				if(receivedData == null || receivedData.length < 5){
					handleError(XIAODIConstants.Error.CheckSingleDataLengthError, null);
					return;
				}
				byte[] receivedDataLength = BLEByteUtil.getSubbytes(receivedData, 3, 2);
				int receivedDataLengthInt = BLEByteUtil.lessThan4bytesInt(receivedDataLength);
				receivedALLBLEData = new byte[receivedDataLengthInt + 7];
				System.arraycopy(receivedData, 0, receivedALLBLEData, 0, receivedData.length);
				dataReceivedCount ++;
				dataReceivedTotalLength = receivedData.length;
				BLELogUtil.d(TAG, "receivedData.length=" + receivedData.length + ",receivedALLBLEData.length=" + receivedALLBLEData.length + ",receivedData.length < receivedALLBLEData.length=" + (receivedData.length < receivedALLBLEData.length));
				if(receivedData.length < receivedALLBLEData.length){
					BLELogUtil.d(TAG, "等待继续接收数据,数据接收次数:" + dataReceivedCount);
					dataReceiveFinished = false;
				}else{
					dataReceiveFinished = true;
				}
			}else if(dataReceivedCount >= 1){
				System.arraycopy(receivedData, 0, receivedALLBLEData, dataReceivedTotalLength, receivedData.length);
				dataReceivedTotalLength  += receivedData.length;
				dataReceivedCount ++;
				if(dataReceivedTotalLength < receivedALLBLEData.length){
					BLELogUtil.d(TAG, "等待继续接收数据,数据接收次数:" + dataReceivedCount);
					dataReceiveFinished = false;
				}else{
					dataReceiveFinished = true;
				}
			}
		}
		if(!dataReceiveFinished){
			BLELogUtil.d(TAG, "===等待继续接收数据,数据接收次数:" + dataReceivedCount);
			return;
		}
		BLELogUtil.d(TAG, "数据接收完成");
		dataReceiveFinished = false;
		dataReceivedCount = 0;
		dataReceivedTotalLength = 0;

		BLELogUtil.d(TAG, "接收到的总数据:" + BLEByteUtil.bytesToHexString(receivedALLBLEData));
		XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer = new XIAODIDataReceivedAnalyzer(receivedALLBLEData);
		Integer alalysisCode = xiaodiDataReceivedAnalyzer.analysisBLEReturnData();
		if(alalysisCode != XIAODIConstants.Error.CorretCode){
			handleError(alalysisCode, null);
			return;
		}
		dispatcherData(xiaodiDataReceivedAnalyzer);
		receivedALLBLEData = null;
	}

	/**
	 * 分发数据
	 * @param xiaodiDataReceivedAnalyzer	接收到的总数据封装管理器
     */
	private void dispatcherData(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer){
		byte[] cmd = xiaodiDataReceivedAnalyzer.getCmd();
		BLELogUtil.d(TAG, "接收数据的命令号=" + BLEByteUtil.bytesToHexString(cmd) + ",发送数据的命令号=" + BLEByteUtil.bytesToHexString(sendCMD));
		if(cmd == null || cmd.length != 1 || cmd[0] != sendCMD[0]){
			handleError(XIAODIConstants.Error.CheckDataCmdNotEquals, xiaodiDataReceivedAnalyzer);
			return;
		}
		if(cmd[0] == 0x24){
			if(!checkAddFingerData(xiaodiDataReceivedAnalyzer)){
				BLELogUtil.d(TAG, "添加指纹返回数据格式不正确");
				onAddFingerListener.fingerTemplateCollectionFailure(XIAODIConstants.Error.CheckDataCheckError, xiaodiDataReceivedAnalyzer);
				return;
			}
			byte[] ack = xiaodiDataReceivedAnalyzer.getAck();
			byte[] dataArea = xiaodiDataReceivedAnalyzer.getDataArea();
			if(ack == null || ack.length !=1 || (ack[0] != 0x00 && ack[0] != 0x01 && ack[0] != 0x02)){
				BLELogUtil.d(TAG, "添加指纹返回数据应答参数不正确");
				onAddFingerListener.fingerTemplateCollectionFailure(XIAODIConstants.Error.CheckCmdParamError, xiaodiDataReceivedAnalyzer);
				return;
			}else if(ack[0] == 0x01){
				BLELogUtil.d(TAG, "添加指纹返回了失败的应答状态");
				onAddFingerListener.fingerTemplateCollectionFailure(XIAODIConstants.Error.CheckCmdError, xiaodiDataReceivedAnalyzer);
				return;
			}else if(ack[0] == 0x02){
				Long fingerCollectionCount = BLEByteUtil.lessThan8bytesToLongInt(dataArea);
				if( fingerCollectionCount == null || fingerCollectionCount < 0 || fingerCollectionCount > 6){
					BLELogUtil.d(TAG, "添加指纹返回的已采集的指纹次数不在0到6之间（cmd=0x24, ack=0x02）");
					onAddFingerListener.fingerTemplateCollectionFailure(XIAODIConstants.Error.CheckAddFingerCollNumError, xiaodiDataReceivedAnalyzer);
					return;
				}
				onAddFingerListener.fingerTemplateCollectionSuccess(fingerCollectionCount, xiaodiDataReceivedAnalyzer);
				return;
			}
			String generatedFingerID = BLEByteUtil.bytesToHexString(BLEByteUtil.getSubbytes(dataArea, 0, 4)).trim();
			Long fingerCollectionCount = BLEByteUtil.lessThan8bytesToLongInt(BLEByteUtil.getSubbytes(dataArea, 4, 1));
			if(fingerCollectionCount == null || fingerCollectionCount < 0 || fingerCollectionCount > 6){
				BLELogUtil.d(TAG, "添加指纹返回的已采集的指纹次数不在0到6之间（cmd=0x24, ack=0x00）");
				onAddFingerListener.fingerTemplateCollectionFailure(XIAODIConstants.Error.CheckAddFingerCollNumError, xiaodiDataReceivedAnalyzer);
				return;
			}
			onAddFingerListener.fingerCollectionSuccess(generatedFingerID, fingerCollectionCount, xiaodiDataReceivedAnalyzer);
			return;
		}else if(cmd[0] == 0x36){
			if(!checkCheckManagePwdData(xiaodiDataReceivedAnalyzer)){
				BLELogUtil.d(TAG, "验证锁上管理密码返回数据格式不正确");
				onCheckManagePwdListener.onCheckFailure(XIAODIConstants.Error.CheckDataCheckError, xiaodiDataReceivedAnalyzer);
				return;
			}
			byte[] ack = xiaodiDataReceivedAnalyzer.getAck();
			if(ack == null || ack.length !=1 || (ack[0] != 0x00 && ack[0] != 0x01 && ack[0] != 0x02)){
				BLELogUtil.d(TAG, "验证锁上管理密码返回数据应答参数不正确");
				onCheckManagePwdListener.onCheckFailure(XIAODIConstants.Error.CheckCmdParamError, xiaodiDataReceivedAnalyzer);
				return;
			}else if(ack[0] == 0x01){
				BLELogUtil.d(TAG, "验证锁上管理密码返回了失败的应答状态");
				onCheckManagePwdListener.onCheckFailure(XIAODIConstants.Error.CheckCmdError, xiaodiDataReceivedAnalyzer);
				return;
			}else if(ack[0] == 0x02){
				BLELogUtil.d(TAG, "验证锁上管理密码,锁已被硬清空");
				onCheckManagePwdListener.onLockForceCleared(xiaodiDataReceivedAnalyzer);
				return;
			}
			onCheckManagePwdListener.onCheckSuccess(xiaodiDataReceivedAnalyzer);
		}else{
			if(!checkXIAODICommonData(xiaodiDataReceivedAnalyzer)){
				BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) + "返回的数据校验失败");
				onCommonListener.failure(XIAODIConstants.Error.CheckDataCheckError, xiaodiDataReceivedAnalyzer);
				return;
			}
			byte[] ack = xiaodiDataReceivedAnalyzer.getAck();
			BLELogUtil.i("ack=" + BLEByteUtil.bytesToHexString(ack));
			if(ack == null || ack.length !=1 || (ack[0] != 0x00 && ack[0] != 0x01)){
				BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) + "返回的数据应答参数不正确");
				onCommonListener.failure(XIAODIConstants.Error.CheckCmdParamError, xiaodiDataReceivedAnalyzer);
				return;
			}else if(ack[0] == 0x01){
				BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) + "返回了失败的应答状态");
				onCommonListener.failure(XIAODIConstants.Error.CheckCmdError, xiaodiDataReceivedAnalyzer);
				return;
			}else {
				onCommonListener.success(xiaodiDataReceivedAnalyzer);
			}
		}
	}

	/**
	 * 返回数据异常处理
	 * @param errorCode	异常代码
	 * @param xiaodiDataReceivedAnalyzer	如果是蓝牙上返回失败,则封装返回的所有数据
     */
	public void handleError(Integer errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer){
		if(onCommonListener != null){
			onCommonListener.failure(errorCode, xiaodiDataReceivedAnalyzer);
			return;
		}
		if(onCheckManagePwdListener != null){
			onCheckManagePwdListener.onCheckFailure(errorCode, xiaodiDataReceivedAnalyzer);
			return;
		}
		if(onAddFingerListener != null){
			onAddFingerListener.fingerTemplateCollectionFailure(errorCode, xiaodiDataReceivedAnalyzer);
			return;
		}
	}

	/**
	 * 添加指纹协议返回数据校验
	 * @param xiaodiDataReceivedAnalyzer	接收到的总数据封装管理器
	 * @return
     */
	public boolean checkAddFingerData(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
		if (xiaodiDataReceivedAnalyzer == null) {
			return false;
		}
		byte[] packageHead = xiaodiDataReceivedAnalyzer.getPackageHead();
		byte[] packageAttribute = xiaodiDataReceivedAnalyzer.getPackageAttribute();
		byte[] cmd = xiaodiDataReceivedAnalyzer.getCmd();
		byte[] dataAreaLength = xiaodiDataReceivedAnalyzer.getDataAreaLength();
		byte[] ack = xiaodiDataReceivedAnalyzer.getAck();
		byte[] dataArea = xiaodiDataReceivedAnalyzer.getDataArea();
		byte[] crc = xiaodiDataReceivedAnalyzer.getCrc();
		//先验证命令字
		if (cmd == null || cmd.length == 0 || cmd[0] != 0x24) {
			BLELogUtil.d(TAG, "0x24, 蓝牙设备返回数据包指令与发送的指令不匹配");
			return false;
		}
		//验证校验位
		byte[] crcself = XIAODIBLECRCUtil.getCRCByteData(xiaodiDataReceivedAnalyzer.getBleDataReceived());
		if (crc == null || !BLEByteUtil.compareTwoBytes(crc, crcself)) {
			BLELogUtil.d(TAG, "0x24, 蓝牙设备返回的数据包校验位校验失败");
			return false;
		}
		//验证数据区长度与实际数据区是否一致
		if ((dataAreaLength == null || dataAreaLength.length != 2)
				|| ((dataArea == null || dataArea.length == 0) && (((dataAreaLength[0] << 8) + dataAreaLength[1]) != 0x0001))
				|| ((dataArea != null && dataArea.length != 0) && (((dataAreaLength[0] << 8) + dataAreaLength[1]) != dataArea.length + 1))) {
			BLELogUtil.d(TAG, "0x24, 蓝牙设备返回的数据包数据区长度指示的长度与实际数据区数据长度不匹配");
			return false;
		}
		//验证数据区,同时条件附带验证应答
		if ((ack == null || ack.length != 1 || (ack[0] != 0x00 && ack[0] != 0x01 && ack[0] != 0x02)) || (ack[0] == 0x00 && (dataArea == null || dataArea.length != 5))
				|| (ack[0] == 0x01 && (dataArea != null)) || (ack[0] == 0x02 && (dataArea == null || dataArea.length != 1))) {
			BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) + "0x24, 蓝牙返回数据区校验失败");
			return false;
		}
		//验证包头
		if (packageHead == null || packageHead.length != 1 || packageHead[0] != (byte) 0xFE) {
			BLELogUtil.d(TAG, "蓝牙设备返回数据包头不正确");
			return false;
		}
		//验证包属性
		if (packageAttribute == null || packageAttribute.length != 1 || packageAttribute[0] != 0x09) {
			BLELogUtil.d(TAG, "蓝牙设备返回数据包属性不正确");
			return false;
		}
		return true;
	}

	/**
	 * 验证管理密码返回的数据校验
	 * @param xiaodiDataReceivedAnalyzer	接收到的总数据封装管理器
	 * @return
     */
	public boolean checkCheckManagePwdData(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
		if (xiaodiDataReceivedAnalyzer == null) {
			return false;
		}
		byte[] packageHead = xiaodiDataReceivedAnalyzer.getPackageHead();
		byte[] packageAttribute = xiaodiDataReceivedAnalyzer.getPackageAttribute();
		byte[] cmd = xiaodiDataReceivedAnalyzer.getCmd();
//			byte[] dataAreaLength = dataReceived.getDataAreaLength();
//			byte[] ack = dataReceived.getAck();
//			byte[] dataArea = dataReceived.getDataArea();
		byte[] crc = xiaodiDataReceivedAnalyzer.getCrc();
		//先验证命令字
		if (cmd == null || cmd.length == 0 || cmd[0] != 0x36) {
			BLELogUtil.d(TAG, "0x36, 蓝牙设备返回数据包指令与发送的指令不匹配");
			return false;
		}
		//验证校验位
		byte[] crcself = XIAODIBLECRCUtil.getCRCByteData(xiaodiDataReceivedAnalyzer.getBleDataReceived());
		if (crc == null || !BLEByteUtil.compareTwoBytes(crc, crcself)) {
			BLELogUtil.d(TAG, "0x36, 蓝牙设备返回的数据包校验位校验失败");
			return false;
		}
//			//验证数据区长度与实际数据区是否一致
//			if((dataAreaLength == null || dataAreaLength.length != 2)
//					|| ((dataArea == null || dataArea.length == 0) && (((dataAreaLength[0]<<8) + dataAreaLength[1]) != 0x0001))
//					|| ((dataArea != null && dataArea.length != 0) && (((dataAreaLength[0]<<8) + dataAreaLength[1]) != dataArea.length + 1))){
//				BLELogUtil.d(TAG, "0x36, 蓝牙设备返回的数据包数据区长度指示的长度与实际数据区数据长度不匹配");
//				return false;
//			}
//			//验证数据区,同时条件附带验证应答
//			if((ack == null || ack.length != 1 || (ack[0] != 0x00 && ack[0] != 0x01 && ack[0] != 0x02)) || (ack[0] == 0x00 && (dataArea == null || dataArea.length != 5))
//					|| (ack[0] ==  0x01 && (dataArea != null)) || (ack[0] == 0x02 && (dataArea == null || dataArea.length != 1))){
//				BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) +  "0x24, 蓝牙返回数据区校验失败");
//				return false;
//			}
		//验证包头
		if (packageHead == null || packageHead.length != 1 || packageHead[0] != (byte) 0xFE) {
			BLELogUtil.d(TAG, "蓝牙设备返回数据包头不正确");
			return false;
		}
		//验证包属性
		if (packageAttribute == null || packageAttribute.length != 1 || packageAttribute[0] != 0x09) {
			BLELogUtil.d(TAG, "蓝牙设备返回数据包属性不正确");
			return false;
		}
		return true;
	}

	/**
	 * 小嘀通用蓝牙操作数据校验
	 * @param xiaodiDataReceivedAnalyzer	接收到的总数据封装管理器
	 * @return
     */
	public boolean checkXIAODICommonData(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
		if (xiaodiDataReceivedAnalyzer == null) {
			return false;
		}
		byte[] packageHead = xiaodiDataReceivedAnalyzer.getPackageHead();
		byte[] packageAttribute = xiaodiDataReceivedAnalyzer.getPackageAttribute();
		byte[] cmd = xiaodiDataReceivedAnalyzer.getCmd();
		byte[] dataAreaLength = xiaodiDataReceivedAnalyzer.getDataAreaLength();
		byte[] ack = xiaodiDataReceivedAnalyzer.getAck();
		byte[] dataArea = xiaodiDataReceivedAnalyzer.getDataArea();
		byte[] crc = xiaodiDataReceivedAnalyzer.getCrc();
		//命令字在BLEHandler中最先校验
		//验证校验位
		byte[] crcself = XIAODIBLECRCUtil.getCRCByteData(xiaodiDataReceivedAnalyzer.getBleDataReceived());
		if (crc == null || !BLEByteUtil.compareTwoBytes(crc, crcself)) {
			BLELogUtil.d(TAG, "蓝牙设备返回的数据包校验位校验失败");
			return false;
		}
//		//验证应答
//		if(ack == null || ack.length != 1 || (ack[0] != 0x00 && ack[0] != 0x01)){
//			BLELogUtil.d(TAG, "蓝牙设备返回数据包应答参数不正确,ack=" + BLEByteUtil.bytesToHexString(ack));
//			return false;
//		}
		if (ack[0] != 0x00) {
			BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) + ",应答失败");
			return false;
		}
		//验证数据区长度与实际数据区是否一致
		if ((dataAreaLength == null || dataAreaLength.length != 2)
				|| ((dataArea == null || dataArea.length == 0) && (((dataAreaLength[0] << 8) + dataAreaLength[1]) != 0x0001))
				|| ((dataArea != null && dataArea.length != 0) && (((dataAreaLength[0] << 8) + dataAreaLength[1]) != dataArea.length + 1))) {
			BLELogUtil.d(TAG, "蓝牙设备返回的数据包数据区长度指示的长度与实际数据区数据长度不匹配");
			return false;
		}
		//验证数据区
//		if(((cmd[0] != 0x03 && cmd[0] != 0x20 && cmd[0] != 0x27 && cmd[0] != 0x34) && dataArea != null)
//				|| (cmd[0] == 0x03 && (dataArea == null || dataArea.length != 2 || (dataArea[1] != 0x00 && dataArea[1] != 0x0F && dataArea[1] != 0xF0 && dataArea[1] != 0xFF)))
//				|| (cmd[0] == 0x20 && (dataArea == null || dataArea.length != 12))
//				|| (cmd[0] == 0x27 && (dataArea == null || dataArea.length != 4 || BLEByteUtil.lessThan8bytesToLongInt(dataAreaLength) < 1) || BLEByteUtil.lessThan8bytesToLongInt(dataAreaLength) > 9999)
//				|| (cmd[0] == 0x34 && (dataArea == null || dataArea.length != 16))){
//			BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) +  ",蓝牙返回数据区校验失败");
//			return false;
//		}
//		if(((cmd[0] != 0x20 && cmd[0] != 0x27 && cmd[0] != 0x34) && dataArea != null)
//				|| (cmd[0] == 0x20 && (dataArea == null || dataArea.length != 12))
//				|| (cmd[0] == 0x27 && (dataArea == null || dataArea.length != 4 || BLEByteUtil.lessThan8bytesToLongInt(dataAreaLength) < 1) || BLEByteUtil.lessThan8bytesToLongInt(dataAreaLength) > 9999)
//				|| (cmd[0] == 0x34 && (dataArea == null || dataArea.length != 16))){
//			BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) +  ",蓝牙返回数据区校验失败");
//			return false;
//		}
		//验证包头
		if (packageHead == null || packageHead.length != 1 || packageHead[0] != (byte) 0xFE) {
			BLELogUtil.d(TAG, "蓝牙设备返回数据包头不正确,packageHead=" + BLEByteUtil.bytesToHexString(packageHead));
			return false;
		}
		//验证包属性
		if (packageAttribute == null || packageAttribute.length != 1 || packageAttribute[0] != 0x09) {
			BLELogUtil.d(TAG, "蓝牙设备返回数据包属性不正确,packageAttribute=" + BLEByteUtil.bytesToHexString(packageAttribute));
			return false;
		}
		BLELogUtil.d(TAG, "0x" + BLEByteUtil.bytesToHexString(cmd) + ",应答成功");
		return true;
	}
}
