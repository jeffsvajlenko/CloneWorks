package util.blockingqueue;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class GroupQueue<E> implements IQueue<E> {

	private BlockingQueue<QueueElement<List<E>>> queue;
	private int maxGroupSize;
	private int numReceivers;
	
	protected GroupQueue(BlockingQueue<QueueElement<List<E>>> queue, int maxGroupSize) {
		this.queue = queue;
		this.maxGroupSize = maxGroupSize;
		this.numReceivers = 0;
	}
	
	@Override
	public IEmitter<E> getEmitter() {
		return new GroupEmitter<E>(queue, maxGroupSize);
	}

	@Override
	public IReceiver<E> getReceiver() {
		numReceivers++;
		return new GroupReceiver<E>(queue);
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
