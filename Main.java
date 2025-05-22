import java.time.DateTimeException;

public class Main {
    public static void main(String[] args) {
        // Create a priority event queue with a capacity of 10
        PriorityEvents events = new PriorityEvents(10);

        // Add some events to the queue
        try {
            events.addEvent(new Event("Project Presentation", 1, 9, 0));
            events.addEvent(new Event("Final Exam", 30, 14, 0));
            events.addEvent(new Event("Team Meeting", 28, 10, 0));
        } catch (DateTimeException | IllegalArgumentException e) {
            System.out.println("Error adding event: " + e.getMessage());
        }

        // View the next event in the queue
        try {
            Event nextEvent = events.peekNextEvent();
            System.out.println("Next event: " + nextEvent);
        } catch (Exception e) {
            System.out.println("Queue is empty: " + e.getMessage());
        }

        // Complete the next event in the queue
        try {
            events.completeEvent();
            System.out.println("An event has been completed!");
        } catch (Exception e) {
            System.out.println("Unable to complete event: " + e.getMessage());
        }

        // Print all remaining events in the queue
        System.out.println("Remaining events:");
        System.out.println(events);
    }
}