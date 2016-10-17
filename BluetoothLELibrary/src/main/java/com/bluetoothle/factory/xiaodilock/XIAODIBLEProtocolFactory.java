package com.bluetoothle.factory.xiaodilock;

import android.content.Context;

import com.bluetoothle.R;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

public class XIAODIBLEProtocolFactory {

    private final static String tag = XIAODIBLEProtocolFactory.class.getSimpleName();

    private final static Context appContext = App.getInstance();

    public final static String SERVICE_UUID = "0000ffe5-0000-1000-8000-00805f9b34fb";
    public final static String CHARACTERISTIC_UUID = "0000ffe9-0000-1000-8000-00805f9b34fb";

    public final static String NOTIFICATION_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public final static String NOTIFICATION_CHARACTERISTIC_UUID = "0000ffe4-0000-1000-8000-00805f9b34fb";
    public final static String BLUETOOTHGATTDESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    private String bleDataType = null;                                                                                                            //数据包类类型
    public final static String BLE_DATATYPE_REQUEST = "com.dsm.ble.data.BLE_DATATYPE_REQUEST";                                                    //请求数据包
    public final static String BLE_DATATYPE_NORESPONSE = "com.dsm.ble.data.BLE_DATATYPE_NORESPONSE";                                            //不应答数据包

    private String bleCmdType = null;
    public final static String BLE_CMDTYPE_CHECKCHANNALPASSWORD = "com.dsm.ble.BLE_CMDTYPE_CHECKCHANNALPASSWORD";                                //验证信道密码 (0x01)
    public final static String BLE_CMDTYPE_UPDATECHANNALPASSWORD = "com.dsm.ble.BLE_CMDTYPE_UPDATECHANNALPASSWORD";                                //修改信道密码 (0x02)
    public final static String BLE_CMDTYPE_OPENBLELOCK = "com.dsm.ble.BLE_CMDTYPE_OPENBLELOCK";                                                    //手机开锁验证 (0x03)
    public final static String BLE_CMDTYPE_GETBLELOCKINFO = "com.dsm.ble.BLE_CMDTYPE_GETBLELOCKINFO";                                            //锁存储信息获取 (0x20)
    public final static String BLE_CMDTYPE_SETBLELOCKOPENTYPE = "com.dsm.ble.BLE_CMDTYPE_SETBLELOCKOPENTYPE";                                    //开锁方式设置 (0x22)
    public final static String BLE_CMDTYPE_SYNCBLELOCKTIME = "com.dsm.ble.BLE_CMDTYPE_SYNCBLELOCKTIME";                                            //时间同步 (0x23)
    public final static String BLE_CMDTYPE_ADDBLELOCKFINGER = "com.dsm.ble.BLE_CMDTYPE_ADDBLELOCKFINGER";                                        //增加指纹 (0x24)
    public final static String BLE_CMDTYPE_ADDBLELOCKMOBILEACCOUNT = "com.dsm.ble.BLE_CMDTYPE_ADDBLELOCKMOBILEACCOUNT";                            //增加手机账号 (0x25)
    public final static String BLE_CMDTYPE_ADDSMARTKEYAUTH = "com.dsm.ble.BLE_CMDTYPE_ADDSMARTKEYAUTH";                                            //增加智能钥匙授权 (0x26)
    public final static String BLE_CMDTYPE_ADDSMARTKEY = "com.dsm.ble.BLE_CMDTYPE_ADDSMARTKEY";                                                    //增加智能钥匙 (0x27)
    public final static String BLE_CMDTYPE_DELETEBLELOCKFINGER = "com.dsm.ble.BLE_CMDTYPE_DELETEBLELOCKFINGER";                                    //删除指纹 (0x28)
    public final static String BLE_CMDTYPE_DELETEBLELOCKMOBILEACCOUNT = "com.dsm.ble.BLE_CMDTYPE_DELETEBLELOCKMOBILEACCOUNT";                    //删除手机账号 (0x29)
    public final static String BLE_CMDTYPE_DELETESMARTKEY = "com.dsm.ble.BLE_CMDTYPE_DELETESMARTKEY";                                            //删除智能钥匙 (0x2A)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKFINGERATTRIBUTE = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKFINGERATTRIBUTE";                //指纹属性修改 (0x2B)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKMOBILEACCOUNTATTRIBUTE = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKMOBILEACCOUNTATTRIBUTE";    //手机账号属性修改 (0x2C)
    public final static String BLE_CMDTYPE_UPDATESMARTKEYATTRIBUTE = "com.dsm.ble.BLE_CMDTYPE_UPDATESMARTKEYATTRIBUTE";                            //智能钥匙属性修改 (0x2D)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKNAME = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKNAME";                                        //锁名称设置 (0x2E)
    public final static String BLE_CMDTYPE_CLOSELOCKOPENPASSWORD = "com.dsm.ble.BLE_CMDTYPE_CLOSELOCKOPENPASSWORD";                                //开锁密码设置 (0x00)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKOPENLOCKPASSWORD = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKOPENLOCKPASSWORD";                //开锁密码设置 (0x30)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKMANAGEPASSWORD = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKMANAGEPASSWORD";                    //锁管理密码设置 (0x31)
    public final static String BLE_CMDTYPE_REGISTERBLELOCKDEVICE = "com.dsm.ble.BLE_CMDTYPE_REGISTERBLELOCKDEVICE";                                //设备注册 (0x34)
    public final static String BLE_CMDTYPE_CLEARBLELOCKMOBILEACCOUNT = "com.dsm.ble.BLE_CMDTYPE_CLEARBLELOCKMOBILEACCOUNT";                        //清空用户(0x35)
    public final static String BLE_CMDTYPE_CHECKBLELOCKMANAGEPASSWORD = "com.dsm.ble.BLE_CMDTYPE_CHECKBLELOCKMANAGEPASSWORD";                    //锁管理密码验证 (0x36)
    public final static String BLE_CMDTYPE_GETBLELOCKSOFTWAREVERSION = "com.dsm.ble.BLE_CMDTYPE_GETBLELOCKSOFTWAREVERSION";                        //锁固件版本获取 (0x37)
    public final static String BLE_CMDTYPE_CONFIGBLELOCKALARMPWD = "com.dsm.ble.BLE_CMDTYPE_CONFIGBLELOCKALARMPWD";                                //报警密码设置 (0x38)
    public final static String BLE_CMDTYPE_OPENBLELOCKENHANCE = "com.dsm.ble.BLE_CMDTYPE_OPENBLELOCKENHANCE";                                    //手机开锁验证加密版 (0x39)													//手机开锁验证加密版 (0x39)
    public final static String BLE_CMDTYPE_GETBLELOCKSECRETKEY = "com.dsm.ble.BLE_CMDTYPE_GETBLELOCKSECRETKEY";                                    //手机秘钥获取 (0x3A)
    public final static String BLE_CMDTYPE_SMARTKEYGETSECRETKEY = "com.dsm.ble.BLE_CMDTYPE_SMARTKEYGETSECRETKEY";                                //智能钥匙授权获取随机数秘钥(0x3D)
    public final static String BLE_CMDTYPE_REGISTERSMARTKEYGETSECRETKEY = "com.dsm.ble.BLE_CMDTYPE_REGISTERSMARTKEYGETSECRETKEY";                //锁注册秘钥获取 (0x3E)
    public final static String BLE_CMDTYPE_GENERATETEMPSECRETKEY = "com.dsm.ble.BLE_CMDTYPE_GENERATETEMPSECRETKEY";                                //产生临时密钥(0x3F)
    public final static String BLE_CMDTYPE_WIFISTATUSCHECK = "com.dsm.ble.BLE_CMDTYPE_WIFISTATUSCHECK";                                            //WIFI连接状态检测(0x40)
    public final static String BLE_CMDTYPE_WIFISTATUSTOGGLE = "com.dsm.ble.BLE_CMDTYPE_WIFISTATUSTOGGLE";                                        //WIFI功能开启关闭设置(0x41)
    public final static String BLE_CMDTYPE_DISCONNECTBLEDEVICE = "com.dsm.ble.BLE_CMDTYPE_DISCONNECTBLEDEVICE";                                    //断开蓝牙连接(0xF0)
    public final static String BLE_CMDTYPE_WIFICONFIGSTART = "com.dsm.ble.BLE_CMDTYPE_WIFICONFIGSTART";                                            //WIFI设置启动 (0xF2)
    public final static String BLE_CMDTYPE_WIFICONFIGFINISH = "com.dsm.ble.BLE_CMDTYPE_WIFICONFIGFINISH";                                        //WIFI设置结束 (0xF3)
    public final static String BLE_CMDTYPE_UPDATEBLEDEVICEPROGRAMMINGMODEL = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLEDEVICEPROGRAMMINGMODEL";            //更新程序模式(0xF4)
    public final static String BLE_CMDTYPE_CONFIGBLELOCKWIFIPARAMS = "com.dsm.ble.BLE_CMDTYPE_CONFIGBLELOCKWIFIPARAMS";                            //WIFI设置参数（0xF5）
    public final static String BLE_CMDTYPE_OPENLOGUPLOADTOGGLE = "com.dsm.ble.BLE_CMDTYPE_OPENLOGUPLOADTOGGLE";                                    //开锁记录实时上传功能开启与关闭切换(0xF9)
    public final static String BLE_CMDTYPE_UPDATEFIRMWARE = "com.dsm.ble.BLE_CMDTYPE_UPDATEFIRMWARE";                                                //更新固件()

