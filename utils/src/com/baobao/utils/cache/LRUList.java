package com.baobao.utils.cache;
import java.io.Serializable;
import java.util.LinkedList;
/**
 * 最近最少使用列表
 * @author 卢相宁
 *
 * @param <E>
 */
public final class LRUList<E> extends LinkedList<E> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1923911640013903063L;
	private final int  mMaxSize;
	
	public LRUList(int pMaxSize) {mMaxSize = pMaxSize;}
	
	public final E put(E e){
		E r = null;
		if(this.size() >= mMaxSize){
			r = this.removeLast();
		}
		this.addFirst(e);
		return r;
	}
	
	public static void main(String[] args){
		LRUList<Long> array = new LRUList<Long>(200);
		for(long i=0L;i<1000L;i++){
			array.put(i);
			if(i%100 == 0)
				System.out.println(array.size());
		}
	}
}
