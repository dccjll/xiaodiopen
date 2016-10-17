package com.bluetoothle.findService;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

import com.bluetoothle.BLEConstants;
import com.bluetoothle.connect.BLEConnect;
import com.bluetoothle.util.BLELogUtil;

import java.util.List;
import java.util.UUID;

/**
 * Created by dessmann on 16/10/17.
 * 连接上之后寻找服务
 */

public class BLEFindService {

    private final static String TAG = BLEFindService.class.getSimpleName();
    private BluetoothGatt bluetoothGatt;//蓝牙连接服务器
    private UUID serviceUUID;//目标服务UUID
    private Integer currentFindServiceNum = 0;//当前找服务器次数
    private Integer maxFindServiceNum = 2;//最大找服务次数
    private OnBLEFindServiceListener onBLEFindServiceListener;//找服务器监听器

    /**
     * gatt服务器找服务监听器
     */
    public interface OnGattBLEFindServiceListener{
        void onFindServiceSuccess(List<BluetoothGattService> bluetoothGattServices);
        void onFindServiceFail(Integer errorCode);
    }

    /**
     * 找服务
     * @param bluetoothGatt  蓝牙连接服务器
     * @param serviceUUID  目标服务UUID
     * @param onBLEFindServiceListener 找服务监听器
     */
    public BLEFindService(BluetoothGatt bluetoothGatt, UUID serviceUUID, OnBLEFindServiceListener onBLEFindServiceListener) {
        this.bluetoothGatt = bluetoothGatt;
        this.serviceUUID = serviceUUID;
        this.onBLEFindServiceListener = onBLEFindServiceListener;
    }

    /**
     * 找服务
     */
    public void findService(){
        if(onBLEFindServiceListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(bluetoothGatt == null){
            onBLEFindServiceListener.onFindServiceFail(BLEConstants.FindServiceError.FindServiceError_BluetoothGattError);
            return;
        }
        if(serviceUUID == null || serviceUUID.toString().length() == 0){
            onBLEFindServiceListener.onFindServiceFail(BLEConstants.FindServiceError.FindServiceError_TargetServiceUUIDError);
            return;
        }
        BLEConnect.bluetoothLeGattCallback.registerOnGattBLEFindServiceListener(
                new OnGattBLEFindServiceListener() {
                    @Override
                    public void onFindServiceSuccess(List<BluetoothGattService> bluetoothGattServices) {
//                        BluetoothGattService targetBluetoothGattService = null;
//                        for(BluetoothGattService bluetoothGattService : bluetoothGattServices){
//                            if(bluetoothGattService.getUuid().toString().equalsIgnoreCase(serviceUUID.toString())){
//                                targetBluetoothGattService = bluetoothGattService;
//                                break;
//                            }
//                        }
//                        if(targetBluetoothGattService == null){
//                            onBLEFindServiceListener.onFindServiceFail(BLEConstants.FindServiceError.FindServiceError_ServiceListNotCantainsTargetService);
//                            return;
//                        }
                        onBLEFindServiceListener.onFindServiceSuccess(bluetoothGattServices);
                    }

                    @Override
                    public void onFindServiceFail(Integer errorCode) {
                        BLELogUtil.e(TAG, "第" + currentFindServiceNum + "次找服务失败");
                        reTry();
                    }
                }
        );
        reTry();
    }

    /**
     * 重试机制
     */
    private void reTry(){
        if(currentFindServiceNum ++ == maxFindServiceNum){
            onBLEFindServiceListener.onFindServiceFail(BLEConstants.FindServiceError.FindServiceError_FindServiceFail);
            return;
        }
        bluetoothGatt.discoverServices();
    }
}