    //锁信道密码长度
    public static int BLELOCKCHANNELPASSWORDLENGTH = 4;
    //手机账号长度
    public static int BLELOCKMOBILEACCOUNTLENGTH = 13;
    //时间长度
    public static int BLELOCKTIMELENGTH = 7;
    //锁上的指纹ID长度
    public static int BLELOCKFINGERIDLENGTH = 4;
    //智能钥匙ID长度
//	public static int BLELOCKSMARTKEYIDLENGTH = 4;
    //锁名称长度
    public static int BLELOCKNAMELENGTH = 10;
    //禁用开锁密码
    public static int BLECLOSELOCKOPENLOCKPASSWORDLENGTH = 8;
    //开锁密码长度
    public static int BLELOCKOPENLOCKPASSWORDLENGTH = 8;
    //管理密码长度
    public static int BLELOCKMANAGEPASSWORDLENGTH = 8;
    //报警密码长度
    public static int BLELOCKALARMPASSWORDLENGTH = 8;
    //时效范围长度
    public static int BLELOCKTIMERANGELENGTH = 13;
    //wifi ssid长度
    public static int BLELOCKWIFISSIDLENGTH = 33;
    //wifi password长度
    public static int BLELOCKWIFIPASSWORDLENTH = 65;
    //加密开门协议数据区长度
    public static int OPENBLELOCKENHANCEDATAAREALENGTH = 24;
    //加密开门协议秘钥长度
    public static int OPENBLELOCKENHANCESECRETKEYLENGTH = 8;
    //开锁记录上传功能开关数据长度
    public static int OPENLOCKLOGUPLOADTOGGLELENGTH = 1;

    //数据信息集合
    private XIAODIDataSendCenter xiaodiDataSendCenter = null;

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
    //封装封包待发送的数据
    private XIAODIDataSend dataSend = null;

    public XIAODIBLEProtocolFactory(byte[] bleDataReceived) {
        this.bleDataReceived = bleDataReceived;
        analysisBLEReturnData();
        initBleDataReceived();
    }

    public XIAODIBLEProtocolFactory(String bleDataType, String bleCmdType, XIAODIDataSendCenter xiaodiDataSendCenter) {
        this.bleDataType = bleDataType;
        this.bleCmdType = bleCmdType;
        this.xiaodiDataSendCenter = xiaodiDataSendCenter;
    }

    //初始化手机账号
    private byte[] initUserMobileAccount(String usermobileaccount) {
        if (!XIAODIBLEUtil.checkMobileAccount(usermobileaccount)) {
            return null;
        }
        return XIAODIBLEUtil.convertStringToBytesWithLength(new String(usermobileaccount.getBytes()), (byte) BLELOCKMOBILEACCOUNTLENGTH);
    }

    public interface InitBleAreaDataListener {
        void initBleAreaDataSuccess(byte[] data);

        void initBleAreaDataFailure(String error);
    }

