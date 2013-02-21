/**
 * 
 */
package com.film.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luxianginng
 *
 */
public class SearchVideoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8243323153723976949L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
       this.doPost(request, response);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String country = request.getParameter("country");
		String type = request.getParameter("type");
		String language = request.getParameter("language");
		////发行时间
		String startTime = request.getParameter("start");
		String endTime = request.getParameter("end");
	}
}
