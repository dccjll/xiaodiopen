package com.bluetoothle;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import com.bluetoothle.connect.BLEConnect;
import com.bluetoothle.util.BLELogUtil;

import java.util.UUID;

/**
 * Created by dessmann on 16/10/17.
 * 底层蓝牙回调状态管理器
 */

public class BluetoothLeGattCallback extends BluetoothGattCallback {

    private final static String TAG = BluetoothLeGattCallback.class.getSimpleName();
    private BLEConnect.OnGattConnectListener onGattConnectListener;
    private UUID writeUUID;
    private UUID changeUUID;

    public void registerOnGattConnectListener(BLEConnect.OnGattConnectListener onGattConnectListener) {
        this.onGattConnectListener = onGattConnectListener;
    }

    public void unregisterOnGattConnectListener() {
        this.onGattConnectListener = null;
    }

    public void setWriteUUID(UUID writeUUID) {
        this.writeUUID = writeUUID;
    }

    public void setChangeUUID(UUID changeUUID) {
        this.changeUUID = changeUUID;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if(status == BluetoothGatt.GATT_SUCCESS){
            if(newState == BluetoothProfile.STATE_CONNECTING){
                BLELogUtil.e(TAG, "正在连接,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
            }else if(newState == BluetoothProfile.STATE_CONNECTED){
                BLELogUtil.e(TAG, "已连接,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
                if(onGattConnectListener != null){
                    onGattConnectListener.onConnectSuccss(gatt);
                }
            }else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                BLELogUtil.e(TAG, "正在断开,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                BLELogUtil.e(TAG, "已断开,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
                if (onGattConnectListener != null) {
                    onGattConnectListener.onConnectFail(BLEConstants.ConnectError.ConnectError_BLEConextError);
                }
            }
        }else{
            BLELogUtil.e(TAG, "收到蓝牙底层协议栈异常消息,gatt=" + gatt + ",status=" + status + ",newState=" + newState);
            if (onGattConnectListener != null) {
                onGattConnectListener.onConnectFail(BLEConstants.ConnectError.ConnectError_ReceivedExceptionStackCodeError);
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
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
