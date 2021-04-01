import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;


public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
	private int trueMods;
	private BinaryNode root;

	// Most of you will prefer to use NULL NODES once you see how to use them.
	private final BinaryNode NULL_NODE = new BinaryNode();

	public BinarySearchTree() {
		root = NULL_NODE; // NULL_NODE;
		this.trueMods = 0;
	}

	// For manual tests only
	void setRoot(BinaryNode n) {
		this.root = n;
	}
	
	public boolean isEmpty() {
		return this.root == NULL_NODE;
	}
	
	public int size() {
		return root.size();
	}
	
	public int height() {
		return root.height();
	}
	
	public boolean containsNonBST(T element) {
		return root.containsNonBST(element);
	}
	
	public ArrayList<T> toArrayList(){
		ArrayList<T> list = new ArrayList<T>();
		this.root.toArrayList(list);
		return list;
	}
	
	public String toString() {
		return toArrayList().toString();
	}
	
	public Object[] toArray() {
		return toArrayList().toArray();
	}
	
	public boolean insert(T data) {
		if(isEmpty()) {
			root = new BinaryNode(data);
		}
		trueMods++;
		return root.insert(data);	
	}
	
	public boolean remove(T data) {
		if(isEmpty()) {
			return false;
		}
		
		MyBoolean bool = new MyBoolean();
		root = root.remove(data, bool);
		
		trueMods++;
		return bool.getBool();
	}
	
	public boolean contains(T data) {
		if(isEmpty()) {
			return false;
		}
		return root.contains(data);
	}
	
	@Override
	public Iterator<T> iterator() {
		return new InOrderIterator();
	}
	
	public Iterator<T> preOrderIterator(){
		return new PreOrderIterator();
	}
	
	public Iterator<T> inefficientIterator() {
		return new InefficientIterator();
	}
	
	// Not private, since we need access for manual testing.
	class BinaryNode {
		private T data;
		private BinaryNode left;
		private BinaryNode right;

		//just to build the NULL_NODE
		public BinaryNode() {
			this.data = null;
			this.left = null;
			this.right = null;
		}
		
		public BinaryNode remove(T data, MyBoolean bool) {
			if(data == null) {
				throw new IllegalArgumentException();
			}
			
			int compare = this.data.compareTo(data);
			
			if(compare < 0) {//data is greater, so goes to right sub tree
				if(this.right != NULL_NODE) {
					this.right = this.right.remove(data, bool);
					return this;
				}
			}else if(compare > 0) {//data is less, so goes to left subtree
				if(this.left != NULL_NODE) {
					this.left = this.left.remove(data, bool);
					return this;
				}
			}else if(compare == 0){//it is equal to what it is compared to
				if(this.right == NULL_NODE && this.left == NULL_NODE){
					return NULL_NODE;
				}
				
				//removal procedure if node only has right child
				if(this.left == NULL_NODE) {
					return this.right;
				}
				//removal procedure if node only has left child
				if(this.right == NULL_NODE) {
					return this.left;
				}
				
				//removal procedure if node has two children
				if(this.right != NULL_NODE && this.left != NULL_NODE) {
					this.data = this.left.getMaxFromRight();
					this.left = this.left.remove(this.data, bool);
				}
				return this;
			}
			
			//if data was not removed
			bool.hasChanged();
			return this;
		}
		
		//
		public T getMaxFromRight() {
			if(this.right == NULL_NODE) {
				return this.data;
			}
			return this.right.getMaxFromRight();
		}

		public boolean contains(T data) {
			if(this == NULL_NODE) {
				return false;
			}
			
			int compare = this.data.compareTo(data);
			
			if(compare < 0) {//goes to right
				return this.right.contains(data);
			}else if(compare > 0) {//goes left
				return this.left.contains(data);
			}else if(compare == 0){
				return true;
			}else {
				return false;
			}
		}

		public boolean insert(T data) {
			if(data == null) {
				throw new IllegalArgumentException();
			}
			
			int compare = this.data.compareTo(data);
			
			if(compare < 0) {//goes to right
				if(this.right == NULL_NODE) {
					this.right = new BinaryNode(data);
					return true;
				}
				return this.right.insert(data);
			}else if(compare > 0) {//goes left
				if(this.left == NULL_NODE) {
					this.left = new BinaryNode(data);
					return true;
				}
				return this.left.insert(data);
			}else {
				return false;
			}
		}

		public BinaryNode(T element) {
			this.data = element;
			this.left = NULL_NODE;
			this.right = NULL_NODE;
		}

		public int height() {
			int lHeight = -1;
			int rHeight = -1;
			if(this == NULL_NODE) {
				return -1;
			}
			
			lHeight = this.left.height() + 1;
			rHeight = this.right.height() + 1;
			
			return (lHeight > rHeight) ? lHeight : rHeight;
		}
		
		public void toArrayList(ArrayList<T> list) {
			if(this == NULL_NODE) {
				return;
			}
			this.left.toArrayList(list);
			list.add(data);
			this.right.toArrayList(list);
		}
		
		public int size() {
			if(this == NULL_NODE) {
				return 0;
			}
			return 1 + left.size() + right.size();
		}
		
		public boolean containsNonBST(T element) {
			if(this == NULL_NODE) {
				return false;
			}
			
			if(this.data.equals(element)) {
				return true;
			}
			
			return left.containsNonBST(element) || right.containsNonBST(element);			
		}

		public T getData() {
			return this.data;
		}

		public BinaryNode getLeft() {
			return this.left;
		}

		public BinaryNode getRight() {
			return this.right;
		}

		// For manual testing
		public void setLeft(BinaryNode left) {
			this.left = left;
		}
		
		public void setRight(BinaryNode right) {
			this.right = right;
		}	
	}

	/**
	 * Iterates through the tree using the i
	 * @author class
	 *
	 */
	public class InefficientIterator implements Iterator<T>{
		private ArrayList<T> list; 
		private int index = -1;
		
		public InefficientIterator() {
			this.list = toArrayList();
		}

		@Override
		public boolean hasNext() {
			return index + 1 < list.size();
		}

		@Override
		public T next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			index++;
			return list.get(index);
		}
	}
	
	/**
	 * Iterates through the tree left, root, right (aka natural order)
	 * @author kimmm
	 *
	 */
	public class InOrderIterator implements Iterator<T>{
		private Stack<BinaryNode> nodes = new Stack<BinaryNode>();
		private BinaryNode parent;
		private int mods;
		
		public InOrderIterator() {
			this.parent = NULL_NODE;
			this.mods = trueMods;
			if(root != NULL_NODE) {
				getAllLeftChilds(root);
			}
		}

		@Override
		public boolean hasNext() {
			return !nodes.isEmpty();
		}

		@Override
		public T next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			if(mods != trueMods) {
				throw new ConcurrentModificationException();
			}
			parent = nodes.pop();
			if(parent.right != NULL_NODE) {
				getAllLeftChilds(parent.right);
			}
			
			return parent.data;
		}
		
		public void remove() {
			if(parent == NULL_NODE) {
				throw new IllegalStateException();
			}
			
			BinarySearchTree.this.remove(parent.data);
			parent = NULL_NODE;
			mods++;
		}
		
		//pushes all the left children of a node onto the stack recursively
		public void getAllLeftChilds(BinaryNode node) {
			nodes.push(node);
			if(node.left != NULL_NODE) {
				getAllLeftChilds(node.left);
			}
		}
		
	}
	
	/**
	 * Iterates through the tree so that it is root, left, right
	 * @author kimmm
	 *
	 */
	public class PreOrderIterator implements Iterator<T>{
		private Stack<BinaryNode> nodes = new Stack<BinaryNode>();
		private BinaryNode parent;
		private int mods;

		public PreOrderIterator() {
			this.parent = NULL_NODE;
			this.mods = trueMods;
			if(root != NULL_NODE) {
				nodes.push(root);
			}
		}
		
		@Override
		public boolean hasNext() {
			return !nodes.isEmpty();
		}

		@Override
		public T next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			if(mods != trueMods) {
				throw new ConcurrentModificationException();
			}
			
			parent = nodes.pop();
			if(parent.right != NULL_NODE) {
				nodes.push(parent.right);
			}
			if(parent.left != NULL_NODE) {
				nodes.push(parent.left);
			}
			return parent.data;
		}
		
		public void remove() {
			if(parent == NULL_NODE) {
				throw new IllegalStateException();
			}		
			BinarySearchTree.this.remove(parent.data);
			parent = NULL_NODE;
			mods++;
		}
		
	}
	
	/**
	 * Wrapper Class for Boolean
	 * @author kimmm
	 *
	 */
	public class MyBoolean{
		private boolean notChanged;
		
		public MyBoolean() {
			this.notChanged = true;
		}
		
		public void hasChanged() {
			this.notChanged = false;
		}
		
		public boolean getBool() {
			return this.notChanged;
		}
	}





	


	 

}
