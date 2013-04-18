package com.film.util;

public class ConstantUtil {
	
	public static enum PLATFORM{
		BAIDU(1),YOUKU(2),QVOD(3),TUDOU(4);
		private final int index;
		PLATFORM(int ind){
			this.index = ind;
		}
		
		public int getIndex(){
			return index;
		}
	}
	
	
	///语言
	public static enum LANGUAGE{
		CHINESE (1), GUANGDONG (2), ENGLISH (3), KOREA (4), JAPANESE (5), THAI (6),RUSSIA(7);

        private final int index;

        LANGUAGE (int ind) {

            this.index = ind;
        }

        public int getIndex () {

            return index;
        }
	}
	
	/**
	 * 地区
	 * @author luxianginng
	 *
	 */
	public static enum COUNTRY{
		CHINA (1), HONGKONG(2), TAIWAN (3), KOREA (4), AMERICA (5),JAPA(6),
		THAI (7),SINGAPORE(8),FRANCE(9),RUSSIA(10),OTHER(256);

        private final int index;

        COUNTRY (int ind) {

            this.index = ind;
        }

        public int getIndex () {

            return index;
        }
	}
	
	/**
	 * 类型
	 * @author luxianginng
	 *
	 */
	public static enum CATEGORY{
		///电视剧
		CHINA (101), HONGKONG(102), TAIWAN (103), KOREA (104), AMERICA (105),JAPA(106),
		THAI (107),SINGAPORE(108),FRANCE(109),RUSSIA(110),
		//电影
		ACTION (201), COMED(202), ROMANCE(203), FICTION (204), WAR (205),DRAMA(206), 
		ANCIENT(207),MODERN (208), WUXIA(209), CITY (210), XUANYI (211), YANQING (212),MYTH(213),HISTORY(214),HORROR(228),
		SUPERSTAR(300),///明星合集
		CARTOON (400),///卡通
		FUN(500);///综艺娱乐
		
        private final int index;

        CATEGORY (int ind) {
            this.index = ind;
        }

        public int getIndex () {
            return index;
        }
	}
	
	/**
	 * 影片属于哪一类，电影、连续剧、综艺
	 * @author luxianginng
	 *
	 */
	public static enum CLASSIFIED{
		FILM(1),SERIES(2),ZONGYI(3);
		private final int index;

		CLASSIFIED (int ind) {

            this.index = ind;
        }

        public int getIndex () {

            return index;
        }
	}
	/**
	 * 影片状态     已经完结   连载中  即将上映
	 * @author luxianginng
	 *
	 */
	public static enum STATE{
		FINISH(1),INSERIOUS(2),WILLON(3);
		private final int index;

		STATE (int ind) {

            this.index = ind;
        }

        public int getIndex () {

            return index;
        }
	}
	
	public static final int TOTALCLICK = 1;
	public static final int MONTHCLICK = 2;
	public static final int WEEKCLICK  = 3;
	
}
