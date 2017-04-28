package core.xmate.common.task;

import core.xmate.common.Callback;
import core.xmate.ex.CancelledException;

import java.util.concurrent.Executor;


/**
 * Created by wyouflf on 15/6/5.
 * 异步任务基类
 *
 * @param <ResultType>
 */
public abstract class AbsTask<Params, ResultType> implements Callback.Cancelable {

    private TaskProxy taskProxy = null;
    private final Callback.Cancelable cancelHandler;

    private volatile boolean isCancelled = false;
    private volatile State state = State.IDLE;
    private ResultType result;

    public AbsTask() {
        this(null);
    }

    public AbsTask(Callback.Cancelable cancelHandler) {
        this.cancelHandler = cancelHandler;
    }

    protected abstract ResultType doBackground(Params params) throws Throwable;

    public final Callback.Cancelable exec(Params params) {
        return TaskControllerImpl.getInstance().start(this, params);
    }

    public final ResultType execSync(Params params) throws Throwable {
        return TaskControllerImpl.getInstance().startSync(this, params);
    }

    private Callback.CommonCallback<ResultType> commonCallback;
    private Callback.ProgressCallback<ResultType> progressCallback;

    public AbsTask<Params, ResultType> setCallback(Callback.CommonCallback<ResultType> callback) {
        commonCallback = callback;
        progressCallback = callback instanceof Callback.ProgressCallback ?
                (Callback.ProgressCallback<ResultType>) callback : null;

        return this;
    }

    protected void onSuccess(ResultType result) {
        if (commonCallback != null) {
            commonCallback.onSuccess(result);
        }
    }

    protected void onError(Throwable ex, boolean isCallbackError) {
        if (commonCallback != null) {
            commonCallback.onError(ex, isCallbackError);
        }
    }

    protected void onWaiting() {
        if (progressCallback != null) {
            progressCallback.onWaiting();
        }
    }

    protected void onStarted() {
        if (progressCallback != null) {
            progressCallback.onStarted();
        }
    }

    protected void onUpdate(int flag, Object... args) {
        if (progressCallback != null) {
            progressCallback.onUpdate(flag, args);
        }
    }

    protected void onCancelled(CancelledException cex) {
        if (commonCallback != null) {
            commonCallback.onCancelled(cex);
        }
    }

    protected void onFinished() {
        if (commonCallback != null) {
            commonCallback.onFinished();
        }
    }

    public Priority getPriority() {
        return null;
    }

    public Executor getExecutor() {
        return null;
    }

    protected final void update(int flag, Object... args) {
        if (taskProxy != null) {
            taskProxy.onUpdate(flag, args);
        }
    }

    /**
     * invoked via cancel()
     */
    protected void cancelWorks() {
    }

    /**
     * 取消任务时是否不等待任务彻底结束, 立即收到取消的通知.
     *
     * @return
     */
    protected boolean isCancelFast() {
        return false;
    }

    @Override
    public final synchronized void cancel() {
        if (!this.isCancelled) {
            this.isCancelled = true;
            cancelWorks();
            if (cancelHandler != null && !cancelHandler.isCancelled()) {
                cancelHandler.cancel();
            }
            if (this.state == State.WAITING || (this.state == State.STARTED && isCancelFast())) {
                if (taskProxy != null) {
                    taskProxy.onCancelled(new CancelledException("cancelled by user"));
                    taskProxy.onFinished();
                } else if (this instanceof TaskProxy) {
                    this.onCancelled(new CancelledException("cancelled by user"));
                    this.onFinished();
                }
            }
        }
    }

    @Override
    public final boolean isCancelled() {
        return isCancelled || state == State.CANCELLED ||
                (cancelHandler != null && cancelHandler.isCancelled());
    }

    public final boolean isFinished() {
        return this.state.value() > State.STARTED.value();
    }

    public final State getState() {
        return state;
    }

    public final ResultType getResult() {
        return result;
    }

    /*package*/
    void setState(State state) {
        this.state = state;
    }

    /*package*/
    final void setTaskProxy(TaskProxy taskProxy) {
        this.taskProxy = taskProxy;
    }

    /*package*/
    final void setResult(ResultType result) {
        this.result = result;
    }

    public enum State {
        IDLE(0), WAITING(1), STARTED(2), SUCCESS(3), CANCELLED(4), ERROR(5);
        private final int value;

        private State(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }
}
