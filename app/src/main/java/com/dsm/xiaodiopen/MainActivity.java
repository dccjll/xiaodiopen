package com.dsm.xiaodiopen;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bluetoothle.core.init.BLEInit;
import com.bluetoothle.core.init.OnInitListener;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.factory.doorguard.DoorGuardSend;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;
import com.dsm.xiaodiopen.util.PermisstionsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dessmann on 16/10/13.
 * 主页
 */

public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private Dialog dialog;
    private BLEInit bleInit;
    private String mac;//设备mac地址
    private String mobile;//注册到锁上的用户手机号码
    private String channelpwd;//锁的信道密码

    private MainHandler mainHandler = new MainHandler();
    private class MainHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            dismissDialog(dialog);
            Boolean flag = (Boolean) msg.obj;
            if(flag){
                Toast.makeText(MainActivity.this, "开门成功", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this, "开门失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        final List<Map<String, String>> mainList = new ArrayList<>();

        Map<String, String> mainData1 = new HashMap<>();
        mainData1.put("deviceName", "德施曼-3楼前门");
        mainData1.put("deviceMac", "C3:FA:58:31:80:65");
        mainData1.put("deviceType", "13");
        mainList.add(mainData1);

        Map<String, String> mainData2 = new HashMap<>();
        mainData2.put("deviceName", "德施曼-3楼后门");
        mainData2.put("deviceMac", "CA:12:9E:22:2D:87");
        mainData2.put("deviceType", "13");
        mainList.add(mainData2);

        Map<String, String> mainData3 = new HashMap<>();
        mainData3.put("deviceName", "德施曼-1楼前门");
        mainData3.put("deviceMac", "C5:22:AB:EB:46:46");
        mainData3.put("deviceType", "13");
        mainList.add(mainData3);

        Map<String, String> mainData4 = new HashMap<>();
        mainData4.put("deviceName", "T700_A6EB");
        mainData4.put("deviceMac", "ED:83:5C:50:A6:EB");
        mainData4.put("deviceType", "11");
        mainData4.put("mobile", "18668165280");
        mainData4.put("channelpwd", "1459AFA3");
        mainList.add(mainData4);

        Map<String, String> mainData5 = new HashMap<>();
        mainData5.put("deviceName", "T700_978E");
        mainData5.put("deviceMac", "FE:0A:A3:D2:97:8E");
        mainData5.put("deviceType", "11");
        mainData5.put("mobile", "18668165280");
        mainData5.put("channelpwd", "71682D5C");
        mainList.add(mainData5);

        Map<String, String> mainData6 = new HashMap<>();
        mainData6.put("deviceName", "桌面门禁");
        mainData6.put("deviceMac", "E6:3F:5E:AC:16:D3");
        mainData6.put("deviceType", "13");
        mainList.add(mainData6);

        mainListView.setAdapter(new SimpleAdapter(this, mainList, android.R.layout.simple_list_item_2, new String[]{"deviceName", "deviceMac"}, new int[]{android.R.id.text1, android.R.id.text2}));

        mainListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog = buildProgressDialog(MainActivity.this, "正在开门", false);
                        dialog.show();
                        Map<String, String> item = mainList.get(position);
                        String deviceType = item.get("deviceType");
                        mac = item.get("deviceMac");
                        if("11".equalsIgnoreCase(deviceType)){
                            mobile = item.get("mobile");
                            channelpwd = item.get("channelpwd");
                            getSecretkey();
                        }else if("13".equalsIgnoreCase(deviceType)){
                            guardOpen();
                        }else{
                            Toast.makeText(MainActivity.this, "设备类型错误", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        PermisstionsUtil.checkSelfPermission(this, PermisstionsUtil.ACCESS_FINE_LOCATION, PermisstionsUtil.ACCESS_FINE_LOCATION_CODE, "6.0以上系统使用蓝牙需要位置权限",
                new PermisstionsUtil.PermissionResult() {
                    @Override
                    public void granted(int requestCode) {
                        bleInit = new BLEInit(
                                MainActivity.this,
                                new OnInitListener() {
                                    @Override
                                    public void onInitSuccess(BluetoothAdapter bluetoothAdapter) {
                                        BLELogUtil.e(TAG, "蓝牙初始化成功,bluetoothAdapter=" + bluetoothAdapter);
                                    }

                                    @Override
                                    public void onInitFail(Integer errorCode) {
                                        BLELogUtil.e(TAG, "蓝牙初始化失败,errorCode=" + errorCode);
                                    }
                                });
                        bleInit.registerReceiver();
                        bleInit.startBLEService();
                    }

                    @Override
                    public void denied(int requestCode) {
                        Toast.makeText(MainActivity.this, "位置权限被阻止,请手动开启", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermisstionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 门禁开门
     */
    private void guardOpen(){
        DoorGuardSend.send(
                mac,
                new OnBLEWriteDataListener() {
                    @Override
                    public void onWriteDataFinish() {
                        BLELogUtil.d(TAG, "所有数据发送成功,开门成功");
                        mainHandler.obtainMessage(0, true).sendToTarget();
                    }

                    @Override
                    public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        BLELogUtil.d(TAG, "单次数据发送成功,gatt=" + gatt + ",data=" + BLEByteUtil.bytesToHexString(characteristic.getValue()) + ",status=" + status);
                    }

                    @Override
                    public void onWriteDataFail(Integer errorCode) {
                        BLELogUtil.e(TAG, "开门失败,errorCode=" + errorCode);
                        mainHandler.obtainMessage(0, false).sendToTarget();
                    }
                }
        );
    }

    /**
     * 小嘀锁开门获取通讯秘钥
     */
    private void getSecretkey(){
//        byte[] timebytes =
//        xiaodiOpen();
    }

    /**
     * 小嘀开门
     */
    private void xiaodiOpen(){

    }

    private Dialog buildProgressDialog(Activity context, String title, boolean cancelable){
        dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    private void dismissDialog(Dialog dialog){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bleInit.unregisterReceiver();
        dismissDialog(dialog);
    }
}
