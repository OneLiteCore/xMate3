package core.mate.db.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;

import core.mate.db.AbsDao;

/**
 * 用于查找单个数据库模型的dao
 *
 * @author DrkCore
 * @since 2016年2月18日10:03:29
 */
public final class FindFirstDbModelDao extends AbsDao<DbModel> {

	public FindFirstDbModelDao () {
	}

	public FindFirstDbModelDao (SqlInfo sqlInfo) {
		this.sqlInfo = sqlInfo;
	}

	public FindFirstDbModelDao (String sql) {
		this.sqlInfo = new SqlInfo(sql);
	}

	/*继承*/

	@Override
	public final DbModel access (@NonNull DbManager db) throws Exception {
		return sqlInfo != null ? db.findDbModelFirst(sqlInfo) : null;
	}

	/*配置*/

	private SqlInfo sqlInfo;

	public FindFirstDbModelDao setSql (SqlInfo sqlInfo) {
		this.sqlInfo = sqlInfo;
		return this;
	}

	public FindFirstDbModelDao setSql (String sql, @Nullable Object... args) {
		if (sqlInfo == null) {
			sqlInfo = new SqlInfo();
		}
		sqlInfo.clear();
		sqlInfo.setSql(sql);
		if (args != null && args.length > 0) {
			sqlInfo.addBindArgs(args);
		}
		return this;
	}

}
