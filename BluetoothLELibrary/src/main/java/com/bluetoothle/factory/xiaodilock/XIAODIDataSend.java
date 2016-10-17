package com.bluetoothle.factory.xiaodilock;


import com.dsm.dsmodule.util.ByteUtil;

public class XIAODIDataSend {
	private byte[] packageHead;
	private byte[] packageAttribute;
	private byte[] cmd;
	private byte[] dataAreaLength;
	private byte[] dataArea;
	private byte[] crc;
	public byte[] getPackageHead() {
		return packageHead;
	}
	public XIAODIDataSend setPackageHead(byte[] packageHead) {
		this.packageHead = packageHead;
		return this;
	}
	public byte[] getPackageAttribute() {
		return packageAttribute;
	}
	public XIAODIDataSend setPackageAttribute(byte[] packageAttribute) {
		this.packageAttribute = packageAttribute;
		return this;
	}
	public byte[] getCmd() {
		return cmd;
	}
	public XIAODIDataSend setCmd(byte[] cmd) {
		this.cmd = cmd;
		return this;
	}
	public byte[] getDataAreaLength() {
		return dataAreaLength;
	}
	public XIAODIDataSend setDataAreaLength(byte[] dataAreaLength) {
		this.dataAreaLength = dataAreaLength;
		return this;
	}
	public byte[] getDataArea() {
		return dataArea;
	}
	public XIAODIDataSend setDataArea(byte[] dataArea) {
		this.dataArea = dataArea;
		return this;
	}
	public byte[] getCrc() {
		return crc;
	}
	public XIAODIDataSend setCrc(byte[] crc) {
		this.crc = crc;
		return this;
	}

	@Override
	public String toString() {
		return "XIAODIDataSend{" +
				"packageHead=" + ByteUtil.bytesToHexString(packageHead) +
				", packageAttribute=" +ByteUtil.bytesToHexString(packageAttribute) +
				", cmd=" + ByteUtil.bytesToHexString(cmd) +
				", dataAreaLength=" + ByteUtil.bytesToHexString(dataAreaLength) +
				", dataArea=" + ByteUtil.bytesToHexString(dataArea) +
				", crc=" + ByteUtil.bytesToHexString(crc) +
				'}';
	}
}
