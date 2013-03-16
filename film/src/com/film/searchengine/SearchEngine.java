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
	protected final String updateUrl;
	
	public SearchEngine(String url,String updateUrl){
		this.url = url;
		this.updateUrl = updateUrl;
	}
	
	protected abstract void searchWebSite();
	protected abstract void searchUpdate();
}
