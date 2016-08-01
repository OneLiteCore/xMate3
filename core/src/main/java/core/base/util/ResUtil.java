package core.base.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import core.base.Core;
import core.base.text.TextBuilder;

/**
 * 用于获取应用信息的工具类
 *
 * @since 2014年11月26日17:36:38
 */
public final class ResUtil {

    private ResUtil() {
    }

	/*版本*/

    /**
     * 获取app的版本名
     *
     * @return 成功则返回应用的版本名。失败，则返回一个空的字符串。
     */
    public static String getAppVerName() {
        String verName = "";
        try {
            Context context = Core.getInstance().getAppContext();
            PackageManager pkgMgr = context.getPackageManager();
            PackageInfo pkgInfo = pkgMgr.getPackageInfo(context.getPackageName(), 0);
            verName = pkgInfo.versionName;
        } catch (NameNotFoundException e) {
            LogUtil.e(e);
        }
        return verName;
    }

    /**
     * 获取app的版本号
     *
     * @return 成功则返回应用的版本号。失败，则-1
     */
    public static int getAppVerCode() {
        int verCode = -1;
        try {
            Context context = Core.getInstance().getAppContext();
            PackageManager pkgMgr = context.getPackageManager();
            PackageInfo pkgInfo = pkgMgr.getPackageInfo(context.getPackageName(), 0);
            verCode = pkgInfo.versionCode;
        } catch (NameNotFoundException e) {
            LogUtil.e(e);
        }

        return verCode;
    }

	/* 资源获取 */

    public static Resources getResources() {
        return Core.getInstance().getAppContext().getResources();
    }

    public static AssetManager getAssets() {
        return Core.getInstance().getAppContext().getAssets();
    }

    public static String getString(@StringRes int strId) {
        return Core.getInstance().getAppContext().getString(strId);
    }

