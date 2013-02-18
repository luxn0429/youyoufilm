/**
 * 
 */
package com.film.dao.factory;

import java.util.List;

import com.film.dao.bean.Hot;
import com.film.dao.inter.IHotTableDAO;
import com.film.dao.inter.impl.MonthHotListDAOImpl;
import com.film.dao.inter.impl.WeekHotListDAOImpl;

/**
 * @author luxianginng
 *
 */
public class HotListFactory {
		
	private static IHotTableDAO<Integer,List<Hot<Long>>> monthList = new MonthHotListDAOImpl("monthvideolist");
	public static IHotTableDAO<Integer, List<Hot<Long>>> getMonthList() {
		return monthList;
	}
	public static IHotTableDAO<Integer, List<Hot<Long>>> getWeekList() {
		return weekList;
	}
	private static IHotTableDAO<Integer,List<Hot<Long>>> weekList = new WeekHotListDAOImpl("weekvideolist");
}
