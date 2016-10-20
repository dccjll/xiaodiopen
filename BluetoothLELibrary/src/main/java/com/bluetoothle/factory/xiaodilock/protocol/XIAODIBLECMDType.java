package com.bluetoothle.factory.xiaodilock.protocol;

/**
 * Created by dessmann on 16/10/18.
 * 小嘀发送命令类型
 */

public class XIAODIBLECMDType {
    public final static String BLE_CMDTYPE_CHECKCHANNALPASSWORD = "com.dsm.ble.BLE_CMDTYPE_CHECKCHANNALPASSWORD";                                   //验证信道密码 (0x01)
    public final static String BLE_CMDTYPE_UPDATECHANNALPASSWORD = "com.dsm.ble.BLE_CMDTYPE_UPDATECHANNALPASSWORD";                                 //修改信道密码 (0x02)
    public final static String BLE_CMDTYPE_OPENBLELOCK = "com.dsm.ble.BLE_CMDTYPE_OPENBLELOCK";                                                     //手机开锁验证 (0x03)
    public final static String BLE_CMDTYPE_GETBLELOCKINFO = "com.dsm.ble.BLE_CMDTYPE_GETBLELOCKINFO";                                               //锁存储信息获取 (0x20)
    public final static String BLE_CMDTYPE_SETBLELOCKOPENTYPE = "com.dsm.ble.BLE_CMDTYPE_SETBLELOCKOPENTYPE";                                       //开锁方式设置 (0x22)
    public final static String BLE_CMDTYPE_SYNCBLELOCKTIME = "com.dsm.ble.BLE_CMDTYPE_SYNCBLELOCKTIME";                                             //时间同步 (0x23)
    public final static String BLE_CMDTYPE_ADDBLELOCKFINGER = "com.dsm.ble.BLE_CMDTYPE_ADDBLELOCKFINGER";                                           //增加指纹 (0x24)
    public final static String BLE_CMDTYPE_ADDBLELOCKMOBILEACCOUNT = "com.dsm.ble.BLE_CMDTYPE_ADDBLELOCKMOBILEACCOUNT";                             //增加手机账号 (0x25)
    public final static String BLE_CMDTYPE_ADDSMARTKEYAUTH = "com.dsm.ble.BLE_CMDTYPE_ADDSMARTKEYAUTH";                                             //增加智能钥匙授权 (0x26)
    public final static String BLE_CMDTYPE_ADDSMARTKEY = "com.dsm.ble.BLE_CMDTYPE_ADDSMARTKEY";                                                     //增加智能钥匙 (0x27)
    public final static String BLE_CMDTYPE_DELETEBLELOCKFINGER = "com.dsm.ble.BLE_CMDTYPE_DELETEBLELOCKFINGER";                                     //删除指纹 (0x28)
    public final static String BLE_CMDTYPE_DELETEBLELOCKMOBILEACCOUNT = "com.dsm.ble.BLE_CMDTYPE_DELETEBLELOCKMOBILEACCOUNT";                       //删除手机账号 (0x29)
    public final static String BLE_CMDTYPE_DELETESMARTKEY = "com.dsm.ble.BLE_CMDTYPE_DELETESMARTKEY";                                               //删除智能钥匙 (0x2A)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKFINGERATTRIBUTE = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKFINGERATTRIBUTE";                   //指纹属性修改 (0x2B)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKMOBILEACCOUNTATTRIBUTE = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKMOBILEACCOUNTATTRIBUTE";     //手机账号属性修改 (0x2C)
    public final static String BLE_CMDTYPE_UPDATESMARTKEYATTRIBUTE = "com.dsm.ble.BLE_CMDTYPE_UPDATESMARTKEYATTRIBUTE";                             //智能钥匙属性修改 (0x2D)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKNAME = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKNAME";                                         //锁名称设置 (0x2E)
    public final static String BLE_CMDTYPE_CLOSELOCKOPENPASSWORD = "com.dsm.ble.BLE_CMDTYPE_CLOSELOCKOPENPASSWORD";                                 //开锁密码设置 (0x00)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKOPENLOCKPASSWORD = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKOPENLOCKPASSWORD";                 //开锁密码设置 (0x30)
    public final static String BLE_CMDTYPE_UPDATEBLELOCKMANAGEPASSWORD = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLELOCKMANAGEPASSWORD";                     //锁管理密码设置 (0x31)
    public final static String BLE_CMDTYPE_REGISTERBLELOCKDEVICE = "com.dsm.ble.BLE_CMDTYPE_REGISTERBLELOCKDEVICE";                                 //设备注册 (0x34)
    public final static String BLE_CMDTYPE_CLEARBLELOCKMOBILEACCOUNT = "com.dsm.ble.BLE_CMDTYPE_CLEARBLELOCKMOBILEACCOUNT";                         //清空用户(0x35)
    public final static String BLE_CMDTYPE_CHECKBLELOCKMANAGEPASSWORD = "com.dsm.ble.BLE_CMDTYPE_CHECKBLELOCKMANAGEPASSWORD";                       //锁管理密码验证 (0x36)
    public final static String BLE_CMDTYPE_GETBLELOCKSOFTWAREVERSION = "com.dsm.ble.BLE_CMDTYPE_GETBLELOCKSOFTWAREVERSION";                         //锁固件版本获取 (0x37)
    public final static String BLE_CMDTYPE_CONFIGBLELOCKALARMPWD = "com.dsm.ble.BLE_CMDTYPE_CONFIGBLELOCKALARMPWD";                                 //报警密码设置 (0x38)
    public final static String BLE_CMDTYPE_OPENBLELOCKENHANCE = "com.dsm.ble.BLE_CMDTYPE_OPENBLELOCKENHANCE";                                       //手机开锁验证加密版 (0x39)
    public final static String BLE_CMDTYPE_GETBLELOCKSECRETKEY = "com.dsm.ble.BLE_CMDTYPE_GETBLELOCKSECRETKEY";                                     //手机秘钥获取 (0x3A)
    public final static String BLE_CMDTYPE_SMARTKEYGETSECRETKEY = "com.dsm.ble.BLE_CMDTYPE_SMARTKEYGETSECRETKEY";                                   //智能钥匙授权获取随机数秘钥(0x3D)
    public final static String BLE_CMDTYPE_REGISTERSMARTKEYGETSECRETKEY = "com.dsm.ble.BLE_CMDTYPE_REGISTERSMARTKEYGETSECRETKEY";                   //锁注册秘钥获取 (0x3E)
    public final static String BLE_CMDTYPE_GENERATETEMPSECRETKEY = "com.dsm.ble.BLE_CMDTYPE_GENERATETEMPSECRETKEY";                                 //产生临时密钥(0x3F)
    public final static String BLE_CMDTYPE_WIFISTATUSCHECK = "com.dsm.ble.BLE_CMDTYPE_WIFISTATUSCHECK";                                             //WIFI连接状态检测(0x40)
    public final static String BLE_CMDTYPE_WIFISTATUSTOGGLE = "com.dsm.ble.BLE_CMDTYPE_WIFISTATUSTOGGLE";                                           //WIFI功能开启关闭设置(0x41)
    public final static String BLE_CMDTYPE_DISCONNECTBLEDEVICE = "com.dsm.ble.BLE_CMDTYPE_DISCONNECTBLEDEVICE";                                     //断开蓝牙连接(0xF0)
    public final static String BLE_CMDTYPE_WIFICONFIGSTART = "com.dsm.ble.BLE_CMDTYPE_WIFICONFIGSTART";                                             //WIFI设置启动 (0xF2)
    public final static String BLE_CMDTYPE_WIFICONFIGFINISH = "com.dsm.ble.BLE_CMDTYPE_WIFICONFIGFINISH";                                           //WIFI设置结束 (0xF3)
    public final static String BLE_CMDTYPE_UPDATEBLEDEVICEPROGRAMMINGMODEL = "com.dsm.ble.BLE_CMDTYPE_UPDATEBLEDEVICEPROGRAMMINGMODEL";             //更新程序模式(0xF4)
    public final static String BLE_CMDTYPE_CONFIGBLELOCKWIFIPARAMS = "com.dsm.ble.BLE_CMDTYPE_CONFIGBLELOCKWIFIPARAMS";                             //WIFI设置参数（0xF5）
    public final static String BLE_CMDTYPE_OPENLOGUPLOADTOGGLE = "com.dsm.ble.BLE_CMDTYPE_OPENLOGUPLOADTOGGLE";                                     //开锁记录实时上传功能开启与关闭切换(0xF9)
}
