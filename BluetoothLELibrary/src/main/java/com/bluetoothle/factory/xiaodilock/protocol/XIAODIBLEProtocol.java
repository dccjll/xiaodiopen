package com.bluetoothle.factory.xiaodilock.protocol;

import com.bluetoothle.BLEApp;
import com.bluetoothle.R;
import com.bluetoothle.factory.xiaodilock.send.XIAODIData;
import com.bluetoothle.factory.xiaodilock.util.XIAODIBLECRCUtil;
import com.bluetoothle.factory.xiaodilock.util.XIAODIBLEUtil;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;
import com.bluetoothle.util.BLEStringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 小嘀蓝牙协议封装
 */
public class XIAODIBLEProtocol {

    private final static String TAG = XIAODIBLEProtocol.class.getSimpleName();

    //当前蓝牙命令类型
    private String bleCmdType = null;

    //数据信息集合
    private XIAODIData xiaodiData = null;

    //包头信息(1字节)
    private byte[] packageHead = new byte[1];
    //包属性信息(1字节)
    private byte[] packageAttribute = new byte[1];
    //指令(1字节)
    private byte[] cmd = new byte[1];
    //数据区长度(2字节)
    private byte[] dataAreaLength = new byte[2];
    //数据区数据(变长)
    private byte[] dataArea = null;
    //校验和(2字节)
    private byte[] crc = new byte[2];
    //封包待发送的数据
    private byte[] bleDataSend = null;

    /**
     * 返回封装成功的待发送数据包
     * @return
     */
    public byte[] getBleDataSend() {
        return bleDataSend;
    }

    /**
     * 数据包封装构造器
     * @param bleCmdType    命令字类型
     * @param xiaodiData    发送数据集合
     */
    public XIAODIBLEProtocol(String bleCmdType, XIAODIData xiaodiData) {
        this.bleCmdType = bleCmdType;
        this.xiaodiData = xiaodiData;
    }

