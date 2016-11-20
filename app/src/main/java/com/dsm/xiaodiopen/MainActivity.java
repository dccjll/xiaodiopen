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
import com.bluetoothle.factory.xiaodilock.OnXIAODIBLEListener;
import com.bluetoothle.factory.xiaodilock.protocol.XIAODIBLECMDType;
import com.bluetoothle.factory.xiaodilock.protocol.XIAODIBLEProtocol;
import com.bluetoothle.factory.xiaodilock.received.XIAODIDataReceived;
import com.bluetoothle.factory.xiaodilock.received.XIAODIDataReceivedAnalyzer;
import com.bluetoothle.factory.xiaodilock.send.XIAODIData;
import com.bluetoothle.factory.xiaodilock.send.XIAODISend;
import com.bluetoothle.util.BLEByteUtil;
import com.bluetoothle.util.BLELogUtil;
import com.bluetoothle.util.BLEStringUtil;
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

//    private String macSmartKey = "23:A2:CD:ED:00:F7";
      private String macSmartKey = "81:00:51:7D:00:36";

    private Dialog dialog;
    private BLEInit bleInit;
    private String mac;//设备mac地址
    private String mobile;//注册到锁上的用户手机号码
    private String channelpwd;//锁的信道密码
    private Map<String, String> item;
    private byte[] openSecretKeyBytes;

    private MainHandler mainHandler = new MainHandler();
    private class MainHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            dismissDialog(dialog);
            Bundle bundle = (Bundle) msg.obj;
            Boolean flag = bundle.getBoolean("flag");
            String desc = bundle.getString("desc");
            Toast.makeText(MainActivity.this, desc + (flag ? "成功" : "失败"), Toast.LENGTH_LONG).show();
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

        Map<String, String> mainData7 = new HashMap<>();
        mainData7.put("deviceName", "叶飞测试锁");
        mainData7.put("deviceMac", "DF:6D:0A:4A:BA:25");
        mainData7.put("deviceType", "11");
        mainData7.put("mobile", "18668165280");
        mainData7.put("channelpwd", "DA45B5F5");
        mainList.add(mainData7);

        Map<String, String> mainData8 = new HashMap<>();
        mainData8.put("deviceName", "测试锁");
        mainData8.put("deviceMac", "FD:27:F0:5E:F2:5C");
        mainData8.put("deviceType", "11");
        mainData8.put("mobile", "18668165280");
        mainData8.put("channelpwd", "A30DA10F");
        mainList.add(mainData8);

        Map<String, String> mainData9 = new HashMap<>();
        mainData9.put("deviceName", "1201");
        mainData9.put("deviceMac", "D9:EC:03:08:5F:46");
        mainData9.put("deviceType", "11");
        mainData9.put("mobile", "18668165280");
        mainData9.put("channelpwd", "A30DA10F");
        mainList.add(mainData9);

        mainListView.setAdapter(new SimpleAdapter(this, mainList, android.R.layout.simple_list_item_2, new String[]{"deviceName", "deviceMac"}, new int[]{android.R.id.text1, android.R.id.text2}));

        mainListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog = buildProgressDialog(MainActivity.this, "正在开门", false);
                        dialog.show();
                        item = mainList.get(position);
                        String deviceType = item.get("deviceType");
                        mac = item.get("deviceMac");
                        channelpwd = item.get("channelpwd");
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
        mainListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        item = mainList.get(position);
                        mac = item.get("deviceMac");
                        channelpwd = item.get("channelpwd");
