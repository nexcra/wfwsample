/**********************************************************************************************************************
 *   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 *   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 *   Ŭ������  	: GolfLoungTMAgent
 *   ����      	: ��������� TM agent
 *   �������  	: ��������� TM��� �о ����Ʈ ��ȸ, ����Ʈ ���� + ����
 *   �ۼ�����  	: 2009.07.03
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
	public static final String TITLE = "��������� TM�۾�"; 

	/***************************************************************************
	 **************************************************************************/
	public ActionResponse execute(WaContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, BaseException {

		info("[��������� TM �ڡڡ� Agent Start �ڡڡ�]");
		runAgent(context);
		ActionResponse respon = new ActionResponse();
		respon.setType(ActionResponse.TYPE_OUT);
		info("[��������� TM �ڡڡ� Agent End �ڡڡ�]");

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