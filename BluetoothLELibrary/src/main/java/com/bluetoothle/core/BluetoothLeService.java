package com.bluetoothle.core;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.bluetoothle.core.init.BLEInit;
import com.bluetoothle.util.BLELogUtil;

/**
 * 底层蓝牙服务
 */
public class BluetoothLeService extends Service {

	private final static String TAG = BluetoothLeService.class.getSimpleName();
	public final static String ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE = "com.bluetoothle.ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE";
	public static BluetoothLeService bluetoothLeService;

	/**
	 * 活跃在服务中的广播接收器
	 */
	private BroadcastReceiver bluetoothLeBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			BLELogUtil.e(TAG, "onReceive---------");
			if(intent.getAction() == BluetoothAdapter.ACTION_STATE_CHANGED){
				int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
				Long startOpen = 0L;
				Long startClose = 0L;
				if(blueState == BluetoothAdapter.STATE_TURNING_ON){
					BLELogUtil.e("onReceive---------STATE_TURNING_ON");
					startOpen = System.currentTimeMillis();
				}else if(blueState == BluetoothAdapter.STATE_ON){
					BLELogUtil.e("onReceive---------STATE_ON");
					Long endOpen = System.currentTimeMillis();
					BLELogUtil.e("打开蓝牙耗费时间:" + (endOpen - startOpen)/1000 + "s");
					BLEInit.BLUETOOTH_IS_OPEN = true;
				}else if(blueState == BluetoothAdapter.STATE_TURNING_OFF){
					BLELogUtil.e("onReceive---------STATE_TURNING_OFF");
					startClose = System.currentTimeMillis();
				}else if(blueState == BluetoothAdapter.STATE_OFF){
					BLELogUtil.e("onReceive---------STATE_OFF");
					Long endClose = System.currentTimeMillis();
					BLELogUtil.e("关闭蓝牙耗费时间:" + (endClose - startClose)/1000 + "s");
					BLEInit.BLUETOOTH_IS_OPEN = false;
				}
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		bluetoothLeService = this;
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(bluetoothLeBroadcastReceiver, filter);

		/**
		 * 绑定摇一摇监听器
		 */
		//......

		BLELogUtil.d(TAG, "后台蓝牙服务器已启动,当前服务主线程为====" + Thread.currentThread());
		sendBroadcast(new Intent(ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE));//发送广播,告诉上层本服务已经完全启动
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		bluetoothLeService = null;
		unregisterReceiver(bluetoothLeBroadcastReceiver);
		BLELogUtil.d(TAG, "后台蓝牙服务器已关闭,当前服务主线程为====" + Thread.currentThread());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}