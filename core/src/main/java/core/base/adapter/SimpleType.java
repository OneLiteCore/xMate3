package core.base.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

import core.base.adapter.FlexibleAdapter.AbsItemType;
import core.base.util.LogUtil;

/**
 * @author DrkCore
 * @since 2016年1月18日20:53:04
 */
public abstract class SimpleType<Item> extends AbsItemType<Item, SimpleViewHolder<Item>> {

	private int layoutId;
	private final Class<? extends View> viewClass;

	public SimpleType(@LayoutRes int layoutId) {
		this.layoutId = layoutId;
		this.viewClass = null;
	}

	public SimpleType(Class<? extends View> viewClass) {
		this.layoutId = 0;
		this.viewClass = viewClass;
	}

	@NonNull
	@Override
	public final SimpleViewHolder<Item> createViewHolder (LayoutInflater inflater, ViewGroup parent) {
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
			return new SimpleViewHolder<>(view);
		}
		throw new IllegalStateException("无法实例化项目视图");

	}

}
