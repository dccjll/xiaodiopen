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
        public final static Integer CheckBuildDataError = 0x0001;//发送数据校验失败
        public final static Integer CorretCode = 0x1000;//分析返回的数据成功
        public final static Integer CheckSingleDataLengthError = 0x1001;//单条返回数据长度检验失败
        public final static Integer CheckWholeDataLengthError = 0x1002;//所有返回数据最小长度校验失败
        public final static Integer CheckSubBytesError = 0x1003;//截取字节异常
        public final static Integer CheckDataCmdNotEquals = 0x2001;//数据指令不匹配
        public final static Integer CheckDataCheckError = 0x2002;//数据校验失败
        public final static Integer CheckCmdParamError = 0x2003;//返回数据应答参数不正确
        public final static Integer CheckCmdError = 0x2004;//返回了失败的应答状态
        public final static Integer CheckAddFingerCollNumError = 0x2005;//添加指纹返回的已采集的指纹次数不在0到6之间
    }
}
