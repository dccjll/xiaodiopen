package com.bluetoothle.core;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.bluetoothle.Request;
import com.bluetoothle.WaitEvent;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by dessmann on 16/7/26.
 * BLE连接设备管理,目前只支持单连接
 */
public class BluetoothLeDevice implements Serializable {
    
    private final static String tag = BluetoothLeDevice.class.getSimpleName();

    /**
     * 封装蓝牙连接设备构造器
     */
    private Service service;
    private BluetoothDevice bluetoothDevice;

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    private Request request;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public BluetoothLeDevice(Request request, BluetoothDevice mBluetoothDevice, Service service) {
        this.request = request;
        this.bluetoothDevice = mBluetoothDevice;
        this.service = service;
    }

    /**
     * 发起连接，传入设备mac地址
     */
    public WaitEvent connectEvent = new WaitEvent();//连接阻塞控制
    public boolean connectOperateStatus = true;//连接操作状态
    public boolean connectDevice(final String address) {//发起连接
        try {
            BluetoothLeManage2.getSingleInstance().mConnectionState = BluetoothLeManage2.STATE_CONNECTING;
            connectOperateStatus = true;
            connectEvent.init(new WaitEvent.WaitListener() {

                @Override
                public void startWait() throws Exception {
                    if (!connectSystemDevice(address))
                        connectOperateStatus = false;
                }
            });

            if (WaitEvent.SUCCESS != connectEvent.waitSignal(WaitEvent.RECV_TIME_OUT_MIDDLE)) {
                return false;
            }
            if (BluetoothLeManage2.getSingleInstance().mBluetoothGatt == null) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 连接底层设备
     * @param address
     * @return
     */
    public boolean connectSystemDevice(final String address) {
        try {
            if (BluetoothLeManage2.getSingleInstance().mBluetoothAdapter == null || address == null) {
                return false;
            }

            bluetoothDevice = BluetoothLeManage2.getSingleInstance().mBluetoothAdapter.getRemoteDevice(address);

            if (bluetoothDevice == null) {
                return false;
            }

            BluetoothLeManage2.getSingleInstance().mBluetoothGatt = bluetoothDevice.connectGatt(service, false, BluetoothLeManage2.getSingleInstance().bluetoothLeGattCallback);
            if (BluetoothLeManage2.getSingleInstance().mBluetoothGatt == null)
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 发起连接重载，传入蓝牙设备对象
     * @param device
     * @return
     */
    public boolean connectDevice(final BluetoothDevice device) {
        try {
            connectOperateStatus = true;
            if (device == null) {
                return false;
            }
            connectEvent.init(new WaitEvent.WaitListener() {

                @Override
                public void startWait() throws Exception {
                    BluetoothLeManage2.getSingleInstance().mBluetoothGatt = device.connectGatt(service, false, BluetoothLeManage2.getSingleInstance().bluetoothLeGattCallback);
                    if(BluetoothLeManage2.getSingleInstance().mBluetoothGatt == null){
                        connectOperateStatus = false;
                    }
                }
            });

            if(WaitEvent.SUCCESS != connectEvent.waitSignal(WaitEvent.RECV_TIME_OUT_MIDDLE)){
                return false;
            }

            if (BluetoothLeManage2.getSingleInstance().mBluetoothGatt == null) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 找服务
     * @return
     */
    public WaitEvent findServiceEvent = new WaitEvent();//找服务阻塞控制
    public boolean findServiceOperateStatus = true;//找服务操作状态
    public boolean findService() {
        try {
            findServiceOperateStatus = true;
            findServiceEvent.init(new WaitEvent.WaitListener() {

                @Override
                public void startWait() throws Exception {
                    if (!BluetoothLeManage2.getSingleInstance().mBluetoothGatt.discoverServices()) {
                        findServiceOperateStatus = false;
                    }
                }
            });

            if (WaitEvent.SUCCESS != findServiceEvent.waitSignal(WaitEvent.RECV_TIME_OUT_MIDDLE)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打开通知
     * @return
     */
    public WaitEvent notificationEvent = new WaitEvent();//打开通知阻塞控制
    public boolean notificationOperateStatus = true;//打开通知操作状态
    public boolean openNotification() {
        try {
            if (BluetoothLeManage2.getSingleInstance().mBluetoothGatt == null) {
                BLELogUtil.d(tag, "openNotification，mBluetoothGatt == null");
                return false;
            }
            if (!request.getReceiveDataFromBLEDevice()) {
                return true;
            }
            BluetoothGattService bluetoothNotifyService = BluetoothLeManage2.getSingleInstance().mBluetoothGatt.getService(UUID.fromString(request.getService_uuid_notification()));
            if (bluetoothNotifyService == null) {
                BLELogUtil.d(tag, "openNotification，bluetoothNotifyService == null");
                return false;
            }
            final BluetoothGattCharacteristic bluetoothNotifyCharacteristic = bluetoothNotifyService.getCharacteristic(UUID.fromString(request.getCharacteristics_uuid_notification()));
            if (bluetoothNotifyCharacteristic == null) {
                BLELogUtil.d(tag, "openNotification，bluetoothNotifyCharacteristic == null");
                return false;
            }
            notificationOperateStatus = true;
            notificationEvent.init(new WaitEvent.WaitListener() {

                @Override
                public void startWait() throws Exception {
                    if (!setCharacteristicNotification(bluetoothNotifyCharacteristic, true)) {
                        BLELogUtil.d(tag, "openNotification，setCharacteristicNotification(bluetoothNotifyCharacteristic, true) == false 1");
                        notificationOperateStatus = false;
                    }
                }
            });

            if (WaitEvent.SUCCESS != notificationEvent.waitSignal(WaitEvent.RECV_TIME_OUT_MIDDLE)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 设置蓝牙返回数据提醒通知
     * @param characteristic
     * @param enable
     * @return
     */
    public boolean setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enable) {
        try {
            if (BluetoothLeManage2.getSingleInstance().mBluetoothGatt == null) {
                return false;
            }
            BluetoothGattDescriptor localBluetoothGattDescriptor;
            UUID localUUID = UUID.fromString(request.getCharacteristics_descriptor_uuid_notification());
            localBluetoothGattDescriptor = characteristic.getDescriptor(localUUID);
            if (localBluetoothGattDescriptor == null) {
                return false;
            }
            if (enable) {
                byte[] arrayOfByte = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                localBluetoothGattDescriptor.setValue(arrayOfByte);
            } else {
                byte[] arrayOfByte = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                localBluetoothGattDescriptor.setValue(arrayOfByte);
            }
            if (!BluetoothLeManage2.getSingleInstance().mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
                return false;
            }
            if (!BluetoothLeManage2.getSingleInstance().mBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 写数据
     * @return
     */
    public WaitEvent sendEvent = new WaitEvent();//写数据阻塞
    public boolean sendOperateStatus = true;//写数据操作状态
    public boolean writeValue() {
        try {
            BLELogUtil.d(tag, "要发送的总数据：" + BLEByteUtil.bytesToHexString(request.getData()));
            BluetoothGattService bluetoothGattService = BluetoothLeManage2.getSingleInstance().mBluetoothGatt.getService(UUID.fromString(request.getService_uuid_write()));
            if (bluetoothGattService == null) {
                return false;
            }
            BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(request.getCharacteristics_uuid_write()));
            if (bluetoothGattCharacteristic == null) {
                return false;
            }
            if (!divideSendBLEData(bluetoothGattCharacteristic, request.getData())) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 拆分写数据
     * @param bluetoothGattCharacteristic
     * @param value
     * @return
     */
    private final static long INTERVAL_SEND_NEXT_PACKAGE = 70;//发送多个数据包时的时间间隔
    private final int MAX_BYTES = 20;// 蓝牙发送数据分包，每个包的最大长度为20个字节
    private boolean divideSendBLEData(final BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] value) {
        try {
            int length = value.length;
            int sendLength;
            int position = 0;

            while (length > 0) {
                if (length > MAX_BYTES) {
                    sendLength = MAX_BYTES;
                    if(length < value.length){
                        BLELogUtil.d(tag, "间隔" + INTERVAL_SEND_NEXT_PACKAGE + "ms再发送下一个数据包");
                        Thread.sleep(INTERVAL_SEND_NEXT_PACKAGE);
                    }
                } else if (length > 0) {
                    sendLength = length;
                    if(length < value.length){
                        BLELogUtil.d(tag, "间隔" + INTERVAL_SEND_NEXT_PACKAGE + "ms再发送最后一个数据包");
                        Thread.sleep(INTERVAL_SEND_NEXT_PACKAGE);
                    }
                } else {
                    return false;
                }
                // 发送数据
                byte[] sendValue = BLEByteUtil.getSubbytes(value, position, sendLength);
                if (sendValue == null) {
                    BLELogUtil.d("---dddd---", "sendValue=null");
                    return false;
                }
                if (!bluetoothGattCharacteristic.setValue(sendValue)) {
                    return false;
                }
                BLELogUtil.d(tag, "position=" + position + ",sendValue=" + BLEByteUtil.bytesToHexString(sendValue));
                sendOperateStatus = true;
                sendEvent.init(new WaitEvent.WaitListener() {

                    @Override
                    public void startWait() throws Exception {
                        BLELogUtil.d(tag, "start writeCharacteristic");
                        if(!BluetoothLeManage2.getSingleInstance().mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic)){
                            BLELogUtil.d(tag, "mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic)=false");
                            sendOperateStatus = false;
                        }
                        BLELogUtil.d(tag, "writeCharacteristic request send success");
                    }
                });
                if(WaitEvent.SUCCESS != sendEvent.waitSignal(WaitEvent.RECV_TIME_OUT_MIDDLE)){
                    return false;
                }
                length -= sendLength;
                position += sendLength;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
