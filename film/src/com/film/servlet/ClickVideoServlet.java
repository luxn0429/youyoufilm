/**
 * 
 */
package com.film.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.film.cache.ClickCache;

/**
 * @author xiangning
 *
 */
public class ClickVideoServlet extends BaseServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -137731301370736480L;

	@Override
	protected String dealRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JSONObject result = new JSONObject();
		String videoId = request.getParameter("videoid");
		if(null == videoId){
			result.put("error",-1);
			return result.toString();
		}
		ClickCache.getInstance().click(Long.valueOf(videoId));
		result.put("error",0);
		return result.toString();
	}
	
}
