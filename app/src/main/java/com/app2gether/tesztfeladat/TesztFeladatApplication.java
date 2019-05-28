package com.app2gether.tesztfeladat;

import android.app.Application;

import com.app2gether.tesztfeladat.singleton.FtpController;

public class TesztFeladatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FtpController.init();
    }
}
