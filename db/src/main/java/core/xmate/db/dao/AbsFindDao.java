package core.xmate.db.dao;

import core.xmate.db.DbManager;
import core.xmate.common.util.ParameterizedTypeUtil;
import core.xmate.db.Selector;
import core.xmate.db.sqlite.WhereBuilder;
import core.xmate.ex.DbException;

import java.lang.reflect.Type;
import java.util.List;

import core.xmate.db.AbsDao;

/**
 * @author DrkCore
 * @since 2016年1月3日21:24:39
 */
public abstract class AbsFindDao<Table> extends AbsDao<List<Table>> {

	/* 继承 */

    @Override
    public final List<Table> access(DbManager db) throws DbException {
        Type type = ParameterizedTypeUtil.getGenericParametersType(getClass())[0];
        if (type instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<Table> clazz = (Class<Table>) type;
            Selector selector = db.selector(clazz);
            if (!onPrepareSelector(selector)) {//没有默认配置项，使用各种方法的配置
                if (whereBuilder != null) {
                    selector.where(whereBuilder);
                }
                if (offset >= 0) {
                    selector.offset(offset);
                }
                if (limit >= 0) {
                    selector.limit(limit);
                }
                if (orderBy != null && !orderBy.equals("")) {
                    selector.orderBy(orderBy, desc);
                }
            }
            return selector.findAll();
        }
        throw new IllegalStateException("指定泛型" + type.toString() + "不可用");
    }

    @Override
    public void clear() {
        super.clear();
        offset = -1;
        limit = -1;
        orderBy = null;
        desc = false;
        whereBuilder = null;
    }

	/* 内部回调 */

    protected boolean onPrepareSelector(Selector selector) {
        return false;
    }

	/*配置*/

    private int offset = -1;
    private int limit = -1;
    private String orderBy;
    private boolean desc;

    private WhereBuilder whereBuilder;

    private WhereBuilder getWhereBuilder() {
        if (whereBuilder == null) {
            whereBuilder = WhereBuilder.b();
        }
        return whereBuilder;
    }

    public final AbsFindDao<Table> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public final AbsFindDao<Table> orderBy(String columnName) {
        orderBy = columnName;
        return this;
    }

    public final AbsFindDao<Table> orderBy(String columnName, boolean desc) {
        orderBy = columnName;
        this.desc = desc;
        return this;
    }

    public final AbsFindDao<Table> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public final AbsFindDao<Table> and(String columnName, String op, Object value) {
        getWhereBuilder().and(columnName, op, value);
        return this;
    }

    public final AbsFindDao<Table> and(WhereBuilder where) {
        getWhereBuilder().and(where);
        return this;
    }

    public final AbsFindDao<Table> or(String columnName, String op, Object value) {
        getWhereBuilder().or(columnName, op, value);
        return this;
    }

    public final AbsFindDao<Table> or(WhereBuilder where) {
        getWhereBuilder().or(where);
        return this;
    }

    public final AbsFindDao<Table> expr(String expr) {
        getWhereBuilder().expr(expr);
        return this;
    }

}
