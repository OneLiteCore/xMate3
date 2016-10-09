package core.xmate.http;

import android.support.annotation.WorkerThread;

import com.alibaba.fastjson.JSON;

import org.xutils.http.RequestParams;

import core.mate.async.Clearable;
import core.mate.http.ApiAction;
import core.mate.http.IllegalDataException;
import core.mate.util.TextUtil;
import core.xmate.model.Weather;

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

    public static final BaiduAction BAIDU_ACTION = new BaiduAction();

    @WorkerThread
    @Override
    protected void onPrepareParams(RequestParams params) {
        super.onPrepareParams(params);
//        try {该方法将在异步线程中执行，你可以在这里同步执行其他的请求
//            String baidu = BAIDU_ACTION.requestSync();
//            logDevMsg("同步获取百度", baidu);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
    }

    /**
     * 提取数据所在的字符串
     *
     * @param dataStr
     * @return
     * @throws IllegalDataException
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
