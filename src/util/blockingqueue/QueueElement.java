package util.blockingqueue;

public class QueueElement<E> {
	
	public static <E> QueueElement<E> getPoison() {
		return new QueueElement<E>(null);
	}
	
	public static <E> boolean isPoison(QueueElement<E> element) {
		if(element.get() == null)
			return true;
		else
			 return false;
	}
	
	private E element;
	
	public QueueElement(E element) {
		this.element = element;
	}
	
	public E get() {
		return element;
	}
	
	public boolean isPoison() {
		if(element == null)
			return true;
		else
			return false;
	}
	
}
