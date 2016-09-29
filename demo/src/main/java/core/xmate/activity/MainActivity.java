package core.xmate.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import core.mate.app.CoreFrag;
import core.mate.util.ClassUtil;
import core.xmate.R;
import core.xmate.activity.base.BaseActivity;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.tabLayout_main_tabs)
    private TabLayout tabLayout;
    private Fragment curFrag;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        //反射获取指定包下的子类
        List<Class> frags = null;
        try {
            frags = ClassUtil.getSubClassUnderPackage(CoreFrag.class, "core.xmate.activity.main");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //填充TabLayout
        for (Class clz : frags) {
            tabLayout.addTab(tabLayout.newTab().setText(clz.getSimpleName()).setTag(clz));
        }
        TabLayout.OnTabSelectedListener listener;
        tabLayout.addOnTabSelectedListener(listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //使用FragHelper快速切换Fragment
                //CoreFrag已经处理了重建Activity后Frag重叠的问题
                Class clz = (Class) tab.getTag();
                curFrag = getFragHelper().switchFragment(R.id.frameLayout_main_fragContaienr, curFrag, clz);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //选中第一个tab
        tabLayout.getTabAt(0).select();
        listener.onTabSelected(tabLayout.getTabAt(0));
    }
}
