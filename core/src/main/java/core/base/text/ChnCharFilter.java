package core.base.text;

/**
 * 只有中文和中文标点能通过检验
 *
 * @author http://liqipan.iteye.com/blog/1564081
 * @since 2015年6月27日01:51:27
 */
public final class ChnCharFilter extends AbsCharFilter {
	
	@Override
	public boolean accept (char ch) {
		return ch >= 0x4e00 && ch <= 0x9fbb;
	}
	
}
