package core.xmate.db;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import core.xmate.db.table.TableEntity;

/**
 * 基于xUtils的数据库基类。
 *
 * @author DrkCore
 * @since 2015年11月12日00:33:19
 */
public abstract class AbsDb extends DbManager.DaoConfig implements DbManager.DbUpgradeListener,
        DbManager.DbOpenListener, DbManager.TableCreateListener, Closeable {

    public AbsDb(String dbName, int dbVersion) {
        this(null, dbName, dbVersion);
    }

    public AbsDb(File inDir, String dbName, int dbVersion) {
        super();
        setDbDir(inDir);
        setDbName(dbName);
        setDbVersion(dbVersion);
        setDbUpgradeListener(this);
        setDbOpenListener(this);
        setTableCreateListener(this);
    }

    private volatile DbManager dbMgr;

    /**
     * 获取该db对应的{@link DbManager}。
     *
     * @return
     * @throws DbException
     */
    public DbManager get() throws DbException {
        if (dbMgr == null) {
            synchronized (this) {
                if (dbMgr == null) {
                    dbMgr = onCreate();
                }
            }
        }
        return dbMgr;
    }

    /**
     * 按照xUtils3的注释（{@link DbManager#close()}），通常不需要关闭数据库。
     */
    @Override
    public void close() throws IOException {
        if (dbMgr != null) {
            synchronized (this) {
                if (dbMgr != null) {
                    try {
                        dbMgr.close();
                    } finally {
                        dbMgr = null;
                    }
                }
            }
        }
    }

	/* LifeCircle */

    protected abstract DbManager onCreate() throws DbException;

    @Override
    public void onDbOpened(DbManager db) {
    }

    @Override
    public void onTableCreated(DbManager db, TableEntity<?> table) {
    }

    @Override
    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
    }

    /*Dao*/

    public <Result> Result accessSync(IDao<Result> dao) throws DbException {
        return dao.access(get());
    }

}
