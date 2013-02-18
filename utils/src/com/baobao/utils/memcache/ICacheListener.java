package com.baobao.utils.memcache;

import java.util.List;

public interface ICacheListener<E> {

	public void runUpdate(List<E> list);
}
