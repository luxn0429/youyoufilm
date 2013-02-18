package com.film.dao.bean;


	/**
	*此类由MySQLToBean工具自动生成
	*备注(数据表的comment字段)：无备注信息
	*@author luxiangning
	*@since 2012-10-21 16:42:53
	*/

public class VideoformatBean{
	private int id;
	private String name;
	private String fullName;

	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id=id;
	}
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getFullName(){
		return this.fullName;
	}
	public void setFullName(String fullName){
		this.fullName=fullName;
	}

}