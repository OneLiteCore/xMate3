package core.base;

import android.app.Application;
import android.content.Context;

/**
 * @author DrkCore
 * @since 2016年2月23日21:16:33
 */
public final class Core {

	/* 单例模式 */

    private static final Core INSTANCE = new Core();

    public Context getAppContext() {
        if (appContext == null) {
            throw new IllegalStateException("在使用CoreBase框架前并未初始化");
        }
        return appContext;
    }

    public static Core getInstance() {
        return INSTANCE;
    }

    private Core() {// 禁止外部创建实例
    }
	
	/* 初始化 */

    private Context appContext;

    /**
     * 初始化框架。建议在{@link Application#onCreate()}执行初始化。
     *
     * @param app
     * @return
     */
    public Core init(Application app) {
        this.appContext = app;
        return this;
    }
	
	/* Dev标志 */

    private boolean devModeEnable;

    public boolean isDevModeEnable() {
        return devModeEnable;
    }

    public Core setDevModeEnable(boolean devModeEnable) {
        this.devModeEnable = devModeEnable;
        return this;
    }

    public Core setDevModeEnable() {
        devModeEnable = true;
        return this;
    }

}
