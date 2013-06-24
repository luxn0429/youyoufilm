/**
 * 
 */
package com.film.searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.baobao.utils.MD5;
import com.film.dao.bean.VideoBean;
import com.film.dao.bean.VolumeBean;
import com.film.dao.factory.DaoFactory;
import com.film.util.ConstantUtil;
import com.film.util.ConstantUtil.PLATFORM;
import com.film.util.EscapeUnescape;

/**
 * @author luxianginng
 *
 */
public class BubuGaoParser extends SearchEngine {
	private static final String encode = "GB18030";
	private static ExecutorService EXEC=Executors.newFixedThreadPool(4);
	
	public BubuGaoParser(String url,String updateUrl){
		super(url,updateUrl);
	}

	/* (non-Javadoc)
	 * @see com.film.searchengine.SearchEngine#parseHome()
	 */
	@Override
	protected void searchWebSite() {
		String html = SearchWebPageUtil.getUrlContent(this.url,encode);
		Document doc = Jsoup.parse(html);
		
		Element nav = doc.getElementById("nav");
		if(null == nav){
			Logger.getLogger(this.getClass()).error("type is error modify quickly!");
			return;
		}
		Elements links = nav.getElementsByTag("a");
		for(int i=0;i<links.size();i++){
			Element link = links.get(i);
			String href = link.attr("href");
			String title = link.text();
			///解析电视剧
			if(title.contains("韩剧")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.KOREA,ConstantUtil.CATEGORY.KOREA,href));
			}else if(title.contains("欧美剧")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.AMERICA,ConstantUtil.CATEGORY.AMERICA,href));
			}else if(title.contains("港剧")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.HONGKONG,ConstantUtil.CATEGORY.HONGKONG,href));
			}else if(title.contains("台剧")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.TAIWAN,ConstantUtil.CATEGORY.TAIWAN,href));
			}else if(title.contains("日剧")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.JAPA,ConstantUtil.CATEGORY.JAPA,href));
			}else if(title.contains("大陆剧")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.CHINA,ConstantUtil.CATEGORY.CHINA,href));
			}else if(title.contains("新马泰")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.SINGAPORE,ConstantUtil.CATEGORY.SINGAPORE,href));
			}else if(title.contains("动作")){///解析电影
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.ACTION,href));
			}else if(title.contains("科幻")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.FICTION,href));
			}else if(title.contains("恐怖")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.HORROR,href));
			}else if(title.contains("喜剧")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.COMED,href));
			}else if(title.contains("爱情")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.ROMANCE,href));
			}else if(title.contains("剧情")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.DRAMA,href));
			}else if(title.contains("战争")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.WAR,href));
			}else if(title.contains("动画")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.CARTOON,href));
			}
		}
	}
	
	
	public void testGetVideo(String path){
		EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.CARTOON,path));
	}
	class GetVideos implements Runnable{

		private ConstantUtil.COUNTRY country;
		private ConstantUtil.CATEGORY type;
		private String path;
		public GetVideos(ConstantUtil.COUNTRY country,ConstantUtil.CATEGORY type, String path){
			this.country = country;
			this.type = type;
			this.path = path;
		}
		
		private VideoBean videoBean= null;
		
		public GetVideos(String path,VideoBean bean){
			videoBean = bean;
			this.path = path;
		}
		@Override
		public void run() {
			if(null == videoBean){
				String next = parseVideo(path);
				while(next != null){
					next = parseVideo(next);
				}
			}
			else
				getVideoBean(path,videoBean);
		}
		/* (non-Javadoc)
		 * @see com.film.searchengine.SearchEngine#parseVideo(int, java.lang.String)
		 */
		protected String parseVideo(String path) {
			String html = SearchWebPageUtil.getUrlUseProxyContent(url+path,encode);
			Logger.getLogger(this.getClass()).info("get page:"+url+path);
			Document doc = Jsoup.parse(html);
			Element videoDoc = doc.getElementsByAttributeValue("class", "lmain").first();
			if(null != videoDoc){
				Elements videoList = videoDoc.getElementsByAttributeValue("class", "litem");
				for(Element video:videoList){
					Element descriptBox = video.getElementsByAttributeValue("class", "vtxt").first();
					
					Element descriptionUrl = descriptBox.getElementsByTag("a").first();
					String desPath = descriptionUrl.attr("href");
					////得到bean的产地
					VideoBean bean = new VideoBean();
					if(null != country){
						bean.setCountry(country.getIndex());
					}
					else{
						Elements hlist = descriptionUrl.children();
						for(Element e:hlist){
							String text = e.text();
							if(text.contains("地区")){
								if(text.contains("大陆")){
									bean.setCountry(ConstantUtil.COUNTRY.CHINA.getIndex());
								}else if(text.contains("香港")){
									bean.setCountry(ConstantUtil.COUNTRY.HONGKONG.getIndex());
								}else if(text.contains("台湾")){
									bean.setCountry(ConstantUtil.COUNTRY.TAIWAN.getIndex());
								}else if(text.contains("美")){
									bean.setCountry(ConstantUtil.COUNTRY.AMERICA.getIndex());
								}else if(text.contains("日")){
									bean.setCountry(ConstantUtil.COUNTRY.JAPA.getIndex());
								}else if(text.contains("韩")){
									bean.setCountry(ConstantUtil.COUNTRY.KOREA.getIndex());
								}else if(text.contains("法")){
									bean.setCountry(ConstantUtil.COUNTRY.FRANCE.getIndex());
								}else if(text.contains("英")){
									bean.setCountry(ConstantUtil.COUNTRY.ENGLAND.getIndex());
								}else  if(text.contains("印度")){
									bean.setCountry(ConstantUtil.COUNTRY.INDIA.getIndex());
								}else if(text.contains("泰")){
									bean.setCountry(ConstantUtil.COUNTRY.THAI.getIndex());
								}else if(text.contains("新加坡")){
									bean.setCountry(ConstantUtil.COUNTRY.SINGAPORE.getIndex());
								}else{
									bean.setCountry(ConstantUtil.COUNTRY.OTHER.getIndex());
								}
								break;
							}
						}
					}
					if(null != type)
						bean.setType(type.getIndex());
					if(country != null)
						bean.setClassified(ConstantUtil.CLASSIFIED.SERIES.getIndex());
					else
						bean.setClassified(ConstantUtil.CLASSIFIED.FILM.getIndex());
					try{
						getVideoBean(desPath,bean);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			/////抓取下一页，防止并发被禁止，不并行抓
			Element pages = doc.getElementsByAttributeValue("class","pages").first();
			if(null != pages){
				Elements links = pages.getElementsByTag("a");
				for(Element element:links){
					if(element.text().contains("下一页")){
						String url = element.attr("href");
						return url;
						//parseVideo(url);
					}
				}
			}
			return null;
		}
		
		public void getVideoBean(String path,VideoBean bean){
			String html = SearchWebPageUtil.getUrlUseProxyContent(url+path,encode);
			Document doc = Jsoup.parse(html);
			
			Map<Integer,List<VolumeBean>> volumeResult = parseVolume(doc,0);
			if(null == volumeResult || volumeResult.size() == 0)
				return ;
			////
			Element video = doc.getElementsByAttributeValue("class", "vcon").first();
			
			Element pictureElement = video.getElementsByAttributeValue("class", "vpic").first();
			Element picture = pictureElement.getElementsByTag("img").first();
			String pic = picture.attr("src");
			String name = picture.attr("alt");
			bean.setName(name);
			
			////处理海报
			if(pic!=null && pic.trim().length()>0){
				/////海报，将海报下载下来
				String downloadPoster = null;
				///海报
				
				if(pic.startsWith("http://")){
					downloadPoster = PictureDownLoad.downLoadPic(pic);
				}else{
					downloadPoster = PictureDownLoad.downLoadPic(url+pic);
				}
				
				if(null != downloadPoster)
					bean.setPoster(downloadPoster);
			}
			////处理其他
			Element videoDetail = video.getElementsByAttributeValue("class", "vdetail").first();
			////处理状态
			Element state = videoDetail.getElementsByAttributeValue("class", "zt").first();
			
			String stateText = state.text();
			if(bean.getClassified()== ConstantUtil.CLASSIFIED.SERIES.getIndex() ){
				if(stateText.contains("全集"))
					bean.setState(ConstantUtil.STATE.FINISH.getIndex());
				else
					bean.setState(ConstantUtil.STATE.INSERIOUS.getIndex());
				
			}else if(bean.getClassified() == ConstantUtil.CLASSIFIED.FILM.getIndex()){
				if(stateText.contains("即"))
					bean.setState(ConstantUtil.STATE.WILLON.getIndex());
				else
					bean.setState(ConstantUtil.STATE.FINISH.getIndex());
			}
			
			
			Element statElement = videoDetail.getElementsByTag("p").first();
			while(statElement!= null){
				stateText = statElement.text();
				if(stateText.contains("语言")){
					////发音
					if(stateText.contains("英语")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.AMERICA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.ENGLISH.getIndex());
					}else if(stateText.contains("国语")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.CHINA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.CHINESE.getIndex());
					}else if(stateText.contains("韩语")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.KOREA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.KOREA.getIndex());
					}else if(stateText.contains("日语")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.JAPA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.JAPANESE.getIndex());
					}else if(stateText.contains("法语")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.FRANCE.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.FRANCE.getIndex());
					}else if(stateText.contains("印度")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.INDIA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.INDIA.getIndex());
					}else if(stateText.contains("粤语")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.HONGKONG.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.GUANGDONG.getIndex());
					}else if(stateText.contains("泰语")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.THAI.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.THAI.getIndex());
					}else if(stateText.contains("俄")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.RUSSIA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.RUSSIA.getIndex());
					}
					bean.setCaption(0);
				}else if(stateText.contains("年份")){
					stateText = stateText.replaceAll("<([^>]*)>", "");
					int i = stateText.indexOf("：");
					if(i<0)
						i = stateText.indexOf(":");
					stateText = stateText.substring(i+1);
					try{
						bean.setPubdate(Integer.valueOf(stateText));
					}catch(Exception e){
						e.printStackTrace();
						Logger.getLogger(this.getClass()).info("path:"+path);
					}
				}else if(stateText.contains("主演")){
					stateText = stateText.replaceAll("<([^>]*)>", "");
					String actor = stateText.substring(stateText.indexOf("：")+1);
					if(null == actor || actor.trim().length() == 0)
						actor = stateText.substring(stateText.indexOf(":")+1);
					bean.setPerformer(actor);
				}else if(stateText.contains("分类")){
					if(stateText.contains("韩剧")){
						bean.setType(ConstantUtil.CATEGORY.KOREA.getIndex());
					}else if(stateText.contains("欧美剧")){
						bean.setType(ConstantUtil.CATEGORY.AMERICA.getIndex());
					}else if(stateText.contains("港剧")){
						bean.setType(ConstantUtil.CATEGORY.HONGKONG.getIndex());
					}else if(stateText.contains("台剧")){
						bean.setType(ConstantUtil.CATEGORY.TAIWAN.getIndex());
					}else if(stateText.contains("日剧")){
						bean.setType(ConstantUtil.CATEGORY.JAPA.getIndex());
					}else if(stateText.contains("大陆剧")){
						bean.setType(ConstantUtil.CATEGORY.CHINA.getIndex());
					}else if(stateText.contains("新马泰")){
						bean.setType(ConstantUtil.CATEGORY.SINGAPORE.getIndex());
					}else if(stateText.contains("动作")){///解析电影
						bean.setType(ConstantUtil.CATEGORY.ACTION.getIndex());
					}else if(stateText.contains("科幻")){
						bean.setType(ConstantUtil.CATEGORY.FICTION.getIndex());
					}else if(stateText.contains("恐怖")){
						bean.setType(ConstantUtil.CATEGORY.HORROR.getIndex());
					}else if(stateText.contains("喜剧")){
						bean.setType(ConstantUtil.CATEGORY.COMED.getIndex());
					}else if(stateText.contains("爱情")){
						bean.setType(ConstantUtil.CATEGORY.ROMANCE.getIndex());
					}else if(stateText.contains("剧情")){
						bean.setType(ConstantUtil.CATEGORY.DRAMA.getIndex());
					}else if(stateText.contains("战争")){
						bean.setType(ConstantUtil.CATEGORY.WAR.getIndex());
					}else if(stateText.contains("动漫") || stateText.contains("动画")){
						bean.setType(ConstantUtil.CATEGORY.CARTOON.getIndex());
					}
					
				}
				statElement = statElement.nextElementSibling();
			}
			
			Element introduceElement = doc.getElementsByAttributeValue("class","vcontent").first();
			if(null != introduceElement){
				String text = introduceElement.html();
				text = text.replaceAll("<([^>]*)>", "");
				bean.setIntroduction(text.trim());
			}
			////插入视频描述信息
			int id = DaoFactory.getInstance().getVideoDAO().insert(bean);
			if(id <0){
				///如果已经存在的视频,则重新更新视频的连接
				id = (int)DaoFactory.getInstance().getVideoDAO().getVideoID(bean.getName());
				
				if(id<0) return;
			}
			

			for(Map.Entry<Integer,List<VolumeBean>> entry:volumeResult.entrySet()){
				for(VolumeBean volumebean:entry.getValue()){
					volumebean.setBelongto(id);
				}
			}
			
			if(!DaoFactory.getInstance().getVolumeDAO().insert(volumeResult))
				return;
		}
		/**
		 * 解析电影集，返回对应播放手段与集的对应，集已按照第一到最后排序好
		 * @param document
		 * @return
		 */
		private Map<Integer,List<VolumeBean>> parseVolume(Document document,int videoID){
			Elements videoList = document.getElementsByAttributeValue("class","qvodl");
			
			if(null != videoList){
				Element playerElement = videoList.first().getElementsByTag("ul").first();
				Elements links = playerElement.getElementsByTag("a");
				if(null != links){
					try{
						Element href = links.first();
						String path = href.attr("href");
						String page = SearchWebPageUtil.getUrlUseProxyContent(url+path,encode);
						Document docNative = Jsoup.parse(page);
						Elements playData = docNative.getElementsByTag("script");
						if(null != playData){
							for(int i=0;i<playData.size();i++){
								Element temp = playData.get(i);
								String scriptSrc = temp.attr("src");
								if(scriptSrc == null || scriptSrc.trim().length() == 0)
									continue;
								if(!scriptSrc.contains("playdata/"))
									continue;
								String volumnString = SearchWebPageUtil.getUrlUseProxyContent(url+scriptSrc,encode);
								return parseVideVolumn(volumnString,videoID);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
						System.out.println(document.html());
					}
				}
				
			}
			return null;
		}
	}
	
	public  Map<Integer,List<VolumeBean>> parseVideVolumn(String volumn,int video){
		String volumnStr = EscapeUnescape.unescape(volumn);
		int start = volumnStr.indexOf("[");
		int end = volumnStr.lastIndexOf("]");
		if(start>=0)
			volumnStr = volumnStr.substring(start, end+1);
		JSONArray videoArray = JSONArray.fromObject(volumnStr);
		System.out.println(volumnStr);
		Map<Integer,List<VolumeBean>> result = new HashMap<Integer,List<VolumeBean>>();
		
		for(int i=0;i<videoArray.size();i++){
			JSONArray arrayPlayType = videoArray.getJSONArray(i);
			String type = arrayPlayType.getString(0);
			JSONArray playVolumn = arrayPlayType.getJSONArray(1);
			if(type.toLowerCase().contains("qvod")){
				int playerPlat = PLATFORM.QVOD.getIndex();
				List<VolumeBean> list = result.get(playerPlat);
				if(null == list){
					list = new ArrayList<VolumeBean>();
					result.put(playerPlat,list);
				}
				
				for(int k=0;k<playVolumn.size();k++){
					String vol = playVolumn.getString(k);
					String[] temp = vol.split("\\$");
					VolumeBean bean = new VolumeBean();
					bean.setBelongto(video);
					bean.setPlayer(playerPlat);
					bean.setUrl(temp[1]);
					bean.setVolume(temp[0]);
					bean.setMd5(MD5.StringToMd5String(bean.getUrl()));
					////如果视频存在则不更新
					if(DaoFactory.getInstance().getVolumeDAO().exist(video, bean.getMd5())){
						DaoFactory.getInstance().getVolumeDAO().updatePlayer(bean.getMd5(), bean.getPlayer());
						continue;
					}
					list.add(bean);
				}
			}
			
		}
		return result;
	}
	@Override
	protected void searchUpdate() {
		String html = SearchWebPageUtil.getUrlUseProxyContent(this.url+"/"+this.updateUrl,encode);
		Document doc = Jsoup.parse(html);
		Elements nav = doc.getElementsByAttributeValue("class","box_con");
		Element box = nav.first();
		Elements videos = box.getElementsByTag("li");
		for(int j=0;j<videos.size();j++){
			Element video = videos.get(j);
			Element movieList = video.getElementsByAttributeValue("class", "movielist_tt").first();
			Elements links = movieList.getElementsByTag("a");
			Element link = null;
			if(links.size()>0)
				link = links.first();
			String href = link.attr("href");
			
			VideoBean videoBean = new VideoBean();
			EXEC.submit(new GetVideos(href,videoBean));
		}
	}
	
	public static void main(String[] args){
		BubuGaoParser par = new BubuGaoParser("http://www.bbgyy.net/","zt/new.html");
		
		String video = "var VideoListJson=[['qvod',['\u7B2C01\u96C6$qvod://367411837|D59DA3BC6AA510A2057F71A25F853595FEA2464B|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP01.540P.HDTV.mkv|$qvod','\u7B2C02\u96C6$qvod://367198450|6065D32F9593F37E2572F6BE0785D314C1B0B568|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP02.540P.HDTV.mkv|$qvod','\u7B2C03\u96C6$qvod://367256835|E93553ADED142F35DB75F793C2640F4C92037A34|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP03.540P.HDTV.mkv|$qvod','\u7B2C04\u96C6$qvod://367195238|DC0B657E8794E2A07FDDC188A421003B607B514E|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP04.540P.HDTV.mkv|$qvod','\u7B2C05\u96C6$qvod://367358964|DE41A5AD9E0CD7E1420AEB5AF6A1DE0EE8DFB2B1|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP05.540P.HDTV.mkv|$qvod','\u7B2C06\u96C6$qvod://367133137|9FA45ECC35A23CF8391C15E3895D2FB057AE7679|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP06.540P.HDTV.mkv|$qvod','\u7B2C07\u96C6$qvod://367292014|1B317C6F1A7BCC6C91C752F17C8F38D0354A9EBA|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP07.540P.HDTV.mkv|$qvod','\u7B2C08\u96C6$qvod://367302921|E7C12057F84FBD6378938C3B80C888DBB6F6CCA0|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP08.540P.HDTV.mkv|$qvod','\u7B2C09\u96C6$qvod://367309598|8CBD22ADE8E34414F021C1AEBCBF98AC8E8A9772|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP09.540P.HDTV.mkv|$qvod','\u7B2C10\u96C6$qvod://367096051|E15DB1AF5139799C25DE1BCE1D11232A6F2A94A7|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP10.540P.HDTV.mkv|$qvod','\u7B2C11\u96C6$qvod://367243170|B04C568470FDC029044AC911601C94A5D9D94464|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP11.540P.HDTV.mkv|$qvod','\u7B2C12\u96C6$qvod://367060270|A0498B34E2DA681BDE7903BF8C4983AED6D72D84|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP12.540P.HDTV.mkv|$qvod','\u7B2C13\u96C6$qvod://367208628|8537D4D97685FBA63F23BFF7B3A7A60C77547A0A|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP13.540P.HDTV.mkv|$qvod','\u7B2C14\u96C6$qvod://367129044|3C877BF0CE9C159B1C0F2DF89DD744F505734B86|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP14.540P.HDTV.mkv|$qvod','\u7B2C15\u96C6$qvod://367389400|01C82B5EF43E8F514BCEB3D776BB077D257AA263|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP15.540P.HDTV.mkv|$qvod','\u7B2C16\u96C6$qvod://367092955|CCD5B0E639ED8E12A11A19D28DA20D95BCA3D1B3|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP16.540P.HDTV.mkv|$qvod','\u7B2C17\u96C6$qvod://367240246|230DF5A12A7E176A5AF332DCBD9D34FBA554D332|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP17.540P.HDTV.mkv|$qvod','\u7B2C18\u96C6$qvod://367245279|AC4932A62D696876C049F3CF49599883CAA04777|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP18.540P.HDTV.mkv|$qvod','\u7B2C19\u96C6$qvod://367277563|0CEDCC3C7135BC652AD0BD915971CB21B850C5D2|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP19.540P.HDTV.mkv|$qvod','\u7B2C20\u96C6$qvod://367268555|91CB37143A241EAA34E5EDABE0C1B4210473CEBC|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B.EP20.540P.HDTV.mkv|$qvod','\u7B2C21\u96C6$qvod://146571157|4D2798868FBF87198C3F235EE9F4586105649587|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B21.rmvb|$qvod','\u7B2C22\u96C6$qvod://141974945|BB8799D8966EFD320B9C30A467FEAF3E80D0157A|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B22.rmvb|$qvod','\u7B2C23\u96C6$qvod://150121456|3C0184243AC70E2A9594E0B7AA7B3183333FA65A|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B23.HDTV.rmvb|$qvod','\u7B2C24\u96C6$qvod://185612037|65D382A5C1E575691290482CF04F3110460C2A25|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B24.HDTV.rmvb| $qvod','\u7B2C25\u96C6$qvod://140599052|736CC2F7767F0881059674E6FC1D5C1BDFD1D0AF|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B25.HDTV.rmvb|$qvod','\u7B2C26\u96C6$qvod://132085842|DEA2313D934A289E0046239FB66CE4FA54026786|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B26.HDTV.rmvb| $qvod']],['qvod',['\u7B2C01\u96C6$qvod://135311019|83233E4290F0D698193474C8F1177A4C40C8F38F|\u65B0\u7F16\u8F91\u7684\u90E8\u6545\u4E8B01.rmvb|$qvod','\u7B2C02\u96C6$qvod://130649414|92027E693C69F2A47D23FE37430FFC4A8A34A243|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B02.rmvb|$qvod','\u7B2C03\u96C6$qvod://157652911|23B9C89586E5432B208A57F84421789788E24236|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B03.rmvb|$qvod','\u7B2C04\u96C6$qvod://157652911|2D766A810CA7E822040D0EFFD7D0B2D49A94FBC7|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B04.rmvb|$qvod','\u7B2C05\u96C6$qvod://223982379|D9D50E60735773213CA5F71A0A7FFBFB10A83B34|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B05.rmvb|$qvod','\u7B2C06\u96C6$qvod://255880602|7AE47DA7C39AA02981384027B344BF88925DF276|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B06.rmvb|$qvod','\u7B2C07\u96C6$qvod://254006903|92F7D7CC087509EF3F6EC83589F7B3BA9574F1F9|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B07.rmvb|$qvod','\u7B2C08\u96C6$qvod://284178300|F5DEBC2D830C482CE3B54929B0A01CF27C643DDC|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B08.rmvb|$qvod','\u7B2C09\u96C6$qvod://273062330|7C199283F4F856B0008930874E1C5481F6DFB8E7|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B09.rmvb|$qvod','\u7B2C10\u96C6$qvod://270334422|16FE5E1A9033190DD82C51003FDEB61BF7ED0FDB|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B10.rmvb|$qvod','\u7B2C11\u96C6$qvod://268079981|65D50DF4EAF61F698DB1F083E733458536212D30|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B11.rmvb|$qvod','\u7B2C12\u96C6$qvod://280714923|13E592DF5FC83586DB628FE6C76EDE3F76372D2D|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B12.rmvb|$qvod','\u7B2C13\u96C6$qvod://208866971|E6774B4F1218F49F8C99023BC463EEE5FED5A3A5|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B13.HDTV.rmvb|$qvod','\u7B2C14\u96C6$qvod://259773680|3D3D35E821805BF0BAE34DE121C8FFB2A1B08348|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B\u9AD8\u6E0514.rmvb|$qvod','\u7B2C15\u96C6$qvod://138397384|63F376D29B1EAACEE8B6F3A464BE3384796A3655|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B15.HDTV.rmvb| $qvod','\u7B2C16\u96C6$qvod://281584484|F8CCC09ED655F24953F658606E8F0C109D8FA070|\u65B0\u7F16\u8F91\u90E8\u7684\u6545\u4E8B\u9AD8\u6E0516.rmvb|$qvod','\u7B2C17\u96C6$qvod://193760644|EBC8C73DD888B0B83A9D7CF749D06ECB847D10AB|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B17.rmvb|$qvod','\u7B2C18\u96C6$qvod://641809657|7E1E5433A70F11973B1E775897A4BBC770AC6376|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B18.HDTV.mp4|$qvod','\u7B2C19\u96C6$qvod://157095733|680DBC4324C4F69AF612C5448A99A1EBA6EBBEFF|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B19rmvb|$qvod','\u7B2C20\u96C6$qvod://140530560|9F0C030509AAE6EF763FFE15ADEFAA59A9CDEA6C|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B20.rmvb|$qvod','\u7B2C21\u96C6$qvod://146571157|4D2798868FBF87198C3F235EE9F4586105649587|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B21.rmvb|$qvod','\u7B2C22\u96C6$qvod://141974945|BB8799D8966EFD320B9C30A467FEAF3E80D0157A|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B22.rmvb|$qvod']],['qvod',['QMV01\u96C6$qvod://234638524|B55D38AD72327864DBD9C07F82377B9EFF231EBF|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B1.QMV|$qvod','QMV02\u96C6$qvod://225816357|CAFD95ACECE045B1B9F9AF50AD4DE9ECB3BD04E1|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B2.QMV|$qvod','QMV03\u96C6$qvod://233590584|1C4D5E34EFBAF68AAED501F1D67117C2C6B5729D|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B3.QMV| $qvod','QMV04\u96C6$qvod://234349277|3A2B84AA4E3394FD058C50E9B02719AD51145446|\u65B0\u7F16\u8F91\u90E8\u6545\u4E8B04.QMV|$qvod']]],urlinfo='http://'+document.domain+'/play/40958.html?40958-<from>-<pos>';";
		//par.parseVideVolumn(video, 1);
		//par.searchWebSite();
		//par.searchUpdate();
		par.testGetVideo("http://www.xigua110.com/drama/index5.htm");
	}

	
}