    /**
     * 封装待发送的数据包
     * @return
     */
    public Boolean buildData() {
        //包头信息(1字节)
        packageHead[0] = (byte) 0xFE;
        //包属性信息(1字节)
        packageAttribute[0] = (byte) 0x01;
        //指令(1字节) 数据区数据(变长)
        if (XIAODIBLECMDType.BLE_CMDTYPE_CHECKCHANNALPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x01;
            //数据包为信道密码,占4byte,出厂密码为0x00000000 代表密码为00000000
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x01"));
                return false;
            }
            byte[] channelpwd = getChannelPwdBytes(xiaodiData.getChannelpwd());
            if (channelpwd == null || channelpwd.length != XIAODIBLELengthCheck.BLELOCKCHANNELPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_channel_length_error,"0x01"));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKCHANNELPASSWORDLENGTH];
            System.arraycopy(channelpwd, 0, dataArea, 0, channelpwd.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_UPDATECHANNALPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x02;
            //数据包为信道密码原密码+新密码,占8byte,出厂密码为0x00000000,APP显示为00000000,设置范围从00000000~99999999
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x02"));
                return false;
            }
            byte[] channelpwd = getChannelPwdBytes(xiaodiData.getChannelpwd());
            byte[] newchannelpwd = getChannelPwdBytes(xiaodiData.getNewchannelpwd());
            if (channelpwd == null || channelpwd.length != XIAODIBLELengthCheck.BLELOCKCHANNELPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_channel_length_error,"0x02"));
                return false;
            }
            if (newchannelpwd == null || newchannelpwd.length != XIAODIBLELengthCheck.BLELOCKCHANNELPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_channel_length_error,"0x02"));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKCHANNELPASSWORDLENGTH * 2];
            System.arraycopy(channelpwd, 0, dataArea, 0, channelpwd.length);
            System.arraycopy(newchannelpwd, 0, dataArea, channelpwd.length, newchannelpwd.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_OPENBLELOCK.equals(bleCmdType)) {
            cmd[0] = (byte) 0x03;
            //数据包格式:信道密码(4byte)+手机属性(账号ID 13byte) 长度:17byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x03"));
                return false;
            }
            byte[] channelpwd = getChannelPwdBytes(xiaodiData.getChannelpwd());
            byte[] mobileaccount = getMobileBytes(xiaodiData.getMobileaccount());
            if (channelpwd == null || channelpwd.length != XIAODIBLELengthCheck.BLELOCKCHANNELPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_channel_length_error,"0x03"));
                return false;
            }
            if (mobileaccount == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.mobile_init_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKCHANNELPASSWORDLENGTH + XIAODIBLELengthCheck.BLELOCKMOBILEACCOUNTLENGTH];
            System.arraycopy(channelpwd, 0, dataArea, 0, channelpwd.length);
            System.arraycopy(mobileaccount, 0, dataArea, channelpwd.length, mobileaccount.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_GETBLELOCKINFO.equals(bleCmdType)) {
            cmd[0] = (byte) 0x20;
            //数据包格式:无 长度:0byte
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_SETBLELOCKOPENTYPE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x22;
            //数据包格式:开锁方式	指纹开锁方式代码为0x01	手机开锁方式为0x02		智能钥匙开锁方式为0x04	长度:1byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x22"));
                return false;
            }
            byte[] openlocktype = xiaodiData.getOpenlocktype();
            if (!XIAODIBLEUtil.checkOpenLockType(openlocktype)) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.param_error,"0x22,"));
                return false;
            }
            dataArea = openlocktype;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_SYNCBLELOCKTIME.equals(bleCmdType)) {
            cmd[0] = (byte) 0x23;
            //数据包格式:时间格式(年,月,日,周,小时,分钟,秒)(7byte)
            //时间格式每个时间单位用1byte代表时间,高字节代表十位时间格式,低字节代码个位时间格式。
            //其中年的基准以2000为基准,如果时间格式为0x15010104083002,代表2015年01月01日 星期四 08:30:02;时间格式以24小时制	长度:7byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x23"));
                return false;
            }
            byte[] time = getTimeBytes();
            if (time == null || time.length != XIAODIBLELengthCheck.BLELOCKTIMELENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.param_error,"0x22,时间"));
                return false;

            }
            dataArea = new byte[time.length];
            System.arraycopy(time, 0, dataArea, 0, time.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_ADDBLELOCKFINGER.equals(bleCmdType)) {
            cmd[0] = (byte) 0x24;
            //数据包内容空
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_ADDBLELOCKMOBILEACCOUNT.equals(bleCmdType)) {
            cmd[0] = (byte) 0x25;
            //数据包格式:手机账号ID (13byte)
            //通过APP账户生成改锁对应的唯一的ID	长度:13byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x25"));
                return false;
            }
            byte[] usermobileaccountbytes = getMobileBytes(xiaodiData.getMobileaccount());
            if (usermobileaccountbytes == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.mobile_init_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKMOBILEACCOUNTLENGTH];
            System.arraycopy(usermobileaccountbytes, 0, dataArea, 0, usermobileaccountbytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_ADDSMARTKEYAUTH.equals(bleCmdType)) {
            cmd[0] = (byte) 0x26;
            //长度:13byte
            //数据包格式:智能秘钥(13byte)(随机产生,和数据库不一样) (数据加密)
            //数据包加密方式:数据包分成8个字节一包,每包与锁注册秘钥(指令0x3E)异或成密文,最后打包发送
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x26"));
                return false;
            }
            byte[] tempData = new byte[13];
            System.arraycopy(xiaodiData.getSecretkey13(), 0, tempData, 0, xiaodiData.getSecretkey13().length);

            //字节分段
            //前8个字节
            byte[] tempDataArray = BLEByteUtil.getSubbytes(tempData, 0, 8);
            //最后5个字节
            byte[] tempDataEnd = BLEByteUtil.getSubbytes(tempData, 8, 13);

            BLELogUtil.d(TAG, "加密前，tempDataArray=" + BLEByteUtil.bytesToHexString(tempDataArray));
            BLELogUtil.d(TAG, "加密前，tempDataEnd=" + BLEByteUtil.bytesToHexString(tempDataEnd));

            //加密
            //前8个字节加密
            for (int j = 0; j < tempDataArray.length; j++) {
                tempDataArray[j] = (byte) (tempDataArray[j] ^ xiaodiData.getSecretkey()[j]);
            }
            //最后7个字节加密
            for (int i = 0; i < tempDataEnd.length; i++) {
                tempDataEnd[i] = (byte) (tempDataEnd[i] ^ xiaodiData.getSecretkey()[i]);
            }

            BLELogUtil.d(TAG, "加密后，tempDataArray=" + BLEByteUtil.bytesToHexString(tempDataArray));
            BLELogUtil.d(TAG, "加密后, tempDataEnd=" + BLEByteUtil.bytesToHexString(tempDataEnd));
            dataArea = new byte[13];
            System.arraycopy(tempDataArray, 0, dataArea, 0, tempDataArray.length);
            System.arraycopy(tempDataEnd, 0, dataArea, tempDataArray.length, tempDataEnd.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_ADDSMARTKEY.equals(bleCmdType)) {
            cmd[0] = (byte) 0x27;
            //长度:23byte
            //数据包格式:信道密码(4byte)+秘钥(13byte)+6byte(锁MAC) (数据加密)
            //数据包加密方式:数据包分成8个字节一包,每包与智能钥匙开锁秘钥(指令0x3D)异或成密文,最后打包发送
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x27"));
                return false;
            }
            byte[] tempData = new byte[23];
            byte[] channelpwdbytes = getChannelPwdBytes(xiaodiData.getChannelpwd());
            System.arraycopy(channelpwdbytes, 0, tempData, 0, channelpwdbytes.length);
            System.arraycopy(xiaodiData.getSecretkey13(), 0, tempData, channelpwdbytes.length, xiaodiData.getSecretkey13().length);
            System.arraycopy(xiaodiData.getLockmac(), 0, tempData, channelpwdbytes.length + xiaodiData.getSecretkey13().length, xiaodiData.getLockmac().length);

            //字节分段
            byte[][] tempDataArray = new byte[2][8];
            byte[] tempDataEnd = new byte[tempData.length - 16];
            //前16个字节分成两段
            for (int i = 0; i < tempData.length - tempDataEnd.length; i += 8) {
                tempDataArray[i / 8] = BLEByteUtil.getSubbytes(tempData, i, 8);
            }
            //最后7个字节一段
            tempDataEnd = BLEByteUtil.getSubbytes(tempData, 16, tempDataEnd.length);
            BLELogUtil.d(TAG, "加密前，tempDataArray[0]=" + BLEByteUtil.bytesToHexString(tempDataArray[0]));
            BLELogUtil.d(TAG, "加密前，tempDataArray[1]=" + BLEByteUtil.bytesToHexString(tempDataArray[1]));
            BLELogUtil.d(TAG, "加密前, tempDataEnd=" + BLEByteUtil.bytesToHexString(tempDataEnd));

            //加密
            //前16个字节加密
            for (int i = 0; i < tempDataArray.length; i++) {
                for (int j = 0; j < tempDataArray[i].length; j++) {
                    tempDataArray[i][j] = (byte) (tempDataArray[i][j] ^ xiaodiData.getSecretkey()[j]);
                }
            }
            //最后7个字节加密
            for (int i = 0; i < tempDataEnd.length; i++) {
                tempDataEnd[i] = (byte) (tempDataEnd[i] ^ xiaodiData.getSecretkey()[i]);
            }

            BLELogUtil.d(TAG, "加密后，tempDataArray[0]=" + BLEByteUtil.bytesToHexString(tempDataArray[0]));
            BLELogUtil.d(TAG, "加密后，tempDataArray[1]=" + BLEByteUtil.bytesToHexString(tempDataArray[1]));
            BLELogUtil.d(TAG, "加密后, tempDataEnd=" + BLEByteUtil.bytesToHexString(tempDataEnd));
            dataArea = new byte[23];
            System.arraycopy(tempDataArray[0], 0, dataArea, 0, tempDataArray[0].length);
            System.arraycopy(tempDataArray[1], 0, dataArea, tempDataArray[0].length, tempDataArray[1].length);
            System.arraycopy(tempDataEnd, 0, dataArea, tempDataArray[0].length + tempDataArray[1].length, tempDataEnd.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_DELETEBLELOCKFINGER.equals(bleCmdType)) {
            cmd[0] = (byte) 0x28;
            //数据包格式:指纹PageID	长度:4byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x28"));
                return false;
            }
