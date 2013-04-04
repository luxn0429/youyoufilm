/**
 *@Title: SolrWeiboInfoExample.java
 *@Package:	com.kmsocial.utils.solr.example
 *@Description:	TODO
 *@author luxiangning luxn0429@gmail.com
 *@Date:2012-7-10 下午5:59:29
 *@version	1.0
 **/
package com.film.solr.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *  Class Name: SolrWeiboInfoExample.java
 *  Function:
 *  Modifications:   
 *  
 */

public class SolrWeiboInfoClient extends AbstractSearch {

    private SolrWeiboInfoClient () {

        super (IndexConfiguration.getInstance ().getSolrUrl () + "db/");
    }

    private static SolrWeiboInfoClient instance = new SolrWeiboInfoClient ();

    public static SolrWeiboInfoClient getInstance () {

        return instance;
    }

    /**
     * 
     *  Function:简单查询
     *
     *  @author luxiangning  DateTime 2012-7-11 下午2:10:36
     *  @param query
     *  @param start
     *  @param rows
     */
    public void doSearch (String query, int start, int rows) {

        SolrDocumentList docList = search (query, start, rows);
        System.out.println (docList.size ());
        for (SolrDocument doc : docList) {
            System.out.println (doc.getFieldValue ("mid"));
            System.out.println (doc.getFieldValue ("postTime"));
            System.out.println (doc.getFieldValue ("conditionIds"));
        }
    }

    public List <Long> doSearch (IndexSearchBean bean) {

        CommonQueryTranslater cqt = getSearchString (bean);
        Logger.getLogger (this.getClass ()).info ("sql " + cqt.toString ());
        SolrQuery solrQuery = new SolrQuery (cqt.toString ());
        solrQuery.setStart (bean.getStartRow ());
        solrQuery.setRows (bean.getRowNumber ());

        ////只做降序的排序
        solrQuery.addSortField ("updateTime", ORDER.desc);
        
        SolrDocumentList docList = super.search (solrQuery);
        List <Long> result = new ArrayList <Long> ();
        if (null == docList)
            return result;

        for (SolrDocument doc : docList) {
        	Integer id = (Integer)doc.getFieldValue("id");
        	result.add(Long.valueOf(id.toString()));
        }
        return result;
    }

    /**
     * 
     *  Function:得到搜索的微博数量
     *
     *  @author luxiangning  DateTime 2012-7-11 下午5:33:23
     *  @param bean
     *  @return
     */
    public long getNumfound (IndexSearchBean bean) {

        CommonQueryTranslater cqt = getSearchString (bean);
        return super.getNumfound (cqt.toString ());
    }

    /**
     *  Function:
     *
     *  @author luxiangning  DateTime 2012-7-11 下午5:59:14
     *  @param bean
     *  @return
     */

    private CommonQueryTranslater getSearchString (IndexSearchBean bean) {

        CommonQueryTranslater cqt = new CommonQueryTranslater ();
        
        if (bean.getCountry() >= 0)
            cqt.put ("country", bean.getCountry());
        if (bean.getLanguage() >= 0)
            cqt.put ("language", bean.getLanguage());
        if(bean.getType() >= 0)
        	cqt.put("type", bean.getType());
        if(null != bean.getKeyword()){
        	cqt.putOrMap("name", bean.getKeyword());
        	cqt.putOrMap("performer", bean.getKeyword());
        }
        ////设置时间
        if (bean.getStartPubDate()>0 && bean.getEndPubDate()>0) {
            cqt.put ("pubdate", bean.getStartPubDate(), bean.getEndPubDate());
        }
        return cqt;
    }

    /**
     * 
     *  Function:删除一批索引
     *
     *  @author luxiangning  DateTime 2012-7-31 下午2:58:16
     *  @param mids
     *  @param platform
     */
    public boolean deleteIndex (List <String> mids, String platform) {

        if (mids == null || mids.size () == 0)
            return true;
        try {
            List <String> deleteList = new ArrayList <String> ();
            for (String mid : mids) {
                deleteList.add (mid);
                if (deleteList.size () > 500) {
                    StringBuffer buffer = getDeleteString (deleteList, platform);
                    String delete = buffer.toString ();
                    Logger.getLogger (this.getClass ()).info ("start delete index:" + delete);
                    super.deleteByQuery (delete);
                    deleteList.clear ();
                }
            }
            if (deleteList.size () > 0) {
                Logger.getLogger (this.getClass ()).info ("start delete index:" + platform);
                StringBuffer buffer = getDeleteString (deleteList, platform);
                super.deleteByQuery (buffer.toString ());
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
        return false;

    }

    private StringBuffer getDeleteString (List <String> mids, String platform) {

        StringBuffer buffer = new StringBuffer ("platform:").append (platform);
        buffer.append (" AND ");

        buffer.append ("(mid:");
        boolean first = true;
        for (String item : mids) {
            if (!first)
                buffer.append (" OR mid:");
            first = false;
            buffer.append (item);
        }
        buffer.append (")");
        return buffer;
    }

    /**
     *  Function:
     *
     *  @author luxiangning  DateTime 2012-7-10 下午5:59:29
     *  @param args
     */

    public static void main (String [] args) {

    	SolrWeiboInfoClient weiboInfo = new SolrWeiboInfoClient();
    	IndexSearchBean search = new IndexSearchBean();
    	//search.setType(1);
    	//search.setLanguage(5);
    	search.setKeyword("双");
    	List<Long> list = weiboInfo.doSearch(search);
    	System.out.println(list.size());
    }
}
