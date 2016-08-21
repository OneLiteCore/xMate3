package core.mate.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xutils.common.Callback;

import core.mate.http.CoreAction;
import core.mate.http.IllegalDataException;

public class ActionNodeWrapper<AsyncMgr extends AsyncManager, Result> extends NodeImpl<AsyncMgr> implements CoreAction.OnActionListener<Result> {

    @Override
    protected void doStart(AsyncMgr asyncMgr) {

    }

    @Override
    public void onWaiting() {

    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {

    }

    @Override
    public boolean onCache(Result result) {
        return false;
    }

    @Override
    public void onResultPrepared(@NonNull Result result) {

    }

    @Override
    public void onSuccess(Result result) {
        endWith(result, null);
    }

    @Override
    public void onError(@Nullable Throwable ex, @Nullable IllegalDataException e, boolean isOnCallback) {
        endWith(null, e);
    }

    @Override
    public void onCancelled(Callback.CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
