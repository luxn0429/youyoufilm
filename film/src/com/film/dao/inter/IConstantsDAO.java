/**
 * 加载预定义数据
 */
package com.film.dao.inter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luxianginng
 *
 */
public abstract class IConstantsDAO<K,T> {
	
	protected Map<String,T> name2Bean = new HashMap<String,T>();
	
	public Map<String, T> getName2Bean() {
		return name2Bean;
	}

	public T getBean(String name){
		return name2Bean.get(name);
	}
	public abstract Map<K,T> loadAllBean();
}
