package gc;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HeapObject {
    private static final int MAX_AGE_THRESHOLD = 10;
    private int age;
    private boolean reachable = true;

    public HeapObject() {
        System.out.println("HeapObject generated.");
    }

    public HeapObject(int age, boolean reachable) {
        this.age = age;
        this.reachable = reachable;
        System.out.println(this + " generated.");
    }

    public void aging() {
        this.age++;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public HeapObject copyObject() {
        return new HeapObject(this.age, this.reachable);
    }
}
