package util.blockingqueue;

import java.util.concurrent.BlockingQueue;

/**
 * 
 * flush does nothing.  needFlush always returns false.  -- Because works directly on blocking queue on each put.
 *
 * @param <E>
 */
public class SingleEmitter<E> implements IEmitter<E> {

	private BlockingQueue<QueueElement<E>> queue;
	
	public SingleEmitter(BlockingQueue<QueueElement<E>> queue) {
		this.queue = queue;
	}
	
	@Override
	public void put(E e) throws InterruptedException {
		queue.put(new QueueElement<E>(e));
	}
	
	@Override
	public void flush() {
	}

	
	@Override
	public boolean needFlush() {
		return false;
	}

}
