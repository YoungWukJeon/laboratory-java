package gc;

public class SurvivorSpaceFullException extends RuntimeException {
    public SurvivorSpaceFullException() {
        super();
    }

    public SurvivorSpaceFullException(String msg) {
        super(msg);
    }

    public SurvivorSpaceFullException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
