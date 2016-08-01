package core.base.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.DrawableRes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import core.base.Core;

/**
 * 本类是用来对图片进行操作的静态工具类
 *
 * @author DrkCore
 * @since 2015年7月30日00:11:52
 */
public class BitmapUtil {

    private BitmapUtil() {
    }

	/*获取图片数据*/

    /**
     * 获取byte数据中的图片的尺寸数据。该方法不会真正地解析图片。
     * 解析之后返回的{@link android.graphics.BitmapFactory.Options#inJustDecodeBounds}将被设置为false。
     *
     * @param data
     * @return
     */
    public static BitmapFactory.Options getImgOption(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inJustDecodeBounds = false;
        return options;
    }

    /**
     * 获取imgFile文件中图片的尺寸数据。该方法不会真正解析图片，具体实现请参阅{@link #getImgOption(String)}。
     *
     * @param imgFile
     * @return
     */
    public static BitmapFactory.Options getImgOption(File imgFile) {
        return getImgOption(imgFile.getAbsolutePath());
    }

    /**
     * 获取imgPath指定图片文件中的尺寸数据。该方法不会真正解析图片。
     * 解析之后返回的{@link android.graphics.BitmapFactory.Options#inJustDecodeBounds}将被设置为false。
     *
     * @param imgPath
     * @return
     */
    public static BitmapFactory.Options getImgOption(String imgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        options.inJustDecodeBounds = false;
        return options;
    }

    /**
     * 获取resId指定的图片资源中的尺寸数据。该方法不会真正解析图片。
     * 解析之后返回的{@link android.graphics.BitmapFactory.Options#inJustDecodeBounds}将被设置为false。
     *
     * @param resId
     * @return
     */
    public static BitmapFactory.Options getImgOption(@DrawableRes int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(Core.getInstance().getAppContext().getResources(), resId, options);
        options.inJustDecodeBounds = false;
        return options;
    }

    /**
     * 默认将bitmap按照100的质量转化成数组，具体实现请参阅{@link #toBytes(Bitmap, int)}。
     *
     * @param bitmap
     * @return
     */
    public static byte[] toBytes(Bitmap bitmap) {
        return toBytes(bitmap, 100);
    }

