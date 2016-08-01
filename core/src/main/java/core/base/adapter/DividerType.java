package core.base.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * 封装了分隔符处理的ItemOperator
 *
 * @author DrkCore
 * @since 2016年1月18日21:01:24
 */
public final class DividerType extends FlexibleAdapter.AbsItemType<Divider, SimpleViewHolder<Divider>> {

	/* 继承 */

	@NonNull
	@Override
	public SimpleViewHolder<Divider> createViewHolder (LayoutInflater inflater, ViewGroup parent) {
		return new SimpleViewHolder<>(new View(parent.getContext()));
	}

	@Override
	public void bindViewData(SimpleViewHolder<Divider> holder, int position, Divider data) {
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, data.getHeightPx());
		holder.getView().setLayoutParams(layoutParams);

		holder.getView().setBackgroundResource(data.getBackgroundResource());
	}
}
