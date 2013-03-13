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
	
	private Map<String,LRUList<Node>> totalClick = new HashMap<String,LRUList<Node>>();
	private Map<String,LRUList<Node>> weekClick = new HashMap<String,LRUList<Node>>();
	private Map<String,LRUList<Node>> monthClick = new HashMap<String,LRUList<Node>>();
	
	private OrderListCache(){
	}
	
	private String getKey(int type,int classified){
		return String.valueOf(type)+classified;
	}
	/**
	 * 
	 * @param type			电影或电视剧的类型
	 * @param classified	电影电视剧等的分类
	 * @param time			时间
	 * @param number		数量
	 */
	private LRUList<Node> loadOrderList(int type,int classified,int time,int number){
		
		IVideoDAO videoDao = DaoFactory.getInstance().getVideoDAO();
		
		List<VideoClickBean> ids = DaoFactory.getInstance().getVideoClickDAO().getClickOrder(type, classified, time, number);
		LRUList<Node> result = new LRUList<Node>(100);
		for(VideoClickBean id:ids){
			VideoBean bean = videoDao.getVideoBean(id.getVideoId());
			
			result.add(new Node(bean,id.getMonthClick()));
		}
		
		if(time == ConstantUtil.MONTHCLICK)
			monthClick.put(getKey(type,classified), result);
		else if(ConstantUtil.WEEKCLICK == time)
			weekClick.put(getKey(type,classified), result);
		else if(ConstantUtil.TOTALCLICK == time)
			totalClick.put(getKey(type,classified), result);
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
		String allKey = this.getKey(-1, bean.getClassified());
		String typekey = this.getKey(bean.getType(), bean.getClassified());
		Node nodeTotal = new Node(bean,click.getTotalClick());
		addToMap(allKey, typekey, nodeTotal,totalClick);
		Node nodeMonth = new Node(bean,click.getMonthClick());
		addToMap(allKey, typekey, nodeMonth,monthClick);
		Node nodeWeek = new Node(bean,click.getWeekClick());
		addToMap(allKey, typekey, nodeWeek,weekClick);
		return bean;
	}

	private void addToMap(String allKey, String typekey, Node nodeTotal,Map<String,LRUList<Node>> map) {
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

	public LRUList<Node> getTotalClick(int type,int classified) {
		String key = this.getKey(type, classified);
		LRUList<Node> list = this.totalClick.get(key);
		if(null == list){
			list = this.loadOrderList(type, classified, ConstantUtil.TOTALCLICK, 100);
			return list;
		}
		return list;
	}

	public LRUList<Node> getWeekClick(int type,int classified) {
		String key = this.getKey(type, classified);
		LRUList<Node> list = this.weekClick.get(key);
		if(null == list){
			list = this.loadOrderList(type, classified, ConstantUtil.WEEKCLICK, 100);
			return list;
		}
		return list;
	}

	public LRUList<Node> getMonthClick(int type,int classified) {
		String key = this.getKey(type, classified);
		LRUList<Node> list = this.monthClick.get(key);
		if(null == list){
			list = this.loadOrderList(type, classified, ConstantUtil.MONTHCLICK, 100);
			return list;
		}
		return list;
	}
	
	
}
