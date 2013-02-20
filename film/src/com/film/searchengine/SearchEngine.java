/**
 * 
 */
package com.film.searchengine;


/**
 * @author luxianginng
 *
 */
public abstract class SearchEngine {
	////要搜索的网站首页
	protected final String url;
	
	public SearchEngine(String url){
		this.url = url;
	}
	
	protected abstract void parseHome();
}
