package com.film.cache;

import com.film.dao.bean.VideoBean;

public class Node implements Comparable<Node> {
	public VideoBean bean;
	public int clickNumber;
	public Node(VideoBean bean,int clickNumber){
		this.bean = bean;
		this.clickNumber = clickNumber;
	}
	@Override
	public int compareTo(Node o) {
		if(clickNumber>o.clickNumber)
			return 1;
		if(clickNumber<o.clickNumber)
			return -1;
		return 0;
	}
}
