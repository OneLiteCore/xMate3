package core.base.text;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * 过滤CR和LF，并添加指定系统的换行符。
 *
 * @author DrkCore
 * @since 2016年2月25日01:15:20
 */
public final class LineSeparatorExchanger extends AbsCharFilter {

	private final LineSeparator lineSeparator;

	public LineSeparator getLineSeparator () {
		return lineSeparator;
	}

	public LineSeparatorExchanger (@NonNull LineSeparator lineSeparator) {
		this.lineSeparator = lineSeparator;
		setMultiFilterAsOnceEnable(true);
	}

	/*继承*/

	@Override
	public boolean accept (char ch) {
		return ch != '\r' && ch != '\n';
	}

	@Override
	protected void onCharFiltered (TextBuilder clearStrBuilder, CharSequence filteredChars) {
		super.onCharFiltered(clearStrBuilder, filteredChars);
		boolean containCR = TextUtils.indexOf(filteredChars, '\r') != -1;
		boolean containLF = TextUtils.indexOf(filteredChars, '\n') != -1;

		//原换行占用字符数量
		int originSeparatorToken = containCR && containLF ? 2 : 1;
		//被过滤的字符串中CR和LF的总和
		int separatorCount = countUnaccepted(filteredChars);

		separatorCount = separatorCount / originSeparatorToken;
		clearStrBuilder.appendMulti(lineSeparator, separatorCount);
	}
}
