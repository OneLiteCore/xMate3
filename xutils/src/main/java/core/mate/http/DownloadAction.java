package core.mate.http;

import org.xutils.http.RequestParams;

import java.io.File;
import java.lang.reflect.Type;

import core.mate.async.Clearable;

public abstract class DownloadAction extends CoreAction<File, File> {

    /*继承*/

    @Override
    protected final Type getLoadType() {
        return File.class;
    }

    @Override
    protected final Type getResultType() {
        return File.class;
    }

    @Override
    protected final boolean onCache(File file) {
        return false;
    }

    @Override
    protected final File onPrepareResult(File rawData) throws IllegalDataException {
        return rawData;
    }

    /*拓展*/

    protected Clearable download(String url, File file) {
        return download(url, file.getAbsolutePath());
    }

    protected Clearable download(String url, String file) {
        RequestParams params = new RequestParams(url);
        params.setSaveFilePath(file);
        params.setCancelFast(true);
        return requestGet(params);
    }
}
