/**
 * 
 */
package de.lightningbug.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * TODO
 * 
 * @author Sebastian Kirchner
 * 
 */
public class HashArray implements List<Map<?, ?>> {

	private List<Map<?, ?>> hashes = null;

	/**
	 * TODO
	 * @param potentialHashArray
	 * @throws NoHashArrayException
	 */
	public HashArray(final Object potentialHashArray) throws NoHashArrayException {
		super();
		if (potentialHashArray == null || !potentialHashArray.getClass().isArray()) {
			throw new NoHashArrayException();
		}
		this.setHashes((Object[]) potentialHashArray);
	}

	/**
	 * TODO
	 * @param potentialHashArray
	 * @throws NoHashArrayException
	 */
	public HashArray(final Object[] potentialHashArray) throws NoHashArrayException {
		super();
		this.setHashes(potentialHashArray);
	}

	/**
	 * TODO
	 * 
	 * @param potentialHashArray
	 * @throws NoHashArrayException
	 */
	private void setHashes(final Object[] potentialHashArray) throws NoHashArrayException {
		if (potentialHashArray == null || potentialHashArray.length == 0) {
			throw new NoHashArrayException();
		}
		this.hashes = new ArrayList<Map<?, ?>>(potentialHashArray.length);
		for (final Object o : potentialHashArray) {
			if (o instanceof Map<?, ?>) {
				this.hashes.add((Map<?, ?>) o);
			} else {
				throw new NoHashArrayException();
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @author Sebastian Kirchner
	 * 
	 */
	public static class NoHashArrayException extends Exception {

		public NoHashArrayException() {
			super("The given array is not an array of map objects");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Map<?, ?> element) {
		this.hashes.add(index, element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Map<?, ?> e) {
		return this.hashes.add(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends Map<?, ?>> c) {
		return this.hashes.addAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends Map<?, ?>> c) {
		return this.hashes.addAll(index, c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() {
		this.hashes.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.hashes.contains(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return this.hashes.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return this.hashes.equals(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#get(int)
	 */
	public Map<?, ?> get(int index) {
		return this.hashes.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.hashes.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return this.hashes.indexOf(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return this.hashes.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#iterator()
	 */
	public Iterator<Map<?, ?>> iterator() {
		return this.hashes.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return this.hashes.lastIndexOf(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<Map<?, ?>> listIterator() {
		return this.hashes.listIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<Map<?, ?>> listIterator(int index) {
		return this.hashes.listIterator(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(int)
	 */
	public Map<?, ?> remove(int index) {
		return this.hashes.remove(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.hashes.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return this.hashes.removeAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return this.hashes.retainAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Map<?, ?> set(int index, Map<?, ?> element) {
		return this.hashes.set(index, element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#size()
	 */
	public int size() {
		return this.hashes.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	public List<Map<?, ?>> subList(int fromIndex, int toIndex) {
		return this.hashes.subList(fromIndex, toIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return this.hashes.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return this.hashes.toArray(a);
	}

}
