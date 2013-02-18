package com.baobao.utils;

public class LinkedListNode {

	public LinkedListNode previous;
	
	public LinkedListNode next;
	
	public Object object;
	
	public long timestamp;
	
	/**
	 * 链表节点的构造函数
	 * @param object 节点自身的数据内容
	 * @param next 链表中后一个节点的引用
	 * @param previous 链表中前一个节点的引用
	 */
	public LinkedListNode(Object object, LinkedListNode next, LinkedListNode previous) {
		this.object = object;
		this.next = next;
		this.previous = previous;
	}
	
	/**
	 * 将此节点从它所在的链表中删除
	 */
	public void remove() {
		previous.next = next;
		next.previous = previous;
	}
	
	public String toString() {
		return object == null ? "null" : object.toString();
	}
	
}
