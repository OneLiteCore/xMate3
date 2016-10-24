package core.mate.http;

import org.xutils.http.RequestParams;

import java.io.File;
import java.lang.reflect.Type;

import core.mate.async.Clearable;

public class DownloadAction extends CoreAction<File, File> {

    /*继承*/

    @Override
    protected Type getLoadType() {
        return File.class;
    }

    @Override
    protected Type getResultType() {
        return File.class;
    }

    @Override
    protected final File onPrepareResult(File rawData) throws IllegalDataException {
        return rawData;
    }

    /*拓展*/

    public final Clearable download(String url, File file) {
        return download(url, file.getAbsolutePath());
    }

    public final Clearable download(String url, String file) {
        RequestParams params = new RequestParams(url);
        params.setSaveFilePath(file);
        params.setCancelFast(true);
        params.setAutoResume(true);
        params.setAutoRename(true);
        return requestGet(params);
    }

    public final File downloadSync(String url, File file) throws Throwable {
        return downloadSync(url, file.getAbsolutePath());
    }

    public final File downloadSync(String url, String file) throws Throwable {
        RequestParams params = new RequestParams(url);
        params.setSaveFilePath(file);
        params.setCancelFast(true);
        params.setAutoResume(true);
        params.setAutoRename(true);
        return requestGetSync(params);
    }
}
