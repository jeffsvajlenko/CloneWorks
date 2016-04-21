package util.blockingqueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class GroupReceiver<E> implements IReceiver<E> {

	private BlockingQueue<QueueElement<List<E>>> queue;
	private List<E> buffer;
	private boolean poisoned;
	
	public GroupReceiver(BlockingQueue<QueueElement<List<E>>> queue) {
		this.queue = queue;
		this.poisoned = false;
		this.buffer = new LinkedList<E>(); // Dummy initial list
	}
	
	@Override
	public E take() throws InterruptedException {
		// Case #1: Poisoned
		if(poisoned) {
			return null;
			
		// Case #2: Not yet poisoned
		} else {
			
			// If empty buffer, fetch new buffer
			if(buffer.size() == 0) {
				QueueElement<List<E>> element = queue.take();
				
				// If poisoned element, set state poisoned, immediatly return null
				if(element.isPoison()) {
					poisoned = true;
					return null;
				} else {
					buffer = element.get();
				}
			}
			
			// Return next element from buffer
			return buffer.remove(0);
		}
	}

	@Override
	public boolean isPoisoned() {
		return this.poisoned;
	}

}
