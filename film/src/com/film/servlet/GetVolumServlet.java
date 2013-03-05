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

import com.film.dao.bean.VolumeBean;
import com.film.dao.factory.DaoFactory;

/**
 * @author xiangning
 *
 */
public class GetVolumServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.film.servlet.BaseServlet#dealRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected String dealRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String videoID = request.getParameter("videoID");
		List<VolumeBean> volumes = DaoFactory.getInstance().getVolumeDAO().getVolumesByVideoID(Long.valueOf(videoID));
		JSONObject result = new JSONObject();
		if(null == volumes){
			result.put("error", -1);
		}else{
			result.put("error", 0);
			JSON json = JSONSerializer.toJSON(volumes);
			result.put("ret", json.toString());
		}
		
		return result.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
