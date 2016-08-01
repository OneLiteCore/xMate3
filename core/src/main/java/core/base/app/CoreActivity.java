package core.base.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

import core.base.R;
import core.base.common.Clearable;
import core.base.common.ClearableHolder;
import core.base.common.ClearableWrapper;
import core.base.common.RefreshRate;

/**
 * 封装了常用方法的Activity基类。
 *
 * @author DrkCore
 * @since 2015年8月1日12:59:01
 */
public abstract class CoreActivity extends AppCompatActivity {

    private boolean isInResumed;

    public boolean isInResumed() {
        return isInResumed;
    }

	/* 继承 */

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 回调各个初始化的方法
        // 如果某个初始化回调中调用了finish则不再进行后续的操作
        init(savedInstanceState);
        if (!isFinishing()) {
            initView(savedInstanceState);
        }
        if (!isFinishing()) {
            // 获取toolbar
            toolbarId = toolbarId != 0 ? toolbarId : R.id.toolbar;
            toolbar = (Toolbar) findViewById(toolbarId);
            if (toolbar != null) {//仅当toolbar存在时回调
                initToolbar(savedInstanceState, toolbar);
            }
        }
        if (!isFinishing()) {
            afterInit(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needRefreshOnResume()) {
            refresh();
        }
        isInResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInResumed = false;

        if (clearAllOnPauseEnable) {
            clearAllClearable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAllClearable();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (onBackKey()) {
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_MENU:
                if (onMenuKey()) {
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public final void onBackPressed() {
        super.onBackPressed();
    }

	/* 回调 */

    /**
     * 在{@link #onCreate(Bundle)}中首先调用，建议在此初始化数据相关的成员变量，比如从intent接受数据。 <br>
     * <br/>
     * 如果调用了{@link #finish()}方法则后续的初始化操作都不会执行。
     *
     * @param savedInstanceState
     */
    protected void init(Bundle savedInstanceState) {
    }

    /**
     * 在{@link #onCreate(Bundle)}之中于 {@link #init(Bundle)} 之后调用。建议在此初始化视图相关的操作，比如设置界面和绑定事件等。<br/>
     * <br/>
     * 如果调用了{@link #finish()}方法则后续的操作都不会执行。
     *
     * @param savedInstanceState
     */
    protected void initView(Bundle savedInstanceState) {
    }

    /**
     * 在{@link #onCreate(Bundle)}中于{@link #initView(Bundle)} 之后调用。<br/>
     * <br/>
     * {@link #configToolbarId(int)}在{@link #init(Bundle)}中提前设置用于查找toolbar的id；
     *
     * @param savedInstanceState
     * @param toolbar
     */
    protected void initToolbar(Bundle savedInstanceState, Toolbar toolbar) {
    }

    /**
     * 当所有的initXXX方法调用之后在{@link #onCreate(Bundle)}的末尾回调。
     *
     * @param savedInstanceState
     */
    protected void afterInit(Bundle savedInstanceState) {
    }

    protected boolean onBackKey() {
        if (backKeyListeners != null && !backKeyListeners.isEmpty()) {
            for (OnActivityBackKeyListener listener : backKeyListeners) {// 安装添加顺序依次调用
                if (listener.onActivityBackKey()) {
                    return true;// 有人捕获了该返回键，终止后续的回调
                }
            }
        }
        return false;
    }

    protected boolean onMenuKey() {
        if (menuKeyListeners != null && !menuKeyListeners.isEmpty()) {
            for (OnActivityMenuKeyListener listener : menuKeyListeners) {// 安装添加顺序依次调用
                if (listener.onActivityMenuKey()) {
                    return true;// 有人捕获了该菜单键，终止后续的回调
                }
            }
        }
        return false;
    }

	/* 接口 */

    public interface OnActivityBackKeyListener {

        /**
         * 当用户按下返回键时回调该方法。
         * 如果有多个监听器则按照添加的顺序来执行，一旦有一个监听器返回了true即表示捕获了事件，
         * 之后将会终止后续的监听器的回调。
         *
         * @return
         */
        boolean onActivityBackKey();

    }

    public interface OnActivityMenuKeyListener {

        /**
         * 当用户按下菜单键时回调该方法。
         * 如果有多个监听器则按照添加的顺序来执行，一旦有一个监听器返回了true即表示捕获了事件，
         * 之后将会终止后续的监听器的回调。
         *
         * @return
         */
        boolean onActivityMenuKey();

    }

    private List<OnActivityBackKeyListener> backKeyListeners;
    private List<OnActivityMenuKeyListener> menuKeyListeners;

    public void addOnActivityBackKeyListener(OnActivityBackKeyListener listener) {
        if (backKeyListeners == null) {// 创建list单例
            backKeyListeners = new ArrayList<>(1);// 默认大小1就够了
        }
        backKeyListeners.add(listener);
    }

    public void addOnActivityMenuKeyListener(OnActivityMenuKeyListener listener) {
        if (menuKeyListeners == null) {
            menuKeyListeners = new ArrayList<>(1);
        }
        menuKeyListeners.add(listener);
    }

    public void removeOnActivityBackKeyListener(OnActivityBackKeyListener listener) {
        if (backKeyListeners != null) {
            backKeyListeners.remove(listener);
        }
    }

    public void removeOnActivityMenuKeyListener(OnActivityMenuKeyListener listener) {
        if (menuKeyListeners != null) {
            menuKeyListeners.remove(listener);
        }
    }

	/* 界面刷新 */

    private RefreshRate refreshRate = RefreshRate.ONCE;
    private boolean onceRefreshed;

    /**
     * 获取当前的刷新频率，默认返回{@link RefreshRate#ONCE}，即只刷新一次。
     *
     * @return
     */
    public RefreshRate getRefreshRate() {
        return refreshRate;
    }

    protected final void setRefreshRate(RefreshRate refreshRate) {
        this.refreshRate = refreshRate;
    }

    private boolean needRefreshOnResume() {
        refreshRate = refreshRate != null ? refreshRate : RefreshRate.ONCE;
        switch (refreshRate) {
            case ALWAYS:
                return true;

            case ONCE:
                if (onceRefreshed) {
                    return false;
                } else {
                    onceRefreshed = true;
                    return true;
                }

            case NEVER:
            default:
                return false;
        }
    }

    protected void refresh() {
    }

	/* Toolbar控制 */

    private int toolbarId;
    private Toolbar toolbar;

    protected final Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * 指定用于从界面获取toolbar的id。请在{@link #setContentView(int)}之前调用该方法。
     *
     * @param toolbarId
     */
    protected final void configToolbarId(@IdRes int toolbarId) {
        this.toolbarId = toolbarId;
    }

    /**
     * 设置toolbar导航为返回按钮，并设置点击事件。
     * 该方法会调用{@link #setSupportActionBar(Toolbar)}
     *
     * @param toolbar
     * @param iconId          导航的图标，为0则不设置图标
     * @param onClickListener
     */
    protected final void configToolbarNav(Toolbar toolbar, int iconId, OnClickListener onClickListener) {
        if (iconId > 0) {
            toolbar.setNavigationIcon(iconId);
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    /**
     * 具体实现请参阅{@link #configToolbarNavToFinish(Toolbar, int)}
     */
    protected final void configToolbarNavToFinish(Toolbar toolbar) {
        configToolbarNavToFinish(toolbar, 0);
    }

    /**
     * 设置toolbar导航为返回按钮，并设置点击后关闭activity。
     * 具体实现请参阅{@link #configToolbarNav(Toolbar, int, OnClickListener)}
     *
     * @param toolbar 导航的按钮
     * @param iconId  导航的图标
     */
    protected final void configToolbarNavToFinish(Toolbar toolbar, int iconId) {
        configToolbarNav(toolbar, iconId, new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

	/* 视图层次 */

    /**
     * 添加视图到Activity根布局上。由于根布局是{@link android.widget.FrameLayout#}因而新添加的视图会悬浮在原有界面上，
     * 默认宽高均为{@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}。
     * 具体实现请参阅{@link #addContentView(View, ViewGroup.LayoutParams)}
     *
     * @param view
     */
    public final void addContentView(View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        super.addContentView(view, params);
    }

    /**
     * 从Activity的根布局中移除控件
     *
     * @param view
     */
    public final void removeContentView(View view) {
        getContentViewContainer().removeView(view);
    }

    /**
     * 获取通过setContentView(int)等方法设置的内容视图。
     *
     * @return
     */
    public final View getContentView() {
        return getContentViewContainer().getChildAt(0);
    }

    /**
     * 获取位于DecorView之下的内容视图的容器，即{@link #setContentView(int)}所填充到的父布局。
     *
     * @return
     */
    public final ViewGroup getContentViewContainer() {
        return (ViewGroup) getDecorViewContainer().findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * 通过getWindow().getDecorView()方法获取到的视图。该视图是当前activity对应的window的最顶层的视图，
     * 即在其之上再不存在任何视图了。其大小就是屏幕的大小。需要注意的是，状态栏是浮动在该View之上的。
     *
     * @return
     */
    public final ViewGroup getDecorViewContainer() {
        return (ViewGroup) getWindow().getDecorView();
    }

	/*Fragment操作*/

    private FragHelper fragHelper;

    protected final FragHelper getFragHelper() {
        if (fragHelper == null) {
            fragHelper = new FragHelper(getSupportFragmentManager());
        }
        return fragHelper;
    }

	/* 线程 */

    private Handler handler;

    /**
     * 获取当前线程的{@link Handler}。
     * 该方法是简单单例的，一次调用之后实例就会被缓存。
     *
     * @return
     */
    protected final Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    /**
     * 具体注释请参阅{@link Handler#post(Runnable)}。
     *
     * @param r
     * @return
     * @see #getHandler()
     */
    protected final boolean post(Runnable r) {
        return getHandler().post(r);
    }

    /**
     * 具体注释请参阅{@link Handler#postAtTime(Runnable, long)}。
     *
     * @param r
     * @param uptimeMillis
     * @return
     * @see #getHandler()
     */
    protected final boolean postAtTime(Runnable r, long uptimeMillis) {
        return getHandler().postAtTime(r, uptimeMillis);
    }

    /**
     * 具体注释请参阅{@link Handler#postAtTime(Runnable, Object, long)}。
     *
     * @param r
     * @param token
     * @param uptimeMillis
     * @return
     * @see #getHandler()
     */
    protected final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return getHandler().postAtTime(r, token, uptimeMillis);
    }

    /**
     * 具体注释请参阅{@link Handler#postDelayed(Runnable, long)}。
     *
     * @param r
     * @param delayMillis
     * @return
     * @see #getHandler()
     */
    protected final boolean postDelayed(Runnable r, long delayMillis) {
        return getHandler().postDelayed(r, delayMillis);
    }

    /**
     * 具体注释请参阅{@link Handler#postAtFrontOfQueue(Runnable)}。
     *
     * @param r
     * @return
     * @see #getHandler()
     */
    protected final boolean postAtFrontOfQueue(Runnable r) {
        return getHandler().postAtFrontOfQueue(r);
    }

	/*Clearable*/

    private ClearableHolder clearableHolder;
    private boolean clearAllOnPauseEnable;

    protected final void setClearAllOnPauseEnable(boolean clearAllOnPauseEnable) {
        this.clearAllOnPauseEnable = clearAllOnPauseEnable;
    }

    protected final <T> T addClearableEx(T t) {
        if (t instanceof Clearable) {
            addClearable((Clearable) t);
        } else if (t instanceof AsyncTask) {
            //如果是原生的AsyncTask的话包裹起来
            //CoreTask已经实现了Clearable接口所以无需担心
            AsyncTask task = (AsyncTask) t;
            addClearable(new ClearableWrapper(task));
        }
        return t;
    }

    /**
     * 保存clearable的弱引用
     *
     * @param clearable
     */
    protected final void addClearable(Clearable clearable) {
        if (clearableHolder == null) {
            clearableHolder = new ClearableHolder();
        }
        clearableHolder.add(clearable);
    }

    /**
     * clear所有保存着的引用。在{@link #onDestroy()}时自动回调。
     */
    protected final void clearAllClearable() {
        if (clearableHolder != null) {
            clearableHolder.clearAll();
        }
    }

}
