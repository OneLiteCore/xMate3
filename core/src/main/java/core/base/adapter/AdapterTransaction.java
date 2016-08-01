package core.base.adapter;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 用于执行适配器事务的基类。
 *
 * @param <Item>
 *
 * @author DrkCore
 * @since 2016年1月30日11:44:04
 */
public final class AdapterTransaction<Item> {

	private final CoreAdapter<Item,?> coreAdapter;
	private final CoreRecyclerAdapter<Item,?> recyclerAdapter;
	private final CorePagerAdapter<Item> pagerAdapter;
	private final List<Item> transactionData;

	AdapterTransaction (CoreAdapter<Item,?> coreAdapter, @Nullable List<Item> data) {
		this.coreAdapter = coreAdapter;
		this.recyclerAdapter = null;
		this.pagerAdapter = null;
		if (data != null) {//避免对原始数据的影响
			this.transactionData = new ArrayList<>(data);
		} else {
			this.transactionData = new ArrayList<>();
		}
	}

	AdapterTransaction (CoreRecyclerAdapter<Item,?> recyclerAdapter, @Nullable List<Item> data) {
		this.coreAdapter = null;
		this.recyclerAdapter = recyclerAdapter;
		this.pagerAdapter = null;
		if (data != null) {//避免对原始数据的影响
			this.transactionData = new ArrayList<>(data);
		} else {
			this.transactionData = new ArrayList<>();
		}
	}

	AdapterTransaction (CorePagerAdapter<Item> pagerAdapter, @Nullable List<Item> data) {
		this.coreAdapter = null;
		this.recyclerAdapter = null;
		this.pagerAdapter = pagerAdapter;
		if (data != null) {//避免对原始数据的影响
			this.transactionData = new ArrayList<>(data);
		} else {
			this.transactionData = new ArrayList<>();
		}
	}

	/* 事务处理 */

	private boolean committed;

	public boolean isCommitted () {
		return committed;
	}

	private void ensureNotCommitted () {
		if (committed) {
			throw new IllegalStateException("无法对已经提交的Transaction进行操作");
		}
	}

	public AdapterTransaction add (int location, Item item) {
		ensureNotCommitted();
		transactionData.add(location, item);
		return this;
	}

	@SafeVarargs
	public final AdapterTransaction add (Item... items) {
		ensureNotCommitted();
		Collections.addAll(transactionData, items);
		return this;
	}

	public AdapterTransaction add (Collection<? extends Item> collection) {
		ensureNotCommitted();
		transactionData.addAll(collection);
		return this;
	}

	@SafeVarargs
	public final AdapterTransaction remove (Item... items) {
		ensureNotCommitted();
		for (Item item : items) {
			transactionData.remove(item);
		}
		return this;
	}

	public AdapterTransaction remove (Collection<Item> collection) {
		ensureNotCommitted();
		transactionData.removeAll(collection);
		return this;
	}

	public AdapterTransaction remove (int location) {
		ensureNotCommitted();
		transactionData.remove(location);
		return this;
	}

	public AdapterTransaction set (int location, Item item) {
		ensureNotCommitted();
		transactionData.set(location, item);
		return this;
	}

	public void commit () {
		ensureNotCommitted();
		committed = true;
		if (coreAdapter != null) {
			coreAdapter.display(transactionData);
		} else if (recyclerAdapter != null) {
			recyclerAdapter.display(transactionData);
		} else if (pagerAdapter != null) {
			pagerAdapter.display(transactionData);
		} else {
			throw new IllegalStateException();
		}
	}

	public int size () {
		return transactionData.size();
	}

	public void clear () {
		transactionData.clear();
	}

}
