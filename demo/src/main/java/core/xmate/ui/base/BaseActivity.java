package core.xmate.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.xutils.x;

import core.mate.app.CoreActivity;

public class BaseActivity extends CoreActivity{

    /*继承*/
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
}
