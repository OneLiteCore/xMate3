package core.xmate.db;

import android.annotation.SuppressLint;
import android.content.Context;

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
public abstract class MateDb extends DbManager.DaoConfig implements DbManager.DbUpgradeListener,
        DbManager.DbOpenListener, DbManager.TableCreateListener, Closeable {

    /*Init*/

    /**
     * Using static field holding {@link android.app.Application} context will not lead to leaking problem.
     * <p>
     * 使用静态变量持有{@link android.app.Application}不会触发内存泄露。
     */
    @SuppressLint("StaticFieldLeak")
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static DbManager getDb(DbManager.DaoConfig daoConfig) {
        if (appContext == null) {
            throw new IllegalStateException("MateDb数据库框架还未初始化");
        }
        return getDb(appContext, daoConfig);
    }

    public static DbManager getDb(Context context, DbManager.DaoConfig daoConfig) {
        return DbManagerImpl.getInstance(context, daoConfig);
    }

    /*Db*/

    public MateDb(String dbName, int dbVersion) {
        this(null, dbName, dbVersion);
    }

    public MateDb(File inDir, String dbName, int dbVersion) {
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
        return getDb(this);
    }

    /**
     * 每次运行时首次访问数据库时该方法会被回调。
     * <p>
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
     * <p>
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
     * <p>
     * 注意，请不要在该方法中调用与{@link #get()}相关的任何代码，包括{@link #accessSync(IDao)}，
     * 否则必定抛出{@link StackOverflowError#}异常。如若此时要对数据库进行操作，请直接使用db参数。
     *
     * @param db
     * @param oldVersion 升级前版本号，初次创建时为0
     * @param newVersion 新的版本号，注意该值并不一定大于oldVersion
     */
    @Override
    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
    }

}
