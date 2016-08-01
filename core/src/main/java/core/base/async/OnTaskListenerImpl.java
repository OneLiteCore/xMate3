package core.base.async;

public abstract class OnTaskListenerImpl<Result> implements CoreTask.OnTaskListener<Result> {

	@Override
	public void onStart () {
	}

	@Override
	public void onPrepareResult (Result result) {

	}

	@Override
	public void onFailure (Exception e) {
	}

	@Override
	public void onDone () {
	}

}
