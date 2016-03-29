package util.blockingqueue;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class SingleQueue<E> implements IQueue<E> {
	
	private BlockingQueue<QueueElement<E>> queue;
	private int numReceivers;
	
	SingleQueue(BlockingQueue<QueueElement<E>> queue) {
		Objects.requireNonNull(queue);
		this.queue = queue;
		this.numReceivers = 0;
	}
	
	/* (non-Javadoc)
	 * @see util.blockingqueue.IQueue#getEmitter()
	 */
	@Override
	public IEmitter<E> getEmitter() {
		return new SingleEmitter<E>(queue);
	}
	
	/* (non-Javadoc)
	 * @see util.blockingqueue.IQueue#getReceiver()
	 */
	@Override
	public IReceiver<E> getReceiver() {
		numReceivers++;
		return new SingleReceiver<E>(queue);
	}
	
	/* (non-Javadoc)
	 * @see util.blockingqueue.IQueue#numReceivers()
	 */
	@Override
	public int numReceivers() {
		return this.numReceivers;
	}
	
	/* (non-Javadoc)
	 * @see util.blockingqueue.IQueue#poisonReceivers()
	 */
	@Override
	public void poisonReceivers() throws InterruptedException {
		for(int i = 0; i < numReceivers; i++) {
			queue.put(QueueElement.getPoison());
			numReceivers--;
		}
	}
	
}
