/**
 * 
 */
package com.film.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.film.dao.bean.VideoBean;
import com.film.dao.bean.VideoFilter;
import com.film.dao.factory.DaoFactory;

/**
 * @author luxianginng
 *
 */
public class SearchVideoServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8243323153723976949L;

	@Override
	protected String dealRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		////国家
		String country = request.getParameter("country");
		////类型
		String type = request.getParameter("type");
		///语言
		String language = request.getParameter("language");
		////发行时间
		String startTime = request.getParameter("start");
		String endTime = request.getParameter("end");
		///页码
		String page = request.getParameter("page");
		String pageNumber = request.getParameter("pagenumber");
		
		VideoFilter filter = new VideoFilter();
		if(null != country)
			filter.setCountry(Integer.valueOf(country));
		if(null != type)
			filter.setType(Integer.valueOf(type));
		if(null != language)
			filter.setLanguate(Integer.valueOf(language));
		if(null != startTime)
			filter.setStartPubDate(Integer.valueOf(startTime));
		if(null != endTime)
			filter.setEndPubDate(Integer.valueOf(endTime));
		
		int startline = 0;
		int pageNum = 20;
		if(null != pageNumber)
			pageNum = Integer.valueOf(pageNumber);
		filter.setPageNumber(pageNum);
		if(null != page){
			startline = (Integer.valueOf(page)-1)*pageNum;
		}
		filter.setStartLine(startline);
		
		List<VideoBean> videoList = DaoFactory.getInstance().getVideoDAO().getVideoByType(filter);
		int number =  DaoFactory.getInstance().getVideoDAO().getVideoNumberByType(filter);
		int totalPage = number/pageNum;
		JSON json = JSONSerializer.toJSON(videoList);
		JSONObject result = new JSONObject();
		if(null == json){
			result.put("error", -1);
		}else{
			result.put("error", 0);
			result.put("ret", json.toString());
			JSONObject pageInfo = new JSONObject();
			pageInfo.put("activePage",Integer.valueOf(page));
			pageInfo.put("totalNumber", number);
			pageInfo.put("totalPage", totalPage);
			result.put("pageInfo", pageInfo);
		}
		return result.toString();
	}
	
	public static void main(String[] args){
		
		VideoFilter filter = new VideoFilter();
		filter.setStartLine(0);
		filter.setPageNumber(20);
		
		List<VideoBean> videoList = DaoFactory.getInstance().getVideoDAO().getVideoByType(filter);
		
		int number =  DaoFactory.getInstance().getVideoDAO().getVideoNumberByType(filter);
		System.out.println(number);
		
		JSON json = JSONSerializer.toJSON(videoList);
		
		System.out.println(json.toString());
	}
	
}
