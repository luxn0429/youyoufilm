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
		init();
	}
	
	private void init(){
		int number = 100;
		IVideoDAO videoDao = DaoFactory.getInstance().getVideoDAO();
		List<VideoClickBean> ids = DaoFactory.getInstance().getVideoClickDAO().getClickOrder(ConstantUtil.MONTHCLICK, number);
		for(VideoClickBean id:ids){
			VideoBean bean = videoDao.getVideoBean(id.getVideoId());
			monthClick.add(new Node(bean,id.getMonthClick()));
		}
		
		ids = DaoFactory.getInstance().getVideoClickDAO().getClickOrder(ConstantUtil.TOTALCLICK, number);
		for(VideoClickBean id:ids){
			VideoBean bean = videoDao.getVideoBean(id.getVideoId());
			totalClick.add(new Node(bean,id.getTotalClick()));
		}
		
		ids = DaoFactory.getInstance().getVideoClickDAO().getClickOrder(ConstantUtil.WEEKCLICK, number);
		for(VideoClickBean id:ids){
			VideoBean bean = videoDao.getVideoBean(id.getVideoId());
			weekClick.add(new Node(bean,id.getWeekClick()));
		}
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
		
		Node nodeTotal = new Node(bean,click.getTotalClick());
		addToList(totalClick,nodeTotal);
		Node nodeMonth = new Node(bean,click.getMonthClick());
		addToList(monthClick,nodeMonth);
		Node nodeWeek = new Node(bean,click.getWeekClick());
		addToList(weekClick,nodeWeek);
		return bean;
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

	public LRUList<Node> getTotalClick() {
		return totalClick;
	}

	public LRUList<Node> getWeekClick() {
		return weekClick;
	}

	public LRUList<Node> getMonthClick() {
		return monthClick;
	}
	
	
}
