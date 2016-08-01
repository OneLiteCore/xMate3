package core.base.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.Collection;

import core.base.util.LogUtil;

/**
 * @author DrkCore
 * @since 2016年1月18日20:47:27
 */
public abstract class SimpleAdapter<Item> extends CoreAdapter<Item, SimpleViewHolder<Item>> {

	private final int layoutId;
	private final Class<? extends View> viewClass;

	public SimpleAdapter (@LayoutRes int layoutId) {
		this.layoutId = layoutId;
		this.viewClass = null;
	}

	@SafeVarargs
	public SimpleAdapter (@LayoutRes int layoutId, Item... itemArr) {
		super(itemArr);
		this.layoutId = layoutId;
		this.viewClass = null;
	}

	public SimpleAdapter (@LayoutRes int layoutId, Collection<Item> items) {
		super(items);
		this.layoutId = layoutId;
		this.viewClass = null;
	}
	
	public SimpleAdapter (Class<? extends View> viewClass) {
		this.viewClass = viewClass;
		this.layoutId = 0;
	}
	
	@SafeVarargs
	public SimpleAdapter (Class<? extends View> viewClass, Item... itemArr) {
		super(itemArr);
		this.viewClass = viewClass;
		this.layoutId = 0;
	}
	
	public SimpleAdapter (Class<? extends View> viewClass, Collection<Item> items) {
		super(items);
		this.viewClass = viewClass;
		this.layoutId = 0;
	}

	/*继承*/

	@NonNull
	@Override
	protected final SimpleViewHolder<Item> createViewHolder (LayoutInflater inflater, ViewGroup parent, int viewType) {
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
