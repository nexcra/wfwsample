/* *************************************************************************************************
* CLASS NAME  : JtTransactionProc
* CREATED BY  : csj007
* DESCRIPTION : Page Navigation Buffer Class for JOLT System
* APP. SCOPE  : BEA JOLT Packages for BC ${BC_SITE} under WATRIX FrameWork
* CREATED IN  : 2008-07-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.jolt;

import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BcLog;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.waf.tao.jolt.JoltUtil;
import com.bccard.golf.common.GolfUserEtt;


/******************************************************************************
 * Proc Class for Transactional BCCARD TUXEDO SERVICE.
 *******************************************************************************
 * ACCEPTED PROPERTIES ARE :
 * POOL_NAME   : "golf_XA", "golf_non", "cashpool"
 *               If not set, "golf_XA" is implicit choice in this Porcess;
 *               "golf_non" in JtProcess.
 *               If you wish to use "cashpool", should be set this explicitly
 *               in Actn coding.
 * LOGIN       : {any} - distributor for logging. Not needed if you are
 *                       not concerned to login side modules.
 * RETURN_CODE : for logging. informs to logger which element is regarded
 *               as return code.
 *               If not set, implicitly "fml_ret1" by default.
 **************************************************************************** */

/******************************************************************************
* Golf : JtTransactionProc
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/


public abstract class JtTransactionProc extends AbstractObject {

	protected final static String SYS_ERROR = "JT99999";
	protected final static String ERROR_LOG = "ERR|";
	protected final static String UNKNOWN_ERROR = "ERR|UNKNOWN";

/** ******************************************************************************** 
* JoltOutput
* @version 2008.07.28 
* @author csj007
* @param context WaContext객체.
* @param request HttpServletRequest.
* @param input JoltInput.
* @return  JoltOutput 
********************************************************************************** */ 
	public JoltOutput execute(WaContext context
                            , HttpServletRequest request
                            , JoltInput input) throws TaoException {
		return this.execute(context, request, input, "golfnew_XA");
	}


/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param context WaContext객체.
* @param input JoltInput객체.
* @return  JoltOutput 
********************************************************************************** */ 
	public JoltOutput execute(WaContext context, JoltInput input) throws TaoException {
		return this.execute(context, null, input, "golfnew_XA");
	}

/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param context WaContext객체.
* @param request HttpServletRequest객체.
* @param input JoltInput객체.
* @param poolName String객체.
* @return  JoltOutput 
********************************************************************************** */ 
	public JoltOutput execute(WaContext context
                            , HttpServletRequest request
                            , JoltInput input
                            , String poolName) throws TaoException {
		Properties properties = new Properties();
		properties.setProperty("POOL_NAME", poolName );
		return this.execute(context, request, input, properties);
	}
/** ******************************************************************************** 
* Transactional Jolt
* @version 2008.07.28 
* @author csj007
* @param context WaContext객체.
* @param input JoltInput객체.
* @return  JoltOutput 
********************************************************************************** */ 
	public JoltOutput execute(WaContext context
							, HttpServletRequest request
							, JoltInput input
							, Properties properties) throws TaoException {
		this.wickedPropertyCheck(properties);
		TaoConnection con  = null;
		JoltOutput output = null;
		Date beginPoint = null;
		Date endPoint = null;
		String beginLog = null;
		String endLog = null;

	    try {
debug("%%%%%%%% "+input.getString("SRVCNM")+" = ["+properties+"] %%%%%%%%");
			this.appendLogInfo(request, input);

			beginPoint = new Date();

			Properties prop = JoltUtil.getProperties("golfnew_XA");
debug("%%%%%%%% jolt connect start %%%%%%%%");
			con = context.getTaoConnection("jolt", prop);
			con.begin(60);
debug("%%%%%%%% jolt execute start %%%%%%%%");
			output = (JoltOutput)con.execute(input);
debug("%%%%%%%% jolt execute end %%%%%%%%");
			endPoint = new Date();

			boolean commit = this.decision(input, output);
			if (commit) { con.commit();   info("COMMITTED"); }
			else        { con.rollback(); info("ROLLEDBACK"); }
			beginLog = JoltLogFormatter.getBeginningJLog(request, input, properties, beginPoint);
			endLog = JoltLogFormatter.getEndingJLog(request, input, output, properties, beginPoint, endPoint);

			BcLog.joltLog(beginLog);
			BcLog.joltLog(endLog);
			BcLog.joltLog(output.toString());
debug("%%%%%%%% jolt connect end %%%%%%%%");
			
			return output;
		} catch ( TaoException t ) {
			if (con != null) try { con.rollback(); } catch (Exception je) {}
			BcLog.joltLog(t.getMessage(),t);
			throw t;
		} catch ( Throwable t ) {
			if (con != null) try { con.rollback(); } catch (Exception je) {}
			BcLog.joltLog(t.getMessage(),t);
			throw new TaoException(t);
		} finally {
			try { if(con != null) con.close(); } catch ( Throwable ignored) {}
		}
	}

/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param prop Properties객체.
* @return  void 
********************************************************************************** */ 
	private void wickedPropertyCheck(Properties prop) {
		try {
			/*
			JoltUtil.getProperties("golf_XA"); 가 문제있을때 이곳에 소스를 박아 놓으세요..

			prop.setProperty("golf_XA", "tr");
			prop.setProperty("golf_non", "nt");
			prop.setProperty("DEFAULT_FACTORY", "jolt");
			prop.setProperty("LOGGER", "off");
			prop.setProperty("POOL_MANAGER_LOAD_TYPE", "poolmgr");

			*/
			if (!prop.containsKey("POOL_NAME")) { prop.setProperty("POOL_NAME", "golfnew_XA"); }
		} catch (Exception ignore) {}
	}

	/* FOR BC-FORMAT LOGGING ******************************************/
	/**
    * appendLogInfo method
    * @param request HttpServletRequest
    * @param input JoltInput 
    * @return N/A
    */	
	private void appendLogInfo(HttpServletRequest request, JoltInput input) {
	    try {
			HttpSession sess = request.getSession(false);
			GolfUserEtt user = null;
			String userId = "UNKNOWN_USER";
			/*
			if (sess != null) {
				user = (EtaxUserEtt)sess.getAttribute("SESSION_USER");
				if (null == user) {
					userId="UNKNOWN_USER";
				} else {
					userId = user.getAccount();
				}
			} else {
				userId = "UNKNOWN_USER";
			}
			*/
			
			input.setString("fml_channel", "WEB");
			input.setString("fml_sec50", userId);
			input.setString("fml_sec51", request.getRemoteAddr());
			// 2006.11.23. HOST FRAMEWORK 변환에 의한 전문 수정 적용 끝
			

        } catch ( Throwable t ) {
			debug(t.getMessage());
        }
    }

/** ******************************************************************************** 
* Transaction result parsing method to determine if COMMIT or ROLLBACK
* @version 2008.07.28 
* @author csj007
* @param input JoltInput객체.
* @param output JoltOutput객체.
* @return  boolean 
********************************************************************************** */ 
	public abstract boolean decision(JoltInput input, JoltOutput output) throws TaoException;

/** ******************************************************************************** 
* Transaction result information method
* @version 2008.07.28 
* @author csj007
* @param input JoltInput객체.
* @param output JoltOutput객체.
* @return  String 
********************************************************************************** */ 
	public abstract String getErrorCode(JoltInput input, JoltOutput output);
}
