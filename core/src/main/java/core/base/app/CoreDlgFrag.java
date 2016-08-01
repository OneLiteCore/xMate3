package core.base.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import core.base.common.Clearable;
import core.base.common.ClearableHolder;
import core.base.common.ClearableWrapper;
import core.base.common.RefreshRate;
import core.base.util.ClassUtil;

/**
 * 简单封装的DialogFrag基类
 *
 * @author DrkCore
 * @since 2015年10月17日12:48:35
 */
public abstract class CoreDlgFrag extends DialogFragment {

	/* 继承 */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dismissIfRecreated && savedInstanceState != null) {
            dismiss();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dlg = getDialog();
        Window dlgWin = dlg.getWindow();
        onPrepareDialogWindow(savedInstanceState, dlg, dlgWin);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRefreshOnResume()) {
            refresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (clearAllOnPauseEnable) {
            clearAllClearable();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearAllClearable();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }

	/* 模板方法 */

    private int winAnimStyle;

    protected final CoreDlgFrag setWinAnimStyle(int winAnimStyle) {
        this.winAnimStyle = winAnimStyle;
        return this;
    }

    /**
     * 配置DialogFragment的样式，在{@link #onCreate(Bundle)}中回调。
     *
     * @param savedInstanceState
     */
    protected void onPrepareDialogStyle(@Nullable Bundle savedInstanceState) {
    }

    /**
     * 配置对话框的window，将在{@link #onActivityCreated(Bundle)}中回调。
     *
     * @param savedInstanceState
     * @param dlg
     * @param dlgWin
     */
    protected void onPrepareDialogWindow(@Nullable Bundle savedInstanceState, Dialog dlg, Window dlgWin) {
        setCancelOnTouchOutSideEnable(cancelOnTouchOutSideEnable);
        if (winAnimStyle > 0) {
            dlgWin.setWindowAnimations(winAnimStyle);
        }
    }

	/* 外部接口 */

    public interface OnDismissListener {

        void onDismiss(CoreDlgFrag dlgFrag);

    }

    private OnDismissListener onDismissListener;

    public final CoreDlgFrag setOnDismissListener(OnDismissListener onDismissListener) {
        if (onDismissListener == this) {// 自己给自己设置监听？这并不好吧……
            throw new IllegalArgumentException("不允许将自己设为自己的监听器，这可能导致无限递归！如果你需要该方法，请直接重写。");
        }
        this.onDismissListener = onDismissListener;
        return this;
    }

	/* 刷新 */

    private RefreshRate refreshRate;
    private boolean onceRefreshed;

    public final RefreshRate getRefreshRate() {
        return refreshRate;
    }

    public final void setRefreshRate(RefreshRate refreshRate) {
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

    public void refresh() {
    }

	/* 显示 */

    /**
     * 显示对话框，使用类名作为tag<br>
     *
     * @param context
     */
    public final void show(FragmentActivity context) {
        show(context.getSupportFragmentManager(), ClassUtil.getTypeName(this));
    }

    /**
     * 显示对话框，使用类名作为tag
     *
     * @param fragMgr
     */
    public final void show(FragmentManager fragMgr) {
        show(fragMgr, ClassUtil.getTypeName(this));
    }

    /**
     * 显示对话框，使用类名作为tag
     *
     * @param frag
     */
    public final void show(Fragment frag) {
        show(frag.getChildFragmentManager());
    }

    private boolean dismissIfRecreated = true;
    private boolean cancelOnTouchOutSideEnable = true;

    public boolean isDismissIfRecreated() {
        return dismissIfRecreated;
    }

    public CoreDlgFrag setDismissIfRecreated(boolean dismissIfRecreated) {
        this.dismissIfRecreated = dismissIfRecreated;
        return this;
    }

    /**
     * 是否启用点击外部区域关闭对话框。默认为true。
     *
     * @return
     */
    public final boolean isCancelOnTouchOutSideEnable() {
        return cancelOnTouchOutSideEnable;
    }

    /**
     * 设置开闭点击外部区域关闭对话框的功能，默认情况下会启用。
     *
     * @param cancelOnTouchOutSideEnable
     * @return
     */
    public final CoreDlgFrag setCancelOnTouchOutSideEnable(boolean cancelOnTouchOutSideEnable) {
        this.cancelOnTouchOutSideEnable = cancelOnTouchOutSideEnable;
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(cancelOnTouchOutSideEnable);
        }
        return this;
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

    /*Fragment操作*/

    private FragHelper fragHelper;

    protected final FragHelper getFragHelper() {
        if (fragHelper == null) {
            fragHelper = new FragHelper(getFragmentManager());
        }
        return fragHelper;
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

	/* 拓展 */

    /**
     * 从父容器中获取回调。
     *
     * @param listener
     * @param <T>
     * @return
     */
    protected final <T> T getListenerFromParent(Class<?> listener) {
        if (!listener.isInterface()) {
            throw new IllegalArgumentException("getListenerFromParent()方法只允许获取接口");
        }
        Object obj;
        if (listener.isInstance(obj = getTargetFragment())/*targetFragment为最优先级*/
                || listener.isInstance(obj = getParentFragment()/*父fragment为次*/)
                || listener.isInstance(obj = getContext())/*Activity最次*/) {
            return (T) obj;
        }
        return null;
    }

    /**
     * 调用activity的{@link Activity#overridePendingTransition(int, int)}
     * 方法来播放activity之间的动画。
     *
     * @param enterAnim
     * @param exitAnim
     */
    protected final void overridePendingTransition(int enterAnim, int exitAnim) {
        getActivity().overridePendingTransition(enterAnim, exitAnim);
    }
}
