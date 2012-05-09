/**********************************************************************************************************************
 *   이 소스는 ㈜비씨카드 소유입니다.
 *   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 *   클래스명  	: GolfLoungSMSAgent
 *   내용      	: 골프라운지 SMS agent
 *   적용범위  	: 골프라운지 VIP 부킹 SMS 대상 찾아서 문자 보내기
 *   작성일자  	: 2009.12.28
 **********************************************************************************************************************/
package com.bccard.golf.action.admin.booking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.bccard.golf.common.BcException;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.proc.admin.booking.GolfLoungSMSAgentDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
 
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.Agent;
import com.bccard.waf.core.WaContext;
 
/*******************************************************************************
 *
 *  
 * @version 1.0
 * @author
 ******************************************************************************/
public class GolfLoungSMSAgent extends AbstractAction implements Agent {
	public static final String TITLE = "골프라운지 VIP SMS 작업"; 

	/*************************************************************************** 
	 **************************************************************************/
	public ActionResponse execute(WaContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, BaseException {

		info("[골프라운지 SMS★★★ URL Start ★★★]");
		runAgent(context);
		ActionResponse respon = new ActionResponse();
		respon.setType(ActionResponse.TYPE_OUT);
		info("[골프라운지 SMS ★★★ URL End ★★★]");

		return respon;
	}
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bccard.waf.core.Agent#execute(com.bccard.waf.core.WaContext)
	 */
	public void execute(WaContext context) throws BaseException {
		//특정 서버에서만 돌도록 설정..
		String yn = "";
		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			yn = AppConfig.getAppProperty("GolfLoungSMSAgent." + hostAddress);
			//debug("GolfLoungSMSAgent IP : " + hostAddress);
		} catch(Throwable t) {
			yn = "N";
		}
		if ( "Y".equals(yn) ) {
			info("[골프라운지 SMS★★★ Agent Start ★★★]");
			runAgent(context);
			info("[골프라운지 SMS ★★★ Agent End ★★★]");
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.bccard.waf.core.Agent#execute(com.bccard.waf.core.WaContext)
	 */
	public void runAgent(WaContext context) throws BaseException {
		try {
			
			GolfLoungSMSAgentDaoProc proc = (GolfLoungSMSAgentDaoProc)context.getProc("GolfLoungSMSAgentDaoProc");
			int fineResult = proc.execute(context);
			debug("GolfLoungSMSAgent 수신 : " + fineResult + "건");

			
		} catch (Throwable t) {
			MsgEtt ett = null;
			if (t instanceof MsgHandler) {
				ett = ((MsgHandler) t).getMsgEtt();
				ett.setTitle(TITLE);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, t.getMessage());
			}
			//throw BaseException();
		}
	}
}