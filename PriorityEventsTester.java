
import java.util.NoSuchElementException;

public class PriorityEventsTester {
  
  /**
   * This method runs all sub-testers related to testing adding an Event to the priority queue.
   * You may wish to add additional output for clarity, or additional private tester methods related
   * to adding Events.
   * @return true if all tests relating to adding an Event to a priority queue pass; false otherwise
   */
  public static boolean testAddEvent() {
    boolean ok = true; // start optimistic – AND in failures
    ok &= testAddEventChronological();   // heap ordered by timestamp
    ok &= testAddEventAlphabetical();    // heap ordered by description
    ok &= testAddEventInvalid();         // invalid inputs
    return ok;
  }

    /**
     * This method tests the addEvent() method of the PriorityEvents class. It creates a new
     * PriorityEvents object, adds an Event to it, and checks if the event was added correctly.
     * @return true if the test passes; false otherwise
     */
  private static boolean testAddEventChronological() {
    try {
      PriorityEvents.sortChronologically();
      PriorityEvents pq = new PriorityEvents(3);

      // Create two events: e1 is earlier than e2
      Event e1 = new Event("A", 1, 0, 0);
      Event e2 = new Event("B", 1, 1, 0);

      // Insert out of order to force a percolate‑up
      pq.addEvent(e2);
      pq.addEvent(e1);

      // Root should now be e1 (earlier time)
      if (!pq.peekNextEvent().equals(e1)) return false;
      return pq.size() == 2;
    } catch (Exception e) {
      return false;
    }
  }
    /**
     * This method tests the addEvent() method of the PriorityEvents class. It creates a new
     * PriorityEvents object, adds an Event to it, and checks if the event was added correctly.
     * @return true if the test passes; false otherwise
     */
    private static boolean testAddEventAlphabetical() {
      try {
        PriorityEvents.sortAlphabetically();
        PriorityEvents pq = new PriorityEvents(3);
        Event e1 = new Event("B", 1, 0, 0);
        Event e2 = new Event("A", 1, 0, 0);

        pq.addEvent(e1);
        pq.addEvent(e2); // should bubble up

        if (!pq.peekNextEvent().equals(e2)) return false;
        return pq.size() == 2;
      } catch (Exception e) {
        return false;
      }
    }



  /**
     * This method tests the addEvent() method of the PriorityEvents class. It creates a new
     * PriorityEvents object, adds an Event to it, and checks if the event was added correctly.
     * @return true if the test passes; false otherwise
     */
  private static boolean testAddEventInvalid() {
    boolean caughtAll = true;

    try {
      new PriorityEvents(2).addEvent(null); // null should throw
      caughtAll = false;
    } catch (IllegalArgumentException ignore) {}

    try {
      Event done = new Event("Done", 1, 0, 0);
      done.markAsComplete();
      new PriorityEvents(2).addEvent(done); // completed should throw
      caughtAll = false;
    } catch (IllegalArgumentException ignore) {}

    return caughtAll;
  }



    /**
     * This method runs all sub-testers related to testing marking an Event in the priority queue as
     * completed. You may wish to add additional output for clarity, or additional private tester
     * methods related to marking Events as completed.
     * @return true if all tests relating to removing an Event from a priority queue pass; false
     * otherwise
     */
    public static boolean testCompleteEvent() {
      // Combine results from all complete-event paths
      boolean ok = true;
      ok &= testCompleteEventChronological();   // normal chronological queue
      ok &= testCompleteEventAlphabetical();    // normal alphabetical queue
      ok &= testCompleteEventInvalid();         // error/edge cases
      return ok;
    }

    /**
     * This method tests the completeEvent() method of the PriorityEvents class. It creates a new
     * PriorityEvents object, adds an Event to it, and checks if the event was completed correctly.
     * @return true if the test passes; false otherwise
     */
    private static boolean testCompleteEventInvalid() {
      boolean ok = true;

      // Empty queue should throw
      try {
        new PriorityEvents(1).completeEvent();
        ok = false;
      } catch (IllegalStateException ignore) {}

      // Overflow completed array
      try {
        PriorityEvents pq = new PriorityEvents(1);
        pq.addEvent(new Event("seed",1,0,0));
        pq.completeEvent();
        for (int i=0;i< pq.clearCompletedEvents().length*2;i++){
          pq.addEvent(new Event("x"+i,1,0,i%60));
          pq.completeEvent();
        }
        pq.completeEvent(); // should throw
        ok = false;
      } catch (IllegalStateException ignore) {}
      catch (Exception e) { ok = false; }

      return ok;
    }
    /**
     * This method tests the completeEvent() method of the PriorityEvents class. It creates a new
     * PriorityEvents object, adds an Event to it, and checks if the event was completed correctly.
     * @return true if the test passes; false otherwise
     */
    private static boolean testCompleteEventChronological() {
      try {
        PriorityEvents.sortChronologically();
        PriorityEvents pq = new PriorityEvents(3);
        Event e1 = new Event("A", 1, 0, 0);
        Event e2 = new Event("B", 1, 1, 0);
        pq.addEvent(e1);
        pq.addEvent(e2);

        pq.completeEvent(); // removes e1

        // Verify sizes and that e2 is now root
        return pq.numCompleted() == 1 && pq.size() == 1 && pq.peekNextEvent().equals(e2);
      } catch (Exception e) {
        return false;
      }
    }