    //初始化包头信息、包属性信息、指令、数据区数据(变长)
    public void initBleAreaData(InitBleAreaDataListener initBleAreaDataListener) {
        //包头信息(1字节)
        packageHead[0] = (byte) 0xFE;
        //包属性信息(1字节)
        if (BLE_DATATYPE_REQUEST.equals(bleDataType)) {
            packageAttribute[0] = (byte) 0x01;
        } else if (BLE_DATATYPE_NORESPONSE.equals(bleDataType)) {
            packageAttribute[0] = (byte) 0x02;
        } else {
            initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.ble_data_type_error,bleDataType+",无法识别的包属性"));
            return;
        }
        //指令(1字节) 数据区数据(变长)
        if (BLE_CMDTYPE_CHECKCHANNALPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x01;
            //数据包为信道密码,占4byte,出厂密码为0x00000000 代表密码为00000000
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x01"));
                return;
            }
            byte[] channelpwd = xiaodiDataSendCenter.getChannelpwd();
            if (channelpwd == null || channelpwd.length != BLELOCKCHANNELPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_channel_length_error,"0x01"));
                return;
            }
            dataArea = new byte[BLELOCKCHANNELPASSWORDLENGTH];
            System.arraycopy(channelpwd, 0, dataArea, 0, channelpwd.length);
        } else if (BLE_CMDTYPE_UPDATECHANNALPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x02;
            //数据包为信道密码原密码+新密码,占8byte,出厂密码为0x00000000,APP显示为00000000,设置范围从00000000~99999999
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x02"));
                return;
            }
            byte[] channelpwd = xiaodiDataSendCenter.getChannelpwd();
            byte[] newchannelpwd = xiaodiDataSendCenter.getNewchannelpwd();
            if (channelpwd == null || channelpwd.length != BLELOCKCHANNELPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_channel_length_error,"0x02"));
                return;
            }
            if (newchannelpwd == null || newchannelpwd.length != BLELOCKCHANNELPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_channel_length_error,"0x02"));
                return;
            }
            dataArea = new byte[BLELOCKCHANNELPASSWORDLENGTH * 2];
            System.arraycopy(channelpwd, 0, dataArea, 0, channelpwd.length);
            System.arraycopy(newchannelpwd, 0, dataArea, channelpwd.length, newchannelpwd.length);
        } else if (BLE_CMDTYPE_OPENBLELOCK.equals(bleCmdType)) {
            cmd[0] = (byte) 0x03;
            //数据包格式:信道密码(4byte)+手机属性(账号ID 13byte) 长度:17byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x03"));
                return;
            }
            byte[] channelpwd = xiaodiDataSendCenter.getChannelpwd();
            byte[] mobileaccount = xiaodiDataSendCenter.getMobileaccount();
            if (channelpwd.length != BLELOCKCHANNELPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_channel_length_error,"0x03"));
                return;
            }
            byte[] usermobileaccountbytes = initUserMobileAccount(new String(mobileaccount).replace(" ", ""));
            if (usermobileaccountbytes == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.mobile_init_error));
                return;
            }
            dataArea = new byte[BLELOCKCHANNELPASSWORDLENGTH + BLELOCKMOBILEACCOUNTLENGTH];
            System.arraycopy(channelpwd, 0, dataArea, 0, channelpwd.length);
            System.arraycopy(usermobileaccountbytes, 0, dataArea, channelpwd.length, usermobileaccountbytes.length);
        } else if (BLE_CMDTYPE_GETBLELOCKINFO.equals(bleCmdType)) {
            cmd[0] = (byte) 0x20;
            //数据包格式:无 长度:0byte
        } else if (BLE_CMDTYPE_SETBLELOCKOPENTYPE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x22;
            //数据包格式:开锁方式	指纹开锁方式代码为0x01	手机开锁方式为0x02		智能钥匙开锁方式为0x04	长度:1byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x22"));
                return;
            }
            byte[] openlocktype = xiaodiDataSendCenter.getOpenlocktype();
            if (!XIAODIBLEUtil.checkOpenLockType(openlocktype)) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.param_error,"0x22,"));
                return;
            }
            dataArea = openlocktype;
        } else if (BLE_CMDTYPE_SYNCBLELOCKTIME.equals(bleCmdType)) {
            cmd[0] = (byte) 0x23;
            //数据包格式:时间格式(年,月,日,周,小时,分钟,秒)(7byte)
            //时间格式每个时间单位用1byte代表时间,高字节代表十位时间格式,低字节代码个位时间格式。
            //其中年的基准以2000为基准,如果时间格式为0x15010104083002,代表2015年01月01日 星期四 08:30:02;时间格式以24小时制	长度:7byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x23"));
                return;
            }
            byte[] time = xiaodiDataSendCenter.getTime();
            if (time == null || time.length != BLELOCKTIMELENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.param_error,"0x22,时间"));
                return;

            }
            dataArea = new byte[time.length];
            System.arraycopy(time, 0, dataArea, 0, time.length);
        } else if (BLE_CMDTYPE_ADDBLELOCKFINGER.equals(bleCmdType)) {
            cmd[0] = (byte) 0x24;
            //数据包内容空
        } else if (BLE_CMDTYPE_ADDBLELOCKMOBILEACCOUNT.equals(bleCmdType)) {
            cmd[0] = (byte) 0x25;
            //数据包格式:手机账号ID (13byte)
            //通过APP账户生成改锁对应的唯一的ID	长度:13byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x25"));
                return;
            }
            byte[] mobileaccount = xiaodiDataSendCenter.getMobileaccount();
            byte[] usermobileaccountbytes = initUserMobileAccount(new String(mobileaccount).replace(" ", ""));
            if (usermobileaccountbytes == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.mobile_init_error));
                return;
            }
            dataArea = new byte[BLELOCKMOBILEACCOUNTLENGTH];
            System.arraycopy(usermobileaccountbytes, 0, dataArea, 0, usermobileaccountbytes.length);
        } else if (BLE_CMDTYPE_ADDSMARTKEYAUTH.equals(bleCmdType)) {
            cmd[0] = (byte) 0x26;
            //长度:13byte
            //数据包格式:智能秘钥(13byte)(随机产生,和数据库不一样) (数据加密)
            //数据包加密方式:数据包分成8个字节一包,每包与锁注册秘钥(指令0x3E)异或成密文,最后打包发送
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x26"));
                return;
            }
            byte[] tempData = new byte[13];
            System.arraycopy(xiaodiDataSendCenter.getSecretkey13(), 0, tempData, 0, xiaodiDataSendCenter.getSecretkey13().length);

            //字节分段
            //前8个字节
            byte[] tempDataArray = BLEByteUtil.getSubbytes(tempData, 0, 8);
            //最后5个字节
            byte[] tempDataEnd = BLEByteUtil.getSubbytes(tempData, 8, 13);

            BLELogUtil.d(tag, "加密前，tempDataArray=" + BLEByteUtil.bytesToHexString(tempDataArray));
            BLELogUtil.d(tag, "加密前，tempDataEnd=" + BLEByteUtil.bytesToHexString(tempDataEnd));

            //加密
            //前8个字节加密
            for (int j = 0; j < tempDataArray.length; j++) {
                tempDataArray[j] = (byte) (tempDataArray[j] ^ xiaodiDataSendCenter.getSecretkey()[j]);
            }
            //最后7个字节加密
            for (int i = 0; i < tempDataEnd.length; i++) {
                tempDataEnd[i] = (byte) (tempDataEnd[i] ^ xiaodiDataSendCenter.getSecretkey()[i]);
            }

            BLELogUtil.d(tag, "加密后，tempDataArray=" + BLEByteUtil.bytesToHexString(tempDataArray));
            BLELogUtil.d(tag, "加密后, tempDataEnd=" + BLEByteUtil.bytesToHexString(tempDataEnd));
            dataArea = new byte[13];
            System.arraycopy(tempDataArray, 0, dataArea, 0, tempDataArray.length);
            System.arraycopy(tempDataEnd, 0, dataArea, tempDataArray.length, tempDataEnd.length);
        } else if (BLE_CMDTYPE_ADDSMARTKEY.equals(bleCmdType)) {
            cmd[0] = (byte) 0x27;
            //长度:23byte
            //数据包格式:信道密码(4byte)+秘钥(13byte)+6byte(锁MAC) (数据加密)
            //数据包加密方式:数据包分成8个字节一包,每包与智能钥匙开锁秘钥(指令0x3D)异或成密文,最后打包发送
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x27"));
                return;
            }
            byte[] tempData = new byte[23];
            System.arraycopy(xiaodiDataSendCenter.getChannelpwd(), 0, tempData, 0, xiaodiDataSendCenter.getChannelpwd().length);
            System.arraycopy(xiaodiDataSendCenter.getSecretkey13(), 0, tempData, xiaodiDataSendCenter.getChannelpwd().length, xiaodiDataSendCenter.getSecretkey13().length);
            System.arraycopy(xiaodiDataSendCenter.getLockmac(), 0, tempData, xiaodiDataSendCenter.getChannelpwd().length + xiaodiDataSendCenter.getSecretkey13().length, xiaodiDataSendCenter.getLockmac().length);

            //字节分段
            byte[][] tempDataArray = new byte[2][8];
            byte[] tempDataEnd = new byte[tempData.length - 16];
            //前16个字节分成两段
            for (int i = 0; i < tempData.length - tempDataEnd.length; i += 8) {
                tempDataArray[i / 8] = BLEByteUtil.getSubbytes(tempData, i, 8);
            }
            //最后7个字节一段
            tempDataEnd = BLEByteUtil.getSubbytes(tempData, 16, tempDataEnd.length);
            BLELogUtil.d(tag, "加密前，tempDataArray[0]=" + BLEByteUtil.bytesToHexString(tempDataArray[0]));
            BLELogUtil.d(tag, "加密前，tempDataArray[1]=" + BLEByteUtil.bytesToHexString(tempDataArray[1]));
            BLELogUtil.d(tag, "加密前, tempDataEnd=" + BLEByteUtil.bytesToHexString(tempDataEnd));

            //加密
            //前16个字节加密
            for (int i = 0; i < tempDataArray.length; i++) {
                for (int j = 0; j < tempDataArray[i].length; j++) {
                    tempDataArray[i][j] = (byte) (tempDataArray[i][j] ^ xiaodiDataSendCenter.getSecretkey()[j]);
                }
            }
            //最后7个字节加密
            for (int i = 0; i < tempDataEnd.length; i++) {
                tempDataEnd[i] = (byte) (tempDataEnd[i] ^ xiaodiDataSendCenter.getSecretkey()[i]);
            }

            BLELogUtil.d(tag, "加密后，tempDataArray[0]=" + BLEByteUtil.bytesToHexString(tempDataArray[0]));
            BLELogUtil.d(tag, "加密后，tempDataArray[1]=" + BLEByteUtil.bytesToHexString(tempDataArray[1]));
            BLELogUtil.d(tag, "加密后, tempDataEnd=" + BLEByteUtil.bytesToHexString(tempDataEnd));
            dataArea = new byte[23];
            System.arraycopy(tempDataArray[0], 0, dataArea, 0, tempDataArray[0].length);
            System.arraycopy(tempDataArray[1], 0, dataArea, tempDataArray[0].length, tempDataArray[1].length);
            System.arraycopy(tempDataEnd, 0, dataArea, tempDataArray[0].length + tempDataArray[1].length, tempDataEnd.length);
        } else if (BLE_CMDTYPE_DELETEBLELOCKFINGER.equals(bleCmdType)) {
            cmd[0] = (byte) 0x28;
            //数据包格式:指纹PageID	长度:4byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x28"));
                return;
            }
            String fingerpageid = xiaodiDataSendCenter.getFingerpageid();
            byte[] fingerpageidbytes = BLEByteUtil.radixStringToBytes(fingerpageid.replace("-", " "), 16);
            if (fingerpageidbytes == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.finger_id_empty));
                return;
            }
            if (fingerpageidbytes.length != BLELOCKFINGERIDLENGTH * 3) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.finger_id_length,BLELOCKFINGERIDLENGTH * 3));
                return;
            }
            dataArea = new byte[BLELOCKFINGERIDLENGTH * 3];
            System.arraycopy(fingerpageidbytes, 0, dataArea, 0, fingerpageidbytes.length);
        } else if (BLE_CMDTYPE_DELETEBLELOCKMOBILEACCOUNT.equals(bleCmdType)) {
            cmd[0] = (byte) 0x29;
            //数据包格式:手机账号ID	长度:13byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x29"));
                return;
            }
            byte[] mobileaccount = xiaodiDataSendCenter.getMobileaccount();
            byte[] usermobileaccountbytes = initUserMobileAccount(new String(mobileaccount));
            if (usermobileaccountbytes == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.mobile_init_error));
                return;
            }
            dataArea = new byte[BLELOCKMOBILEACCOUNTLENGTH];
            System.arraycopy(usermobileaccountbytes, 0, dataArea, 0, usermobileaccountbytes.length);
        } else if (BLE_CMDTYPE_DELETESMARTKEY.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2A;
            //数据包格式:KeyID	长度:4byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x2A"));
                return;
            }
