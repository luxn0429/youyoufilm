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

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
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
public class XiguaParser extends SearchEngine {
	private static final String encode = "GB18030";
	private static ExecutorService EXEC=Executors.newFixedThreadPool(50);
	
	public XiguaParser(String url,String updateUrl){
		super(url,updateUrl);
	}

	/* (non-Javadoc)
	 * @see com.film.searchengine.SearchEngine#parseHome()
	 */
	@Override
	protected void searchWebSite() {
		String html = SearchWebPageUtil.getUrlUseProxyContent(this.url,encode);
		Document doc = Jsoup.parse(html);
		
		Elements nav = doc.getElementsByAttributeValue("class","navtop");
		Element navNode = nav.first();
		if(null == navNode){
			Logger.getLogger(this.getClass()).error("type is error modify quickly!");
			return;
		}
		Elements links = navNode.getElementsByTag("a");
		for(int i=0;i<links.size();i++){
			Element link = links.get(i);
			String href = link.attr("href");
			///解析电视剧
			if(href.contains("korea")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.KOREA,ConstantUtil.CATEGORY.KOREA,href));
			}else if(href.contains("occident")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.AMERICA,ConstantUtil.CATEGORY.AMERICA,href));
			}else if(href.contains("tvb")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.HONGKONG,ConstantUtil.CATEGORY.HONGKONG,href));
			}else if(href.contains("idol")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.TAIWAN,ConstantUtil.CATEGORY.TAIWAN,href));
			}else if(href.contains("japan")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.JAPA,ConstantUtil.CATEGORY.JAPA,href));
			}else if(href.contains("inland")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.CHINA,ConstantUtil.CATEGORY.CHINA,href));
			}else if(href.contains("singapore")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.SINGAPORE,ConstantUtil.CATEGORY.SINGAPORE,href));
			}else if(href.contains("korea")){
				EXEC.submit(new GetVideos(ConstantUtil.COUNTRY.KOREA,ConstantUtil.CATEGORY.KOREA,href));
			}else if(href.contains("cartoon")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.CARTOON,href));
			}
		}
		
		Elements film = doc.getElementsByAttributeValue("class", "navbottom");
		Element filmNode = film.first();
		if(null == filmNode)
			return;
		Elements flinks = filmNode.getElementsByTag("a");
		for(int i=0;i<flinks.size();i++){
			Element link = flinks.get(i);
			String href = link.attr("href");
			///解析电影
			if(href.contains("action")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.ACTION,href));
			}else if(href.contains("fiction")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.FICTION,href));
			}else if(href.contains("horror")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.HORROR,href));
			}else if(href.contains("comed")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.COMED,href));
			}else if(href.contains("romance")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.ROMANCE,href));
			}else if(href.contains("drama")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.DRAMA,href));
			}else if(href.contains("war")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.WAR,href));
			}else if(href.contains("fun")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.FUN,href));
			}else if(href.contains("superstar")){
				EXEC.submit(new GetVideos(null,ConstantUtil.CATEGORY.SUPERSTAR,href));
			}
		}
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
				String url = parseVideo(path);
				while(null != url){
					url = parseVideo(url);
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
			Element videoDoc = doc.getElementsByAttributeValue("class", "vodlist_l box").first();
			if(null != videoDoc){
				Elements elements = videoDoc.getElementsByTag("ul");
				Element videoULElement = null;
				if(elements.size()>1){
					videoULElement = elements.get(1);
				}else
					videoULElement = elements.first();
					
				Elements videoList = videoULElement.getElementsByTag("li");
				for(Element video:videoList){
					Element descriptionUrl = video.getElementsByTag("a").first();
					String desPath = descriptionUrl.attr("href");
					////得到bean的产地
					VideoBean bean = new VideoBean();
					if(null != country){
						bean.setCountry(country.getIndex());
					}
					else{
						Element countryElement = descriptionUrl.nextElementSibling();
						Elements hlist = countryElement.children();
						for(Element e:hlist){
							String text = e.text();
							if(text.contains("地区")){
								if(text.contains("韩国")){
									bean.setCountry(ConstantUtil.COUNTRY.KOREA.getIndex());
								}else if(text.contains("欧美")){
									bean.setCountry(ConstantUtil.COUNTRY.AMERICA.getIndex());
								}else if(text.contains("日本")){
									bean.setCountry(ConstantUtil.COUNTRY.JAPA.getIndex());
								}else if(text.contains("大陆")){
									bean.setCountry(ConstantUtil.COUNTRY.CHINA.getIndex());
								}else if(text.contains("香港")){
									bean.setCountry(ConstantUtil.COUNTRY.HONGKONG.getIndex());
								}else if(text.contains("台湾")){
									bean.setCountry(ConstantUtil.COUNTRY.TAIWAN.getIndex());
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
					getVideoBean(desPath,bean);
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
			Element video = doc.getElementsByAttributeValue("class", "vod_l").first();
			Element pictureElement = video.getElementsByAttributeValue("class", "pic").first();
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
			Element statElement = pictureElement.nextElementSibling().nextElementSibling();
			while(statElement!= null){
				String stateText = statElement.text();
				if(stateText.contains("影片状态")){
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
				}else if(stateText.contains("对白语言")){
					////发音
					if(stateText.contains("英语发音")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.AMERICA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.ENGLISH.getIndex());
					}else if(stateText.contains("普通话发音")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.CHINA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.CHINESE.getIndex());
					}else if(stateText.contains("韩语发音")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.KOREA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.KOREA.getIndex());
					}else if(stateText.contains("日语发音")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.JAPA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.JAPANESE.getIndex());
					}else if(stateText.contains("粤语发音")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.HONGKONG.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.GUANGDONG.getIndex());
					}else if(stateText.contains("泰语发音")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.THAI.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.THAI.getIndex());
					}else if(stateText.contains("俄罗斯")){
						if(bean.getCountry()<=0)
							bean.setCountry(ConstantUtil.COUNTRY.RUSSIA.getIndex());
						bean.setLanguage(ConstantUtil.LANGUAGE.RUSSIA.getIndex());
					}
					//字幕
					if(stateText.contains("中文字幕")){
						bean.setCaption(ConstantUtil.LANGUAGE.CHINESE.getIndex());
					}else if(stateText.contains("英文字幕")){
						bean.setCaption(ConstantUtil.LANGUAGE.ENGLISH.getIndex());
					}else
						bean.setCaption(0);
				}else if(stateText.contains("上映年份")){
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
				}else if(stateText.contains("连载说明")){
					int i = stateText.indexOf("：");
					if(i<0)
						i = stateText.indexOf(":");
					stateText = stateText.substring(i+1);
					if(stateText.contains("连载")){
						bean.setState(ConstantUtil.STATE.INSERIOUS.getIndex());
					}else bean.setState(ConstantUtil.STATE.FINISH.getIndex());
					bean.setSeriousIntro(stateText);
				}else if(stateText.contains("主演")){
					String actor = stateText.substring(stateText.indexOf("：")+1);
					if(null == actor || actor.trim().length() == 0)
						actor = stateText.substring(stateText.indexOf(":")+1);
					bean.setPerformer(actor);
				}
				statElement = statElement.nextElementSibling();
			}
			
			Element introduceElement = doc.getElementsByAttributeValue("class","vod_content").first();
			if(null != introduceElement){
				Elements pEl = introduceElement.getElementsByTag("p");
				if(pEl != null && pEl.size()>0){
					String intro = pEl.first().text();
					bean.setIntroduction(intro.trim());
				}else{
					bean.setIntroduction(introduceElement.text().trim());
				}
			}
			int id = DaoFactory.getInstance().getVideoDAO().insert(bean);
			if(id <0){
				id = (int)DaoFactory.getInstance().getVideoDAO().getVideoID(bean.getName());
				if(id<0) return;
			}
			Map<Integer,List<VolumeBean>> volumeResult = parseVolume(doc,id);
			if(!DaoFactory.getInstance().getVolumeDAO().insert(volumeResult))
				return;
		}
		/**
		 * 解析电影集，返回对应播放手段与集的对应，集已按照第一到最后排序好
		 * @param document
		 * @return
		 */
		private Map<Integer,List<VolumeBean>> parseVolume(Document document,int videoID){
			Elements videoList = document.getElementsByAttributeValue("class","playlist wbox");
			
			if(null != videoList){
				Element videoHref = videoList.first();
				Elements links = videoHref.getElementsByTag("a");
				if(null != links){
					try{
						Element href = links.first();
						String path = href.attr("href");
						if(path.equals("http"))
							return null;
						String page = SearchWebPageUtil.getUrlUseProxyContent(url+path,encode);
						Document docNative = Jsoup.parse(page);
						Elements playing = docNative.getElementsByAttributeValue("class", "playing");
						if(null != playing && playing.first()!=null){
							Element play = playing.first();
							
							Elements script = play.getElementsByTag("script");
							for(int i=0;i<script.size();i++){
								Element temp = script.get(i);
								String scriptSrc = temp.attr("src");
								if(scriptSrc == null || scriptSrc.trim().length() == 0)
									continue;
								String volumnString = SearchWebPageUtil.getUrlUseProxyContent(url+scriptSrc,encode);
								return parseVideVolumn(volumnString,videoID);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
						Logger.getLogger(this.getClass()).info(document.html());
					}
				}
				
			}
			return null;
		}
		
		
		public  Map<Integer,List<VolumeBean>> parseVideVolumn(String volumn,int video){
			String volumnStr = EscapeUnescape.unescape(volumn);
			int start = volumnStr.indexOf("'");
			int end = volumnStr.lastIndexOf("'");
			if(start>=0)
				volumnStr = volumnStr.substring(start+1, end);
			Map<Integer,List<VolumeBean>> result = new HashMap<Integer,List<VolumeBean>>();
			String[] platform = volumnStr.split("\\$\\$\\$");
			for(String p:platform){
				///平台与集的对应
				
				String[] p2v = p.split("\\$\\$");
				if(p2v.length <=1){
					p2v = p.split("\\$");
				}
				int playerPlat = 0;
				if(p2v[0].contains("百度")||p.toLowerCase().contains("bdhd")){
					playerPlat = PLATFORM.BAIDU.getIndex();
				}else if(p2v[0].contains("优酷") || p.toLowerCase().contains("youku")){
					playerPlat = PLATFORM.YOUKU.getIndex();
				}else if(p2v[0].contains("土豆") || p.toLowerCase().contains("tudou")){
					playerPlat = PLATFORM.TUDOU.getIndex();
				}else if(p2v[0].toLowerCase().contains("qvod") || p.toLowerCase().contains("qvod")){
					playerPlat = PLATFORM.QVOD.getIndex();
				}
				List<VolumeBean> list = result.get(playerPlat);
				if(null == list){
					list = new ArrayList<VolumeBean>();
					result.put(playerPlat,list);
				}
				String[] contents = p2v[1].split("#");
				for(String content:contents){
					String[] temp = content.split("\\$");
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
			return result;
		}
	}

	
	public void searchUpdate(){
		String html = SearchWebPageUtil.getUrlUseProxyContent(this.url+"/"+this.updateUrl,encode);
		Document doc = Jsoup.parse(html);
		Elements nav = doc.getElementsByAttributeValue("class","box");
		for(int i=0;i<nav.size();i++){
			Element element = nav.get(i);
			
			Elements videos = element.getElementsByTag("li");
			
			for(int j=0;j<videos.size();j++){
				Element video = videos.get(j);
				Elements links = video.getElementsByTag("a");
				Element link = null;
				if(links.size()>0)
					link = links.first();
				String href = link.attr("href");
				
				VideoBean videoBean = new VideoBean();
				///解析电影
				if(href.contains("action")){
				}else if(href.contains("fiction")){
				}else if(href.contains("horror")){
				}else if(href.contains("comed")){
				}else if(href.contains("romance")){
				}else if(href.contains("drama")){
				}else if(href.contains("war")){
				}else if(href.contains("fun")){
				}else if(href.contains("superstar")){
				}else if(href.contains("korea")){ ///解析电视剧
					videoBean.setCountry(ConstantUtil.COUNTRY.KOREA.getIndex());
				}else if(href.contains("occident")){
					videoBean.setCountry(ConstantUtil.COUNTRY.AMERICA.getIndex());
				}else if(href.contains("tvb")){
					videoBean.setCountry(ConstantUtil.COUNTRY.HONGKONG.getIndex());
				}else if(href.contains("idol")){
					videoBean.setCountry(ConstantUtil.COUNTRY.TAIWAN.getIndex());
				}else if(href.contains("japan")){
					videoBean.setCountry(ConstantUtil.COUNTRY.JAPA.getIndex());
				}else if(href.contains("inland")){
					videoBean.setCountry(ConstantUtil.COUNTRY.CHINA.getIndex());
				}else if(href.contains("singapore")){
					videoBean.setCountry(ConstantUtil.COUNTRY.KOREA.getIndex());
				}else if(href.contains("korea")){
					videoBean.setCountry(ConstantUtil.COUNTRY.KOREA.getIndex());
				}else if(href.contains("cartoon")){
					videoBean.setCountry(ConstantUtil.CATEGORY.CARTOON.getIndex());
				}
				EXEC.submit(new GetVideos(href,videoBean));
			}
		}
	}
	
	public static void main(String[] args){
		XiguaParser par = new XiguaParser("","");
		
		String video ="%u767E%u5EA6%u5F71%u97F3%24%24%u7B2C1%u96C6%24bdhd%3A//69931115%7C0808B181FE2D21BA4189076D529F423D%7C%u8D75%u672C%u5C71.1%u52A01%u7B49%u4E8E.rmvb%24baidu%23%u7B2C2%u96C6%24bdhd%3A//49684427%7C6D0F3C0D6DA9CF9242EEBB7AF64401B6%7C%u8D75%u672C%u5C71.%u4E09%u97AD%u5B50.rmvb%24baidu%23%u7B2C3%u96C6%24bdhd%3A//138012591%7CD3447B53C7AF759B87B2AB18584115B2%7C%u8D75%u672C%u5C71.%u4E0D%u5DEE%u94B1.rmvb%24baidu%23%u7B2C4%u96C6%24bdhd%3A//33061998%7C2FA1428618B97E9578358EF835B2984A%7C%u8D75%u672C%u5C71.%u513F%u5B50%u5927%u4E86.rmvb%24baidu%23%u7B2C5%u96C6%24bdhd%3A//61563589%7CB39AD16F91B7C60D1A05E2D27A7239F3%7C%u8D75%u672C%u5C71.%u529E%u73ED.rmvb%24baidu%23%u7B2C6%u96C6%24bdhd%3A//77184210%7C36D03CBB78F759CA042DE7FD9AEABB74%7C%u8D75%u672C%u5C71.%u529F%u592B.rmvb%24baidu%23%u7B2C7%u96C6%24bdhd%3A//44278152%7CA5FA9A58D517033CE5CD8CE0492A3718%7C%u8D75%u672C%u5C71.%u5356%u62D0.rmvb%24baidu%23%u7B2C8%u96C6%24bdhd%3A//68793588%7CA9D25E5D4686BABFB503152D31A760D5%7C%u8D75%u672C%u5C71.%u5356%u8F66.rmvb%24baidu%23%u7B2C9%u96C6%24bdhd%3A//30382611%7CBD7BD3A02F2C917B2855608F9854ADAB%7C%u8D75%u672C%u5C71.%u57CE%u5E02%u6253%u5DE5%u59B9.rmvb%24baidu%23%u7B2C10%u96C6%24bdhd%3A//40152697%7C454C121FE0C2116187756EA2A7197C11%7C%u8D75%u672C%u5C71.%u5982%u6B64%u7ADE%u4E89.RM%24baidu%23%u7B2C11%u96C6%24bdhd%3A//49778818%7C37871119D0C6AA44E18DE1C664FB0F7F%7C%u8D75%u672C%u5C71.%u5C0F%u4E5D%u8001%u4E50.rmvb%24baidu%23%u7B2C12%u96C6%24bdhd%3A//31914783%7CD5FD49D4576D4074609C4B42EB0929A3%7C%u8D75%u672C%u5C71.%u5E74%u524D%u5E74%u540E.rmvb%24baidu%23%u7B2C13%u96C6%24bdhd%3A//68726547%7CF7618D149DC524AE3047D3D846A62180%7C%u8D75%u672C%u5C71.%u5FC3%u75C5.rmvb%24baidu%23%u7B2C14%u96C6%24bdhd%3A//74701866%7CC67E8934F66061D2210D9167DEE25FDF%7C%u8D75%u672C%u5C71.%u6211%u60F3%u6709%u4E2A%u5BB6.rmvb%24baidu%23%u7B2C15%u96C6%24bdhd%3A//76286430%7C65BF722FB5FF6331D0A92BB946BA16AF%7C%u8D75%u672C%u5C71.%u62DC%u5E74.rmvb%24baidu%23%u7B2C16%u96C6%24bdhd%3A//118938201%7CEFBC5EFD3CAE0B4C98552D8EE5C14CF3%7C%u8D75%u672C%u5C71.%u6350%u52A9.rmvb%24baidu%23%u7B2C17%u96C6%24bdhd%3A//86014243%7C12368756AEE272E8642E5B90534BF594%7C%u8D75%u672C%u5C71.%u6628%u5929%u4ECA%u5929%u660E%u5929.rmvb%24baidu%23%u7B2C18%u96C6%24bdhd%3A//44752496%7C9BB945AAD8B4F17AE0A20A221C311479%7C%u8D75%u672C%u5C71.%u6709%u94B1%u4E86.rmvb%24baidu%23%u7B2C19%u96C6%24bdhd%3A//30253792%7CF1AE5F5E9AB74C2C64F5DA12E7C01BA1%7C%u8D75%u672C%u5C71.%u6F14%u5458%u7684%u70E6%u607C.rm%24baidu%23%u7B2C20%u96C6%24bdhd%3A//102659904%7C61834CD039DFED1C0B660FAD1FCAE99B%7C%u8D75%u672C%u5C71.%u706B%u70AC%u624B.rmvb%24baidu%23%u7B2C21%u96C6%24bdhd%3A//36742380%7C280B5F11188EFAEB7391F7A94D67C512%7C%u8D75%u672C%u5C71.%u725B%u5927%u53D4%u63D0%u5E72.rmvb%24baidu%23%u7B2C22%u96C6%24bdhd%3A//45303738%7C9014ACDB45855F3F9BF5C67EFE6252F9%7C%u8D75%u672C%u5C71.%u76F8%u4EB2.rm%24baidu%23%u7B2C23%u96C6%24bdhd%3A//77289527%7C7D63FC2F8CB98F17F86DA7BAF2998ED1%7C%u8D75%u672C%u5C71.%u7B56%u5212.rmvb%24baidu%23%u7B2C24%u96C6%24bdhd%3A//26747816%7C442BDED43DFE5871A26550F2E062B67F%7C%u8D75%u672C%u5C71.%u7EA2%u9AD8%u7CB1%u6A21%u7279%u961F.rm%24baidu%23%u7B2C25%u96C6%24bdhd%3A//4131333%7C29CC29AA24677C7DC93E5841D9036DA7%7C%u8D75%u672C%u5C71.%u8001%u4F34.wmv%24baidu%23%u7B2C26%u96C6%24bdhd%3A//59404494%7C053A29970F19D7B0B392D1226F95CDCB%7C%u8D75%u672C%u5C71.%u8001%u62DC%u5E74.rmvb%24baidu%23%u7B2C27%u96C6%24bdhd%3A//43598962%7C216B20AC8651864239964CC7A44C83DD%7C%u8D75%u672C%u5C71.%u8001%u6709%u5C11%u5FC3.RM%24baidu%23%u7B2C28%u96C6%24bdhd%3A//17071084%7C9123B35FAF3EDD84144A325B70271A2B%7C%u8D75%u672C%u5C71.%u8001%u852B%u5B8C%u5A5A.rm%24baidu%23%u7B2C29%u96C6%24bdhd%3A//30515290%7C674035892F0024392975142A2AD690CF%7C%u8D75%u672C%u5C71.%u9001%u6C34.rmvb%24baidu%23%u7B2C30%u96C6%24bdhd%3A//43904731%7CB5854780225890A0576C76FACB6C6553%7C%u8D75%u672C%u5C71.%u949F%u70B9%u5DE5.rmvb%24baidu%23%u7B2C31%u96C6%24bdhd%3A//26122692%7C27B91239A9DED4915888FE4FDCD22350%7C%u8D75%u672C%u5C71.%u95E8.rmvb%24baidu%23%u7B2C32%u96C6%24bdhd%3A//120266381%7C8014F8B6BC194E15C691FC642B0FAC1D%7C%u8D75%u672C%u5C71.%u540C%u684C%u7684%u4F60.rmvb%24baidu%24%24%24%u571F%u8C46%24%24%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u4E0D%u5DEE%u94B1%2426403590%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u8BF4%u4E8B%u513F%2426169668%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u529F%u592B%2426166826%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u9001%u6C34%u5DE5%2426169666%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5FC3%u75C5%2426170311%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5356%u8F66%2426168655%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5356%u62D0%2426168486%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u949F%u70B9%u5DE5%2426170430%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u6628%u5929%u4ECA%u5929%u660E%u5929%2426170678%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u62DC%u5E74%2426164873%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u7EA2%u9AD8%u7CB1%u6A21%u7279%u961F%2426167529%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u4E09%u97AD%u5B50%2426169379%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u534E%u5927%u53D4%u201C%u63D0%u5E72%u201D%2426167359%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u8001%u62DC%u5E74%2426168223%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u6211%u60F3%u6709%u4E2A%u5BB6%2426169968%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5C0F%u4E5D%u8001%u4E50%2426170073%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u76F8%u4EB2%2426169977%24tudou%23%u8D75%u672C%u5C71-%u65B0%u7F16%u5927%u5FFD%u60A0%2429860980%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-+%u751F%u65E5%u5FEB%u4E50%2426553929%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u540C%u5B66%u4F1A%2426169777%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u7B11%u661F%u62DC%u5E74%2426170004%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5C0F%u8349%2426169871%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u6709%u94B1%u4E86%2426170701%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5E74%u524D%u5E74%u540E%2426169124%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u53CC%u7C27%2426169197%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5982%u6B64%u7ADF%u4E89%2426169096%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u9A71%u90AA%2426169020%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u9762%u5B50%2426168847%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u95E8%u795E%2426168766%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5356%u68A8%2426168751%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u8001%u852B%u5B8C%u5A5A%2426168385%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u8001%u6839%u65AD%u6848%2426168159%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u8001%u4F34%2426167775%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u513F%u5B50%u5927%u4E86%2426166713%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u51FA%u540D%2426166555%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u6F14%u5458%u7684%u70E6%u607C%2426170197%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u57CE%u5E02%u6253%u5DE5%u59B9%2426165957%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u5B9D%u5EA7%2426165430%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C61+1%3D%3F%2426165119%24tudou%23%u8D75%u672C%u5C71%u6625%u665A%u5C0F%u54C1%u5408%u96C6-%u529E%u73ED%2426165066%24tudou%23%u8D75%u672C%u5C71-%u300A%u9001%u620F%u300B%2429859641%24tudou%23%u8D75%u672C%u5C71-%u300A%u8FC7%u5E74%u4E86%u300B%2429859422%24tudou%23%u8D75%u672C%u5C71-%u6709%u75C5%u6CA1%u75C5%2429859222%24tudou%23%u8D75%u672C%u5C71-%u8B66%u5BDF%u4E0E%u7763%u5BDF%2429858980%24tudou%23%u8D75%u672C%u5C71-%u8BF4%u60C5%2429858928%24tudou%23%u8D75%u672C%u5C71-%u300A%u4E71%u6536%u8D39%u300B%2429858771%24tudou%23%u8D75%u672C%u5C71-%u300A%u6350%u732E%u300B%2429858698%24tudou%23%u8D75%u672C%u5C71-%u300A%u8F85%u5BFC%u300B%2429858629%24tudou%23%u8D75%u672C%u5C71-%u8001%u6709%u5C11%u5FC3%2429858447%24tudou%23%u8D75%u672C%u5C71-%u300A%u778E%u6405%u548C%u300B%2429858222%24tudou%23%u8D75%u672C%u5C71-%u300A%u7A0E%u7F18%u300B%2429858096%24tudou%23%u5927%u89C2%u706F%uFF08%u7247%u6BB5%uFF09%uFF08%u8D75%u672C%u5C71%uFF0C%u6F58%u957F%u6C5F%u65E9%u671F%u4F5C%u54C1%uFF09%2430196724%24tudou%23%u9EBB%u5C06%u8C46%u8150-%u8D75%u672C%u5C71%2430196620%24tudou";
	}
}
