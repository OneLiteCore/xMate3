package core.base.file;

import java.io.File;

/**
 * 目录的Filter
 *
 * @author DrkCore
 * @since 2015年3月13日12:35:32
 */
public class DirFilter extends VisibleFilter {
	
	@Override
	public boolean accept (File file) {
		return super.accept(file) && file.isDirectory();
	}
	
}
