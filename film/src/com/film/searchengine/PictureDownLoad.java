package com.film.searchengine;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class PictureDownLoad {
	private static final String PATH = "WebContent/pic/";

	public static String downLoadPic(String imageUrl){
		
		DataInputStream dis = null;
		FileOutputStream fos = null;
		try{
			URL url = new URL(imageUrl);
		    //打开网络输入流
		    dis = new DataInputStream(url.openStream());
		    File dir = new File(PATH);
		    int i= dir.listFiles().length+1;
		    String sufix = imageUrl.substring(imageUrl.lastIndexOf(".")+1);
		    //建立一个新的文件
		    String fileName = i+"."+sufix;
		    fos = new FileOutputStream(new File(PATH+fileName));
		    byte[] buffer = new byte[1024];
		    int length;
		    //开始填充数据
		    while( (length = dis.read(buffer))>0){
		    	fos.write(buffer,0,length);
		    }
		    return fileName;
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

	}
}