    /**
     * 将Bitmap按照指定的质量转化为byte数组。
     *
     * @param bitmap
     * @param quality 0~100以内，否者将抛出异常
     * @return
     */
    public static byte[] toBytes(Bitmap bitmap, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 将Bitmap压缩成PNG编码，质量为100%存储
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, os);// 除了PNG还有很多常见格式，如jpeg等。
        return os.toByteArray();
    }

	/* 生成图片 */

    public static Bitmap decodeImg(Bitmap bitmap, int reqWidth, int reqHeight) {
        return decodeImg(toBytes(bitmap), reqWidth, reqHeight);
    }

    public static Bitmap decodeImg(Bitmap bitmap, int quality, int reqWidth, int reqHeight) {
        return decodeImg(toBytes(bitmap, quality), reqWidth, reqHeight);
    }

    public static Bitmap decodeImg(byte[] data) {
        return decodeImg(data, 0, 0);
    }

    /**
     * 将指定byte数组中的数据缩放为指定大小的图片。关于缩放请参阅{@link #calculateInSampleSize(BitmapFactory.Options, int, int)}。
     * 其中reqWidth和reqHeight任何一个小于等于0则表示不压缩。
     *
     * @param data
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeImg(byte[] data, int reqWidth, int reqHeight) {
        if (reqWidth > 0 && reqHeight > 0) {
            BitmapFactory.Options options = getImgOption(data);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } else {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
    }

    public static Bitmap decodeImg(File imgFile) {
        return decodeImg(imgFile, 0, 0);
    }

    /**
     * 将指定文件中的数据缩放为指定大小的图片，具体实现请参阅{@link #decodeImg(String, int, int)}。
     * 其中reqWidth和reqHeight任何一个小于等于0则表示不压缩。
     *
     * @param imgFile
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeImg(File imgFile, int reqWidth, int reqHeight) {
        return decodeImg(imgFile.getAbsolutePath(), reqWidth, reqHeight);
    }

    public static Bitmap decodeImg(String imgPath) {
        return decodeImg(imgPath, 0, 0);
    }

    /**
     * 将指定路径中的文件的数据缩放为指定大小的图片。关于缩放请参阅{@link #calculateInSampleSize(BitmapFactory.Options, int, int)}。
     * 其中reqWidth和reqHeight任何一个小于等于0则表示不压缩。
     *
     * @param imgPath
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeImg(String imgPath, int reqWidth, int reqHeight) {
        if (reqWidth > 0 && reqHeight > 0) {
            BitmapFactory.Options options = getImgOption(imgPath);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(imgPath, options);
        } else {
            return BitmapFactory.decodeFile(imgPath);
        }
    }

    public static Bitmap decodeImg(@DrawableRes int resId) {
        return decodeImg(resId, 0, 0);
    }

    /**
     * 将res指定的资源文件解析成指定大小的bitmap。关于缩放请参阅{@link #calculateInSampleSize(BitmapFactory.Options, int, int)}。
     * 其中reqWidth和reqHeight任何一个小于等于0则表示不压缩。
     *
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeImg(@DrawableRes int resId, int reqWidth, int reqHeight) {
        if (reqWidth > 0 && reqHeight > 0) {
            BitmapFactory.Options options = getImgOption(resId);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(Core.getInstance().getAppContext().getResources(), resId, options);
        } else {
            return BitmapFactory.decodeResource(Core.getInstance().getAppContext().getResources(), resId);
        }
    }

    /**
     * 按照设计图的尺寸解析图片。先通过{@link #getImgOption(int)}等方法获取图片的宽度和宽高比，
     * 通过宽度和设计图尺寸的比例计算出最终用于解析的图片大小，关于解析时的大小你可能还需要参阅{@link #calculateInSampleSize(BitmapFactory.Options, int, int)}。
     *
     * @param resId
     * @param designedScreenWidth 设计图对应的屏幕大小
     * @return
     */
    private static Bitmap decodeImgInDesignedSize(@DrawableRes int resId, int designedScreenWidth) {
        if (designedScreenWidth <= 0) {
            throw new IllegalArgumentException("designedScreenWidth的值" + designedScreenWidth + "不允许小于等于0");
        }

        BitmapFactory.Options options = getImgOption(resId);
        float srcWidthVsHeight = (float) options.outWidth / (float) options.outHeight;//原图宽高比
        int imgWidthPx = Math.round((float) options.outWidth / (float) designedScreenWidth * ViewUtil.getScreenWidthPx());
        int imgHeightPx = Math.round(imgWidthPx / srcWidthVsHeight);
        return BitmapUtil.decodeImg(resId, imgWidthPx, imgHeightPx);
    }

	/* 计算尺寸 */

    /**
     * 在保存原图片的宽高比的基础下计算将图片转化为要求的尺寸之内的缩小倍率，
     * 如果inSampleSize为2表示使用1个像素来表示原图上的2个像素的，也就是缩小成原来的1/2倍的意思。
     * 因而解析出来的图片的长宽比是保持不变的。如果要求尺寸比原始都大的话，则默认返回1，表示不缩小。 得出的比例取进一法。
     * <br/>
     * 需要注意的是，通过该该方法计算出的图片很可能其尺寸不是reqWidth和reqHeight所要求的大小。
     * <br/>
     * <br/>更多相关请参阅<a href=http://blog.csdn.net/xu_fu/article/details/8262153>这里</a>。
     *
     * @param options   已经decode过的options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        int inSampleSize = 1;
        //inSampleSize只能让你缩小

        if (srcHeight > reqHeight || srcWidth > reqWidth) {//原图有部分超过了需求的尺寸
            float srcWidthVsHeight = (float) srcWidth / (float) srcHeight;//原图宽高比
            float reqWidthVsHeight = (float) reqWidth / (float) reqHeight;//需求图宽高比

            //宽高比 = 1，正方形；宽高比 > 1，躺着的长方形；宽高比 < 1，站着的长方形
            if (srcWidthVsHeight > reqWidthVsHeight) {//原图躺的比较厉害
                inSampleSize = (int) Math.ceil((float) srcWidth / (float) reqWidth);//将原图的宽度塞进需求图就可以了
            } else {//原图站起来了
                inSampleSize = (int) Math.ceil((float) srcHeight / (float) reqHeight);//将原图的高度塞进需求图
            }
        }
        return inSampleSize > 1 ? inSampleSize : 1;
    }

    /**
     * 计算bitmap位图所占的内存大小，适应不同api
     *
     * @param bitmap
     * @return
     */
    @SuppressLint("NewApi")
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();// API 19
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();// API 12
        }
        return bitmap.getRowBytes() * bitmap.getHeight();// 更古老的api
    }
}
