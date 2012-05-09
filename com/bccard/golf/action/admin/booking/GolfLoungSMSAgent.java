/**********************************************************************************************************************
 *   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 *   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 *   Ŭ������  	: GolfLoungSMSAgent
 *   ����      	: ��������� SMS agent
 *   �������  	: ��������� VIP ��ŷ SMS ��� ã�Ƽ� ���� ������
 *   �ۼ�����  	: 2009.12.28
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
	public static final String TITLE = "��������� VIP SMS �۾�"; 

	/*************************************************************************** 
	 **************************************************************************/
	public ActionResponse execute(WaContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, BaseException {

		info("[��������� SMS�ڡڡ� URL Start �ڡڡ�]");
		runAgent(context);
		ActionResponse respon = new ActionResponse();
		respon.setType(ActionResponse.TYPE_OUT);
		info("[��������� SMS �ڡڡ� URL End �ڡڡ�]");

		return respon;
	}
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bccard.waf.core.Agent#execute(com.bccard.waf.core.WaContext)
	 */
	public void execute(WaContext context) throws BaseException {
		//Ư�� ���������� ������ ����..
		String yn = "";
		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			yn = AppConfig.getAppProperty("GolfLoungSMSAgent." + hostAddress);
			//debug("GolfLoungSMSAgent IP : " + hostAddress);
		} catch(Throwable t) {
			yn = "N";
		}
		if ( "Y".equals(yn) ) {
			info("[��������� SMS�ڡڡ� Agent Start �ڡڡ�]");
			runAgent(context);
			info("[��������� SMS �ڡڡ� Agent End �ڡڡ�]");
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
			debug("GolfLoungSMSAgent ���� : " + fineResult + "��");

			
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