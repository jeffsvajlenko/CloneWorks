package util.blockingqueue;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueBuilder {
	
	public static <E> IQueue<E> singleQueue_arrayBacked(int capacity) {
		return new SingleQueue<E>(new ArrayBlockingQueue<QueueElement<E>>(capacity));
	}
	
	public static <E> IQueue<E> singleQueue_linkedListBacked() {
		return new SingleQueue<E>(new LinkedBlockingQueue<QueueElement<E>>());
	}
	
	public static <E> IQueue<E> groupQueue_arrayBacked(int capacity, int maxGroupSize) {
		return new GroupQueue<E>(new ArrayBlockingQueue<QueueElement<List<E>>>(capacity), maxGroupSize);
	}
	
	public static <E> IQueue<E> groupQueue_linkedListBacked(int maxGroupSize) {
		return new GroupQueue<E>(new LinkedBlockingQueue<QueueElement<List<E>>>(), maxGroupSize);
	}
	
}
