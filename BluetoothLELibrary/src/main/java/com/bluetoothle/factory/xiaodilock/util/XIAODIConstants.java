package com.bluetoothle.factory.xiaodilock.util;

/**
 * Created by dessmann on 16/10/18.
 * 小嘀蓝牙通讯常量类
 */

public class XIAODIConstants {

    /**
     * 发送数据状态常量
     */
    public final static class SendDataStatics{
        public final static Integer BuildDataCheckError = 0x0001;//发送数据校验失败
    }

    /**
     * 接收数据分析状态常量
     */
    public final static class ReceivedDataAnalysis {
        public final static Integer CorretCode = 0x1000;//分析返回的数据成功
        public final static Integer DataLengthError = 0x1001;//返回数据长度检验失败
        public final static Integer DataMinLengthError = 0x1002;//返回数据最小长度校验失败
        public final static Integer SubBytesError = 0x1003;//校验字节失败
    }

    /**
     * 接收数据分发状态常量
     */
    public final static class ReceivedDataDispatcher {
        public final static Integer DataCmdNotEquals = 0x2001;//数据指令不匹配
        public final static Integer DataCheckError = 0x2002;//数据校验失败
        public final static Integer CmdParamError = 0x2003;//返回数据应答参数不正确
        public final static Integer CmdError = 0x2004;//返回了失败的应答状态
        public final static Integer AddFingerCollNumCheckError = 0x2005;//添加指纹返回的已采集的指纹次数不在0到6之间
    }
}
