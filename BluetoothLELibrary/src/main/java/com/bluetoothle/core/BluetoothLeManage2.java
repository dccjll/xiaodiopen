package com.bluetoothle.core;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.bluetoothle.Request;
import com.bluetoothle.util.BLELogUtil;

import java.lang.reflect.Method;

/**
 * Created by dessmann on 16/7/18.
 * 蓝牙低功耗管理
 */
public class BluetoothLeManage2 {

    private final static String TAG = BluetoothLeManage2.class.getSimpleName();

    /**
     * 是否正在执行摇一摇,摇一摇与普通任务分开，摇一摇的广播接收器一直存在，普通任务的接收器在任务完结的时刻被强制注销，默认状态为非摇一摇
     */
//    public boolean isShaking = false;

    /**
     * 蓝牙连接管理类
     */
    public BluetoothLeDevice bluetoothLeDevice;//蓝牙设备二次封装
    public BluetoothAdapter mBluetoothAdapter;//本地蓝牙适配器
    public BluetoothGatt mBluetoothGatt;//蓝牙服务器连接
    public BluetoothLeGattCallback bluetoothLeGattCallback;//蓝牙服务器状态回调管理器
    private BluetoothAdapter.LeScanCallback leScanCallback;//蓝牙扫描设备回调管理器
    public void setLeScanCallback(BluetoothAdapter.LeScanCallback leScanCallback) {
        this.leScanCallback = leScanCallback;
    }

    /**
     * 当前连接状态管理
     */
    public static final int STATE_DISCONNECTED = 0;//断开连接
    public static final int STATE_CONNECTING = 1;//正在连接
    public static final int STATE_CONNECTED = 2;//已连接
    public int mConnectionState = STATE_DISCONNECTED;//默认为断开连接

    /**
     * 蓝牙任务结果标号
     */
    public final static String ACTION_BLE_ERROR = "com.bluetoothle.ACTION_BLE_ERROR";//任务出错
    public final static String ACTION_DATA_AVAILABLE = "com.bluetoothle.ACTION_DATA_AVAILABLE";//收到设备数据
    public final static String ACTION_WRITTEN_SUCCESS = "com.bluetoothle.ACTION_WRITTEN_SUCCESS";//向设备写数据成功

    /**
     * 蓝牙摇一摇任务结果标号
     */
    public final static String ACTION_BLE_ERROR_SHAKING = "com.bluetoothle.ACTION_BLE_ERROR_SHAKING";//摇一摇任务出错
    public final static String ACTION_DATA_AVAILABLE_SHAKING = "com.bluetoothle.ACTION_DATA_AVAILABLE_SHAKING";//摇一摇收到设备数据
    public final static String ACTION_WRITTEN_SUCCESS_SHAKING = "com.bluetoothle.ACTION_WRITTEN_SUCCESS_SHAKING";//摇一摇向设备写数据成功

    /**
     * 蓝牙低功耗管理类，单例
     */
    private static BluetoothLeManage2 bluetoothLeManage = null;
    public static BluetoothLeManage2 getSingleInstance(){
        if(bluetoothLeManage == null){
            bluetoothLeManage = new BluetoothLeManage2();
        }
        return bluetoothLeManage;
    }

    /**
     * 注册蓝牙服务
     */
    private static Service service = null;//蓝牙服务
    public void registerBluetoothLeService(Service _service){
        service = _service;
    }

