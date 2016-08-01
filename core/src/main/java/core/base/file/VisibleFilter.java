package core.base.file;

import android.support.annotation.IntDef;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 按照文件可见性过滤文件的FileFilter
 *
 * @author DrkCore
 * @since 2016年1月24日09:30:47
 */
public class VisibleFilter implements FileFilter {

	public static final int ALL = 0;
	public static final int VISIBLE = 1;
	public static final int HIDDEN = 2;

	@IntDef({ALL, VISIBLE, HIDDEN})
	@Retention(RetentionPolicy.SOURCE)
	public @interface VISIBLE {}

	private int visible = VISIBLE;

	/**
	 * 设置可以通过过滤的文件可见性，默认情况下只有{@link #VISIBLE}的文件可以通过过滤
	 *
	 * @param visible
	 * @return
	 */
	public VisibleFilter setFilter (@VISIBLE int visible) {
		this.visible = visible;
		return this;
	}

	@Override
	public boolean accept (File pathname) {
		switch (visible) {
			case VISIBLE:
				return !pathname.isHidden();

			case HIDDEN:
				return pathname.isHidden();

			case ALL:
			default:
				return true;
		}
	}

}
