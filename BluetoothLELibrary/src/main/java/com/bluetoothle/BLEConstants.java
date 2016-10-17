package com.bluetoothle;

/**
 * Created by dessmann on 16/7/26.
 * 蓝牙框架常量
 */
public class BLEConstants {

    /**
     * 蓝牙初始化异常
     */
    public final static class InitError {
        public final static Integer InitError_NotSupportBLE = 0x2001;//当前系统版本不支持蓝牙BLE
        public final static Integer InitError_GetBluetoothManager = 0x2002;//获取蓝牙管理服务异常
        public final static Integer InitError_GetBluetoothAdapter = 0x2003;//获取蓝牙适配器异常
        public final static Integer InitError_TimeoutOpenBLE = 0x2004;//打开BLE超时
        public final static Integer InitError_OpenBLESleep = 0x2005;//打开BLE过程中线程休眠异常
    }

    /**
     * 构建请求数据校验异常
     */
    public final static class RequestError {
        public final static Integer RequestCode_InvalidTargetDeviceMacAddress = 0x1001;//mac地址不正确
        public final static Integer RequestCode_InvalidService_uuid_write = 0x1002;//写数据服务uuid异常
        public final static Integer RequestCode_InvalidCharacteristics_uuid_write = 0x1003;//写数据特征uuid异常
        public final static Integer RequestCode_InvalidService_uuid_notification = 0x1004;//通知服务uuid异常
        public final static Integer RequestCode_InvalidCharacteristics_uuid_notification = 0x1005;//通知特征uuid异常
        public final static Integer RequestCode_InvalidCharacteristics_descriptor_uuid_notification = 0x1006;//通知的特征描述uuid异常
        public final static Integer RequestCode_InvalidData = 0x1007;
    }

    /**
     * 扫描设备异常
     */
    public final static class ScanError{
        public final static Integer ScanError_isScaning = 0x3001;//正在扫描
        public final static Integer ScanError_NotFoundDevice = 0x3002;//没有发现目标设备
        public final static Integer ScanError_errorMacAddress = 0x3003;//mac地址不正确
        public final static Integer ScanError_errorMacAddressList = 0x3004;//mac地址列表不正确
        public final static Integer ScanError_errorServiceUUIDs = 0x3005;//uuid数组不正确
    }

    /**
     * 连接设备异常
     */
    public final static class ConnectError{
        public final static Integer ConnectError_ReceivedExceptionStackCodeError = 0x4001;//收到底层协议栈异常信息
        public final static Integer ConnectError_BLEConextError = 0x4002;//蓝牙连接上下文异常
        public final static Integer ConnectError_BLEDeviceOrBluetoothAdapterOrTargetMacAddressError = 0x4003;//蓝牙设备或目标设备mac地址异常
        public final static Integer ConnectError_MaxConnectNumError = 0x4004;//最大连接重试次数异常
        public final static Integer ConnectError_ConnectFail = 0x4005;//连接失败
    }

    /**
     * 找服务异常
     */
    public final static class FindServiceError{
        public final static Integer FindServiceError_BluetoothGattError = 0x5001;
        public final static Integer FindServiceError_TargetServiceUUIDError = 0x5002;
        public final static Integer FindServiceError_FindServiceFail = 0x5003;
        public final static Integer FindServiceError_Disconnect = 0x5004;
        public final static Integer FindServiceError_ReceivedExceptionStackCodeError = 0x5005;
        public final static Integer FindServiceError_ServiceListNotCantainsTargetService = 0x5006;
    }

}
