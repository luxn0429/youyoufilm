/**
 * 
 */
package com.film.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.film.dao.bean.VideoBean;
import com.film.dao.bean.VolumeBean;
import com.film.dao.factory.DaoFactory;

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
		JSONObject result = new JSONObject();
		
		String videoId = request.getParameter("videoid");
		if(null == videoId){
			result.put("error", -1);
			return result.toString();
		}
		
		
		VideoBean bean = DaoFactory.getInstance().getVideoDAO().getVideoBean(Long.valueOf(videoId));
		if(null == bean){
			result.put("error", -1);
		}else{
			result.put("error", 0);
			result.put("ret", JSONSerializer.toJSON(bean).toString());
			
			List<VolumeBean> volumes = DaoFactory.getInstance().getVolumeDAO().getVolumesByVideoID(Long.valueOf(videoId));
			result.put("volumes", JSONSerializer.toJSON(volumes).toString());
		}
		
		return result.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<VolumeBean> volumes = DaoFactory.getInstance().getVolumeDAO().getVolumesByVideoID(Long.valueOf("1"));
		for(VolumeBean bean:volumes){
			System.out.println(bean.getVolume());
		}
		System.out.println(volumes);
	}
}
