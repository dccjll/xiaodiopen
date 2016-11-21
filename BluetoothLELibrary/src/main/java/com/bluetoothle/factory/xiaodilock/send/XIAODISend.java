package com.bluetoothle.factory.xiaodilock.send;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BLEManage;
import com.bluetoothle.core.init.BLEInit;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.factory.xiaodilock.protocol.XIAODIBLEProtocol;
import com.bluetoothle.factory.xiaodilock.protocol.XIAODIBLEUUID;
import com.bluetoothle.factory.xiaodilock.received.XIAODIDataReceived;
import com.bluetoothle.factory.xiaodilock.util.XIAODIConstants;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

/**
 * Created by dessmann on 16/10/18.
 * 小嘀发送蓝牙数据
 */

public class XIAODISend {

    private final static String TAG = XIAODISend.class.getSimpleName();

    public interface OnSmartKeyInitListener{
        void onInitSuccess();
        void onInitFaiure(Object obj);
    }

    /**
     * 发送数据,不监听接收数据
     * @param mac   目前设备mac地址
     * @param cmdType   命令字类型
     * @param xiaodiData    要发送的数据集合
     * @param disconnectOnFinish    任务完成后是否断开蓝牙连接
     * @param onBLEWriteDataListener    数据发送监听器
     */
    public static void send(String mac, String cmdType, XIAODIData xiaodiData, Boolean disconnectOnFinish, final OnBLEWriteDataListener onBLEWriteDataListener){
        if(onBLEWriteDataListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        XIAODIBLEProtocol xiaodibleProtocol = new XIAODIBLEProtocol(cmdType, xiaodiData);
        if(!xiaodibleProtocol.buildData()){
            onBLEWriteDataListener.onWriteDataFail(XIAODIConstants.Error.BuildData);
            return;
        }
        byte[] data = xiaodibleProtocol.getBleDataSend();
        BLELogUtil.e(TAG, "准备发送数据,mac=" + mac + ",data=" + BLEByteUtil.bytesToHexString(data));
        if(BLEInit.bluetoothAdapter == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.BluetoothAdapter);
            return;
        }
        BLEManage bleManage = new BLEManage(BLEInit.bluetoothAdapter, mac, null, XIAODIBLEUUID.buildTwoUUIDs(), null, disconnectOnFinish);
        bleManage.setData(data);
        bleManage.setOnBLEWriteDataListener(onBLEWriteDataListener);
        bleManage.write();
    }

    /**
     * 发送数据,监听接收数据
     * @param mac   目前设备mac地址
     * @param cmdType   命令字类型
     * @param xiaodiData    要发送的数据集合
     * @param disconnectOnFinish    任务完成后是否断开蓝牙连接
     * @param xiaodiDataReceived    数据接收监听器
     */
    public static void send(String mac, String cmdType, XIAODIData xiaodiData, Boolean disconnectOnFinish, final XIAODIDataReceived xiaodiDataReceived){
        if(xiaodiDataReceived == null){
            BLELogUtil.e(TAG, "没有配置接收数据回调接口");
            return;
        }
        XIAODIBLEProtocol xiaodibleProtocol = new XIAODIBLEProtocol(cmdType, xiaodiData);
        if(!xiaodibleProtocol.buildData()){
            xiaodiDataReceived.onError(XIAODIConstants.Error.BuildData);
            return;
        }
        byte[] data = xiaodibleProtocol.getBleDataSend();
        BLELogUtil.e(TAG, "准备发送数据,mac=" + mac + ",data=" + BLEByteUtil.bytesToHexString(data));
        if(BLEInit.bluetoothAdapter == null){
            xiaodiDataReceived.onError(BLEConstants.Error.BluetoothAdapter);
            return;
        }
        BLEManage bleManage = new BLEManage(BLEInit.bluetoothAdapter, mac, null, XIAODIBLEUUID.buildFiveUUIDs(), null, disconnectOnFinish);
        bleManage.setData(data);
        bleManage.setOnBLEResponse(xiaodiDataReceived);
        bleManage.write();
    }

    /**
     * 智能钥匙初始化
     * @param mac   智能钥匙mac地址
     * @param onSmartKeyInitListener    初始化监听器
     */
    public static void smartKeyInit(final String mac, final OnSmartKeyInitListener onSmartKeyInitListener){
        final String tag = "智能钥匙初始化";
        if(onSmartKeyInitListener == null){
            BLELogUtil.e(TAG, "没有配置智能钥匙初始化监听器");
            return;
        }
        byte[] data = XIAODIBLEProtocol.buildSmartKeyInitData();
        BLELogUtil.e(TAG, "准备发送数据,mac=" + mac + ",data=" + BLEByteUtil.bytesToHexString(data));
        if(BLEInit.bluetoothAdapter == null){
            onSmartKeyInitListener.onInitFaiure(BLEConstants.Error.BluetoothAdapter);
            return;
        }
        BLEManage bleManage = new BLEManage(BLEInit.bluetoothAdapter, mac, null, XIAODIBLEUUID.buildSmartKeyUUIDs(), null, false);
        bleManage.setData(data);
        bleManage.setOnBLEResponse(
                new XIAODIDataReceived()
        );
        bleManage.write();
    }
}
