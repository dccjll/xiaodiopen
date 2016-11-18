package com.bluetoothle.core.init;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BLEService;
import com.bluetoothle.util.BLELogUtil;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙初始化
 */

public class BLEInit {

    private final static String TAG = BLEInit.class.getSimpleName();
    public static Boolean BLUETOOTH_IS_OPEN = false;//当前设备蓝牙开关默认设置为关闭状态
    public static BluetoothAdapter bluetoothAdapter;
    public static boolean status = false;//蓝牙环境初始化状态

    public static Application application;
    private OnInitListener onInitListener;

    public BLEInit(Application application) {
        this.application = application;
    }

    private BroadcastReceiver initBLEBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase(BLEService.ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE)){
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                context.unregisterReceiver(initBLEBroadcastReceiver);
                                initBLE();
                            }
                        }
                ).start();
            }
        }
    };

    /**
     * 启动后台蓝牙服务,启动成功后手动调用initBLE,适用app启动时调用
     */
    public void startBLEService(OnInitListener onInitListener){
        this.onInitListener = onInitListener;
        application.registerReceiver(initBLEBroadcastReceiver, new IntentFilter(BLEService.ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE));
        application.startService(new Intent(application, BLEService.class));
    }

    /**
     * 蓝牙环境检测、初始化,扫描之间手动调用,传入之前启动的服务的上下文
     */
    private void initBLE(){
        if(onInitListener == null){
            BLELogUtil.e(TAG, "初始化监听器不能为空");
            return;
        }
        //判断当前设备是否支持蓝牙ble功能
        boolean hasBLEFeature = application.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if(!hasBLEFeature){
            onInitListener.onInitFail(BLEConstants.Error.NotSupportBLE);
            return;
        }
        //获得蓝牙管理服务
        BluetoothManager bluetoothManager = (BluetoothManager) application.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null){
            onInitListener.onInitFail(BLEConstants.Error.BluetoothManager);
            return;
        }
        //获得蓝牙适配器
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter == null){
            onInitListener.onInitFail(BLEConstants.Error.BluetoothAdapter);
            return;
        }
        //如果蓝牙是关闭的,则请求打开蓝牙
        if(!bluetoothAdapter.isEnabled()){
            BLUETOOTH_IS_OPEN = false;
            bluetoothAdapter.enable();
            while(!BLUETOOTH_IS_OPEN){
                BLELogUtil.e("正在打开蓝牙...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    onInitListener.onInitFail(BLEConstants.Error.OpenBluetoothSleep);
                    return;
                }
            }
            status = true;
            onInitListener.onInitSuccess(bluetoothAdapter);
            return;
        }
        status = true;
        onInitListener.onInitSuccess(bluetoothAdapter);
    }
}
