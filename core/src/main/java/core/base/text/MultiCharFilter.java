package core.base.text;

/**
 * @author DrkCore
 * @since 2015年6月27日02:06:01
 */
public final class MultiCharFilter extends AbsCharFilter {
	
	private AbsCharFilter[] filters;
	
	public MultiCharFilter (AbsCharFilter... filters) {
		super();
		this.filters = filters;
	}
	
	/* 继承 */
	
	@Override
	public boolean accept (char ch) {
		for (AbsCharFilter filter : filters) {
			if (!filter.accept(ch)) {
				return false;
			}
		}
		return true;
	}
	
}
