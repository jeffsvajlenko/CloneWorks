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
	
	@Override
	public IEmitter<E> getEmitter() {
		return new SingleEmitter<E>(queue);
	}
	
	@Override
	public IReceiver<E> getReceiver() {
		numReceivers++;
		return new SingleReceiver<E>(queue);
	}
	
	@Override
	public int numReceivers() {
		return this.numReceivers;
	}
	
	@Override
	public void poisonReceivers() throws InterruptedException {
		while(numReceivers != 0) {
			queue.put(QueueElement.getPoison());
			numReceivers--;
		}
	}
	
	@Override
	public int size() {
		return queue.size();
	}
	
}
