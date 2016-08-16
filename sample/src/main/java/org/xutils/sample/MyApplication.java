package org.xutils.sample;

import android.app.Application;

import org.xutils.x;

import core.mate.Core;

/**
 * Created by wyouflf on 15/10/28.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Core.getInstance().init(this).setDevModeEnable();

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能
    }
}
