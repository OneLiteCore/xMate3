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

import core.mate.async.Clearable;
import core.mate.async.CoreTask;
import core.mate.async.CoreTask.OnTaskListener;
import core.mate.db.dao.ExecNonQuerySqlDao;
import core.mate.db.dao.FindFirstDbModelDao;
import core.mate.util.LogUtil;
import core.mate.view.ITaskIndicator;

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
     * 该方法中可能执行部分耗时操作，如无必要，请通过{@link AbsDao#access(DbManager)}
     * 方法来获得实例。
     *
     * @return
     * @throws DbException
     */
    public DbManager getOrCreateDb() throws DbException {
        if (dbMgr == null) {
            synchronized (this) {
                if (dbMgr == null) {
                    try {//准备数据库
                        onPrepare();
                    } catch (Exception e) {
                        LogUtil.e(e);
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException) e;
                        } else {
                            throw new DbException("onPrepare抛出异常，无法创建数据库");
                        }
                    }
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
     * <p>
     * 如果该方法抛出异常则认定数据库无法创建。
     *
     * @return
     */
    protected void onPrepare() throws Exception {
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
    
    public <Result> Clearable access(AbsDao<Result> dao) {
        return access(dao, null, null);
    }
    
    public <Result> Clearable access(AbsDao<Result> dao, ITaskIndicator indicator) {
        return access(dao, indicator, null);
    }
    
    public <Result> Clearable access(AbsDao<Result> dao, OnTaskListener<Result> listener) {
        return access(dao, null, listener);
    }
    
    public <Result> Clearable access(AbsDao<Result> dao, ITaskIndicator indicator, OnTaskListener<Result> listener) {
        DaoTask<Result> task = new DaoTask<>();
        task.addIndicator(indicator);
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
    public <Result> Result accessSync(AbsDao<Result> dao) throws Exception {
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
    
    public boolean isCacheDaoEnable() {
        return cacheDaoEnable;
    }
    
    public CoreDb setCacheDaoEnable() {
        return setCacheDaoEnable(DEFAULT_DAO_CACHE_SIZE);
    }
    
    public CoreDb setCacheDaoEnable(int cacheSize) {
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
    
    public synchronized <T extends AbsDao> T getCachedDao(Class<T> clazz) {
        AbsDao dao = null;
        if (cacheDaoEnable) {
            synchronized (daoCacheLock) {
                WeakReference<AbsDao> ref = getDaoCache().get(clazz);
                if (ref != null) {
                    dao = ref.get();
                    if (dao != null) {
                        dao.clear();
                    }
                    //获取引用之后就清空缓存中的引用
                    ref.clear();
                    daoCache.remove(clazz);
                }
            }
        }
        return (T) dao;
    }
    
    public <T extends AbsDao> T getCachedDaoOrNewInstance(Class<T> clazz) {
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
    
    public void execNonQuery(String sql, Object... args) {
        execNonQuery(new SqlInfo(sql).addBindArgs(args));
    }
    
    public void execNonQuery(SqlInfo sql) {
        ExecNonQuerySqlDao dao = getCachedDao(ExecNonQuerySqlDao.class);
        if (dao == null) {
            dao = new ExecNonQuerySqlDao();
        }
        dao.setSql(sql);
        access(dao);
    }
    
    public DbModel execQuerySync(String sql, Object... args) throws Exception {
        return execQuerySync(new SqlInfo(sql).addBindArgs(args));
    }
    
    public DbModel execQuerySync(SqlInfo sql) throws Exception {
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
    public void close() {
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
