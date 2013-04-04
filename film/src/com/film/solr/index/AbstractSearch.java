package com.film.solr.index;
/**
 * @author luxianginng
 * 
 * 抽象search，提供基本查询函数
 */
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class AbstractSearch {
	private long elapsedTime;
	private int qTime;
	////solr address
	protected String url = null;
	
	protected AbstractSearch(String url){
		this.url = url;
	}
	
	protected SolrDocumentList search(SolrQuery solrQuery){
		QueryResponse response;
		try {
			response = SolrServerFactory.getCommonsHttpSolrServer(url).query(solrQuery);
			this.elapsedTime = response.getElapsedTime();
			this.qTime = response.getQTime();
			return response.getResults();
		} catch (SolrServerException e) {
			Logger.getLogger(this.getClass()).error("Solr server error when query:" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 得到满足条件的某一页
	 * 
	 * @param queryRequest			查询条件
	 * @param start					开始行
	 * @param rows					要获得从开始起的一共几行
	 * @return
	 */
	protected SolrDocumentList search(String queryRequest, int start, int rows) {
		
		if (queryRequest == null || queryRequest.equals(""))
			queryRequest = "*:*";
		Logger.getLogger(this.getClass()).info(queryRequest);
		SolrQuery solrQuery = new SolrQuery(queryRequest);
		solrQuery.setRows(rows);
		solrQuery.setStart(start);
		return search(solrQuery);
	}

	/**
	 * 用于得到分页使用的总数量
	 * 
	 * @param queryRequest
	 * @return
	 */
	public long getNumfound(String queryRequest) {
		SolrServer solrServer = SolrServerFactory.getCommonsHttpSolrServer(url);
		if (queryRequest == null || queryRequest.equals(""))
			queryRequest = "*:*";
		SolrQuery solrQuery = new SolrQuery(queryRequest);
		try {
			solrQuery.setRows(0);
			QueryResponse response = solrServer.query(solrQuery);
			return response.getResults().getNumFound();
		} catch (SolrServerException e) {
			Logger.getLogger(this.getClass()).error("Solr server error when query:" + queryRequest + ":\n" + e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public int getQTime() {
		return qTime;
	}

	public void optimize() {
		try {
			SolrServer solrClient = SolrServerFactory.getCommonsHttpSolrServer(url);
			solrClient.optimize();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteByQuery(String query) {
		try {
			Logger.getLogger(this.getClass()).info("delete query start delete:"+url);
			SolrServer solrServer = SolrServerFactory.getCommonsHttpSolrServer(url);
			Logger.getLogger(this.getClass()).info("delete query get delete:"+url);
			solrServer.deleteByQuery(query);
			Logger.getLogger(this.getClass()).info("delete query end delete:"+url);
			//System.out.println(response.getStatus());
		} catch (SolrServerException e) {
			Logger.getLogger(this.getClass()).error("Solr server error when query:" + query + ":\n" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