//            String fingerpageid = xiaodiData.getFingerpageid();
//            byte[] fingerpageidbytes = BLEByteUtil.radixStringToBytes(fingerpageid.replace("-", " "), 16);
            byte[] fingerpageidbytes = xiaodiData.getFingerpageid();
            if (fingerpageidbytes == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.finger_id_empty));
                return false;
            }
            if (fingerpageidbytes.length != XIAODIBLELengthCheck.BLELOCKFINGERIDLENGTH * 3) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.finger_id_length, XIAODIBLELengthCheck.BLELOCKFINGERIDLENGTH * 3));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKFINGERIDLENGTH * 3];
            System.arraycopy(fingerpageidbytes, 0, dataArea, 0, fingerpageidbytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_DELETEBLELOCKMOBILEACCOUNT.equals(bleCmdType)) {
            cmd[0] = (byte) 0x29;
            //数据包格式:手机账号ID	长度:13byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x29"));
                return false;
            }
            byte[] usermobileaccountbytes = getMobileBytes(xiaodiData.getMobileaccount());
            if (usermobileaccountbytes == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.mobile_init_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKMOBILEACCOUNTLENGTH];
            System.arraycopy(usermobileaccountbytes, 0, dataArea, 0, usermobileaccountbytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_DELETESMARTKEY.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2A;
            //数据包格式:KeyID	长度:4byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x2A"));
                return false;
            }
