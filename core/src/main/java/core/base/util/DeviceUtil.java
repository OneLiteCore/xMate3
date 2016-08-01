package core.base.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import core.base.Core;

/**
 * 获取设备属性的工具集
 *
 * @author DrkCore
 * @since 2016年2月27日20:47:24
 */
public final class DeviceUtil {

	private DeviceUtil () {}

	/*唯一码*/
	/*参阅博客：http://blog.csdn.net/dai_zhenliang/article/details/8634042*/

	/**
	 * 获取Android系统为开发者提供的用于标识手机设备的串号。
	 * 也是各种方法中普适性较高的，可以说几乎所有的设备都可以返回这个串号，并且唯一性良好。
	 * 需要注意的是：
	 * <p/>
	 * <li/> 非手机设备：最开始搭载Android系统都手机设备，而现在也出现了非手机设备：如平板电脑、电子书、电视、音乐播放器等。
	 * 这些设备没有通话的硬件功能，系统中也就没有TELEPHONY_SERVICE，自然也就无法获得DEVICE_ID。
	 * <li/>权限问题：获取DEVICE_ID需要<b>"android.permission.READ_PHONE_STATE"</b>权限。
	 * <li/> 厂商定制系统中的Bug：少数手机设备上，由于该实现有漏洞，会返回垃圾，如:zeros或者asterisks。
	 *
	 * @return
	 */
	public static String getDeviceId () {
		Context context = Core.getInstance().getAppContext();
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getDeviceId();
	}

	/**
	 * 获取SIM卡的序列号。
	 * <b>注意：对于CDMA设备，返回的是一个空值！</b>
	 *
	 * @return
	 */
	@Nullable
	public static String getSimSerialNumber () {
		Context context = Core.getInstance().getAppContext();
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getSimSerialNumber();
	}

	/**
	 * 获取AndroidId。在设备首次启动时，系统会随机生成一个64位的数字，并把这个数字以16进制字符串的形式保存下来，
	 * 这个16进制的字符串就是ANDROID_ID，当设备被wipe后该值会被重置。
	 * 需要注意的是：
	 * <p/>
	 * <li/> 厂商定制系统的Bug：不同的设备可能会产生相同的ANDROID_ID。
	 * <li/> 厂商定制系统的Bug：有些设备返回的值为null。
	 * <li/> 设备差异：对于CDMA设备，ANDROID_ID和TelephonyManager.getDeviceId() 返回相同的值。
	 *
	 * @return
	 */
	@Nullable
	public static String getAndroidId () {
		Context context = Core.getInstance().getAppContext();
		return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

	}

	/**
	 * 获取序列号。只在Android系统2.3版本以上可以获取。
	 * 对非手机设备有效。
	 *
	 * @return
	 */
	public static String getSerialNumber () {
		return Build.SERIAL;
	}

}
