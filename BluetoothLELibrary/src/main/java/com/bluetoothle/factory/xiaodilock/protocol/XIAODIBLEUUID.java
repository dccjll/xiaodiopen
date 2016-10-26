package com.bluetoothle.factory.xiaodilock.protocol;

import java.util.UUID;

/**
 * Created by dessmann on 16/10/18.
 * 小嘀蓝牙通讯UUID
 */

public class XIAODIBLEUUID {

    public final static String WRITE_SERVICE_UUID = "0000ffe5-0000-1000-8000-00805f9b34fb";
    public final static String WRITE_CHARACTERISTIC_UUID = "0000ffe9-0000-1000-8000-00805f9b34fb";
    public final static String WRITE_SMARTKEY_DSM_CHARACTERISTIC_UUID = "0000ffea-0000-1000-8000-00805f9b34fb";

    public final static String NOTIFICATION_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public final static String NOTIFICATION_CHARACTERISTIC_UUID = "0000ffe4-0000-1000-8000-00805f9b34fb";
    public final static String DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    /**
     * 构建智能钥匙接收数据的uuid数组
     * @return
     */
    public static UUID[] buildSmartKeyUUIDs(){
        UUID[] uuids = new UUID[5];
        uuids[0] = UUID.fromString(WRITE_SERVICE_UUID);
        uuids[1] = UUID.fromString(WRITE_SMARTKEY_DSM_CHARACTERISTIC_UUID);
        uuids[2] = UUID.fromString(NOTIFICATION_SERVICE_UUID);
        uuids[3] = UUID.fromString(NOTIFICATION_CHARACTERISTIC_UUID);
        uuids[4] = UUID.fromString(DESCRIPTOR_UUID);
        return uuids;
    }

    /**
     * 构建接收数据的uuid数组
     * @return
     */
    public static UUID[] buildFiveUUIDs(){
        UUID[] uuids = new UUID[5];
        uuids[0] = UUID.fromString(WRITE_SERVICE_UUID);
        uuids[1] = UUID.fromString(WRITE_CHARACTERISTIC_UUID);
        uuids[2] = UUID.fromString(NOTIFICATION_SERVICE_UUID);
        uuids[3] = UUID.fromString(NOTIFICATION_CHARACTERISTIC_UUID);
        uuids[4] = UUID.fromString(DESCRIPTOR_UUID);
        return uuids;
    }

    /**
     * 构建不接收数据的uuid数组
     * @return
     */
    public static UUID[] buildTwoUUIDs(){
        UUID[] uuids = new UUID[2];
        uuids[0] = UUID.fromString(WRITE_SERVICE_UUID);
        uuids[1] = UUID.fromString(WRITE_CHARACTERISTIC_UUID);
        return uuids;
    }
}