    /**
     * 蓝牙服务消息管理器
     */
    public static final int MSG_BLUETOOTHLESERVICE_SCAN_FOUND_DEVICE = 0x0001;//区别摇一摇扫描设备，发现了目标设备
    public static final int MSG_BLUETOOTHLESERVICE_CANCEL_TIMEOUT = 0x0002;//取消超时的事件
    private static final int MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION = 0x0003;//在主线程中关闭蓝牙连接
    public BLEHandler bleHandler = new BLEHandler();//蓝牙服务消息管理器
    public class BLEHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_BLUETOOTHLESERVICE_SCAN_FOUND_DEVICE://扫描设备，发现了目标设备
                    try {
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        //比对扫描到的设备，看是否是需要的目标设备,foundTargetDevice标识是否已经发现了目标设备，扫描时很容易返回已经发现的设备，不加该标识可能会重复执行
                        if(request.getTargetDeviceMacAddress().equalsIgnoreCase(device.getAddress()) && !foundTargetDevice){
                            foundTargetDevice = true;
                            //发现设备后停止扫描，移除扫描超时任务
                            mBluetoothAdapter.stopLeScan(leScanCallback);
                            timeoutScanDeviceHandler.removeCallbacks(timeoutScanDeviceRunnable);

                            bluetoothLeDevice = new BluetoothLeDevice(request, device, service);
                            bluetoothLeGattCallback = new BluetoothLeGattCallback(bluetoothLeDevice);

                            connect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        handleBLEError(true, "连接异常");
                    }
                    break;
                case MSG_BLUETOOTHLESERVICE_CANCEL_TIMEOUT://取消超时任务
                    BLELogUtil.d(tag, "已取消全局超时");
                    timeoutWholeTaskHandler.removeCallbacks(timeoutWholeTaskRunnable);
                    break;
                case MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION://在主线程关闭蓝牙连接，根据网上相关资料，在子线程关闭连接会有无法预料的问题
                    try {
                        closeBluetoothGatt();
                    } catch (Exception e) {
                        e.printStackTrace();
                        broadcastUpdate(ACTION_BLE_ERROR, "连接异常");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 整个任务超时管理器
     */
    public int timeoutWholeTask = 12000;//整个任务默认超时时间
    private final Handler timeoutWholeTaskHandler = new Handler();//整个任务超时调度器
    private Runnable timeoutWholeTaskRunnable = new Runnable() {//整个任务超时处理器
        @Override
        public void run() {
            isTimeOut=true;
            handleBLEError(true, "任务超时");
        }
    };

    /**
     * 当蓝牙设备连接为长连接时，不执行下一个蓝牙任务自动断开连接的控制主体
     */
    private boolean enableAutoDisconnect = true;//自动断开控制位，默认开启自动断开机制
    private static long INTERVAL_AUTO_DISCONNECT =  2 * 60 * 1000;//默认自动断开的时间间隔
    public final static String ACTION_AUTO_DISCONNECT = "com.bluetooth.ACTION_AUTO_DISCONNECT";//自动断开的广播动作
    private final Handler autoDisconnectHandler = new Handler();//任务调度器
    private final Runnable autoDisconnectRunnable = new Runnable() {

        @Override
        public void run() {//任务实体
            if(!enableAutoDisconnect){
                BLELogUtil.d(tag, "未启用自动断开机制");
                return;
            }
            BLELogUtil.d(tag, "已启用自动断开机制，发送断开广播到上层...");
            if(!checkCurrentConnectActive()){
                BLELogUtil.d(tag, "当前无蓝牙连接");
                service.sendBroadcast(new Intent(ACTION_BLE_ERROR));
                return;
            }
            isDisconnecting = true;
            service.sendBroadcast(new Intent(ACTION_AUTO_DISCONNECT));
        }
    };
    public boolean isDisconnecting = false;//是否正在断开，如果正在断开了，则不继续执行任何蓝牙操作
    public void configAutoDisconnectRunnable(final boolean autoDisconnect){//自动断开控制器 autoDisconnect=true 自动断开 autoDisconnect=false 取消自动断开
        if(autoDisconnect){
            autoDisconnectHandler.removeCallbacks(autoDisconnectRunnable);
            autoDisconnectHandler.postDelayed(autoDisconnectRunnable, INTERVAL_AUTO_DISCONNECT);
            return;
        }
        autoDisconnectHandler.removeCallbacks(autoDisconnectRunnable);
    }

    /**
     * 检测当前连接是否是活跃连接
     * @return
     */
    private boolean checkCurrentConnectActive(){
        try {
            if(mConnectionState != STATE_CONNECTED){
                return false;
            }
            if(mBluetoothGatt == null || mBluetoothGatt.getDevice() == null){
                return false;
            }
            if(bluetoothLeDevice == null || bluetoothLeDevice.getBluetoothDevice() == null){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 检测当前连接是否是活跃连接
     * @param mac   验证的设备mac地址
     * @return
     */
    public boolean checkCurrentConnectActive(String mac){
        try {
            if(mConnectionState != STATE_CONNECTED){
                return false;
            }
            if(mBluetoothGatt == null || mBluetoothGatt.getDevice() == null){
                return false;
            }
            if(!mBluetoothGatt.getDevice().getAddress().equalsIgnoreCase(mac)){
                return false;
            }
            if(bluetoothLeDevice == null || bluetoothLeDevice.getBluetoothDevice() == null){
                return false;
            }
            if(!bluetoothLeDevice.getBluetoothDevice().getAddress().equalsIgnoreCase(mac)){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 扫描目标设备超时机制
     */
    private final long timeoutScanDevice = 5000;//扫描设备默认超时时间
    private final Handler timeoutScanDeviceHandler = new Handler();//扫描设备超时调度器
    private Runnable timeoutScanDeviceRunnable = new Runnable() {
        @Override
        public void run() {//扫描设备超时任务处理
            try {
                mBluetoothAdapter.stopLeScan(leScanCallback);
                BluetoothLeScanCallback bluetoothLeScanCallback = (BluetoothLeScanCallback) leScanCallback;
                if(bluetoothLeScanCallback.getOnLeScanListener() != null){
                    bluetoothLeScanCallback.getOnLeScanListener().notFoundDevice();
                    return;
                }
                handleBLEError(true, "未发现设备");
            } catch (Exception e) {
                e.printStackTrace();
                BluetoothLeScanCallback bluetoothLeScanCallback = (BluetoothLeScanCallback) leScanCallback;
                if(bluetoothLeScanCallback.getOnLeScanListener() != null){
                    bluetoothLeScanCallback.getOnLeScanListener().scanError(e.getMessage());
                    return;
                }
                handleBLEError(true, "扫描设备异常");
            }
        }
    };

    /**
     * 蓝牙任务入口
     * @param _request 蓝牙请求的数据对象
     */
    public Request request;//请求数据封装
    private boolean isTimeOut=false;//当前任务是否超时
    public void prepareScanLe(Request _request){//普通请求初始化
        request = _request;
        isTimeOut = false;
        timeoutWholeTaskHandler.postDelayed(timeoutWholeTaskRunnable, timeoutWholeTask);
        if(checkCurrentConnectActive(request.getTargetDeviceMacAddress())){
            bluetoothLeDevice.setRequest(request);
            bluetoothLeGattCallback = new BluetoothLeGattCallback(bluetoothLeDevice);
            BLELogUtil.d(tag, "直接写数据...");
            send();
            return;
        }
        if(isDisconnecting){
            handleBLEError(true, "正在断开连接");
            return;
        }
        if(bluetoothLeDevice != null && bluetoothLeDevice.getBluetoothDevice() != null && bluetoothLeDevice.getBluetoothDevice().getAddress().equalsIgnoreCase(request.getTargetDeviceMacAddress())){
            bluetoothLeDevice.setRequest(request);
            bluetoothLeGattCallback = new BluetoothLeGattCallback(bluetoothLeDevice);
            BLELogUtil.d(tag, "直接连接设备...");
            connect();
            return;
        }
        BLELogUtil.d(tag, "扫描设备然后连接...");
        scanLeDevice(true);
    }

    /**
     * 超时初始化，适用于摇一摇等事先不确定设备地址的情况
     */
    public void timeoutInit(){
        timeoutWholeTaskHandler.postDelayed(timeoutWholeTaskRunnable, timeoutWholeTask);
    }

    /**
     * 找到了设备，适用于摇一摇等事先不确定设备地址的情况
     * @param device
     */
    public void foundDeviceShake(BluetoothDevice device, Request request){
        mBluetoothAdapter.stopLeScan(leScanCallback);
        BLELogUtil.d(tag, "===========匹配到目标设备,foundDeviceShake,mac=" + device.getAddress() + "==============");
        this.request = request;
        leScanCallback = new BluetoothLeScanCallback(bleHandler);
        timeoutScanDeviceHandler.removeCallbacks(timeoutScanDeviceRunnable);
        bluetoothLeDevice = new BluetoothLeDevice(request, device, service);
        bluetoothLeGattCallback = new BluetoothLeGattCallback(bluetoothLeDevice);
    }

    /**
     * 扫描设备
     */
    private boolean foundTargetDevice = false;//扫描设备时，是否已经发现了目标设备
    public void scanLeDevice(boolean initLeScanCallback) {
        if(isDisconnecting){
            handleBLEError(true, "正在断开连接");
            return;
        }
        if (initLeScanCallback) {
            leScanCallback = new BluetoothLeScanCallback(bleHandler);
            foundTargetDevice = false;
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        timeoutScanDeviceHandler.postDelayed(timeoutScanDeviceRunnable, timeoutScanDevice);
        new Thread(new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(leScanCallback);
            }
        }).start();
    }

    /**
     * 关闭并置空蓝牙连接
     */
    public void closeBluetoothGatt() throws Exception {
        if(mBluetoothGatt != null){
            mBluetoothGatt.disconnect();
        }
//        if(bluetoothLeDevice != null){
//            removeBond();
//        }
        if(mBluetoothGatt != null){
            refreshDeviceCache();
        }
        if(mBluetoothGatt != null){
            mBluetoothGatt.close();
        }
//            mBluetoothGatt = null;
//            if(bluetoothLeDevice != null){
//                bluetoothLeDevice.setBluetoothDevice(null);
//            }
//            bluetoothLeDevice = null;
//        mConnectionState = STATE_DISCONNECTED;
        BLELogUtil.d(tag, "关闭蓝牙的线程为====" + Thread.currentThread());
    }

    /**
     * 刷新gatt服务器
     * @return
     */
    private boolean refreshDeviceCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                final boolean success = (Boolean) refresh.invoke(mBluetoothGatt);
                BLELogUtil.i(tag, "Refreshing result: " + success);
                return success;
            }
        } catch (Exception e) {
            BLELogUtil.e(tag, "An exception occured while refreshing device " + e);
        }
        return false;
    }

    /**
     * 移除设备绑定状态
     */
//    private void removeBond() {
//        try {
//            Method m = bluetoothLeDevice.getClass().getMethod("removeBond");
//            m.invoke(bluetoothLeDevice);
//            BLELogUtil.i(tag, "removeBond");
//        } catch (Exception ex) {
//            BLELogUtil.i(tag, "ex:" + ex.getMessage());
//        }
//    }

    /**
     * 蓝牙任务出错统一处理
     * @param sendmsg 是否发送消息告知上层
     * @param error 出错消息
     */
    public void handleBLEError(boolean sendmsg, String error){
        try {
            BLELogUtil.d(tag, error);
            timeoutScanDeviceHandler.removeCallbacks(timeoutScanDeviceRunnable);
            timeoutWholeTaskHandler.removeCallbacks(timeoutWholeTaskRunnable);
            bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);//发消息到主线程关闭蓝牙连接
            if(sendmsg){
//                if(isShaking){
//                    broadcastUpdate(ACTION_BLE_ERROR_SHAKING, error);
//                    return;
//                }
                broadcastUpdate(ACTION_BLE_ERROR, error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(sendmsg){
//                if(isShaking){
//                    broadcastUpdate(ACTION_BLE_ERROR_SHAKING, error);
//                    return;
//                }
                broadcastUpdate(ACTION_BLE_ERROR, error);
            }
        }
    }

    /**
     * 手动检测是否有定位权限，没有的话手动请求
     * Android6.0 蓝牙扫描才需要
     * @param thisActivity
     * @param MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
     */
    public static void checkBluetoothScanLocationPermission(Activity thisActivity, int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //请求权限
                ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        }
    }

    /**
     * 判断本地蓝牙是否已打开
     * @return
     */
    public boolean checkBluetoothStatus(){
        try {
            BluetoothManager mBluetoothManager = (BluetoothManager) service.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                BLELogUtil.d(tag, "Unable to initialize BluetoothManager.");
                return false;
            }

            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                BLELogUtil.d(tag, "Unable to obtain a BluetoothAdapter.");
                return false;
            }

            if(!mBluetoothAdapter.isEnabled()){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 发送带数据的广播，数据索引字符串
     * @param action
     * @param obj
     */
    public static void broadcastUpdate(String action, Object obj) {
        Intent intent = new Intent(action);
        if(obj instanceof String){
            intent.putExtra(action, (String)obj);
        }else if(obj instanceof byte[]){
            intent.putExtra(action, (byte[]) obj);
        }else{
            intent.putExtra(action, "empty");
        }

        service.sendBroadcast(intent);
    }

    /**
     * 连接核心操作 连接、找服务、打开通知、写数据四段阻塞，分段多次重试
     * @throws InterruptedException
     */
    private int interval_reconnect = 500;//操作过程中突然断开连接间隔重新连接的时间
    private int interval_startfindservice = 50;//连接成功后开始找服务的时间间隔
    private int interval_startopennotification = 50;//找到服务之后开始打开通知的时间间隔
    private int interval_startwritevalue = 50;//打开通知之后开始写数据的时间间隔
    private void connect() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            BLELogUtil.d(tag, "开始连接,mac=" + request.getTargetDeviceMacAddress());
                            if(!bluetoothLeDevice.connectDevice(request.getTargetDeviceMacAddress()) || !bluetoothLeDevice.connectOperateStatus){
                                if(isTimeOut){
                                    return;
                                }
                                BLELogUtil.d(tag, "连接失败,做一次关闭连接，休眠" + interval_reconnect  + "ms重新连接");
                                bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);
                                Thread.sleep(interval_reconnect);
                                if(isTimeOut){
                                    return;
                                }
                                BLELogUtil.d(tag, "重新连接");
                                if(!bluetoothLeDevice.connectDevice(request.getTargetDeviceMacAddress()) || !bluetoothLeDevice.connectOperateStatus){
                                    if(isTimeOut){
                                        return;
                                    }
                                    BLELogUtil.d(tag, "连接失败,做一次关闭连接，休眠" + interval_reconnect  + "ms重新连接");
                                    bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);
                                    Thread.sleep(interval_reconnect);
                                    if(isTimeOut){
                                        return;
                                    }
                                    BLELogUtil.d(tag, "重新连接");
                                    if(!bluetoothLeDevice.connectDevice(request.getTargetDeviceMacAddress()) || !bluetoothLeDevice.connectOperateStatus){
                                        if(isTimeOut){
                                            return;
                                        }
                                        handleBLEError(true, "设备连接失败");
                                        return;
                                    }
                                }
                            }
                            BLELogUtil.d(tag, "设备已连接");

                            BLELogUtil.d(tag, "休眠" + interval_startfindservice + "ms准备开始找服务");
                            Thread.sleep(interval_startfindservice);
                            if(isTimeOut){
                                return;
                            }
                            if(mConnectionState == BluetoothLeManage2.STATE_DISCONNECTED){
                                if(isTimeOut){
                                    return;
                                }
                                BLELogUtil.d(tag, "准备找服务时断开连接，做一次关闭连接,休眠" + interval_reconnect + "重新连接");
                                bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);
                                Thread.sleep(interval_reconnect);
                                if(isTimeOut){
                                    return;
                                }
                                BLELogUtil.d(tag, "重新连接");
                                connect();
                                return;

                            }
                            BLELogUtil.d(tag, "开始找服务...");
                            if(!bluetoothLeDevice.findService() || !bluetoothLeDevice.findServiceOperateStatus){
                                if(isTimeOut){
                                    return;
                                }
                                BLELogUtil.d(tag, "开始找服务时断开连接，做一次关闭连接,休眠" + interval_reconnect + "重新连接");
                                bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);
                                Thread.sleep(interval_reconnect);
                                if(isTimeOut){
                                    return;
                                }
                                BLELogUtil.d(tag, "重新连接");
                                connect();
                                return;
                            }
                            BLELogUtil.d(tag, "找到服务");

                            if(request.getReceiveDataFromBLEDevice()){
                                BLELogUtil.d(tag, "休眠" + interval_startopennotification + "ms准备开始打开通知");
                                Thread.sleep(interval_startopennotification);
                                if(isTimeOut){
                                    return;
                                }
                                if(mConnectionState == BluetoothLeManage2.STATE_DISCONNECTED){
                                    if(isTimeOut){
                                        return;
                                    }
                                    BLELogUtil.d(tag, "准备开始打开通知时断开连接，做一次关闭连接,休眠" + interval_reconnect + "重新连接");
                                    bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);
                                    Thread.sleep(interval_reconnect);
                                    if(isTimeOut){
                                        return;
                                    }
                                    BLELogUtil.d(tag, "重新连接");
                                    connect();
                                    return;

                                }
                                BLELogUtil.d(tag, "开始打开通知...");
                                if(!bluetoothLeDevice.openNotification() || !bluetoothLeDevice.notificationOperateStatus){
                                    if(isTimeOut){
                                        return;
                                    }
                                    BLELogUtil.d(tag, "打开通知时断开连接，做一次关闭连接,休眠" + interval_reconnect + "重新连接");
                                    bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);
                                    Thread.sleep(interval_reconnect);
                                    if(isTimeOut){
                                        return;
                                    }
                                    BLELogUtil.d(tag, "重新连接");
                                    connect();
                                    return;
                                }
                                BLELogUtil.d(tag, "通知已打开");
                            }
                            send();
                        } catch (Exception e) {
                            e.printStackTrace();
                            handleBLEError(false, "发生异常");
                        }
                        Looper.loop();
                    }
                }
        ).start();
    }

    /**
     * 发送数据
     */
    private void send() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BLELogUtil.d(tag, "休眠" + interval_startwritevalue + "ms准备开始写数据");
                            Thread.sleep(interval_startwritevalue);
                            if(isTimeOut){
                                return;
                            }
                            if(mConnectionState == BluetoothLeManage2.STATE_DISCONNECTED){
                                if(isTimeOut){
                                    return;
                                }
                                BLELogUtil.d(tag, "准备写数据时断开连接，做一次关闭连接,休眠" + interval_reconnect + "重新连接");
                                bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);
                                Thread.sleep(interval_reconnect);
                                if(isTimeOut){
                                    return;
                                }
                                BLELogUtil.d(tag, "重新连接");
                                connect();
                                return;

                            }

                            BLELogUtil.d(tag, "开始写数据");
                            configAutoDisconnectRunnable(false);
                            if(!bluetoothLeDevice.writeValue()){
                                if(isTimeOut){
                                    return;
                                }
                                if(mConnectionState == BluetoothLeManage2.STATE_DISCONNECTED){
                                    if(isTimeOut){
                                        return;
                                    }
                                    BLELogUtil.d(tag, "开始写数据时断开连接，做一次关闭连接,休眠" + interval_reconnect + "重新连接");
                                    bleHandler.sendEmptyMessage(MSG_BLUETOOTHLESERVICE_CLOSE_CONNECTION);
                                    Thread.sleep(interval_reconnect);
                                    if(isTimeOut){
                                        return;
                                    }
                                    BLELogUtil.d(tag, "重新连接");
                                    connect();
                                    return;

                                }
                                handleBLEError(true, "数据写入失败");
                                return;
                            }
                            BLELogUtil.d(tag, "数据写入成功");
                            configAutoDisconnectRunnable(true);

                            if(!request.getReceiveDataFromBLEDevice()){
                                bleHandler.sendEmptyMessage(BluetoothLeManage2.MSG_BLUETOOTHLESERVICE_CANCEL_TIMEOUT);
                                broadcastUpdate(ACTION_WRITTEN_SUCCESS, null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }

    /**
     * 停止服务
     */
    public void stop(){
        service.stopSelf();
    }

}
