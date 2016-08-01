package core.base.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author DrkCore
 * @since 2016年2月10日22:07:37
 */
public abstract class CorePagerAdapter<Item> extends PagerAdapter {

	private final List<Item> data;

	public CorePagerAdapter () {
		data = new ArrayList<>();
	}

	public CorePagerAdapter (Collection<Item> items) {
		data = new ArrayList<>(items.size());
		data.addAll(items);
	}

	@SafeVarargs
	public CorePagerAdapter (Item... items) {
		data = new ArrayList<>(items.length);
		Collections.addAll(data, items);
	}

	/*继承*/

	private LayoutInflater inflater;
	private SparseArray<View> views;

	@Override
	public final int getCount () {
		return data.size();
	}

	@Override
	public final boolean isViewFromObject (View view, Object object) {
		return view == object;
	}

	@Override
	public final Object instantiateItem (ViewGroup container, int position) {
		if (inflater == null) {
			inflater = LayoutInflater.from(container.getContext());
		}
		if (views == null) {
			views = new SparseArray<>(getCount());
		}

		//检查对应视图是否已存在
		View view = views.get(position);
		if (view == null) {
			Item data = getItem(position);
			view = onCreateItemView(inflater, container, position, data);

			views.put(position, view);

			ViewPager viewPager = (ViewPager) container;
			viewPager.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		}
		return view;
	}

	@Override
	public final void destroyItem (ViewGroup container, int position, Object object) {
		View view = views != null ? views.get(position) : null;
		if (view != null) {
			container.removeView(view);
			views.remove(position);
		}
	}

	/*内部回调*/

	@NonNull
	protected abstract View onCreateItemView(LayoutInflater inflater, ViewGroup container, int position, Item data);

	/*项目处理*/

	@SafeVarargs
	public final void display (Item... items) {
		this.data.clear();
		Collections.addAll(this.data, items);
		notifyDataSetChanged();
	}

	public final void display (Collection<? extends Item> items) {
		this.data.clear();
		this.data.addAll(items);
		notifyDataSetChanged();
	}

	@SafeVarargs
	public final boolean add (Item... items) {
		if (Collections.addAll(this.data, items)) {
			notifyDataSetChanged();
			return true;
		}
		return false;
	}

	public final boolean add (Collection<? extends Item> items) {
		if (data.addAll(items)) {
			notifyDataSetChanged();
			return true;
		}
		return false;
	}

	public final boolean remove (int position) {
		if (this.data.remove(position) != null) {
			notifyDataSetChanged();
			return true;
		}
		return false;
	}

	public final boolean remove (Item item) {
		if (this.data.remove(item)) {
			notifyDataSetChanged();
			return true;
		}
		return false;
	}

	public final boolean remove (Collection<? extends Item> items) {
		if (this.data.removeAll(items)) {
			notifyDataSetChanged();
			return true;
		}
		return false;
	}

	public final void clear () {
		this.data.clear();
		notifyDataSetChanged();
	}

	/* 数据事务 */

	private AdapterTransaction<Item> transaction;

	public final AdapterTransaction<Item> beginTransaction () {
		return beginTransaction(true);
	}

	public final AdapterTransaction<Item> beginTransaction (boolean withSrc) {
		if (transaction != null) {
			// 清空上一个事务的痕迹
			transaction.clear();
			transaction = null;
		}
		transaction = new AdapterTransaction<>(this, withSrc ? data : null);
		return transaction;
	}

	/*拓展*/

	/**
	 * 浅复制一个和原始数据一样的列表。
	 *
	 * @return
	 */
	public final List<Item> getData () {
		return new ArrayList<>(data);
	}

	public final Item getItem (int position) {
		return data.get(position);
	}

	public final Iterator<Item> iterator () {
		return data.iterator();
	}

	public final int indexOf (Item item) {
		return data.indexOf(item);
	}
}
