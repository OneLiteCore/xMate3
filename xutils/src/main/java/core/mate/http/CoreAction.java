package core.mate.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import core.mate.async.Clearable;
import core.mate.util.ClassUtil;
import core.mate.util.LogUtil;
import core.mate.view.ITaskIndicator;

/**
 * 基于xUtils的http模块访问服务器的基类。
 *
 * @param <Raw>原始数据类型
 * @param <Result>    最终业务需要的类型
 * @author DrkCore
 * @since 2015年11月26日20:40:07
 */
public abstract class CoreAction<Raw, Result> implements Clearable {

    /* 发送请求 */

    protected final Clearable requestPost(String url) {
        return requestPost(new RequestParams(url));
    }

    protected final Clearable requestGet(String url) {
        return requestGet(new RequestParams(url));
    }

    protected final Clearable requestPost(RequestParams params) {
        return request(HttpMethod.POST, params);
    }

    protected final Clearable requestGet(RequestParams params) {
        return request(HttpMethod.GET, params);
    }

    protected synchronized final Clearable request(HttpMethod method, RequestParams params) {
        if (cleared) {
            throw new IllegalStateException("该Action已经被清理，无法使用");
        }

        logUsedCount();

        if (timeOut > 0) {
            params.setConnectTimeout(timeOut);
        }
        if (retryTime > 0) {
            params.setMaxRetryCount(retryTime);
        }
        if (executor != null) {
            params.setExecutor(executor);
        }

        logSendRequest(method, params.getUri(), params);

        if (innerCallBack == null) {
            if (cacheEnable) {
                innerCallBack = new InnerCacheCallback();
            } else {
                innerCallBack = new InnerCallback();
            }
        }

        lastRequestTime = System.currentTimeMillis();
        lastRequestHandler = new ClearableWrapper(x.http().request(method, params, innerCallBack));
        return lastRequestHandler;
    }

    @WorkerThread
    protected final Result requestGetSync(String url) throws Throwable {
        return requestGetSync(new RequestParams(url));
    }

    protected final Result requestPostSync(String url) throws Throwable {
        return requestPostSync(new RequestParams(url));
    }

    @WorkerThread
    protected final Result requestGetSync(RequestParams params) throws Throwable {
        return requestSync(HttpMethod.GET, params);
    }

    @WorkerThread
    protected final Result requestPostSync(RequestParams params) throws Throwable {
        return requestSync(HttpMethod.POST, params);
    }

    @WorkerThread
    protected synchronized final Result requestSync(HttpMethod method, RequestParams params) throws Throwable {
        if (cleared) {
            throw new IllegalStateException("该Action已经被清理，无法使用");
        }

        logSendRequest(method, params.getUri(), params, true);

        if (innerCallBack == null) {
            if (cacheEnable) {
                innerCallBack = new InnerCacheCallback();
            } else {
                innerCallBack = new InnerCallback();
            }
        }

        logDevMsg("同步发起请求");

        lastRequestTime = System.currentTimeMillis();
        onPrepareParams(params);
        ResultHolder result = x.http().requestSync(method, params, innerCallBack);
        if (result.exception != null) {
            throw result.exception;
        }

        logSuccess(result.result);

        return result.result;
    }

	/* 内部回调 */

    public enum ActionState {

        WAITING, START, SUCCESS, ERROR, CANCELLED, CACHED, CLEARED

    }

    private class ResultHolder {

        public final Raw raw;
        public final Result result;
        public final IllegalDataException exception;

        public ResultHolder(Raw raw, Result result, IllegalDataException exception) {
            this.raw = raw;
            this.result = result;
            this.exception = exception;
        }
    }

