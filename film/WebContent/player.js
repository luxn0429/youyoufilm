var playerw= window.parent.playerw;//播放器宽度
var playerh= window.parent.playerh;//播放器高度
var adsTime= window.parent.adsTime;//视频播放前广告时间，单位秒
var adsPage= window.parent.adsPage;//视频播放前广告页路径

var w3c = (document.getElementById) ? true : false;
var agt = navigator.userAgent.toLowerCase();
var ie = ((agt.indexOf("msie") != -1) && (agt.indexOf("opera") == -1) && (agt.indexOf("omniweb") == -1));
var ie5 = (w3c && ie) ? true : false;
var ns6 = (w3c && (navigator.appName == "Netscape")) ? true : false;
var op8 = (navigator.userAgent.toLowerCase().indexOf("opera") == -1) ? false : true;

try{top.moveTo(0,0);top.resizeTo(screen.availWidth,screen.availHeight);}catch(e){}

document.write('<iframe id="cciframe" style="width:0px;height:0px;overflow:hidden;position:absolute;top:0px;left:0px;z-index:9998;" frameborder="0" scrolling="no"></iframe>');

function GetXYWH(o){
	var nLt = 0;
	var nTp = 0;
	var offsetParent = o;
	while (offsetParent != null && offsetParent != document.body) {
		nLt += offsetParent.offsetLeft;
		nTp += offsetParent.offsetTop;
		if (!ns6) {
			parseInt(offsetParent.currentStyle.borderLeftWidth) > 0 ? nLt += parseInt(offsetParent.currentStyle.borderLeftWidth) : "";
			parseInt(offsetParent.currentStyle.borderTopWidth) > 0 ? nTp += parseInt(offsetParent.currentStyle.borderTopWidth) : "";
		}
		offsetParent = offsetParent.offsetParent;
	}
	//if (ie){nLt-=11;nTp-=10;}//定位不精确的时候可以在这里调整nLt左边距nTp上边距
   return  nTp + "," + nLt ;
}


function qvodCheck(){
	if(window.confirm('您的电脑未安装Qvodplayer播放软件,请点击下载安装后刷新本页面播放')){location.href='http://dl.qvod.com/QvodSetup360.exe';}
}

function getHtmlParasdt(suffix){
  var cur_url=location.href;
  var urlParas=location.search;
  if(cur_url.indexOf("?")>0){
	return urlParas.substring(urlParas.lastIndexOf('?')+1,urlParas.lastIndexOf(suffix)).split('-')
  }else {
  return cur_url.substring(cur_url.lastIndexOf("/")+1,cur_url.indexOf(suffix)).split('-') 
}
}

function getHtmlParas(suffix){
  var cur_url=location.href;
  var urlParas=location.search;
  if (cur_url.indexOf("?")>0){
	return urlParas.substring(urlParas.lastIndexOf(suffix)+1,urlParas.length).split('-')
  }else{
  return cur_url.substring(cur_url.lastIndexOf("/")+1,cur_url.indexOf(suffix)).split('-') 
}
}

function handleParas(para1,para2){
	var i,fromArray,len1,len2,urlArray,j,dataStr,dataArray
	if (isNaN(para1) || isNaN(para2)){return false}
	fromArray=VideoInfoList.split('$$$')
	len1=fromArray.length;if(para2>len1-1){para2=len1-1}
	for (i=0;i<len1;i++){if (para2==i){urlArray=fromArray[i].split('$$')[1].split('#');len2=urlArray.length;if(para1>len2-1){para1=len2-1};for (j=0;j<len2;j++){if (para1==j){dataStr=urlArray[j];dataArray=dataStr.split('$');return dataArray}}}}
}



function viewplay(para1,para2){//para2=来源组 para1＝集数
	var urlAndFrom,doc,url,from
	urlAndFrom=handleParas(para1-1,para2-1)
	//try{autoSubmitErr(urlAndFrom,para2)}catch(e){}
	url=escape(urlAndFrom[1]);from=urlAndFrom[2]
	doc =$("cciframe");
	document.write('<div id="qvodad" style="position:absolute;z-index:9999;"></div>');
	if(urlAndFrom[2].toLowerCase() == "qvod"){
		doc.src="/"+sitePath+"js/player/qvod.html?a="+url+"&b="+from+"&w="+playerw+"&h="+playerh;
	}else if(urlAndFrom[2].toLowerCase() == "swf"){
		doc.src="/"+sitePath+"js/player/swf.html?&&a="+url+"&&b="+from+"&&w="+playerw+"&&h="+playerh;
	}else{
		doc.src=adsPage;
		setTimeout(function(){doc.src="http://js.xigua110.com/js/player/"+from+".html?s=http://www.56.com/&a="+url+"&b="+from+"&w="+playerw+"&h="+playerh;},adsTime*1000);	
	}
	document.write('<div id="ccplay" style="width:'+playerw+'px;height:'+playerh+'px;"></div>');
	doc.style.width=playerw+"px";
	doc.style.height=playerh+"px";
	doc.style.top=GetXYWH(Obj('ccplay')).split(",")[0]+"px";
	doc.style.left=GetXYWH(Obj('ccplay')).split(",")[1]+"px";
}


