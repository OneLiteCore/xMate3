# xMate

## 简介
本框架基于xUtils3进行二次开发，与原版的主要差别如下：

    * 修改视图注入模块，更加自由灵活
    * 封装Http模块，并提供类似postman的详细日志输出
    * 封装数据库模块，添加数据访问对象Dao的封装

本项目fork自xUtils3但不再赘述其相关介绍，如果你还不了解xUtils框架建议先到[原版]([xUtils3](https://github.com/wyouflf/xUtils3))的repo中自行了解。

欢迎大家star~

## 如何集成
Android Studio用户可有在模块的build.gradle中于dependencies下添加如下依赖：

```
compile 'core.mate:core:1.0.3'
compile 'core.mate:xutils:1.0.3'
```

之后在你的Application中进行初始化：

```
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

    @Event(R.id.button_demo_requestWeather)
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



## 联系作者
QQ：178456643
邮箱：178456643@qq.com
