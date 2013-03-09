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
	public static enum CATEGORY{							   //剧情
		ACTION (11), COMED(12), ROMANCE(13), FICTION (14), WAR (15),DRAMA(16), CARTOON (17),FUN(18),ANCIENT(19),
		MODERN (20), WUXIA(21), CITY (22), XUANYI (23), YANQING (24),MYTH(25),HISTORY(26),SUPERSTAR(27),HORROR(28);

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
