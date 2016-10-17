package com.bluetoothle.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import com.bluetoothle.core.connect.BLEConnect;
import com.bluetoothle.core.findService.BLEFindService;
import com.bluetoothle.core.openNotification.BLEOpenNotification;
import com.bluetoothle.util.BLELogUtil;
import com.bluetoothle.core.writeData.BLEWriteData;

import java.util.UUID;

/**
 * Created by dessmann on 16/10/17.
 * 底层蓝牙回调状态管理器
 */

public class BluetoothLeGattCallback extends BluetoothGattCallback {

    private final static String TAG = BluetoothLeGattCallback.class.getSimpleName();
    private BLEConnect.OnGattConnectListener onGattConnectListener;
    private BLEFindService.OnGattBLEFindServiceListener onGattBLEFindServiceListener;
    private BLEOpenNotification.OnGattBLEOpenNotificationListener onGattBLEOpenNotificationListener;
    private BLEWriteData.OnGattBLEWriteDataListener onGattBLEWriteDataListener;
    private UUID uuidCharacteristicWrite;
    private UUID uuidCharacteristicChange;
    private UUID uuidDescriptorWrite;

    public void registerOnGattConnectListener(BLEConnect.OnGattConnectListener onGattConnectListener) {
        this.onGattConnectListener = onGattConnectListener;
        this.onGattBLEFindServiceListener = null;
        this.onGattBLEOpenNotificationListener = null;
        this.onGattBLEWriteDataListener = null;
    }

    public void registerOnGattBLEFindServiceListener(BLEFindService.OnGattBLEFindServiceListener onGattBLEFindServiceListener) {
        this.onGattConnectListener = null;
        this.onGattBLEFindServiceListener = onGattBLEFindServiceListener;
        this.onGattBLEOpenNotificationListener = null;
        this.onGattBLEWriteDataListener = null;
    }

    public void registerOnGattBLEOpenNotificationListener(BLEOpenNotification.OnGattBLEOpenNotificationListener onGattBLEOpenNotificationListener) {
        this.onGattConnectListener = null;
        this.onGattBLEFindServiceListener = null;
        this.onGattBLEOpenNotificationListener = onGattBLEOpenNotificationListener;
        this.onGattBLEWriteDataListener = null;
    }

    public void registerOnGattBLEWriteDataListener(BLEWriteData.OnGattBLEWriteDataListener onGattBLEWriteDataListener) {
        this.onGattConnectListener = null;
        this.onGattBLEFindServiceListener = null;
        this.onGattBLEOpenNotificationListener = null;
        this.onGattBLEWriteDataListener = onGattBLEWriteDataListener;
    }

    public void setUuidCharacteristicWrite(UUID uuidCharacteristicWrite) {
        this.uuidCharacteristicWrite = uuidCharacteristicWrite;
    }

    public void setUuidCharacteristicChange(UUID uuidCharacteristicChange) {
        this.uuidCharacteristicChange = uuidCharacteristicChange;
    }

    public void setUuidDescriptorWrite(UUID uuidDescriptorWrite) {
        this.uuidDescriptorWrite = uuidDescriptorWrite;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        BLELogUtil.e(TAG, "onConnectionStateChange,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
        if(status == BluetoothGatt.GATT_SUCCESS){
            if(newState == BluetoothProfile.STATE_CONNECTING){
                BLELogUtil.e(TAG, "正在连接,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
            }else if(newState == BluetoothProfile.STATE_CONNECTED){
                BLELogUtil.e(TAG, "已连接,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
                if(onGattConnectListener != null){
                    onGattConnectListener.onConnectSuccss(gatt, status, newState);
                }
            }else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                BLELogUtil.e(TAG, "正在断开,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                BLELogUtil.e(TAG, "已断开,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
                if (onGattConnectListener != null) {
                    onGattConnectListener.onConnectFail(BLEConstants.ConnectError.ConnectError_BLEConextError);
                    return;
                }
                if (onGattBLEFindServiceListener != null) {
                    onGattBLEFindServiceListener.onFindServiceFail(BLEConstants.FindServiceError.FindServiceError_Disconnect);
                    return;
                }
                if (onGattBLEOpenNotificationListener != null) {
                    onGattBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_OpenFail);
                }
            }
        }else{
            BLELogUtil.e(TAG, "收到蓝牙底层协议栈异常消息,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
            if (onGattConnectListener != null) {
                onGattConnectListener.onConnectFail(BLEConstants.ConnectError.ConnectError_ReceivedExceptionStackCodeError);
                return;
            }
            if (onGattBLEFindServiceListener != null) {
                onGattBLEFindServiceListener.onFindServiceFail(BLEConstants.FindServiceError.FindServiceError_ReceivedExceptionStackCodeError);
                return;
            }
            if (onGattBLEOpenNotificationListener != null) {
                onGattBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_OpenFail);
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        BLELogUtil.e(TAG, "onServicesDiscovered,gatt=" + gatt + ",status=" + status);
        if(status == BluetoothGatt.GATT_SUCCESS){
            if(onGattBLEFindServiceListener != null){
                onGattBLEFindServiceListener.onFindServiceSuccess(gatt, status, gatt.getServices());
            }
        }else{
            if (onGattBLEFindServiceListener != null) {
                onGattBLEFindServiceListener.onFindServiceFail(BLEConstants.FindServiceError.FindServiceError_FindServiceFail);
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if(status != BluetoothGatt.GATT_SUCCESS || !characteristic.getUuid().toString().equalsIgnoreCase(uuidCharacteristicWrite.toString())){
            if(onGattBLEWriteDataListener != null){
                onGattBLEWriteDataListener.onWriteDataFail(BLEConstants.WriteDataError.WriteDataError_WriteDataFail);
            }
            return;
        }
        if(onGattBLEWriteDataListener != null){
            onGattBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if(characteristic.getUuid().toString().equalsIgnoreCase(uuidCharacteristicChange.toString())){//接收到数据

        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        BLELogUtil.e(TAG, "onDescriptorWrite,gatt=" + gatt + ",descriptor=" + descriptor + ",status=" + status);
        if(status != BluetoothGatt.GATT_SUCCESS || !descriptor.getUuid().toString().equalsIgnoreCase(uuidDescriptorWrite.toString())){
            if (onGattBLEOpenNotificationListener != null) {
                onGattBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_OpenFail);
            }
            return;
        }
        if(onGattBLEOpenNotificationListener != null){
            onGattBLEOpenNotificationListener.onOpenNotificationSuccess(gatt, descriptor, status);
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
    }
}
