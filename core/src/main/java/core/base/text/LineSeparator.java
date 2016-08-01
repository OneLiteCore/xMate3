package core.base.text;

public enum LineSeparator {
	
	/**
	 * CRLF
	 */
	WIN("\r\n"),
	
	/**
	 * LF
	 */
	LINUX("\n"),

	/**
	 * CR
	 */
	MAC("\r"),
	
	/**
	 * 当前系统的换行，在Android下默认是{@link #LINUX}制式的，也就是LF，也即'\n'。
	 */
	SYS(System.getProperty("line.separator"));
	
	private String lineSeparator;
	
	LineSeparator (String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
	
	@Override
	public String toString () {
		return lineSeparator;
	}
}
