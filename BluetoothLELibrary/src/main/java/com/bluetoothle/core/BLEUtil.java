package com.bluetoothle.core;

import android.bluetooth.BluetoothGatt;

import com.bluetoothle.util.BLEStringUtil;

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
     * 获取一个连接
     * @param targetMac 目标设备mac地址
     * @param connectedBluetoothGattList    连接缓冲列表
     * @return  一个连接
     */
    public static BluetoothGatt getBluetoothGatt(String targetMac, List<BluetoothGatt> connectedBluetoothGattList){
        for(BluetoothGatt gatt : connectedBluetoothGattList){
            if(gatt.getDevice().getAddress().equalsIgnoreCase(targetMac)){
                return gatt;
            }
        }
        return null;
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