//    		String smartkeyid = xiaodiDataSendCenter.getSmartkeyid();
//    		if(!BLEUtil.checkLength(smartkeyid, "智能钥匙ID", BLELOCKSMARTKEYIDLENGTH)){
//    			initBleAreaDataListener.initBleAreaDataFailure("智能钥匙ID长度不正确");
//        		return;
//    		}
            dataArea = new byte[xiaodiDataSendCenter.getSmartkeyid().length];
            System.arraycopy(xiaodiDataSendCenter.getSmartkeyid(), 0, dataArea, 0, xiaodiDataSendCenter.getSmartkeyid().length);
        } else if (BLE_CMDTYPE_UPDATEBLELOCKFINGERATTRIBUTE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2B;
            //数据包格式:指纹PageID(4byte)+亲情/紧急(1byte)+时效（1byte）+时效范围(13byte)
            //功能开启 0xF,功能关闭0x0
            //其中亲情/紧急：高字节代表亲情，低字节代表紧急
            //时效功能：高字节代表时效，低字节保留
            //周用1byte表示。0b00111110 代表周一到周五时间有效，用bit1代表星期一，bit2代表星期二，用bit3代表星期三，用bit4代表星期四，用bit5代表星期五，bit6代表星期六，用bit7代表礼拜天
            //时效范围：内容起始时间（年+月+日+时+分+秒）+时效时间（年+月+日+时+分+秒）+周
            //长度:19byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x2B"));
                return;
            }
            String fingerpageid = xiaodiDataSendCenter.getFingerpageid();
            BLELogUtil.i("fingerpageid=" + fingerpageid);
            byte[] lovealarmflag = xiaodiDataSendCenter.getLovealarmflag();
            byte[] timestatus = xiaodiDataSendCenter.getTimestatus();
            byte[] timerange = xiaodiDataSendCenter.getTimerange();
            byte[] fingerpageidbytes = new byte[0];
            try {
                fingerpageidbytes = BLEByteUtil.radixStringToBytes(fingerpageid.replace("-", " "), 16);
            } catch (Exception e) {
                e.printStackTrace();
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.finger_id_error));
                return;
            }
            if (fingerpageidbytes == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.finger_id_empty));
                return;
            }
            if (fingerpageidbytes.length != BLELOCKFINGERIDLENGTH * 3) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.finger_id_length,BLELOCKFINGERIDLENGTH * 3));
                return;
            }
            if (!XIAODIBLEUtil.checkLoveAlarmFlag(lovealarmflag)) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.love_alarm_param_error));
                return;
            }
            if (!XIAODIBLEUtil.checkTimeStatus(timestatus)) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.timeset_param_error));
                return;
            }
            if (timerange == null || timerange.length != BLELOCKTIMERANGELENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.timeset_rang_param_error));
                return;
            }
            dataArea = new byte[fingerpageidbytes.length + 2 + timerange.length];
            System.arraycopy(fingerpageidbytes, 0, dataArea, 0, fingerpageidbytes.length);
            System.arraycopy(lovealarmflag, 0, dataArea, fingerpageidbytes.length, lovealarmflag.length);
            System.arraycopy(timestatus, 0, dataArea, fingerpageidbytes.length + lovealarmflag.length, timestatus.length);
            System.arraycopy(timerange, 0, dataArea, fingerpageidbytes.length + lovealarmflag.length + timestatus.length, timerange.length);
        } else if (BLE_CMDTYPE_UPDATEBLELOCKMOBILEACCOUNTATTRIBUTE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2C;
            //数据包格式:手机账号ID(13byte)+亲情/紧急(1byte)+时效（1byte）+时效范围(13byte)
            //功能开启 0xF,功能关闭0x0
            //其中亲情/紧急：高字节代表亲情，低字节代表紧急
            //时效功能：高字节代表时效，低字节保留
            //周用1byte表示。0b00111110 代表周一到周五时间有效，用bit1代表星期一，bit2代表星期二，用bit3代表星期三，用bit4代表星期四，用bit5代表星期五，bit6代表星期六，用bit7代表礼拜天
            //时效范围：内容起始时间（年+月+日+时+分+秒）+时效时间（年+月+日+时+分+秒）+周
            //长度:28byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x2C"));
                return;
            }
            byte[] mobileaccount = xiaodiDataSendCenter.getMobileaccount();
            byte[] lovealarmflag = xiaodiDataSendCenter.getLovealarmflag();
            byte[] timestatus = xiaodiDataSendCenter.getTimestatus();
            byte[] timerange = xiaodiDataSendCenter.getTimerange();
            byte[] usermobileaccountbytes = initUserMobileAccount(new String(mobileaccount));
            if (usermobileaccountbytes == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.mobile_init_error));
                return;
            }
            if (!XIAODIBLEUtil.checkLoveAlarmFlag(lovealarmflag)) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.love_alarm_param_error));
                return;
            }
            if (!XIAODIBLEUtil.checkTimeStatus(timestatus)) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.timeset_param_error));
                return;
            }
            if (timerange == null || timerange.length != BLELOCKTIMERANGELENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.timeset_rang_param_error));
                return;
            }
            dataArea = new byte[usermobileaccountbytes.length + 2 + timerange.length];
            System.arraycopy(usermobileaccountbytes, 0, dataArea, 0, usermobileaccountbytes.length);
            System.arraycopy(lovealarmflag, 0, dataArea, usermobileaccountbytes.length, 1);
            System.arraycopy(timestatus, 0, dataArea, usermobileaccountbytes.length + lovealarmflag.length, 1);
            System.arraycopy(timerange, 0, dataArea, usermobileaccountbytes.length + lovealarmflag.length + timestatus.length, timerange.length);
        } else if (BLE_CMDTYPE_UPDATESMARTKEYATTRIBUTE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2D;
            //数据包格式:智能钥匙ID(4byte)+亲情/紧急(1byte)+时效（1byte）+时效范围(13byte)
            //功能开启 0xF,功能关闭0x0
            //其中亲情/紧急：高字节代表亲情，低字节代表紧急
            //时效功能：高字节代表时效，低字节保留
            //周用1byte表示。0b00111110 代表周一到周五时间有效，用bit1代表星期一，bit2代表星期二，用bit3代表星期三，用bit4代表星期四，用bit5代表星期五，bit6代表星期六，用bit7代表礼拜天
            //时效范围：内容起始时间（年+月+日+时+分+秒）+时效时间（年+月+日+时+分+秒）+周
            //长度:19byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x2D"));
                return;
            }
            byte[] smartkeyid = xiaodiDataSendCenter.getSmartkeyid();
            byte[] lovealarmflag = xiaodiDataSendCenter.getLovealarmflag();
            byte[] timestatus = xiaodiDataSendCenter.getTimestatus();
            byte[] timerange = xiaodiDataSendCenter.getTimerange();
