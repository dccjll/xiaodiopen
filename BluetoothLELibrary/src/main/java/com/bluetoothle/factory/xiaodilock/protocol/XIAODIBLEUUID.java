package com.bluetoothle.factory.xiaodilock.protocol;

import java.util.UUID;

/**
 * Created by dessmann on 16/10/18.
 * 小嘀蓝牙通讯UUID
 */

public class XIAODIBLEUUID {

    public final static String SERVICE_UUID = "0000ffe5-0000-1000-8000-00805f9b34fb";
    public final static String CHARACTERISTIC_UUID = "0000ffe9-0000-1000-8000-00805f9b34fb";

    public final static String NOTIFICATION_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public final static String NOTIFICATION_CHARACTERISTIC_UUID = "0000ffe4-0000-1000-8000-00805f9b34fb";
    public final static String BLUETOOTHGATTDESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    /**
     * 构建接收数据的uuid数组
     * @return
     */
    public static UUID[] buildFiveUUIDs(){
        UUID[] uuids = new UUID[5];
        uuids[0] = UUID.fromString(SERVICE_UUID);
        uuids[1] = UUID.fromString(CHARACTERISTIC_UUID);
        uuids[2] = UUID.fromString(NOTIFICATION_SERVICE_UUID);
        uuids[3] = UUID.fromString(NOTIFICATION_CHARACTERISTIC_UUID);
        uuids[4] = UUID.fromString(BLUETOOTHGATTDESCRIPTOR_UUID);
        return uuids;
    }

    /**
     * 构建不接收数据的uuid数组
     * @return
     */
    public static UUID[] buildTwoUUIDs(){
        UUID[] uuids = new UUID[2];
        uuids[0] = UUID.fromString(SERVICE_UUID);
        uuids[1] = UUID.fromString(CHARACTERISTIC_UUID);
        return uuids;
    }
}
