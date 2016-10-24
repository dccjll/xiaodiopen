package com.bluetoothle.core;

import android.bluetooth.BluetoothGatt;

import com.bluetoothle.util.BLEStringUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dessmann on 16/10/18.
 * 蓝牙简单工具类
 */

public class BLEUtil {

    /**
     * 验证当前mac地址的设备是否已连接
     * @param targetMac   目标设备mac地址
     * @return  true 连接存在 false 连接不存在
     */
    public synchronized static Boolean checkConnectStatus(String targetMac){
        Iterator<Map<BluetoothGatt,Long>> bluetoothGattListIte = BLEManage.connectedBluetoothGattList.iterator();
        while(bluetoothGattListIte.hasNext()){
            Map<BluetoothGatt,Long> bluetoothGattLongMap = bluetoothGattListIte.next();
            for(Map.Entry<BluetoothGatt,Long> entry : bluetoothGattLongMap.entrySet()){
                if(entry.getKey().getDevice().getAddress().equalsIgnoreCase(targetMac)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取一个连接
     * @param targetMac 目标设备mac地址
     * @return  一个连接
     */
    public synchronized static BluetoothGatt getBluetoothGatt(String targetMac){
        Iterator<Map<BluetoothGatt,Long>> bluetoothGattListIte = BLEManage.connectedBluetoothGattList.iterator();
        while(bluetoothGattListIte.hasNext()){
            Map<BluetoothGatt,Long> bluetoothGattLongMap = bluetoothGattListIte.next();
            for(Map.Entry<BluetoothGatt,Long> entry : bluetoothGattLongMap.entrySet()){
                if(entry.getKey().getDevice().getAddress().equalsIgnoreCase(targetMac)){
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * 删除一个断开的连接
     * @param targetMac 目标设备mac地址
     */
    public synchronized static void removeConnect(String targetMac){
        Iterator<Map<BluetoothGatt,Long>> bluetoothGattListIte = BLEManage.connectedBluetoothGattList.iterator();
        while(bluetoothGattListIte.hasNext()){
            Map<BluetoothGatt,Long> bluetoothGattLongMap = bluetoothGattListIte.next();
            for(Map.Entry<BluetoothGatt,Long> entry : bluetoothGattLongMap.entrySet()){
                if(entry.getKey().getDevice().getAddress().equalsIgnoreCase(targetMac)){
                    bluetoothGattListIte.remove();
                }
            }
        }
    }

    /**
     * 断开一个蓝牙连接
     * @param targetMac 目标设备mac地址
     */
    public synchronized static void disconnectBluetoothGatt(String targetMac){
        Iterator<Map<BluetoothGatt,Long>> bluetoothGattListIte = BLEManage.connectedBluetoothGattList.iterator();
        while(bluetoothGattListIte.hasNext()){
            Map<BluetoothGatt,Long> bluetoothGattLongMap = bluetoothGattListIte.next();
            for(Map.Entry<BluetoothGatt,Long> entry : bluetoothGattLongMap.entrySet()){
                if(entry.getKey().getDevice().getAddress().equalsIgnoreCase(targetMac)){
                    removeConnect(targetMac);
                    entry.getKey().disconnect();
                    break;
                }
            }
        }
    }

    /**
     * 断开所有连接
     */
    public synchronized static void disconnectAllBluetoothGatt(){
        Iterator<Map<BluetoothGatt,Long>> bluetoothGattListIte = BLEManage.connectedBluetoothGattList.iterator();
        while(bluetoothGattListIte.hasNext()){
            Map<BluetoothGatt,Long> bluetoothGattLongMap = bluetoothGattListIte.next();
            for(Map.Entry<BluetoothGatt,Long> entry : bluetoothGattLongMap.entrySet()){
                removeConnect(entry.getKey().getDevice().getAddress());
                entry.getKey().disconnect();
            }
        }
    }

    /**
     * 更新某一个连接对象上一次通讯的时间戳
     * @param bluetoothGatt 某一个连接对象
     * @param lastCommunicationTime 最后一次通讯的时间戳
     */
    public synchronized static void updateBluetoothGattLastCommunicationTime(BluetoothGatt bluetoothGatt, Long lastCommunicationTime){
        Iterator<Map<BluetoothGatt,Long>> bluetoothGattListIte = BLEManage.connectedBluetoothGattList.iterator();
        while(bluetoothGattListIte.hasNext()){
            Map<BluetoothGatt,Long> bluetoothGattLongMap = bluetoothGattListIte.next();
            for(Map.Entry<BluetoothGatt,Long> entry : bluetoothGattLongMap.entrySet()){
                if(entry.getKey() == bluetoothGatt){
                    entry.setValue(lastCommunicationTime);
                    break;
                }
            }
        }
    }

    /**
     * 验证设备mac地址
     * @param address
     * @return
     */
    public static boolean checkAddress(String address){
        if(BLEStringUtil.isEmpty(address)){
            return false;
        }
        if(address.split(":").length != 6){
            return false;
        }
        char[] macChars = address.replace(":", "").toCharArray();
        String regexChars = "0123456789ABCDEF";
        for(char c : macChars){
            if(!regexChars.contains(c + "")){
                return false;
            }
        }
        return true;
    }

    /**
     * 验证设备mac地址列表
     * @param targetAddressList 目标设备mac地址列表
     * @return
     */
    public static boolean checkTargetAddressList(List<String> targetAddressList){
        if(targetAddressList == null || targetAddressList.size() == 0){
            return false;
        }
        for(String mac : targetAddressList){
            if(!checkAddress(mac)){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        System.out.println(checkAddress("DC:B1:1F:80:69:05"));
    }
}
