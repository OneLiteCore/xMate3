package core.base.adapter;

import android.support.annotation.NonNull;
import android.support.v4.widget.Space;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * 封装了间隔处理的ViewOperator
 *
 * @author DrkCore
 * @since 2016年1月18日21:01:24
 */
public final class SpanType extends FlexibleAdapter.AbsItemType<Span, SimpleViewHolder<Span>> {

	/* 继承 */

	@NonNull
	@Override
	public SimpleViewHolder<Span> createViewHolder (LayoutInflater inflater, ViewGroup parent) {
		return new SimpleViewHolder<>(new Space(parent.getContext()));
	}

	@Override
	public void bindViewData(SimpleViewHolder<Span> viewHolder, int position, Span data) {
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, data.getHeightPx());
		viewHolder.getView().setLayoutParams(layoutParams);
	}
}
