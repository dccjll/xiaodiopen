package com.bluetoothle.factory.doorguard.hainan;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BluetoothLeManage;
import com.bluetoothle.core.init.BLEInit;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.factory.doorguard.DoorGuardProtocol;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

/**
 * Created by dessmann on 16/10/19.
 * 海南门禁发送数据
 */

public class HaiNanDoorGuardSend {
    private final static String TAG = HaiNanDoorGuardSend.class.getSimpleName();

    /**
     * 发送数据开门命令,不监听接收数据
     * @param mac   目前设备mac地址
     * @param cmd   命令字节号
     * @param mobile    手机号码字节数组
     * @param onBLEWriteDataListener    数据发送监听器
     */
    public static void sendOpen(String mac, byte cmd, byte[] mobile, final OnBLEWriteDataListener onBLEWriteDataListener){
        if(onBLEWriteDataListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(cmd == 0 || mobile == null || mobile.length != 13){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBLEDataError);
            return;
        }
        byte[] data = HaiNanDoorGuardProtocol.buildOpenBLEData(cmd, mobile);
        BLELogUtil.e(TAG, "准备发送数据,mac=" + mac + ",data=" + BLEByteUtil.bytesToHexString(data));
        send(mac, data, onBLEWriteDataListener);
    }

    /**
     * 发送配置命令,不监听接收数据
     * @param mac   目前设备mac地址
     * @param cmd   命令字节号
     * @param toData    需要配置的数据字节数组
     * @param onBLEWriteDataListener    数据发送监听器
     */
    public static void sendConfig(String mac, byte cmd, byte[] toData, final OnBLEWriteDataListener onBLEWriteDataListener){
        if(onBLEWriteDataListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(cmd == 0 || toData == null || toData.length == 0){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBLEDataError);
            return;
        }
        byte[] data = HaiNanDoorGuardProtocol.buildSettingBLEData(cmd, toData);
        BLELogUtil.e(TAG, "准备发送数据,mac=" + mac + ",data=" + BLEByteUtil.bytesToHexString(data));
        send(mac, data, onBLEWriteDataListener);
    }

    /**
     * 通用发送数据
     * @param mac   目前设备mac地址
     * @param data  发送的数据
     * @param onBLEWriteDataListener    数据发送监听器
     */
    private static void send(String mac, byte[] data, OnBLEWriteDataListener onBLEWriteDataListener) {
        if(data == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBLEDataError);
            return;
        }
        if(BLEInit.bluetoothAdapter == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBluetoothAdapterError);
            return;
        }
        BluetoothLeManage bluetoothLeManage = new BluetoothLeManage(BLEInit.bluetoothAdapter, mac, null, DoorGuardProtocol.buildTwoUUIDs(), null);
        bluetoothLeManage.setData(data);
        bluetoothLeManage.setOnBLEWriteDataListener(onBLEWriteDataListener);
        bluetoothLeManage.write();
    }
}
