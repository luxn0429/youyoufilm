package com.baobao.utils.dbtool;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

public class TransactionManager {

	private TransactionManager() {

    }
	
    /*private static UserTransaction ut = null;
    
    static {
        try{
        	Context initCtx = new InitialContext();
			ut = (UserTransaction)initCtx.lookup("java:comp/UserTransaction");
            if (ut == null) {
                LogUtil.logger.error("can't get user transaction: the UserTrasaction not find");
            }
        }
        catch (NamingException e) {
            e.printStackTrace(System.out);
            LogUtil.logger.error("can't get user transaction:" + e);
        }
    }
    
    public synchronized static UserTransaction getUserTransaction() {
        if (ut == null) {
        	LogUtil.logger.error( "can't get user transaction: the UserTrasaction not find");
            return null;
        }
        return ut;
    }*/
    
    public static UserTransaction getUserTransaction() {
    	UserTransaction ut = null;
    	try{
        	Context initCtx = new InitialContext();
        	ut = (UserTransaction)initCtx.lookup("java:comp/UserTransaction");
            if (ut == null) {
                Logger.getLogger(TransactionManager.class).error("can't get user transaction: the UserTrasaction not find");
            }
        }
        catch (NamingException e) {
            e.printStackTrace(System.out);
            Logger.getLogger(TransactionManager.class).error("can't get user transaction:" + e);
        }
        return ut;
    }

}
