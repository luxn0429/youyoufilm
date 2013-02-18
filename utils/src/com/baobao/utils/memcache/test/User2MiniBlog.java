package com.baobao.utils.memcache.test;

import java.io.Serializable;

/**
 * 用户与发布的日志对应表,包括迷你博客和普通博客
 * @author 卢相宁
 *
 */
public class User2MiniBlog implements Comparable<User2MiniBlog>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1072379645918344217L;
	private long blogId;
	private int createDate;
	private int gmbFrom; //微博来源
	private int gmbType;//微博分类
	
	public long getBlogId() {
		return blogId;
	}
	public void setBlogId(long blogId) {
		this.blogId = blogId;
	}
	public int getCreateDate() {
		return createDate;
	}
	public void setCreateDate(int createDate) {
		this.createDate = createDate;
	}
	
	public int getGmbFrom()
	{
		return gmbFrom;
	}
	public void setGmbFrom(int gmbFrom)
	{
		this.gmbFrom = gmbFrom;
	}
	public int getGmbType()
	{
		return gmbType;
	}
	public void setGmbType(int gmbType)
	{
		this.gmbType = gmbType;
	}
	@Override
	public int compareTo(User2MiniBlog o) {
		
		return Integer.valueOf(this.createDate).compareTo(o.createDate);
	}
}