//    		String smartkeyid = xiaodiData.getSmartkeyid();
//    		if(!BLEUtil.checkLength(smartkeyid, "智能钥匙ID", BLELOCKSMARTKEYIDLENGTH)){
//    			BLELogUtil.e(TAG, "智能钥匙ID长度不正确");
//        		return false;
//    		}
            dataArea = new byte[xiaodiData.getSmartkeyid().length];
            System.arraycopy(xiaodiData.getSmartkeyid(), 0, dataArea, 0, xiaodiData.getSmartkeyid().length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_UPDATEBLELOCKFINGERATTRIBUTE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2B;
            //数据包格式:指纹PageID(4byte)+亲情/紧急(1byte)+时效（1byte）+时效范围(13byte)
            //功能开启 0xF,功能关闭0x0
            //其中亲情/紧急：高字节代表亲情，低字节代表紧急
            //时效功能：高字节代表时效，低字节保留
            //周用1byte表示。0b00111110 代表周一到周五时间有效，用bit1代表星期一，bit2代表星期二，用bit3代表星期三，用bit4代表星期四，用bit5代表星期五，bit6代表星期六，用bit7代表礼拜天
            //时效范围：内容起始时间（年+月+日+时+分+秒）+时效时间（年+月+日+时+分+秒）+周
            //长度:19byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x2B"));
                return false;
            }
//            String fingerpageid = xiaodiData.getFingerpageid();
//            BLELogUtil.i("fingerpageid=" + fingerpageid);
            byte[] lovealarmflag = xiaodiData.getLovealarmflag();
            byte[] timestatus = xiaodiData.getTimestatus();
            byte[] timerange = xiaodiData.getTimerange();
            byte[] fingerpageidbytes = xiaodiData.getFingerpageid();
//            try {
//                fingerpageidbytes = BLEByteUtil.radixStringToBytes(fingerpageid.replace("-", " "), 16);
//            } catch (Exception e) {
//                e.printStackTrace();
//                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.finger_id_error));
//                return false;
//            }
            if (fingerpageidbytes == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.finger_id_empty));
                return false;
            }
            if (fingerpageidbytes.length != XIAODIBLELengthCheck.BLELOCKFINGERIDLENGTH * 3) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.finger_id_length, XIAODIBLELengthCheck.BLELOCKFINGERIDLENGTH * 3));
                return false;
            }
            if (!XIAODIBLEUtil.checkLoveAlarmFlag(lovealarmflag)) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.love_alarm_param_error));
                return false;
            }
            if (!XIAODIBLEUtil.checkTimeStatus(timestatus)) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.timeset_param_error));
                return false;
            }
            if (timerange == null || timerange.length != XIAODIBLELengthCheck.BLELOCKTIMERANGELENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.timeset_rang_param_error));
                return false;
            }
            dataArea = new byte[fingerpageidbytes.length + 2 + timerange.length];
            System.arraycopy(fingerpageidbytes, 0, dataArea, 0, fingerpageidbytes.length);
            System.arraycopy(lovealarmflag, 0, dataArea, fingerpageidbytes.length, lovealarmflag.length);
            System.arraycopy(timestatus, 0, dataArea, fingerpageidbytes.length + lovealarmflag.length, timestatus.length);
            System.arraycopy(timerange, 0, dataArea, fingerpageidbytes.length + lovealarmflag.length + timestatus.length, timerange.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_UPDATEBLELOCKMOBILEACCOUNTATTRIBUTE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2C;
            //数据包格式:手机账号ID(13byte)+亲情/紧急(1byte)+时效（1byte）+时效范围(13byte)
            //功能开启 0xF,功能关闭0x0
            //其中亲情/紧急：高字节代表亲情，低字节代表紧急
            //时效功能：高字节代表时效，低字节保留
            //周用1byte表示。0b00111110 代表周一到周五时间有效，用bit1代表星期一，bit2代表星期二，用bit3代表星期三，用bit4代表星期四，用bit5代表星期五，bit6代表星期六，用bit7代表礼拜天
            //时效范围：内容起始时间（年+月+日+时+分+秒）+时效时间（年+月+日+时+分+秒）+周
            //长度:28byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x2C"));
                return false;
            }
            byte[] mobileaccount = getMobileBytes(xiaodiData.getMobileaccount());
            byte[] lovealarmflag = xiaodiData.getLovealarmflag();
            byte[] timestatus = xiaodiData.getTimestatus();
            byte[] timerange = xiaodiData.getTimerange();
            byte[] usermobileaccountbytes = getMobileBytes(new String(mobileaccount));
            if (usermobileaccountbytes == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.mobile_init_error));
                return false;
            }
            if (!XIAODIBLEUtil.checkLoveAlarmFlag(lovealarmflag)) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.love_alarm_param_error));
                return false;
            }
            if (!XIAODIBLEUtil.checkTimeStatus(timestatus)) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.timeset_param_error));
                return false;
            }
            if (timerange == null || timerange.length != XIAODIBLELengthCheck.BLELOCKTIMERANGELENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.timeset_rang_param_error));
                return false;
            }
            dataArea = new byte[usermobileaccountbytes.length + 2 + timerange.length];
            System.arraycopy(usermobileaccountbytes, 0, dataArea, 0, usermobileaccountbytes.length);
            System.arraycopy(lovealarmflag, 0, dataArea, usermobileaccountbytes.length, 1);
            System.arraycopy(timestatus, 0, dataArea, usermobileaccountbytes.length + lovealarmflag.length, 1);
            System.arraycopy(timerange, 0, dataArea, usermobileaccountbytes.length + lovealarmflag.length + timestatus.length, timerange.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_UPDATESMARTKEYATTRIBUTE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2D;
            //数据包格式:智能钥匙ID(4byte)+亲情/紧急(1byte)+时效（1byte）+时效范围(13byte)
            //功能开启 0xF,功能关闭0x0
            //其中亲情/紧急：高字节代表亲情，低字节代表紧急
            //时效功能：高字节代表时效，低字节保留
            //周用1byte表示。0b00111110 代表周一到周五时间有效，用bit1代表星期一，bit2代表星期二，用bit3代表星期三，用bit4代表星期四，用bit5代表星期五，bit6代表星期六，用bit7代表礼拜天
            //时效范围：内容起始时间（年+月+日+时+分+秒）+时效时间（年+月+日+时+分+秒）+周
            //长度:19byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x2D"));
                return false;
            }
            byte[] smartkeyid = xiaodiData.getSmartkeyid();
            byte[] lovealarmflag = xiaodiData.getLovealarmflag();
            byte[] timestatus = xiaodiData.getTimestatus();
            byte[] timerange = xiaodiData.getTimerange();
