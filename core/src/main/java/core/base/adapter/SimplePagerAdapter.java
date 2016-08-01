package core.base.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

import core.base.util.ResUtil;

/**
 * @author DrkCore
 * @since 2016年2月10日22:43:57
 */
public class SimplePagerAdapter extends CorePagerAdapter<SimplePagerAdapter.PagerData> {

	public static class PagerData {

		public final int layoutId;
		public final String title;

		public PagerData (@LayoutRes int layoutId, @StringRes int titleId) {
			this(layoutId, ResUtil.getString(titleId));
		}

		public PagerData (@LayoutRes int layoutId) {
			this(layoutId, null);
		}

		public PagerData (@LayoutRes int layoutId, @Nullable String title) {
			this.layoutId = layoutId;
			this.title = title;
		}

		public static PagerData[] asArray (@LayoutRes int... layoutIds) {
			int len = layoutIds.length;
			PagerData[] arr = new PagerData[len];
			for (int i = 0; i < len; i++) {
				arr[i] = new PagerData(layoutIds[i]);
			}
			return arr;
		}
	}



	/*构造*/

	public SimplePagerAdapter () {
	}

	public SimplePagerAdapter (Collection<PagerData> pagerData) {
		super(pagerData);
	}

	public SimplePagerAdapter (PagerData... pagerData) {
		super(pagerData);
	}

	/*继承*/

	@Override
	public final CharSequence getPageTitle (int position) {
		return getItem(position).title;
	}

	@NonNull
	@Override
	protected final View onCreateItemView(LayoutInflater inflater, ViewGroup container, int position, PagerData data) {
		return inflater.inflate(data.layoutId, container, false);
	}

}
