package com.film.searchengine;

import java.util.Map;

public class SearchMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		///是否是全抓
		String searchAll = null;
		if(args.length>0)
			searchAll = args[0];
		if(null == searchAll){
			Map<String,SearchEngine> url = SearchEngineConfigParser.getInstance().getAllEngine();
			System.out.println("start download all"+url.size());
			for(Map.Entry<String,SearchEngine> entry:url.entrySet()){
				SearchEngine engine = entry.getValue();
				engine.searchWebSite();
			}
		}else{
			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {
					Map<String,SearchEngine> url = SearchEngineConfigParser.getInstance().getAllEngine();
					System.out.println("start update today"+url.size());
					for(Map.Entry<String,SearchEngine> entry:url.entrySet()){
						SearchEngine engine = entry.getValue();
						//engine.searchWebSite();
						engine.searchUpdate();
					}
					
					try {
						Thread.sleep(6*60*60*1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
		
	}

}
