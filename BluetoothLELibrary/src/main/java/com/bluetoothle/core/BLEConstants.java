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
        public final static String BLEInit = "BLEInit";                                                                        //BLE环境初始化异常
        public final static String NotSupportBLE = "NotSupportBLE";                            //当前系统版本不支持蓝牙BLE
        public final static String BluetoothManager = "BluetoothManager";                    //蓝牙管理服务异常
        public final static String BluetoothAdapter = "BluetoothAdapter";                    //蓝牙适配器异常
        public final static String OpenBluetoothSleep = "OpenBluetoothSleep";                       //打开BLE过程中线程休眠异常
        public final static String MacAddress = "MacAddress";                          //mac地址不正确
        public final static String MacAddressList = "MacAddressList";                      //mac地址列表不正确
        public final static String Write_Service_UUID = "Write_Service_UUID";                  //写数据服务uuid异常
        public final static String Write_Characteristics_UUID = "Write_Characteristics_UUID";          //写数据特征uuid异常
        public final static String Notification_Service_UUID = "Notification_Service_UUID";           //通知服务uuid异常
        public final static String Notification_Characteristics_UUID = "Notification_Characteristics_UUID";   //通知特征uuid异常
        public final static String Descriptor_Notification_UUID = "Descriptor_Notification_UUID";          //通知的特征描述uuid异常
        public final static String BLEScanning = "BLEScanning";                              //正在扫描
        public final static String NotFoundDevice = "NotFoundDevice";                           //没有发现目标设备
        public final static String UUIDArrays = "UUIDArrays";                          //uuid数组不正确
        public final static String ReceivedBLEStackExceptionCode = "ReceivedBLEStackExceptionCode";                     //收到底层协议栈异常信息
        public final static String BLEConext = "BLEConext";                           //蓝牙连接上下文异常
        public final static String Device_Address = "Device_Address";                       //验证连接设备地址或蓝牙对象异常
        public final static String Disconnect = "Disconnect";                               //正常断开的异常消息
        public final static String Connect = "Connect";                                  //连接异常
        public final static String BluetoothGatt = "BluetoothGatt";                       //蓝牙连接服务器异常
        public final static String BluetoothGattService = "BluetoothGattService";                //服务异常
        public final static String BluetoothGattCharacteristic = "BluetoothGattCharacteristic";         //特征异常
        public final static String BluetoothGattDescriptor = "BluetoothGattDescriptor";             //特征描述异常
        public final static String FindService = "FindService";                              //找服务异常
        public final static String GatService = "GatService";                             //获取服务异常
        public final static String SetCharacteristicNotification = "SetCharacteristicNotification";            //设置特征通知异常
        public final static String WriteDescriptor = "WriteDescriptor";                          //写特征描述异常
        public final static String CheckBLEDataError = "CheckBLEDataError";                             //验证发送数据异常
        public final static String WriteDataError = "WriteDataError";                                //写数据异常
        public final static String OnBLEResponse = "OnBLEResponse";               //验证接收数据监听器异常
        public final static String Scann = "Scann";                            //发生扫描异常
    }
}
