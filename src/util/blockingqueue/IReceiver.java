package util.blockingqueue;

public interface IReceiver<E> {

	public E take() throws InterruptedException;
	public boolean isPoisoned();
}