function viewend(){
	var doc=$("cciframe");
	doc.style.top=GetXYWH(Obj('ccplay')).split(",")[0]+"px";
	doc.style.left=GetXYWH(Obj('ccplay')).split(",")[1]+"px";
}

function killErrors(){
	return true;
}

var resize=function(){
	var cObj=window.Obj('ccplay');
	orginLeft=window.GetXYWH(cObj).split(',')[1]+'px';
	var i=$('cciframe');
	i.style.left=orginLeft;
}

window.onresize=function(){resize();}
window.onerror = killErrors;

function PlayList(part,from){
	var addressArray,partArray,i,p,partlen,partnameArray
	addressArray=VideoInfoList.split('$$$');
	partArray=addressArray[from-1].split('$$')[1].split('#');
	partlen=partArray.length;
	for (i=0;i<partlen;i++){
		partnameArray=partArray[i].split('$');
		if (part==i+1){
			document.write("<li><font class='red'>"+partnameArray[0]+"</font></li>");
		}else{
			wdiii=i+1
			document.write("<li><a title='"+partnameArray[0]+"' href='"+play_vid+"-"+from+"-"+wdiii+".htm'>"+partnameArray[0]+"</a></li>");
		}
	}
}





function wdjs(para1,para2){
	para2=para2-1
	var wdi,wdfromArray,wdlen1,wdlen2,wdurlArray,wdj,wddataStr,wddataArray
	wdfromArray=VideoInfoList.split('$$$')
	wdlen1=wdfromArray.length;if(para2>wdlen1-1){para2=wdlen1-1}
	for (wdi=0;wdi<wdlen1;wdi++){if (para2==wdi){wdurlArray=wdfromArray[wdi].split('$$')[1].split('#');wdlen2=wdurlArray.length;if(wdfromArray[wdi].split('$$')[1].substring(wdfromArray[wdi].split('$$')[1].length-1)=='#'){wdlen2=wdlen2-1};return wdlen2;}}}


function dyj(para1,para2){
	para2=para2-1
	var wdi,wdfromArray,wdlen1,wdlen2,wdurlArray,wdj,wddataStr,wddataArray
	wdfromArray=VideoInfoList.split('$$$')
	wdlen1=wdfromArray.length;if(para2>wdlen1-1){para2=wdlen1-1}
	for (wdi=0;wdi<wdlen1;wdi++){if (para2==wdi){wdurlArray=wdfromArray[wdi].split('$$')[1].split('#');return wdurlArray[0].split('$')[0];}}}

function dej(para1,para2){
	para2=para2-1
	var wdi,wdfromArray,wdlen1,wdlen2,wdurlArray,wdj,wddataStr,wddataArray
	wdfromArray=VideoInfoList.split('$$$')
	wdlen1=wdfromArray.length;if(para2>wdlen1-1){para2=wdlen1-1}
	for (wdi=0;wdi<wdlen1;wdi++){if (para2==wdi){wdurlArray=wdfromArray[wdi].split('$$')[1].split('#');return wdurlArray[1].split('$')[0];}}}



function shang()
{
if(wdjs(paras[2],paras[1])>1){
if(dyj(paras[2],paras[1]).match(/\d+/ig)>dej(paras[2],paras[1]).match(/\d+/ig))
{
daoxuxia("没有上一集了");
}
else
{ 
daoxushang("没有上一集了");
}
}
else
{ 
daoxushang("没有上一集了");
}
}

function xia()
{
if(wdjs(paras[2],paras[1])>1){
if(dyj(paras[2],paras[1]).match(/\d+/ig)>dej(paras[2],paras[1]).match(/\d+/ig))
{
daoxushang("目前没有下一集了");
}
else
{ 
daoxuxia("目前没有下一集了");
}
}
else
{ 
daoxuxia("目前没有下一集了");
}
}

function daoxushang(wdts)
{
	var d = paras[2];
	var z = parseInt(paras[1]);
	var n = parseInt(paras[2]);

    if(n==1){
	alert(wdts);
	}
	else
	{ 
	var n=n-1
	 window.location.href=""+paras[0]+"-"+z+"-"+n+".htm";
		}
		}


function daoxuxia(wdts)
{
	var d = paras[2];
	var z = parseInt(paras[1]);
	var n = parseInt(paras[2]);
var m = wdjs(paras[2],paras[1]);
	if(n==m){alert(wdts);
	}
	else
	{
n=parseInt(n)+1;
	 window.location.href=""+paras[0]+"-"+z+"-"+n+".htm";
		}
		}