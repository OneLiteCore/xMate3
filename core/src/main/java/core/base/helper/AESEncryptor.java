package core.base.helper;

import java.nio.charset.Charset;

import core.base.util.EncryptUtil;
import core.base.util.LogUtil;

/**
 * @author DrkCore
 * @since 2016年2月24日22:40:31
 */
public class AESEncryptor extends AbsEncryptor {

	private final String mode;
	private final String padding;
	private final String psd;
	private final Charset charset;

	public AESEncryptor (String psd) {
		this(psd, null);
	}

	public AESEncryptor (String psd, String salt) {
		this(EncryptUtil.AES_MODE_ECB, EncryptUtil.AES_PADDING_ZERO, psd, Charset.defaultCharset(), salt);
	}

	public AESEncryptor (@EncryptUtil.AESMode String mode, @EncryptUtil.AESPadding String padding, String psd, Charset charset, String salt) {
		super(salt);
		this.mode = mode;
		this.padding = padding;
		this.charset = charset;
		this.psd = psd;
	}

	/*实现*/

	@Override
	protected final String doEncrypt (String src) {
		try {
			return EncryptUtil.encryptAES(mode, padding, src, psd, charset);
		} catch (Exception e) {
			LogUtil.e(e);
			throw new IllegalStateException();
		}
	}

	@Override
	protected final String doDecrypt (String cipher) {
		try {
			return EncryptUtil.decryptAES(mode, padding, cipher, psd, charset);
		} catch (Exception e) {
			LogUtil.e(e);
			throw new IllegalStateException();
		}
	}
}
