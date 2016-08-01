package core.base.util;

/**
 * 用于编码的工具集
 *
 * @author DrkCore
 * @since 2016年2月16日10:37:08
 */
public final class EncodeUtil {

	private EncodeUtil () {}

	/*十六进制转化*/

	/**
	 * 具体实现请参阅{@link #toHexString(boolean, byte...)}。默认使用小写。
	 *
	 * @param bytes
	 * @return
	 */
	public static String toHexString (byte... bytes) {
		return toHexString(false, bytes);
	}

	/**
	 * 将字节数组转为十六进制的字符串。如果一个字节的值小于两位，则补位0。 如0000 1111将会转化成“0f”。
	 *
	 * @param upperCase 是否需要大写
	 * @param bytes
	 * @return
	 */
	public static String toHexString (boolean upperCase, byte... bytes) {
		if (bytes.length == 0) {
			return "";
		}

		int len = bytes.length;
		StringBuilder builder = new StringBuilder(len * 2);

		String tmp;
		for (byte b : bytes) {
			tmp = upperCase ? Integer.toHexString(0xFF & b).toUpperCase() : Integer.toHexString(0xFF & b);
			if (tmp.length() == 1) {
				builder.append("0").append(tmp);
			} else
				builder.append(tmp);
		}
		return builder.toString();
	}

	/**
	 * 将十六进制的字符串转化为字节数据。如将“0f”转化为0000 1111。无视大小写。
	 *
	 * @param hexStr
	 * @return
	 */
	public static byte[] toBytes (String hexStr) {
		int len = hexStr.length();
		if (len == 0) {
			return null;
		}

		byte[] result = new byte[len / 2];
		for (int i = 0; i < len / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
}
