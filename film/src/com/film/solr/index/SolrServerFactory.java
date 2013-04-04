
package com.film.solr.index;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
/**
 * 
 * @author luxianginng
 *	solrserver工程类
 */
public class SolrServerFactory {
	
	private static Map<String, SolrServer> solrServerMap = Collections.synchronizedMap(new HashMap<String, SolrServer>());
	
	private SolrServerFactory(){
		
	}
	/**
	 * 通过查询URL得到一个solrserver
	 * 
	 * @param SOLR_URL				查询连接
	 * @return
	 */
	public static SolrServer getCommonsHttpSolrServer(final String SOLR_URL){
		SolrServer solrServer = null;
		if (!solrServerMap.containsKey(SOLR_URL)){
			try {
				solrServer = new CommonsHttpSolrServer(SOLR_URL);
				if (solrServer != null){
					solrServerMap.put(SOLR_URL, solrServer);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return solrServerMap.get(SOLR_URL);
	}
	
}
