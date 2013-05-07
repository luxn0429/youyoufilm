package com.film.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baobao.utils.cache.LRUList;
import com.film.dao.bean.VideoBean;
import com.film.dao.bean.VideoClickBean;
import com.film.dao.factory.DaoFactory;
import com.film.dao.inter.IVideoDAO;
import com.film.util.ConstantUtil;

public class OrderListCache {
	
	private Map<Integer,LRUList<Node>> totalClick = new HashMap<Integer,LRUList<Node>>();
	private Map<Integer,LRUList<Node>> weekClick = new HashMap<Integer,LRUList<Node>>();
	private Map<Integer,LRUList<Node>> monthClick = new HashMap<Integer,LRUList<Node>>();
	
	private OrderListCache(){
	}
	
	/**
	 * 
	 * @param type			电影或电视剧的类型
	 * @param time			时间
	 * @param number		数量
	 */
	private LRUList<Node> loadOrderList(int type,int time,int number){
		
		IVideoDAO videoDao = DaoFactory.getInstance().getVideoDAO();
		
		List<VideoClickBean> ids = DaoFactory.getInstance().getVideoClickDAO().getClickOrder(type, time, number);
		LRUList<Node> result = new LRUList<Node>(100);
		for(VideoClickBean id:ids){
			VideoBean bean = videoDao.getVideoBean(id.getVideoId());
			
			result.add(new Node(bean,id.getMonthClick()));
		}
		
		if(time == ConstantUtil.MONTHCLICK)
			monthClick.put(type, result);
		else if(ConstantUtil.WEEKCLICK == time)
			weekClick.put(type, result);
		else if(ConstantUtil.TOTALCLICK == time)
			totalClick.put(type, result);
		return result;
	}
	
	private static OrderListCache instance = new OrderListCache();
	public static OrderListCache getInstance(){return instance;}
	
	/**
	 * 
	 * @param clickbean 	已经更新完的
	 * @return
	 */
	public VideoBean addToClickList(long videoId){
		IVideoDAO videoDao = DaoFactory.getInstance().getVideoDAO();
		VideoBean bean = videoDao.getVideoBean(videoId);
		VideoClickBean click = DaoFactory.getInstance().getVideoClickDAO().getClickBean(videoId);
		if(null == click)
			return bean;
		Integer allKey = -1;
		Node nodeTotal = new Node(bean,click.getTotalClick());
		addToMap(allKey, bean.getType(), nodeTotal,totalClick);
		Node nodeMonth = new Node(bean,click.getMonthClick());
		addToMap(allKey, bean.getType(), nodeMonth,monthClick);
		Node nodeWeek = new Node(bean,click.getWeekClick());
		addToMap(allKey, bean.getType(), nodeWeek,weekClick);
		return bean;
	}

	private void addToMap(int allKey, int typekey, Node nodeTotal,Map<Integer,LRUList<Node>> map) {
		LRUList<Node> list = map.get(allKey);
		if(null != list){
			addToList(list,nodeTotal);
		}
		list = map.get(typekey);
		if(null != list)
			addToList(list,nodeTotal);
	}
	
	private void addToList(LRUList<Node> list,Node typeNode){
		boolean find =false;
		for(Node node:list){
			if(node.bean.getId() == typeNode.bean.getId()){
				node.clickNumber = typeNode.clickNumber;
				find = true;
				break;
			}
		}
		if(!find){
			if(list.size()<100){
				list.add(typeNode);
			}else{
				Node node = list.get(list.size()-1);
				if(node.clickNumber<typeNode.clickNumber)
					list.put(typeNode);
			}
		}
		
		Collections.sort(list);
	}

	public LRUList<Node> getTotalClick(int type) {
		String key =String.valueOf(type);
		LRUList<Node> list = this.totalClick.get(key);
		if(null == list){
			list = this.loadOrderList(type, ConstantUtil.TOTALCLICK, 100);
			return list;
		}
		return list;
	}

	public LRUList<Node> getWeekClick(int type) {
		String key = String.valueOf(type);
		LRUList<Node> list = this.weekClick.get(key);
		if(null == list){
			list = this.loadOrderList(type, ConstantUtil.WEEKCLICK, 100);
			return list;
		}
		return list;
	}

	public LRUList<Node> getMonthClick(int type) {
		String key = String.valueOf(type);
		LRUList<Node> list = this.monthClick.get(key);
		if(null == list){
			list = this.loadOrderList(type, ConstantUtil.MONTHCLICK, 100);
			return list;
		}
		return list;
	}
	
	
}
