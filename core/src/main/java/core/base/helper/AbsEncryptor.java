package core.base.helper;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbsEncryptor {

	private final String salt;

	public AbsEncryptor () {
		salt = null;
	}

	public AbsEncryptor (String salt) {
		this.salt = salt;
	}

	/*继承*/

	public final String encrypt (String src) {
		if (src != null && !TextUtils.isEmpty(salt)) {
			src += salt;
		}
		return doEncrypt(src);
	}

	public final String decrypt (String cipher) {
		String src = doDecrypt(cipher);
		if (src != null && !TextUtils.isEmpty(salt) && src.endsWith(salt)) {
			src = src.substring(0, src.length() - salt.length());
		}
		return src;
	}

	/*回调*/

	protected abstract String doEncrypt (String src);

	protected abstract String doDecrypt (String cipher);

	/*拓展*/

	/**
	 * 加密集合中的字符串。该方法不会修改原有的数据。
	 *
	 * @param values
	 * @return
	 */
	public final List<String> encrypt (Collection<String> values) {
		if (values == null) {
			return null;
		}

		List<String> list = new ArrayList<>(values.size());
		if (!values.isEmpty()) {
			for (String v : values) {
				list.add(encrypt(v));
			}
		}
		return list;
	}

	/**
	 * 加密数组中的字符串。该方法不会修改原有数据。
	 *
	 * @param values
	 * @return
	 */
	public final String[] encrypt (String... values) {
		if (values == null) {
			return null;
		}

		int len = values.length;
		String[] encryptValues = new String[len];
		for (int i = 0; i < len; i++) {
			encryptValues[i] = encrypt(values[i]);
		}
		return encryptValues;
	}

	/**
	 * 解密集合中的字符串。该方法不会修改原有数据。
	 *
	 * @param values
	 * @return
	 */
	public final List<String> decrypt (Collection<String> values) {
		if (values == null) {
			return null;
		}

		List<String> list = new ArrayList<>(values.size());
		if (!values.isEmpty()) {
			for (String v : values) {
				list.add(decrypt(v));
			}
		}
		return list;
	}

	/**
	 * 解密数组中的字符串。该方法不会修改原有数据。
	 *
	 * @param values
	 * @return
	 */
	public final String[] decrypt (String... values) {
		if (values == null) {
			return null;
		}

		int len = values.length;
		String[] decryptValues = new String[len];
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				decryptValues[i] = decrypt(values[i]);
			}
		}
		return decryptValues;
	}

}
