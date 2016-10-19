package com.bluetoothle.factory.doorguard;

import java.util.UUID;

public class DoorGuardProtocol {
	public final static String SERVICE_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID = "0000fff9-0000-1000-8000-00805f9b34fb";

	public static byte[] buildData(){
		return "Open the door".getBytes();
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
