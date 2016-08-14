package core.mate.db.dao;

import android.support.annotation.NonNull;

import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import core.mate.db.AbsDao;

/**
 * @author 陈锡强 178456643@qq.com
 * @since 2016年1月27日15:47:07
 */
public abstract class AbsSaveOrUpdateDao<Table> extends AbsDao<Void> {

	/*继承*/

	@Override
	public final Void access (@NonNull DbManager db) throws Exception {
		if (saveOrUpdateList != null && !saveOrUpdateList.isEmpty()) {
			db.saveOrUpdate(saveOrUpdateList);
		}
		return null;
	}

	/*数据*/

	private List<Table> saveOrUpdateList;

	@SafeVarargs
	public final AbsSaveOrUpdateDao<Table> add (Table... items) {
		if (saveOrUpdateList == null) {
			saveOrUpdateList = new ArrayList<>(items.length);
		}
		Collections.addAll(saveOrUpdateList, items);
		return this;
	}

	public final AbsSaveOrUpdateDao<Table> add (Collection<Table> items) {
		if (saveOrUpdateList == null) {
			saveOrUpdateList = new ArrayList<>(items.size());
		}
		saveOrUpdateList.addAll(items);
		return this;
	}

	/*拓展*/

	public final AbsSaveOrUpdateDao<Table> clear () {
		if (saveOrUpdateList != null) {
			saveOrUpdateList.clear();
		}
		return this;
	}
}
