package com.baobao.utils.memcache.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.baobao.utils.cache.Event;
import com.baobao.utils.memcache.IMemcachedCache;
import com.baobao.utils.memcache.impl.MemcachedCacheManager;

public class Test {

	static class InsertBlog extends Thread {
		private long key;
		private CountDownLatch startSignal;
		private CountDownLatch doneSignal;
		private int times;
		private IMemcachedCache memcache = MemcachedCacheManager.getInstance()
				.getCache("test");
		private List<User2MiniBlog> list = new ArrayList<User2MiniBlog>();

		public InsertBlog(long key, int blogNumber, int times,
				CountDownLatch startSignal, CountDownLatch doneSignal) {
			this.key = key;
			this.startSignal = startSignal;
			this.doneSignal = doneSignal;
			this.times = times;
			for (int i = 0; i < blogNumber; i++) {
				User2MiniBlog miniblog = new User2MiniBlog();
				miniblog.setBlogId(1234L);
				miniblog.setCreateDate(12345);
				miniblog.setGmbFrom(12);
				miniblog.setGmbType(123);
				list.add(miniblog);
			}
		}

		public void run() {
			try {
				startSignal.await();
				for (int i = 0; i < times; i++) {
					memcache.putQuiet(String.valueOf(key), list);
				}
				doneSignal.countDown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*int in_b = 1000; ///对象数量
		int number = 10; // /线程数量
		int times = 1000; // /循环次数
		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(number);

		InsertBlog[] blogs = new InsertBlog[number];
		for (int i = 0; i < number; i++) {
			blogs[i] = new InsertBlog(i + 1, in_b, times, startSignal,
					doneSignal);
			blogs[i].start();
		}
		long notime = System.currentTimeMillis();
		startSignal.countDown();
		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Long second = (System.currentTimeMillis() - notime) / 1000L;
		if (second == 0)
			second = 1L;
		System.out.println("total time " + second + " seconds, "+number +" thread ,every put "+times+" times, total put "
						+ (number *times) + " times, " + ( number * times) / second
						+ "p/s");*/
		IMemcachedCache noti = MemcachedCacheManager.getInstance().getCache("noti");
		IMemcachedCache number = MemcachedCacheManager.getInstance().getCache("notinum");
		noti.putQuiet("1234","123234234");
		System.out.println(noti.get("1234"));
		Event event = new Event(1L,null);
		noti.remove("1234", event);

	}

}
