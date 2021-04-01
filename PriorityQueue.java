import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.util.ArrayList;
import java.util.Iterator;


public class PriorityQueue<T extends Comparable <? super T>> extends ArrayList<T> {
	
	public PriorityQueue() {
		super();
	}
	
	public boolean add(T e) {
		if(e == null) {
			throw new NullPointerException();
		}
		super.add(e);
		
		int currentIndex = size() - 1;
		int comparedIndex = (currentIndex - 1) / 2;
		while(currentIndex > 1) {
			T last = super.get(currentIndex);
			T compared = super.get(comparedIndex);
			if(compared.compareTo(last) > 0) {
				super.set(comparedIndex, last);
				super.set(currentIndex, compared);
				currentIndex = comparedIndex;
				comparedIndex = (comparedIndex - 1) / 2;
			}else {
				break;
			}
		}
		return true;
		
	}
	
	public boolean offer(T e) {
		return add(e);
	}
	
	
	
	
	public T poll() {
		if(super.isEmpty()) {
			return null;
		}
		T head = peek();
		remove(head);
		return head;
	}
	

	

	
	
	
	

	public void clear() {
		super.clear();
		
	}
	public int size() {
		return super.size();
	}
	public boolean contains(Object o) {
		return super.contains(o);	
	}
	public Iterator<T> iterator(){
		return super.iterator();
	}
	public Object[] toArray() {
		return super.toArray();
	}
	public <T> T[] toArray(T[] a) {
		return super.toArray(a);
		
	}


	public T peek() {
		if(super.isEmpty()) {
			return null;
		}
		return super.get(0);
	}
	public boolean remove(Object o) {
		if(!super.contains(o)) {
			return false;
		}
		int indexOfRemoval = super.indexOf(o);
		if(indexOfRemoval != size() - 1) {
			super.set(indexOfRemoval, super.get(size() - 1));
			super.remove(size() -1);
			percolateDown(indexOfRemoval);
		}else {
			super.remove(size() -1);
		}
		return true;
	}
	
	private void percolateDown(int index) {
		int currentIndex = index;
		while(currentIndex < size() - 1) {
			int smallest = findSmallestChild(currentIndex);
			if(smallest == -1) {
				return;
			}
			T parent = super.get(index);
			T child = super.get(smallest);
			if(parent.compareTo(child) > 0) {
				super.set(currentIndex, child);
				super.set(smallest, parent);
				currentIndex = smallest;
			}else {
				break;
			}
		}
	}

	
	private int findSmallestChild(int index) {
		int rcIndex = index * 2 + 2;
		int lcIndex = index * 2 + 1;
		
		T rightChild = (rcIndex < size()) ? super.get(index * 2 + 2) : null;
		T leftChild = (lcIndex < size()) ?  super.get(index * 2 + 1) : null;
		
		if(rightChild == null && leftChild != null) {
			return index * 2 + 1;
		}else if(leftChild == null && rightChild != null) {
			return index * 2 + 2;
		}else if(rightChild != null && leftChild != null){
			return (rightChild.compareTo(leftChild) < 0) ? index * 2 + 2 : index * 2 + 1;
		}else {
			return -1;
		}
	}

}
