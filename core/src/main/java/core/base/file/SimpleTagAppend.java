package core.base.file;

import java.io.File;

import core.base.util.FileUtil;

/**
 * 简单的文件名称的tag添加工具。
 * 比如当路径为“/storage/sdcard0/1.txt”，处理后可以得到“/storage/sdcard0/1【tag...tag】.txt”
 * 类似的名称。<br/>
 * 如果路径类似“/storage/sdcard0/abc”，处理后可得到“/storage/sdcard0/abc【tag...tag】”类似的名称。<br/>
 * 
 * 即不影响文件的类型名。
 * 
 *@author DrkCore
 * @since 2015年9月22日23:37:16
 */
public class SimpleTagAppend extends FileUtil.AbsTagAppender {
	
	private final String tag;
	
	public SimpleTagAppend(String tag) {
		if (tag == null || tag.equals("")) {
			throw new IllegalArgumentException("tag不可为空");
		} else if (tag.contains("\\") || tag.contains("/") || tag.contains(".")) {
			throw new IllegalArgumentException("tag中不允许带有非法字符");
		}
		this.tag = tag;
	}
	
	@Override
	public File appendTagToFile(String basePath) {
		File file = new File(basePath);
		if (file.exists()) {// 重名文件已存在
			StringBuilder path = new StringBuilder(basePath);
			String type = FileUtil.getFileExtName(basePath);
			
			if (type != null) {// 拓展名存在
				int typeLen = type.length();
				do {
					path.replace(path.length() - typeLen - 1, path.length(), tag).append('.').append(type);
					file = new File(path.toString());
				} while (file.exists());
			} else {// 拓展名不存在
				do {
					// 计算路径
					path.append(tag);
					file = new File(path.toString());
				} while (file.exists());
			}
		}
		return file;
	}
	
	@Override
	protected File appendTagToDir(String basePath) {
		File dir = new File(basePath);
		if (dir.exists()) {// 重名文件已存在
			StringBuilder path = new StringBuilder(basePath);
			do {
				// 计算路径
				path.append(tag);
				dir = new File(path.toString());
			} while (dir.exists());
		}
		return dir;
	}
}
