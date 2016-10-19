package com.dsm.xiaodiopen;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bluetoothle.core.BLEUtil;
import com.bluetoothle.core.init.BLEInit;
import com.bluetoothle.core.init.OnInitListener;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.factory.doorguard.DoorGuardSend;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dessmann on 16/10/13.
 * 主页
 */

public class MainActivity extends Activity {

    private Dialog dialog;
    private BLEInit bleInit;

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
        mainList.add(mainData4);

        Map<String, String> mainData5 = new HashMap<>();
        mainData5.put("deviceName", "T700_978E");
        mainData5.put("deviceMac", "FE:0A:A3:D2:97:8E");
        mainData5.put("deviceType", "11");
        mainList.add(mainData5);

        mainListView.setAdapter(new SimpleAdapter(this, mainList, android.R.layout.simple_list_item_2, new String[]{"deviceName", "deviceMac"}, new int[]{android.R.id.text1, android.R.id.text2}));

        mainListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog = buildProgressDialog(MainActivity.this, "正在开门", false);
                        dialog.show();
                        Map<String, String> item = mainList.get(position);
                        String deviceType = item.get("deviceType");
                        final String deviceMac = item.get("deviceMac");
                        if("11".equalsIgnoreCase(deviceType)){
                            Toast.makeText(MainActivity.this, "暂不支持锁设备开门", Toast.LENGTH_LONG).show();
                            dismissDialog(dialog);
                        }else if("13".equalsIgnoreCase(deviceType)){
                            DoorGuardSend.send(
                                    deviceMac,
                                    new OnBLEWriteDataListener() {
                                        @Override
                                        public void onWriteDataFinish() {
                                            BLELogUtil.d("所有数据发送成功");
                                            dismissDialog(dialog);
                                            BLEUtil.closeBluetoothGatt(deviceMac);
                                        }

                                        @Override
                                        public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                                            BLELogUtil.d("单次数据发送成功,gatt=" + gatt + ",data=" + BLEByteUtil.bytesToHexString(characteristic.getValue()) + ",status=" + status);
                                        }

                                        @Override
                                        public void onWriteDataFail(Integer errorCode) {
                                            BLELogUtil.e("开门失败,errorCode=" + errorCode);
                                            Toast.makeText(MainActivity.this, "开门失败", Toast.LENGTH_LONG).show();
                                            dismissDialog(dialog);
                                            BLEUtil.closeBluetoothGatt(deviceMac);
                                        }
                                    }
                            );
                        }else{
                            Toast.makeText(MainActivity.this, "设备类型错误", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        bleInit = new BLEInit(
                MainActivity.this,
                new OnInitListener() {
                    @Override
                    public void onInitSuccess(BluetoothAdapter bluetoothAdapter) {
                        BLELogUtil.e("蓝牙初始化成功,bluetoothAdapter=" + bluetoothAdapter);
                    }

                    @Override
                    public void onInitFail(Integer errorCode) {
                        BLELogUtil.e("蓝牙初始化失败,errorCode=" + errorCode);
                    }
                });
        bleInit.registerReceiver();
        bleInit.startBLEService();
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
