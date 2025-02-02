package core.xmate.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import core.xmate.db.sqlite.CursorIterator;
import core.xmate.db.sqlite.DbModelCursorIterator;
import core.xmate.db.sqlite.SqlInfo;
import core.xmate.db.sqlite.WhereBuilder;
import core.xmate.db.table.DbModel;
import core.xmate.db.table.TableEntity;
import core.xmate.util.KeyValue;

/**
 * 数据库访问接口
 */
public interface DbManager extends Closeable {

    DaoConfig getDaoConfig();

    SQLiteDatabase getDatabase();

    /**
     * 保存实体类或实体类的List到数据库,
     * 如果该类型的id是自动生成的, 则保存完后会给id赋值.
     */
    boolean saveBindingId(Object entity) throws DbException;

    /**
     * 保存或更新实体类或实体类的List到数据库, 根据id对应的数据是否存在.
     */
    void saveOrUpdate(Object entity) throws DbException;

    /**
     * 保存或更新实体类或实体类的List到数据库, 根据id对应的数据是否存在.
     */
    void saveOrUpdate(Object entity, boolean withTransaction) throws DbException;

    /**
     * 保存实体类或实体类的List到数据库
     */
    void save(Object entity) throws DbException;

    /**
     * 保存实体类或实体类的List到数据库
     */
    void save(Object entity, boolean withTransaction) throws DbException;

    /**
     * 保存或更新实体类或实体类的List到数据库, 根据id和其他唯一索引判断数据是否存在.
     */
    void replace(Object entity) throws DbException;

    void beginTransaction();

    void setTransactionSuccessful();

    void endTransaction();

    ///////////// delete
    void deleteById(Class<?> entityType, Object idValue) throws DbException;

    void delete(Object entity) throws DbException;

    void delete(Class<?> entityType) throws DbException;

    int delete(Class<?> entityType, WhereBuilder whereBuilder) throws DbException;

    ///////////// update
    void update(Object entity, String... updateColumnNames) throws DbException;

    int update(Class<?> entityType, WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException;

    ///////////// find
    <T> T findById(Class<T> entityType, Object idValue) throws DbException;

    <T> T findFirst(Class<T> entityType) throws DbException;

    <T> List<T> findAll(Class<T> entityType) throws DbException;

    <T> Selector<T> selector(Class<T> entityType) throws DbException;

    DbModel findDbModelFirst(SqlInfo sqlInfo) throws DbException;

    List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws DbException;

    ///////////// table

    /**
     * 获取表信息
     */
    <T> TableEntity<T> getTable(Class<T> entityType) throws DbException;

    /**
     * 删除表
     */
    void dropTable(Class<?> entityType) throws DbException;

    /**
     * 静默删除表
     */
    @Nullable
    DbException dropTableQuietly(Class<?> entityType);

    /**
     * 添加一列,
     * 新的entityType中必须定义了这个列的属性.
     */
    void addColumn(Class<?> entityType, String column) throws DbException;

    /**
     * 获取所有表名
     */
    List<String> getTables() throws DbException;

    ///////////// db

    /**
     * 删除库
     */
    void dropDb() throws DbException;

    /**
     * 静默删除库
     */
    @Nullable
    DbException dropDbQuietly();

    /**
     * 关闭数据库.
     * 同一个库是单实例的, 尽量不要调用这个方法, 会自动释放.
     */
    void close() throws IOException;

    ///////////// custom
    int executeUpdateDelete(SqlInfo sqlInfo) throws DbException;

    int executeUpdateDelete(String sql) throws DbException;

    void execNonQuery(SqlInfo sqlInfo) throws DbException;

    void execNonQuery(String sql) throws DbException;

    Cursor execQuery(SqlInfo sqlInfo) throws DbException;

    Cursor execQuery(String sql) throws DbException;

    CursorIterator<DbModel> iterator(SqlInfo sqlInfo) throws DbException;

    CursorIterator<DbModel> iterator(String sql) throws DbException;

    public interface DbOpenListener {
        void onDbOpened(DbManager db) throws DbException;
    }

    public interface DbUpgradeListener {
        void onUpgrade(DbManager db, int oldVersion, int newVersion) throws DbException;
    }

    public interface TableCreateListener {
        void onTableCreated(DbManager db, TableEntity<?> table);
    }

    public static class DaoConfig {
        private File dbDir;
        private String dbName = "xUtils.db"; // default db name
        private int dbVersion = 1;
        private boolean allowTransaction = true;
        private DbUpgradeListener dbUpgradeListener;
        private TableCreateListener tableCreateListener;
        private DbOpenListener dbOpenListener;

        public DaoConfig() {
        }

        public DaoConfig setDbDir(File dbDir) {
            this.dbDir = dbDir;
            return this;
        }

        public DaoConfig setDbName(String dbName) {
            if (!TextUtils.isEmpty(dbName)) {
                this.dbName = dbName;
            }
            return this;
        }

        public DaoConfig setDbVersion(int dbVersion) {
            this.dbVersion = dbVersion;
            return this;
        }

        public DaoConfig setAllowTransaction(boolean allowTransaction) {
            this.allowTransaction = allowTransaction;
            return this;
        }

        public DaoConfig setDbOpenListener(DbOpenListener dbOpenListener) {
            this.dbOpenListener = dbOpenListener;
            return this;
        }

        public DaoConfig setDbUpgradeListener(DbUpgradeListener dbUpgradeListener) {
            this.dbUpgradeListener = dbUpgradeListener;
            return this;
        }

        public DaoConfig setTableCreateListener(TableCreateListener tableCreateListener) {
            this.tableCreateListener = tableCreateListener;
            return this;
        }

        public File getDbDir() {
            return dbDir;
        }

        public String getDbName() {
            return dbName;
        }

        public int getDbVersion() {
            return dbVersion;
        }

        public boolean isAllowTransaction() {
            return allowTransaction;
        }

        public DbOpenListener getDbOpenListener() {
            return dbOpenListener;
        }

        public DbUpgradeListener getDbUpgradeListener() {
            return dbUpgradeListener;
        }

        public TableCreateListener getTableCreateListener() {
            return tableCreateListener;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DaoConfig daoConfig = (DaoConfig) o;

            if (!dbName.equals(daoConfig.dbName)) return false;
            return dbDir == null ? daoConfig.dbDir == null : dbDir.equals(daoConfig.dbDir);
        }

        @Override
        public int hashCode() {
            int result = dbName.hashCode();
            result = 31 * result + (dbDir != null ? dbDir.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return String.valueOf(dbDir) + "/" + dbName;
        }
    }
}
