package core.base.common;

import android.view.View;

public class EnableIndicator implements ITaskIndicator {

    private final View view;

    public EnableIndicator(View view) {
        this.view = view;
    }

    @Override
    public boolean isProgressing() {
        return !view.isEnabled();
    }

    @Override
    public void showProgress() {
        view.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        view.setEnabled(true);
    }
}
