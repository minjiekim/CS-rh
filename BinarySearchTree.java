import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;


public class BinarySearchTree <T extends Comparable <? super T>> implements Iterable <T> {
	
	private BinaryNode root;
	private int globalCounter;
	
	public BinarySearchTree(){
		root = null;
		globalCounter = 0;
	}
	
	public BinarySearchTree(BinaryNode n){
		root = n;
	}
	
	public boolean isEmpty() {
		return root == null;
	}
	
	public int height() {
		if(isEmpty()) {
			return -1;
		}
		return root.height();
	}
	
	public int size() {
		return toArrayList().size();
	}
	
	public String toString() {
		return toArrayList().toString();
	}
	
	public ArrayList<Object> toArrayList() {
		if(isEmpty()) {
			return new ArrayList<Object>();
		}
		return root.toArrayList();	
	}
	
	public Object[] toArray() {
		int size = size();
		Object[] array= new Object[size];
		for(int i = 0; i < size; i++) {
			Object element = toArrayList().get(i);
			array[i] = element;
		}
		return array;
		
	}
	
	public Iterator<T> preOrderIterator() {
		return new PreOrderIterator();
	}
	
	public boolean insert(T element) {
		if(isEmpty()) {
			root = new BinaryNode(element);
		}
		globalCounter++;
		return root.insert(element);
	}
	
	public  boolean remove(T element) {
		MyBoolean bool = new MyBoolean();
		
		if(element == null) {
			throw new IllegalArgumentException();
		}
		
		if(isEmpty()) {
			return false;
		}
		
		root = root.remove(element, bool);
		globalCounter++;
		
		return bool.getBool();
	}
	
	@Override
	public Iterator<T> iterator() {
		return new InOrderIterator();
	}
	
	public class BinaryNode {
		private T element;
		private BinaryNode leftChild;
		private BinaryNode rightChild;
		
		public BinaryNode(T e){
			this.element = e;
			this.leftChild = null;
			this.rightChild = null;		
		}
		
		public BinaryNode remove(T e, MyBoolean myBool) {
			int compare = element.compareTo(e);
			
			if(compare > 0) {   // goes left
				if(leftChild != null) {
					leftChild = leftChild.remove(e, 												myBool);
					return this;
				}	
			}
			
			if(compare < 0) {    //goes right
				if(rightChild != null) {
					
				rightChild = rightChild.remove(e, myBool);
				return this;
				}
			}

			if(compare == 0) {
				
				if(leftChild == null && rightChild == null) {
					return null;
				}
				if(leftChild == null) {
					return rightChild;
				}
				if(rightChild == null) {
					return leftChild;
				}
				if(rightChild != null && leftChild != null) {
					element =  leftChild.getMax();
					leftChild = leftChild.remove(element, myBool);
				}
				return this;
			}
			
			myBool.setFalse();
			return this;
		}
		
		public T getMax() {
			if(rightChild == null) {
				return element;
			}
			return rightChild.getMax();
		}
		

		public boolean insert(T e) {
			if(e == null) {
				throw new IllegalArgumentException();
			}
			if(element.compareTo(e) < 0) {	
				if(rightChild == null) {
					rightChild = new BinaryNode(e);
					return true;
				}
				return rightChild.insert(e);	
			}
			if(element.compareTo(e) > 0) {
				if(leftChild == null) {
					leftChild = new BinaryNode(e);
					return true;
				}
				return leftChild.insert(e);
			}
			return false;
			
		}
		
		public int height() {
			int lHeight = -1;
			int rHeight = -1;
			
			if(leftChild != null) {
				lHeight = leftChild.height();
			}
			if(rightChild != null) {
				rHeight = rightChild.height();
			}
			return (lHeight > rHeight) ? lHeight:rHeight + 1 ;
		}
		
		public ArrayList<Object> toArrayList() {
			ArrayList<Object> a = new ArrayList<Object>();
			if(leftChild != null) {
				a.addAll(leftChild.toArrayList());
			}
			a.add(element);
			if(rightChild != null) {
				a.addAll(rightChild.toArrayList());
			}
			return a;
		}
	} 
	
	private class InOrderIterator implements Iterator<T> {
		
		private Stack<BinaryNode> stack = new Stack<BinaryNode>();
		private int counter;
		private BinaryNode prev;

		public InOrderIterator() {
			prev = null;
			counter = globalCounter ;
			if(root != null)
			getAllLeft(root);
		}
	
		public boolean hasNext() {
			return !(stack.isEmpty());
		}

		@Override
		public T next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			
			if(counter != globalCounter) {
				throw new ConcurrentModificationException();
			}
			
			prev = stack.pop();
			
			if(prev.rightChild != null) {
				getAllLeft(prev.rightChild);
			}
			return prev.element;
		}
		
		private void getAllLeft(BinaryNode element) {
			stack.push(element);
			if(element.leftChild != null) {
				getAllLeft(element.leftChild);
			}
		}
		
		public void remove(){
			if (prev == null) {
				throw new IllegalStateException();
			}
			if(counter != globalCounter) {
				throw new ConcurrentModificationException();
			}
			
			BinarySearchTree.this.remove(prev.element);
			counter ++;
			prev = null;
		}
		
	}
	
	private class PreOrderIterator implements Iterator {
		
		private Stack<BinaryNode> stack = new Stack<BinaryNode>();
		private BinaryNode prev;
		private int counter;
		
		public PreOrderIterator() {
			counter = globalCounter;
			prev = null;
			if(root != null) {
				stack.push(root);
			}
		}
		
		@Override
		public boolean hasNext() {
			return !(stack.isEmpty());
		}

		@Override
		public T next() {
			if(hasNext() == false) {
				throw new NoSuchElementException();
			}
			
			if(counter != globalCounter) {
				throw new ConcurrentModificationException();
			}
			prev = stack.pop();
			if(prev.rightChild != null) {
				stack.push(prev.rightChild);
			}
			if(prev.leftChild != null) {
				stack.push(prev.leftChild);
			}
			
			return prev.element;
		}
		
		public void remove() {
			if(prev == null) {
				throw new IllegalStateException();
			}
			
			if(counter != globalCounter) {
				throw new ConcurrentModificationException();
			}

			BinarySearchTree.this.remove(prev.element);
			counter++;
			
			if(prev.leftChild != null && prev.rightChild != null) {
				stack.pop();
				stack.pop();
				stack.push(prev);
			}
			
			prev = null;
			
		}
		
	}

	private class MyBoolean {
		private boolean bool = true;

		public void setFalse() {
			bool = false;
		}
		
		public boolean getBool() {
			return bool;
		}
	}
}
