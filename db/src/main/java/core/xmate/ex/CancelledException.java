package core.xmate.ex;

/**
 * @author DrkCore
 * @since 2017-04-28
 */
public class CancelledException extends RuntimeException {
    public CancelledException(String detailMessage) {
        super(detailMessage);
    }
}
