package graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class AdjacencyMatrixGraph<T> extends Graph<T> implements Iterable<T> {
	Map<T, Integer> keyToIndex;
	List<T> indexToKey;
	int[][] matrix;

	AdjacencyMatrixGraph(Set<T> keys) {
		int size = keys.size();
		this.keyToIndex = new HashMap<T, Integer>();
		this.indexToKey = new ArrayList<T>();
		this.matrix = new int[size][size];
		int i = 0;
		for(T key : keys) {
			this.keyToIndex.put(key, i);
			this.indexToKey.add(i,key);
			i++;
		}
	}

	@Override
	public int size() {
		return this.keyToIndex.size();
	}

	@Override
	public int numEdges() {
		int count = 0;
		for (int r = 0; r < this.matrix.length; r++) {
			for (int c = 0; c < this.matrix.length; c++) {
				if (this.matrix[r][c] == 1) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public boolean addEdge(T from, T to) {
		if (!this.keyToIndex.containsKey(from) || !this.keyToIndex.containsKey(to)) {
			throw new NoSuchElementException();
		}
		int row = this.keyToIndex.get(from);
		int col = this.keyToIndex.get(to);
		if (!hasEdge(from, to)) {
			this.matrix[row][col] = 1;
			return true;
		}
		return false;
	}

	@Override
	public boolean hasVertex(T key) {
		return keyToIndex.containsKey(key);
	}

	@Override
	public boolean hasEdge(T from, T to) throws NoSuchElementException {
		if (!this.keyToIndex.containsKey(from) || !this.keyToIndex.containsKey(to)) {
			throw new NoSuchElementException();
		}
		int row = this.keyToIndex.get(from);
		int col = this.keyToIndex.get(to);
		if(matrix[row][col] == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean removeEdge(T from, T to) throws NoSuchElementException {
		if (!this.keyToIndex.containsKey(from) || !this.keyToIndex.containsKey(to)) {
			throw new NoSuchElementException();
		}
		int row = this.keyToIndex.get(from);
		int col = this.keyToIndex.get(to);
		if (hasEdge(from, to)) {
			this.matrix[row][col] = 0;
			return true;
		}
		return false;
	}

	@Override
	public int outDegree(T key) {
		if (!this.keyToIndex.containsKey(key)) {
			throw new NoSuchElementException();
		}
		return successorSet(key).size();
	}

	@Override
	public int inDegree(T key) {
		if (!this.keyToIndex.containsKey(key)) {
			throw new NoSuchElementException();
		}
		return predecessorSet(key).size();
	}

	@Override
	public Set<T> keySet() {
		return this.keyToIndex.keySet();
	}

	@Override
	public Set<T> successorSet(T key) {
		if (!this.keyToIndex.containsKey(key)) {
			throw new NoSuchElementException();
		}
		Set<T> succSet = new HashSet<T>();
		int row = this.keyToIndex.get(key);
		for (int c = 0; c < this.matrix.length; c++) {
			if (this.matrix[row][c] == 1) {
				succSet.add(this.indexToKey.get(c));
			}
		}

		return succSet;
	}

	@Override
	public Set<T> predecessorSet(T key) {
		if (!this.indexToKey.contains(key)) {
			throw new NoSuchElementException();
		}
		Set<T> predSet = new HashSet<T>();
		int col = this.keyToIndex.get(key);
		for (int r = 0; r < this.matrix.length; r++) {
			if (this.matrix[r][col] == 1) {
				predSet.add(this.indexToKey.get(r));
			}
		}
		return predSet;
	}

	@Override
	public Iterator<T> successorIterator(T key) {
		if (!this.indexToKey.contains(key)) {
			throw new NoSuchElementException();
		}
		return new SuccessorIterator(this.matrix, this.keyToIndex.get(key));
	}

	@Override
	public Iterator<T> predecessorIterator(T key) {
		if (!this.indexToKey.contains(key)) {
			throw new NoSuchElementException();
		}
		return new PredecessorIterator(this.matrix, this.keyToIndex.get(key));
	}

	// milestone 2
	@Override
	public Set<T> stronglyConnectedComponent(T key) {
		if (!this.indexToKey.contains(key)) {
			throw new NoSuchElementException();
		}

		Set<T> succ = successorSet(key);
		Set<T> pred = predecessorSet(key);
		pred.add(key);
		Set<T> scc = new HashSet<>();
		scc.add(key);
		while (true) {
			Set<T> newSucc = new HashSet<>();
			for (T sKeys : succ) {
				if (scc.contains(sKeys)) {
					// do nothing
				}
				else if (pred.contains(sKeys)) {
					scc.add(sKeys);
					pred.addAll(predecessorSet(sKeys));
					newSucc.addAll(successorSet(sKeys));
				} else {
					newSucc.addAll(successorSet(sKeys));
					newSucc.add(sKeys);
				}
			}
			if (newSucc.size() == succ.size()) {
				break;
			}
			succ = newSucc;
		}
		return scc;
	}

	// milestone 2
	@Override
	public List<T> shortestPath(T startLabel, T endLabel) {
		if (!this.keyToIndex.containsKey(startLabel) || !this.keyToIndex.containsKey(endLabel)) {
			throw new NoSuchElementException();
		}

		ArrayList<T> visited = new ArrayList<T>();
		ArrayList<ArrayList<T>> allPossPaths = new ArrayList<>();
		ArrayList<T> currentPath = new ArrayList<T>();

		visited.add(startLabel);
		currentPath.add(startLabel);
		while (true) {
			for (T key : successorSet(visited.get(visited.size() - 1))) {
				if (!visited.contains(key)) {
					ArrayList<T> temp = new ArrayList<T>();
					temp = (ArrayList<T>) currentPath.clone();
					temp.add(key);
					allPossPaths.add(temp);
				}
			}

			if(allPossPaths.isEmpty()) {
				return null;
			}
			currentPath = allPossPaths.remove(0);
			if(currentPath.contains(startLabel) && currentPath.contains(endLabel)) {
				return currentPath;
			}
			visited.add(currentPath.get(currentPath.size() - 1));

			if (visited.size() == size()) {
				if(!allPossPaths.get(0).contains(endLabel)) {
					return null;
				}
				break;
			}
		}

		return allPossPaths.get(0);
	}

	public class SuccessorIterator implements Iterator<T> {
		private int[][] matrix;
		private int indexOfKey;
		private int size;
		private int index;

		public SuccessorIterator(int[][] matrix, int indexOfKey) {
			this.matrix = matrix;
			this.indexOfKey = indexOfKey;
			this.size = this.matrix.length;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			for (int i = this.index; i < this.size; i++) {
				if (this.matrix[indexOfKey][i] == 1) {
					this.index = i;
					return true;
				}
			}
			return false;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				return null;
			} else {
				T next = indexToKey.get(this.index);
				this.index += 1;
				return next;
			}
		}

	}

	public class PredecessorIterator implements Iterator<T> {
		private int[][] matrix;
		private int indexOfKey;
		private int size;
		private int index;

		public PredecessorIterator(int[][] matrix, int indexOfKey) {
			this.matrix = matrix;
			this.indexOfKey = indexOfKey;
			this.size = this.matrix.length;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			for (int i = this.index; i < this.size; i++) {
				if (this.matrix[i][indexOfKey] == 1) {
					this.index = i;
					return true;
				}
			}
			return false;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				return null;
			} else {
				T next = indexToKey.get(this.index);
				this.index += 1;
				return next;
			}
		}

	}

	@Override
	public Iterator<T> iterator() {
		return null;
	}

}
