package util.blockingqueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * 
 * Is *NOT* thread-safe.  Each thread should have its own GroupEmitter.
 *
 * @param <E>
 */
public class GroupEmitter<E> implements IEmitter<E> {

	private BlockingQueue<QueueElement<List<E>>> queue;
	private List<E> buffer;
	private int size;
	
	
	public GroupEmitter(BlockingQueue<QueueElement<List<E>>> queue, int size) {
		this.queue = queue;
		this.size = size;
		this.buffer = new ArrayList<E>(size);
	}
	
	@Override
	public void put(E e) throws InterruptedException {
		if(buffer.size() == this.size) {
			queue.put(new QueueElement<List<E>>(buffer));
			buffer = new LinkedList<E>();
		}
		buffer.add(e);
	}

	@Override
	public void flush() throws InterruptedException {
		queue.put(new QueueElement<List<E>>(buffer));
		buffer = new ArrayList<E>(size);
	}

	@Override
	public boolean needFlush() {
		if(queue.size() == 0)
			return false;
		else
			return true;
	}

	
	
}
