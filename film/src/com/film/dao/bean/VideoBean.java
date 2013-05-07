package com.film.dao.bean;


	/**
	*此类由MySQLToBean工具自动生成
	*存储电影或电视剧信息
	*@author luxiangning
	*@since 2012-10-21 16:42:53
	*/

public class VideoBean{
	//电影或电视剧ID
	private long id;
	//名字
	private String name;
	//电视剧电影类型：喜剧、恐怖等可以是多种类型
	private int type;
	//国别
	private int country;
	//出品日期
	private int pubdate;
	//字幕
	private int caption;
	//语言
	private int language;
	//入库时间
	private int updateTime;
	//主演
	private String performer;
	//海报
	private String poster;
	//简介
	private String introduction;
	//更新状态
	private int state;
	//格式：DVD
	private int format;
	//清晰度
	private String clarity;
	///分类 1 电影 2 电视剧 3综艺娱乐
	private int classified; ////不用

	private String seriousIntro;
	
	public String getSeriousIntro() {
		return seriousIntro;
	}
	public void setSeriousIntro(String seriousIntro) {
		this.seriousIntro = seriousIntro;
	}
	public int getClassified() {
		return classified;
	}
	public void setClassified(int classified) {
		this.classified = classified;
	}
	public long getId(){
		return this.id;
	}
	public void setId(long id){
		this.id=id;
	}
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
	}
	public int getType(){
		return this.type;
	}
	public void setType(int type){
		this.type=type;
	}
	public int getCountry(){
		return this.country;
	}
	public void setCountry(int country){
		this.country=country;
	}
	public int getPubdate(){
		return this.pubdate;
	}
	public void setPubdate(int pubdate){
		this.pubdate=pubdate;
	}
	public int getCaption(){
		return this.caption;
	}
	public void setCaption(int caption){
		this.caption=caption;
	}
	public int getLanguage(){
		return this.language;
	}
	public void setLanguage(int language){
		this.language=language;
	}
	public int getUpdateTime(){
		return this.updateTime;
	}
	public void setUpdateTime(int updateTime){
		this.updateTime=updateTime;
	}
	public String getPerformer(){
		return this.performer;
	}
	public void setPerformer(String performer){
		this.performer=performer;
	}
	public String getPoster(){
		return this.poster;
	}
	public void setPoster(String poster){
		this.poster=poster;
	}
	public String getIntroduction(){
		return this.introduction;
	}
	public void setIntroduction(String introduction){
		this.introduction=introduction;
	}
	public int getState(){
		return this.state;
	}
	public void setState(int state){
		this.state=state;
	}
	public int getFormat(){
		return this.format;
	}
	public void setFormat(int format){
		this.format=format;
	}
	public String getClarity(){
		return this.clarity;
	}
	public void setClarity(String clarity){
		this.clarity=clarity;
	}

}