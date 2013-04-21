/**
 * 
 */
package com.film.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.film.dao.bean.VideoBean;
import com.film.dao.factory.DaoFactory;
import com.film.solr.index.IndexSearchBean;
import com.film.solr.index.SolrWeiboInfoClient;

/**
 * @author xiangning
 *
 */
public class SearchServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2827353044042108696L;

	/* (non-Javadoc)
	 * @see com.film.servlet.BaseServlet#dealRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected String dealRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String keyword = request.getParameter("words");
		JSONObject result = new JSONObject();
		if(null == keyword || keyword.trim().length() == 0){
			result.put("error",-1);
			return result.toString();
		}
		IndexSearchBean search = new IndexSearchBean();
    	search.setKeyword(keyword);
    	int start = 1;
    	String start_str = request.getParameter("start");
    	if(null != start_str)
    		start = Integer.valueOf(start_str);
    	if(start <0)
    		start = 1;
    	int pageNumber = 20;
    	String page_str = request.getParameter("page");
    	if(null != page_str)
    		pageNumber = Integer.valueOf(page_str);
    	search.setStartRow((start-1)*pageNumber);
    	search.setRowNumber(pageNumber);
    	List<Long> list = SolrWeiboInfoClient.getInstance().doSearch(search);
    	long number = SolrWeiboInfoClient.getInstance().getNumfound(search);
    	int totalPage = (int)number/pageNumber;
    	List<VideoBean> tempList = new ArrayList<VideoBean>();
    	for(Long i:list){
    		VideoBean bean = DaoFactory.getInstance().getVideoDAO().getVideoBean(i);
    		if(null != bean)
    			tempList.add(bean);
    	}
    	//int totalNumber = 
    	JSON json = JSONSerializer.toJSON(tempList);
		if(null == json){
			result.put("error", -1);
		}else{
			result.put("error", 0);
			
			result.put("ret", json.toString());
			JSONObject pageInfo = new JSONObject();
			pageInfo.put("activePage",start);
			pageInfo.put("totalNumber", number);
			pageInfo.put("totalPage", totalPage);
			result.put("pageInfo", pageInfo);
		}
		return result.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
