package com.bluetoothle.core.request;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

import java.io.Serializable;

/**
 * 蓝牙请求封装
 */
public class Request implements Serializable {

	private final static long serialVersionUID = 1L;
	private final static String TAG = Request.class.getSimpleName();

	private String targetDeviceMacAddress;//目标设备mac地址
	private String service_uuid_write;//写数据的服务UUID
	private String characteristics_uuid_write;//写数据的特征UUID
	private String service_uuid_notification;//通知的服务UUID
	private String characteristics_uuid_notification;//通知的特征UUID
	private String characteristics_descriptor_uuid_notification;//通知的特征描述UUID
	private byte[] data;//要发送的数据
	private boolean receiveDataFromBLEDevice = false;//是否关心设备回数据

	public String getTargetDeviceMacAddress() {
		return targetDeviceMacAddress;
	}
	public Request setTargetDeviceMacAddress(String targetDeviceMacAddress) {
		this.targetDeviceMacAddress = targetDeviceMacAddress;
		return this;
	}
	public String getService_uuid_write() {
		return service_uuid_write;
	}
	public Request setService_uuid_write(String service_uuid_write) {
		this.service_uuid_write = service_uuid_write;
		return this;
	}
	public String getCharacteristics_uuid_write() {
		return characteristics_uuid_write;
	}
	public Request setCharacteristics_uuid_write(String characteristics_uuid_write) {
		this.characteristics_uuid_write = characteristics_uuid_write;
		return this;
	}
	public String getService_uuid_notification() {
		return service_uuid_notification;
	}
	public Request setService_uuid_notification(String service_uuid_notification) {
		this.service_uuid_notification = service_uuid_notification;
		return this;
	}
	public String getCharacteristics_uuid_notification() {
		return characteristics_uuid_notification;
	}
	public Request setCharacteristics_uuid_notification(
			String characteristics_uuid_notification) {
		this.characteristics_uuid_notification = characteristics_uuid_notification;
		return this;
	}
	public String getCharacteristics_descriptor_uuid_notification() {
		return characteristics_descriptor_uuid_notification;
	}
	public Request setCharacteristics_descriptor_uuid_notification(
			String characteristics_descriptor_uuid_notification) {
		this.characteristics_descriptor_uuid_notification = characteristics_descriptor_uuid_notification;
		return this;
	}
	public byte[] getData() {
		return data;
	}
	public Request setData(byte[] data) {
		this.data = data;
		return this;
	}
	public boolean getReceiveDataFromBLEDevice() {
		return receiveDataFromBLEDevice;
	}
	public Request setReceiveDataFromBLEDevice(boolean receiveDataFromBLEDevice) {
		this.receiveDataFromBLEDevice = receiveDataFromBLEDevice;
		return this;
	}

	@Override
	public String toString() {
		return "Request{" +
				"targetDeviceMacAddress='" + targetDeviceMacAddress + '\'' +
				", service_uuid_write='" + service_uuid_write + '\'' +
				", characteristics_uuid_write='" + characteristics_uuid_write + '\'' +
				", service_uuid_notification='" + service_uuid_notification + '\'' +
				", characteristics_uuid_notification='" + characteristics_uuid_notification + '\'' +
				", characteristics_descriptor_uuid_notification='" + characteristics_descriptor_uuid_notification + '\'' +
				", data=" + BLEByteUtil.bytesToHexString(data) +
				", receiveDataFromBLEDevice=" + receiveDataFromBLEDevice +
				'}';
	}

	/**
	 * 构建监听设备返回数据的请求
	 * @param targetDeviceMacAddress	设备mac地址
	 * @param service_uuid_write	写数据的服务UUID
	 * @param characteristics_uuid_write	写数据的特征UUID
	 * @param service_uuid_notification		通知的服务UUID
	 * @param characteristics_uuid_notification		通知的特征UUID
	 * @param characteristics_descriptor_uuid_notification	通知的特征描述UUID
	 * @param data	要发送的总数据
     * @param onRequestListener	 构建状态监听器
     */
	public static void buildRequest(String targetDeviceMacAddress, String service_uuid_write, String characteristics_uuid_write, final String service_uuid_notification, final String characteristics_uuid_notification, final String characteristics_descriptor_uuid_notification, byte[] data, final OnRequestListener onRequestListener){
		if(onRequestListener == null){
			BLELogUtil.e(TAG, "请求监听器不能为空");
			return;
		}
		buildNoReturnDataRequest(targetDeviceMacAddress, service_uuid_write, characteristics_uuid_write, data,
				new OnRequestListener() {
					@Override
					public void onRequestSuccss(Request request) {
						if(service_uuid_notification == null){
							onRequestListener.onRequestFail(BLEConstants.RequestError.RequestCode_InvalidService_uuid_notification);
							return;
						}
						if(characteristics_uuid_notification == null){
							onRequestListener.onRequestFail(BLEConstants.RequestError.RequestCode_InvalidCharacteristics_uuid_notification);
							return;
						}
						if(characteristics_descriptor_uuid_notification == null){
							onRequestListener.onRequestFail(BLEConstants.RequestError.RequestCode_InvalidCharacteristics_descriptor_uuid_notification);
							return;
						}
						request.setService_uuid_notification(service_uuid_notification);
						request.setCharacteristics_uuid_notification(characteristics_uuid_notification);
						request.setCharacteristics_descriptor_uuid_notification(characteristics_descriptor_uuid_notification);
						onRequestListener.onRequestSuccss(request);
					}

					@Override
					public void onRequestFail(Integer errorCode) {
						onRequestListener.onRequestFail(errorCode);
					}
				});
	}

	/**
	 * 构建不监听设备返回数据的请求
	 * @param targetDeviceMacAddress	设备mac地址
	 * @param service_uuid_write	写数据的服务UUID
	 * @param characteristics_uuid_write	写数据的特征UUID
	 * @param data	要发送的总数据
     */
	public static void buildNoReturnDataRequest(String targetDeviceMacAddress, String service_uuid_write, String characteristics_uuid_write, byte[] data, final OnRequestListener onRequestListener){
		if(onRequestListener == null){
			BLELogUtil.e(TAG, "请求监听器不能为空");
			return;
		}
		if(targetDeviceMacAddress == null || targetDeviceMacAddress.split(":").length != 6){
			onRequestListener.onRequestFail(BLEConstants.RequestError.RequestCode_InvalidTargetDeviceMacAddress);
			return;
		}
		if(service_uuid_write == null){
			onRequestListener.onRequestFail(BLEConstants.RequestError.RequestCode_InvalidService_uuid_write);
			return;
		}
		if(characteristics_uuid_write == null){
			onRequestListener.onRequestFail(BLEConstants.RequestError.RequestCode_InvalidCharacteristics_uuid_write);
			return;
		}
		if(data == null){
			onRequestListener.onRequestFail(BLEConstants.RequestError.RequestCode_InvalidData);
			return;
		}
		Request request = new Request();
		request.setTargetDeviceMacAddress(targetDeviceMacAddress);
		request.setService_uuid_write(service_uuid_write);
		request.setCharacteristics_uuid_write(characteristics_uuid_write);
		request.setData(data);
		request.setReceiveDataFromBLEDevice(false);
		onRequestListener.onRequestSuccss(request);
	}
}
