package core.base.util;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 用于加密和解密的工具类
 *
 * @author DrkCore
 * @since 2016年2月2日22:37:01
 */
public class EncryptUtil {

	private EncryptUtil () {}

	/* AES */

	public static final String AES = "AES";

	public static final String AES_MODE_CBC = "CBC";
	public static final String AES_MODE_CFB = "CFB";
	public static final String AES_MODE_ECB = "ECB";
	public static final String AES_MODE_OFB = "OFB";
	public static final String AES_MODE_PCBC = "PCBC";

	public static final String AES_PADDING_ZERO = "ZeroBytePadding";
	public static final String AES_PADDING_PKCS5 = "PKCS5Padding";
	public static final String AES_PADDING_ISO10126 = "ISO10126Padding";

	public static final String AES_DEFAULT_SECURE_ALGORITHM = "SHA1PRNG";
	public static final String AES_DEFAULT_SECURE_PROVIDER = "Crypto";

	@StringDef({
			AES_MODE_CBC,
			AES_MODE_CFB,
			AES_MODE_ECB,
			AES_MODE_OFB,
			AES_MODE_PCBC
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface AESMode {}

	@StringDef({
			AES_PADDING_ZERO,
			AES_PADDING_PKCS5,
			AES_PADDING_ISO10126
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface AESPadding {}

	/* AES加密 */

	/**
	 * 具体实现请参阅{@link #encryptAES(String, String, String, String, Charset)}。
	 * 默认使用{@link #AES_MODE_ECB}模式，使用{@link #AES_PADDING_ZERO}填充，
	 * 并使用系统默认编码{@link Charset#defaultCharset()}来获取字节数组。
	 */
	public static String encryptAES (String content, String psd) throws Exception {
		return encryptAES(AES_MODE_ECB, AES_PADDING_ZERO, content, psd, Charset.defaultCharset());
	}

	/**
	 * 加密明文为小写十六进制密文。
	 * 具体实现请参阅{@link #encryptAES(String, String, byte[], byte[])}，使用charset来提取字节数组。
	 */
	public static String encryptAES (@AESMode String mode, @AESPadding String padding, String content, String psd, Charset charset) throws Exception {
		byte[] contentBytes = content.getBytes(charset);
		byte[] psdBytes = psd.getBytes(charset);
		return EncodeUtil.toHexString(encryptAES(mode, padding, contentBytes, psdBytes));
	}

	/**
	 * 具体实现请参阅{@link #encryptAES(String, String, byte[], byte[])}，
	 * 默认使用{@link #AES_MODE_ECB}模式，使用{@link #AES_PADDING_ZERO}填充
	 */
	public static byte[] encryptAES (byte[] content, byte[] psd) throws Exception {
		return encryptAES(AES_MODE_ECB, AES_PADDING_ZERO, content, psd);
	}

	/**
	 * AES加密
	 *
	 * @param content
	 * @param psd
	 * @param mode
	 * @param padding
	 * @return
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] encryptAES (@AESMode String mode, @AESPadding String padding, byte[] content, byte[] psd) throws Exception {
		String cipherName = AES + '/' + mode + '/' + padding;

		//SecureRandom sr = new SecureRandom();// java pc版加密设置
		SecureRandom secureRandom = SecureRandom.getInstance(AES_DEFAULT_SECURE_ALGORITHM, AES_DEFAULT_SECURE_PROVIDER);// android版加密设置
		secureRandom.setSeed(psd);

		KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
		keyGenerator.init(256, secureRandom); // 192 and 256 bits may not be available
		SecretKey secretKey = keyGenerator.generateKey();
		byte[] encodedKey = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(encodedKey, AES);

		//Cipher cipher = Cipher.getInstance(AES);// java pc版加密设置
		Cipher cipher = Cipher.getInstance(cipherName);// Android版加密设置
		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
		return cipher.doFinal(content);
	}

	/* AES解密*/

	/**
	 * 具体实现请参阅{@link #decryptAES(String, String, String, String, Charset)}。
	 * 默认使用{@link #AES_MODE_ECB}模式，使用{@link #AES_PADDING_ZERO}填充，
	 * 使用系统编码{@link Charset#defaultCharset()}提取字节数组。
	 */
	public static String decryptAES (String cipherHex, String psd) throws Exception {
		return decryptAES(AES_MODE_ECB, AES_PADDING_ZERO, cipherHex, psd, Charset.defaultCharset());
	}

	/**
	 * 解密进过AES加密的十六进制字符串。
	 * 具体实现请参阅{@link #decryptAES(String, String, byte[], byte[])}。
	 */
	public static String decryptAES (@AESMode String mode, @AESPadding String padding, String cipherHex, String psd, Charset charset) throws Exception {
		byte[] cipherBytes = EncodeUtil.toBytes(cipherHex);
		byte[] psdBytes = psd.getBytes(charset);
		//TODO 在6.0以上该方法被Google修改，会直接抛出异常
		return new String(decryptAES(mode, padding, cipherBytes, psdBytes));
	}

	/**
	 * 具体实现请参阅{@link #decryptAES(String, String, byte[], byte[])}，
	 * 默认使用{@link #AES_MODE_ECB}模式，使用{@link #AES_PADDING_ZERO}填充
	 */
	public static byte[] decryptAES (byte[] cipherBytes, byte[] psd) throws Exception {
		return decryptAES(AES_MODE_ECB, AES_PADDING_ZERO, cipherBytes, psd);
	}

	/**
	 * AES解密。默认用于初始化{@link KeyGenerator}的长度为256。
	 *
	 * @param mode
	 * @param padding
	 * @param cipherBytes
	 * @param psd
	 * @return
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] decryptAES (@AESMode String mode, @AESPadding String padding, byte[] cipherBytes, byte[] psd) throws Exception {
		String cipherName = AES + '/' + mode + '/' + padding;

		//SecureRandom sr = new SecureRandom();// java pc版解密设置
		SecureRandom secureRandom = SecureRandom.getInstance(AES_DEFAULT_SECURE_ALGORITHM, AES_DEFAULT_SECURE_PROVIDER);//Android
		secureRandom.setSeed(psd);

		KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
		keyGenerator.init(256, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();
		byte[] encodedKey = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(encodedKey, AES);

		//Cipher cipher = Cipher.getInstance(AES);// java pc版设置
		Cipher cipher = Cipher.getInstance(cipherName);// Android版设置
		cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
		return cipher.doFinal(cipherBytes);
	}

}
