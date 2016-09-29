package core.xmate.http;


import java.io.File;

import core.mate.Core;
import core.mate.async.Clearable;
import core.mate.http.DownloadAction;
import core.mate.util.ResUtil;
import core.xmate.R;

public class DownAarAction extends DownloadAction {

    public Clearable start(){
        String url = ResUtil.getString(R.string.test_download_url);
        File dir = Core.getInstance().getAppContext().getFilesDir();
        File aar = new File(dir,"testDown.aar");
        return download(url, aar );
    }

}
