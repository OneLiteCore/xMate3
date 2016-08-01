package core.base.http;

import android.support.annotation.NonNull;

import org.xutils.common.Callback;

import core.base.exception.IllegalDataFormatException;

/**
 * @author DrkCore
 * @since 2016年1月9日11:09:49
 */
public abstract class OnActionListenerImpl<Result> implements CoreAction.OnActionListener<Result> {

	@Override
	public void onWaiting () {

	}

	@Override
	public void onStarted () {

	}

	@Override
	public void onLoading (long total, long current, boolean isDownloading) {

	}

	@Override
	public void onResultPrepared (@NonNull Result result) {

	}

	@Override
	public void onError (Throwable ex, IllegalDataFormatException e, boolean isOnCallback) {

	}

	@Override
	public void onCancelled (Callback.CancelledException cex) {

	}

	@Override
	public void onFinished () {

	}
}
