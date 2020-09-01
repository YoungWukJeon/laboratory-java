package gc;

import org.junit.Test;

public class HeapObjectGeneratorTests {
    private final YoungGenerationSpace youngGenerationSpace = new YoungGenerationSpace();

    @Test
    public void loop() {
        while (true) {
            generate();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void generate() {
        HeapObject newHeapObject = HeapObjectGenerator.getInstance().generate();
        try {
            youngGenerationSpace.getEdenSpace().addHeapObject(newHeapObject);
        } catch (EdenSpaceFullException edenSpaceFullException) {
            edenSpaceFullException.printStackTrace();
            GarbageCollector.getInstance().executeMinorGC(youngGenerationSpace);
        }
    }
}
