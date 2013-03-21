/**
 * 
 */
package com.film.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
		response.setContentType("text/html;charset=utf-8");
		ServletOutputStream writer = response.getOutputStream();
		writer.write(result.getBytes("UTF-8"));
		writer.flush();
		writer.close();
	}
}
