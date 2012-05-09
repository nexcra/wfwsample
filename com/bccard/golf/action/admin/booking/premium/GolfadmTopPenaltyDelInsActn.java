/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopPenaltyListActn
*   �ۼ���    : ����
*   ����      : ������ > ��ŷ > �г�Ƽ����  > �г�Ƽ���� ����Ʈ ����
*   �������  : Golf
*   �ۼ�����  : 2010-11-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import com.bccard.waf.core.RequestParser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Map;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.DateUtil;
 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;

import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/** ****************************************************************************
 * ���Ʈ���� �����Է� ���� �׼�.
 * @author  ����
 * @version 2010.12.07
 **************************************************************************** */
public class GolfadmTopPenaltyDelInsActn extends AbstractAction {

	public static final String TITLE	= "���Ʈ���� ����";
		public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
			TaoConnection con = null;

			try {
					RequestParser parser = context.getRequestParser("default",request,response);
		
					String seqNo		= parser.getParameter("seqNo","");			//�Ϸù�ȣ
					String memId		= parser.getParameter("memId","");			//ȸ����ȣ
		
					con = context.getTaoConnection("dbtao", null);
		
					//����Ʈ������ ����
					TaoDataSet dataSet = new DbTaoDataSet(TITLE);			
		
					dataSet.setString("seqNo", seqNo);
					dataSet.setString("memId", memId);
					//dataSet.setString("regIp", regIp);
					//dataSet.setString("regAdminNo", String.valueOf(regAdminNo));
					//dataSet.setString("regAdminNm", regAdminNm);
		
					GolfadmTopPenaltyDelInsProc proc = (GolfadmTopPenaltyDelInsProc)context.getProc("GolfadmTopPenaltyDelInsProc");		 // ������ �߰�
					DbTaoResult greenLstIns = (DbTaoResult) proc.execute(context, dataSet);  // ������ �߰�
		
		
			//		TaoResult greenLstIns = con.execute("GolfadmTopPenaltyDelInsProc", dataSet);

		} catch(BaseException be) {
			throw be;
		} catch(Throwable t) {
			MsgEtt ett = null;
			if ( t instanceof MsgHandler ) {
				ett = ((MsgHandler)t).getMsgEtt();
				ett.setTitle(TITLE);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,t.getMessage());
			}
			throw new GolfException(ett,t);
		} finally {
			try { con.close(); } catch(Throwable ignore) {}
		}
		return getActionResponse(context); // response key
	}
}

