package gc;

public enum HeapObjectGenerator {
    INSTANCE;

    public static HeapObjectGenerator getInstance() {
        return INSTANCE;
    }

    public HeapObject generate() {
        return new HeapObject();
    }
}
