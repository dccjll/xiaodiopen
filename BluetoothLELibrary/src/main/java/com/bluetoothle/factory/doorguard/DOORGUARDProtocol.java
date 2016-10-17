package com.bluetoothle.factory.doorguard;

public class DoorGuardProtocol {
	public final static String SERVICE_UUID = "0000fff5-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_UUID = "0000fff9-0000-1000-8000-00805f9b34fb";

	public static byte[] buildData(){
		return "Open the door".getBytes();
	}
}
