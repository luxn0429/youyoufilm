package com.film.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.film.cache.Node;
import com.film.cache.OrderListCache;

public class GetOrderListServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8868570801659529576L;

	@Override
	protected String dealRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String monthNumber = request.getParameter("monthnumber");
		String totalNumber = request.getParameter("totalnumber");
		String weekNumber  = request.getParameter("weeknumber");
		int month = 20;
		if(null != monthNumber) month = Integer.valueOf(monthNumber);
		int total = 20;
		if(null != totalNumber) total = Integer.valueOf(totalNumber);
		int week = 20;
		if(null != weekNumber) week = Integer.valueOf(weekNumber);
		
		String type = request.getParameter("type");
		String classified = request.getParameter("classified");
		
		int type_int = -1;
		if(null != type) type_int = Integer.valueOf(type);
		int classified_int = Integer.valueOf(classified);
		
		JSONObject result = new JSONObject();
		List<Node> totalList = OrderListCache.getInstance().getTotalClick(type_int,classified_int);
		JSONArray totalArray = getOrderList(total, totalList);
		JSONArray monthArray = getOrderList(month,OrderListCache.getInstance().getMonthClick(type_int,classified_int));
		JSONArray weekArray = getOrderList(week,OrderListCache.getInstance().getWeekClick(type_int,classified_int));
		result.put("error", 0);
		result.put("total", totalArray.toString());
		result.put("month", monthArray.toString());
		result.put("week", weekArray.toString());
		return result.toString();
	}

	private JSONArray getOrderList(int total, List<Node> totalList) {
		JSONArray totalArray = new JSONArray();
		int count = 0;
		for(Node node:totalList){
			if(count>=total)
				break;
			JSONObject obj = new JSONObject();
			obj.put("id", node.bean.getId());
			obj.put("name", node.bean.getName());
			obj.put("number", node.clickNumber);
			obj.put("date", new Date(node.bean.getUpdateTime()));
			totalArray.add(obj);
		}
		return totalArray;
	}

}
