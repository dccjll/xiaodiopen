package com.bluetoothle.factory.xiaodilock.received;

import com.bluetoothle.factory.xiaodilock.util.XIAODIConstants;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

import java.util.Arrays;

/**
 * Created by dessmann on 16/10/18.
 * 小嘀数据接收分析器
 */

public class XIAODIDataReceivedAnalyzer {
    private final static String TAG = XIAODIDataReceivedAnalyzer.class.getSimpleName();
    private final static int BLELOCKDEVICERETURNDATAMINLENGTH = 8;//蓝牙锁设备返回的数据的最小长度
    private byte[] bleDataReceived = null;//接收到蓝牙锁设备返回的数据
    
    private byte[] packageHead;
    private byte[] packageAttribute;
    private byte[] cmd;
    private byte[] dataAreaLength;
    private byte[] ack;
    private byte[] dataArea;
    private byte[] crc;

    public byte[] getBleDataReceived() {
        return bleDataReceived;
    }

    public void setBleDataReceived(byte[] bleDataReceived) {
        this.bleDataReceived = bleDataReceived;
    }

    public byte[] getPackageHead() {
        return packageHead;
    }

    public void setPackageHead(byte[] packageHead) {
        this.packageHead = packageHead;
    }

    public byte[] getPackageAttribute() {
        return packageAttribute;
    }

    public void setPackageAttribute(byte[] packageAttribute) {
        this.packageAttribute = packageAttribute;
    }

    public byte[] getCmd() {
        return cmd;
    }

    public void setCmd(byte[] cmd) {
        this.cmd = cmd;
    }

    public byte[] getDataAreaLength() {
        return dataAreaLength;
    }

    public void setDataAreaLength(byte[] dataAreaLength) {
        this.dataAreaLength = dataAreaLength;
    }

    public byte[] getAck() {
        return ack;
    }

    public void setAck(byte[] ack) {
        this.ack = ack;
    }

    public byte[] getDataArea() {
        return dataArea;
    }

    public void setDataArea(byte[] dataArea) {
        this.dataArea = dataArea;
    }

    public byte[] getCrc() {
        return crc;
    }

    public void setCrc(byte[] crc) {
        this.crc = crc;
    }

    /**
     * 小嘀数据接收分析器构造器
     * @param bleDataReceived   接收到的总数据
     */
    public XIAODIDataReceivedAnalyzer(byte[] bleDataReceived) {
        this.bleDataReceived = bleDataReceived;
    }

    /**
     * 解析蓝牙锁设备返回的数据
     * @return  解析成功失败标志 true 成功 false 失败
     */
    public Integer analysisBLEReturnData() {
        if (bleDataReceived.length < BLELOCKDEVICERETURNDATAMINLENGTH) {
            return XIAODIConstants.Error.CheckWholeDataLengthError;
        }
        try {
            packageHead = BLEByteUtil.getSubbytes(bleDataReceived, 0, 1);
            BLELogUtil.d(TAG, "packageHead=" + BLEByteUtil.bytesToHexString(packageHead));
            packageAttribute = BLEByteUtil.getSubbytes(bleDataReceived, 1, 1);
            BLELogUtil.d(TAG, "packageAttribute=" + BLEByteUtil.bytesToHexString(packageAttribute));
            cmd = BLEByteUtil.getSubbytes(bleDataReceived, 2, 1);
            BLELogUtil.d(TAG, "cmd=" + BLEByteUtil.bytesToHexString(cmd));
            dataAreaLength = BLEByteUtil.getSubbytes(bleDataReceived, 3, 2);
            BLELogUtil.d(TAG, "dataAreaLength=" + BLEByteUtil.bytesToHexString(dataAreaLength));
            ack = BLEByteUtil.getSubbytes(bleDataReceived, 5, 1);
            BLELogUtil.d(TAG, "ack=" + BLEByteUtil.bytesToHexString(ack));
            dataArea = BLEByteUtil.getSubbytes(bleDataReceived, 6, bleDataReceived.length - packageHead.length - packageAttribute.length - cmd.length - dataAreaLength.length - ack.length - crc.length);
            BLELogUtil.d(TAG, "dataArea=" + BLEByteUtil.bytesToHexString(dataArea));
            crc = BLEByteUtil.getSubbytes(bleDataReceived, bleDataReceived.length - 2, 2);
            BLELogUtil.d(TAG, "crc=" + BLEByteUtil.bytesToHexString(crc));
        } catch (Exception e) {
            e.printStackTrace();
            return XIAODIConstants.Error.CheckSubBytesError;
        }
        return XIAODIConstants.Error.CorretCode;
    }

    @Override
    public String toString() {
        return "XIAODIDataReceivedAnalyzer{" +
                "bleDataReceived=" + BLEByteUtil.bytesToHexString(bleDataReceived) +
                ", packageHead=" + BLEByteUtil.bytesToHexString(packageHead) +
                ", packageAttribute=" + BLEByteUtil.bytesToHexString(packageAttribute) +
                ", cmd=" + BLEByteUtil.bytesToHexString(cmd) +
                ", dataAreaLength=" + BLEByteUtil.bytesToHexString(dataAreaLength) +
                ", ack=" + BLEByteUtil.bytesToHexString(ack) +
                ", dataArea=" + BLEByteUtil.bytesToHexString(dataArea) +
                ", crc=" + BLEByteUtil.bytesToHexString(crc) +
                '}';
    }
}
