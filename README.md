# xMate

## 简介
本框架基于xUtils3进行二次开发，与原版的主要差别如下：

    * 封装Http模块，并提供类似postman的详细日志输出
    * 封装数据库模块，添加数据访问对象Dao的封装
    * 修改视图注入和数据库的部分源码，更加自由灵活

本项目fork自xUtils3但不再赘述其相关介绍，如果你还不了解xUtils框架建议先到[原版]([xUtils3](https://github.com/wyouflf/xUtils3))的repo中自行了解。

欢迎大家star~

## 如何集成
Android Studio用户可有在模块的build.gradle中于dependencies下添加如下依赖：

```
compile 'core.mate:core:1.0.3'
compile 'core.mate:xutils:1.0.3'
```

之后在你的Application中进行初始化：

```java
//初始化CoreMate框架
Core.getInstance().init(this);
//开启debug日志输出
Core.getInstance().setDevModeEnable(true);

//初始化xUtils3框架
x.Ext.init(this);
//开启日志输出
x.Ext.setDebug(true);
```

在AndroidManifest.xml中添加xUtils所需的权限：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 如何封装HTTP请求
按照单一职责原则一个类只干一件事情是最好的，所以这里推荐大家应该将服务器上的一个API单独封装成一个类，如下：
```java
/**
 * 访问天气接口。
 * <p>
 * 这里ApiAction基类会将自动将服务器返回的JSONObject字符串转化成泛型
 * 中指定的类的实例。如果服务器返回的是JSONArray，那么你只要将泛型写成类似于List《Weather》即可。
 *
 * @author DrkCore
 * @since 2016年9月4日22:00:07
 */
public class WeatherAction extends ApiAction<Weather> {

    private static final String BASE_URL = "http://mobile.weather.com.cn/data/sk/";
    public static final String CITY_BEIJING = "101010100";// 北京

    /**
     * 将服务器需要的参数直接写成方法的形参，通过JAVA本身的机制来避免写错key值的错误发生。
     *
     * @param cityId
     * @return
     */
    public Clearable request(String cityId) {
        return requestGet(TextUtil.buildString(BASE_URL, cityId, ".html"));
    }

    /**
     * 提取数据所在的字符串
     *
     * @param dataStr
     * @return
     */
    @Override
    protected String onPrepareDataString(String dataStr) throws IllegalDataException {
        //服务器返回的字符串如下：
        //{"sk_info":{"date":"20131012","cityName":"北京","areaID":"101010100","temp":"21℃","tempF":"69.8℉","wd":"东风","ws":"3级","sd":"39%","time":"15:10","sm":"暂无实况"}}
        //其中真正的数据在sk_info对应的字段中
        //你可以使用JSON解析取出其中的数据，也可以直接使用String.subString()方法。
        return JSON.parseObject(dataStr).getString("sk_info");
    }
}
```
外部的业务逻辑不需要知道具体的访问细节，只需要提供参数并处理好回调即可，如下：
```java
    private WeatherAction weatherAction;

    @Event(R.id.button_frag_http_requestWeather)
    private void requestWeather() {
        if (weatherAction == null) {
            weatherAction = new WeatherAction();
            //设置启用缓存，只有Action从未发出过请求前调用才有效
            weatherAction.setCacheEnable();
            //设置当上一个请求未结束却又打算发出新的请求时，抛弃新的请求
            weatherAction.setConflictOperation(CoreAction.ConflictOperation.ABANDON_NEW_REQUEST);
            //添加转菊花的用户指示，阻塞用户的操作
            weatherAction.setIndicator(new ProgressDlgFrag().setFragmentManager(this));
            //设置回调
            weatherAction.addOnActionListener(new OnActionListenerImpl<Weather>() {

                @Override
                public boolean onCache(Weather weather) {
                    return false;
                }

                @Override
                public void onSuccess(Weather weather) {
                    ToastUtil.toastShort("请求成功，具体内容请以Action类名查看Log");
                }
            });
        }
        weatherAction.request(WeatherAction.CITY_BEIJING);
    }
```
如果你想查看与该次请求更多的细节可以在日志中以该Action的类名过滤，说明如下：

![Action日志输出](https://raw.githubusercontent.com/DrkCore/xMate/master/doc/img/action日志输出.png)

该日志输出只在Dev模式启用的情况下才有：
```
Core.getInstance().setDevModeEnable(true)
```
更多的细节可以参阅demo或者源码，如果你熟悉xUtils3框架的话这对你来说应该不难。

## 如何封装数据库
同样的思想，这里推荐大家将一个数据库封装成一个类，与整个数据库相关的逻辑都应该放在其中。
```java
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

```
有了数据库之后就需要封装数据库访问对象：
```java

/**
 * 用于查询的数据库访问对象
 *
 * @author DrkCore
 * @since 2016-09-06
 */
public class FindProvinceDao extends AbsDao<List<Province>>{

    @Override
    public List<Province >access(@NonNull DbManager db) throws Exception {
        //一个类只干一件事情，但是所谓的封装就是这么一回事
        return db.findAll(Province.class);
    }

}
```
如你所见，Dao就是将对DbManager的操作封装起来而已。虽然很多时候可能里面只有一两句话，但所谓的封装就是这么一回事了。

访问数据库的逻辑则如下：
```java

        //获取已经缓存的dao对象，或者用反射通过默认构造方法创建一个
        //FindProvinceDao dao = regionDb.getCachedDaoOrNewInstance(FindProvinceDao.class);

        //如果你不喜欢反射或者没有默认构造函数的话，可以使用下方的逻辑来获取缓存的dao
        FindProvinceDao dao = regionDb.getCachedDao(FindProvinceDao.class);
        if (dao == null) {
            dao = new FindProvinceDao();
        }

        //访问数据库
        ToastUtil.toastShort("使用的dao.hashCode() = " + dao.hashCode());
        regionDb.access(dao, new ProgressDlgFrag().setFragmentManager(this), new OnTaskListenerImpl<List<Province>>() {
            @Override
            public void onSuccess(List<Province> provinces) {
                //刷新数据
                SimpleAdapter<Province> adapter = (SimpleAdapter<Province>) listView.getAdapter();
                adapter.display(provinces);
            }
        });

```

在CoreDb中封装了对Dao的缓存，具体逻辑请查看[源码](https://github.com/DrkCore/xMate/blob/master/xutils/src/main/java/core/mate/db/CoreDb.java)。

由于Db是静态单例的，为了避免内存泄漏不会缓存定义为非静态内部类的dao实例。同时当且仅当Dao的access(DbManager)方法操作完毕后才会将之缓存起来，外部逻辑获取到缓存后就会将之从中删除，因而无需担心缓存的dao参数冲突的问题。

为了简化数据库的操作本库一并提供了很多常用的dao的抽象基类，都在[core.mate.db.dao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao)包下：

  * [AbsDeleteDao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao/AbsDeleteDao.java)
  * [AbsFindByIdDao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao/AbsFindByIdDao.java)
  * [AbsFindDao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao/AbsFindDao.java)
  * [AbsSaveDao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao/AbsSaveDao.java)
  * [AbsSaveOrUpdateDao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao/AbsSaveOrUpdateDao.java)
  * [ExecNonQuerySqlDao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao/ExecNonQuerySqlDao.java)
  * [FindDbModelDao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao/FindDbModelDao.java)
  * [FindFirstDbModelDao](https://github.com/DrkCore/xMate/tree/master/xutils/src/main/java/core/mate/db/dao/FindFirstDbModelDao.java)



## 该库与原版的xUtils的差异
该库虽然基于xUtils3开发但修改了部分源代码，主要如下：

### 视图注入模块
  * 允许@Event注解绑定到public的方法
  * 允许@Event注解绑定到无参数的方法
  * 添加能将多种事件绑定到无参数方法的注解@MultiEvent（这个其实用处不大）

关于该模块的逻辑和修改可以看看我的博客：

[Android：xUtils3拆解笔记——视图模块详解及拓展](http://blog.csdn.net/drkcore/article/details/50922448)

### 数据库模块
    * 开放DbManager.createTableInFotExists()方法
    * 添加DbManager.findModelAll()方法

数据库的逻辑其实还是比较简单的，参阅我的博客：

[Android：xUtils3拆解笔记——数据库模块解析](http://blog.csdn.net/drkcore/article/details/51866495)

## 联系作者
QQ：178456643

邮箱：178456643@qq.com
