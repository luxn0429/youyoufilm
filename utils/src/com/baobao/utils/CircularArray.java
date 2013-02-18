package com.baobao.utils;

/**
 * 环形数组
 * 
 * @since 2009-12-10
 */
public class CircularArray {

	//head和tail分别标记首元素和尾元素在数组中的当前位置
	private int head , tail , size;
	
	private Object[] elements;
	
	private final static int DEFAULT_CAPACITY = 10000;
	
	private int capacity;
	
	public CircularArray() {
		head = tail = size = 0;
		elements = new Object[DEFAULT_CAPACITY];
		this.capacity = DEFAULT_CAPACITY;
	}
	
	public CircularArray(int capacity) {
		head = tail = size = 0;
		elements = new Object[capacity];
		this.capacity = capacity;
	}
	
	/**
	 * 添加元素到数组头部
	 * @param o
	 */
	public boolean addFirst(Object o) {
		if(o == null) 
			return false;
		if(size == 0) {
			elements[head] = o;
			size++;
		}
		else if(size == elements.length) {
			head = (head - 1 + elements.length) % elements.length;
			tail = (tail - 1 + elements.length) % elements.length;
			elements[head] = o;
		}
		else {
			head = (head - 1 + elements.length) % elements.length;
			elements[head] = o;
			size++;
		}
		return true;
	}
	
	/**
	 * 添加元素到数组尾部
	 * @param o
	 */
	public boolean addLast(Object o) {
		if(o == null)
			return false;
		if(size == 0) {
			elements[tail] = o;
			size++;
		}
		else if(size == elements.length) {
			head = (head + 1) % elements.length;
			tail = (tail + 1) % elements.length;
			elements[tail] = o;
		}
		else {
			tail = (tail + 1) % elements.length;
			elements[tail] = o;
			size++;
		}
		return true;
	}
	
	/**
	 * 删除数组头部的元素
	 * @return
	 */
	public Object removeFirst() {
		if(size == 0)
			return null;
		Object result = elements[head];
		elements[head] = null;
		if(size > 1)
			head = (head + 1) % elements.length;
		size--;
		return result;
	}
	
	/**
	 * 删除数组尾部的元素
	 * @return
	 */
	public Object removeLast() {
		if(size == 0)
			return null;
		Object result = elements[tail];
		elements[tail] = null;
		if(size > 1)
			tail = (tail -1 + elements.length) % elements.length;
		size--;
		return result;
	}
	
	/**
	 * 将指定位置的已有元素替换为新的元素
	 * @param index
	 * @param newValue
	 * @return
	 */
	public Object set(int index, Object newValue) {
		if(newValue == null || index < 0 || index >= size)
			return null;
		Object oldValue = elements[convert(index)];
		elements[convert(index)] = newValue;
		return oldValue;
	}
	
	/**
	 * 删除从头数起第index个元素
	 * @param index
	 * @return
	 */
	public Object remove(int index) {
		if(size == 0 || index < 0 || index >= size)
			return null;
		int current = convert(index);
		Object result = elements[current];
		elements[current] = null;
		if(index < (size >> 1)) {
			while(index > 0) {
				elements[convert(index)] = elements[convert(index-1)];
				index--;				                             
			}
			if(size > 1)
				head = (head + 1) % elements.length;
		} 
		else {
			while(index < size-1) {
				elements[convert(index)] = elements[convert(index+1)];
				index++;
			}
			if(size > 1)
				tail = (tail - 1 +elements.length) % elements.length;
		}
		size--;
		return result;
	}
	
	/**
	 * 获得数组的首元素
	 * @return
	 */
	public Object getFirst() {
		if(size == 0)
			return null;
		return elements[head]; 
	}
	
	/**
	 * 获得数组的尾元素
	 * @return
	 */
	public Object getLast() {
		if(size == 0)
			return null;
		return elements[tail]; 
	}
	
	/**
	 * 获得从头数起第index个元素
	 * @param index
	 * @return
	 */	
	public Object get(int index) {
		if(size == 0 || index < 0 || index >= size)
			return null;
		return elements[convert(index)];
	}
	 
	/**
	 * 获得从第start个元素起的连续pageSize个元素（从头数起）
	 * @param start
	 * @param pageSize
	 * @return
	 */	
	public Object[] get(int start, int pageSize) {
		if(size == 0 || start < 0 || start >= size)
			return null;
		Object[] result = null;
		if(size - start >= pageSize) {
			result = new Object[pageSize];
		}
		else {
			result = new Object[size - start];
		}
		for(int i = 0; i < result.length; i++) {
			result[i] = elements[convert(start + i)];
		}
		return result;
	}
	
	/**
	 * 判断环形数组是否为空
	 * @return
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	
	/**
	 * 判断环形数组是否已满
	 * @return
	 */
	public boolean isFull() {
		return size == elements.length;
	}
	
	public void clear() {
		for(int i = 0; i < elements.length; i++) {
			elements[i] = null;
		}
	}
	
	/**
	 * 获取首元素所在的位置
	 * @return
	 */
	public int getHeadIndex() {
		return head;
	}
	
	/**
	 * 获取尾元素所在的位置
	 * @return
	 */
	public int getTailIndex() {
		return tail;
	}
	
	/**
	 * 返回数组中元素的个数
	 * @return
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * 返回数组的最大容量
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * 返回整个环形数组
	 * @return
	 */
	public Object[] getElements() {
		return elements;
	}
	
	/**
	 * 返回环形数组的字符串形式表示
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			sb.append(elements[convert(i)].toString());
		}
		return sb.toString();
	}
	
	/**
	 * 把环形数组转化为标准数组
	 * @return
	 */
	public Object[] toArray() {
		Object[] result = new Object[size];
		for(int i = 0; i < size; i++) {
			result[i] = elements[convert(i)];
		}
		return result;
	}
	
	public int indexOf(Object o) {
		if(o == null)
			return -1;
		for(int i = 0; i < size; i++) {
			if(o.equals(elements[convert(i)]))
				return i;
		}
		return -1;
	}
	
	/**
	 * 将index转化为环形数组中的实际位置
	 * @param index
	 * @return
	 */
	private int convert(int index) {
		return (index + head) % elements.length;
	}
	
}
