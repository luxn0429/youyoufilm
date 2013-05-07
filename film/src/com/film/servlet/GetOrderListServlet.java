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
		String number = request.getParameter("number");
		
		int int_number = 20;
		if(null != number) int_number = Integer.valueOf(number);
		
		String type = request.getParameter("type");
		
		int type_int = -1;
		if(null != type) type_int = Integer.valueOf(type);
		
		JSONObject result = new JSONObject();
		List<Node> totalList = OrderListCache.getInstance().getTotalClick(type_int);
		JSONArray totalArray = getOrderList(int_number, totalList);
		JSONArray monthArray = getOrderList(int_number,OrderListCache.getInstance().getMonthClick(type_int));
		JSONArray weekArray = getOrderList(int_number,OrderListCache.getInstance().getWeekClick(type_int));
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
