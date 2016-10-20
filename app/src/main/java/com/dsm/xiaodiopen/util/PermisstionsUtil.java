package com.dsm.xiaodiopen.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by yanfa on 2016/8/4.
 * 统一权限管理
 */
@TargetApi(Build.VERSION_CODES.M)
public class PermisstionsUtil {
    private static String tag = PermisstionsUtil.class.getSimpleName();
    //拍照权限
    public static String CAMERA = Manifest.permission.CAMERA;
    public static int CAMERA_CODE = 0x1101;
    //读取联系人权限
    public static String CONTACTS = Manifest.permission.READ_CONTACTS;
    public static int CONTACTS_CODE = 0x1102;
    //读写权限
    public static String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static int STORAGE_CODE = 0x1104;
    //WIFI、位置权限
    public static String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static int ACCESS_FINE_LOCATION_CODE = 0x1105;

    private static Context mContext;
    private static int currentRequestCode;

    private static Boolean checkVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查权限
     *
     * @param context
     * @param permission
     * @param permissionCode
     * @return
     */
    public static void checkSelfPermission(Context context, String permission, int permissionCode, String msg, PermissionResult permissionResult) {
        mContext = context;
        PermisstionsUtil.currentRequestCode = permissionCode;
        PermisstionsUtil.permissionResult = permissionResult;
        if (checkVersion()) {
            //方式一
//            if (mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {//无权限
//                if (((Activity)mContext).shouldShowRequestPermissionRationale(permission)) {
//                    //解释需要权限的原因
//                    showExplain(msg, permission, permissionCode);
//                } else {
//                    // 不解释直接请求权限
//                    ((Activity)mContext).requestPermissions(new String[]{permission}, permissionCode);
//                }
//            } else {
//                //有权限
//                if (PermisstionsUtil.permissionResult != null) {
//                    PermisstionsUtil.permissionResult.granted(permissionCode);
//                }
//            }
            //方式二
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                //无权限
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permission)) {
                    //解释需要权限的原因
                    showExplain(msg, permission, permissionCode);
                } else {
                    // 不解释直接请求权限
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission}, permissionCode);
                }
            } else {
                //有权限
                if (PermisstionsUtil.permissionResult != null) {
                    try {
                        PermisstionsUtil.permissionResult.granted(permissionCode);
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                //无权限
                if (PermisstionsUtil.permissionResult != null) {
                    try {
                        PermisstionsUtil.permissionResult.denied(permissionCode);
                    } catch (Exception e) {
                    }
                }
            } else {
                //有权限
                if (PermisstionsUtil.permissionResult != null) {
                    try {
                        PermisstionsUtil.permissionResult.granted(permissionCode);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * 向用户解释为什么需要该权限
     *
     * @param msg
     * @param permission
     * @param permissionCode
     */
    private static void showExplain(String msg, final String permission, final int permissionCode) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("权限解释")
                .setMessage(msg)
                .setNegativeButton("取消", null)
                .setPositiveButton(
                        "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission}, permissionCode);
                            }
                        })
                .show();
    }

    /**
     * 请求权限回调（须在Activity权限回调中使用）
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (checkVersion()) {
            if (PermisstionsUtil.currentRequestCode == requestCode) {
                if (permissionResult != null) {
                    boolean isAllow = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (isAllow) {
                        try {
                            permissionResult.granted(requestCode);
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            permissionResult.denied(requestCode);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    private static PermissionResult permissionResult;

    public interface PermissionResult {
        //权限允许
        void granted(int requestCode);

        //权限拒绝
        void denied(int requestCode);
    }
}
