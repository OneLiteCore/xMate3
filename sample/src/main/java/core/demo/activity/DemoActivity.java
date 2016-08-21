package core.demo.activity;

import com.alibaba.fastjson.JSON;

import org.xutils.sample.R;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import java.io.File;

import core.demo.activity.base.BaseActivity;
import core.demo.http.DownAarAction;
import core.demo.http.WeatherAction;
import core.mate.http.CoreAction;
import core.mate.http.OnActionListenerImpl;
import core.mate.util.LogUtil;
import core.mate.util.ToastUtil;

@ContentView(R.layout.activity_demo)
public class DemoActivity extends BaseActivity {

    private WeatherAction weatherAction;

    @Event(R.id.button_demo_requestWeather)
    private void requestWeather() {
        if (weatherAction == null) {
            weatherAction = new WeatherAction();
            weatherAction.setCacheEnable();
            weatherAction.setConflictOperation(CoreAction.ConflictOperation.ABANDON_NEW_REQUEST);
            weatherAction.addOnActionListener(new OnActionListenerImpl<WeatherAction.Weather>() {

                @Override
                public boolean onCache(WeatherAction.Weather weather) {
                    LogUtil.d("或得到的缓存为： " + JSON.toJSONString(weather));
                    return true;//super.onCache(weather);
                }

                @Override
                public void onSuccess(WeatherAction.Weather weather) {
                    ToastUtil.toastShort("请求成功，具体内容请以Action类名查看Log");
                }
            });
        }
        weatherAction.request(WeatherAction.CITY_BEIJING);
    }


    private DownAarAction downAarAction;

    @Event(R.id.button_demo_down)
    private void startDownload() {
        if (downAarAction == null) {
            downAarAction = new DownAarAction();
            downAarAction.setConflictOperation(CoreAction.ConflictOperation.ABANDON_NEW_REQUEST);
            downAarAction.addOnActionListener(new OnActionListenerImpl<File>() {

                @Override
                public void onStarted() {
                    super.onStarted();
                    ToastUtil.toastShort("开始下载");
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    super.onLoading(total, current, isDownloading);
                }

                @Override
                public void onSuccess(File file) {

                }
            });
        }
        downAarAction.start();
    }
}
