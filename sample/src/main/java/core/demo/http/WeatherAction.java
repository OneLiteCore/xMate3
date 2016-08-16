package core.demo.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import core.mate.common.Clearable;
import core.mate.http.ApiAction;
import core.mate.http.IllegalDataException;
import core.mate.util.TextUtil;

public class WeatherAction extends ApiAction<WeatherAction.Weather> {

    public static class Weather {

        private String date;//20131012"
        private String cityName;//北京"
        private String areaID;//101010100"
        private String temp;//21℃"
        private String tempF;//69.8℉"
        private String wd;//东风"
        private String ws;//3级"
        private String sd;//39%"
        private String time;//15:10"
        private String sm;//暂无实况"

        public String getDate() {
            return date;
        }

        public Weather setDate(String date) {
            this.date = date;
            return this;
        }

        public String getCityName() {
            return cityName;
        }

        public Weather setCityName(String cityName) {
            this.cityName = cityName;
            return this;
        }

        public String getAreaID() {
            return areaID;
        }

        public Weather setAreaID(String areaID) {
            this.areaID = areaID;
            return this;
        }

        public String getTemp() {
            return temp;
        }

        public Weather setTemp(String temp) {
            this.temp = temp;
            return this;
        }

        public String getTempF() {
            return tempF;
        }

        public Weather setTempF(String tempF) {
            this.tempF = tempF;
            return this;
        }

        public String getWd() {
            return wd;
        }

        public Weather setWd(String wd) {
            this.wd = wd;
            return this;
        }

        public String getWs() {
            return ws;
        }

        public Weather setWs(String ws) {
            this.ws = ws;
            return this;
        }

        public String getSd() {
            return sd;
        }

        public Weather setSd(String sd) {
            this.sd = sd;
            return this;
        }

        public String getTime() {
            return time;
        }

        public Weather setTime(String time) {
            this.time = time;
            return this;
        }

        public String getSm() {
            return sm;
        }

        public Weather setSm(String sm) {
            this.sm = sm;
            return this;
        }
    }

    public static final String CITY_BEIJING = "101010100";// 北京
    public static final String CITY_HAIDIAN = "101010200";// 海淀
    public static final String CITY_CHAOYANG = "101010300";// 朝阳
    public static final String CITY_SHUNYI = "101010400";// 顺义

    private static final String BASE_URL = "http://mobile.weather.com.cn/data/sk/";

    public Clearable request(String cityId) {
        return requestGet(TextUtil.buildString(BASE_URL, cityId, ".html"));
    }

    @Override
    protected String onPrepareDataString(String dataStr) throws IllegalDataException {
        //服务器返回的字符串如下：
        //{"sk_info":{"date":"20131012","cityName":"北京","areaID":"101010100","temp":"21℃","tempF":"69.8℉","wd":"东风","ws":"3级","sd":"39%","time":"15:10","sm":"暂无实况"}}
        //其中真正的数据在sk_info对应的字段中
        //你可以使用JSON解析取出其中的数据，也可以直接使用String.subString()方法。
        //不过为了开发赶时间，还是解析JSON吧
        return JSON.parseObject(dataStr).getString("sk_info");
    }
}
