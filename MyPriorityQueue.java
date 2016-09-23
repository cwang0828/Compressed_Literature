import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * TCSS 342 - Data Structure
 * Assignment 3 - Compressed Literature. 
 */

/**
 * This class uses an arrayList to implement a similar
 * functions as the PriorityQueue from the java library. 
 * @author Hsin-Jung Wang and Travis Arriola
 * @version 1.0
 * @param <E> is an unknown type. 
 */
public class MyPriorityQueue<E extends Comparable> {
	
	/**
	 * List used to store data for the priority queue.
	 */
	private List<E> myQueue;	
	 
	/**
	 * Constructor initializes all fields.
	 */
	public MyPriorityQueue() {
		myQueue = new ArrayList<E>();		
		
	}
	
	/**
	 * Adds an object of same type to the priority queue and sorts.
	 * @param o
	 */
	public void add(E o) {		
		myQueue.add(o);	
		Collections.sort(myQueue);
	}
	
	/**
	 * Removes the first object in the priority queue.
	 * @return E 
	 */
	public E remove() {
		if (myQueue.size() == 0) {
			throw new NoSuchElementException();
		}
		return myQueue.remove(0);
	}
	
	/**
	 * Returns size of queue.
	 * @return size
	 */
	public int size() {
		return myQueue.size();
		
	}
}

