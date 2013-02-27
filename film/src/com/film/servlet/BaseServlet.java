/**
 * 
 */
package com.film.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luxianginng
 *
 */
public abstract class BaseServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6004493547095050367L;
	
	private static final String CHARSET = "UTF-8";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
       this.doPost(request, response);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String result = dealRequest(request,response);
		writeResult(response,CHARSET,result);
	}
	
	protected abstract String dealRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	
	private void writeResult(HttpServletResponse response,String charset,String result) throws IOException{
		response.setCharacterEncoding (charset);
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.write(result);
		writer.flush();
		writer.close();
	}
}
