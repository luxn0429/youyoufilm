package com.baobao.utils.cache;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class LRUSet<E> extends AbstractSet<E> implements Set<E>, Cloneable,java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5210092101757049841L;

	@Override
	public Iterator<E> iterator() {
		return cache.keySet().iterator();
	}

	@Override
	public int size() {
		return cache.size();
	}

	private static final Integer IGNORE = new Integer(0);

	private final LRUMap<E, Integer> cache;

	/**
	 * Default constructor for an LRU Cache The default capacity is 10000
	 */
	public LRUSet() {
		this(0, 10000, 0.75f, true);
	}

	/**
	 * Constructs a LRUCache with a maximum capacity
	 * 
	 * @param maximumCacheSize
	 */
	public LRUSet(int maximumCacheSize) {
		this(0, maximumCacheSize, 0.75f, true);
	}

	/**
	 * Constructs an empty <tt>LRUCache</tt> instance with the specified
	 * initial capacity, maximumCacheSize,load factor and ordering mode.
	 * 
	 * @param initialCapacity
	 *            the initial capacity.
	 * @param maximumCacheSize
	 * @param loadFactor
	 *            the load factor.
	 * @param accessOrder
	 *            the ordering mode - <tt>true</tt> for access-order,
	 *            <tt>false</tt> for insertion-order.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative or the load factor is
	 *             non-positive.
	 */

	public LRUSet(int initialCapacity, int maximumCacheSize, float loadFactor,
			boolean accessOrder) {
		this.cache = new LRUMap<E, Integer>(initialCapacity, maximumCacheSize,
				loadFactor, accessOrder);
	}

	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public boolean contains(Object o) {
		return cache.containsKey(o);
	}

	public boolean add(E o) {
		return cache.put(o, IGNORE) == null;
	}

	public boolean remove(Object o) {
		return cache.remove(o) == IGNORE;
	}

	public void clear() {
		cache.clear();
	}

}
