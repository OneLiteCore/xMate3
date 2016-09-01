package core.mate.db;

import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import core.mate.async.CoreTask;
import core.mate.async.CoreTask.OnTaskListener;
import core.mate.common.Clearable;
import core.mate.common.ITaskIndicator;
import core.mate.db.dao.ExecNonQuerySqlDao;
import core.mate.db.dao.FindFirstDbModelDao;
import core.mate.util.LogUtil;

/**
 * 基于xUtils的数据库基类。
 *
 * @author DrkCore
 * @since 2015年11月12日00:33:19
 */
public abstract class CoreDb extends DbManager.DaoConfig implements DbManager.DbUpgradeListener, DbManager.DbOpenListener, DbManager.TableCreateListener {

    public CoreDb(String dbName, int dbVersion) {
        this(null, dbName, dbVersion);
    }

    public CoreDb(File inDir, String dbName, int dbVersion) {
        super();
        setDbDir(inDir);
        setDbName(dbName);
        setDbVersion(dbVersion);
        setDbUpgradeListener(this);
        setDbOpenListener(this);
        setTableCreateListener(this);
    }

	/* 初始化数据库工具 */

    private volatile DbManager dbMgr;

    /**
     * 获取该db对应的{@link DbManager}。首次获取后的实例会被缓存到成员变量中。
     *
     * @return
     * @throws DbException
     */
    /*package*/
    final DbManager getOrCreateDb() throws DbException {
        if (dbMgr == null) {
            synchronized (this) {
                if (dbMgr == null && onPrepare()) {// 仅当onPrepare返回true时创建。
                    // 避免出现onCreate抛出异常却仍然创建了DbManager实例
                    DbManager tmpDbManager = x.getDb(this);
                    onCreate(tmpDbManager);
                    dbMgr = tmpDbManager;
                }
            }
        }
        return dbMgr;
    }

	/* 内部回调 */

    /**
     * 准备数据库的回调，将在创建DbManager之前回调。
     * 该方法将在异步线程中执行，你可以在这个方法中做耗时的准备操作，
     * 比如将assets中携带的数据库导出或者清空旧的数据库文件等。
     * 其返回值用于标志数据库是否可用，默认为true。
     * <b>返回false将无法创建{@link DbManager}的实例。</b>
     *
     * @return
     */
    protected boolean onPrepare() {
        return true;
    }

    /**
     * 第一次从该DaoConfig中创建{@link DbManager}时回调该方法。
     * 你可以在该方法中创建数据表等操作。
     * <b>但是一旦操作抛出异常将导致创建{@link DbManager}实例失败。</b>
     *
     * @param db
     * @throws DbException
     */
    protected void onCreate(DbManager db) throws DbException {
    }

    @Override
    public void onDbOpened(DbManager db) {
    }

    @Override
    public void onTableCreated(DbManager db, TableEntity<?> table) {
    }

