package com.bluetoothle.core;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.bluetoothle.core.init.BLEInit;
import com.bluetoothle.util.BLELogUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * 底层蓝牙服务
 */
public class BLEService extends Service {

	private final static String TAG = BLEService.class.getSimpleName();
	public final static String ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE = "com.bluetoothle.ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE";
	public static BLEService bleService;

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

		bleService = this;
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(bluetoothLeBroadcastReceiver, filter);

		/**
		 * 绑定摇一摇监听器
		 */
		//......

		/**
		 * 启动一个子线程，不停扫描蓝牙连接池，如果某一个连接超过规定的时间仍然没有通讯的话，主动断开连接
		 */
		new Thread(
				new Runnable() {
					@Override
					public void run() {
						while(true){
							try {
								synchronized (BLEManage.connectedBluetoothGattList) {
									if(BLEManage.connectedBluetoothGattList != null && BLEManage.connectedBluetoothGattList.size() > 0){
                                        Iterator<Map<BluetoothGatt,Long>> bluetoothGattListIte = BLEManage.connectedBluetoothGattList.iterator();
                                        while(bluetoothGattListIte.hasNext()){
                                            Map<BluetoothGatt,Long> bluetoothGattLongMap = bluetoothGattListIte.next();
                                            for(Map.Entry<BluetoothGatt,Long> entry : bluetoothGattLongMap.entrySet()){
                                                Long timeInterval = System.currentTimeMillis() - entry.getValue();
                                                if(timeInterval >= BLEConfig.MaxWaitDisconnectTimeInterval){
                                                    BLELogUtil.e(TAG, "连接" + entry.getKey() + "超过规定的时间仍然没有通讯，主动断开连接,mac=" + entry.getKey().getDevice().getAddress());
                                                    entry.getKey().disconnect();
                                                }
                                            }
                                        }
                                    }
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
		).start();

		BLELogUtil.d(TAG, "后台蓝牙服务器已启动,当前服务主线程为====" + Thread.currentThread());
		sendBroadcast(new Intent(ACTION_BLUETOOTHLESERVICE_BOOT_COMPLETE));//发送广播,告诉上层本服务已经完全启动
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		bleService = null;
		unregisterReceiver(bluetoothLeBroadcastReceiver);
		BLELogUtil.d(TAG, "后台蓝牙服务器已关闭,当前服务主线程为====" + Thread.currentThread());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}