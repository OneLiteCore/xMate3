package core.base.http;

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
import java.util.List;

import core.base.Core;
import core.base.common.Clearable;
import core.base.common.ITaskIndicator;
import core.base.util.ClassUtil;
import core.base.util.LogUtil;

/**
 * 基于xUtils的http访问服务器的基类。
 * * <b>注意，当你实现该类的子类时请保留第一个泛型参数为该Action的结果的数据类型。</b>
 *
 * @author DrkCore
 * @since 2015年11月26日20:40:07
 */
public abstract class CoreAction<Result> implements Clearable {

    public static class ClearableWrapper implements Clearable {

        private final Callback.Cancelable cancelable;

        public ClearableWrapper(Callback.Cancelable cancelable) {
            this.cancelable = cancelable;
        }

        @Override
        public boolean isCleared() {
            return cancelable.isCancelled();
        }

        @Override
        public void clear() {
            cancelable.cancel();
        }
    }

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

    protected final Clearable request(HttpMethod method, RequestParams params) {
        if (cleared) {
            throw new IllegalStateException("该Action已经被清理，无法使用");
        }

        logUsedCount();

        if (isLastRequestWorking()) {// 上一个请求还在工作
            conflictOperation = conflictOperation != null ? conflictOperation : ConflictOperation.LET_REQUEST_FLY;
            logConflict(conflictOperation);
            switch (conflictOperation) {
                case CANCEL_LAST_REQUEST:
                    cancelLastRequest();// 取消上一次的请求
                    break;

                case ABANDON_CURRENT_REQUEST:
                    return lastRequestHandler;// 放弃当前请求，并返回上一次的handler

                case LET_REQUEST_FLY:// 默认情况不做处理
                default:
                    break;
            }
        }

        onPrepareRequestParams(params);
        logSendRequest(method, params.getUri(), params);

        lastRequestTime = System.currentTimeMillis();
        lastRequestHandler = new ClearableWrapper(x.http().request(method, params, innerCallBack));
        return lastRequestHandler;
    }

	/* 内部回调 */

    public enum ActionState {

        WAITING, START, SUCCESS, ERROR, CANCELLED, CLEARED

    }

    private class ResultHolder {

        public final Result result;
        public final IllegalDataException exception;

        public ResultHolder(Result result, IllegalDataException exception) {
            this.result = result;
            this.exception = exception;
        }
    }

    private class InnerCallback implements Callback.CommonCallback<ResultHolder>, Callback.ProgressCallback<ResultHolder>, Callback.PrepareCallback<String, ResultHolder> {

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
        public ResultHolder prepare(String rawData) {
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
            return new ResultHolder(result, exception);
        }

