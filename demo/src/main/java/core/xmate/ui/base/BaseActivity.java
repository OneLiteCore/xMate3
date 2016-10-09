package core.xmate.ui.base;

import android.os.Bundle;

import org.xutils.x;

import core.mate.app.CoreActivity;

public class BaseActivity extends CoreActivity{

    /*继承*/

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        x.view().inject(this);
    }
}
