package core.xmate.db;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import core.xmate.MateDb;
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

    public DbManager get() {
        if (dbMgr == null) {
            synchronized (this) {
                if (dbMgr == null) {
                    dbMgr = createDb();
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

    protected DbManager createDb() {
        return MateDb.getDb(this);
    }

    /**
     * 每次运行时首次访问数据库时该方法会被回调。
     *
     * 注意，请不要在该方法中调用与{@link #get()}相关的任何代码，包括{@link #accessSync(IDao)}，
     * 否则必定抛出{@link StackOverflowError#}异常。如若此时要对数据库进行操作，请直接使用db参数。
     *
     * @param db
     */
    @Override
    public void onDbOpened(DbManager db) {
    }

    /**
     * 当创建表时该方法会被调用。
     *
     * 注意，请不要在该方法中调用与{@link #get()}相关的任何代码，包括{@link #accessSync(IDao)}，
     * 否则必定抛出{@link StackOverflowError#}异常。如若此时要对数据库进行操作，请直接使用db参数。
     *
     * @param db
     * @param table
     */
    @Override
    public void onTableCreated(DbManager db, TableEntity<?> table) {
    }

    /**
     * 当数据库初次创建或者升级时回调该方法。
     *
     * 注意，请不要在该方法中调用与{@link #get()}相关的任何代码，包括{@link #accessSync(IDao)}，
     * 否则必定抛出{@link StackOverflowError#}异常。如若此时要对数据库进行操作，请直接使用db参数。
     *
     * @param db
     * @param oldVersion 升级前版本号，初次创建时为0
     * @param newVersion 新的版本号，注意改值并不一定大于oldVersion
     */
    @Override
    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
    }

    /*Dao*/

    public <Result> Result accessSync(IDao<Result> dao) throws DbException {
        return dao.access(get());
    }

}
