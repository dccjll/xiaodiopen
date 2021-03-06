package com.bluetoothle.core.writeData;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BLEGattCallback;
import com.bluetoothle.core.connect.BLEConnect;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

import java.util.List;
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
    private List<byte[]> dataList;
    private OnBLEWriteDataListener onBLEWriteDataListener;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private Integer index = 0;//当前发送的第几个数据包
    private BLEGattCallback bleGattCallback;

    public interface OnGattBLEWriteDataListener{
        void onWriteDataFinish();
        void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
        void onWriteDataFail(String errorCode);
    }

    /**
     * 写数据构造器
     * @param bluetoothGatt 蓝牙服务器连接
     * @param uuids 包含服务、特征与描述对象的2个UUID(按顺序 uuids[0]为服务UUID, uuids[1]为特征UUID)
     * @param data  要写入的总数据字节
     * @param onBLEWriteDataListener    写数据监听器
     */
    public BLEWriteData(BluetoothGatt bluetoothGatt, UUID[] uuids, byte[] data, BLEGattCallback bleGattCallback, OnBLEWriteDataListener onBLEWriteDataListener) {
        this.bluetoothGatt = bluetoothGatt;
        this.uuids = uuids;
        this.data = data;
        this.bleGattCallback = bleGattCallback;
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
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.BluetoothGatt);
            return;
        }
        if(uuids == null || uuids.length != 2){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.UUIDArrays);
            return;
        }
        if(data == null || data.length == 0){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBLEDataError);
            return;
        }
        dataList = BLEByteUtil.paeseByteArrayToByteList(data, MAX_BYTES);
        if(dataList == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBLEDataError);
            return;
        }
        if(bleGattCallback == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.BluetoothGattCallBack);
            return;
        }
        writtenDataLength = 0;
        bleGattCallback.setUuidCharacteristicWrite(uuids[1]);
        bleGattCallback.registerOnGattBLEWriteDataListener(
                new OnGattBLEWriteDataListener() {
                    @Override
                    public void onWriteDataFinish() {
                        onBLEWriteDataListener.onWriteDataFinish();
                    }

                    @Override
                    public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status, bleGattCallback);
                        if((writtenDataLength += characteristic.getValue().length) == data.length){
                            onWriteDataFinish();
                            return;
                        }
                        try {
                            BLELogUtil.d(TAG, "间隔" + INTERVAL_SEND_NEXT_PACKAGE + "ms再发送下一个数据包");
                            Thread.sleep(INTERVAL_SEND_NEXT_PACKAGE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendBLEData(dataList.get(++index));
                    }

                    @Override
                    public void onWriteDataFail(String errorCode) {
                        onBLEWriteDataListener.onWriteDataFail(errorCode);
                    }
                }
        );
        bluetoothGattService = bluetoothGatt.getService(uuids[0]);
        if(bluetoothGattService == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.BluetoothGatt);
            return;
        }
        bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(uuids[1]);
        if(bluetoothGattCharacteristic == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.BluetoothGattCharacteristic);
            return;
        }
//        try {
//            divideSendBLEData(bluetoothGattCharacteristic, data);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
//            return;
//        }
        sendBLEData(dataList.get(index));
    }

    private final static long INTERVAL_SEND_NEXT_PACKAGE = 70;//发送多个数据包时的时间间隔
    private final static int MAX_BYTES = 20;// 蓝牙发送数据分包，每个包的最大长度为20个字节
    /**
     * 写数据
     * @param value 写入的数据
     */
    private void sendBLEData(byte[] value) {
        if (!bluetoothGattCharacteristic.setValue(value)) {
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
            return;
        }
        BLELogUtil.d(TAG, "sendValue=" + BLEByteUtil.bytesToHexString(value));
        if(!bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic)){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
            return;
        }
    }
//    /**
//     * 拆分写数据
//     * @param bluetoothGattCharacteristic 写入的蓝牙特征对象
//     * @param value 写入的数据
//     */
//    private void divideSendBLEData(final BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] value) throws InterruptedException {
//        int length = value.length;
//        int sendLength = 0;
//        int position = 0;
//
//        while (length > 0) {
//            if (length > MAX_BYTES) {
//                sendLength = MAX_BYTES;
//                if(length < value.length){
//                    BLELogUtil.d(TAG, "间隔" + INTERVAL_SEND_NEXT_PACKAGE + "ms再发送下一个数据包");
//                    Thread.sleep(INTERVAL_SEND_NEXT_PACKAGE);
//                }
//            } else if (length > 0) {
//                sendLength = length;
//                if(length < value.length){
//                    BLELogUtil.d(TAG, "间隔" + INTERVAL_SEND_NEXT_PACKAGE + "ms再发送最后一个数据包");
//                    Thread.sleep(INTERVAL_SEND_NEXT_PACKAGE);
//                }
//            } else{
//                onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
//                return;
//            }
//            // 发送数据
//            byte[] sendValue = BLEByteUtil.getSubbytes(value, position, sendLength);
//            if (sendValue == null) {
//                onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
//                return;
//            }
//            if (!bluetoothGattCharacteristic.setValue(sendValue)) {
//                onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.WriteDataError);
//                return;
//            }
//            BLELogUtil.d(TAG, "position=" + position + ",sendValue=" + BLEByteUtil.bytesToHexString(sendValue));
//            bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            length -= sendLength;
//            position += sendLength;
//        }
//    }
}