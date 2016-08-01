package core.base.util;

import android.support.annotation.StringRes;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import core.base.Core;

/**
 * @author DrkCore
 * @since 2016年5月28日16:37:55
 */
public class ToastUtil {

    private static WeakReference<Toast> lastToastRef;

    public static Toast toastShort(@StringRes int resId) {
        return toastShort(ResUtil.getString(resId));
    }

    public static Toast toastShort(CharSequence tip) {
        Toast toast = Toast.makeText(Core.getInstance().getAppContext(), tip, Toast.LENGTH_SHORT);
        toast.show();
        lastToastRef = new WeakReference<Toast>(toast);
        return toast;
    }

    public static Toast toastLong(CharSequence tip) {
        Toast toast = Toast.makeText(Core.getInstance().getAppContext(), tip, Toast.LENGTH_LONG);
        toast.show();
        lastToastRef = new WeakReference<Toast>(toast);
        return toast;
    }

    public static Toast toastLong(@StringRes int resId) {
        return toastLong(ResUtil.getString(resId));
    }

    public static void cancelLastToast() {
        if (lastToastRef != null) {
            Toast toast = lastToastRef.get();
            if (toast != null) {
                toast.cancel();
            }
            lastToastRef.clear();
            lastToastRef = null;
        }
    }

}
