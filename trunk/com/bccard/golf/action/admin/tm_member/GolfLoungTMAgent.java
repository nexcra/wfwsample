/**********************************************************************************************************************
 *   이 소스는 ㈜비씨카드 소유입니다.
 *   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 *   클래스명  	: GolfLoungTMAgent
 *   내용      	: 골프라운지 TM agent
 *   적용범위  	: 골프라운지 TM대상 읽어서 포인트 조회, 포인트 차감 + 승인
 *   작성일자  	: 2009.07.03
 **********************************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.bccard.golf.common.BcException;
import com.bccard.golf.common.AppConfig;
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
public class GolfLoungTMAgent extends AbstractAction implements Agent {
	public static final String TITLE = "골프라운지 TM작업"; 

	/***************************************************************************
	 **************************************************************************/
	public ActionResponse execute(WaContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, BaseException {

		info("[골프라운지 TM ★★★ Agent Start ★★★]");
		runAgent(context);
		ActionResponse respon = new ActionResponse();
		respon.setType(ActionResponse.TYPE_OUT);
		info("[골프라운지 TM ★★★ Agent End ★★★]");

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
			yn = AppConfig.getAppProperty("GolfLoungTMAgent." + hostAddress);
		} catch(Throwable t) {
			yn = "N";
		}
		if ( "Y".equals(yn) ) {
			runAgent(context);
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.bccard.waf.core.Agent#execute(com.bccard.waf.core.WaContext)
	 */
	public void runAgent(WaContext context) throws BaseException {
		try {
			//GolfLoungTMProc proc = new GolfLoungTMProc();
			//proc.getContext(context);
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