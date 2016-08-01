package core.base.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 初步封装的Adapter基类。
 *
 * @param <Item>
 * @author DrkCore
 * @since 2015年10月31日23:37:53
 */
public abstract class CoreAdapter<Item, Holder extends CoreAdapter.AbsViewHolder<Item>> extends BaseAdapter {

    private final List<Item> data = new ArrayList<>();

    public CoreAdapter() {
    }

    @SuppressWarnings("unchecked")
    public CoreAdapter(Item... itemArr) {
        Collections.addAll(this.data, itemArr);
    }

    public CoreAdapter(Collection<Item> items) {
        this.data.addAll(items);
    }

	/* 继承 */

    private LayoutInflater inflater;

    @Override
    public final int getCount() {
        return data.size();
    }

    @Override
    public final Item getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        int viewType = getItemViewType(position);
        Item data = getItem(position);

        Holder holder;
        if (convertView == null) {
            holder = createViewHolder(inflater, parent, viewType);
            convertView = holder.getView();
            convertView.setTag(holder);// 绑定ViewHolder
        } else {
            holder = (Holder) convertView.getTag();
        }
        // 绑定视图数据
        holder.setPosition(position);
        holder.setItem(data);
        bindViewData(holder, position, data, viewType);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        super.hasStableIds();
        // 钩子方法，使之返回true。否者多选操作会bug
        return true;
    }

	/* 内部回调 */

    public static abstract class AbsViewHolder<Item> {

        private Item item;
        private int position;
        private final View view;

        /**
         * 获取Holder携带的View。
         *
         * @return
         */
        public final View getView() {
            return view;
        }

        /**
         * 获取Holder携带的View，并转化成指定类型。
         *
         * @param <T>
         * @return
         */
        public final <T extends View> T getCastView() {
            return (T) getView();
        }

        public final Item getItem() {
            return item;
        }

        public final int getPosition() {
            return position;
        }

        public final void setItem(Item item) {
            this.item = item;
        }

        public final void setPosition(int position) {
            this.position = position;
        }

        public AbsViewHolder(View v) {
            this.view = v;
        }

    }

    @NonNull
    protected abstract Holder createViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract void bindViewData(Holder holder, int position, Item data, int viewType);

	/* 数据展示 */

    @SafeVarargs
    public final void display(Item... items) {
        this.data.clear();
        Collections.addAll(this.data, items);
        notifyDataSetChanged();
    }

    public final void display(Collection<? extends Item> items) {
        this.data.clear();
        this.data.addAll(items);
        notifyDataSetChanged();
    }

    @SafeVarargs
    public final boolean add(Item... items) {
        if (Collections.addAll(this.data, items)) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public final boolean add(Collection<? extends Item> items) {
        if (data.addAll(items)) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public final boolean remove(int position) {
        if (this.data.remove(position) != null) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public final boolean remove(Item item) {
        if (this.data.remove(item)) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public final boolean remove(Collection<? extends Item> items) {
        if (this.data.removeAll(items)) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public final void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }

	/* 数据事务 */

    private AdapterTransaction<Item> transaction;

    public final AdapterTransaction<Item> beginTransaction() {
        return beginTransaction(true);
    }

    public final AdapterTransaction<Item> beginTransaction(boolean withSrc) {
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
    public final List<Item> getData() {
        return new ArrayList<>(data);
    }

    public final Iterator<Item> iterator() {
        return data.iterator();
    }

    public final int indexOf(Item item) {
        return data.indexOf(item);
    }

    public final boolean contains(Item item) {
        return data.contains(item);
    }

}
