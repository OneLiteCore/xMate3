package core.base.common;

import android.os.AsyncTask;

public class ClearableWrapper implements Clearable {

    private final AsyncTask task;

    public ClearableWrapper(AsyncTask task) {
        this.task = task;
    }

    @Override
    public boolean isCleared() {
        return task.isCancelled() || task.getStatus() == AsyncTask.Status.FINISHED;
    }

    @Override
    public void clear() {
        if (!isCleared()) {
            task.cancel(false);
        }
    }
}
