package com.bluetoothle.factory.xiaodilock;


public class XIAODIDataReceived {
	private byte[] packageHead;
	private byte[] packageAttribute;
	private byte[] cmd;
	private byte[] dataAreaLength;
	private byte[] ack;
	private byte[] dataArea;
	private byte[] crc;
	public byte[] getPackageHead() {
		return packageHead;
	}
	public XIAODIDataReceived setPackageHead(byte[] packageHead) {
		this.packageHead = packageHead;
		return this;
	}
	public byte[] getPackageAttribute() {
		return packageAttribute;
	}
	public XIAODIDataReceived setPackageAttribute(byte[] packageAttribute) {
		this.packageAttribute = packageAttribute;
		return this;
	}
	public byte[] getCmd() {
		return cmd;
	}
	public XIAODIDataReceived setCmd(byte[] cmd) {
		this.cmd = cmd;
		return this;
	}
	public byte[] getDataAreaLength() {
		return dataAreaLength;
	}
	public XIAODIDataReceived setDataAreaLength(byte[] dataAreaLength) {
		this.dataAreaLength = dataAreaLength;
		return this;
	}
	public byte[] getAck() {
		return ack;
	}
	public XIAODIDataReceived setAck(byte[] ack) {
		this.ack = ack;
		return this;
	}
	public byte[] getDataArea() {
		return dataArea;
	}
	public XIAODIDataReceived setDataArea(byte[] dataArea) {
		this.dataArea = dataArea;
		return this;
	}
	public byte[] getCrc() {
		return crc;
	}
	public XIAODIDataReceived setCrc(byte[] crc) {
		this.crc = crc;
		return this;
	}
	
	public byte[] getAllReceivedData(){
		byte[] dataReceived;
		if(dataArea != null && dataArea.length > 0){
			dataReceived = new byte[packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length + ack.length + dataArea.length + crc.length];
		}else{
			dataReceived = new byte[packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length + ack.length + crc.length];
		}
		System.arraycopy(packageHead, 0, dataReceived, 0, packageHead.length);
		System.arraycopy(packageAttribute, 0, dataReceived, packageHead.length, packageAttribute.length);
		System.arraycopy(cmd, 0, dataReceived, packageHead.length + packageAttribute.length, cmd.length);
		System.arraycopy(dataAreaLength, 0, dataReceived, packageHead.length + packageAttribute.length + cmd.length, dataAreaLength.length);
		System.arraycopy(ack, 0, dataReceived, packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length, ack.length);
		if(dataArea != null && dataArea.length > 0){
			System.arraycopy(dataArea, 0, dataReceived, packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length + ack.length, dataArea.length);
			System.arraycopy(crc, 0, dataReceived, packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length + ack.length + dataArea.length, crc.length);
		}else{
			System.arraycopy(crc, 0, dataReceived, packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length + ack.length, crc.length);
		}
		return dataReceived;
	}
}
