package core.base.async;

import core.base.util.LogUtil;

/**
 * 用于计时的线程
 *
 * @author DrkCore
 * @since 2016年4月17日22:30:20
 */
public abstract class CountDownThread extends Thread {

    public static final long DEFAULT_TIME_OUT = 2000L;

    private final long timeOut;

    public CountDownThread(long timeOut) {
        this.timeOut = timeOut;
        if (timeOut <= 0) {
            throw new IllegalArgumentException("timeOut必须大于0");
        }
    }

    public void resetTime() {
        lastStartTime = System.currentTimeMillis();
    }

    /*计时*/

    private long lastStartTime;

    private boolean holding = false;

    public void setHolding(boolean holding) {
        this.holding = holding;
    }

    @Override
    public void run() {
        lastStartTime = System.currentTimeMillis();

        //等待时间
        while (holding || System.currentTimeMillis() - lastStartTime <= timeOut) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                LogUtil.e(e);
            }
        }

        onCountDown();
    }

    /*回调*/

    protected abstract void onCountDown();

}