//    		if(!BLEUtil.checkLength(smartkeyid, "智能钥匙ID", BLELOCKSMARTKEYIDLENGTH)){
//    			initBleAreaDataListener.initBleAreaDataFailure("智能钥匙ID长度不正确");
//        		return;
//    		}
//    		if(!BLEUtil.checkLoveAlarmFlag(lovealarmflag)){
//    			initBleAreaDataListener.initBleAreaDataFailure("亲情/紧急参数不正确");
//        		return;
//    		}
//    		if(!BLEUtil.checkTimeStatus(timestatus)){
//    			initBleAreaDataListener.initBleAreaDataFailure("时效状态参数不正确");
//        		return;
//    		}
//    		if(!BLEUtil.checkTimeRange(timerange)){
//    			initBleAreaDataListener.initBleAreaDataFailure("时效范围参数不正确");
//        		return;
//    		}
            dataArea = new byte[smartkeyid.length + 2 + timerange.length];
            System.arraycopy(smartkeyid, 0, dataArea, 0, smartkeyid.length);
            System.arraycopy(lovealarmflag, 0, dataArea, smartkeyid.length, lovealarmflag.length);
            System.arraycopy(timestatus, 0, dataArea, smartkeyid.length + lovealarmflag.length, timestatus.length);
            System.arraycopy(timerange, 0, dataArea, smartkeyid.length + lovealarmflag.length + timestatus.length, timerange.length);
        } else if (BLE_CMDTYPE_UPDATEBLELOCKNAME.equals(bleCmdType)) {
            cmd[0] = (byte) 0x2E;
            //数据包格式:锁名称10byte,编码格式unicode 	长度:10byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x2E"));
                return;
            }
            String lockname = xiaodiDataSendCenter.getLockname();
            if (lockname == null || lockname.length() > BLELOCKNAMELENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.lockname_error,BLELOCKNAMELENGTH));
                return;
            }
            dataArea = new byte[BLELOCKNAMELENGTH];
            System.arraycopy(lockname.getBytes(), 0, dataArea, 0, lockname.length());
        } else if (BLE_CMDTYPE_CLOSELOCKOPENPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x30;
            //数据包格式:关闭开锁密码8字节 	长度: 8byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x30"));
                return;
            }
            byte[] closelockpwd = xiaodiDataSendCenter.getCloselockpwd();
            if (closelockpwd == null || closelockpwd.length != BLECLOSELOCKOPENLOCKPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.close_open_pwd_error));
                return;
            }
            dataArea = new byte[BLECLOSELOCKOPENLOCKPASSWORDLENGTH];
            System.arraycopy(closelockpwd, 0, dataArea, 0, closelockpwd.length);
        } else if (BLE_CMDTYPE_UPDATEBLELOCKOPENLOCKPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x30;
            //数据包格式:开锁密码8字节 	长度: 8byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x30"));
                return;
            }
            String openlockpwd = xiaodiDataSendCenter.getOpenlockpwd();
            if (openlockpwd == null || openlockpwd.length() != BLELOCKOPENLOCKPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.open_pwd_param_error));
                return;
            }
            dataArea = new byte[BLELOCKOPENLOCKPASSWORDLENGTH];
            System.arraycopy(openlockpwd.getBytes(), 0, dataArea, 0, openlockpwd.length());
        } else if (BLE_CMDTYPE_UPDATEBLELOCKMANAGEPASSWORD.equals(bleCmdType)) {
            cmd[0] = (byte) 0x31;
            //数据包格式:锁管理密码8字节  	长度: 8byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x31"));
                return;
            }
            String managepwd = xiaodiDataSendCenter.getManagepwd();
            if (managepwd == null || managepwd.length() != BLELOCKMANAGEPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.manage_pwd_length_error));
                return;
            }
            dataArea = new byte[BLELOCKMANAGEPASSWORDLENGTH];
            System.arraycopy(managepwd.getBytes(), 0, dataArea, 0, managepwd.length());
        } else if (BLE_CMDTYPE_REGISTERBLELOCKDEVICE.equals(bleCmdType)) {
            cmd[0] = (byte) 0x34;
            //数据包格式:为空	长度:0byte
        } else if (BLE_CMDTYPE_CLEARBLELOCKMOBILEACCOUNT.equals(bleCmdType)) {
            cmd[0] = (byte) 0x35;
//    		//数据包格式:手机账号ID (4byte),需保留的手机账户（首个账户）	长度:4byte
//    		if(dataSendCenter == null){
//    			ViewUtil.printLogAndTips(context, "0x35,参数错误");
//    			return false;
//    		}
//    		byte[] mobileaccount = dataSendCenter.getMobileaccount();
//    		byte[] usermobileaccountbytes = initUserMobileAccount(context, new String(mobileaccount));
//    		if(usermobileaccountbytes == null){
//    			ViewUtil.printLogAndTips(context, "手机账号初始化失败");
//    			return false;
//    		}
//    		dataArea = new byte[BLELOCKMOBILEACCOUNTLENGTH];
//    		System.arraycopy(usermobileaccountbytes, 0, dataArea, 0, usermobileaccountbytes.length);
            dataArea = new byte[]{0x25, (byte) 0x87, 0x45, 0x10};
        } else if (BLE_CMDTYPE_CHECKBLELOCKMANAGEPASSWORD.equals(bleCmdType)) {
            cmd[0] = 0x36;
            //数据包格式:锁管理密码8字节  	长度: 8byte
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x36"));
                return;
            }
            String managepwd = xiaodiDataSendCenter.getManagepwd();
            if (managepwd == null || managepwd.length() != BLELOCKMANAGEPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.manage_pwd_length_error));
                return;
            }
            dataArea = new byte[BLELOCKMANAGEPASSWORDLENGTH];
            System.arraycopy(managepwd.getBytes(), 0, dataArea, 0, managepwd.length());
        } else if (BLE_CMDTYPE_GETBLELOCKSOFTWAREVERSION.equals(bleCmdType)) {
            cmd[0] = 0x37;
        } else if (BLE_CMDTYPE_CONFIGBLELOCKALARMPWD.equals(bleCmdType)) {
            cmd[0] = 0x38;
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x38"));
                return;
            }
            byte[] alarmpwdbytes = xiaodiDataSendCenter.getAlarmpwd();
            if (alarmpwdbytes.length != BLELOCKALARMPASSWORDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.alarm_pwd_length_error));
                return;
            }
            dataArea = new byte[BLELOCKALARMPASSWORDLENGTH];
            System.arraycopy(alarmpwdbytes, 0, dataArea, 0, alarmpwdbytes.length);
        } else if (BLE_CMDTYPE_OPENBLELOCKENHANCE.equals(bleCmdType)) {
            cmd[0] = 0x39;
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x39"));
                return;
            }
            byte[] channelpwdbytes = xiaodiDataSendCenter.getChannelpwd();
            byte[] mobilebytes = xiaodiDataSendCenter.getMobileaccount();
            byte[] timebytes = xiaodiDataSendCenter.getTime();
            byte[] secretkeybytes = xiaodiDataSendCenter.getSecretkey();
            if (xiaodiDataSendCenter == null || channelpwdbytes == null || mobilebytes == null || timebytes == null
                    || channelpwdbytes.length == 0 || mobilebytes.length == 0 || timebytes.length == 0 || secretkeybytes == null || secretkeybytes.length == 0) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.param_check_error,"0x39"));
                return;
            }
            byte[] usermobileaccountbytes = initUserMobileAccount(new String(mobilebytes).replace(" ", ""));
            if (usermobileaccountbytes == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.mobile_init_error));
                return;
            }
            if (OPENBLELOCKENHANCEDATAAREALENGTH != channelpwdbytes.length + usermobileaccountbytes.length + timebytes.length) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.data_length_check_error));
                return;
            }
            if (OPENBLELOCKENHANCESECRETKEYLENGTH != secretkeybytes.length) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.secretkey_length_check_error));
                return;
            }
            byte[] tempData = new byte[OPENBLELOCKENHANCEDATAAREALENGTH];
            System.arraycopy(channelpwdbytes, 0, tempData, 0, channelpwdbytes.length);
            System.arraycopy(usermobileaccountbytes, 0, tempData, channelpwdbytes.length, usermobileaccountbytes.length);
            System.arraycopy(timebytes, 0, tempData, channelpwdbytes.length + usermobileaccountbytes.length, timebytes.length);
            byte[][] tempDataArray = new byte[3][8];
            for (int i = 0; i < tempData.length; i += 8) {
                tempDataArray[i / 8] = BLEByteUtil.getSubbytes(tempData, i, 8);
            }
            BLELogUtil.d(tag, "加密前，dataArea=" + BLEByteUtil.bytesToHexString(tempData));
            for (int i = 0; i < tempDataArray.length; i++) {
                for (int j = 0; j < tempDataArray[i].length; j++) {
                    tempDataArray[i][j] = (byte) (tempDataArray[i][j] ^ secretkeybytes[j]);
                }
            }
            BLELogUtil.d(tag, "加密后，tempDataArray[0]=" + BLEByteUtil.bytesToHexString(tempDataArray[0]));
            BLELogUtil.d(tag, "加密后，tempDataArray[1]=" + BLEByteUtil.bytesToHexString(tempDataArray[1]));
            BLELogUtil.d(tag, "加密后，tempDataArray[2]=" + BLEByteUtil.bytesToHexString(tempDataArray[2]));
            dataArea = new byte[OPENBLELOCKENHANCEDATAAREALENGTH];
            System.arraycopy(tempDataArray[0], 0, dataArea, 0, tempDataArray[0].length);
            System.arraycopy(tempDataArray[1], 0, dataArea, tempDataArray[0].length, tempDataArray[1].length);
            System.arraycopy(tempDataArray[2], 0, dataArea, tempDataArray[0].length + tempDataArray[1].length, tempDataArray[2].length);
        } else if (BLE_CMDTYPE_GETBLELOCKSECRETKEY.equals(bleCmdType)) {
            cmd[0] = 0x3A;
        } else if (BLE_CMDTYPE_SMARTKEYGETSECRETKEY.equals(bleCmdType)) {
            cmd[0] = 0x3D;
        } else if (BLE_CMDTYPE_REGISTERSMARTKEYGETSECRETKEY.equals(bleCmdType)) {
            cmd[0] = 0x3E;
        } else if (BLE_CMDTYPE_GENERATETEMPSECRETKEY.equalsIgnoreCase(bleCmdType)) {
            cmd[0] = 0x3F;
        } else if (BLE_CMDTYPE_WIFISTATUSCHECK.equalsIgnoreCase(bleCmdType)) {
            cmd[0] = 0x40;
        } else if (BLE_CMDTYPE_WIFISTATUSTOGGLE.equalsIgnoreCase(bleCmdType)) {
            cmd[0] = 0x41;
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0x41"));
                return;
            }
            dataArea = new byte[1];
            if (xiaodiDataSendCenter.getEnbleWifi()) {
                dataArea[0] = 0x00;
            } else {
                dataArea[0] = 0x11;
            }
        } else if (BLE_CMDTYPE_DISCONNECTBLEDEVICE.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF0;
        } else if (BLE_CMDTYPE_WIFICONFIGSTART.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF2;
        } else if (BLE_CMDTYPE_WIFICONFIGFINISH.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF3;
        } else if (BLE_CMDTYPE_UPDATEBLEDEVICEPROGRAMMINGMODEL.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF4;
        } else if (BLE_CMDTYPE_CONFIGBLELOCKWIFIPARAMS.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF5;
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0xF5"));
                return;
            }
            byte[] wifissidbytes = xiaodiDataSendCenter.getWifissid();
            byte[] wifipasswordbytes = xiaodiDataSendCenter.getWifipassword();
            if (wifissidbytes.length != BLELOCKWIFISSIDLENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.wifi_length_error,"ssid"));
                return;
            }
            if (wifipasswordbytes.length != BLELOCKWIFIPASSWORDLENTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.wifi_length_error,"password"));
                return;
            }
            dataArea = new byte[BLELOCKWIFISSIDLENGTH + BLELOCKWIFIPASSWORDLENTH];
            System.arraycopy(wifissidbytes, 0, dataArea, 0, wifissidbytes.length);
            System.arraycopy(wifipasswordbytes, 0, dataArea, wifissidbytes.length, wifipasswordbytes.length);
        } else if (BLE_CMDTYPE_OPENLOGUPLOADTOGGLE.equals(bleCmdType)) {
            cmd[0] = (byte) 0xF9;
            if (xiaodiDataSendCenter == null) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.cmd_param_error,"0xF9"));
                return;
            }
            byte[] openloguploadtogglebytes = xiaodiDataSendCenter.getOpenlogtoggle();
            if (openloguploadtogglebytes.length != OPENLOCKLOGUPLOADTOGGLELENGTH) {
                initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.open_lock_record_data_length_error));
                return;
            }
            dataArea = new byte[OPENLOCKLOGUPLOADTOGGLELENGTH];
            System.arraycopy(openloguploadtogglebytes, 0, dataArea, 0, openloguploadtogglebytes.length);
        } else {
            initBleAreaDataListener.initBleAreaDataFailure(appContext.getString(R.string.ble_data_type_error,bleCmdType + appContext.getString(R.string.unknown_order)));
            return;
        }
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
        dataSend = new XIAODIDataSend();
        dataSend.setPackageHead(packageHead);
        dataSend.setPackageAttribute(packageAttribute);
        dataSend.setCmd(cmd);
        dataSend.setDataAreaLength(dataAreaLength);
        dataSend.setDataArea(dataArea);
        dataSend.setCrc(crc);
        initBleAreaDataListener.initBleAreaDataSuccess(getDataSendBytes());
    }

    //返回封包待发送的数据
    public byte[] getDataSendBytes() {
        return bleDataSend;
    }

    //返回封装的封包待发送的数据
    public XIAODIDataSend getDataSend() {
        return dataSend;
    }

    public byte[] getCmd() {
        return cmd;
    }

    //接收到蓝牙锁设备返回的数据
    private byte[] bleDataReceived = null;
    //封装接收到蓝牙锁设备返回的数据
    private XIAODIDataReceived dataReceived = null;
    //蓝牙锁设备返回的数据的最小长度
    private final static int BLELOCKDEVICERETURNDATAMINLENGTH = 8;
    //应答(1字节)
    private byte[] ack = new byte[1];

    //解析蓝牙锁设备返回的数据
    private void analysisBLEReturnData() {
        if (bleDataReceived == null) {
            BLELogUtil.d(tag, "蓝牙设备返回数据为空");
            return;
        }
        if (bleDataReceived.length < BLELOCKDEVICERETURNDATAMINLENGTH) {
            BLELogUtil.d(tag, "当前蓝牙锁设备协议规定，返回数据的长度最小为" + BLELOCKDEVICERETURNDATAMINLENGTH + "位,但是当前蓝牙锁设备返回的数据长度为" + bleDataReceived.length);
            return;
        }
        packageHead = BLEByteUtil.getSubbytes(bleDataReceived, 0, 1);
        BLELogUtil.d(tag, "packageHead=" + BLEByteUtil.bytesToHexString(packageHead));
        packageAttribute = BLEByteUtil.getSubbytes(bleDataReceived, 1, 1);
        BLELogUtil.d(tag, "packageAttribute=" + BLEByteUtil.bytesToHexString(packageAttribute));
        cmd = BLEByteUtil.getSubbytes(bleDataReceived, 2, 1);
        BLELogUtil.d(tag, "cmd=" + BLEByteUtil.bytesToHexString(cmd));
        dataAreaLength = BLEByteUtil.getSubbytes(bleDataReceived, 3, 2);
        BLELogUtil.d(tag, "dataAreaLength=" + BLEByteUtil.bytesToHexString(dataAreaLength));
        ack = BLEByteUtil.getSubbytes(bleDataReceived, 5, 1);
        BLELogUtil.d(tag, "ack=" + BLEByteUtil.bytesToHexString(ack));
        dataArea = BLEByteUtil.getSubbytes(bleDataReceived, 6, bleDataReceived.length - packageHead.length - packageAttribute.length - cmd.length - dataAreaLength.length - ack.length - crc.length);
        BLELogUtil.d(tag, "dataArea=" + BLEByteUtil.bytesToHexString(dataArea));
        crc = BLEByteUtil.getSubbytes(bleDataReceived, bleDataReceived.length - 2, 2);
        BLELogUtil.d(tag, "crc=" + BLEByteUtil.bytesToHexString(crc));
    }

    //初始化接受包对象
    private void initBleDataReceived() {
        dataReceived = new XIAODIDataReceived();
        dataReceived.setPackageHead(packageHead);
        dataReceived.setPackageAttribute(packageAttribute);
        dataReceived.setCmd(cmd);
        dataReceived.setDataAreaLength(dataAreaLength);
        dataReceived.setAck(ack);
        dataReceived.setDataArea(dataArea);
        dataReceived.setCrc(crc);
    }

    //返回数据包数据
    public byte[] getDataArea() {
        return dataArea;
    }

    //返回数据包数据
    public XIAODIDataReceived getDataReceived() {
        return dataReceived;
    }

    //返回应答
    public byte[] getAck() {
        return ack;
    }

    //通过解析蓝牙锁设备返回的数据,判断操作是否成功
    public boolean checkBLEOperateStatusExceptACK() {
        if (dataReceived == null) {
            BLELogUtil.d(tag, "蓝牙设备返回的数据包解析失败");
            return false;
        }
        byte[] packageHead = dataReceived.getPackageHead();
        byte[] packageAttribute = dataReceived.getPackageAttribute();
        byte[] cmd = dataReceived.getCmd();
        byte[] dataAreaLength = dataReceived.getDataAreaLength();
        byte[] ack = dataReceived.getAck();
        byte[] dataArea = dataReceived.getDataArea();
        byte[] crc = dataReceived.getCrc();
        //命令字在BLEHandler中最先校验
        //验证校验位
        byte[] crcself = XIAODIBLECRCUtil.getCRCByteData(bleDataReceived);
        if (crc == null || !BLEByteUtil.compareTwoBytes(crc, crcself)) {
            BLELogUtil.d(tag, "蓝牙设备返回的数据包校验位校验失败");
            return false;
        }
//		//验证应答
//		if(ack == null || ack.length != 1 || (ack[0] != 0x00 && ack[0] != 0x01)){
//			BLELogUtil.d(tag, "蓝牙设备返回数据包应答参数不正确,ack=" + BLEByteUtil.bytesToHexString(ack));
//			return false;
//		}
        if (ack[0] != 0x00) {
            BLELogUtil.d(tag, "0x" + BLEByteUtil.bytesToHexString(cmd) + ",应答失败");
            return false;
        }
        //验证数据区长度与实际数据区是否一致
        if ((dataAreaLength == null || dataAreaLength.length != 2)
                || ((dataArea == null || dataArea.length == 0) && (((dataAreaLength[0] << 8) + dataAreaLength[1]) != 0x0001))
                || ((dataArea != null && dataArea.length != 0) && (((dataAreaLength[0] << 8) + dataAreaLength[1]) != dataArea.length + 1))) {
            BLELogUtil.d(tag, "蓝牙设备返回的数据包数据区长度指示的长度与实际数据区数据长度不匹配");
            return false;
        }
        //验证数据区
//		if(((cmd[0] != 0x03 && cmd[0] != 0x20 && cmd[0] != 0x27 && cmd[0] != 0x34) && dataArea != null)
//				|| (cmd[0] == 0x03 && (dataArea == null || dataArea.length != 2 || (dataArea[1] != 0x00 && dataArea[1] != 0x0F && dataArea[1] != 0xF0 && dataArea[1] != 0xFF)))
//				|| (cmd[0] == 0x20 && (dataArea == null || dataArea.length != 12))
//				|| (cmd[0] == 0x27 && (dataArea == null || dataArea.length != 4 || BLEByteUtil.lessThan8bytesToLongInt(dataAreaLength) < 1) || BLEByteUtil.lessThan8bytesToLongInt(dataAreaLength) > 9999)
//				|| (cmd[0] == 0x34 && (dataArea == null || dataArea.length != 16))){
//			BLELogUtil.d(tag, "0x" + BLEByteUtil.bytesToHexString(cmd) +  ",蓝牙返回数据区校验失败");
//			return false;
//		}
//		if(((cmd[0] != 0x20 && cmd[0] != 0x27 && cmd[0] != 0x34) && dataArea != null)
//				|| (cmd[0] == 0x20 && (dataArea == null || dataArea.length != 12))
//				|| (cmd[0] == 0x27 && (dataArea == null || dataArea.length != 4 || BLEByteUtil.lessThan8bytesToLongInt(dataAreaLength) < 1) || BLEByteUtil.lessThan8bytesToLongInt(dataAreaLength) > 9999)
//				|| (cmd[0] == 0x34 && (dataArea == null || dataArea.length != 16))){
//			BLELogUtil.d(tag, "0x" + BLEByteUtil.bytesToHexString(cmd) +  ",蓝牙返回数据区校验失败");
//			return false;
//		}
        //验证包头
        if (packageHead == null || packageHead.length != 1 || packageHead[0] != (byte) 0xFE) {
            BLELogUtil.d(tag, "蓝牙设备返回数据包头不正确,packageHead=" + BLEByteUtil.bytesToHexString(packageHead));
            return false;
        }
        //验证包属性
        if (packageAttribute == null || packageAttribute.length != 1 || packageAttribute[0] != 0x09) {
            BLELogUtil.d(tag, "蓝牙设备返回数据包属性不正确,packageAttribute=" + BLEByteUtil.bytesToHexString(packageAttribute));
            return false;
        }
        BLELogUtil.d(tag, "0x" + BLEByteUtil.bytesToHexString(cmd) + ",应答成功");
        return true;
    }

    //添加指纹协议返回数据特殊处理,验证除应答以外的数据
    public boolean checkBLEOperateAddFingerStatusExceptACK() {
        if (dataReceived == null) {
            BLELogUtil.d(tag, "蓝牙设备返回的数据包解析失败");
            return false;
        }
        byte[] packageHead = dataReceived.getPackageHead();
        byte[] packageAttribute = dataReceived.getPackageAttribute();
        byte[] cmd = dataReceived.getCmd();
        byte[] dataAreaLength = dataReceived.getDataAreaLength();
        byte[] ack = dataReceived.getAck();
        byte[] dataArea = dataReceived.getDataArea();
        byte[] crc = dataReceived.getCrc();
        //先验证命令字
        if (cmd == null || cmd.length == 0 || cmd[0] != 0x24) {
            BLELogUtil.d(tag, "0x24, 蓝牙设备返回数据包指令与发送的指令不匹配");
            return false;
        }
        //验证校验位
        byte[] crcself = XIAODIBLECRCUtil.getCRCByteData(bleDataReceived);
        if (crc == null || !BLEByteUtil.compareTwoBytes(crc, crcself)) {
            BLELogUtil.d(tag, "0x24, 蓝牙设备返回的数据包校验位校验失败");
            return false;
        }
        //验证数据区长度与实际数据区是否一致
        if ((dataAreaLength == null || dataAreaLength.length != 2)
                || ((dataArea == null || dataArea.length == 0) && (((dataAreaLength[0] << 8) + dataAreaLength[1]) != 0x0001))
                || ((dataArea != null && dataArea.length != 0) && (((dataAreaLength[0] << 8) + dataAreaLength[1]) != dataArea.length + 1))) {
            BLELogUtil.d(tag, "0x24, 蓝牙设备返回的数据包数据区长度指示的长度与实际数据区数据长度不匹配");
            return false;
        }
        //验证数据区,同时条件附带验证应答
        if ((ack == null || ack.length != 1 || (ack[0] != 0x00 && ack[0] != 0x01 && ack[0] != 0x02)) || (ack[0] == 0x00 && (dataArea == null || dataArea.length != 5))
                || (ack[0] == 0x01 && (dataArea != null)) || (ack[0] == 0x02 && (dataArea == null || dataArea.length != 1))) {
            BLELogUtil.d(tag, "0x" + BLEByteUtil.bytesToHexString(cmd) + "0x24, 蓝牙返回数据区校验失败");
            return false;
        }
        //验证包头
        if (packageHead == null || packageHead.length != 1 || packageHead[0] != (byte) 0xFE) {
            BLELogUtil.d(tag, "蓝牙设备返回数据包头不正确");
            return false;
        }
        //验证包属性
        if (packageAttribute == null || packageAttribute.length != 1 || packageAttribute[0] != 0x09) {
            BLELogUtil.d(tag, "蓝牙设备返回数据包属性不正确");
            return false;
        }
        return true;
    }

    //添加指纹协议返回数据特殊处理,验证除应答以外的数据
    public boolean checkBLEManagePasswordStatusExceptACK() {
        if (dataReceived == null) {
            BLELogUtil.d(tag, "蓝牙设备返回的数据包解析失败");
            return false;
        }
        byte[] packageHead = dataReceived.getPackageHead();
        byte[] packageAttribute = dataReceived.getPackageAttribute();
        byte[] cmd = dataReceived.getCmd();
//			byte[] dataAreaLength = dataReceived.getDataAreaLength();
//			byte[] ack = dataReceived.getAck();
//			byte[] dataArea = dataReceived.getDataArea();
        byte[] crc = dataReceived.getCrc();
        //先验证命令字
        if (cmd == null || cmd.length == 0 || cmd[0] != 0x36) {
            BLELogUtil.d(tag, "0x36, 蓝牙设备返回数据包指令与发送的指令不匹配");
            return false;
        }
        //验证校验位
        byte[] crcself = XIAODIBLECRCUtil.getCRCByteData(bleDataReceived);
        if (crc == null || !BLEByteUtil.compareTwoBytes(crc, crcself)) {
            BLELogUtil.d(tag, "0x36, 蓝牙设备返回的数据包校验位校验失败");
            return false;
        }
//			//验证数据区长度与实际数据区是否一致
//			if((dataAreaLength == null || dataAreaLength.length != 2)
//					|| ((dataArea == null || dataArea.length == 0) && (((dataAreaLength[0]<<8) + dataAreaLength[1]) != 0x0001))
//					|| ((dataArea != null && dataArea.length != 0) && (((dataAreaLength[0]<<8) + dataAreaLength[1]) != dataArea.length + 1))){
//				BLELogUtil.d(tag, "0x36, 蓝牙设备返回的数据包数据区长度指示的长度与实际数据区数据长度不匹配");
//				return false;
//			}
//			//验证数据区,同时条件附带验证应答
//			if((ack == null || ack.length != 1 || (ack[0] != 0x00 && ack[0] != 0x01 && ack[0] != 0x02)) || (ack[0] == 0x00 && (dataArea == null || dataArea.length != 5))
//					|| (ack[0] ==  0x01 && (dataArea != null)) || (ack[0] == 0x02 && (dataArea == null || dataArea.length != 1))){
//				BLELogUtil.d(tag, "0x" + BLEByteUtil.bytesToHexString(cmd) +  "0x24, 蓝牙返回数据区校验失败");
//				return false;
//			}
        //验证包头
        if (packageHead == null || packageHead.length != 1 || packageHead[0] != (byte) 0xFE) {
            BLELogUtil.d(tag, "蓝牙设备返回数据包头不正确");
            return false;
        }
        //验证包属性
        if (packageAttribute == null || packageAttribute.length != 1 || packageAttribute[0] != 0x09) {
            BLELogUtil.d(tag, "蓝牙设备返回数据包属性不正确");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        byte[] data = new byte[]{0x09, 0x34, 0x00, 0x11, 0x00, 0x53, 0x37, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x31, (byte) 0xDB, (byte) 0x9F, (byte) 0xAE, 0x6A, 0x1E, 0x04};
        int sum = 0;
        for (byte aData : data) {
            sum += aData;
        }
        byte[] sumbyte = new byte[2];
        sumbyte[0] = (byte) ((sum >> 8) & 0x000000FF);
        sumbyte[1] = (byte) sum;
        BLELogUtil.i("data=" + BLEByteUtil.bytesToHexString(data) + ",crc=" + BLEByteUtil.bytesToHexString(sumbyte));
    }
}