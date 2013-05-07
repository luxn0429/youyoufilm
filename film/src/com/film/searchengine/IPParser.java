/**
 * 
 */
package com.film.searchengine;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.film.dao.factory.DaoFactory;

/**
 * @author xiangning
 *
 */
public class IPParser extends SearchEngine {
	
	public IPParser(String url, String updateUrl) {
		super(url, updateUrl);
	}
	private String encode = "utf-8";
	/* (non-Javadoc)
	 * @see com.film.searchengine.SearchEngine#searchWebSite()
	 */
	@Override
	protected void searchWebSite() {
		String html = SearchWebPageUtil.getUrlContent(this.url,encode);
		Document doc = Jsoup.parse(html);
		Elements ipbox = doc.getElementsByAttributeValue("class", "m_box2");
		if(ipbox.size() ==0){
			return ;
		}
		Element ipelement = ipbox.first();
		
		Element ul = ipelement.getElementsByTag("ul").first();
		Elements li = ul.getElementsByTag("li");
		if(li.size() == 0)
			return;
		
		Element alink = li.get(0).getElementsByTag("a").first();
		String proxyHttp = alink.attr("href");
		
		Set<String> proxy = getProxy(proxyHttp);
		for(String ip:proxy){
			DaoFactory.getInstance().getIPDao().insert(ip);
		}
		Logger.getLogger(this.getClass()).info("has download ips:"+proxy.size());
		////重新加载可用的IP
		IPCache.getInstance().loadIP();
	}
	
	private Set<String> getProxy(String href){
		Set<String> result = new HashSet<String>();
		
		String html = SearchWebPageUtil.getUrlContent(href,encode);
		Document doc = Jsoup.parse(html);
		
		Elements ipdiv = doc.getElementsByAttributeValue("class", "cont_font");
		Element span = ipdiv.first().getElementsByTag("span").first();
		String content = span.html();
		content =content.replaceAll("<([^>]*)>", "\r\n");
		System.out.println(content);
		String[] ipStr = content.split("\r\n");
		for(int i=0;i<ipStr.length;i++){
			String[] ips = ipStr[i].split("@");
			result.add(ips[0].trim());
			System.out.println(ips[0].trim());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.film.searchengine.SearchEngine#searchUpdate()
	 */
	@Override
	protected void searchUpdate() {
		Logger.getLogger(this.getClass()).info("start update ip");
		this.searchWebSite();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IPParser parser = new IPParser("http://www.youdaili.cn","");
		parser.searchWebSite();
	}

}