    public static int getColor(@ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Core.getInstance().getAppContext().getColor(colorId);
        } else {
            return getResources().getColor(colorId);
        }
    }

    public static Drawable getDrawable(@DrawableRes int drawableId) {
        Context context = Core.getInstance().getAppContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(drawableId);
        } else {
            return context.getResources().getDrawable(drawableId);
        }
    }

    public static float getDimension(@DimenRes int dimenId) {
        Context context = Core.getInstance().getAppContext();
        return context.getResources().getDimension(dimenId);
    }

    public int getDimensionPixelOffset(@DimenRes int id) {
        Context context = Core.getInstance().getAppContext();
        return context.getResources().getDimensionPixelOffset(id);
    }

    public int getDimensionPixelSize(@DimenRes int id) {
        Context context = Core.getInstance().getAppContext();
        return context.getResources().getDimensionPixelSize(id);
    }


	/*Attr*/

    public static int[] getStyledResourceIds(int... attrIds) {
        return getStyledResourceIds(Core.getInstance().getAppContext(), attrIds);
    }

    public static int[] getStyledResourceIds(Context context, int... attrIds) {
        Resources.Theme theme = context.getTheme();
        TypedArray arr = theme.obtainStyledAttributes(attrIds);
        int count = arr.getIndexCount();
        int[] resIds = new int[count];
        for (int i = 0; i < count; i++) {
            resIds[i] = arr.getResourceId(i, -1);
        }
        arr.recycle();
        return resIds;
    }

    public static Drawable[] getStyledDrawables(int... attrIds) {
        return getStyledDrawables(Core.getInstance().getAppContext(), attrIds);
    }

    public static Drawable[] getStyledDrawables(Context context, int... attrIds) {
        Resources.Theme theme = context.getTheme();
        TypedArray arr = theme.obtainStyledAttributes(attrIds);
        int count = arr.getIndexCount();
        Drawable[] drawables = new Drawable[count];
        for (int i = 0; i < count; i++) {
            drawables[i] = arr.getDrawable(i);
        }
        arr.recycle();
        return drawables;
    }

    public static int[] getStyledColors(int... attrIds) {
        return getStyledColors(Core.getInstance().getAppContext(), attrIds);
    }

    public static int[] getStyledColors(Context context, int... attrIds) {
        Resources.Theme theme = context.getTheme();
        TypedArray arr = theme.obtainStyledAttributes(attrIds);
        int count = arr.getIndexCount();
        int[] drawables = new int[count];
        for (int i = 0; i < count; i++) {
            drawables[i] = arr.getColor(i, -1);
        }
        arr.recycle();
        return drawables;
    }

    public static float[] getStyledDimensions(int... attrIds) {
        return getStyledDimensions(Core.getInstance().getAppContext(), attrIds);
    }

    public static float[] getStyledDimensions(Context context, int... attrIds) {
        Resources.Theme theme = context.getTheme();
        TypedArray arr = theme.obtainStyledAttributes(attrIds);
        int count = arr.getIndexCount();
        float[] drawables = new float[count];
        for (int i = 0; i < count; i++) {
            drawables[i] = arr.getDimension(i, -1);
        }
        arr.recycle();
        return drawables;
    }

    public static float[] getStyledDimensionPixelSizes(int... attrIds) {
        return getStyledDimensionPixelSizes(Core.getInstance().getAppContext(), attrIds);
    }

    public static float[] getStyledDimensionPixelSizes(Context context, int... attrIds) {
        Resources.Theme theme = context.getTheme();
        TypedArray arr = theme.obtainStyledAttributes(attrIds);
        int count = arr.getIndexCount();
        float[] drawables = new float[count];
        for (int i = 0; i < count; i++) {
            drawables[i] = arr.getDimensionPixelSize(i, -1);
        }
        arr.recycle();
        return drawables;
    }

    public static Animation getAnim(@AnimRes int animId) {
        Context context = Core.getInstance().getAppContext();
        return AnimationUtils.loadAnimation(context, animId);
    }

	/*Assets*/

    public static String readAsset(String asset) throws IOException {
        return readAsset(asset, Charset.defaultCharset());
    }

    public static String readAsset(String asset, Charset charset) throws IOException {
        return IOUtil.readString(getAssets().open(asset), charset);
    }

    /**
     * 具体实现请参阅{@link #exportAssetDir(String, File, boolean)}，默认覆盖重名的文件。
     *
     * @param assetDir
     * @param dstDir
     * @return
     * @throws IOException
     */
    public static File exportAssetDir(String assetDir, File dstDir) throws IOException {
        return exportAssetDir(assetDir, dstDir, true);
    }

    /**
     * 从工程的Assets目录导出assetsDir指定的目录到dstDir之下，比如你在assets中放置了一个目录myDir，
     * 那么导出hour你就会得到dstDir/myDir。
     * <b>注意，由于Android本身不提供判断assets目录下的项目是文件还是目录所以这里使用的判定的规则是
     * {@link AssetManager#list(String)}返回的数组长度不为0则为目录，否则即是文件。
     * 因此assetsDir目录和其子目录不允许为空！</b>
     *
     * @param assetDir
     * @param dstDir
     * @param cover
     * @return 导出后的assetDir对应的目录
     * @throws IOException
     */
    public static File exportAssetDir(String assetDir, File dstDir, boolean cover) throws IOException {
        AssetManager assetMgr = ResUtil.getAssets();
        String[] assets = assetMgr.list(assetDir);
        if (assets != null && assets.length > 0) {// assetsDir是目录且里面有东西
            // 在dstDir目录下创建assetDir目录
            File dstAssetDir = FileUtil.getOrCreateDir(new File(dstDir, assetDir));

            TextBuilder textBuilder = new TextBuilder(3 * assetDir.length() + 1);
            String[] itemAssets;
            for (String item : assets) {//这里的item已经带有父目录的地址了，如"docs/home.html"

                //检查assert中的子项目是目录还是文件
                itemAssets = assetMgr.list(textBuilder.buildString(assetDir, '/', item));
                if (itemAssets != null) {
                    if (itemAssets.length > 0) {// 子项目是目录
                        exportAssetDir(item, dstAssetDir, cover);// 递归
                    } else {// 文件
                        exportAssetFile(item, dstAssetDir, cover);
                    }
                }
            }
            //一切顺利，返回导出后的assetDir对应的目录
            return dstAssetDir;
        } else {// 为空直接抛出异常
            throw new IllegalStateException(assetDir + "为空！");
        }
    }

    /**
     * 具体实现请参阅{@link #exportAssetFile(String, File, boolean)}，默认会覆盖重名的文件。
     *
     * @param asset
     * @param dstDir
     * @return
     * @throws IOException
     */
    public static File exportAssetFile(String asset, File dstDir) throws IOException {
        return exportAssetFile(asset, dstDir, true);
    }

    /**
     * 从应用Assets目录导出文件到dstDir下，如果你要导出myFile.txt会返回导出的dstDir/myFile.txt文件。
     * 如果asset的路径类似"docs/home.html"则会得到dstDir/home.html，过滤父路径的逻辑请参阅{@link FileUtil#getItemName(String)}。
     *
     * @param asset  资源名
     * @param dstDir 目标文件
     * @param cover  是否覆盖重名文件。如果重名的是目录则抛出异常。
     * @return 成功导出后的文件，如果指定文件存在且cover为false则直接返回dst。
     * @throws IOException 写入操作或者dst指向一个目录都将抛出该异常。
     */
    public static File exportAssetFile(String asset, File dstDir, boolean cover) throws IOException {
        dstDir = FileUtil.getOrCreateDir(dstDir);//确认目录

        //计算文件名，如从"docs/home.html"中取出"home.html"
        String itemName = FileUtil.getItemName(asset);
        File dstFile = new File(dstDir, itemName);
        if (dstFile.isDirectory()) {
            throw new IOException("指定位置" + dstFile + "被目录占据，无法写入");
        }
        if (dstFile.isFile() && !cover) {//文件已存在但是不覆盖，直接返回已存在的那个文件
            return dstFile;
        }

        AssetManager assetMgr = ResUtil.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetMgr.open(asset);
            out = new FileOutputStream(dstFile);
            IOUtil.writeData(in, out);
            return dstFile;
        } finally {
            IOUtil.close(in);
            IOUtil.close(out);
        }
    }

}
