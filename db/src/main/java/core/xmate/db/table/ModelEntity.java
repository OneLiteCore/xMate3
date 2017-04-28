package core.xmate.db.table;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

/**
 * 用于从DbModel的临时对象
 *
 * @author DrkCore
 * @since 2016年6月27日19:44:14
 */
public class ModelEntity<T> {

    private Class<T> entityType;
    private Constructor<T> constructor;

    private final LinkedHashMap<String, ColumnEntity> columnMap;

    /*package*/ ModelEntity(Class<T> entityType) throws Throwable {
        this.entityType = entityType;
        this.constructor = entityType.getConstructor();
        this.constructor.setAccessible(true);

        this.columnMap = TableUtils.findColumnMap(entityType);
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public T createEntity() throws Throwable {
        return this.constructor.newInstance();
    }

    public LinkedHashMap<String, ColumnEntity> getColumnMap() {
        return columnMap;
    }
}
