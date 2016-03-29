package util.blockingqueue;

import java.util.concurrent.BlockingQueue;

public class SingleReceiver<E> implements IReceiver<E> {

	private BlockingQueue<QueueElement<E>> queue;
	private boolean poisoned;
	
	public SingleReceiver(BlockingQueue<QueueElement<E>> queue) {
		this.queue = queue;
		this.poisoned = false;
	}
	
	@Override
	public E take() throws InterruptedException {
		if(poisoned) {
			return null;
		} else {
			E element = queue.take().get();
			if(element == null) {
				poisoned = true;
			}
			return element;
		}
	}

	@Override
	public boolean isPoisoned() {
		return this.poisoned;
	}

}
