package graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

public class AdjacencyListGraph<T> extends Graph<T> {
	Map<T, Vertex> keyToVertex;

	private class Vertex {
		T key;
		List<Vertex> successors;
		List<Vertex> predecessors;

		Vertex(T key) {
			this.key = key;
			this.successors = new ArrayList<Vertex>();
			this.predecessors = new ArrayList<Vertex>();
		}
	}

	AdjacencyListGraph(Set<T> keys) {
		this.keyToVertex = new HashMap<T, Vertex>();
		for (T key : keys) {
			Vertex v = new Vertex(key);
			this.keyToVertex.put(key, v);
		}
	}

	@Override
	public int size() {
		return this.keyToVertex.size();
	}

	@Override
	public int numEdges() {
		int count = 0;
		for (T key : keySet()) {
			count += this.keyToVertex.get(key).successors.size();
		}
		return count;
	}

	@Override
	public boolean addEdge(T from, T to) {
		if (!this.keyToVertex.containsKey(from) || !this.keyToVertex.containsKey(to)) {
			throw new NoSuchElementException();
		}
		Vertex fromV = this.keyToVertex.get(from);
		Vertex toV = this.keyToVertex.get(to);
		List<Vertex> succ = fromV.successors;
		List<Vertex> pred = toV.predecessors;
		if (!succ.contains(toV) && !pred.contains(fromV)) {
			succ.add(toV);
			pred.add(fromV);
			return true;
		}

		return false;
	}

	@Override
	public boolean hasVertex(T key) {
		return this.keyToVertex.containsKey(key);
	}

	@Override
	public boolean hasEdge(T from, T to) throws NoSuchElementException {
		if (!this.keyToVertex.containsKey(from) || !this.keyToVertex.containsKey(to)) {
			throw new NoSuchElementException();
		}
		Vertex fromV = this.keyToVertex.get(from);
		Vertex toV = this.keyToVertex.get(to);
		List<Vertex> succ = fromV.successors;
		List<Vertex> pred = toV.predecessors;
		if (succ.contains(toV) && pred.contains(fromV)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean removeEdge(T from, T to) throws NoSuchElementException {
		if (!this.keyToVertex.containsKey(from) || !this.keyToVertex.containsKey(to)) {
			throw new NoSuchElementException();
		}
		Vertex fromV = this.keyToVertex.get(from);
		Vertex toV = this.keyToVertex.get(to);
		List<Vertex> succ = fromV.successors;
		List<Vertex> pred = toV.predecessors;
		if (succ.contains(toV) && pred.contains(fromV)) {
			succ.remove(toV);
			pred.remove(fromV);
			return true;
		}

		return false;
	}

	@Override
	public int outDegree(T key) {
		if (!this.keyToVertex.containsKey(key)) {
			throw new NoSuchElementException();
		}
		return this.keyToVertex.get(key).successors.size();
	}

	@Override
	public int inDegree(T key) {
		if (!this.keyToVertex.containsKey(key)) {
			throw new NoSuchElementException();
		}
		return this.keyToVertex.get(key).predecessors.size();
	}

	@Override
	public Set<T> keySet() {
		return this.keyToVertex.keySet();
	}

	@Override
	public Set<T> successorSet(T key) {
		if (!this.keyToVertex.containsKey(key)) {
			throw new NoSuchElementException();
		}
		Set<T> succ = new HashSet<T>();
		List<Vertex> succL = this.keyToVertex.get(key).successors;
		for (Vertex v : succL) {
			succ.add(v.key);
		}
		return succ;
	}

	@Override
	public Set<T> predecessorSet(T key) {
		if (!this.keyToVertex.containsKey(key)) {
			throw new NoSuchElementException();
		}
		Set<T> pred = new HashSet<T>();
		List<Vertex> predL = this.keyToVertex.get(key).predecessors;
		for (Vertex v : predL) {
			pred.add(v.key);
		}
		return pred;
	}

	@Override
	public Iterator<T> successorIterator(T key) {
		return successorSet(key).iterator();
	}

	@Override
	public Iterator<T> predecessorIterator(T key) {
		return predecessorSet(key).iterator();
	}

	// Milestone 2
	@Override
	public Set<T> stronglyConnectedComponent(T key) {
		if (!this.keySet().contains(key)) {
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
				} else {
					if (pred.contains(sKeys)) {
						scc.add(sKeys);
						pred.addAll(predecessorSet(sKeys));
						newSucc.addAll(successorSet(sKeys));
					} else {
						newSucc.addAll(successorSet(sKeys));
						newSucc.add(sKeys);
					}
					//newSucc.addAll(successorSet(sKeys));
				}
			}
			if (newSucc.size() == succ.size()) {
				break;
			}
			succ = newSucc;
		}
		return scc;
	}

	// Milestone 2
	@Override
	public List<T> shortestPath(T startLabel, T endLabel) {
		if (!this.keyToVertex.containsKey(startLabel) || !this.keyToVertex.containsKey(endLabel)) {
			throw new NoSuchElementException();
		}
		
		Set<T> visited = new HashSet<>();
		LinkedList<ArrayList<T>> allPossPaths = new LinkedList<>();
		ArrayList<T> currentPath = new ArrayList<T>();

		currentPath.add(startLabel);

		while (true) {
			int lastIndex = currentPath.size() - 1;
			if (currentPath.get(lastIndex).equals(endLabel)) {
				return currentPath;
			}

			visited.add(currentPath.get(lastIndex));
			for (T key : successorSet(currentPath.get(lastIndex))) {
				if (!visited.contains(key)) {
					ArrayList<T> temp = (ArrayList<T>) currentPath.clone();
					temp.add(key);
					if(visited.contains(startLabel) && visited.contains(endLabel)) {
						return temp;
					}
					allPossPaths.add(temp);
				}
			}
			if (allPossPaths.isEmpty()) {
				return null;
			}

			currentPath = allPossPaths.remove();
			

			if (visited.size() == size()) {
				break;
			}
		}
		return allPossPaths.get(0);
	}
}
