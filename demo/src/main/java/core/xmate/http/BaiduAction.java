package core.xmate.http;

import org.xutils.http.RequestParams;

import core.mate.async.Clearable;
import core.mate.http.ApiAction;

/**
 * @author DrkCore
 * @since 2016-10-09
 */
public class BaiduAction extends ApiAction<String>{

    public Clearable request(){
        return requestGet("https://www.baidu.com/");
    }

    public String requestSync() throws Throwable {
        return requestGetSync(new RequestParams("https://www.baidu.com/"));
    }

}
