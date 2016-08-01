package core.base.file;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import core.base.util.FileUtil;

/**
 * 按照文件拓展名过滤的Filter，如你所见，只针对文件进行过滤。
 *
 * @author DrkCore
 * @since 2016年1月24日10:05:31
 */
public class ExtNameFilter extends VisibleFilter {

	/*常见的拓展名*/

	public static final String EXT_TXT = "txt";
	public static final String EXT_DOC = "doc";
	public static final String EXT_DOCX = "docx";
	public static final String EXT_PDF = "pdf";
	public static final String EXT_WPS = "wps";
	public static final String EXT_XLS = "xls";
	public static final String EXT_PPT = "ppt";

	public static final String EXT_RAR = "rar";
	public static final String EXT_ZIP = "zip";
	public static final String EXT_7Z = "7z";

	public static final String EXT_MP3 = "mp3";
	public static final String EXT_AAC = "aac";

	public static final String EXT_MP4 = "mp4";
	public static final String EXT_AVI = "avi";
	public static final String EXT_MKV = "mkv";
	public static final String EXT_RMB = "rmb";
	public static final String EXT_RMVB = "rmvb";
	public static final String EXT_FLV = "flv";

	public static final String EXT_BMP = "bmp";
	public static final String EXT_PSD = "psd";
	public static final String EXT_JPG = "jpg";
	public static final String EXT_PNG = "png";

	public static final String EXT_C = "c";
	public static final String EXT_CPP = "cpp";
	public static final String EXT_JAVA = "java";
	public static final String EXT_CLASS = "class";

	public static final String EXT_JSON = "json";
	public static final String EXT_XML = "xml";
	public static final String EXT_HTML = "html";
	public static final String EXT_DAT = "dat";
	public static final String EXT_DB = "db";

	public static final String EXT_TORRENT = "torrent";

	/*配置*/

	private final Set<String> extSet;

	public ExtNameFilter (Set<String> extSet) {
		this.extSet = extSet;
	}

	public ExtNameFilter (String... exts) {
		if (exts == null || exts.length == 0) {
			throw new IllegalArgumentException();
		}
		extSet = new HashSet<>(exts.length);
		Collections.addAll(extSet, exts);
	}

	/*继承*/

	@Override
	public boolean accept (File file) {
		String extName = null;
		if (super.accept(file) && file.isFile()) {
			extName = FileUtil.getFileExtName(file);
			extName = extName != null? extName.toLowerCase():null;
		}
		return extName != null && extSet.contains(extName);
	}
}
