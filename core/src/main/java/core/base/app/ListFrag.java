package core.base.app;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ListFrag extends CoreFrag implements OnItemClickListener,
        OnItemLongClickListener {

    private int listViewId;
    private int layoutRes;

    private ListView listView;
    private BaseAdapter adapter;

    public ListView getListView() {
        return listView;
    }

    protected <Adapter extends BaseAdapter> Adapter getAdapter() {
        return (Adapter) adapter;
    }

    protected final void configListViewId(@IdRes int listViewId) {
        if (listView != null) {
            throw new IllegalStateException("ListView已经创建，此时无法设置listViewId");
        }
        this.listViewId = listViewId;
    }

    protected final void configLayoutRes(@LayoutRes int layoutRes) {
        if (listView != null) {
            throw new IllegalStateException("ListView已经创建，此时无法设置layoutRes");
        }
        this.layoutRes = layoutRes;
    }

    protected final void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        listView.setAdapter(adapter);
    }

	/* 继承 */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView;
        if (layoutRes <= 0) {// 并未指定布局，直接创建ListView
            contentView = new ListView(getActivity());
        } else {// 指定了布局
            contentView = inflater.inflate(layoutRes, container, false);
        }

        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view instanceof ListView) {// 布局本身就是ListView或者其子类
            listView = (ListView) view;
        } else if (listViewId > 0) {// 指定了id
            listView = (ListView) view.findViewById(listViewId);
        }

        if (listView == null) {// 不存在ListView
            throw new IllegalStateException("可用的ListView不存在");
        }

        onPrepareListView(listView);
    }
	
	/* 内部回调 */

    protected void onPrepareListView(ListView listView) {
        // 设置监听
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
	
	/* 传递ListView的多选操作 */

    public final boolean isItemChecked(int position) {
        return listView.isItemChecked(position);
    }

    public final void setItemChecked(int position, boolean value) {
        listView.setItemChecked(position, value);
    }

    public final <T> T getItemAtPosition(int position) {
        return (T) listView.getItemAtPosition(position);
    }

    public final long getItemIdAtPosition(int position) {
        return listView.getItemIdAtPosition(position);
    }

    public final int getAdapterCount() {
        return getAdapter().getCount();
    }

    public final int getCheckedItemCount() {
        return listView.getCheckedItemIds().length;
    }

    public final long[] getCheckedItemIds() {
        return listView.getCheckedItemIds();
    }

    public final int getCheckedItemPosition() {
        return listView.getCheckedItemPosition();
    }

    public final SparseBooleanArray getCheckedItemPositions() {
        return listView.getCheckedItemPositions();
    }

    public final void clearChoices() {
        listView.clearChoices();
    }

}
