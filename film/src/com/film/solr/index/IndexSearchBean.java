/**
 *@Title: IndexSearchBean.java
 *@Description:	TODO
 *@author luxiangning luxn0429@gmail.com
 *@Date:2012-7-11 下午2:13:19
 *@version	1.0
 **/
package com.film.solr.index;


/**
 *  Class Name: SearchWeiboBean.java
 *  Function:用于向搜索引擎中传入搜索字段，不用检索 String == null  int==false date == null
 *  Modifications:   
 *  
 */

public class IndexSearchBean {
	
	private int country = -1;
	private int language = -1;
	private int type = -1;
	private int startPubDate = -1;
	private int endPubDate = -1;
	
	private String keyword;
   
    private int startRow = 0;
    private int rowNumber      = 20;
	public int getCountry() {
		return country;
	}
	public void setCountry(int country) {
		this.country = country;
	}
	public int getLanguage() {
		return language;
	}
	public void setLanguage(int language) {
		this.language = language;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStartPubDate() {
		return startPubDate;
	}
	public void setStartPubDate(int startPubDate) {
		this.startPubDate = startPubDate;
	}
	public int getEndPubDate() {
		return endPubDate;
	}
	public void setEndPubDate(int endPubDate) {
		this.endPubDate = endPubDate;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}


  
}
