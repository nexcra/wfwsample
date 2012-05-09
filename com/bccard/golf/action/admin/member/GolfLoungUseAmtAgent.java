/**********************************************************************************************************************
 *   이 소스는 ㈜비씨카드 소유입니다.
 *   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 *   클래스명  	: GolfLoungUseAmtAgent
 *   내용     	: 골프라운지 이용 실적 처리 agent
 *   적용범위  	: golf
 *   업무내용	: 배치 프로그램.WAS에서 Agent 방식으로 작업. 매일 19시 
 *   작성일자  	: 2011.01.07
 **********************************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.proc.admin.member.GolfLoungUseAmtAgentDaoProc;
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
public class GolfLoungUseAmtAgent extends AbstractAction implements Agent {
	public static final String TITLE = "골프라운지 이용 실적  처리 작업";  

	/*************************************************************************** 
	 **************************************************************************/
	public ActionResponse execute(WaContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, BaseException {
		
		info("[골프라운지 이용 실적 처리★★★ URL Start ★★★]");
		runAgent(context);
		ActionResponse respon = new ActionResponse();
		respon.setType(ActionResponse.TYPE_OUT);
		info("[골프라운지 이용 실적 처리 ★★★ URL End ★★★]");  

		return respon; 
	}
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bccard.waf.core.Agent#execute(com.bccard.waf.core.WaContext)
	 */
	public void execute(WaContext context) throws BaseException {
		//특정 서버(31)에서만 돌도록 설정..
		String was31Yn = "";
		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			was31Yn = AppConfig.getAppProperty("GolfLoungSMSAgent." + hostAddress);
			debug("hostAddress IP : " + hostAddress);
			debug("was31Yn : " + was31Yn);

		} catch(Throwable t) {
			was31Yn = "N";
		}
		if ( "Y".equals(was31Yn) ) {
			info("[골프라운지 이용 실적 처리★★★ Agent Start ★★★]");
			runAgent(context);
			info("[골프라운지 이용 실적 처리★★★ Agent End ★★★]");
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.bccard.waf.core.Agent#execute(com.bccard.waf.core.WaContext)
	 */
	public void runAgent(WaContext context) throws BaseException {
		try {
			
			GolfLoungUseAmtAgentDaoProc proc = (GolfLoungUseAmtAgentDaoProc)context.getProc("GolfLoungUseAmtAgentDaoProc");
			boolean fineResult = proc.execute(context);
			debug("GolfLoungUseAmtAgent 처리 : " + fineResult );
 
			
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