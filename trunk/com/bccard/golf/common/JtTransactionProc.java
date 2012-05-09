/***************************************************************************************************
*   클래스명  : JtTransactionProc
*   작성자    : 
*   내용      : 
*   적용범위  : bccard.com
*   작성일자  : 2005.7.21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.Properties;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.golf.common.login.BcUserEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.BcJoltLogFormatter;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.tao.jolt.JoltUtil;

/** ****************************************************************************
 * Proc Class for Transactional BCCARD TUXEDO SERVICE.
 *******************************************************************************
 * ACCEPTED PROPERTIES ARE :
 * POOL_NAME   : "bccard_XA", "bccard_non", "cashpool"
 *               If not set, "bccard_XA" is implicit choice in this Porcess;
 *               "bccard_non" in JtProcess.
 *               If you wish to use "cashpool", should be set this explicitly
 *               in Actn coding.
 * LOGIN       : {any} - distributor for logging. Not needed if you are
 *                       not concerned to login side modules.
 * RETURN_CODE : for logging. informs to logger which element is regarded
 *               as return code.
 *               If not set, implicitly "fml_ret1" by default.
 **************************************************************************** */
public abstract class JtTransactionProc extends AbstractObject {
	protected final static String SYS_ERROR = "JT99999";
	protected final static String ERROR_LOG = "ERR|";
	protected final static String UNKNOWN_ERROR = "ERR|UNKNOWN";

	
	/*
	public JoltOutput execute(WaContext context
                            , HttpServletRequest request
                            , JoltInput input) throws TaoException {
		return this.execute(context, request, input, "card_XA");
	}


	public JoltOutput execute(WaContext context
                            , HttpServletRequest request
                            , JoltInput input
                            , String poolName) throws TaoException {
		Properties properties = new Properties();
		properties.setProperty("POOL_NAME", poolName );
		return this.execute(context, request, input, properties);
	}
	*/

	/** ****************************************************************************
	 * Transactional Jolt
	 **************************************************************************** */
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
		