  /**
     * This method tests the completeEvent() method of the PriorityEvents class. It creates a new
     * PriorityEvents object, adds an Event to it, and checks if the event was completed correctly.
     * @return true if the test passes; false otherwise
     */
    private static boolean testCompleteEventAlphabetical() {
      try {
        PriorityEvents.sortAlphabetically();
        PriorityEvents pq = new PriorityEvents(3);
        Event e1 = new Event("B",1,0,0);
        Event e2 = new Event("A",1,0,0);
        pq.addEvent(e1);
        pq.addEvent(e2);
        pq.completeEvent(); // removes "A"

        if (pq.numCompleted()!=1||pq.size()!=1) return false;
        if (!pq.getCompletedEvents()[0].isComplete()) return false;
        return pq.peekNextEvent().equals(e1);
      }catch(Exception ex){return false;}
    }



  /**
   * Verifies the peekNextEvent() method. You may wish to break this out into smaller sub-testers.
   * @return true if all tests pass; false otherwise
   */
  public static boolean testPeek() {
    boolean ok = true;
    try {
      PriorityEvents pq = new PriorityEvents(2);
      // Empty queue – expect NoSuchElementException
      try {
        pq.peekNextEvent();
        return false;
      } catch (NoSuchElementException ignore) {}

      // Non‑empty queue — element should be returned but not removed
      Event e = new Event("X",1,0,0);
      pq.addEvent(e);
      ok &= pq.peekNextEvent().equals(e)&&pq.size()==1;
    }catch(Exception e){return false;}

    ok &= testPeekRootOnly();
    return ok;
  }



  /**
     * Verifies the peekNextEvent() method when the root is the only event in the queue.
     * @return true if all tests pass; false otherwise
     */
  private static boolean testPeekRootOnly() {
      try {
        PriorityEvents.sortChronologically();
        PriorityEvents pq = new PriorityEvents(3);

        // Insert late first, then early, forcing a percolate‑up of "early"
        Event early = new Event("early", 1, 0, 0);
        Event late  = new Event("late",  1, 10, 0);
        pq.addEvent(late);
        pq.addEvent(early);

        // Root should be early and queue size should remain 2 after peek
        return pq.peekNextEvent().equals(early) && pq.size() == 2;
      } catch (Exception e) {
        return false;
      }
    }

  /**
   * Verifies the overloaded PriorityEvents constructor that creates a valid heap from an input
   * array of values. You may wish to break this out into smaller sub-testers.
   * @return true if all tests pass; false otherwise
   */
  public static boolean testHeapify() {
    boolean ok = true;

    // Case 1: chronological heapify
    try {
      PriorityEvents.sortChronologically();
      Event[] arr = {
              new Event("B", 1, 1, 0),
              new Event("A", 1, 0, 0),
              new Event("C", 1, 2, 0)
      };
      PriorityEvents pq = new PriorityEvents(arr, 3);
      ok &= pq.peekNextEvent().equals(arr[1]); // "A" should be root
    } catch (Exception e) { ok = false; }

    // Case 2: invalid input (completed event present)
    ok &= testHeapifyInvalid();
    // Case 3: alphabetical heapify
    ok &= testHeapifyAlphabetical();
    return ok;
  }
    /**
     * Verifies the overloaded PriorityEvents constructor that creates a valid heap from an input
     * array of values.
     * @return true if all tests pass; false otherwise
     */
   private static boolean testHeapifyInvalid() {
    try {
      Event[] arr={new Event("A",1,0,0),new Event("B",1,1,0)};
      arr[1].markAsComplete();                   // corrupt input
      new PriorityEvents(arr,2);                // should throw
      return false;
    }catch(IllegalArgumentException e){return true;}
  }

    /**
     * Verifies the overloaded PriorityEvents constructor that creates a valid heap from an input
     * array of values.
     * @return true if all tests pass; false otherwise
     */
    private static boolean testHeapifyAlphabetical() {
      try {
        PriorityEvents.sortAlphabetically();
        // unsorted by description; "A" should surface
        Event[] arr = {
                new Event("B",1,1,0),
                new Event("A",1,1,0),
                new Event("C",1,1,0)
        };
        PriorityEvents pq = new PriorityEvents(arr,3);
        return pq.peekNextEvent().equals(arr[1]);
      } catch (Exception e) { return false; }
    }

  public static void main(String[] args) {
    System.out.println("ADD: "+testAddEvent());
    System.out.println("COMPLETE: "+testCompleteEvent());
    System.out.println("PEEK: "+testPeek());
    System.out.println("HEAPIFY: "+testHeapify());
  }

}
