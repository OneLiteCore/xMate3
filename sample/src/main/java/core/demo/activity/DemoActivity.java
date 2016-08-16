package core.demo.activity;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
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

    @Event(R.id.button_demo_requestWeather)
    private void requestWeather() {
        WeatherAction action = new WeatherAction();
        action.setCacheEnable();
        action.setOnActionListener(new OnActionListenerImpl<WeatherAction.Weather>() {

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
        action.request(WeatherAction.CITY_BEIJING);
    }


    private DownAarAction action;

    @Event(R.id.button_demo_down)
    private void startDownload() {
        if (action == null) {
            action = new DownAarAction();
            action.setConflictOperation(CoreAction.ConflictOperation.ABANDON_CURRENT_REQUEST);
            action.setOnActionListener(new OnActionListenerImpl<File>() {

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
        action.start();
    }
}
