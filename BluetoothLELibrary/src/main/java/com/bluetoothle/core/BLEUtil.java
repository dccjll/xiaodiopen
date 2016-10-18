package com.bluetoothle.core;

import android.bluetooth.BluetoothGatt;

import java.util.Iterator;
import java.util.List;

/**
 * Created by dessmann on 16/10/18.
 * 蓝牙简单工具类
 */

public class BLEUtil {

    /**
     * 验证当前mac地址的设备是否已连接
     * @param targetMac   目标设备mac地址
     * @param connectedBluetoothGattList    连接缓冲列表
     * @return  true 连接存在 false 连接不存在
     */
    public static Boolean checkConnectStatus(String targetMac, List<BluetoothGatt> connectedBluetoothGattList){
        for(BluetoothGatt gatt : connectedBluetoothGattList){
            if(gatt.getDevice().getAddress().equalsIgnoreCase(targetMac)){
                return true;
            }
        }
        return false;
    }

    /**
     * 删除一个断开的连接
     * @param targetMac 目标设备mac地址
     * @param connectedBluetoothGattList    连接缓冲列表
     */
    public static void removeConnect(String targetMac, List<BluetoothGatt> connectedBluetoothGattList){
        Iterator<BluetoothGatt> gattIterator = connectedBluetoothGattList.iterator();
        while(gattIterator.hasNext()){
            if(gattIterator.next().getDevice().getAddress().equalsIgnoreCase(targetMac)){
                gattIterator.remove();
                break;
            }
        }
    }

    /**
     * 断开一个蓝牙连接
     * @param targetMac 目标设备mac地址
     * @param connectedBluetoothGattList    连接缓冲列表
     */
    public static void closeBluetoothGatt(String targetMac, List<BluetoothGatt> connectedBluetoothGattList){
        for(BluetoothGatt gatt : connectedBluetoothGattList){
            if(gatt.getDevice().getAddress().equalsIgnoreCase(targetMac)){
                gatt.disconnect();
                gatt.close();
                break;
            }
        }
    }
}
