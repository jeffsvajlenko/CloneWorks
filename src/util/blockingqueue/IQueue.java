package util.blockingqueue;

public interface IQueue<E> {

	IEmitter<E> getEmitter();

	IReceiver<E> getReceiver();

	int numReceivers();

	/**
	 * 
	 * @throws InterruptedException Keeps track of number of successful poisons.  If interrupted, can call again to resume.
	 */
	void poisonReceivers() throws InterruptedException;

}