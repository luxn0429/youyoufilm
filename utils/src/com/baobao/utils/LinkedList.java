package com.baobao.utils;

import java.util.NoSuchElementException;



public class LinkedList {

    /**
     * 链表的根节点，用来保持对链表首节点和尾节点的引用
     */
    private LinkedListNode head = new LinkedListNode("head", null, null);
    
    /**
     * 链表的当前容量
     */
    private int size;

    /**
     * 链表的构造函数
     */
    public LinkedList() {
        head.next = head.previous = head;
        size = 0;
    }

    /**
     * 返回链表的首节点
     *
     * @return 
     */
    public LinkedListNode getFirst() {
        LinkedListNode node = head.next;
        if (node == head) {
            return null;
        }
        return node;
    }

    /**
     * 返回链表的尾节点
     *
     * @return 
     */
    public LinkedListNode getLast() {
        LinkedListNode node = head.previous;
        if (node == head) {
            return null;
        }
        return node;
    }
    
    /**
     * 返回链表指定位置处的节点
     * @param index
     * @return 
     */
    public LinkedListNode get(int index) {
    	if(index<0 || index >= size)
    		throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    	LinkedListNode node = head;
    	if(index < (size >> 1)) {
    		for(int i=0;i<=index;i++)
    			node = node.next;
    	}
    	else {
    		for(int i=size;i>index;i--)
    			node = node.previous;
    	}
    	return node;
    }

    /**
     * 向链表头部插入一个节点
     *
     * @param node 
     */
    public LinkedListNode addFirst(LinkedListNode node) {
        node.next = head.next;
        node.previous = head;
        node.previous.next = node;
        node.next.previous = node;
        size++;
        return node;
    }

    /**
     * 向链表头部插入一个节点，通过传入该节点的内容并自动创建该链表节点而实现
     *
     * @param object 节点的内容
     * @return 
     */
    public LinkedListNode addFirst(Object object) {
        LinkedListNode node = new LinkedListNode(object, head.next, head);
        node.previous.next = node;
        node.next.previous = node;
        size++;
        return node;
    }

    /**
     * 向链表尾部插入一个节点
     *
     * @param node 
     */
    public LinkedListNode addLast(LinkedListNode node) {
        node.next = head;
        node.previous = head.previous;
        node.previous.next = node;
        node.next.previous = node;
        size++;
        return node;
    }
    
    /**
     * 向链表尾部插入一个节点，通过传入该节点的内容并自动创建该链表节点而实现
     *
     * @param object 节点的内容
     * @return 
     */
    public LinkedListNode addLast(Object object) {
        LinkedListNode node = new LinkedListNode(object, head, head.previous);
        node.previous.next = node;
        node.next.previous = node;
        size++;
        return node;
    }

    /**
     * 删除第一个节点
     * @return
     */
    public LinkedListNode removeFirst() {
    	return remove(head.next);
    }
    
    /**
     * 删除最后一个节点
     * @return
     */
    public LinkedListNode removeLast() {
    	return remove(head.previous);
    }
    
    /**
     * 删除指定的节点
     * @param node
     * @return
     */
    public LinkedListNode remove(LinkedListNode node) {
    	if(node == head)
    		throw new NoSuchElementException();
    	LinkedListNode result = node;
    	node.remove();
    	node = null;
    	size--;
    	return result;
    }
    
    /**
     * 删除指定位置处的节点
     * @param index
     * @return
     */
    public LinkedListNode remove(int index) {
    	LinkedListNode node = get(index);
    	return remove(node);
    }
    
    /**
     * 清空并重新初始化链表
     */
    public void clear() {
        //清空链表中所有元素及其引用
        LinkedListNode node = getLast();
        while (node != null) {
            node.remove();
            node = getLast();
        }

        //重新初始化链表
        head.next = head.previous = head;
        size = 0;
    }

    /**
     * 返回链表的字符串表示形式，各元素间以逗号分隔
     *
     * @return 
     */
    public String toString() {
        LinkedListNode node = head.next;
        StringBuilder buf = new StringBuilder();
        while (node != head) {
            buf.append(node.toString()).append(", ");
            node = node.next;
        }
        return buf.toString();
    }
	
    /**
     * 返回链表的当前容量
     * @return
     */
    public int getSize() {
    	return size;
    }
}
