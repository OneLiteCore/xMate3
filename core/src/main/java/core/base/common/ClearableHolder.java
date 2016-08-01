package core.base.common;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ClearableHolder {

    private final List<WeakReference<Clearable>> clearables = new ArrayList<>();

    public void add(Clearable clearable) {
        this.clearables.add(new WeakReference<>(clearable));
    }

    public void clearAll() {
        if (clearables.isEmpty()) {
            return;
        }
        Clearable tmp;
        for (WeakReference<Clearable> clearable : clearables) {
            tmp = clearable.get();
            if (tmp != null && !tmp.isCleared()) {
                tmp.clear();
            }
        }
        clearables.clear();
    }

}
