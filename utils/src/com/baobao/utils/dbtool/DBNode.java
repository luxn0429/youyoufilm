package com.baobao.utils.dbtool;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.baobao.utils.dbtool.hash.ICustomNode;


public class DBNode implements ICustomNode {
	
	private String DBName;

	public DBNode(String DBName) {
		this.DBName = DBName;
	}
	
	public String getDBName() {
		return this.DBName;
	}
	
	public boolean equals(Object obj) {    
		if (obj instanceof DBNode){
			DBNode dbNode = (DBNode)obj;
			return new EqualsBuilder().append(DBName,dbNode.getDBName()).isEquals();
		} return false;
	} 

	public int hashCode() {       
		return new HashCodeBuilder(17, 37).append(DBName).toHashCode();
	}
	
}
