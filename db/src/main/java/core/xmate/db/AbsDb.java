package core.xmate.db;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * 调用 {@link IDao#access(DbManager, Object)} 方法。
     *
     * 该方法不会缓存实例。
     *
     * @param dao
     * @param params
     * @param <Params>
     * @param <Result>
     * @return
     * @throws DbException
     */
    public <Params, Result> Result accessSync(IDao<Params, Result> dao, Params params) throws DbException {
        return dao.access(get(), params);
    }

    /**
     * 获取 dao 的缓存实例，或者反射创建实例再缓存起来，并同步执行其 {@link IDao#access(DbManager, Object)}方法。
     *
     * @param dao
     * @param params
     * @param <Params>
     * @param <Result>
     * @return
     * @throws DbException
     */
    public <Params, Result> Result accessSync(Class<? extends IDao<Params, Result>> dao
            , Params params) throws DbException {
        return accessSync(getCacheOrCreate(dao), params);
    }

	/*Cache*/

    private static final int DEFAULT_DAO_CACHE_SIZE = 8;
    private volatile Map<Class, IDao> daoCaches;

    private Map<Class, IDao> getCaches() {
        if (daoCaches == null) {
            synchronized (this) {
                if (daoCaches == null) {
                    daoCaches = new ConcurrentHashMap<>(DEFAULT_DAO_CACHE_SIZE);
                }
            }
        }
        return daoCaches;
    }

    private void cache(IDao dao) {
        Class clz = dao != null ? dao.getClass() : null;
        if (clz != null) {
            //允许静态的内部类
            boolean isStatistic = Modifier.isStatic(clz.getModifiers());
            //允许单独定义在一个java文件的类型
            boolean isCommon = !clz.isMemberClass() && !clz.isLocalClass() && !clz.isAnonymousClass();
            if (isStatistic || isCommon) {
                getCaches().put(clz,dao);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getCache(Class<T> clazz) {
        return (T) getCaches().get(clazz);
    }

    private <T extends IDao> T getCacheOrCreate(Class<T> clazz) {
        T dao = getCache(clazz);
        if (dao == null) {
            synchronized (this) {
                dao = getCache(clazz);
                if (dao == null) {
                    try {
                        dao = clazz.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cache(dao);
                }
            }
        }
        return dao;
    }

}
