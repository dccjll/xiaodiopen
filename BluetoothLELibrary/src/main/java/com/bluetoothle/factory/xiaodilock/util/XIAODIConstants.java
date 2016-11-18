package com.bluetoothle.factory.xiaodilock.util;

/**
 * Created by dessmann on 16/10/18.
 * 小嘀蓝牙通讯常量类
 */

public class XIAODIConstants {

    /**
     * 小嘀管家蓝牙操作异常类
     */
    public final static class Error {
        public final static String BuildData = "BuildData";//发送数据校验失败
        public final static String CorretCode = "CorretCode";//分析返回的数据成功
        public final static String SingleDataLength = "SingleDataLength";//单条返回数据长度检验失败
        public final static String CheckData = "CheckData";//所有返回数据最小长度校验失败
        public final static String SubBytes = "SubBytes";//截取字节异常
        public final static String DataCmdNotEquals = "DataCmdNotEquals";//数据指令不匹配
        public final static String DataCheck = "DataCheck";//数据校验失败
        public final static String ErrorAck = "ErrorAck";//返回数据应答参数不正确
        public final static String ReturnFailureAck = "ReturnFailureAck";//返回了失败的应答状态
        public final static String AddFingerCollNum = "AddFingerCollNum";//添加指纹返回的已采集的指纹次数不在0到6之间
    }
}
