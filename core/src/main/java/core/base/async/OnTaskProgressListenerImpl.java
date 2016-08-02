package core.base.async;

import android.support.annotation.Nullable;

public abstract class OnTaskProgressListenerImpl<Progress, Result> implements CoreTask.OnTaskProgressListener<Progress, Result> {

    @Override
    public void onUpdateProgress(Progress progress) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPrepareResult(Result result) {

    }

    @Override
    public void onFailure(@Nullable Exception e) {

    }

    @Override
    public void onDone() {

    }
}
