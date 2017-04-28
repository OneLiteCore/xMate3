package core.xmate.common;

import core.xmate.ex.CancelledException;

/**
 * Created by wyouflf on 15/6/5.
 * 通用回调接口
 */
public interface Callback {

    interface CommonCallback<ResultType> extends Callback {
        void onSuccess(ResultType result);

        void onError(Throwable ex, boolean isOnCallback);

        void onCancelled(CancelledException cex);

        void onFinished();
    }

    interface ProgressCallback<ResultType> extends CommonCallback<ResultType> {
        void onWaiting();

        void onStarted();

        void onUpdate(int flag, Object... args);
    }

    interface Cancelable {
        void cancel();

        boolean isCancelled();
    }

}
