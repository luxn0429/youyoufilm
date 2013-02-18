package com.film.dao.bean;


	/**
	*此类由MySQLToBean工具自动生成
	*备注(数据表的comment字段)：无备注信息
	*@author luxiangning
	*@since 2012-10-21 16:42:53
	*/

public class VolumeBean{
	private long id;
	//视频连接
	private String url;
	//视频属于哪个电影或电视剧
	private long belongto;
	//电影集
	private String volume;
	//使用的播放方式
	private int player;
	//本集介绍，可以为空
	private String description;

	public long getId(){
		return this.id;
	}
	public void setId(long id){
		this.id=id;
	}
	public String getUrl(){
		return this.url;
	}
	public void setUrl(String url){
		this.url=url;
	}
	public long getBelongto(){
		return this.belongto;
	}
	public void setBelongto(long belongto){
		this.belongto=belongto;
	}
	public String getVolume(){
		return this.volume;
	}
	public void setVolume(String volume){
		this.volume=volume;
	}
	public int getPlayer(){
		return this.player;
	}
	public void setPlayer(int player){
		this.player=player;
	}
	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description=description;
	}

}