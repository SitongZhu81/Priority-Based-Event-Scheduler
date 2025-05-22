# Priority-Based Event Scheduler

This Java program implements a custom event scheduling system using a heap-based priority queue.  
It allows efficient insertion and retrieval of events based on time or alphabetical order, depending on the sorting mode.

## Features

- Min-heap priority queue implemented with an oversize array
- Enqueue and dequeue operations in O(log N) time
- Sorting can be configured by:
  - Event time (`LocalDateTime`)
  - Alphabetical order by name
- Includes internal test cases for validation without external libraries

## Technologies Used

- Java 17
- Custom comparison logic using `Comparable` and enums
- Heapify constructor to initialize from an array
- No external dependencies

## Files

- `PriorityEvents.java` — core heap and queue logic
- `PriorityEventsTester.java` — unit tests for all main methods
- `Event.java` — immutable event class
- `Overrides.java` — optional flags or helper constants
- `Main.java` — sample driver code for demonstration

## Example Usage

```java
PriorityEvents events = new PriorityEvents();
events.enqueue(new Event("CS Lecture", LocalDateTime.of(2025, 5, 23, 10, 0)));
System.out.println(events.dequeue());
