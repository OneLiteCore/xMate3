package core.xmate;

import android.app.Application;

import org.xutils.x;

import core.mate.Core;

/**
 * @author DrkCore
 * @since 2016-09-04
 */
public class App extends Application {

    private static App instance = null;

    public static App getInstance() {
        return instance;
    }

    /*继承*/

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //初始化CoreMate
        Core.getInstance().init(this);
        Core.getInstance().setDevModeEnable();

        //初始化xUtils
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
