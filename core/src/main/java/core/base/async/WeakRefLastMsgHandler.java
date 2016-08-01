package core.base.async;

import android.os.Message;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 只响应最后一次请求的Handler
 *
 * @author DrkCore
 * @since 2016年4月17日22:49:20
 */
public abstract class WeakRefLastMsgHandler<RefType> extends LastMsgHandler {

    private final WeakReference<RefType> ref;

    public WeakRefLastMsgHandler(RefType ref) {
        this.ref = new WeakReference<>(ref);
    }

    /*继承*/

    @Override
    protected final void handleLastMessage(Message msg) {
        RefType realRef = ref.get();
        if (realRef != null) {
            onLastMessageLively(realRef, msg);
        }
    }

    protected abstract void onLastMessageLively(@NonNull  RefType ref, Message msg);
}
