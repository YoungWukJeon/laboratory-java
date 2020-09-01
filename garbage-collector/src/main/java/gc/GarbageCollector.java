package gc;

import java.util.Arrays;
import java.util.function.Predicate;

import static gc.YoungGenerationSpace.*;

public enum GarbageCollector {
    INSTANCE;

    public static GarbageCollector getInstance() {
        return INSTANCE;
    }

    public void executeMinorGC(YoungGenerationSpace youngGenerationSpace) {
        System.out.println("MinorGC occurred!");
        System.out.println("--Stop the World!--");

        int markCount = markReachableObject(youngGenerationSpace.getEdenSpace(), youngGenerationSpace.getToSpace());

        if (!youngGenerationSpace.getToSpace().isAvailable(markCount)) {
            moveReachableHeapObjectsToSpaceFromSpace(youngGenerationSpace.getToSpace(), youngGenerationSpace.getFromSpace());
            moveReachableHeapObjectsToSpace(youngGenerationSpace.getEdenSpace(), youngGenerationSpace.getFromSpace());
            youngGenerationSpace.swapSurvivorSpace();
        } else {
            moveReachableHeapObjectsToSpace(youngGenerationSpace.getEdenSpace(), youngGenerationSpace.getToSpace());
        }
        // moveNonReachableHeapObjectsToTenuredSpace(); // promotion
        removeNonReachableHeapObjects(youngGenerationSpace.getEdenSpace(), youngGenerationSpace.getFromSpace());

    }

    private int markReachableObject(EdenSpace edenSpace, SurvivorSpace toSpace) {
        int markCount = 0;
        for (HeapObject heapObject : edenSpace.getHeapObjects()) {
            if (heapObject != null) {
                int rate = (int) (Math.random() * 2);
                if (rate % 2 == 0) {
                    heapObject.setReachable(false);
                    markCount++;
                }
            }
        }
        for (HeapObject heapObject : toSpace.getHeapObjects()) {
            if (heapObject != null) {
                int rate = (int) (Math.random() * 2);
                if (rate % 2 == 0) {
                    heapObject.setReachable(false);
                }
            }
        }
        return markCount;
    }

    private void moveReachableHeapObjectsToSpaceFromSpace(SurvivorSpace toSpace, SurvivorSpace fromSpace) {
        for (HeapObject heapObject : toSpace.getHeapObjects()) {
            if (heapObject != null && heapObject.isReachable()) {
                fromSpace.addHeapObject(heapObject.copyObject());
                heapObject.aging();
            }
        }
    }

    private void moveReachableHeapObjectsToSpace(EdenSpace edenSpace, SurvivorSpace toSpace) {
        // TODO: 2020-09-01 SurvivorSpaceFullException 발생 가능!
        for (HeapObject heapObject : edenSpace.getHeapObjects()) {
            if (heapObject != null && heapObject.isReachable()) {
                toSpace.addHeapObject(heapObject.copyObject());
                heapObject.aging();
            }
        }
    }

    private void moveNonReachableHeapObjectsToTenuredSpace() {
        System.out.println("Premature promotion!");
    }

    private void removeNonReachableHeapObjects(EdenSpace edenSpace, SurvivorSpace fromSpace) {
        edenSpace.clear();
        fromSpace.clear();
    }

    public void executeMajorGC() {
    }
}
