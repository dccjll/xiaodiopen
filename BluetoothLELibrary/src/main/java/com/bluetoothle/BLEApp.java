package com.bluetoothle;

import android.app.Application;

/**
 * Created by dessmann on 16/10/18.
 * 蓝牙库全局上下文
 */

public class BLEApp extends Application {

    public static BLEApp bleApp;

    @Override
    public void onCreate() {
        super.onCreate();
        bleApp = this;
    }
}
