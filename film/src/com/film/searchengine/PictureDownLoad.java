package com.film.searchengine;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

import com.baobao.utils.*;

public class PictureDownLoad {
	private static final String PATH = "/var/www/html/";

	/**
	 * 下载图片
	 * @param imageUrl	图片链接
	 * @return			图片保存位置
	 */
	public static String downLoadPic(String imageUrl){
		
		DataInputStream dis = null;
		FileOutputStream fos = null;
		try{
			
			File dir = new File(PATH);
			if(!dir.exists())
				dir.mkdirs();
			
			Date date = new Date();
			/////图片存储每个月的图片存在一个文件夹中
			String dirDate = DateParser.getInstance().getYM(date.getTime());
			/////如果文件夹不存在则新建一个
			File dirFile = new File(PATH+dirDate);
			if(!dirFile.exists())
				dirFile.mkdir();
			
			URL url = new URL(imageUrl);
		    //打开网络输入流
		    dis = new DataInputStream(url.openStream());
		   
		    
		    String sufix = imageUrl.substring(imageUrl.lastIndexOf(".")+1);
		    
		    //建立一个新的文件
		    String fileName = MD5.StringToMd5String(imageUrl)+"."+sufix;
		    fos = new FileOutputStream(new File(PATH+"/"+dirDate+"/"+fileName));
		    byte[] buffer = new byte[1024];
		    int length;
		    //开始填充数据
		    while( (length = dis.read(buffer))>0){
		    	fos.write(buffer,0,length);
		    }
		    Logger.getLogger("download picture success:"+dirDate+"/"+fileName);
		    return dirDate+"/"+fileName;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			 try {
				dis.close();
				fos.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String imageUrl = "http://ww3.sinaimg.cn/thumbnail/6374753ajw1e1zj0yamxgj.jpg";
		PictureDownLoad.downLoadPic(imageUrl);
	}
}
