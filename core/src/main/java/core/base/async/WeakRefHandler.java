package core.base.async;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * @param <RefType>
 * @author DrkCore
 * @since 2015年10月7日22:41:44
 */
public abstract class WeakRefHandler<RefType> extends Handler {

    private WeakReference<RefType> ref;

    public WeakRefHandler(RefType ref) {
        super();
        this.ref = new WeakReference<>(ref);
    }

	/* 继承 */

    @Override
    public final void handleMessage(Message msg) {
        super.handleMessage(msg);
        RefType realRef = ref.get();
        if (realRef != null) {
            onMessageLively(realRef, msg);
        }
    }

	/* 回调 */

    protected abstract void onMessageLively(@NonNull  RefType ref, Message msg);
}
