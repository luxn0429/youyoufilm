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
public class GetLatestVideoServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8243323153723976949L;

	@Override
	protected String dealRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		////国家
		String type = request.getParameter("type");//100,200,300,400,500
		String number = request.getParameter("number");
		
		
		int pageNum = 20;
		if(null != number)
			pageNum = Integer.valueOf(number);
		
		int int_type = 100;
		if(null != type)
			int_type = Integer.valueOf(type);
		
		List<VideoBean> videoList = DaoFactory.getInstance().getVideoDAO().getLatestVideo(int_type, pageNum);
		JSON json = JSONSerializer.toJSON(videoList);
		JSONObject result = new JSONObject();
		if(null == json){
			result.put("error", -1);
		}else{
			result.put("error", 0);
			result.put("ret", json.toString());
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
