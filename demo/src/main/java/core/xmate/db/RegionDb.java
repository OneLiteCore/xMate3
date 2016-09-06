package core.xmate.db;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.io.File;

import core.mate.db.CoreDb;
import core.mate.util.ResUtil;
import core.xmate.App;
import core.xmate.db.region.Area;
import core.xmate.db.region.City;
import core.xmate.db.region.Province;

/**
 * 对一个数据库的封装
 *
 * @author DrkCore
 * @since 2016-09-06
 */
public class RegionDb extends CoreDb {

    public static final String DB_NAME = "region.db";
    public static final int DB_VERSION = 1;

    private static volatile RegionDb instance = null;

    private RegionDb() {
        super(DB_NAME, DB_VERSION);
        setCacheDaoEnable();//启动dao缓存
    }

    /**
     * 按照方法{@link DbManager#close()}的注释xUtils3对一个数据库
     * 的连接就是单例的，通常不需要close调用。
     * <p>
     * 因而这里建议使用单例来实现每一个db。
     *
     * @return
     */
    public static RegionDb getInstance() {
        if (instance == null) {
            synchronized (RegionDb.class) {
                if (instance == null) {
                    instance = new RegionDb();
                }
            }
        }
        return instance;
    }

    /*继承*/

    @Override
    protected void onPrepare() throws Exception {
        //该方法将在子线程中执行，因而这里可以同步处理耗时的操作
        //比如从assets中导出数据库文件
        File dbFile = App.getInstance().getDatabasePath(DB_NAME);
        ResUtil.exportAssetFile(DB_NAME, dbFile.getParentFile());
    }

    @Override
    protected void onCreate(DbManager db) throws DbException {
        super.onCreate(db);
        //xUtils3原版中把该方法屏蔽掉了，并默认会在第一次插入数据的时候创建表
        //由于感觉缺少可控性于是果断把这个方法的权限弄回来了
        db.createTableIfNotExist(Province.class);
        db.createTableIfNotExist(City.class);
        db.createTableIfNotExist(Area.class);
    }

}
