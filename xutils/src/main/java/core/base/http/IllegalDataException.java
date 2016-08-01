package core.base.http;

public class IllegalDataException extends Exception {

    public IllegalDataException() {
        this("数据格式异常");
    }

    public IllegalDataException(String detailMessage) {
        super(detailMessage);
    }

    public IllegalDataException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public IllegalDataException(Throwable throwable) {
        super(throwable);
    }
}
