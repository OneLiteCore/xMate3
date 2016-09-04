package core.xmate.model;

import core.xmate.http.WeatherAction;

/**
 * @author DrkCore
 * @since 2016-09-04
 */
public class Weather {

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
