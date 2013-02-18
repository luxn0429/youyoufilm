package com.baobao.utils;
public class IpChanger
{
	/* converter */
	static public String convert2String(long ip)
	{
		String[] seg=new String[4];
		seg[0]=String.valueOf( (ip&0xff000000l)>>24 );
		seg[1]=String.valueOf( (ip&0x00ff0000l)>>16 );
		seg[2]=String.valueOf( (ip&0x0000ff00l)>> 8 );
		seg[3]=String.valueOf( (ip&0x000000ffl)>> 0 );
		return seg[0]+"."+seg[1]+"."+seg[2]+"."+seg[3];
	}
	static public long convert2Long(String ip)
	{
		long nIP=0;
		String[] seg=ip.split("\\.");
		if ( seg.length!=4 )
			return -1;
		nIP = 	(Long.valueOf(seg[0].trim()).longValue()<<24) |
			(Long.valueOf(seg[1].trim()).longValue()<<16) |
			(Long.valueOf(seg[2].trim()).longValue()<< 8) |
			(Long.valueOf(seg[3].trim()).longValue()<< 0);
		return nIP;
	}
	
	public static void main(String[] args){
		System.out.println(convert2String(1902053706));
	}
}
