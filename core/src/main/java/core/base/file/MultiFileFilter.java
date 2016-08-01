package core.base.file;

import java.io.File;
import java.io.FileFilter;

/**
 * 多个{@link FileFilter#}合体的Filter
 * 
 *@author DrkCore
 * @since 2015年8月11日00:37:28
 */
public class MultiFileFilter implements FileFilter {
	
	private FileFilter[] fileFilters;
	
	public MultiFileFilter(FileFilter... fileFilters) {
		this.fileFilters = fileFilters;
	}
	
	/* 继承 */
	
	@Override
	public boolean accept(File pathname) {
		for (FileFilter temp : fileFilters) {
			if (!temp.accept(pathname)) {
				return false;
			}
		}
		return true;
	}
	
}
