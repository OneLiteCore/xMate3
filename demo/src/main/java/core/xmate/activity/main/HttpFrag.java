package core.xmate.activity.main;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import java.io.File;

import core.mate.app.ProgressDlgFrag;
import core.mate.http.CoreAction;
import core.mate.http.OnActionListenerImpl;
import core.mate.util.ToastUtil;
import core.xmate.R;
import core.xmate.activity.base.BaseFrag;
import core.xmate.http.DownAarAction;
import core.xmate.http.WeatherAction;
import core.xmate.model.Weather;

@ContentView(R.layout.frag_http)
public class HttpFrag extends BaseFrag {

    /*继承*/

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
            weatherAction.addIndicator(new ProgressDlgFrag().setFragmentManager(this));
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

    private DownAarAction downAarAction;

    @Event(R.id.button_frag_http_down)
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
