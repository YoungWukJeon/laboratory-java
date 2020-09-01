package gc;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public class YoungGenerationSpace {
    private EdenSpace edenSpace;
    private SurvivorSpace survivorSpace0;
    private SurvivorSpace survivorSpace1;

    public YoungGenerationSpace() {
        init();
    }

    private void init() {
        edenSpace = new EdenSpace();
        survivorSpace0 = new SurvivorSpace("toSpace");
        survivorSpace1 = new SurvivorSpace("fromSpace");
    }

    public SurvivorSpace getToSpace() {
        return "toSpace".equals(survivorSpace0.getName())? survivorSpace0: survivorSpace1;
    }

    public SurvivorSpace getFromSpace() {
        return "fromSpace".equals(survivorSpace0.getName())? survivorSpace0: survivorSpace1;
    }

    public void swapSurvivorSpace() {
        SurvivorSpace toSpace = getToSpace();
        SurvivorSpace fromSpace = getFromSpace();
        toSpace.setName("fromSpace");
        fromSpace.setName("toSpace");
        System.out.println("Survivor space swapped.");
    }

    @Getter
    public static class EdenSpace {
        private final static int DEFAULT_SIZE = 5;
        private HeapObject[] heapObjects;
        private int topIndex;

        public EdenSpace() {
            init();
        }

        private void init() {
            heapObjects = new HeapObject[DEFAULT_SIZE];
            this.topIndex = 0;
        }

        public void addHeapObject(HeapObject heapObject) throws EdenSpaceFullException {
            if (topIndex >= heapObjects.length) {
                throw new EdenSpaceFullException("Eden space is full.");
            }
            heapObjects[topIndex++] = heapObject;
        }

        public boolean isFull() {
            return topIndex >= heapObjects.length;
        }

        public void clear() {
            init();
        }
    }

    @Getter
    public static class SurvivorSpace {
        private final static int DEFAULT_SIZE = 5;
        private HeapObject[] heapObjects;
        private String name;
        private int topIndex;

        public SurvivorSpace(String name) {
            this.name = name;
            init();
        }

        private void init() {
            heapObjects = new HeapObject[DEFAULT_SIZE];
            this.topIndex = 0;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isFull() {
            return topIndex >= heapObjects.length;
        }

        public boolean isAvailable(int markCount) {
            return topIndex + markCount >= heapObjects.length;
        }

        public void addHeapObject(HeapObject heapObject) throws SurvivorSpaceFullException {
            if (topIndex >= heapObjects.length) {
                throw new EdenSpaceFullException(name + " is full.");
            }
            heapObjects[topIndex++] = heapObject;
        }

        public void clear() {
            init();
        }
    }
}
