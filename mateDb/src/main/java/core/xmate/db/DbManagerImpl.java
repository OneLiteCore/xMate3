/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package core.xmate.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.xmate.db.sqlite.CursorIterator;
import core.xmate.db.sqlite.DbModelCursorIterator;
import core.xmate.db.sqlite.SqlInfo;
import core.xmate.db.sqlite.SqlInfoBuilder;
import core.xmate.db.sqlite.WhereBuilder;
import core.xmate.db.table.ColumnEntity;
import core.xmate.db.table.DbBase;
import core.xmate.db.table.DbModel;
import core.xmate.db.table.TableEntity;
import core.xmate.util.IOUtil;
import core.xmate.util.KeyValue;
import core.xmate.util.LogUtil;

public final class DbManagerImpl extends DbBase {

    //*************************************** create instance ****************************************************

    /**
     * key: dbName
     */
    private final static HashMap<DaoConfig, DbManagerImpl> DAO_MAP = new HashMap<DaoConfig, DbManagerImpl>();

    private SQLiteDatabase database;
    private DaoConfig daoConfig;
    private final boolean allowTransaction;

    private DbManagerImpl(Context context, DaoConfig config) throws DbException {
        if (config == null) {
            throw new IllegalArgumentException("daoConfig may not be null");
        }

        this.daoConfig = config;
        this.allowTransaction = config.isAllowTransaction();
        try {
            this.database = openOrCreateDatabase(context, config);
            DbOpenListener dbOpenListener = config.getDbOpenListener();
            if (dbOpenListener != null) {
                dbOpenListener.onDbOpened(this);
            }
        } catch (DbException ex) {
            IOUtil.closeQuietly(this.database);
            throw ex;
        } catch (Throwable ex) {
            IOUtil.closeQuietly(this.database);
            throw new DbException(ex.getMessage(), ex);
        }
    }

    public synchronized static DbManager getInstance(Context context, DaoConfig daoConfig) throws DbException {
        if (daoConfig == null) {//使用默认配置
            daoConfig = new DaoConfig();
        }

        DbManagerImpl dao = DAO_MAP.get(daoConfig);
        if (dao == null) {
            dao = new DbManagerImpl(context, daoConfig);
            DAO_MAP.put(daoConfig, dao);
        } else {
            dao.daoConfig = daoConfig;
        }

        // update the database if needed
        SQLiteDatabase database = dao.database;
        int oldVersion = database.getVersion();
        int newVersion = daoConfig.getDbVersion();
        if (oldVersion != newVersion) {
            DbManager.DbUpgradeListener upgradeListener = daoConfig.getDbUpgradeListener();
            if (upgradeListener != null) {
                upgradeListener.onUpgrade(dao, oldVersion, newVersion);
            } else if (oldVersion != 0) {
                try {
                    dao.dropDb();
                } catch (DbException e) {
                    LogUtil.e(e.getMessage(), e);
                }
            }
            database.setVersion(newVersion);
        }

        return dao;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return database;
    }

    @Override
    public DaoConfig getDaoConfig() {
        return daoConfig;
    }

    //*********************************************** operations ********************************************************


    @Override
    public void saveOrUpdate(Object entity) throws DbException {
        saveOrUpdate(entity, true);
    }

