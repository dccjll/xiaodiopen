package com.bluetoothle.core;

/**
 * Created by dessmann on 16/10/18.
 * 蓝牙连接参数配置
 */

public class BLEConfig {
    public final static Integer MaxScanCount = 2;//最多扫描次数
    public final static Integer MaxConnectCount = 3;//最多连接次数
    public final static Integer MaxFindServiceCount = 2;//最多找服务次数
    public final static Integer MaxOpenNotificationCount = 2;//最多打开通知次数

    public final static Integer MaxReconnectCountWhenDisconnectedOnFindService = 1;//找服务时断开连接最大重连的次数
    public final static Integer MaxReconnectCountWhenDisconnectedOnOpenNotification = 1;//打开通知时断开连接最大重连的次数

    public final static Integer MaxWaitDisconnectTimeInterval = 2*60*1000;//蓝牙连接上之后，多久不通讯将主动断开连接的间隔时间
}
