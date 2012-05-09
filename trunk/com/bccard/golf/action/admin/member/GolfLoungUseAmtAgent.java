/**********************************************************************************************************************
 *   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 *   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 *   Ŭ������  	: GolfLoungUseAmtAgent
 *   ����     	: ��������� �̿� ���� ó�� agent
 *   �������  	: golf
 *   ��������	: ��ġ ���α׷�.WAS���� Agent ������� �۾�. ���� 19�� 
 *   �ۼ�����  	: 2011.01.07
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
	public static final String TITLE = "��������� �̿� ����  ó�� �۾�";  

	/*************************************************************************** 
	 **************************************************************************/
	public ActionResponse execute(WaContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, BaseException {
		
		info("[��������� �̿� ���� ó���ڡڡ� URL Start �ڡڡ�]");
		runAgent(context);
		ActionResponse respon = new ActionResponse();
		respon.setType(ActionResponse.TYPE_OUT);
		info("[��������� �̿� ���� ó�� �ڡڡ� URL End �ڡڡ�]");  

		return respon; 
	}
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bccard.waf.core.Agent#execute(com.bccard.waf.core.WaContext)
	 */
	public void execute(WaContext context) throws BaseException {
		//Ư�� ����(31)������ ������ ����..
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
			info("[��������� �̿� ���� ó���ڡڡ� Agent Start �ڡڡ�]");
			runAgent(context);
			info("[��������� �̿� ���� ó���ڡڡ� Agent End �ڡڡ�]");
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
			debug("GolfLoungUseAmtAgent ó�� : " + fineResult );
 
			
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