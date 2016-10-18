package com.bluetoothle.factory.xiaodilock.send;

import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.factory.xiaodilock.protocol.XIAODIBLEProtocol;
import com.bluetoothle.factory.xiaodilock.received.XIAODIDATAReceived;
import com.bluetoothle.factory.xiaodilock.util.XIAODIConstants;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

/**
 * Created by dessmann on 16/10/18.
 * 小嘀发送蓝牙数据
 */

public class XIAODISend {

    private final static String TAG = XIAODISend.class.getSimpleName();

    /**
     * 发送数据,不监听接收数据
     * @param mac   目前设备mac地址
     * @param cmdType   命令字类型
     * @param xiaodiData    要发送的数据集合
     * @param onBLEWriteDataListener    数据发送监听器
     */
    public static void send(String mac, String cmdType, XIAODIData xiaodiData, final OnBLEWriteDataListener onBLEWriteDataListener){
        XIAODIBLEProtocol xiaodibleProtocol = new XIAODIBLEProtocol(cmdType, xiaodiData);
        if(!xiaodibleProtocol.buildData()){
            onBLEWriteDataListener.onWriteDataFail(XIAODIConstants.SendDataStatics.BuildDataCheckError);
            return;
        }
        byte[] data = xiaodibleProtocol.getBleDataSend();
        BLELogUtil.e(TAG, "准备发送数据,mac=" + mac + ",data=" + BLEByteUtil.bytesToHexString(data));
    }

    /**
     * 发送数据,监听接收数据
     * @param mac   目前设备mac地址
     * @param cmdType   命令字类型
     * @param xiaodiData    要发送的数据集合
     * @param onBLEWriteDataListener    数据发送监听器
     * @param xiaodidataReceived    数据接收监听器
     */
    public static void send(String mac, String cmdType, XIAODIData xiaodiData, final OnBLEWriteDataListener onBLEWriteDataListener, final XIAODIDATAReceived xiaodidataReceived){
        XIAODIBLEProtocol xiaodibleProtocol = new XIAODIBLEProtocol(cmdType, xiaodiData);
        if(!xiaodibleProtocol.buildData()){
            xiaodidataReceived.handleError(XIAODIConstants.SendDataStatics.BuildDataCheckError, null);
            return;
        }
        byte[] data = xiaodibleProtocol.getBleDataSend();
        BLELogUtil.e(TAG, "准备发送数据,mac=" + mac + ",data=" + BLEByteUtil.bytesToHexString(data));
    }
}
