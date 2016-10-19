package com.bluetoothle.core;

/**
 * Created by dessmann on 16/7/26.
 * 蓝牙框架常量
 */
public class BLEConstants {

    /**
     * 蓝牙操作异常类,编号从0x1001开始
     */
    public final static class Error {
        public final static Integer NotSupportBLEError = 0x1001;                            //当前系统版本不支持蓝牙BLE
        public final static Integer CheckBluetoothManagerError = 0x1002;                    //蓝牙管理服务异常
        public final static Integer CheckBluetoothAdapterError = 0x1003;                    //蓝牙适配器异常
        public final static Integer OpenBluetoothTimeoutError = 0x1004;                     //打开蓝牙超时
        public final static Integer OpenBluetoothSleepError = 0x1005;                       //打开BLE过程中线程休眠异常
        public final static Integer CheckMacAddressError = 0x1006;                          //mac地址不正确
        public final static Integer CheckMacAddressListError = 0x1007;                      //mac地址列表不正确
        public final static Integer CheckService_UUID_WriteError = 0x1008;                  //写数据服务uuid异常
        public final static Integer CheckCharacteristics_UUID_WriteError = 0x1009;          //写数据特征uuid异常
        public final static Integer CheckService_UUID_NotificationError = 0x100A;           //通知服务uuid异常
        public final static Integer CheckCharacteristics_UUID_NotificationError = 0x100B;   //通知特征uuid异常
        public final static Integer CheckCharacteristics_UUID_Descriptor = 0x100C;          //通知的特征描述uuid异常
        public final static Integer BLEScanningError = 0x100D;                              //正在扫描
        public final static Integer NotFoundDeviceError = 0x100E;                           //没有发现目标设备
        public final static Integer CheckUUIDArraysError = 0x100F;                          //uuid数组不正确
        public final static Integer ReceivedBLEStackCodeError = 0x1010;                     //收到底层协议栈异常信息
        public final static Integer CheckBLEConextError = 0x1011;                           //蓝牙连接上下文异常
        public final static Integer CheckConnectDeviceError = 0x1012;                       //验证连接设备地址或蓝牙对象异常
        public final static Integer DisconnectError = 0x1013;                               //正常断开的异常消息
        public final static Integer ConnectError = 0x1014;                                  //连接异常
        public final static Integer CheckBluetoothGattError = 0x1015;                       //蓝牙连接服务器异常
        public final static Integer CheckBluetoothGattServiceError = 0x1016;                //服务异常
        public final static Integer CheckBluetoothGattCharacteristicError = 0x1017;         //特征异常
        public final static Integer CheckBluetoothGattDescriptorError = 0x1018;             //特征描述异常
        public final static Integer FindServiceError = 0x1019;                              //找服务异常
        public final static Integer GattServicesError = 0x101A;                             //获取服务异常
        public final static Integer SetCharacteristicNotificationError = 0x101B;            //设置特征通知异常
        public final static Integer WriteDescriptorError = 0x101C;                          //写特征描述异常
        public final static Integer OpenNotificationError = 0x101D;                         //打开通知异常
        public final static Integer CheckBLEDataError = 0x101E;                             //验证发送数据异常
        public final static Integer WriteDataError = 0x101F;                                //写数据异常
        public final static Integer CheckOnBLEResponseListenerError = 0x1020;               //验证接收数据监听器异常
        public final static Integer OccurScanningError = 0x1021;                            //发生扫描异常
    }
}
