/**
 * 
 */
package com.film.dao.bean;

/**
 * @author luxianginng
 *
 */
public class VideoFilter {
	///视频类型
	private int type = -1;
	///国别
	private int country = -1;
	///发布时间（开始）
	private int startPubDate = -1;
	////发布时间(结束)
	private int endPubDate = -1;
	///语言
	private int languate = -1;
	///开始行
	private int startLine = -1;
	
	///每页显示数量
	private int pageNumber;
	
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCountry() {
		return country;
	}
	public void setCountry(int country) {
		this.country = country;
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
	public int getLanguate() {
		return languate;
	}
	public void setLanguate(int languate) {
		this.languate = languate;
	}
	
}
