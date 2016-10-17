package com.bluetoothle.factory.xiaodilock;

/**
 * Created by dessmann on 16/7/28.
 * 小滴管家回调接口
 */
public class OnXIAODIBLEListener {

    //添加指纹协议返回数据处理接口(0x24)
    public interface XIAODIBLEAddFingerRetrunDataListener{
        void fingerCollectionFailure(String error);
        void fingerCollectionSuccess(long fingerCollectionCcount);
        void fingerAddSuccess(String generatedFingerID, long fingerCollectionCount);
    }

    //验证锁上管理密码协议返回数据处理接口(0x36)
    public interface XIAODIBLECheckManagePasswordRetrunDataListener{
        void checkBLEManagePasswordReturnFailure(String error);
        void checkBLEManagePasswordReturnSuccess();
        void checkBLEManagePasswordSuccess();
    }
}
