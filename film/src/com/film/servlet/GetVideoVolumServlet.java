/**
 * 
 */
package com.film.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luxianginng
 *
 */
public class GetVideoVolumServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2252004146601478218L;

	/* (non-Javadoc)
	 * @see com.film.servlet.BaseServlet#dealRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected String dealRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String videoId = request.getParameter("videoId");
		
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
}
