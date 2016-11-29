package com.bluetoothle.core;

import android.bluetooth.BluetoothGatt;

import com.bluetoothle.util.BLEStringUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dessmann on 16/10/18.
 * 蓝牙简单工具类
 */

public class BLEUtil {

    /**
     * 验证当前mac地址的设备是否已连接
     */
    public synchronized static Boolean checkConnectStatus(List<Map<String, Object>> gattList, String targetMac){
        for (Map<String,Object> map: gattList) {
            if(((BluetoothGatt)map.get("bluetoothGatt")).getDevice().getAddress().equalsIgnoreCase(targetMac)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取一个连接
     */
    public synchronized static BluetoothGatt getBluetoothGatt(List<Map<String, Object>> gattList, String targetMac){
        for (Map<String,Object> map: gattList) {
            if(((BluetoothGatt)map.get("bluetoothGatt")).getDevice().getAddress().equalsIgnoreCase(targetMac)){
                return (BluetoothGatt)map.get("bluetoothGatt");
            }
        }
        return null;
    }

    /**
     * 获取一个连接对象
     */
    public synchronized static Map<String, Object> getBluetoothGattMap(List<Map<String, Object>> gattList, BluetoothGatt bluetoothGatt){
        for (Map<String,Object> map: gattList) {
            if(map.get("bluetoothGatt") == bluetoothGatt){
                return map;
            }
        }
        return null;
    }

    /**
     * 移除一个断开的连接
     */
    public synchronized static void removeConnect(List<Map<String, Object>> gattList, String targetMac){
        Iterator<Map<String, Object>> bluetoothGattListIte = gattList.iterator();
        while(bluetoothGattListIte.hasNext()){
            Map<String, Object> bluetoothLongMap = bluetoothGattListIte.next();
            if(((BluetoothGatt)bluetoothLongMap.get("bluetoothGatt")).getDevice().getAddress().equalsIgnoreCase(targetMac)){
                bluetoothGattListIte.remove();
            }
        }
    }

    /**
     * 断开一个蓝牙连接
     */
    public synchronized static void disconnectBluetoothGatt(List<Map<String, Object>> gattList, String targetMac){
        for (Map<String,Object> map: gattList) {
            if(((BluetoothGatt)map.get("bluetoothGatt")).getDevice().getAddress().equalsIgnoreCase(targetMac)){
                removeConnect(gattList, targetMac);
                ((BluetoothGatt) map.get("bluetoothGatt")).disconnect();
                ((BluetoothGatt) map.get("bluetoothGatt")).close();
            }
        }
    }

    /**
     * 断开所有连接
     */
    public synchronized static void disconnectAllBluetoothGatt(List<Map<String, Object>> gattList){
        for (Map<String,Object> map: gattList) {
            ((BluetoothGatt) map.get("bluetoothGatt")).disconnect();
        }
        gattList.clear();
    }

    /**
     * 更新某一个连接对象上一次通讯的时间戳
     * @param bluetoothGatt 某一个连接对象
     * @param lastCommunicationTime 最后一次通讯的时间戳
     */
    public synchronized static void updateBluetoothGattLastCommunicationTime(List<Map<String, Object>> gattList, BluetoothGatt bluetoothGatt, Long lastCommunicationTime){
        for (Map<String,Object> map: gattList) {
            if(map.get("bluetoothGatt")  == bluetoothGatt){
                map.put("connectedTime", lastCommunicationTime);
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
