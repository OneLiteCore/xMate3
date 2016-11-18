package core.mate.http;

import org.xutils.common.Callback;

import core.mate.async.Clearable;

/**
 * @author DrkCore
 * @since 2016-10-09
 */
public class ClearableWrapper implements Clearable {

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
