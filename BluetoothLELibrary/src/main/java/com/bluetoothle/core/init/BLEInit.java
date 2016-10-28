package com.bluetoothle.core.init;

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
//    public static Integer timeoutOpenBluetooth = 10*1000;//打开蓝牙超时时间
    public static BluetoothAdapter bluetoothAdapter;

    private Context context;
    private OnInitListener onInitListener;

    public BLEInit(Context context, OnInitListener onInitListener) {
        this.context = context;
        this.onInitListener = onInitListener;
    }

    private BroadcastReceiver initBLEBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase(BLEService.ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE)){
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
//                                Looper.prepare();
                                initBLE();
//                                Looper.loop();
                            }
                        }
                ).start();
            }
        }
    };

    /**
     * 注册蓝牙状态广播监听器
     */
    public void registerReceiver(){
        context.registerReceiver(initBLEBroadcastReceiver, new IntentFilter(BLEService.ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE));
    }

    /**
     * 注销蓝牙状态广播监听器
     */
    public void unregisterReceiver(){
        context.unregisterReceiver(initBLEBroadcastReceiver);
    }

    /**
     * 启动后台蓝牙服务,启动成功后手动调用initBLE,适用app启动时调用
     */
    public void startBLEService(){
        context.startService(new Intent(context, BLEService.class));
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
        boolean hasBLEFeature = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if(!hasBLEFeature){
            onInitListener.onInitFail(BLEConstants.Error.NotSupportBLEError);
            return;
        }
        //获得蓝牙管理服务
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null){
            onInitListener.onInitFail(BLEConstants.Error.CheckBluetoothManagerError);
            return;
        }
        //获得蓝牙适配器
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter == null){
            onInitListener.onInitFail(BLEConstants.Error.CheckBluetoothAdapterError);
            return;
        }
        //如果蓝牙是关闭的,则请求打开蓝牙
        if(!bluetoothAdapter.isEnabled()){
            BLUETOOTH_IS_OPEN = false;
//            Handler openBluetoothHandler = new Handler();
//            Runnable openBluetoothRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    bluetoothAdapter.disable();
//                    onInitListener.onInitFail(BLEConstants.Error.OpenBluetoothTimeoutError);
//                }
//            };
//            openBluetoothHandler.postDelayed(openBluetoothRunnable, timeoutOpenBluetooth);
            bluetoothAdapter.enable();
            while(!BLUETOOTH_IS_OPEN){
                BLELogUtil.e("正在打开蓝牙...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
//                    openBluetoothHandler.removeCallbacks(openBluetoothRunnable);
                    onInitListener.onInitFail(BLEConstants.Error.OpenBluetoothSleepError);
                    return;
                }
            }
//            openBluetoothHandler.removeCallbacks(openBluetoothRunnable);
            onInitListener.onInitSuccess(bluetoothAdapter);
            return;
        }
        onInitListener.onInitSuccess(bluetoothAdapter);
    }
}