    @Override
    public void saveOrUpdate(Object entity, final boolean withTransaction) throws DbException {
        try {
            if (withTransaction) {
                beginTransaction();
            }

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = this.getTable(entities.get(0).getClass());
                table.createTableIfNotExists();
                for (Object item : entities) {
                    saveOrUpdateWithoutTransaction(table, item);
                }
            } else {
                TableEntity<?> table = this.getTable(entity.getClass());
                table.createTableIfNotExists();
                saveOrUpdateWithoutTransaction(table, entity);
            }

            if (withTransaction) {
                setTransactionSuccessful();
            }
        } finally {
            if (withTransaction) {
                endTransaction();
            }
        }
    }

    @Override
    public void replace(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = this.getTable(entities.get(0).getClass());
                table.createTableIfNotExists();
                for (Object item : entities) {
                    execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table, item));
                }
            } else {
                TableEntity<?> table = this.getTable(entity.getClass());
                table.createTableIfNotExists();
                execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table, entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void save(Object entity) throws DbException {
        save(entity, true);
    }

    @Override
    public void save(Object entity, final boolean withTransaction) throws DbException {
        try {
            if (withTransaction) {
                beginTransaction();
            }

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = this.getTable(entities.get(0).getClass());
                table.createTableIfNotExists();
                for (Object item : entities) {
                    execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, item));
                }
            } else {
                TableEntity<?> table = this.getTable(entity.getClass());
                table.createTableIfNotExists();
                execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            }

            if (withTransaction) {
                setTransactionSuccessful();
            }
        } finally {
            if (withTransaction) {
                endTransaction();
            }
        }
    }

    @Override
    public boolean saveBindingId(Object entity) throws DbException {
        boolean result = false;
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return false;
                TableEntity<?> table = this.getTable(entities.get(0).getClass());
                table.createTableIfNotExists();
                for (Object item : entities) {
                    if (!saveBindingIdWithoutTransaction(table, item)) {
                        throw new DbException("saveBindingId error, transaction will not commit!");
                    }
                }
            } else {
                TableEntity<?> table = this.getTable(entity.getClass());
                table.createTableIfNotExists();
                result = saveBindingIdWithoutTransaction(table, entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
        return result;
    }

    @Override
    public void deleteById(Class<?> entityType, Object idValue) throws DbException {
        TableEntity<?> table = this.getTable(entityType);
        if (!table.tableIsExists()) return;
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfoById(table, idValue));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void delete(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = this.getTable(entities.get(0).getClass());
                if (!table.tableIsExists()) return;
                for (Object item : entities) {
                    execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(table, item));
                }
            } else {
                TableEntity<?> table = this.getTable(entity.getClass());
                if (!table.tableIsExists()) return;
                execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(table, entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void delete(Class<?> entityType) throws DbException {
        delete(entityType, null);
    }

    @Override
    public int delete(Class<?> entityType, WhereBuilder whereBuilder) throws DbException {
        TableEntity<?> table = this.getTable(entityType);
        if (!table.tableIsExists()) return 0;
        int result = 0;
        try {
            beginTransaction();

            result = executeUpdateDelete(SqlInfoBuilder.buildDeleteSqlInfo(table, whereBuilder));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
        return result;
    }

    @Override
    public void update(Object entity, String... updateColumnNames) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                TableEntity<?> table = this.getTable(entities.get(0).getClass());
                if (!table.tableIsExists()) return;
                for (Object item : entities) {
                    execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table, item, updateColumnNames));
                }
            } else {
                TableEntity<?> table = this.getTable(entity.getClass());
                if (!table.tableIsExists()) return;
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table, entity, updateColumnNames));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public int update(Class<?> entityType, WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException {
        TableEntity<?> table = this.getTable(entityType);
        if (!table.tableIsExists()) return 0;

        int result = 0;
        try {
            beginTransaction();

            result = executeUpdateDelete(SqlInfoBuilder.buildUpdateSqlInfo(table, whereBuilder, nameValuePairs));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }

        return result;
    }

    @Override
    public <T> T findById(Class<T> entityType, Object idValue) throws DbException {
        TableEntity<T> table = this.getTable(entityType);
        if (!table.tableIsExists()) return null;

        Selector<T> selector = Selector.from(table).where(table.getId().getName(), "=", idValue);
        String sql = selector.limit(1).toString();
        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getEntity(table, cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return null;
    }

    @Override
    public <T> T findFirst(Class<T> entityType) throws DbException {
        return this.selector(entityType).findFirst();
    }

    @Override
    public <T> List<T> findAll(Class<T> entityType) throws DbException {
        return this.selector(entityType).findAll();
    }

    @Override
    public <T> Selector<T> selector(Class<T> entityType) throws DbException {
        return Selector.from(this.getTable(entityType));
    }

    @Override
    public DbModel findDbModelFirst(SqlInfo sqlInfo) throws DbException {
        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return null;
    }

    @Override
    public List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws DbException {
        List<DbModel> dbModelList = new ArrayList<DbModel>();

        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return dbModelList;
    }

    //******************************************** config ******************************************************

    private SQLiteDatabase openOrCreateDatabase(Context context, DbManager.DaoConfig config) {
        SQLiteDatabase result = null;

        File dbDir = config.getDbDir();
        if (dbDir != null && (dbDir.exists() || dbDir.mkdirs())) {
            File dbFile = new File(dbDir, config.getDbName());
            result = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } else {
            result = context.openOrCreateDatabase(config.getDbName(), 0, null);
        }
        return result;
    }

    //***************************** private operations with out transaction *****************************
    private void saveOrUpdateWithoutTransaction(TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            if (id.getColumnValue(entity) != null) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table, entity));
            } else {
                saveBindingIdWithoutTransaction(table, entity);
            }
        } else {
            execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table, entity));
        }
    }

    private boolean saveBindingIdWithoutTransaction(TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            long idValue = getLastAutoIncrementId(table.getName());
            if (idValue == -1) {
                return false;
            }
            id.setAutoIdValue(entity, idValue);
            return true;
        } else {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            return true;
        }
    }

    //************************************************ tools ***********************************

    private long getLastAutoIncrementId(String tableName) throws DbException {
        long id = -1;
        Cursor cursor = execQuery("SELECT seq FROM sqlite_sequence WHERE name='" + tableName + "' LIMIT 1");
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    id = cursor.getLong(0);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return id;
    }

    /**
     * 关闭数据库.
     * 同一个库的是单实例的, 尽量不要调用这个方法, 会自动释放.
     */
    @Override
    public void close() throws IOException {
        if (DAO_MAP.containsKey(daoConfig)) {
            DAO_MAP.remove(daoConfig);
            this.database.close();
        }
    }

    ///////////////////////////////////// exec sql /////////////////////////////////////////////////////

    @Override
    public void beginTransaction() {
        if (allowTransaction) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && database.isWriteAheadLoggingEnabled()) {
                database.beginTransactionNonExclusive();
            } else {
                database.beginTransaction();
            }
        }
    }

    @Override
    public void setTransactionSuccessful() {
        if (allowTransaction) {
            database.setTransactionSuccessful();
        }
    }

    @Override
    public void endTransaction() {
        if (allowTransaction) {
            database.endTransaction();
        }
    }

    @Override
    public int executeUpdateDelete(SqlInfo sqlInfo) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = sqlInfo.buildStatement(database);
            return statement.executeUpdateDelete();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.releaseReference();
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public int executeUpdateDelete(String sql) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = database.compileStatement(sql);
            return statement.executeUpdateDelete();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.releaseReference();
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public void execNonQuery(SqlInfo sqlInfo) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = sqlInfo.buildStatement(database);
            statement.execute();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.releaseReference();
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public void execNonQuery(String sql) throws DbException {
        try {
            database.execSQL(sql);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public Cursor execQuery(SqlInfo sqlInfo) throws DbException {
        try {
            return database.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public Cursor execQuery(String sql) throws DbException {
        try {
            return database.rawQuery(sql, null);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public CursorIterator<DbModel> iterator(SqlInfo sqlInfo) throws DbException {
        Cursor cursor = null;
        try {
            cursor = execQuery(sqlInfo);
            if (cursor != null) {
                return new DbModelCursorIterator(cursor);
            }
            return DbModelCursorIterator.EMPTY_INSTANCE;
        } catch (Throwable e) {
            IOUtil.closeQuietly(cursor);
            if (e instanceof DbException) {
                throw e;
            }
            throw new DbException(e);
        }
    }

    @Override
    public CursorIterator<DbModel> iterator(String sql) throws DbException {
        Cursor cursor = null;
        try {
            cursor = execQuery(sql);
            if (cursor != null) {
                return new DbModelCursorIterator(cursor);
            }
            return DbModelCursorIterator.EMPTY_INSTANCE;
        } catch (Throwable e) {
            IOUtil.closeQuietly(cursor);
            if (e instanceof DbException) {
                throw e;
            }
            throw new DbException(e);
        }
    }
}
