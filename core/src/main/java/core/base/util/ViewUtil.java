package core.base.util;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import core.base.Core;

/**
 * 封装了对控件的一些操作的工具类
 *
 * @author DrkCore
 * @since 2015年11月13日18:47:08
 */
public final class ViewUtil {

    private ViewUtil() {
    }

	/*视图参数*/

    private static float density;
    private static float scaledDensity;
    private static int widthPixels;
    private static int heightPixels;

    static {// 初始化
        DisplayMetrics displayMetrics = ResUtil.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        scaledDensity = displayMetrics.scaledDensity;
        widthPixels = displayMetrics.widthPixels;
        heightPixels = displayMetrics.heightPixels;
    }

    public static float getDisplayMetricsDensity() {
        return density;
    }

    public static float getDisplayMetricsScaledDensity() {
        return scaledDensity;
    }

    public static int getScreenWidthPx() {
        return widthPixels;
    }

    public static int getScreenHeightPx() {
        return heightPixels;
    }


	/* 单位转换 */

    public static int dpToPx(float dpValue) {
        return (int) (dpValue * getDisplayMetricsDensity() + 0.5F);
    }

    public static int pxToDp(float pxValue) {
        return (int) (pxValue / getDisplayMetricsDensity() + 0.5F);
    }

    public static int pxToSp(float pxValue) {
        return (int) (pxValue / getDisplayMetricsScaledDensity() + 0.5f);
    }

    public static int spToPx(float spValue) {
        return (int) (spValue * getDisplayMetricsScaledDensity() + 0.5f);
    }

	/* 动画/可见性 */

    /**
     * 切换布局的可见性。当布局的可见性为{@link View#VISIBLE}时切换到{@link View#GONE}，
     * 当可见性为{@link View#GONE}或者{@link View#INVISIBLE}时，切换到{@link View#VISIBLE}
     *
     * @param view
     */
    public static void toggleViewVisible(View view) {
        int nextVisibility = view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        view.setVisibility(nextVisibility);
    }

    /**
     * 显示控件后播放动画。如果animRes小于或者等于0则表示不使用动画。
     * 如果view已经处于{@link View#VISIBLE}状态则不做任何处理。
     *
     * @param view
     * @param animRes
     */
    public static void showWithAnim(View view, int animRes) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            if (animRes > 0) {
                Animation anim = AnimationUtils.loadAnimation(Core.getInstance().getAppContext(), animRes);
                view.startAnimation(anim);
            }
        }
    }

    /**
     * 播放动画后隐藏控件。如果animRes小于或者等于0则表示不使用动画。
     * 如果view已经处于{@link View#GONE}状态则不做任何处理。
     *
     * @param view
     * @param animRes
     */
    public static void hideWithAnim(View view, int animRes) {
        if (view.getVisibility() != View.GONE) {
            if (animRes > 0) {
                Animation anim = AnimationUtils.loadAnimation(Core.getInstance().getAppContext(), animRes);
                view.startAnimation(anim);
            }
            view.setVisibility(View.GONE);
        }
    }

	/* 控件尺寸 */

    public static int getViewHeight(View v) {
        // 获取高度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredHeight();
    }

    public static int getViewWidth(View v) {
        // 获取高度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredWidth();
    }

    /**
     * 动态调整ListView的高度。当你将ListView嵌入ScrollView的时候可能需要用到该方法。
     *
     * @param listView
     */
    public static void adjustListViewHeight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

	/*获取属性*/

    /**
     * 获取ListView的滚动的Y。该方法只适用于Item的高度都一致的情况
     *
     * @param listView
     * @return
     */
    public static int getListViewScrollY(AbsListView listView) {
        //这里获得并不是Adapter中第0个item而是ListView可见范围内的第0个item
        View lastVisibleItem = listView.getChildAt(0);
        if (lastVisibleItem == null) {
            return 0;
        }
        //初始状态下第0个和firstVisible一样的
        //但是滑动到下面之后第0个其实是firstVisible的上一个item
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int top = lastVisibleItem.getTop();
        //综合计算一下就可以得出当前Y，不过只限于Item的高度都是一样的情况下
        int scrollY = -top + firstVisiblePosition * lastVisibleItem.getHeight();

        if (listView instanceof ListView) {
            ListView view = (ListView) listView;
            int dividerHeight = view.getDividerHeight();
            scrollY += dividerHeight * firstVisiblePosition;
        }
        return scrollY;
    }

	/* View截图 */

    /**
     * 获取视图的截图。
     *
     * @param view
     * @return
     */
    public static Bitmap getDrawingCache(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //因为原图可能会被回收，这里创建一个拷贝
        Bitmap img = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return img;
    }

}
