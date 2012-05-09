/*******************************************************************************
*  클래스명		:   BcasDbConnectionFactory 
*  작성자		:	M4   
*  내용			:   기본 DB Connection 을 가져오기 위한 팩토리.
*  적용범위		:   GolfLounG 
*  작성일자		:   2006.12.27
************************** 수정이력 ********************************************
* 일자            수정자         변경사항 
*
*******************************************************************************/
package com.bccard.golf.factory;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import javax.naming.*;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.dao.DbConnectionFactory;
import com.bccard.waf.dao.DbConnection;

/**
* 기본 DB Connection 을 가져오기 위한 팩토리. 
* @version 2006.12.27
* @author e4net
*/ 
public class GolfDbConnectionFactory extends DbConnectionFactory {
	
	/** 데이터소스 */
	protected static DataSource dataSource = null;
	/** synchronized 를 위한 Object */
	protected static Object lock = new Object();
	
	/**
    * getConnection
    * @param prop Properties Object
    * @version 2006.12.27
 	* @author e4net
    * @return Connection
    */ 
	public Connection getConnection(Properties prop) throws Exception {
		Connection con = null;		
		try {
	        
			if ( dataSource == null ) {
					// 웹로직
					Hashtable ht = new Hashtable();
					ht.put(Context.PROVIDER_URL, "t3://localhost:7001");
					ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
					InitialContext ctx = new InitialContext(ht);
					
					DataSource ds = (DataSource)ctx.lookup("bc_loung"); //구매개발기\
				
					// 톰캣
//					Context ctx = new InitialContext();
//					Context envCtx = (Context) ctx.lookup("java:comp/env");
//					DataSource ds = (DataSource) envCtx.lookup("jdbc/golflngdata_web");
					
					synchronized (lock) {
						dataSource = ds;
					}
			}
			// 프로퍼티의 isQueryLog 가 false이면 쿼리 로그를 남기지 않는다.
			String queryLog = "";
			if ( prop != null ) queryLog = prop.getProperty("isQueryLog","");
			if ( queryLog.equals("false") ) {
				con = dataSource.getConnection();
			} else {	 			
				con = new DbConnection( dataSource.getConnection(), this.getClass().getName() );
			}
	        		
			/*
			String forname = "oracle.jdbc.driver.OracleDriver";
	        String url = "jdbc:oracle:thin:@130.1.192.56:1521:bcgolf";
	        String user = "BCDBA";
	        String password = "media4th";			

			Class.forName(forname);    
	        Connection conn = DriverManager.getConnection(url, user, password);
	        con = new DbConnection(conn, this.getClass().getName());
			*/
		} catch(Throwable t) {
			throw errorHandler(t);
		}
		if ( con == null ) throw errorHandler(null);
		return con;
	}

	/**
    * errorHandler
    * @param t Throwable Object
    * @version 2006.12.27
 	* @author e4net
    * @return BaseException
    */
	protected BaseException errorHandler(Throwable t) {
		BaseException exception = null;
		if ( t != null ) {
			exception = new BaseException(t);
		} else {
			exception = new BaseException();
		}
		// 커넥션 연결 실패일 때 표시할 메시지 키 설정.
		exception.setKey("DB_CONNECTION_FAIL");
		return exception;
	}
}

