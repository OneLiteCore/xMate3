package core.base.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import core.base.R;
import core.base.util.ViewUtil;

/**
 * 面板对话框。你可以通过{@link #setDialogHeightDp(int)}的方法来动态设置对话框的高度，
 * 否则将默认使用{@link LayoutParams#WRAP_CONTENT}作为高度。
 * 这将导致的结果就是所加载的布局的高度全部转为wrap_content，
 * 尽管其layout_height属性可能是match_parent或者是某个具体的dp数值。
 *
 * @author DrkCore
 * @since 2015年10月27日00:23:17
 */
public class PanelDlgFrag extends CoreDlgFrag {

	/* 对话框配置 */

    private Integer dlgHeightDp;
    private Float dlgHeightPercent;

    /**
     * 设置对话框的高度，单位为dp。
     * 该方法只在{@link #onActivityCreated(Bundle)}之前调用有效。
     * 除了具体的dp数值以外，你还可以使用如下参数：
     * <li/>{@link LayoutParams#WRAP_CONTENT}
     * ，表示将根布局的高度强制设为wrap_content；
     * <li/>{@link LayoutParams#MATCH_PARENT}
     * ，表示将根布局的高度强制设为填满屏幕；
     * <li/>数值0，表示和{@link LayoutParams#WRAP_CONTENT}和相同；
     * <br/>
     * <br/>
     * 如果通过{@link #setDialogHeightPercent(float)}设置了百分比则此方法无效。
     *
     * @param dp
     * @throws IllegalArgumentException 当dp值小于-2时抛出该异常。
     */
    protected final PanelDlgFrag setDialogHeightDp(int dp) {
        if (dp < -2) {
            throw new IllegalArgumentException("dp 不允许小于-2");
        }
        this.dlgHeightDp = dp;
        return this;
    }

    /**
     * 设置对话框的高度相对于设备高度的百分比。该方法的优先级高于{@link #setDialogHeightDp(int)}的效果。
     *
     * @param percent [0,1]
     * @return
     * @throws IllegalArgumentException 当percent大于1或者小于0时抛出该异常
     */
    protected final PanelDlgFrag setDialogHeightPercent(float percent) {
        if (percent < 0 || percent > 1) {
            throw new IllegalArgumentException("percent " + percent + " 不合法");
        }
        this.dlgHeightPercent = percent;
        return this;
    }

	/* 继承 */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPrepareDialogStyle(savedInstanceState);
    }

    @Override
    @NonNull
    public final Dialog onCreateDialog(Bundle savedInstanceState) {// 使用final修饰阻止重写该方法
        return super.onCreateDialog(savedInstanceState);
    }

    /**
     * 配置DialogFragment的样式，在{@link #onCreate(Bundle)}中回调。
     * 默认将会设置样式为{@link #STYLE_NO_FRAME}，并且style为
     * {@link core.base.R.style#PanelDlgStyle}
     *
     * @param savedInstanceState
     */
    @Override
    protected void onPrepareDialogStyle(@Nullable Bundle savedInstanceState) {
        super.onPrepareDialogStyle(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.PanelDlgStyle);
        setWinAnimStyle(R.style.CoreWindowAnimSlideTopStyle);
    }

    /**
     * 配置对话框的window。默认情况下将会设置对话框的重力为{@link Gravity#BOTTOM}，宽度填满整个屏幕，
     * 如果并未{@link #setDialogHeightDp(int)}方法设置了高度的话则默认将高度设置为
     * {@link LayoutParams#WRAP_CONTENT}。
     *
     * @param savedInstanceState
     * @param dlg
     * @param dlgWin
     */
    @Override
    protected void onPrepareDialogWindow(@Nullable Bundle savedInstanceState, Dialog dlg, Window dlgWin) {
        super.onPrepareDialogWindow(savedInstanceState, dlg, dlgWin);
        int deviceWidth = ViewUtil.getScreenWidthPx();
        int deviceHeight = ViewUtil.getScreenHeightPx();

        // 判断高度
        int winHeightPx;
        if (dlgHeightPercent != null) {
            winHeightPx = (int) (deviceHeight * dlgHeightPercent);
        } else if (dlgHeightDp != null) {
            if (dlgHeightDp > 0) {
                winHeightPx = ViewUtil.dpToPx(dlgHeightDp);
            } else if (dlgHeightDp == 0) {// 数值0，也就是默认情况，设为wrap_content
                winHeightPx = LayoutParams.WRAP_CONTENT;
            } else if (dlgHeightDp >= -2) {// 0到-2之内的数值表示match_parent或者wrap_content参数
                winHeightPx = dlgHeightDp;
            } else {
                throw new IllegalArgumentException("dlgHeight的参数不合法");
            }
        } else {
            winHeightPx = LayoutParams.WRAP_CONTENT;// 默认为wrap_content
        }

        dlgWin.setLayout(deviceWidth, winHeightPx);
        dlgWin.setGravity(Gravity.BOTTOM);
    }

}
