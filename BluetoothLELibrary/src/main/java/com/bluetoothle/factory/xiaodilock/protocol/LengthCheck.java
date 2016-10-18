package com.bluetoothle.factory.xiaodilock.protocol;

/**
 * Created by dessmann on 16/10/18.
 * 长度校验
 */

public class LengthCheck {
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
}
