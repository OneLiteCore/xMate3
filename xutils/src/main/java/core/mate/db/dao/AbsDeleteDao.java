package core.mate.db.dao;

import android.support.annotation.NonNull;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import core.mate.db.AbsDao;
import core.mate.util.ClassUtil;

/**
 * 执行数据库删除的Dao对象。
 * 注意，如果你不配置任何删除方法的话该Dao将什么都不会做。
 *
 * @author DrkCore
 * @since 2016年2月24日23:06:48
 */
public abstract class AbsDeleteDao<Table> extends AbsDao<Void> {

	/* 继承 */

    @Override
    public final Void access(@NonNull DbManager db) throws Exception {
        Type type = ClassUtil.getGenericParametersType(getClass())[0];
        if (type instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<Table> clazz = (Class<Table>) type;

            WhereBuilder createdWhere = onCreateWhereBuilder(clazz);
            if (createdWhere != null) {
                db.delete(clazz, createdWhere);
            }
            if (whereBuilder != null) {
                db.delete(clazz, whereBuilder);
            }
            if (idValue != null) {
                db.deleteById(clazz, idValue);
            }
            if (delList != null && !delList.isEmpty()) {
                db.delete(delList);
            }
            return null;// 返回null表示成功
        }
        throw new IllegalStateException("指定泛型" + type.toString() + "不可用");
    }

    @Override
    public void clear() {
        super.clear();
        whereBuilder = null;
        idValue = null;
        if (delList != null) {
            delList.clear();
        }
    }

	/* 内部回调 */

    /**
     * 配置用于删除数据的whereBuilder。
     * 如果该方法返回null，则默认使用{@link #where(WhereBuilder)}、{@link #byId(Object)}
     * 和{@link #add(Collection)}等方法配置的执行删除操作。
     */
    protected WhereBuilder onCreateWhereBuilder(Class<Table> clazz) {
        return null;
    }

	/* where */

    private WhereBuilder whereBuilder;

    public final AbsDeleteDao<Table> where(WhereBuilder where) {
        this.whereBuilder = where;
        return this;
    }

	/* byId */

    private Object idValue;

    public final AbsDeleteDao<Table> byId(Object idValue) {
        this.idValue = idValue;
        return this;
    }

	/* byObjects */

    private List<Table> delList;

    public final AbsDeleteDao<Table> add(Collection<Table> items) {
        if (delList == null) {
            delList = new ArrayList<>(items.size());
        }
        delList.addAll(items);
        return this;
    }

    @SafeVarargs
    public final AbsDeleteDao<Table> add(Table... items) {
        if (delList == null) {
            delList = new ArrayList<>(items.length);
        }
        Collections.addAll(delList, items);
        return this;
    }


}
