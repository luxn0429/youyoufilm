package com.film.server;

import com.baobao.utils.webtool.JettyApplication;

public class FilmServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		JettyApplication jettyServer = JettyApplication.getInstance();
		
		jettyServer.start();

	}

}
