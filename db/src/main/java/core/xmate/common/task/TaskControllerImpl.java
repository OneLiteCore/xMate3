package core.xmate.common.task;

import android.os.Looper;

import core.xmate.common.TaskController;
import core.xmate.ex.CancelledException;

/**
 * Created by wyouflf on 15/6/5.
 * 异步任务的管理类
 */
public final class TaskControllerImpl implements TaskController {

    private TaskControllerImpl() {
    }

    private static volatile TaskController instance;

    public static TaskController getInstance() {
        if (instance == null) {
            synchronized (TaskController.class) {
                if (instance == null) {
                    instance = new TaskControllerImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public <E, T> AbsTask<E, T> start(AbsTask<E, T> task) {
        return start(task, null);
    }

    @Override
    public <E, T> AbsTask<E, T> start(AbsTask<E, T> task, E e) {
        TaskProxy<E, T> proxy = null;
        if (task instanceof TaskProxy) {
            proxy = (TaskProxy<E, T>) task;
        } else {
            proxy = new TaskProxy<E, T>(task);
        }
        try {
            proxy.doBackground(e);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return proxy;
    }

    @Override
    public <E, T> T startSync(AbsTask<E, T> task) throws Throwable {
        return startSync(task, null);
    }

    @Override
    public <E, T> T startSync(AbsTask<E, T> task, E e) throws Throwable {
        T result = null;
        try {
            task.onWaiting();
            task.onStarted();
            result = task.doBackground(e);
            task.onSuccess(result);
        } catch (CancelledException cex) {
            task.onCancelled(cex);
        } catch (Throwable ex) {
            task.onError(ex, false);
            throw ex;
        } finally {
            task.onFinished();
        }
        return result;
    }

    @Override
    public void autoPost(Runnable runnable) {
        if (runnable == null) return;
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            TaskProxy.sHandler.post(runnable);
        }
    }

    /**
     * run in UI thread
     *
     * @param runnable
     */
    @Override
    public void post(Runnable runnable) {
        if (runnable == null) return;
        TaskProxy.sHandler.post(runnable);
    }

    /**
     * run in UI thread
     *
     * @param runnable
     * @param delayMillis
     */
    @Override
    public void postDelayed(Runnable runnable, long delayMillis) {
        if (runnable == null) return;
        TaskProxy.sHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * run in background thread
     *
     * @param runnable
     */
    @Override
    public void run(Runnable runnable) {
        if (!TaskProxy.sDefaultExecutor.isBusy()) {
            TaskProxy.sDefaultExecutor.execute(runnable);
        } else {
            new Thread(runnable).start();
        }
    }

    /**
     * 移除post或postDelayed提交的, 未执行的runnable
     *
     * @param runnable
     */
    @Override
    public void removeCallbacks(Runnable runnable) {
        TaskProxy.sHandler.removeCallbacks(runnable);
    }
}
