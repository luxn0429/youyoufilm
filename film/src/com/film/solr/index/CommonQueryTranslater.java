package com.film.solr.index;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CommonQueryTranslater {
	protected final Map<String, Object> map = new HashMap<String, Object>();
	protected final Map<String,String> orMap = new HashMap<String,String>();
	
	private String fuzzyValue(Object obj){
		if (obj instanceof Integer) {
			Integer new_name = (Integer) obj;
			return new_name == -1 ? "*": String.valueOf(new_name);
		}
		if (obj instanceof Long) {
			Long new_name = (Long) obj;
			return new_name == -1 ? "*": String.valueOf(new_name);
		}
		if (obj instanceof String) {
			String new_name = (String) obj;
			return new_name.equals("") ? "*": new_name;
		}
		return "*";
	}
	public void put(String fieldname, Object fileValue) {
		map.put(fieldname, fuzzyValue(fileValue));
	}
	
	public void put(String fieldname, int start, int end) {
		String strValue = "[" + fuzzyValue(start) + " TO " + fuzzyValue(end) + "]";
		map.put(fieldname, strValue);
	}
	
	public void putOrMap(String fieldname,String value){
		if(null == value)
			return;
		orMap.put(fieldname, value);
	}
	
	public void putOrNotMatch(String fieldname,List<String> value){
		if(null == value || value.size() == 0)
			return;
		StringBuffer buffer = new StringBuffer("(").append(fieldname).append(":");
		boolean first = true;
		for(String item:value){
			if(!first)
				buffer.append(" OR ").append(fieldname).append(":");
			first = false;
			buffer.append(item);
		}
		buffer.append(")");
		orMap.put(fieldname, buffer.toString());
	}
	
	
	public void put(String fieldname,Date start,Date end){
		String strValue = "["+getDate2SearchStr(start)+" TO "+getDate2SearchStr(end)+"]";
		map.put(fieldname, strValue);
	}

	public void putMatch(String fieldname,String value){
		String strValue = "~,"+value+",~";
		map.put(fieldname, strValue);
	}
	
	public String toString() {
		if(map.size() ==0 && orMap.size() == 0)
			return "*:*";
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			if (!first)
				sb.append(" AND ");
			first = false;
			sb.append(key);
			sb.append(":");
			sb.append(val);
		}
		if(sb.length()>0 && orMap.size()>0)
			sb.append(" AND (");
		else if(sb.length() == 0 && orMap.size()>0)
			sb.append("(");
		first = true;
		for(Map.Entry<String,String> entry:orMap.entrySet()){
			if(!first)
				sb.append(" OR ");
			first = false;
			sb.append(entry.getKey()).append(":").append(entry.getValue());
		}
		
		if(sb.length()>0 && orMap.size()>0)
			sb.append(")");
		return sb.toString();
	}
	private static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String getDate2SearchStr(Date date){
		TimeZone gmtTime = TimeZone.getTimeZone("GMT"); 
		FORMAT.setTimeZone(gmtTime);
		String format = FORMAT.format(date);
		String[] split = format.split(" ");
		return split[0]+"T"+split[1]+"Z";
	}
	
	public static void main(String[] args){
		Date date = new Date(1342886423263L);
		System.out.println(date);
		//System.out.println(getSearchStr(date));
	}
}
