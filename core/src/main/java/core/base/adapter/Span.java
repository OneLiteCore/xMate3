package core.base.adapter;

import core.base.util.ViewUtil;

/**
 * 间隔
 *
 * @author DrkCore
 * @since 2016年2月1日00:16:53
 */
public final class Span {

	private int heightPx;

	public int getHeightPx () {
		if (heightPx <= 0) {
			heightPx = ViewUtil.dpToPx(8);//默认8dp
		}
		return heightPx;
	}

	public Span setHeightPx (int heightPx) {
		this.heightPx = heightPx;
		return this;
	}

	/**
	 * 使用dp来设置像素高度，将会自动转化成px
	 *
	 * @param heightDp
	 * @return
	 */
	public Span setHeightDp (float heightDp) {
		heightPx = ViewUtil.dpToPx(heightDp);
		return this;
	}
}