    private class InnerCallback implements Callback.CommonCallback<ResultHolder>, Callback.ProgressCallback<ResultHolder>,
            Callback.PrepareCallback<Raw, ResultHolder>, Callback.TypedCallback<ResultHolder> {

        @Override
        public void onWaiting() {
            CoreAction.this.onWaiting();
        }

        @Override
        public void onStarted() {
            CoreAction.this.onStarted();
        }

        @Override
        public void onLoading(long total, long current, boolean isDownloading) {
            CoreAction.this.onLoading(total, current, isDownloading);
        }

        @Override
        public Type getLoadType() {
            return CoreAction.this.getLoadType();
        }

        @WorkerThread
        @Override
        public void prepare(RequestParams params) {
            CoreAction.this.onPrepareParams(params);
        }

        @Override
        public ResultHolder prepare(Raw rawData) {
            Result result = null;
            IllegalDataException exception = null;
            try {
                result = CoreAction.this.onPrepareResult(rawData);
            } catch (IllegalDataException e) {
                LogUtil.e(e);
                exception = e;
            }
            if (result != null) {
                onResultPrepared(result);
            }
            return new ResultHolder(rawData, result, exception);
        }

        @Override
        public void onSuccess(ResultHolder result) {
            if (result == null) {//当信任缓存时result永远为null
                return;
            }

            //一般请求
            if (result.exception == null) {
                CoreAction.this.onSuccess(result.result);
            } else {
                CoreAction.this.onError(null, result.exception, true);
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            CoreAction.this.onError(ex, null, isOnCallback);
        }

        @Override
        public void onCancelled(CancelledException cex) {
            CoreAction.this.onCancelled(cex);
        }

        @Override
        public void onFinished() {
            CoreAction.this.onFinished();
        }

    }

    private class InnerCacheCallback extends InnerCallback implements Callback.CacheCallback<ResultHolder> {

        @Override
        public boolean onCache(ResultHolder resultHolder) {
            Result result = resultHolder != null ? resultHolder.result : null;
            return result != null && CoreAction.this.onCache(result);
        }

    }

    private ActionState actionState;
    private InnerCallback innerCallBack;

    private boolean cacheEnable;

    public CoreAction setCacheEnable() {
        if (actionState == null) {
            this.cacheEnable = true;
            logEnableCache();
        } else {
            throw new IllegalStateException("当且仅当该Action从未发出请求时才允许启用cache");
        }
        return this;
    }

    public final ActionState getActionState() {
        return actionState;
    }

    protected abstract Type getLoadType();

    protected abstract Type getResultType();

    protected void onWaiting() {
        actionState = ActionState.WAITING;
        logActionState();

        if (listeners != null) {
            for (OnActionListener listener : listeners) {
                listener.onWaiting();
            }
        }

        if (indicators != null) {
            for (ITaskIndicator indicator : indicators) {
                if (indicator != null && !indicator.isProgressing()) {
                    indicator.showProgress();
                }
            }
        }
    }

    protected void onStarted() {
        actionState = ActionState.START;
        logActionState();

        if (listeners != null) {
            for (OnActionListener listener : listeners) {
                listener.onStarted();
            }
        }
    }

    /**
     * 配置{@link RequestParams}，该方法将在异步线程中执行。
     *
     * @param params
     */
    @WorkerThread
    protected void onPrepareParams(RequestParams params) {
    }

    protected void onLoading(long total, long current, boolean isDownloading) {
        logLoading(total, current, isDownloading);

        if (listeners != null) {
            for (OnActionListener listener : listeners) {
                listener.onLoading(total, current, isDownloading);
            }
        }
    }

    protected boolean onCache(Result result) {
        boolean trustCache = false;
        if (listeners != null) {
            for (OnActionListener<Result> listener : listeners) {
                //只要有一个监听者信任即可
                trustCache |= listener.onCache(result);
            }
        }
        logCache(result, trustCache);

        if (trustCache) {
            actionState = ActionState.CACHED;
            logActionState();
        }
        return trustCache;
    }

    protected abstract Result onPrepareResult(Raw rawData) throws IllegalDataException;

    /**
     * 从服务器放回数据，并且成功转化成最终对象时回调该方法。
     * 该方法将在工作线程之中调用，你可以在该方法中通过同步的方法将数据插入数据库或者写入本地文件里。
     *
     * @param result 转化后的最终对象，该参数必定不会为null
     */
    @WorkerThread
    protected void onResultPrepared(@NonNull Result result) {
        if (listeners != null) {
            for (OnActionListener<Result> listener : listeners) {
                listener.onResultPrepared(result);
            }
        }
    }

    protected void onSuccess(Result result) {
        actionState = ActionState.SUCCESS;
        logActionState();
        logSuccess(result);

        if (listeners != null) {
            for (OnActionListener<Result> listener : listeners) {
                listener.onSuccess(result);
            }
        }
    }

    protected void onError(Throwable thr, IllegalDataException e, boolean isOnCallback) {
        actionState = ActionState.ERROR;
        logActionState();

        if (listeners != null) {
            for (OnActionListener listener : listeners) {
                listener.onError(thr, e, isOnCallback);
            }
        }
    }

    protected void onCancelled(Callback.CancelledException cex) {
        actionState = ActionState.CANCELLED;
        logActionState();

        if (listeners != null) {
            for (OnActionListener listener : listeners) {
                listener.onCancelled(cex);
            }
        }
    }

    protected void onFinished() {
        logDevMsg("任务结束，最终状态为", actionState);
        logTime();
        logDevMsg("___________________________________");

        lastRequestHandler = null;//清空上一请求的句柄

        if (listeners != null) {
            for (OnActionListener listener : listeners) {
                listener.onFinished();
            }
        }
        if (indicators != null) {
            for (ITaskIndicator indicator : indicators) {
                if (indicator.isProgressing()) {
                    indicator.hideProgress();
                }
            }
        }
        if (clearOnFinishedEnable) {
            clear();
        }
    }

    protected void onClear() {
        actionState = ActionState.CLEARED;
        logActionState();
    }

	/* 外部接口 */

    public interface OnActionListener<Result> {

        /**
         * 当请求还在请求队列等待时回调该方法，即该方法是请求中最先被调用的
         */
        void onWaiting();

        /**
         * 请求等待结束，开始向服务器发起请求时回调该方法。
         */
        void onStarted();

        /**
         * 当下载时候回调该方法
         *
         * @param total
         * @param current
         * @param isDownloading
         */
        void onLoading(long total, long current, boolean isDownloading);

        boolean onCache(Result result);

        /**
         * 从服务器放回数据，并且成功转化成最终对象时回调该方法。
         * 该方法将在工作线程之中调用，你可以在该方法中通过同步的方法将数据插入数据库或者写入本地文件里。
         *
         * @param result 转化后的最终对象，该参数必定不会为null
         */
        @WorkerThread
        void onResultPrepared(@NonNull Result result);

        void onSuccess(Result result);

        /**
         * 当请求失败或者服务器的数据转化成Result失败的时候回调该方法。
         *
         * @param ex           由xUtils框架返回的异常，当数据转化失败的时候该参数为null
         * @param e            数据转化失败时该参数不为null
         * @param isOnCallback
         */
        void onError(@Nullable Throwable ex, @Nullable IllegalDataException e, boolean isOnCallback);

        void onCancelled(Callback.CancelledException cex);

        void onFinished();
    }

    private List<OnActionListener<Result>> listeners;

    public final OnActionListener<Result> removeOnActionListener(OnActionListener<Result> listener) {
        return listeners != null && listeners.remove(listener) ? listener : null;
    }

    /**
     * 设置http请求的回调。请在发送请求之前调用，否则没有效果。
     *
     * @param listener
     */
    public final CoreAction<Raw, Result> addOnActionListener(OnActionListener<Result> listener) {
        if (listener != null) {
            if (this.listeners == null) {
                this.listeners = new ArrayList<>();
            }
            this.listeners.add(listener);
        }
        return this;
    }

	/*请求配置*/

    private int timeOut = 0;
    private int retryTime = 3;
    private Executor executor;

    public final int getRetryTime() {
        return retryTime;
    }

    public final int getTimeOut() {
        return timeOut;
    }

    public Executor getExecutor() {
        return executor;
    }

    public final CoreAction setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public final CoreAction setRetryTime(int retryTime) {
        this.retryTime = retryTime;
        return this;
    }

    public CoreAction setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    /* 请求记录 */

    private Clearable lastRequestHandler;
    private long lastRequestTime;

    /**
     * 判断最后一次请求是否正在工作。
     *
     * @return
     */
    public final boolean isLastRequestWorking() {
        return lastRequestHandler != null;
    }

    /**
     * 获取上一次请求发出的时间戳。如果从未发出过请求则返回0。
     *
     * @return
     */
    public final long getLastRequestTime() {
        return lastRequestTime;
    }

    /**
     * 取消最后一次的请求，当且仅当{@link #isLastRequestWorking()}成立时取消。
     */
    public final void cancelLastRequest() {
        if (isLastRequestWorking()) {
            lastRequestHandler.clear();
        }
    }

	/* 用户指示 */

    private List<ITaskIndicator> indicators;

    public final CoreAction<Raw, Result> addIndicator(ITaskIndicator indicator) {
        if (indicator != null) {
            if (this.indicators == null) {
                this.indicators = new ArrayList<>();
            }
            this.indicators.add(indicator);
        }
        return this;
    }

    public final void clearIndicator() {
        if (indicators != null) {
            for (ITaskIndicator indicator : indicators) {
                if (indicator.isProgressing()) {
                    indicator.hideProgress();
                }
            }
            indicators.clear();
        }
    }

	/* 开发模式 */

    private int usedCount;
    private Boolean isDevModeEnable;
    private LogUtil.Builder logBuilder;

    protected final boolean isDevModeEnable() {
        if (isDevModeEnable == null) {
            //成员变量的速度比其他类的静态变量速度要快一点
            isDevModeEnable = x.isDebug();
        }
        return isDevModeEnable;
    }

    protected final void logDevMsg(Object... msgs) {
        if (isDevModeEnable()) {
            if (logBuilder == null) {
                logBuilder = LogUtil.newBuilder();
                logBuilder.setTag(ClassUtil.getTypeName(getClass()) + "@" + hashCode());
            }

            logBuilder.append(msgs).log();
        }
    }

    private void logUsedCount() {
        logDevMsg("接口调用次数 = ", ++usedCount);
    }

    private void logSendRequest(HttpMethod method, String url, RequestParams params) {
        logSendRequest(method, url, params, false);
    }

    private void logSendRequest(HttpMethod method, String url, RequestParams params, boolean sync) {
        if (isDevModeEnable()) {// 开发模式
            logDevMsg("HttpMethod: ", method);
            logDevMsg("URL: ", url);

            // log输出参数
            List<KeyValue> kvs = params != null ? params.getAllParams() : null;
            if (kvs != null && !kvs.isEmpty()) {
                for (KeyValue kv : kvs) {
                    logDevMsg("Params: ", kv.key, " : ", kv.value);
                }
            } else {
                logDevMsg("该Action没有Params参数");
            }

        }
    }

    private void logEnableCache() {
        logDevMsg("已经启动cache缓存，此时onPrepareResult可能会多运行一次以准备缓存结果。");
    }

    private void logCache(Result result, boolean trustCache) {
        if (isDevModeEnable()) {
            logDevMsg("存在缓存");
            logResult(result);
            logDevMsg("信赖缓存：", trustCache);
            logDevMsg("————————————");
        }
    }

    private void logLoading(long total, long current, boolean isDownloading) {
        if (isDevModeEnable()) {
            logDevMsg("loading：total_", total, " current_", current, " isDownloading_", isDownloading);
        }
    }

    private void logSuccess(Result result) {
        if (isDevModeEnable()) {
            logDevMsg("成功接收到数据：");
            logResult(result);
        }
    }

    private void logResult(Result result) {
        if (isDevModeEnable()) {
            if (result == null) {
                logDevMsg("转化后的Result为null");
                return;
            }

            try {
                Type type = getResultType();
                if (type instanceof Class) {// Class类型
                    Class<?> clazz = (Class<?>) type;
                    String pkgPath = clazz.getCanonicalName();
                    if (pkgPath.contains("java.lang") || pkgPath.contains("java.io")) {// 原始类型或者io包下
                        logDevMsg("转化后的结果: ", result);
                    } else {// 普通的JavaBean
                        String resultJson = JSON.toJSONString(result);
                        logDevMsg("转成JSON: ", resultJson);
                    }
                } else {
                    String typeName = type.toString();
                    if (typeName.startsWith("java.util.") && typeName.contains("List<") && typeName.endsWith(">")) {// 泛型符合“java.util.***List<core.test.model.People>”格式
                        List<?> list = (List<?>) result;
                        logDevMsg("列表长度: ", list.size());
                        if (!list.isEmpty()) {
                            String jsonResult = JSON.toJSONString(list.get(0));
                            logDevMsg("第一个元素转成JSON: ", jsonResult);
                        }
                    }
                }
            } catch (Throwable e) {
                LogUtil.e(e);
            }
        }
    }

    private void logTime() {
        if (isDevModeEnable()) {
            long timeUsed = System.currentTimeMillis() - lastRequestTime;
            logDevMsg("距离上次发起请求耗时(ms):", timeUsed);
        }
    }

    private void logActionState() {
        if (isDevModeEnable()) {
            if (actionState == ActionState.CLEARED) {
                logDevMsg("Action已清理");
            } else {
                logDevMsg("Action状态：", actionState != null ? actionState.name() : null);
            }
        }
    }

	/* 清理数据 */

    private boolean cleared;
    private boolean clearOnFinishedEnable;

    /**
     * 判断是否在请求结束后自动调用{@link #clear()}清空数据，默认返回false，即不主动清空。
     *
     * @return
     */
    public final boolean isClearOnFinishedEnable() {
        return clearOnFinishedEnable;
    }

    public final CoreAction<Raw, Result> setClearOnFinishedEnable(boolean clearOnFinishedEnable) {
        this.clearOnFinishedEnable = clearOnFinishedEnable;
        return this;
    }

    @Override
    public final boolean isCleared() {
        return cleared;
    }

    /**
     * 清空数据，并终止使用。如果当前还有请求正在工作将会被cancel掉。
     * 该方法同样会清空ProgressIndicator。
     */
    @Override
    public final void clear() {
        if (!cleared) {
            cleared = true;
            if (isLastRequestWorking()) {
                cancelLastRequest();
            }
            clearIndicator();
            if (listeners != null) {
                listeners.clear();
                listeners = null;
            }
            onClear();
        }
    }

}
