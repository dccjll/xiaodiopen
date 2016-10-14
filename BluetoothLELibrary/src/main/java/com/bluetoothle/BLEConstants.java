package com.bluetoothle;

/**
 * Created by dessmann on 16/7/26.
 * 蓝牙框架常量
 */
public class BLEConstants {

    /**
     * 蓝牙初始化异常
     */
    public static final class InitError {
        public static final Integer InitError_NotSupportBLE = 0x2001;//当前系统版本不支持蓝牙BLE
        public static final Integer InitError_GetBluetoothManager = 0x2002;//获取蓝牙管理服务异常
        public static final Integer InitError_GetBluetoothAdapter = 0x2003;//获取蓝牙适配器异常
        public static final Integer InitError_TimeoutOpenBLE = 0x2004;//打开BLE超时
        public static final Integer InitError_OpenBLESleep = 0x2005;//打开BLE过程中线程休眠异常
    }

    /**
     * 构建请求数据校验异常
     */
    public static final class RequestError {
        public static final Integer RequestCode_InvalidTargetDeviceMacAddress = 0x1001;//mac地址不正确
        public static final Integer RequestCode_InvalidService_uuid_write = 0x1002;//写数据服务uuid异常
        public static final Integer RequestCode_InvalidCharacteristics_uuid_write = 0x1003;//写数据特征uuid异常
        public static final Integer RequestCode_InvalidService_uuid_notification = 0x1004;//通知服务uuid异常
        public static final Integer RequestCode_InvalidCharacteristics_uuid_notification = 0x1005;//通知特征uuid异常
        public static final Integer RequestCode_InvalidCharacteristics_descriptor_uuid_notification = 0x1006;//通知的特征描述uuid异常
        public static final Integer RequestCode_InvalidData = 0x1007;
    }

    /**
     * 扫描设备异常
     */
    public static final class ScanError{
        public static final Integer ScanError_isScaning = 0x3001;//正在扫描
        public static final Integer ScanError_NotFoundDevice = 0x3002;//没有发现目标设备
        public static final Integer ScanError_errorMacAddress = 0x3003;//mac地址不正确
        public static final Integer ScanError_errorMacAddressList = 0x3004;//mac地址列表不正确
        public static final Integer ScanError_errorServiceUUIDs = 0X3005;//uuid数组不正确
    }

}
