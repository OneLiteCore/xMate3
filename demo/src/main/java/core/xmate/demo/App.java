package core.xmate.demo;

import android.app.Application;

import core.xmate.MateDb;
import core.xmate.util.LogUtil;

/**
 * @author DrkCore
 * @since 2017-05-20
 */
public class App extends Application{

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;

        //初始化框架
        MateDb.init(this);
        LogUtil.setDebug(BuildConfig.DEBUG);
    }
}
