package com.bluetoothle.core.writeData;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.connect.BLEConnect;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

import java.util.UUID;

/**
 * Created by dessmann on 16/10/17.
 * 蓝牙写数据
 */

public class BLEWriteData {

    private final static String TAG = BLEWriteData.class.getSimpleName();
    private BluetoothGatt bluetoothGatt;
    private UUID[] uuids;
    private byte[] data;
    private OnBLEWriteDataListener onBLEWriteDataListener;

    public interface OnGattBLEWriteDataListener{
        void onWriteDataFinish();
        void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
        void onWriteDataFail(Integer errorCode);
    }

    /**
     * 写数据构造器
     * @param bluetoothGatt 蓝牙服务器连接
     * @param uuids 包含服务、特征与描述对象的2个UUID(按顺序 uuids[0]为服务UUID, uuids[1]为特征UUID)
     * @param data  要写入的总数据字节
     * @param onBLEWriteDataListener    写数据监听器
     */
    public BLEWriteData(BluetoothGatt bluetoothGatt, UUID[] uuids, byte[] data, OnBLEWriteDataListener onBLEWriteDataListener) {
        this.bluetoothGatt = bluetoothGatt;
        this.uuids = uuids;
        this.data = data;
        this.onBLEWriteDataListener = onBLEWriteDataListener;
    }

    /**
     * 写数据
     */
    private Integer writtenDataLength;
    public void writeData(){
        if(onBLEWriteDataListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(bluetoothGatt == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBluetoothGattError);
            return;
        }
        if(uuids == null || uuids.length != 2){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckUUIDArraysError);
            return;
        }
        if(data == null || data.length == 0){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBLEDataError);
            return;
        }
        writtenDataLength = 0;
        BLEConnect.bluetoothLeGattCallback.setUuidCharacteristicWrite(uuids[1]);
        BLEConnect.bluetoothLeGattCallback.registerOnGattBLEWriteDataListener(
                new OnGattBLEWriteDataListener() {
                    @Override
                    public void onWriteDataFinish() {
                        onBLEWriteDataListener.onWriteDataFinish();
                    }

                    @Override
                    public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
                        if((writtenDataLength += characteristic.getValue().length) == data.length){
                            onWriteDataFinish();
                        }
                    }

                    @Override
                    public void onWriteDataFail(Integer errorCode) {
                        onBLEWriteDataListener.onWriteDataFail(errorCode);
                    }
                }
        );
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(uuids[0]);
        if(bluetoothGattService == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBluetoothGattError);
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(uuids[1]);
        if(bluetoothGattCharacteristic == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBluetoothGattCharacteristicError);
            return;
        }
        try {
            divideSendBLEData(bluetoothGattCharacteristic, data);
        } catch (InterruptedException e) {
            e.printStackTrace();
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
            return;
        }
    }

    private final static long INTERVAL_SEND_NEXT_PACKAGE = 70;//发送多个数据包时的时间间隔
    private final static int MAX_BYTES = 20;// 蓝牙发送数据分包，每个包的最大长度为20个字节
    /**
     * 拆分写数据
     * @param bluetoothGattCharacteristic 写入的蓝牙特征对象
     * @param value 写入的数据
     */
    private void divideSendBLEData(final BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] value) throws InterruptedException {
        int length = value.length;
        int sendLength = 0;
        int position = 0;

        while (length > 0) {
            if (length > MAX_BYTES) {
                sendLength = MAX_BYTES;
                if(length < value.length){
                    BLELogUtil.d(TAG, "间隔" + INTERVAL_SEND_NEXT_PACKAGE + "ms再发送下一个数据包");
                    Thread.sleep(INTERVAL_SEND_NEXT_PACKAGE);
                }
            } else if (length > 0) {
                sendLength = length;
                if(length < value.length){
                    BLELogUtil.d(TAG, "间隔" + INTERVAL_SEND_NEXT_PACKAGE + "ms再发送最后一个数据包");
                    Thread.sleep(INTERVAL_SEND_NEXT_PACKAGE);
                }
            } else{
                onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
                return;
            }
            // 发送数据
            byte[] sendValue = BLEByteUtil.getSubbytes(value, position, sendLength);
            if (sendValue == null) {
                onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
                return;
            }
            if (!bluetoothGattCharacteristic.setValue(sendValue)) {
                onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
                return;
            }
            BLELogUtil.d(TAG, "position=" + position + ",sendValue=" + BLEByteUtil.bytesToHexString(sendValue));
            bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
            length -= sendLength;
            position += sendLength;
        }
    }
}