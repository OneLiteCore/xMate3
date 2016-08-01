package core.base.util;

import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 封装了Http辅助的工具类
 *
 * @author DrkCore
 * @since 2016年1月10日11:15:13
 */
public final class HttpUtil {

	private HttpUtil() {
	}

	/*参数提取*/

	/**
	 * 获取路径参数
	 *
	 * @param url
	 * @return
	 */
	public static List<String> getActionsFromUrl(String url) {
		Uri uri = Uri.parse(url);
		List<String> actions = new ArrayList<>();
		actions.add(uri.getHost());
		actions.addAll(uri.getPathSegments());
		return actions;
	}

	/**
	 * 获取url参数
	 *
	 * @param url
	 * @return
	 */
	public static Map<String, String> getUrlParamsFromUrl(String url) {
		Map<String, String> params = new HashMap<>();
		Uri uri = Uri.parse(url);
		Set<String> keys = uri.getQueryParameterNames();
		for (String key : keys) {
			params.put(key, uri.getQueryParameter(key));
		}
		return params;
	}

	/**
	 * 拼接ip地址。如果ip为null或者空字符串，或者port小于等于0，则返回null。
	 * 否则返回"http://ip:port"格式的字符串
	 *
	 * @param ip
	 * @param port
	 * @return
	 */
	public static String buildUrl(String ip, int port) {
		if (TextUtils.isEmpty(ip) ||  port <= 0) {
			return null;
		}
		return "http://" + ip + ":" + port;
	}
}
