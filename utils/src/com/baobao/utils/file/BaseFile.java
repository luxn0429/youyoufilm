package com.baobao.utils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 解析XML文件的基类
 * @author 卢相宁
 * @since  08.06.15
 */
public abstract class BaseFile {
	
	//根节点名字
	protected String root; 
	//xml文件路径
	protected String path;
	//XML中记录信息的节点名
	protected String nodeName;
	//记录文件是否被修改过
	protected boolean dirty;
	//读写锁
	protected final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	/**
	 * 得到XML文件的解析
	 * @return       dom4j XML解析句柄
	 */
	protected Document getDocument(){
		if(null == path){
			Logger.getLogger(BaseFile.class).error("path is null");
			return null;
		}
		Document doc = null;
		File config = new File(path);
		try{
			if(!config.exists()){ //如果文件不存在，则创建一个
				doc = DocumentHelper.createDocument();
				doc.addElement(root);
				writeDocument(doc); //记录文件
			}else{
				//use DOM4j to parse XML file
				SAXReader reader = new SAXReader();
				reader.setEncoding("UTF-8");
				doc = reader.read(config);
			}
		}catch(Exception e){
			e.printStackTrace();
			Logger.getLogger(BaseFile.class).error(e.getMessage());
			return null;
		}
		return doc;
	}
	
	/**
	 * 保存XML文件
	 * @param  doc   dom4j的文件流
	 * @return       true or false
	 */
	protected boolean writeDocument(Document doc){
		XMLWriter writer = null;
		try {
			//创建写出流      
			FileOutputStream out = new FileOutputStream(path);
			// 格式化输出       
			OutputFormat format = OutputFormat.createPrettyPrint();            
			format.setEncoding("UTF-8");            
			//创建写出对象            
			writer = new XMLWriter(out,format);           
			writer.write(doc);   
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(BaseFile.class).error(e.getMessage());
			return false;
		}finally{
			if(null != writer)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
		}           
		return true;
	}
	
	/**
	 * 初始化文件
	 * @return   初始化成功与否
	 */
	protected abstract boolean init();
	
	/**
	 * 存储文件数据
	 * @return   存储成功与否
	 */
	public abstract boolean storeData();

	/**
	 * 存储数据
	 * @param dirty  如何存储
	 * @return       true or false
	 */
	public boolean storeData(boolean dirty){
		setDirty(dirty);
		return this.storeData();
	}
	
	/**
	 * 加载数据
	 * @return   true or false
	 */
	public boolean loadData(){
		return this.init();
	}
	
	/**
	 * 判断文件是否修改过
	 * @return
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	/**
	 * 设置文件为修改过
	 * @param dirty
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