        @Override
        public void onSuccess(ResultHolder result) {
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

    private ActionState actionState;
    private InnerCallback innerCallBack = new InnerCallback();

    public final ActionState getActionState() {
        return actionState;
    }

    /**
     * 在请求发送之前配置RequestParams。
     *
     * @param params
     */
    protected void onPrepareRequestParams(RequestParams params) {
        if (timeOut > 0) {
            params.setConnectTimeout(timeOut);
        }
        if (retryTime > 0) {
            params.setMaxRetryCount(retryTime);
        }
    }

    protected void onWaiting() {
        actionState = ActionState.WAITING;
        logActionState();

        if (listener != null) {
            listener.onWaiting();
        }

        if (indicator != null && !indicator.isProgressing()) {
            indicator.showProgress();
        }
    }

    protected void onStarted() {
        actionState = ActionState.START;
        logActionState();

        if (listener != null) {
            listener.onStarted();
        }
    }

    protected void onLoading(long total, long current, boolean isDownloading) {
        logLoading(total, current, isDownloading);

        if (listener != null) {
            listener.onLoading(total, current, isDownloading);
        }
    }

    /**
     * 将带有数据的字符串转化为真正需要用的数据类型，如将json的字符串转为object。
     * <br/>
     * <br/>
     * 根据{@link #isAutoConvertEnable()}的结果有以下情况：
     * <li/>为true，则默认使用{@link #onAutoConvertResult(String)}
     * 自动转化数据。如果自动转化失败则使用
     * {@link #onConvertComplexResult(String)}转化；
     * <li/>为false时，直接使用{@link #onConvertComplexResult(String)}
     * 转化。
     *
     * @param rawData
     * @return
     * @throws IllegalDataException
     */
    private Result onPrepareResult(String rawData) throws IllegalDataException {
        logRawData(rawData);

        rawData = onPrepareDataString(rawData);
        Result result = null;
        if (autoConvertEnable) {// 自动转化数据
            result = onAutoConvertResult(rawData);
        }
        if (result == null) {// 手动转换
            result = onConvertComplexResult(rawData);
        }

        if (getResultType() != Void.class && result == null) {// 我就干了，自动转换和手动转换都不行，简直呵呵哒
            throw new IllegalStateException("当Result类型不为Void且onAutoConvertResult不可用时，onConvertComplexResult方法不允许返回null。");
        }

        return result;
    }

    /**
     * 从服务器放回数据，并且成功转化成最终对象时回调该方法。
     * 该方法将在工作线程之中调用，你可以在该方法中通过同步的方法将数据插入数据库或者写入本地文件里。
     *
     * @param result 转化后的最终对象，该参数必定不会为null
     */
    @WorkerThread
    protected void onResultPrepared(@NonNull Result result) {
    }

    protected void onSuccess(Result result) {
        actionState = ActionState.SUCCESS;
        logActionState();
        logSuccessResult(result);

        if (listener != null) {
            listener.onSuccess(result);
        }
    }

    protected void onError(Throwable thr, IllegalDataException e, boolean isOnCallback) {
        actionState = ActionState.ERROR;
        logActionState();

        if (listener != null) {
            listener.onError(thr, e, isOnCallback);
        }
    }

    protected void onCancelled(Callback.CancelledException cex) {
        actionState = ActionState.CANCELLED;
        logActionState();

        if (listener != null) {
            listener.onCancelled(cex);
        }
    }

    protected void onFinished() {
        logDevMsg("任务结束，最终状态为", actionState);
        logTime();
        logDevMsg("___________________________________");

        lastRequestHandler = null;//清空上一请求的句柄

        if (listener != null) {
            listener.onFinished();
        }
        if (indicator != null && indicator.isProgressing()) {
            indicator.hideProgress();
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

    private OnActionListener<Result> listener;

    /**
     * 设置http请求的回调。请在发送请求之前调用，否则没有效果。
     *
     * @param listener
     */
    public final CoreAction<Result> setOnActionListener(OnActionListener<Result> listener) {
        this.listener = listener;
        return this;
    }

	/*请求配置*/

    private int timeOut = 0;
    private int retryTime = 3;

    public final int getRetryTime() {
        return retryTime;
    }

    public final int getTimeOut() {
        return timeOut;
    }

    public final CoreAction setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public final CoreAction setRetryTime(int retryTime) {
        this.retryTime = retryTime;
        return this;
    }

	/* 自动转换 */

    private boolean autoConvertEnable = true;

    protected final boolean isAutoConvertEnable() {
        return autoConvertEnable;
    }

    protected final void setAutoConvertEnable(boolean autoConvertEnable) {
        this.autoConvertEnable = autoConvertEnable;
    }

    /**
     * 从文本中提取用于转化成对象的数据字符串，比如你的服务器返回的数据如下：
     * <b>{"code":OK,"msg":{"name":"小明","age":17} }</b>，其中msg标明的JSON字符串才是最终的数据字符串。
     * 如果数据不合法，比如数据中的code标明的是OK，设计上而言你应该抛出{@link IllegalDataException}。
     * 默认情况下直接返回。
     *
     * @param dataStr
     * @return
     * @throws IllegalDataException
     */
    protected String onPrepareDataString(String dataStr) throws IllegalDataException {
        return dataStr;
    }

    /**
     * 获取当前类中的泛型的具体类型，并自动将{@link #onPrepareDataString(String)}
     * 中返回的数据转为指定的Result对象。
     *
     * @param dataStr
     * @return
     */
    private Result onAutoConvertResult(String dataStr) {
        Type type = getResultType();
        logAutoConvertType(type);

        //如果适配的是Void类型的话，直接返回null
        if (type == Void.class) {
            return null;
        }

        try {
            if (type instanceof Class && type == String.class) {
                return (Result) dataStr;
            } else {
                return JSON.parseObject(dataStr, type);
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return null;
    }

    /**
     * 手动将数据的字符串转为对象。
     *
     * @param dataStr
     * @return
     * @throws IllegalDataException 当服务器的数据不合法时请抛出该异常。
     */
    protected Result onConvertComplexResult(String dataStr) throws IllegalDataException {
        return null;
    }

    private Type getResultType() {
        Type[] types = ClassUtil.getGenericParametersType(getClass());
        return types[types.length - 1];
    }

	/* 请求记录 */

    public enum ConflictOperation {

        /**
         * 取消上一个请求后发起新的请求
         */
        CANCEL_LAST_REQUEST,
        /**
         * 废弃当前的请求，并返回上一次的handler
         */
        ABANDON_CURRENT_REQUEST,
        /**
         * 不做任何处理，让子弹飞。该操作将会使得当前的请求成为最后一次请求。
         */
        LET_REQUEST_FLY

    }

    private Clearable lastRequestHandler;
    private long lastRequestTime;
    private ConflictOperation conflictOperation = ConflictOperation.LET_REQUEST_FLY;

    /**
     * 当上一个请求还未结束时又来了一个请求时的处理方法。
     * 默认情况下返回{@link ConflictOperation#LET_REQUEST_FLY}，即不做处理。
     *
     * @return
     */
    public final ConflictOperation getConflictOperation() {
        return conflictOperation;
    }

    public final CoreAction<Result> setConflictOperation(ConflictOperation conflictOperation) {
        this.conflictOperation = conflictOperation;
        return this;
    }

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

    private ITaskIndicator indicator;

    public final CoreAction<Result> setIndicator(ITaskIndicator indicator) {
        this.indicator = indicator;
        return this;
    }

    public final void clearIndicator() {
        if (indicator != null) {
            if (indicator.isProgressing()) {
                indicator.hideProgress();
            }
            indicator = null;
        }
    }

	/* 标签携带数据 */

    private Object tag;

    public CoreAction<Result> setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public Object getTag() {
        return tag;
    }

	/* 开发模式 */

    private int usedCount;
    private Boolean isDevModeEnable;
    private LogUtil.Builder logBuilder;

    private boolean isDevModeEnable() {
        if (isDevModeEnable == null) {
            //成员变量的速度比其他类的静态变量速度要快一点
            isDevModeEnable = Core.getInstance().isDevModeEnable();
        }
        return isDevModeEnable;
    }

    protected final void logDevMsg(Object... msgs) {
        if (isDevModeEnable()) {
            if (logBuilder == null) {
                logBuilder = LogUtil.newBuilder();
                logBuilder.setTag(ClassUtil.getTypeName(getClass()));
            }

            logBuilder.append(msgs).log();
        }
    }

    private void logUsedCount() {
        logDevMsg("接口调用次数 = ", ++usedCount);
    }

    private void logSendRequest(HttpMethod method, String url, RequestParams params) {
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

    private void logConflict(ConflictOperation operation) {
        if (isDevModeEnable()) {// 开发模式
            operation = operation != null ? operation : null;
            if (operation != null) {
                switch (operation) {
                    case ABANDON_CURRENT_REQUEST:
                        logDevMsg("两个请求同时存在，抛弃新的请求");
                        break;

                    case CANCEL_LAST_REQUEST:
                        logDevMsg("两个请求同时存在，取消上一请求");
                        break;

                    case LET_REQUEST_FLY:
                        logDevMsg("两个请求同时存在，让它们飞一会");
                        break;
                }
            } else {
                logDevMsg("两个请求同时存在，然而ConflictOperation为null");
            }
        }
    }

    private void logLoading(long total, long current, boolean isDownloading) {
        if (isDevModeEnable()) {
            logDevMsg("loading：total_", total, " current_", current, " isDownloading_", isDownloading);
        }
    }

    private void logRawData(String rawData) {
        if (isDevModeEnable()) {
            logDevMsg("原始数据：", rawData);
        }
    }

    private void logAutoConvertType(Type type) {
        if (isDevModeEnable()) {
            logDevMsg("Result泛型: ", type.toString());
        }
    }

    private void logSuccessResult(Result result) {
        if (isDevModeEnable()) {
            if (result == null) {
                logDevMsg("转化后的Result为null");
                return;
            }

            try {
                Type type = ClassUtil.getGenericParametersType(getClass())[0];
                if (type instanceof Class) {// Class类型
                    Class<?> clazz = (Class<?>) type;
                    String pkgPath = clazz.getCanonicalName();
                    if (pkgPath.contains("java.lang")) {// 原始类型
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

    public final CoreAction<Result> setClearOnFinishedEnable(boolean clearOnFinishedEnable) {
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
            listener = null;
            onClear();
        }
    }

}