//    		if(!BLEUtil.checkLength(smartkeyid, "智能钥匙ID", BLELOCKSMARTKEYIDLENGTH)){
//    			BLELogUtil.e(TAG, "智能钥匙ID长度不正确");
//        		return false;
//    		}
//    		if(!BLEUtil.checkLoveAlarmFlag(lovealarmflag)){
//    			BLELogUtil.e(TAG, "亲情/紧急参数不正确");
//        		return false;
//    		}
//    		if(!BLEUtil.checkTimeStatus(timestatus)){
//    			BLELogUtil.e(TAG, "时效状态参数不正确");
//        		return false;
//    		}
//    		if(!BLEUtil.checkTimeRange(timerange)){
//    			BLELogUtil.e(TAG, "时效范围参数不正确");
//        		return false;
//    		}
            dataArea = new byte[smartkeyid.length + 2 + timerange.length];
            System.arraycopy(smartkeyid, 0, dataArea, 0, smartkeyid.length);
            System.arraycopy(lovealarmflag, 0, dataArea, smartkeyid.length, lovealarmflag.length);
            System.arraycopy(timestatus, 0, dataArea, smartkeyid.length + lovealarmflag.length, timestatus.length);
            System.arraycopy(timerange, 0, dataArea, smartkeyid.length + lovealarmflag.length + timestatus.length, timerange.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_UPDATEBLELOCKNAME.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2E;
            //数据包格式:锁名称10byte,编码格式unicode 	长度:10byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x2E"));
                return false;
            }
//            String lockname = xiaodiData.getLockname();
//            if (lockname == null || lockname.length() > BLELOCKNAMELENGTH) {
//                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.lockname_error,BLELOCKNAMELENGTH));
//                return false;
//            }
            byte[] locknamebytes = xiaodiData.getLockname();
            if (locknamebytes == null || locknamebytes.length > XIAODIBLELengthCheck.BLELOCKNAMELENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.lockname_error, XIAODIBLELengthCheck.BLELOCKNAMELENGTH));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKNAMELENGTH];
            System.arraycopy(locknamebytes, 0, dataArea, 0, locknamebytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_CLOSELOCKOPENPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x30;
            //数据包格式:关闭开锁密码8字节 	长度: 8byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x30"));
                return false;
            }
            byte[] closelockpwd = xiaodiData.getCloselockpwd();
            if (closelockpwd == null || closelockpwd.length != XIAODIBLELengthCheck.BLECLOSELOCKOPENLOCKPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.close_open_pwd_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLECLOSELOCKOPENLOCKPASSWORDLENGTH];
            System.arraycopy(closelockpwd, 0, dataArea, 0, closelockpwd.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_UPDATEBLELOCKOPENLOCKPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x30;
            //数据包格式:开锁密码8字节 	长度: 8byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x30"));
                return false;
            }
