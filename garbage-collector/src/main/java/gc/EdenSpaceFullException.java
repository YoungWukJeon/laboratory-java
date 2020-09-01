package gc;

public class EdenSpaceFullException extends RuntimeException {
    public EdenSpaceFullException() {
        super();
    }

    public EdenSpaceFullException(String msg) {
        super(msg);
    }

    public EdenSpaceFullException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}