package core.base.db.dao;

import android.support.annotation.NonNull;

import org.xutils.DbManager;

import core.base.db.CoreDb;

/**
 * @author 陈锡强 178456643@qq.com
 * @since 2016年1月19日14:30:43
 */
public final class CountDao extends CoreDb.AbsDao<Long> {

	private Class table;

	public CountDao (Class table) {
		this.table = table;
	}

	/*继承*/

	@Override
	public Long access (@NonNull DbManager db) throws Exception {
		return table != null ? db.selector(table).count() : -1;
	}

	public CountDao setTable (Class table) {
		this.table = table;
		return this;
	}
}
