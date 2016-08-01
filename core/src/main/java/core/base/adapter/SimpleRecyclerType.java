package core.base.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

import core.base.util.LogUtil;

/**
 * @author DrkCore
 * @since 2016年2月1日00:06:31
 */
public abstract class SimpleRecyclerType<Item> extends FlexibleRecyclerAdapter.AbsRecyclerItemType<Item, SimpleRecyclerViewHolder> {

	private int layoutId;
	private final Class<? extends View> viewClass;

	public SimpleRecyclerType(@LayoutRes int layoutId) {
		this.layoutId = layoutId;
		this.viewClass = null;
	}

	public SimpleRecyclerType(Class<? extends View> viewClass) {
		this.layoutId = 0;
		this.viewClass = viewClass;
	}

	/*继承*/

	@Override
	public final SimpleRecyclerViewHolder createViewHolder (LayoutInflater inflater, ViewGroup parent) {
		View view = null;
		if (layoutId > 0) {
			view = inflater.inflate(layoutId, parent, false);
		} else if (viewClass != null) {
			try {
				Constructor constructor = viewClass.getConstructor(Context.class);
				view = (View) constructor.newInstance(inflater.getContext());
			} catch (Exception e) {
				LogUtil.e(e);
			}
		}
		if (view != null) {
			return new SimpleRecyclerViewHolder(view);
		}
		throw new IllegalStateException("无法实例化项目视图");

	}

}
