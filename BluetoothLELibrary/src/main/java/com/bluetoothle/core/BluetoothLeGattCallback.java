package com.bluetoothle.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import com.bluetoothle.Request;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

import java.util.UUID;

/**
 * Created by dessmann on 16/7/27.
 * 底层蓝牙回调状态管理器
 */
public class BluetoothLeGattCallback extends BluetoothGattCallback {

    private final static String tag = BluetoothLeGattCallback.class.getSimpleName();

    private BluetoothLeDevice bluetoothLeDevice;
    private Request request;

    public BluetoothLeGattCallback(BluetoothLeDevice bluetoothLeDevice) {
        this.bluetoothLeDevice = bluetoothLeDevice;
        this.request = bluetoothLeDevice.getRequest();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if(status == BluetoothGatt.GATT_SUCCESS){
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                BLELogUtil.e(tag, "connected to GATT server.");
                BluetoothLeManage2.getSingleInstance().mConnectionState = BluetoothLeManage2.STATE_CONNECTED;
                bluetoothLeDevice.connectEvent.setSignal(true);
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                BLELogUtil.e(tag, "Disconnected from GATT server.");
                BluetoothLeManage2.getSingleInstance().mConnectionState = BluetoothLeManage2.STATE_DISCONNECTED;
                bluetoothLeDevice.connectEvent.setSignal(false);
                bluetoothLeDevice.findServiceEvent.setSignal(false);
                bluetoothLeDevice.notificationEvent.setSignal(false);
                bluetoothLeDevice.sendEvent.setSignal(false);
            }
        }else {//收到异常协议栈消息
            BLELogUtil.e(tag, "received error ble code:" + status);
            BluetoothLeManage2.getSingleInstance().mConnectionState = BluetoothLeManage2.STATE_DISCONNECTED;
            bluetoothLeDevice.connectEvent.setSignal(false);
            bluetoothLeDevice.findServiceEvent.setSignal(false);
            bluetoothLeDevice.notificationEvent.setSignal(false);
            bluetoothLeDevice.sendEvent.setSignal(false);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            bluetoothLeDevice.findServiceEvent.setSignal(true);
        } else {
            bluetoothLeDevice.findServiceEvent.setSignal(false);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        BLELogUtil.d(tag, "=======onCharacteristicWrite===status==" + status + "\nwritten data:" + BLEByteUtil.bytesToHexString(characteristic.getValue()));
        if(status == BluetoothGatt.GATT_SUCCESS && characteristic.getUuid().equals(UUID.fromString(request.getCharacteristics_uuid_write()))){
            bluetoothLeDevice.sendEvent.setSignal(true);
        }else{
            bluetoothLeDevice.sendEvent.setSignal(false);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        BLELogUtil.d(tag, "onCharacteristicRead,status=" + status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if(BluetoothLeManage2.getSingleInstance().isDisconnecting){
            BLELogUtil.d(tag, "正在断开连接");
            return;
        }
        BluetoothLeManage2.getSingleInstance().configAutoDisconnectRunnable(false);
        BLELogUtil.d(tag, "=onCharacteristicChanged===接收到数据===characteristicsuuid=" + characteristic.getUuid() + "\n=====receivedData==" + BLEByteUtil.bytesToHexString(characteristic.getValue()));
//        if(BluetoothLeManage2.getSingleInstance().isShaking){
//            BluetoothLeManage2.broadcastUpdate(BluetoothLeManage2.ACTION_DATA_AVAILABLE_SHAKING, characteristic.getValue());
//        }else{
//            BluetoothLeManage2.broadcastUpdate(BluetoothLeManage2.ACTION_DATA_AVAILABLE, characteristic.getValue());
//        }
        BluetoothLeManage2.broadcastUpdate(BluetoothLeManage2.ACTION_DATA_AVAILABLE, characteristic.getValue());
        BluetoothLeManage2.getSingleInstance().configAutoDisconnectRunnable(true);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BLELogUtil.d(tag, "Descript success ");
            bluetoothLeDevice.notificationEvent.setSignal(true);
        } else {
            bluetoothLeDevice.notificationEvent.setSignal(false);
        }
    }
}
