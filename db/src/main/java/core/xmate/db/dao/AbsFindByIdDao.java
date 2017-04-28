package core.xmate.db.dao;

import core.xmate.db.DbManager;
import core.xmate.common.util.ParameterizedTypeUtil;
import core.xmate.ex.DbException;

import java.lang.reflect.Type;

import core.xmate.db.AbsDao;

/**
 * 用于根据id查询数据库的访问基类
 *
 * @author DrkCore 178456643@qq.com
 * @since 2016年1月3日21:21:03
 */
public abstract class AbsFindByIdDao<Table> extends AbsDao<Table> {

	/* 继承 */

	@Override
	public final Table access ( DbManager db) throws DbException {
		Type type = ParameterizedTypeUtil.getGenericParametersType(getClass())[0];
		if (type instanceof Class) {
			@SuppressWarnings("unchecked")
			Class<Table> clazz = (Class<Table>) type;
			return db.findById(clazz, idValue);
		}
		throw new IllegalStateException("指定泛型" + type.toString() + "不可用");
	}

	@Override
	public void clear() {
		super.clear();
		idValue = null;
	}

	/* 拓展 */

	private Object idValue;

	public final AbsFindByIdDao<Table> byId (Object idValue) {
		this.idValue = idValue;
		return this;
	}

}
