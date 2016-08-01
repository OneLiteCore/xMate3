package core.base.adapter;

import android.support.v4.widget.Space;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * 封装了间隔处理的RecyclerViewOperator
 *
 * @author DrkCore
 * @since 2016年1月18日21:01:24
 */
public final class RecyclerSpanType extends FlexibleRecyclerAdapter.AbsRecyclerItemType<Span, SimpleRecyclerViewHolder> {

	/* 继承 */

	@Override
	public SimpleRecyclerViewHolder createViewHolder (LayoutInflater inflater, ViewGroup parent) {
		return new SimpleRecyclerViewHolder(new Space(parent.getContext()));
	}

	@Override
	public void bindViewData(SimpleRecyclerViewHolder holder, int position, Span data) {
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, data.getHeightPx());
		holder.itemView.setLayoutParams(layoutParams);
	}
}
