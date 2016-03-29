package util.blockingqueue;

/**
 * 
 * Interface to object for putting to BlockingQueue.
 * 
 * @param <E>
 */
public interface IEmitter<E> {
	
	/**
	 * Put the element into the queue.  If InterruptedException is thrown, it is safe to re-attempt (will not cause duplicate put). 
	 * @param e
	 * @throws InterruptedException 
	 */
	public void put(E e) throws InterruptedException;
	
	/**
	 * Does the Emitter need to be flushed?
	 * @return
	 */
	public boolean needFlush();
	
	/**
	 * Call this last to ensure all elements have been added to the queue.
	 * @throws InterruptedException 
	 */
	public void flush() throws InterruptedException;
}
