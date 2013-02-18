package com.film.dao.bean;


	/**
	*此类由MySQLToBean工具自动生成
	*备注(数据表的comment字段)：无备注信息
	*@author luxiangning
	*@since 2012-10-21 16:50:32
	*/

public class VideotovolumeBean{
	private int id;
	private long videoId;
	private long volumeId;

	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id=id;
	}
	public long getVideoId(){
		return this.videoId;
	}
	public void setVideoId(long videoId){
		this.videoId=videoId;
	}
	public long getVolumeId(){
		return this.volumeId;
	}
	public void setVolumeId(long volumeId){
		this.volumeId=volumeId;
	}

}