//            String openlockpwd = xiaodiData.getOpenlockpwd();
//            if (openlockpwd == null || openlockpwd.length() != BLELOCKOPENLOCKPASSWORDLENGTH) {
//                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.open_pwd_param_error));
//                return false;
//            }
            byte[] openlockpwdbytes = xiaodiData.getOpenlockpwd();
            if (openlockpwdbytes == null || openlockpwdbytes.length != XIAODIBLELengthCheck.BLELOCKOPENLOCKPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.open_pwd_param_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKOPENLOCKPASSWORDLENGTH];
            System.arraycopy(openlockpwdbytes, 0, dataArea, 0, openlockpwdbytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_UPDATEBLELOCKMANAGEPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x31;
            //数据包格式:锁管理密码8字节  	长度: 8byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x31"));
                return false;
            }
//            String managepwd = xiaodiData.getManagepwd();
//            if (managepwd == null || managepwd.length() != BLELOCKMANAGEPASSWORDLENGTH) {
//                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.manage_pwd_length_error));
//                return false;
//            }
            byte[] managepwdbytes = xiaodiData.getManagepwd();
            if (managepwdbytes == null || managepwdbytes.length != XIAODIBLELengthCheck.BLELOCKMANAGEPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.manage_pwd_length_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKMANAGEPASSWORDLENGTH];
            System.arraycopy(managepwdbytes, 0, dataArea, 0, managepwdbytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_REGISTERBLELOCKDEVICE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x34;
            //数据包格式:为空	长度:0byte
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_CLEARBLELOCKMOBILEACCOUNT.equals(bleCmdType)) {
            cmd[0] = (byte) 0x35;
//    		//数据包格式:手机账号ID (4byte),需保留的手机账户（首个账户）	长度:4byte
//    		if(dataSendCenter == null){
//    			ViewUtil.printLogAndTips(context, "0x35,参数错误");
//    			return false false;
//    		}
//    		byte[] mobileaccount = dataSendCenter.getMobileaccount();
//    		byte[] usermobileaccountbytes = getMobileBytes(context, new String(mobileaccount));
//    		if(usermobileaccountbytes == null){
//    			ViewUtil.printLogAndTips(context, "手机账号初始化失败");
//    			return false false;
//    		}
//    		dataArea = new byte[BLELOCKMOBILEACCOUNTLENGTH];
//    		System.arraycopy(usermobileaccountbytes, 0, dataArea, 0, usermobileaccountbytes.length);
            dataArea = new byte[]{0x25, (byte) 0x87, 0x45, 0x10};
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_CHECKBLELOCKMANAGEPASSWORD.equals(bleCmdType)) {
            cmd[0] = 0x36;
            //数据包格式:锁管理密码8字节  	长度: 8byte
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x36"));
                return false;
            }
