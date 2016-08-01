package core.base.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import core.base.app.CoreFrag;
import core.base.util.LogUtil;

/**
 * @author DrkCore
 * @since 2016年4月20日21:32:37
 */
public final class FragPagerAdapter extends FragmentPagerAdapter {

    private final Class[] fragClzs;
    private final Fragment[] frags;

    public FragPagerAdapter(FragmentManager fm, Class[] fragClzs) {
        super(fm);
        this.fragClzs = fragClzs;
        this.frags = new Fragment[fragClzs.length];
    }

    /*继承*/

    @Override
    public Fragment getItem(int position) {
        Fragment frag = frags[position];
        if (frag == null) {
            try {
                frag = (Fragment) fragClzs[position].newInstance();
                frags[position] = frag;
            } catch (Exception e) {
                LogUtil.e(e);
                throw new IllegalStateException();
            }
        }
        return frag;
    }

    @Override
    public int getCount() {
        return fragClzs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (CoreFrag.class.isAssignableFrom(fragClzs[position])) {
            CoreFrag coreFrag = (CoreFrag) getItem(position);
            return coreFrag.getFragTitle();
        }
        return super.getPageTitle(position);
    }
}
