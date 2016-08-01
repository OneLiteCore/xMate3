package core.base.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import core.base.util.LogUtil;

/**
 * 封装了Fragment操作的辅助类
 *
 * @author DrkCore
 * @since 2015年11月9日14:41:58
 */
public class FragHelper {

    private final FragmentManager fragMgr;

    public FragHelper(FragmentManager fragMgr) {
        this.fragMgr = fragMgr;
    }

	/* 获取或添加 */

    public Fragment findOrAddFragment(int containerId, Class fragClazz) {
        return findOrAddFragment(containerId, fragClazz, fragClazz.getCanonicalName());
    }

    public Fragment findOrAddFragment(int containerId, Class fragClazz, String fragTag) {
        Fragment frag = fragMgr.findFragmentByTag(fragTag);
        if (frag == null) {
            try {
                frag = (Fragment) fragClazz.newInstance();
            } catch (Exception e) {
                LogUtil.e(e);
                throw new IllegalArgumentException("指定的fragClazz必须存在可见的默认构造函数");
            }
            fragMgr.beginTransaction().add(containerId, frag, fragTag).commit();
        }
        return frag;
    }

	/* 切换 */

    public Fragment switchFragment(int containerId, Fragment curFrag, Class nextFragClass) {
        return switchFragment(containerId, curFrag, null, nextFragClass, nextFragClass.getCanonicalName());
    }

    public Fragment switchFragment(int containerId, Fragment curFrag, Class nextFragClass, String fragTag) {
        return switchFragment(containerId, curFrag, null, nextFragClass, fragTag);
    }

    public Fragment switchFragment(int containerId, Fragment curFrag, Fragment nextFrag) {
        return switchFragment(containerId, curFrag, nextFrag, null, nextFrag.getClass().getCanonicalName());
    }

    public Fragment switchFragment(int containerId, Fragment curFrag, Fragment nextFrag, String fragTag) {
        return switchFragment(containerId, curFrag, nextFrag, null, fragTag);
    }

    private Fragment switchFragment(int containerId, Fragment curFrag, Fragment nextFrag, Class nextFragClass, String fragTag) {
        if (TextUtils.isEmpty(fragTag)) {
            throw new IllegalArgumentException("fragTag不允许为null或者空字符串");
        } else if (nextFrag == null && nextFragClass == null) {
            throw new IllegalArgumentException("nextFrag和nextFragClass不允许同时为null");
        }

        FragmentTransaction fragTran = fragMgr.beginTransaction();

        // 隐藏当前的frag
        if (curFrag != null) {
            if (curFrag == nextFrag || curFrag.getClass() == nextFragClass) {
                fragTran.show(curFrag).commit();
                return curFrag;
            } else {
                fragTran.hide(curFrag);
            }
        }

        // 查找或者创建nextFrag的实例
        if (nextFrag == null) {
            nextFrag = fragMgr.findFragmentByTag(fragTag);
        }
        if (nextFrag == null) {
            try {
                nextFrag = (Fragment) nextFragClass.newInstance();
            } catch (Exception e) {
                LogUtil.e(e);
                throw new IllegalArgumentException("指定的nextFragClass必须存在可见的默认构造函数");
            }
        }

        // 切换到nextFrag
        if (nextFrag.isAdded()) {
            fragTran.show(nextFrag);
        } else {
            fragTran.add(containerId, nextFrag, fragTag);
        }
        fragTran.commit();

        return nextFrag;
    }

	/* 显示或者隐藏 */

    @SuppressWarnings("unchecked")
    public boolean hideFragment(Class... fragClasses) {
        int len = fragClasses.length;
        List<Fragment> frags = new ArrayList<>(len);
        Fragment frag;
        for (Class fragClass : fragClasses) {
            frag = fragMgr.findFragmentByTag(fragClass.getCanonicalName());
            if (frag != null) {
                frags.add(frag);
            }
        }

        return hideFragment(frags.toArray(new Fragment[frags.size()]));
    }

    public boolean hideFragment(String... fragTags) {
        int len = fragTags.length;
        List<Fragment> frags = new ArrayList<>(len);
        Fragment frag;
        for (String fragTag : fragTags) {
            frag = fragMgr.findFragmentByTag(fragTag);
            if (frag != null) {
                frags.add(frag);
            }
        }

        return hideFragment(frags.toArray(new Fragment[frags.size()]));
    }

    private boolean hideFragment(Fragment... frags) {
        if (frags.length > 0) {
            FragmentTransaction fragTran = fragMgr.beginTransaction();
            for (Fragment frag : frags) {

                fragTran.hide(frag);
            }
            fragTran.commit();
            return true;
        }
        return false;
    }

}
