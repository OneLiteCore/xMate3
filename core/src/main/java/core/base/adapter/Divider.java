package core.base.adapter;

import android.support.annotation.DrawableRes;

import core.base.R;
import core.base.util.ViewUtil;

/**
 * 分割线
 *
 * @author DrkCore
 * @since 2016年2月1日00:16:53
 */
public final class Divider {

	private int heightPx;
	private int bgResId;

	public int getHeightPx () {
		if (heightPx <= 0) {
			heightPx = 1;//默认一个像素
		}
		return heightPx;
	}

	public int getBackgroundResource () {
		if (bgResId <= 0) {
			bgResId = R.color.core_black_alpha;
		}
		return bgResId;
	}

	public Divider setHeightPx (int heightPx) {
		this.heightPx = heightPx;
		return this;
	}

	public Divider setHeightDp (float heightDp) {
		heightPx = ViewUtil.dpToPx(heightDp);
		return this;
	}

	public Divider setBackgroundRes (@DrawableRes int bgResId) {
		this.bgResId = bgResId;
		return this;
	}

	public Divider () {}

	public Divider (int heightPx) {
		this.heightPx = heightPx;
	}

	public Divider (int heightPx, int bgResId) {
		this.heightPx = heightPx;
		this.bgResId = bgResId;
	}
}
