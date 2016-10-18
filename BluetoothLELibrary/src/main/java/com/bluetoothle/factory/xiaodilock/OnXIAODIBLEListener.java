package com.bluetoothle.factory.xiaodilock;

import com.bluetoothle.factory.xiaodilock.received.XIAODIDataReceivedAnalyzer;

/**
 * Created by dessmann on 16/7/28.
 * 小嘀管家回调接口
 */
public class OnXIAODIBLEListener {

    /**
     * 通用返回数据接口
     */
    public interface OnCommonListener{
        void success(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer);//成功
        void failure(Integer errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer);//失败
    }

    /**
     * 添加指纹协议返回数据处理接口(0x24)
     */
    public interface OnAddFingerListener {
        void fingerTemplateCollectionFailure(Integer errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer);//指纹模板采集失败
        void fingerTemplateCollectionSuccess(long fingerCollectionCcount, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer);//指纹模板采集成功
        void fingerCollectionSuccess(String generatedFingerID, long fingerCollectionCount, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer);//指纹采集成功
    }

    /**
     * 验证锁上管理密码协议返回数据处理接口(0x36)
     */
    public interface OnCheckManagePwdListener {
        void onCheckFailure(Integer errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer);//验证失败
        void onCheckSuccess(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer);//验证成功
        void onLockForceCleared(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer);//锁已被清空
    }
}
