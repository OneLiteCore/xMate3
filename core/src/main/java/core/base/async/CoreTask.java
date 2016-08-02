package core.base.async;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import core.base.Core;
import core.base.common.Clearable;
import core.base.util.ClassUtil;
import core.base.util.LogUtil;
import core.base.common.ITaskIndicator;

/**
 * 初步封装的异步任务基类。<br/>
 * <b>注意，当你实现该类的子类时请保留最后一个泛型参数为该异步任务的Result类型。
 * 否则将会因为在运行时无法确定Result类型而导致抛出异常。</b>
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 * @author DrkCore
 * @since 2015年9月29日10:28:34
 */
public abstract class CoreTask<Params, Progress, Result> extends AsyncTask<Params, Progress, CoreTask.ResultHolder<Result>> implements Clearable {

    protected static class ResultHolder<Result> {

        public final Result result;
        public final Exception e;

        private ResultHolder(Result result, Exception e) {
            this.result = result;
            this.e = e;
        }

    }

	/* 继承 */

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        taskState = TaskState.PRE;
        logTaskState();

        if (indicator != null && !indicator.isProgressing()) {
            indicator.showProgress();
        }

        onStart();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final ResultHolder<Result> doInBackground(Params... params) {
        taskState = TaskState.DO;
        logTaskState();

        try {
            Result result = doInBack(params != null && params.length >= 1 ? params[0] : null);
            if (onTaskListener != null) {
                onTaskListener.onPrepareResult(result);
            }
            return new ResultHolder<>(result, null);
        } catch (Exception e) {
            LogUtil.e(e);
            if (e instanceof RuntimeException) {
                // 运行时异常通常是逻辑的问题的问题，为了可维护性这里重新抛出
                throw new RuntimeException(e);
            }
            return new ResultHolder<>(null, e);
        }
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        if (onTaskListener instanceof OnTaskProgressListener) {
            OnTaskProgressListener<Progress, Result> listener = (OnTaskProgressListener<Progress, Result>) onTaskListener;
            listener.onUpdateProgress(values != null && values.length > 0 ? values[0] : null);
        }
    }

    @Override
    protected final void onPostExecute(ResultHolder<Result> holder) {
        super.onPostExecute(holder);
        if (indicator != null && indicator.isProgressing()) {
            indicator.hideProgress();
        }

        resultHolder = holder;

        // 检查任务可行性
        if (holder.e == null) {// 没有异常则认定为任务成功
            onSuccess(holder.result);
        } else {// 任务失败
            onFailure(holder.e);
        }

        onDone();

        //清理所有外部的引用
        clear();
    }

	/* 内部回调 */

    protected void onStart() {
        // 处理回调
        if (onTaskListener != null) {
            onTaskListener.onStart();
        }
    }

    /**
     * 异步调用的方法。
     *
     * @param params
     * @return
     * @throws Exception 该方法抛出任何异常都视为这个异步任务失败。
     *                   但是如果抛出的异常是{@link RuntimeException}或者其子类，
     *                   则将该异常重新抛出使程序崩溃。
     */
    public abstract Result doInBack(Params params) throws Exception;

    protected void onSuccess(Result result) {
        taskState = TaskState.SUCCESS;
        logTaskState();

        if (onTaskListener != null) {
            onTaskListener.onSuccess(result);
        }
    }

    protected void onFailure(Exception e) {
        taskState = TaskState.FAILURE;
        logTaskState();

        if (onTaskListener != null) {
            onTaskListener.onFailure(e);
        }
    }

    protected void onDone() {
        if (onTaskListener != null) {
            onTaskListener.onDone();
        }
    }

	/* 外部接口 */

    public interface OnTaskListener<Result> {

        /**
         * 在任务开始之前回调
         */
        void onStart();

        /**
         * 在{@link #doInBack(Object)}完成之后于异步线程之中回调。
         * 你可以在改回调之中处理一些耗时的操作。
         *
         * @param result
         */
        @WorkerThread
        void onPrepareResult(Result result);

        /**
         * 当任务成功时回调该方法
         *
         * @param result
         */
        void onSuccess(Result result);

        /**
         * 当判断任务失败时会调用该方法
         *
         * @param e 异步任务中抛出的异常，有可能为null
         */
        void onFailure(@Nullable Exception e);

        /**
         * 当任务结束时回调该方法
         */
        void onDone();
    }

    public interface OnTaskProgressListener<Progress, Result> extends OnTaskListener<Result> {

        void onUpdateProgress(Progress progress);

    }

    private OnTaskListener<Result> onTaskListener;

    public final CoreTask<Params, Progress, Result> setOnTaskListener(OnTaskListener<Result> listener) {
        this.onTaskListener = listener;
        return this;
    }

	/*任务记录*/

    private enum TaskState {

        INIT, PRE, DO, SUCCESS, FAILURE

    }

    private ResultHolder<Result> resultHolder;

    private TaskState taskState = TaskState.INIT;

    public final Result getPostedResult() {
        return resultHolder != null ? resultHolder.result : null;
    }

    public final Exception getPostedException() {
        return resultHolder != null ? resultHolder.e : null;
    }

    public final TaskState getTaskState() {
        return taskState;
    }

	/* 用户指示 */

    private ITaskIndicator indicator;

    public final CoreTask<Params, Progress, Result> setIndicator(ITaskIndicator indicator) {
        this.indicator = indicator;
        return this;
    }

	/* 开发模式 */

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

    private void logException(Exception e) {
        if (isDevModeEnable()) {
            logDevMsg("抛出异常：");
            logDevMsg(e);
        }
    }

    private void logTaskState() {
        if (isDevModeEnable()) {
            logDevMsg("Task状态：", taskState != null ? taskState.name() : null);
        }
    }

	/* 拓展 */

    private Object tag;

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

	/* 清空数据 */

    private boolean cleared;
    private boolean clearMayInterruptIfRunning = false;

    @Override
    public boolean isCleared() {
        return cleared;
    }

    public CoreTask setClearMayInterruptIfRunning(boolean clearMayInterruptIfRunning) {
        this.clearMayInterruptIfRunning = clearMayInterruptIfRunning;
        return this;
    }

    public boolean isClearMayInterruptIfRunning() {
        return clearMayInterruptIfRunning;
    }

    @Override
    public final void clear() {
        clear(clearMayInterruptIfRunning);
    }

    /**
     * 清理外部的引用，包括{@link #onTaskListener}和{@link #indicator}，
     * 然后取消任务。
     *
     * @param mayInterruptIfRunning
     */
    public final void clear(boolean mayInterruptIfRunning) {
        if (indicator != null) {
            if (indicator.isProgressing()) {
                indicator.hideProgress();
            }
            indicator = null;
        }
        onTaskListener = null;
        if (!isCancelled()) {
            cancel(mayInterruptIfRunning);
        }
        cleared = true;
    }

}