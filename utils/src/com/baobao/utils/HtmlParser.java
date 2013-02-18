package com.baobao.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HtmlParser {
	/** 
	 * 获取HTML的预览信息，其中content是对象的一个属性，也就是待处理的HTML内容
	 * @return  
	*/
	private static String re = "<[^>]*>";
	private static Pattern pattern = Pattern.compile(re);

	public static String getPreviewContent(String content,int length,boolean stripHTML){
		if(null == content || content.trim().length()==0 || 0 == length)
			return "";
		///如果不是截断HTML
        if(stripHTML){
        	Matcher matcher = pattern.matcher(content);
        	if(matcher.find())
        		content = matcher.replaceAll("");
        	content = content.replaceAll(" ", "").replaceAll(" ","");
        	
            if (content.length() <= length)
                return content;
            else
                return content.substring(0, length) + "……";
        }
        
        if (content.length() <= length)
            return content;

        StringBuffer sb = new StringBuffer();
        StringBuffer startTag  = new StringBuffer();
        int str_length = content.length();
        LinkedList stack = new LinkedList();
        int size = 0;
        boolean start = false;
        boolean end = false;
        for(int i=0;i<str_length;i++){
        	char ch = content.charAt(i);
        	if(ch == '<' && i+1 == content.length()){
        		break;
        	}
        	if(ch == '<' && content.charAt(i+1)=='/'){
        		end = true;
        	}else if(ch == '<' && content.charAt(i+1) != '/'){
        		start = true;
        	}else if(ch == '/' && content.charAt(i+1)=='>'){
        		start = false;
        		startTag = new StringBuffer();
        	}
        	if(start && ch=='>'){
        		startTag.append(ch);
        		stack.addFirst(startTag.toString());
        		startTag = new StringBuffer();
        		start = false;
        	}
        	
        	if(end && ch=='>'){
        		stack.removeFirst();
        		end = false;
        	}
        	
        	if(start)
        		startTag.append(ch);
        	sb.append(ch);
        	size ++;
        	if(size >= length)
        		break;
        }
       
        if(start){
        	int k = sb.lastIndexOf("<");
        	sb.delete(k,sb.length());
        }
        int lastLeft = sb.lastIndexOf("<");
        if(sb.lastIndexOf(">")<lastLeft)
        	sb.delete(lastLeft, sb.length());
        String tag = null; ;
        StringBuffer tagName = null;
        while(stack.getFirst() != null){
        	tag = stack.removeFirst().toString().toLowerCase();
        	tagName = new StringBuffer();
        	for(int i=1;i<tag.length();i++){
        		if(tag.charAt(i)<='z' && tag.charAt(i)>='a')
        			tagName.append(tag.charAt(i));
        		else break;
        	}
        	sb.append("</");
        	sb.append(tagName.toString().toLowerCase());
        	sb.append(">");
        }
        sb.append("……");
        return sb.toString();
	}
	
	////装换杂谈
	private static String getHref(String str){
    	String temp = str.substring(1,str.length()-1);
    	String[] ss = temp.split(":");
    	if(ss.length<2)
    		return "";
    	return "<img src=\"images/face/"+ss[1]+".gif"+"\" class=\"con_dimg\"/>";
    } 
    
//    private static Pattern pattern_mini = Pattern.compile("\\[[^\\s&&[^\\[]]+\\]");
    private static Pattern pattern_mini = Pattern.compile("\\[em:([0-9]|1\\d|2\\d|3[0-6])\\]");
    /**
     * 表情图片替换
     * @param path   图片路径
     * @param str	 含有表情的字符串
     * @return
     */
    public static String replace(String str){
    	Matcher matcher = pattern_mini.matcher(str);
    	if(matcher.find()){
    		str = matcher.replaceFirst(getHref(matcher.group()));
    		return replace(str);
    	}
    	return str;
    }
    
    private static Pattern img_pattern = Pattern.compile("<img.*?>|</img>",Pattern.CASE_INSENSITIVE);
    /**
     * 替换html img标签
     * @param str		输入串
     * @return			替换后的字符串
     */
    public static String remplaceImg(String str){
    	Matcher matcher = img_pattern.matcher(str);
    	if(matcher.find()){
    		return matcher.replaceAll("[图片]");
    	}
    	return str;
    }
    
    private static Pattern pattern_All = Pattern.compile("\\[[^\\s&&[^\\[]]+\\]");
    private static final int PATTERN_LENGTH = 7;
    
    private static  Map<String,String> TRANSFER = new HashMap<String,String>();
    static{
    	TRANSFER.put("<","&lt;");
		TRANSFER.put(">","&gt;");
		TRANSFER.put("\'","&apos;");
		TRANSFER.put("\"","&quot;");
		TRANSFER.put(" ","&nbsp;");
    }
    
    /**
     * 计算带有表情的内容长度
     * @param content		输入的内容
     * @param length		需要截取的长度
     * @return				截取结果
     */
    public static String calculateFace(String content,int length){
    	if(null == content)
    		return "";
    	
    	String tempContent = content.trim();
    	if(tempContent.length()<=length)
    		return tempContent;
    	for(Map.Entry<String,String> entry:TRANSFER.entrySet()){
    		tempContent = tempContent.replaceAll(entry.getValue(),entry.getKey());
    	}
    	
    	Matcher matcher = pattern_All.matcher(content);
    	if(!matcher.find()){
    		return tempContent.substring(0,length)+"....";
    	}
    	StringBuffer result = new StringBuffer();
    	for(int i=0;i<content.length();){
    		if(i>=length)
    			break;
    		String matchStr = matcher.group();
    		////检查匹配是否是定好的图片
    		if(matchStr.length()<=PATTERN_LENGTH){
    			String temp = tempContent.substring(0,tempContent.indexOf(matchStr));
        		if(temp.length()>0){
        			i+= temp.length();
            		if(i>length){
            			result.append(temp.substring(0,length-i+temp.length()));
            			break;
            		}else if(i==length){
            			result.append(temp);
            			break;
            		}else 
            			result.append(temp);
        		}
        		
        		tempContent = tempContent.substring(tempContent.indexOf(matchStr)+matchStr.length());
        		result.append(matchStr);
        		i++;
    		}else{
    			tempContent = tempContent.substring(tempContent.indexOf(matchStr)+matchStr.length());
    			int sublength = length - i;
    			if(matchStr.length()<sublength)
    				sublength = matchStr.length();
        		result.append(matchStr.substring(0, sublength));
        		i += sublength;
    		}
    		
    		///匹配字段用完了
    		matcher = pattern_All.matcher(tempContent);
    		if(!matcher.find()){
    			int sublength = length -i;
    			if(tempContent.length()<sublength){
    				sublength = tempContent.length();
    			}
    			result.append(tempContent.substring(0,sublength));
    			break;
    		}
    	}
    	result.append("....");
    	String rs_str = result.toString();
    	for(Map.Entry<String,String> entry:TRANSFER.entrySet()){
    		rs_str = rs_str.replaceAll(entry.getKey(),entry.getValue());
    	}
    	return rs_str;
    }
    
	public static void main(String[] args){
		FileReader reader = null;
		String in = "d:/dy_all.html";
		try {
			reader = new FileReader(in);
			BufferedReader  br = new BufferedReader(reader);
			String line = null;
			StringBuilder builder = new StringBuilder();
			while((line =br.readLine()) != null){
				builder.append(line);
			}
			System.out.println(HtmlParser.getPreviewContent(builder.toString().trim(),15,false));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		/*String a = "[sfs:dfsdf]按时打发士大夫[em:1][em:15][em:18]萨法是否[em:27][em:17][em:37][em:334]大事发生的法";
		System.out.println(HtmlParser.replace(a));
		
		String number = "[34]adf[36]dsf[1]UU[0]&&&[38]sdf[400][23][9][12]";
		
		Pattern pattern_All = Pattern.compile("\\[([0-9]|1\\d|2\\d|3[0-6])\\]");
		Matcher matcher = pattern_All.matcher(number);
		while(matcher.find()){
			System.out.println(matcher.group());
			number = matcher.replaceFirst("");
			matcher = pattern_All.matcher(number);
		}*/
	}
}
