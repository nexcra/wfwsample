/* *************************************************************************************************
* CLASS NAME  : JtProcess
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
//import com.bccard.waf.tao.jolt.JtInput;
//import com.bccard.waf.tao.jolt.JtOutput;
//import com.bccard.waf.tao.jolt.JtOutputEtt;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.waf.tao.jolt.JoltUtil;
import com.bccard.golf.common.GolfUserEtt;


/*****************************************
* Proc Class for improper BCCARD TUXEDO SERVICE.
* @version 2008.07.28 
* @author csj007
********************************************** */
public class JtProcess extends AbstractObject {
	protected final static String POOL_NAME = "golfnew_non";
	protected final static String ERROR_LOG = "ERR|";
	protected final static String UNKNOWN_ERROR = "ERR|UNKNOWN";
/** ******************************************************************************** 
* 기업 
* @version 2008.07.28 
* @author csj007
* @param context WaContext객체.
* @param request HttpServletRequest.
* @param input JoltInput객체.
* @return  JoltOutput 
********************************************************************************** */ 
	public JoltOutput call(WaContext context
                            , HttpServletRequest request
                            , JoltInput input) throws TaoException {
		return this.call(context, request, input, POOL_NAME );
	}


/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param context WaContext객체.
* @param input JoltInput객체.
* @return  JoltOutput 
********************************************************************************** */ 
	public JoltOutput call(WaContext context, JoltInput input) throws TaoException {
		return this.call(context, null, input, POOL_NAME );
	}

/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param context WaContext객체.
* @param request HttpServletRequest.
* @param input JoltInput객체.
* @param poolName String객체.
* @return  JoltOutput 
********************************************************************************** */ 
	public JoltOutput call(WaContext context
                            , HttpServletRequest request
                            , JoltInput input
                            , String poolName) throws TaoException {
		Properties properties = new Properties();
		properties.setProperty("POOL_NAME", poolName );
		return this.call(context, request, input, properties);
	}
/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param context WaContext객체.
* @param request HttpServletRequest.
* @param input JoltInput객체.
* @param prop Properties객체.
* @return  JoltOutput 
********************************************************************************** */ 
	public JoltOutput call(WaContext context
		                 , HttpServletRequest request
		                 , JoltInput input
		                 , Properties prop) throws TaoException {
		this.wickedPropertyCheck(prop);
		TaoConnection con  = null;
		JoltOutput output = null;
		Date beginPoint = null;
		Date endPoint = null;
		String beginLog = null;
		String endLog = null;

		debug("jtProcess call Start");

		try {
			this.appendLogInfo(request, input);

			beginPoint = new Date();

			Properties properties = JoltUtil.getProperties("golfnew_non");

			con = context.getTaoConnection("jolt", properties);
			 
			if (con == null) {
				debug("JtProcess >> con is null >> ");
			} else {
				debug("JtProcess >> connection >> ");
			}


			output = (JoltOutput)con.execute(input);

			endPoint = new Date();

			beginLog = JoltLogFormatter.getBeginningJLog(request, input, prop, beginPoint);
			endLog = JoltLogFormatter.getEndingJLog(request, input, output, prop, beginPoint, endPoint);

			BcLog.joltLog(beginLog);
			BcLog.joltLog(endLog);
			BcLog.joltLog(input.toString());
			BcLog.joltLog(output.toString());

		} catch ( TaoException t ) {
			beginLog = JoltLogFormatter.getBeginningJLog(request, input, prop, beginPoint);
			BcLog.joltLog(beginLog);
			BcLog.joltLog(ERROR_LOG + t.getKey());
			BcLog.joltLog(t.getMessage(),t);
			throw t;
		} catch ( Throwable t ) {
			beginLog = JoltLogFormatter.getBeginningJLog(request, input, prop, beginPoint);
			BcLog.joltLog(beginLog);
			BcLog.joltLog(UNKNOWN_ERROR);
			BcLog.joltLog(t.getMessage(),t);
			throw new TaoException(t);
		}
		return output;
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
			JoltUtil.getProperties("etax_non"); 가 문제있을때 이곳에 소스를 박아 놓으세요..

			prop.setProperty("etax_XA", "tr");
			prop.setProperty("etax_non", "nt");
			prop.setProperty("DEFAULT_FACTORY", "jolt");
			prop.setProperty("LOGGER", "off");
			prop.setProperty("POOL_MANAGER_LOAD_TYPE", "poolmgr");

			*/
			if (!prop.containsKey("POOL_NAME")) { prop.setProperty("POOL_NAME", POOL_NAME); }
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
			HttpSession sess = request.getSession( false );
			GolfUserEtt user = null;
			String userId = "UNKNOWN_USER";
			/*
			if (sess != null) {
				user = (EtaxUserEtt)sess.getAttribute("SESSION_USER");
				if (null == user) {
					userId="UNKNOWN_USER" ;
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


}
