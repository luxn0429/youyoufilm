package com.film.db.interf.impl;

import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;


import com.film.dao.bean.LanguageBean;
import com.film.dao.inter.IConstantsDAO;
import com.film.dao.inter.impl.LanguageConstantsDAO;

public class LanguageConstantsTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configure ("log4j.properties");
	}
	@Test
	public void testLoadAllBean() {
		IConstantsDAO<Integer,LanguageBean> db = LanguageConstantsDAO.getInstance();
		Map<Integer,LanguageBean> map = db.loadAllBean();
		System.out.println(map.size());
		
	}

}