//            String managepwd = xiaodiData.getManagepwd();
//            if (managepwd == null || managepwd.length() != BLELOCKMANAGEPASSWORDLENGTH) {
//                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.manage_pwd_length_error));
//                return false;
//            }
            byte[] managepwdbytes = xiaodiData.getManagepwd();
            if (managepwdbytes == null || managepwdbytes.length != XIAODIBLELengthCheck.BLELOCKMANAGEPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.manage_pwd_length_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKMANAGEPASSWORDLENGTH];
            System.arraycopy(managepwdbytes, 0, dataArea, 0, managepwdbytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_GETBLELOCKSOFTWAREVERSION.equals(bleCmdType)) {
            cmd[0] = 0x37;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_CONFIGBLELOCKALARMPWD.equals(bleCmdType)) {
            cmd[0] = 0x38;
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x38"));
                return false;
            }
            byte[] alarmpwdbytes = xiaodiData.getAlarmpwd();
            if (alarmpwdbytes.length != XIAODIBLELengthCheck.BLELOCKALARMPASSWORDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.alarm_pwd_length_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKALARMPASSWORDLENGTH];
            System.arraycopy(alarmpwdbytes, 0, dataArea, 0, alarmpwdbytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_OPENBLELOCKENHANCE.equals(bleCmdType)) {
            cmd[0] = 0x39;
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x39"));
                return false;
            }
            byte[] channelpwdbytes = getChannelPwdBytes(xiaodiData.getChannelpwd());
            byte[] usermobileaccountbytes = getMobileBytes(xiaodiData.getMobileaccount());
            byte[] timebytes = getTimeBytes();
            byte[] secretkeybytes = xiaodiData.getSecretkey();
            if (xiaodiData == null || channelpwdbytes == null || usermobileaccountbytes == null || timebytes == null
                    || channelpwdbytes.length == 0 || usermobileaccountbytes.length == 0 || timebytes.length == 0 || secretkeybytes == null || secretkeybytes.length == 0) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.param_check_error,"0x39"));
                return false;
            }
            if (usermobileaccountbytes == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.mobile_init_error));
                return false;
            }
            if (XIAODIBLELengthCheck.OPENBLELOCKENHANCEDATAAREALENGTH != channelpwdbytes.length + usermobileaccountbytes.length + timebytes.length) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.data_length_check_error));
                return false;
            }
            if (XIAODIBLELengthCheck.OPENBLELOCKENHANCESECRETKEYLENGTH != secretkeybytes.length) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.secretkey_length_check_error));
                return false;
            }
            byte[] tempData = new byte[XIAODIBLELengthCheck.OPENBLELOCKENHANCEDATAAREALENGTH];
            System.arraycopy(channelpwdbytes, 0, tempData, 0, channelpwdbytes.length);
            System.arraycopy(usermobileaccountbytes, 0, tempData, channelpwdbytes.length, usermobileaccountbytes.length);
            System.arraycopy(timebytes, 0, tempData, channelpwdbytes.length + usermobileaccountbytes.length, timebytes.length);
            byte[][] tempDataArray = new byte[3][8];
            for (int i = 0; i < tempData.length; i += 8) {
                tempDataArray[i / 8] = BLEByteUtil.getSubbytes(tempData, i, 8);
            }
            BLELogUtil.d(TAG, "加密前，dataArea=" + BLEByteUtil.bytesToHexString(tempData));
            for (int i = 0; i < tempDataArray.length; i++) {
                for (int j = 0; j < tempDataArray[i].length; j++) {
                    tempDataArray[i][j] = (byte) (tempDataArray[i][j] ^ secretkeybytes[j]);
                }
            }
            BLELogUtil.d(TAG, "加密后，tempDataArray[0]=" + BLEByteUtil.bytesToHexString(tempDataArray[0]));
            BLELogUtil.d(TAG, "加密后，tempDataArray[1]=" + BLEByteUtil.bytesToHexString(tempDataArray[1]));
            BLELogUtil.d(TAG, "加密后，tempDataArray[2]=" + BLEByteUtil.bytesToHexString(tempDataArray[2]));
            dataArea = new byte[XIAODIBLELengthCheck.OPENBLELOCKENHANCEDATAAREALENGTH];
            System.arraycopy(tempDataArray[0], 0, dataArea, 0, tempDataArray[0].length);
            System.arraycopy(tempDataArray[1], 0, dataArea, tempDataArray[0].length, tempDataArray[1].length);
            System.arraycopy(tempDataArray[2], 0, dataArea, tempDataArray[0].length + tempDataArray[1].length, tempDataArray[2].length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_GETBLELOCKSECRETKEY.equals(bleCmdType)) {
            cmd[0] = 0x3A;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_SMARTKEYGETSECRETKEY.equals(bleCmdType)) {
            cmd[0] = 0x3D;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_REGISTERSMARTKEYGETSECRETKEY.equals(bleCmdType)) {
            cmd[0] = 0x3E;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_GENERATETEMPSECRETKEY.equalsIgnoreCase(bleCmdType)) {
            cmd[0] = 0x3F;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_WIFISTATUSCHECK.equalsIgnoreCase(bleCmdType)) {
            cmd[0] = 0x40;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_WIFISTATUSTOGGLE.equalsIgnoreCase(bleCmdType)) {
            cmd[0] = 0x41;
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0x41"));
                return false;
            }
            dataArea = new byte[1];
            if (xiaodiData.getEnbleWifi()) {
                dataArea[0] = 0x00;
            } else {
                dataArea[0] = 0x11;
            }
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_DISCONNECTBLEDEVICE.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF0;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_WIFICONFIGSTART.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF2;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_WIFICONFIGFINISH.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF3;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_UPDATEBLEDEVICEPROGRAMMINGMODEL.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF4;
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_CONFIGBLELOCKWIFIPARAMS.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF5;
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0xF5"));
                return false;
            }
            byte[] wifissidbytes = xiaodiData.getWifissid();
            byte[] wifipasswordbytes = xiaodiData.getWifipassword();
            if (wifissidbytes.length != XIAODIBLELengthCheck.BLELOCKWIFISSIDLENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.wifi_length_error,"ssid"));
                return false;
            }
            if (wifipasswordbytes.length != XIAODIBLELengthCheck.BLELOCKWIFIPASSWORDLENTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.wifi_length_error,"password"));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.BLELOCKWIFISSIDLENGTH + XIAODIBLELengthCheck.BLELOCKWIFIPASSWORDLENTH];
            System.arraycopy(wifissidbytes, 0, dataArea, 0, wifissidbytes.length);
            System.arraycopy(wifipasswordbytes, 0, dataArea, wifissidbytes.length, wifipasswordbytes.length);
        } else if (XIAODIBLECMDType.BLE_CMDTYPE_OPENLOGUPLOADTOGGLE.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF9;
            if (xiaodiData == null) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.cmd_param_error,"0xF9"));
                return false;
            }
            byte[] openloguploadtogglebytes = xiaodiData.getOpenlogtoggle();
            if (openloguploadtogglebytes.length != XIAODIBLELengthCheck.OPENLOCKLOGUPLOADTOGGLELENGTH) {
                BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.open_lock_record_data_length_error));
                return false;
            }
            dataArea = new byte[XIAODIBLELengthCheck.OPENLOCKLOGUPLOADTOGGLELENGTH];
            System.arraycopy(openloguploadtogglebytes, 0, dataArea, 0, openloguploadtogglebytes.length);
        } else {
            BLELogUtil.e(TAG, BLEApp.bleApp.getString(R.string.ble_data_type_error,bleCmdType + BLEApp.bleApp.getString(R.string.unknown_order)));
            return false;
        }
        try {
            //初始化数据区长度、校验和
            if (dataArea == null) {//没有数据区,初始化封包待发送的数据
                dataAreaLength = new byte[]{0x00, 0x00};
                bleDataSend = new byte[packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length + crc.length];
                System.arraycopy(packageHead, 0, bleDataSend, 0, packageHead.length);
                System.arraycopy(packageAttribute, 0, bleDataSend, packageHead.length, packageAttribute.length);
                System.arraycopy(cmd, 0, bleDataSend, packageHead.length + packageAttribute.length, cmd.length);
                System.arraycopy(dataAreaLength, 0, bleDataSend, packageHead.length + packageAttribute.length + cmd.length, dataAreaLength.length);
                //校验和(2字节)
                byte[] crc = XIAODIBLECRCUtil.getCRCByteData(bleDataSend);
                System.arraycopy(crc, 0, bleDataSend, packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length, crc.length);
            } else {//有数据区,初始化封包待发送的数据
                dataAreaLength[0] = (byte) (dataArea.length >> 8);
                dataAreaLength[1] = (byte) dataArea.length;
                bleDataSend = new byte[packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length + dataArea.length + crc.length];
                System.arraycopy(packageHead, 0, bleDataSend, 0, packageHead.length);
                System.arraycopy(packageAttribute, 0, bleDataSend, packageHead.length, packageAttribute.length);
                System.arraycopy(cmd, 0, bleDataSend, packageHead.length + packageAttribute.length, cmd.length);
                System.arraycopy(dataAreaLength, 0, bleDataSend, packageHead.length + packageAttribute.length + cmd.length, dataAreaLength.length);
                System.arraycopy(dataArea, 0, bleDataSend, packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length, dataArea.length);
                //校验和(2字节)
                byte[] crc = XIAODIBLECRCUtil.getCRCByteData(bleDataSend);
                System.arraycopy(crc, 0, bleDataSend, packageHead.length + packageAttribute.length + cmd.length + dataAreaLength.length + dataArea.length, crc.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 计算手机账号字节数组
     * @param usermobileaccount 手机账号
     * @return  计算结果
     */
    private byte[] getMobileBytes(String usermobileaccount) {
        usermobileaccount = usermobileaccount.replace(" ", "");
        if (!XIAODIBLEUtil.checkMobileAccount(usermobileaccount)) {
            return null;
        }
        return XIAODIBLEUtil.convertStringToBytesWithLength(usermobileaccount, (byte) XIAODIBLELengthCheck.BLELOCKMOBILEACCOUNTLENGTH);
    }

    /**
     * 计算设备信道密码字节数组
     * @param channelpwd    设备信道密码
     * @return  计算结果
     */
    private byte[] getChannelPwdBytes(String channelpwd){
        byte[] channelpwdbytes = new byte[4];
        if(BLEStringUtil.isEmpty(channelpwd) || channelpwd.length() != 8){
            return null;
        }
        String[] lockchannelarray = new String[channelpwdbytes.length];
        for(int i=0;i<lockchannelarray.length;i++){
            lockchannelarray[i] = channelpwd.substring(i*2, i*2 + 2);
            channelpwdbytes[i] = (byte) Integer.parseInt(lockchannelarray[i], 16);
        }
        return channelpwdbytes;
    }

    /**
     * 计算开门时间转换字节
     * @return  计算结果
     */
    private byte[]  getTimeBytes(){
        return XIAODIBLEUtil.parseServerTimeToProtocolBytes(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(System.currentTimeMillis())));
    }
}