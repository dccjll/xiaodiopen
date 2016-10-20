package com.bluetoothle.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.bluetoothle.core.connect.OnBLEConnectListener;
import com.bluetoothle.core.findService.OnBLEFindServiceListener;
import com.bluetoothle.core.openNotification.OnBLEOpenNotificationListener;
import com.bluetoothle.core.scan.OnBLEScanListener;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.util.BLELogUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by dessmann on 16/10/20.
 * 蓝牙核心响应管理器,统一处理底层蓝牙响应消息,并转发到上层
 * 1.对于底层蓝牙断开的消息,判断蓝牙任务是否执行完毕,执行完毕,则直接显示断开日志,不进行后续处理;没有执行完毕,则通知上层任务执行失败
 * 2.根据不同需求,对于蓝牙任务执行完毕需要立即关闭当前连接的,直接关闭
 */
public class BLECoreResponse {

    private final static String TAG = BLECoreResponse.class.getSimpleName();
    private Boolean running = false;//当前蓝牙任务是否执行完毕
    private Boolean disconnectOnFinish = false;//断开连接后是否关闭蓝牙
    private String mac;//当前连接的mac地址

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public void setDisconnectOnFinish(Boolean disconnectOnFinish) {
        this.disconnectOnFinish = disconnectOnFinish;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * 找到设备
     * @param onBLEScanListener 找设备监听器
     * @param bluetoothDevice   设备对象
     * @param rssi  设备信号
     * @param scanRecord    设备数据
     */
    public void onFoundDevice(OnBLEScanListener onBLEScanListener, BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord){
        onBLEScanListener.onFoundDevice(bluetoothDevice, rssi, scanRecord);
    }

    /**
     * 扫描完成
     * @param onBLEScanListener 找设备监听器
     * @param bluetoothDeviceList   找到的设备列表
     */
    public void onScanFinish(OnBLEScanListener onBLEScanListener, List<Map<String, Object>> bluetoothDeviceList){
        onBLEScanListener.onScanFinish(bluetoothDeviceList);
        setTaskFinishCheck();
    }

    /**
     * 连接成功
     * @param onBLEConnectListener 连接监听器
     * @param bluetoothGatt 连接上的设备服务器
     * @param status    连接状态
     * @param newState  连接属性状态
     */
    public void onConnectSuccess(OnBLEConnectListener onBLEConnectListener, BluetoothGatt bluetoothGatt, int status, int newState){
        onBLEConnectListener.onConnectSuccess(bluetoothGatt, status, newState);
        setTaskFinishCheck();
    }

    /**
     * 找服务成功
     * @param onBLEFindServiceListener 找服务监听器
     * @param bluetoothGatt 连接上的设备服务器
     * @param status    连接状态
     * @param bluetoothGattServices 找到的服务列表
     */
    public void onFindServiceSuccess(OnBLEFindServiceListener onBLEFindServiceListener, BluetoothGatt bluetoothGatt, int status, List<BluetoothGattService> bluetoothGattServices){
        onBLEFindServiceListener.onFindServiceSuccess(bluetoothGatt, status, bluetoothGattServices);
        setTaskFinishCheck();
    }

    /**
     * 打开通知成功
     * @param onBLEOpenNotificationListener 打开通知监听器
     * @param gatt  连接上的设备服务器
     * @param descriptor    通知的描述符
     * @param status    打开通知状态
     */
    public void onOpenNotificationSuccess(OnBLEOpenNotificationListener onBLEOpenNotificationListener, BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
        onBLEOpenNotificationListener.onOpenNotificationSuccess(gatt, descriptor, status);
        setTaskFinishCheck();
    }

    /**
     * 写数据完成
     * @param onBLEWriteDataListener    写数据监听器
     */
    public void onWriteDataFinish(OnBLEWriteDataListener onBLEWriteDataListener){
        onBLEWriteDataListener.onWriteDataFinish();
        setTaskFinishCheck();
    }

    /**
     * 单次写数据成功
     * @param onBLEWriteDataListener 写数据监听器
     * @param gatt  连接上的设备服务器
     * @param characteristic    写数据的特征对象
     * @param status    写数据的状态
     */
    public void onWriteDataSuccess(OnBLEWriteDataListener onBLEWriteDataListener, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
    }

    /**
     * 处理蓝牙断开与协议栈错误
     * @param objectListener    任务监听器
     * @param errorCode 蓝牙协议栈错误代码
     */
    public void onBLEDisconnectErrorCode(Object objectListener, Integer errorCode){
        if(running){
            if(objectListener instanceof OnBLEScanListener){
                ((OnBLEScanListener)objectListener).onScanFail(errorCode);
            }else if(objectListener instanceof OnBLEConnectListener){
                ((OnBLEConnectListener)objectListener).onConnectFail(errorCode);
            }else if(objectListener instanceof OnBLEFindServiceListener){
                ((OnBLEFindServiceListener)objectListener).onFindServiceFail(errorCode);
            }else if(objectListener instanceof OnBLEOpenNotificationListener){
                ((OnBLEOpenNotificationListener)objectListener).onOpenNotificationFail(errorCode);
            }else if(objectListener instanceof OnBLEWriteDataListener){
                ((OnBLEWriteDataListener)objectListener).onWriteDataFail(errorCode);
            }
            return;
        }
        String msg = null;
        if(errorCode == BLEConstants.Error.DisconnectError){
            msg = "蓝牙已断开";
        }else if(errorCode == BLEConstants.Error.ReceivedBLEStackCodeError){
            msg = "收到蓝牙协议栈错误代码";
        }
        BLELogUtil.e(TAG, "onBLEDisconnectErrorCode," + msg);
    }

    /**
     * 任务失败转发
     * @param objectListener    任务监听器
     * @param errorCode 任务失败代码
     */
    public void onResponseError(Object objectListener, Integer errorCode){
        if(errorCode == BLEConstants.Error.DisconnectError || errorCode == BLEConstants.Error.ReceivedBLEStackCodeError){
            onBLEDisconnectErrorCode(objectListener, errorCode);
            return;
        }
        if(objectListener instanceof OnBLEScanListener){
            ((OnBLEScanListener)objectListener).onScanFail(errorCode);
        }else if(objectListener instanceof OnBLEConnectListener){
            ((OnBLEConnectListener)objectListener).onConnectFail(errorCode);
        }else if(objectListener instanceof OnBLEFindServiceListener){
            ((OnBLEFindServiceListener)objectListener).onFindServiceFail(errorCode);
        }else if(objectListener instanceof OnBLEOpenNotificationListener){
            ((OnBLEOpenNotificationListener)objectListener).onOpenNotificationFail(errorCode);
        }else if(objectListener instanceof OnBLEWriteDataListener){
            ((OnBLEWriteDataListener)objectListener).onWriteDataFail(errorCode);
        }
    }

    /**
     * 配置任务完成检验,任务完成后,如果需要断开连接,则发起断开请求
     */
    private void setTaskFinishCheck(){
        running = false;
        if(disconnectOnFinish){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BLEUtil.disconnectBluetoothGatt(mac);
        }
    }
}
