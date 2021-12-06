package core.xmate.db.table;

import android.database.Cursor;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.xmate.db.DbException;
import core.xmate.db.DbManager;
import core.xmate.util.IOUtil;
import core.xmate.util.LogUtil;

/**
 * DbManager基类, 包含表结构的基本操作.
 * Created by wyouflf on 16/1/22.
 */
public abstract class DbBase implements DbManager {

    private final HashMap<Class<?>, TableEntity<?>> tableMap = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> TableEntity<T> getTable(Class<T> entityType) throws DbException {
        synchronized (tableMap) {
            TableEntity<T> table = (TableEntity<T>) tableMap.get(entityType);
            if (table == null) {
                try {
                    table = new TableEntity<T>(this, entityType);
                } catch (DbException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    throw new DbException(ex);
                }
                tableMap.put(entityType, table);
            }

            return table;
        }
    }

    @Override
    public void dropTable(Class<?> entityType) throws DbException {
        TableEntity<?> table = this.getTable(entityType);
        if (!table.tableIsExists()) return;
        execNonQuery("DROP TABLE \"" + table.getName() + "\"");
        table.setTableCheckedStatus(false);
        this.removeTable(entityType);
    }

    @Override
    @Nullable
    public DbException dropTableQuietly(Class<?> entityType) {
        DbException exception = null;
        try {
            dropTable(entityType);
        } catch (DbException e) {
            exception = e;
            LogUtil.e("dropTableQuietly", e);
        }
        return exception;
    }

    @Override
    public void dropDb() throws DbException {
        Cursor cursor = execQuery("SELECT name FROM sqlite_master WHERE type='table' AND name<>'sqlite_sequence'");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    try {
                        String tableName = cursor.getString(0);
                        execNonQuery("DROP TABLE " + tableName);
                    } catch (Throwable e) {
                        LogUtil.e(e.getMessage(), e);
                    }
                }

                synchronized (tableMap) {
                    for (TableEntity<?> table : tableMap.values()) {
                        table.setTableCheckedStatus(false);
                    }
                    tableMap.clear();
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
    }

    @Override
    public DbException dropDbQuietly() {
        DbException exception = null;
        try {
            dropDb();
        } catch (DbException e) {
            exception = e;
            LogUtil.e(e.getMessage(), e);
        }
        return exception;
    }

    @Override
    public List<String> getTables() throws DbException {
        List<String> tables = new ArrayList<>();
        Cursor cursor = execQuery("SELECT name FROM sqlite_master WHERE type='table' AND name<>'sqlite_sequence'");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    try {
                        tables.add(cursor.getString(0));
                    } catch (Throwable e) {
                        LogUtil.e(e.getMessage(), e);
                    }
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }

        return tables;
    }

    @Override
    public void addColumn(Class<?> entityType, String column) throws DbException {
        TableEntity<?> table = this.getTable(entityType);
        ColumnEntity col = table.getColumnMap().get(column);
        if (col != null) {
            if (!table.tableIsExists()) return; // 不需要添加, 表创建时会自动添加
            StringBuilder builder = new StringBuilder();
            builder.append("ALTER TABLE ").append("\"").append(table.getName()).append("\"").
                    append(" ADD COLUMN ").append("\"").append(col.getName()).append("\"").
                    append(" ").append(col.getColumnDbType()).
                    append(" ").append(col.getProperty());
            execNonQuery(builder.toString());
        } else {
            throw new DbException("the column(" + column + ") is not defined in table: " + table.getName());
        }
    }

    protected void removeTable(Class<?> entityType) {
        synchronized (tableMap) {
            tableMap.remove(entityType);
        }
    }
}
