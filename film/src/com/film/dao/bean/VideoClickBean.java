/**
 * 
 */
package com.film.dao.bean;

/**
 * @author xiangning
 *
 */
public class VideoClickBean {
	private long videoId;
	private int totalClick;
	private int weekClick;
	private int monthClick;
	public long getVideoId() {
		return videoId;
	}
	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}
	public int getTotalClick() {
		return totalClick;
	}
	public void setTotalClick(int totalClick) {
		this.totalClick = totalClick;
	}
	public int getWeekClick() {
		return weekClick;
	}
	public void setWeekClick(int weekClick) {
		this.weekClick = weekClick;
	}
	public int getMonthClick() {
		return monthClick;
	}
	public void setMonthClick(int monthClick) {
		this.monthClick = monthClick;
	}
}