//                        new AlertDialog.Builder(MainActivity.this)
//                                .setTitle("选择操作")
//                                .setItems(
//                                        new String[]{"注册智能钥匙", "添加智能钥匙"},
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface _dialog, int which) {
//                                                if ("测试锁".equalsIgnoreCase(item.get("deviceName"))) {
//                                                    openSecretKeyBytes = BLEByteUtil.parseRadixStringToBytes("112233445566778899AABBCCDD", 16);
//                                                }else{
//                                                    openSecretKeyBytes = XIAODIBLEProtocol.parse13Secretkeys("18668165280");
//                                                }
//                                                if(which == 0){
//                                                    dialog = buildProgressDialog(MainActivity.this, "正在注册智能钥匙", false);
//                                                    dialog.show();
//                                                    XIAODISend.smartKeyInit(
//                                                            macSmartKey,
//                                                            new XIAODISend.OnSmartKeyInitListener() {
//                                                                @Override
//                                                                public void onInitSuccess() {
//                                                                    getSecretKeyOnSmarKey();
//                                                                }
//
//                                                                @Override
//                                                                public void onInitFaiure(Object obj) {
//                                                                    BLELogUtil.e(TAG, "智能钥匙初始化失败,obj=" + obj);
//                                                                    mainHandler.obtainMessage(0, buildBundle("注册智能钥匙", false)).sendToTarget();
//                                                                }
//                                                            });
//                                                }else if(which == 1){
//                                                    getSecretKeyOnLock();
//                                                }
//                                            }
//                                        })
//                                .show();
//                        if ("测试锁".equalsIgnoreCase(item.get("deviceName"))) {
//                            openSecretKeyBytes = BLEByteUtil.parseRadixStringToBytes("112233445566778899AABBCCDD", 16);
//                        }else{
//                            openSecretKeyBytes = XIAODIBLEProtocol.parse13Secretkeys("18668165280");
//                        }
                        openSecretKeyBytes = XIAODIBLEProtocol.parse13Secretkeys("18668165280");
                        dialog = buildProgressDialog(MainActivity.this, "正在注册智能钥匙", false);
                        dialog.show();
                        XIAODISend.smartKeyInit(
                                macSmartKey,
                                new XIAODISend.OnSmartKeyInitListener() {
                                    @Override
                                    public void onInitSuccess() {
                                        getSecretKeyOnSmarKey();
                                    }

                                    @Override
                                    public void onInitFaiure(Object obj) {
                                        BLELogUtil.e(TAG, "智能钥匙初始化失败,obj=" + obj);
                                        mainHandler.obtainMessage(0, buildBundle("注册智能钥匙", false)).sendToTarget();
                                    }
                                });
                        return true;
                    }
                }
        );
        requestLocationPermission();
        requestSdcardControlPermission();
        bleInit();
    }

    /**
     * 请求位置权限
     */
    private void requestLocationPermission(){
        PermisstionsUtil.checkSelfPermission(this, PermisstionsUtil.ACCESS_FINE_LOCATION, PermisstionsUtil.ACCESS_FINE_LOCATION_CODE, "6.0以上系统使用蓝牙需要位置权限",
                new PermisstionsUtil.PermissionResult() {
                    @Override
                    public void granted(int requestCode) {
                        BLELogUtil.e(TAG, "允许请求位置权限");
                    }

                    @Override
                    public void denied(int requestCode) {
                        Toast.makeText(MainActivity.this, "位置权限被阻止,请手动开启", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 请求读写sd卡权限
     */
    private void requestSdcardControlPermission(){
        PermisstionsUtil.checkSelfPermission(this, PermisstionsUtil.STORAGE, PermisstionsUtil.STORAGE_CODE, "写日志到后台需要读写sd卡权限",
                new PermisstionsUtil.PermissionResult() {
                    @Override
                    public void granted(int requestCode) {
                        BLELogUtil.e(TAG, "允许读写sd卡,日志将写入到文件,请在" + BLELogUtil.LOG_FILEPATH + "目录查看日志");
                    }

                    @Override
                    public void denied(int requestCode) {
                        BLELogUtil.e(TAG, "读写sd卡权限被阻止,日志将无法写入到文件");
                        BLELogUtil.LOG_WRITE_TO_FILE = false;
                    }
                });
    }

    /**
     * BLE初始化
     */
    private void bleInit() {
        new BLEInit(getApplication()).startBLEService(
                new OnInitListener() {
                    @Override
                    public void onInitSuccess(BluetoothAdapter bluetoothAdapter) {
                        BLELogUtil.e(TAG, "蓝牙初始化成功,bluetoothAdapter=" + bluetoothAdapter);

                    }

                    @Override
                    public void onInitFail(String errorCode) {
                        BLELogUtil.e(TAG, "蓝牙初始化失败,errorCode=" + errorCode);
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        BLELogUtil.e(TAG, "requestCode=" + requestCode + ",premissions[0]=" + permissions[0] + ",grantResults[0]=" + grantResults[0]);
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
                        mainHandler.obtainMessage(0, buildBundle("开门", true)).sendToTarget();
                    }

                    @Override
                    public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        BLELogUtil.d(TAG, "单次数据发送成功,gatt=" + gatt + ",data=" + BLEByteUtil.bytesToHexString(characteristic.getValue()) + ",status=" + status);
                    }

                    @Override
                    public void onWriteDataFail(String errorCode) {
                        BLELogUtil.e(TAG, "开门失败,errorCode=" + errorCode);
                        mainHandler.obtainMessage(0, buildBundle("开门", false)).sendToTarget();
                    }
                }
        );
    }

    /**
     * 小嘀锁开门获取通讯秘钥
     */
    private void getSecretkey(){
        XIAODISend.send(
                mac,
                XIAODIBLECMDType.BLE_CMDTYPE_GETBLELOCKSECRETKEY,
                null,
                false,
                new XIAODIDataReceived(
                        new byte[]{0x3A},
                        new OnXIAODIBLEListener.OnCommonListener() {
                            @Override
                            public void success(final XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, "获取通讯密钥，数据接收成功" + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                xiaodiOpen(xiaodiDataReceivedAnalyzer.getDataArea());
                                            }
                                        }
                                );
                            }

                            @Override
                            public void failure(String errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, "获取通讯密钥，数据接收失败,errorCode=" + errorCode + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                mainHandler.obtainMessage(0, buildBundle("开门", false)).sendToTarget();
                            }
                        }
                )
        );
    }

    /**
     * 小嘀开门
     * @param secretbytes   锁返回的密钥字节
     */
    private void xiaodiOpen(byte[] secretbytes){
        XIAODISend.send(
                mac,
                XIAODIBLECMDType.BLE_CMDTYPE_OPENBLELOCKENHANCE,
                new XIAODIData().setMobileaccount(mobile).setChannelpwd(channelpwd).setSecretkey(secretbytes),
                true,
                new XIAODIDataReceived(
                        new byte[]{0x39},
                        new OnXIAODIBLEListener.OnCommonListener() {
                            @Override
                            public void success(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, "小嘀开门，数据接收成功" + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                mainHandler.obtainMessage(0, buildBundle("开门", true)).sendToTarget();
                            }

                            @Override
                            public void failure(String errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, "小嘀开门，数据接收失败,errorCode=" + errorCode + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                mainHandler.obtainMessage(0, buildBundle("开门", false)).sendToTarget();
                            }
                        }
                )
        );
    }

    /**
     * 从智能钥匙获取通讯秘钥
     */
    private void getSecretKeyOnSmarKey(){
        XIAODISend.send(
                macSmartKey,
                XIAODIBLECMDType.BLE_CMDTYPE_SMARTKEYGETSECRETKEY,
                null,
                false,
                new XIAODIDataReceived(
                        new byte[]{0x3D},
                        new OnXIAODIBLEListener.OnCommonListener() {
                            @Override
                            public void success(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, "从智能钥匙获取通讯秘钥，数据接收成功" + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                registerOnSmartKey(channelpwd, openSecretKeyBytes, mac, xiaodiDataReceivedAnalyzer.getDataArea());
                            }

                            @Override
                            public void failure(String errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, "从智能钥匙获取通讯秘钥，数据接收失败,errorCode=" + errorCode + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                mainHandler.obtainMessage(0, buildBundle("注册智能钥匙", false)).sendToTarget();
                            }
                        }
                )
        );
    }

    /**
     * 智能钥匙上添加开门秘钥
     * @param mac   设备mac地址
     * @param channelPwd    信道密码
     * @param openSecretKeyBytes    开门秘钥
     * @param secretKeyBytes    通讯秘钥
     */
    private void registerOnSmartKey(final String channelPwd, final byte[] openSecretKeyBytes, final String mac, final byte[] secretKeyBytes){
        XIAODISend.send(
                macSmartKey,
                XIAODIBLECMDType.BLE_CMDTYPE_ADDSMARTKEY,
                new XIAODIData().setChannelpwd(channelPwd).setSecretkey13(openSecretKeyBytes).setLockmac(BLEStringUtil.reserveString(mac, true)).setSecretkey(secretKeyBytes),
                true,
                new XIAODIDataReceived(
                        new byte[]{0x27},
                        new OnXIAODIBLEListener.OnCommonListener() {
                            @Override
                            public void success(final XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                BLELogUtil.d(TAG, "智能钥匙上添加开门秘钥，数据接收成功" + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
//                                mainHandler.obtainMessage(0, buildBundle("注册智能钥匙", true)).sendToTarget();
                                                dismissDialog(dialog);
                                                getSecretKeyOnLock();
                                            }
                                        }
                                );
                            }

                            @Override
                            public void failure(String errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, "智能钥匙上添加开门秘钥，数据接收失败,errorCode=" + errorCode + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                mainHandler.obtainMessage(0, buildBundle("注册智能钥匙", false)).sendToTarget();
                            }
                        }
                )
        );
    }

    /**
     * 从锁上获取通讯秘钥
     */
    private void getSecretKeyOnLock(){
        dialog = buildProgressDialog(MainActivity.this, "正在添加智能钥匙", false);
        dialog.show();
        final String tag = "从锁上获取通讯秘钥";
        XIAODISend.send(
                mac,
                XIAODIBLECMDType.BLE_CMDTYPE_REGISTERSMARTKEYGETSECRETKEY,
                null,
                false,
                new XIAODIDataReceived(
                        new byte[]{0x3E},
                        new OnXIAODIBLEListener.OnCommonListener() {
                            @Override
                            public void success(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, tag + "，数据接收成功" + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                addSmartKeyOnLock(openSecretKeyBytes, xiaodiDataReceivedAnalyzer.getDataArea());
                            }

                            @Override
                            public void failure(String errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, tag + "，数据接收失败,errorCode=" + errorCode + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                mainHandler.obtainMessage(0, buildBundle(tag, false)).sendToTarget();
                            }
                        }
                )
        );
    }

    /**
     * 锁上添加智能钥匙
     * @param openSecretKeyBytes   开门秘钥
     * @param secretKeyBytes    通讯秘钥
     */
    private void addSmartKeyOnLock(final byte[] openSecretKeyBytes, final byte[] secretKeyBytes){
        final String tag = "锁上添加智能钥匙";
        XIAODISend.send(
                mac,
                XIAODIBLECMDType.BLE_CMDTYPE_ADDSMARTKEYAUTH,
                new XIAODIData().setSecretkey13(openSecretKeyBytes).setSecretkey(secretKeyBytes),
                true,
                new XIAODIDataReceived(
                        new byte[]{0x26},
                        new OnXIAODIBLEListener.OnCommonListener() {
                            @Override
                            public void success(XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, tag + "，数据接收成功" + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                mainHandler.obtainMessage(0, buildBundle(tag, true)).sendToTarget();
                            }

                            @Override
                            public void failure(String errorCode, XIAODIDataReceivedAnalyzer xiaodiDataReceivedAnalyzer) {
                                BLELogUtil.d(TAG, tag + "，数据接收失败,errorCode=" + errorCode + ",xiaodiDataReceivedAnalyzer=" + xiaodiDataReceivedAnalyzer);
                                mainHandler.obtainMessage(0, buildBundle(tag, false)).sendToTarget();
                            }
                        }
                )
        );
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

    /**
     * 构建bundle
     * @param desc  操作描述
     * @param flag  操作状态
     * @return
     */
    private Bundle buildBundle(String desc, Boolean flag){
        Bundle bundle = new Bundle();
        bundle.putString("desc", desc);
        bundle.putBoolean("flag", flag);
        return bundle;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog(dialog);
    }
}
