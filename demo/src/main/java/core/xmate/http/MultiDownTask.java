package core.xmate.http;

import org.xutils.common.Callback;
import org.xutils.common.TaskController;
import org.xutils.common.task.AbsTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.mate.async.CoreTask;
import core.mate.content.TextBuilder;
import core.mate.http.CoreAction;
import core.mate.http.DownloadAction;
import core.mate.http.OnActionListenerImpl;
import core.mate.util.DataUtil;
import core.mate.util.FileUtil;

/**
 * 单个异步任务处理多线程下载文件。
 * <p>
 * 该做法只是笔者想到的封装的一种方式，并不一定是最清真的做法。
 * 通过xUtils3提供的
 * {@link TaskController#startTasks(Callback.GroupCallback, AbsTask[])}
 * 方法也是可以实现类似的功能，并且可能比笔者的这种做法更好一些。
 * 有兴趣的同学可以自行了解。
 * <p>
 * 如果你觉得笔者的这种做法有所缺陷，请务必联系笔者：
 * 178456643@qq.com
 *
 * @author DrkCore
 * @since 2016-10-21
 */
public class MultiDownTask extends CoreTask<MultiDownTask.Params, Void, List<File>> {

    public static class Params {

        public final String[] urls;
        public final File dir;

        public Params(String[] urls, File dir) {
            this.urls = urls;
            this.dir = dir;
        }
    }

    @Override
    public List<File> doInBack(Params params) throws Exception {
        final int len = DataUtil.getSize(params.urls);
        if (len == 0 || params.dir == null || params.dir.isFile()) {
            throw new IllegalArgumentException();
        }

        DownloadAction action = new DownloadAction();

        if (len == 1) {//要下载的文件只有一个，直接同步下载即可
            String url = params.urls[0];
            try {
                File file = action.downloadSync(url, getSavePath(params.dir, url));
                return Collections.singletonList(file);
            } catch (Throwable throwable) {//重抛出异常
                throw new Exception(throwable);
            }
        }

        //count用于同步当前线程和回调，其中唯一的元素表示剩余回调数量
        //当其为0时表示所有异步都已经回调结束
        final int[] count = {len};
        final List<File> files = new ArrayList<>(len);
        CoreAction.OnActionListener<File> listener = new OnActionListenerImpl<File>() {
            @Override
            public void onSuccess(File file) {
                //保存所有下载成功的结果
                files.add(file);
            }

            @Override
            public void onFinished() {
                super.onFinished();
                synchronized (count) {
                    count[0]--;
                    if (count[0] == 0) {
                        //所有回调结束，唤醒被阻塞的我们的异步任务线程
                        count.notify();
                    }
                }
            }
        };

        //配置下载任务
        action.addOnActionListener(listener);
        for (String url : params.urls) {
            //发起异步下载请求，xUtils内部会维护下载的多个线程
            action.download(url, getSavePath(params.dir, url));
        }

        //阻塞当前线程10秒，用于测试在wait之前异步回调都已结束的情况
        //Thread.sleep(10000L);

        //阻塞等待所有下载结束
        synchronized (count) {
            if (count[0] > 0) {//只在还有回调剩余的情况阻塞等待
                count.wait();
            }
        }

        return files;
    }

    private TextBuilder builder = new TextBuilder().setEmptyAsNullEnable(true);

    private String getSavePath(File dir, String url) {
        String ext = FileUtil.getFileExtName(url);
        String name = FileUtil.getAbsoluteFileName(url);
        return builder.clear().append(dir).append(File.separator)
                .append(name).append(".").append(ext).removeEnd(".").toString();
    }

}
