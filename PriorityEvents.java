/**
 * A priority event scheduler using a heap-based queue.
 * Allows events to be inserted and retrieved based on customizable sorting logic (time or name).
 *
 * Implements core data structures and demonstrates OOP design in Java.
 */
import java.util.Arrays;
import java.util.NoSuchElementException;

public class PriorityEvents {
    private Event[] heapData; // An array which maintains the heap structure for our priority queue; data in this array MUST be maintained in valid heap order with respect to either Event comparisons or description, per the value of the sortAlphabetically field of this object
    private Event[] completed;// An array which contains all of the completed Events that have passed through this priority queue; this array has double the capacity of heapData.
    private int size; // The number of events currently stored in the heapData array
    private int completedSize; // The number of events currently stored in the completed array
    private static boolean sortAlphabetically = false; // Indicates whether the events in this priority queue should be arranged in heap order with respect to their timestamps (using Event.compareTo()) or alphabetically by their descriptions

    /**
     * Creates a new priority queue with the given capacity.
     *
     * @param capacity the capacity of the queue; must be > 0
     * @throws IllegalArgumentException if a capacity of 0 or less is provided
     */
    public PriorityEvents(int capacity) throws IllegalArgumentException {
        // Validate the requested capacity first
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive: " + capacity);
        }
        // Allocate the backing arrays (completed is twice the capacity per spec)
        this.heapData = new Event[capacity];
        this.completed = new Event[capacity * 2];
        // Initialize counters – heap is empty at construction time
        this.size = 0;
        this.completedSize = 0;
    }

    /**
     * Creates a valid min-heap from the provided oversize array of Events
     *
     * @param events the Events to be prioritized
     * @param size   the number of Events in the provided events array, assumed valid
     * @throws IllegalArgumentException if any provided event is already completed
     */
    public PriorityEvents(Event[] events, int size) throws IllegalArgumentException{
        // Null‑check to avoid NPEs later
        if (events == null) {
            throw new IllegalArgumentException("Events array cannot be null");
        }
        // Deep copy of the incoming array so the caller can still mutate theirs safely
        this.heapData = Arrays.copyOf(events, events.length);
        this.size = size;
        // Verify none of the supplied Events are already complete
        for (int i = 0; i < size; i++) {
            if (heapData[i].isComplete()) {
                throw new IllegalArgumentException("Cannot heapify completed event");
            }
        }
        // Prepare the completed array (empty at construction time)
        this.completed = new Event[events.length * 2];
        this.completedSize = 0;
        // -------- Heapify --------
        // Starting from the last internal node and moving to the root guarantees O(N) time.
        for (int i = (size / 2) - 1; i >= 0; i--) {
            percolateDown(i);
        }
    }

    /**
     * Reports whether queues are sorted alphabetically.
     *
     * @return true if this priority queue is ordered by description, false if it is ordered by timestamp
     */
    public static boolean isSortedAlphabetically() {
        return sortAlphabetically;
    }

    /**
     * Sets all priority queues to be sorted alphabetically.
     */
    public static void sortAlphabetically() {
        sortAlphabetically = true;
    }

    /**
     * Sets all priority queues to be sorted chronologically.
     */
    public static void sortChronologically() {
        sortAlphabetically = false;
    }

    /**
     * For testing purposes; returns a deep copy of the completed array WITHOUT clearing the array
     *
     * @return a deep copy of the contents of the completed array
     */
    protected Event[] getCompletedEvents() {
        // Defensive copy so tests cannot mutate internal state
        return Arrays.copyOf(completed, completedSize);
    }

    /**
     * For testing purposes; accesses a deep copy of the heapData array. It is not necessary to create deep copies of the Events contained in that array.
     *
     * @return a deep copy of the heapData array
     */
    protected Event[] getHeapData() {
        return Arrays.copyOf(heapData, heapData.length);
    }

    /**
     * Reports whether this priority queue currently contains any Events, not counting those in the completed array
     */
    public boolean isEmpty() {
        // Empty exactly when no live events are stored
        return size == 0;
    }

    /**
     * Accesses the number of events currently in this priority queue, not counting those in the completed array
     */
    public int size() {
        return size;
    }

    /**
     * Accesses the number of events in the completed array
     */
    public int numCompleted() {
        return completedSize;
    }

    /**
     * Returns a deep copy of the completed array, and empties out the array
     *
     * @return deep copy of events that were completed
     */
    public Event[] clearCompletedEvents() {
        // Copy first so the caller receives the list as‑was
        Event[] copy = Arrays.copyOf(completed, completedSize);
        // Logical clear – simply reset the counter; objects remain for GC to reclaim if needed
        completedSize = 0;
        return copy;
    }

    /**
     * Accesses the next (according to priority) event without removing it from the queue
     *
     * @return a reference to the next (upcoming or alphabetical) event in the queue
     * @throws NoSuchElementException if the queue is currently empty
     */
    public Event peekNextEvent() throws NoSuchElementException{
        // The min element is always at index 0 in a min‑heap
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        return heapData[0];
    }

    /**
     * Inserts a new Event into the priority queue in the correct location in O(log N) time -> MUST call one of the percolate helper methods
     *
     * @param e the new Event to be added
     * @throws IllegalStateException    if queue is full
     * @throws IllegalArgumentException if event is null or already completed
     */
    public void addEvent(Event e) throws IllegalStateException, IllegalArgumentException {
        // ----- input validation -----
        if (e == null || e.isComplete()) {
            throw new IllegalArgumentException("Invalid event to add");
        }
        if (size >= heapData.length) {
            throw new IllegalStateException("Priority queue is full");
        }
        // ----- insertion -----
        // Place the new item at the next open slot (end of the heap)
        int idx = size;
        heapData[idx] = e;
        size++;
        // Restore heap order by bubbling the new element upward
        percolateUp(idx);
    }

    /**
     * Removes the next (according to priority) Event from the priority queue, marks it as complete, and appends it to the completed array. -> MUST call one of the percolate helper methods
     *
     * @throws IllegalStateException if queue is empty or completed array is full
     */
    public void completeEvent() throws IllegalStateException {
        // Pre‑condition checks first
        if (isEmpty()) {
            throw new IllegalStateException("Priority queue is empty");
        }
        if (completedSize >= completed.length) {
            throw new IllegalStateException("Completed array is full");
        }
        // ----- remove root (min) -----
        Event best = heapData[0];
        best.markAsComplete();               // logical status update
        completed[completedSize++] = best;   // record in completed list
        // Replace root with last element to keep tree complete
        heapData[0] = heapData[size - 1];
        heapData[size - 1] = null;           // avoid loitering
        size--;
        // Restore heap order by bubbling the replacement downward if queue not empty
        if (size > 0) {
            percolateDown(0);
        }
    }

    /**
     * Required helper method for toString, which creates a deep copy of the current queue
     *
     * @return a new PriorityEvents queue with a deep copy of the heapData and completed arrays and their corresponding sizes
     */
    protected PriorityEvents deepCopy() {
        // Create a new instance with same capacity
        PriorityEvents copy = new PriorityEvents(heapData.length);
        // Manually clone primitive fields then clone the arrays
        copy.size = this.size;
        copy.completedSize = this.completedSize;
        copy.heapData = Arrays.copyOf(this.heapData, this.heapData.length);
        copy.completed = Arrays.copyOf(this.completed, this.completed.length);
        return copy;
    }

    /**
     * Creates a String representation of all events in the queue in sorted order, one on each line (no trailing newline). Must NOT modify the queue - use a deep copy of the queue instead.
     * @return a String representation of all events in sorted order
     */
    @Override
    public String toString() {
        // Work on a deep copy so the real queue remains untouched
        PriorityEvents temp = deepCopy();
        StringBuilder sb = new StringBuilder();
        // Repeatedly remove the best (min) until exhausted – this outputs in sorted order
        while (!temp.isEmpty()) {
            Event next = temp.removeBest();
            sb.append(next.toString());
            // Insert newline only between events (none after the last)
            if (!temp.isEmpty()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Helper to remove and return the best event, without marking completed.
     */
    private Event removeBest() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        // Standard dequeue for a min‑heap: pop root, replace with last, bubble down
        Event best = heapData[0];
        heapData[0] = heapData[size - 1];
        heapData[size - 1] = null;
        size--;
        if (size > 0) {
            percolateDown(0);
        }
        return best;
    }

    /**
     * Helper method; MUST BE IMPLEMENTED RECURSIVELY
     *
     * Percolates the value at index i of the heapData array toward index 0 according to min-heap protocols, comparing either Event timestamps or descriptions depending on the value of the sortAlphabetically field
     * @param i - the index of the Event in heapData to be percolated
     */
    protected void percolateUp(int i) {
        // Base case: reached root
        if (i <= 0) return;
        int parent = (i - 1) / 2;
        // Decide ordering criterion based on global flag
        Event curr = heapData[i];
        Event above = heapData[parent];
        int cmp = sortAlphabetically
                ? curr.getDescription().compareTo(above.getDescription())
                : curr.compareTo(above);
        // If current node violates min‑heap property, swap and recurse
        if (cmp < 0) {
            heapData[i] = above;
            heapData[parent] = curr;
            percolateUp(parent); // tail recursion on the new index
        }
    }

    /**
     * Helper method; MUST BE IMPLEMENTED RECURSIVELY
     *
     * Percolates the value at index i of the heapData array away from index 0 according to min-heap protocols, comparing either Event timestamps or descriptions depending on the value of the sortAlphabetically field
     */
    protected void percolateDown(int i) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int smallest = i;
        // Compare left child
        if (left < size) {
            Event leftE = heapData[left];
            Event bestE = heapData[smallest];
            int cmp = sortAlphabetically
                    ? leftE.getDescription().compareTo(bestE.getDescription())
                    : leftE.compareTo(bestE);
            if (cmp < 0) {
                smallest = left;
            }
        }
        // Compare right child against current best
        if (right < size) {
            Event rightE = heapData[right];
            Event bestE = heapData[smallest];
            int cmp = sortAlphabetically
                    ? rightE.getDescription().compareTo(bestE.getDescription())
                    : rightE.compareTo(bestE);
            if (cmp < 0) {
                smallest = right;
            }
        }
        // If a child is smaller, swap with that child and continue percolating down
        if (smallest != i) {
            Event tmp = heapData[i];
            heapData[i] = heapData[smallest];
            heapData[smallest] = tmp;
            percolateDown(smallest); // recurse on the child index that received the swapped element
        }
    }

}