		String strTransaction = "|NULL";
	    try {
			
			//properties.setProperty(JoltUtil.getProperties("card_XA"));
			this.appendLogInfo(request, input);
			
			debug("JtTransactionProc >> input >> " + input.toString());

			/* ******************************************************************
			 * Get connection, fetch encapsulated  ServletResult
			 *******************************************************************/
			debug("==========JoltOutput=======111111111");
			beginPoint = new Date();
			debug("==========JoltOutput=======22222222");
			con = context.getTaoConnection("jolt", properties);
			debug("==========JoltOutput=======33333333333");
			debug("==========JoltOutput=======con : " + con);
			//con.begin(input.getTransactionTimeOut());
			con.begin(60);
			debug("==========JoltOutput=======444444444");
			output = (JoltOutput)con.execute(input);
			debug("==========JoltOutput=======555555555");
			
			debug("JtTransactionProc >> output >> "+output);
			debug("==========JoltOutput=======66666666666");
			
			endPoint = new Date();
			debug("==========JoltOutput=======888888888");
			if (output == null) {
				debug("==========JoltOutput=======9999999999");
				try { con.rollback(); strTransaction= "|ROLLEDBACK"; debug(strTransaction); } catch (TaoException e) {}
			} else {
				debug("==========JoltOutput=======000000000000");
				boolean commit = this.decision(input, output);
				if (commit) { con.commit();  strTransaction= "|COMMITTED"; debug(strTransaction); }
				else        { con.rollback(); strTransaction= "|ROLLEDBACK"; debug(strTransaction); }
			}
			
			//output.setTransactionResult(commit);

			beginLog = BcJoltLogFormatter.getBeginningJLog(request, input, properties, beginPoint);
			endLog = BcJoltLogFormatter.getEndingJLog(request, input, output, properties, beginPoint, endPoint) +strTransaction ;

			BcLog.joltLog("beginLog :::::" + beginLog);
			BcLog.joltLog("endLog   :::::" + endLog);

			return output;

/*
        } catch ( TaoException t ) {
			if (con != null) try { con.rollback(); } catch (Exception je) {}
			throw new TaoException(t);
			*/
        } catch ( TaoException t ) {
			if (con != null) try { con.rollback(); strTransaction= "|ROLLEDBACK";} catch (Exception je) {}
			beginLog = BcJoltLogFormatter.getBeginningJLog(request, input, properties, beginPoint);
			BcLog.joltLog(beginLog);
			BcLog.joltLog(ERROR_LOG + t.getKey()+strTransaction);
            BcLog.joltLog(t.getMessage(),t);
            throw t;
        } catch ( Throwable t ) {
			if (con != null) try { con.rollback(); strTransaction= "|ROLLEDBACK";} catch (Exception je) {}
			beginLog = BcJoltLogFormatter.getBeginningJLog(request, input, properties, beginPoint);
			BcLog.joltLog(beginLog);
			BcLog.joltLog(UNKNOWN_ERROR+strTransaction);
            BcLog.joltLog(t.getMessage(),t);
            throw new TaoException(t);
        } finally {
            try { con.close(); } catch ( Throwable ignored) {}
		}
    }

    /** 
     * wickedPropertyCheck
    */
	private void wickedPropertyCheck(Properties prop) {
		try {
			
			prop.setProperty("card_XA", "tr");
			prop.setProperty("card_non", "nt");
			prop.setProperty("card_cash", "nt");
			prop.setProperty("DEFAULT_FACTORY", "joltProto");
			prop.setProperty("LOGGER", "off");
			prop.setProperty("POOL_MANAGER_LOAD_TYPE", "poolmgr");

			if (!prop.containsKey("POOL_NAME")) { prop.setProperty("POOL_NAME", "card_XA"); }
		} catch (Exception ignore) {}
	}


	/* FOR BC-FORMAT LOGGING ******************************************/
    /** 
     * appendLogInfo
    */
	private void appendLogInfo(HttpServletRequest request, JoltInput input) {
	    
		try {
			String userId = "";
			/*
			HttpSession sess = request.getSession( false );
			BcUserEtt user = null;
			//BcAdminEtt admin = null;

			if (sess != null) {
				user = (BcUserEtt)sess.getAttribute("LOGIN_USER");
				if (null == user) {
					//admin = (BcAdminEtt)sess.getAttribute("LOGIN_ADMIN");
					//if (null != admin) { userId = "bc_admin"; }
				} else {
					userId = user.getAccount();
				}
			}
			*/

			// 20090728 - Golf 수정 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			 if(userEtt != null) {
				userId = userEtt.getAccount();
			}
			
			// 2006.11.23. HOST FRAMEWORK 변환에 의한 전문 수정 적용 시작
			
			if("".equals(userId)) {
				userId = "UNKNOWN_USER";
			}
			
			//debug("=`=`=`=`=`=`=`=` appendLogInfo ==== : " + userId);

			//input.setUserId( userId );
			//input.setUserIp( request.getRemoteAddr() );

			// 2007.02.15. HOST FRAMEWORK 변환에 의한 전문 수정 적용 시작
			//input.setString("fml_sec50", userId );
			//input.setString("fml_sec51",  request.getRemoteAddr() );

			if ( !input.getFieldSet().contains("fml_channel") ) 
				input.setString("fml_channel", "WEB");			

			if ( !input.getFieldSet().contains("fml_sec50") ) 
				input.setString("fml_sec50", userId );

			if ( !input.getFieldSet().contains("fml_sec51") ) 
				input.setString("fml_sec51",  request.getRemoteAddr() );

			// 2007.02.15. HOST FRAMEWORK 변환에 의한 전문 수정 적용 끝

        } catch ( Throwable t ) {
			debug(t.getMessage());
        }
    }


	/** ****************************************************************************
	 * Transaction result parsing method to determine if COMMIT or ROLLBACK
	 **************************************************************************** */
	public abstract boolean decision(JoltInput input, JoltOutput output) throws TaoException;

	/** ****************************************************************************
	 * Transaction result information method
	 **************************************************************************** */
	public abstract String getErrorCode(JoltInput input, JoltOutput output);
}