    /**
     * 从该DaoConfig中创建DbManager时，当已存在的数据库的version和DaoConfig中指定的version<b>不一致</b>
     * 时回调该方法。<b>该方法将在异步线程之中执行</b>。
     *
     * @param db
     * @param oldVersion 已存在的数据的版本号。注意，这个版本号不一定小于newVersion。
     * @param newVersion 将要创建的数据库的版本号。注意，这个版本号不一定大于oldVersion。
     */
    @Override
    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
    }

	/* DAO操作 */

    private class DaoTask<Result> extends CoreTask<AbsDao<Result>, Void, Result> {

        @Override
        public Result doInBack(AbsDao<Result> dao) throws Exception {
            return accessSync(dao);
        }
    }

    public final <Result> Clearable access(AbsDao<Result> dao) {
        return access(dao, null, null);
    }

    public final <Result> Clearable access(AbsDao<Result> dao, ITaskIndicator indicator) {
        return access(dao, indicator, null);
    }

    public final <Result> Clearable access(AbsDao<Result> dao, OnTaskListener<Result> listener) {
        return access(dao, null, listener);
    }

    public final <Result> Clearable access(AbsDao<Result> dao, ITaskIndicator indicator, OnTaskListener<Result> listener) {
        DaoTask<Result> task = new DaoTask<>();
        task.setIndicator(indicator);
        task.addOnTaskListener(listener);
        task.execute(dao);
        return task;
    }

    /**
     * 同步访问数据库。
     *
     * @param dao
     * @param <Result>
     * @return
     * @throws Exception
     */
    public final <Result> Result accessSync(AbsDao<Result> dao) throws Exception {
        Result result = dao.access(getOrCreateDb());
        cacheDao(dao);
        return result;
    }

	/*Dao缓存*/

    public static final int DEFAULT_DAO_CACHE_SIZE = 8;
    private int daoCacheSize;

    private boolean cacheDaoEnable;

    /**
     * LrcCache是线程安全的
     */
    private volatile ConcurrentMap<Class, WeakReference<AbsDao>> daoCache;

    private ConcurrentMap<Class, WeakReference<AbsDao>> getDaoCache() {
        if (daoCache == null) {
            synchronized (this) {
                if (daoCache == null) {
                    daoCache = new ConcurrentHashMap<>(daoCacheSize);
                }
            }
        }
        return daoCache;
    }

    public final boolean isCacheDaoEnable() {
        return cacheDaoEnable;
    }

    public final CoreDb setCacheDaoEnable() {
        return setCacheDaoEnable(DEFAULT_DAO_CACHE_SIZE);
    }

    public final CoreDb setCacheDaoEnable(int cacheSize) {
        if (!cacheDaoEnable) {
            this.daoCacheSize = cacheSize;
            this.cacheDaoEnable = true;

            if (cacheSize <= 0) {
                throw new IllegalArgumentException();
            }
        }
        return this;
    }

    private final Object daoCacheLock = new Object();

    /**
     * 缓存dao实例。通过{@link #setCacheDaoEnable()}启用了缓存之后每次访问数据库完成后都会缓存dao对象。
     * 需要注意的是，为了避免内存泄露<b>匿名类的dao实例会被过滤</b>。
     *
     * @param absDao
     * @return
     */
    private boolean cacheDao(AbsDao absDao) {
        Class clz = cacheDaoEnable && absDao != null ? absDao.getClass() : null;
        if (clz != null) {
            // 不缓存可能会带有外部类的强引用（很多时候都是Activity或者Fragment）的Dao类型
            if (Modifier.isStatic(clz.getModifiers())/*允许静态的内部类*/
                    || (!clz.isMemberClass() && !clz.isLocalClass() && !clz.isAnonymousClass())/*非静态非成员非局部非匿名，也就是单独定义在一个java文件的类型*/) {
                synchronized (daoCacheLock) {
                    getDaoCache().put(clz, new WeakReference<>(absDao));
                }
                return true;
            }
        }
        return false;
    }

    public synchronized final <T extends AbsDao> T getCachedDao(Class<T> clazz) {
        AbsDao dao = null;
        if (cacheDaoEnable) {
            synchronized (daoCacheLock) {
                WeakReference<AbsDao> ref = getDaoCache().get(clazz);
                if (ref != null) {
                    dao = ref.get();
                    //获取引用之后就清空缓存中的引用
                    ref.clear();
                    daoCache.remove(clazz);
                }
            }
        }
        return (T) dao;
    }

    public final <T extends AbsDao> T getCachedDaoOrNewInstance(Class<T> clazz) {
        T dao = getCachedDao(clazz);
        if (dao == null) {
            try {
                dao = clazz.newInstance();
            } catch (Exception e) {
                LogUtil.e(e);
            }
        }
        return dao;
    }

	/*SQL语法操作*/

    public final void execNonQuery(String sql, Object... args) {
        execNonQuery(new SqlInfo(sql).addBindArgs(args));
    }

    public final void execNonQuery(SqlInfo sql) {
        ExecNonQuerySqlDao dao = getCachedDao(ExecNonQuerySqlDao.class);
        if (dao == null) {
            dao = new ExecNonQuerySqlDao();
        }
        dao.setSql(sql);
        access(dao);
    }

    public final DbModel execQuerySync(String sql, Object... args) throws Exception {
        return execQuerySync(new SqlInfo(sql).addBindArgs(args));
    }

    public final DbModel execQuerySync(SqlInfo sql) throws Exception {
        FindFirstDbModelDao dao = getCachedDao(FindFirstDbModelDao.class);
        if (dao == null) {
            dao = new FindFirstDbModelDao();
        }
        dao.setSql(sql);
        return accessSync(dao);
    }

	/*拓展*/

    /**
     * 按照xUtils3的注释（{@link DbManager#close()}），通常不需要关闭数据库。
     */
    public final void close() {
        if (dbMgr != null) {
            try {
                dbMgr.close();
            } catch (IOException e) {
                LogUtil.e(e);
            }
        }
        //清空缓存的Dao
        daoCache = null;
    }
}
