package com.bluetoothle.factory.doorguard;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BLEManage;
import com.bluetoothle.core.init.BLEInit;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

/**
 * Created by dessmann on 16/10/19.
 * 门禁发送数据
 */

public class DoorGuardSend {
    private final static String TAG = DoorGuardSend.class.getSimpleName();

    /**
     * 发送数据,不监听接收数据
     * @param mac   目前设备mac地址
     * @param onBLEWriteDataListener    数据发送监听器
     */
    public static void send(String mac, final OnBLEWriteDataListener onBLEWriteDataListener){
        if(onBLEWriteDataListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        byte[] data = DoorGuardProtocol.buildData();
        BLELogUtil.e(TAG, "准备发送数据,mac=" + mac + ",data=" + BLEByteUtil.bytesToHexString(data));
        if(BLEInit.bluetoothAdapter == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBluetoothAdapterError);
            return;
        }
        BLEManage bleManage = new BLEManage(BLEInit.bluetoothAdapter, mac, null, DoorGuardProtocol.buildTwoUUIDs(), null, true);
        bleManage.setData(data);
        bleManage.setOnBLEWriteDataListener(onBLEWriteDataListener);
        bleManage.write();
    }
}
