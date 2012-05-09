/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : AdmGolfTopPointUpdInsActn
*   �ۼ���    : ����
*   ����      : ������ > ��ŷ > �г�Ƽ����  > ����Ʈ���� ���� �Է� �׼� 
*   �������  : Golf
*   �ۼ�����  : 2010-12-29
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import com.bccard.waf.core.RequestParser;
import javax.servlet.http.HttpSession;
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

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;


/** ****************************************************************************
 * ����Ʈ���� �����Է� ���� �׼�.
 * @author  ����
 * @version 2010.12.29
 **************************************************************************** */
public class AdmGolfTopPointUpdInsActn extends AbstractAction {

	public static final String TITLE	= "����Ʈ���� ����";
		public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {

				TaoConnection con = null;
				debug("***********************************************************************************");
				debug(" Action  AdmGolfTopPointUpdInsActn.java ���� �׼� execute");
				debug("***********************************************************************************");

		try {
			// 01.������������ 
				RequestParser parser = context.getRequestParser("default",request,response);
	
				String roundDate        = parser.getParameter("roundDate","");          //��������
				String seqNo            = parser.getParameter("seqNo","");              //�Ϸù�ȣ
				String memId            = parser.getParameter("memId","");              //ȸ����ȣ
				String pointClss        = parser.getParameter("pointClss","50");        //����Ʈ����
				String pointDetlCd      = parser.getParameter("pointDetlCd","");        //����Ʈ�����ڵ�
				String pointMemo        = parser.getParameter("pointMemo");             //����Ʈ����
				String regIp            = request.getRemoteAddr();                      //��ŷó��IP
	                      
				// 02.���� ������������ 

				HttpSession session = request.getSession(true);
	
				String regAdminNo = null;
				String regAdminNm = null;
				GolfAdminEtt sessionEtt = (GolfAdminEtt)session.getAttribute("SESSION_ADMIN"); 
				if(sessionEtt != null) {
					regAdminNo    = (String)sessionEtt.getMemNm();  //��ŷó�� �����ڸ�
					regAdminNo    = (String)sessionEtt.getMemNo();//��ŷó�� �����ڹ�ȣ
				}
				con = context.getTaoConnection("dbtao", null);
	
				// DataSet ���� ��� 
				TaoDataSet dataSet = new DbTaoDataSet(TITLE);			
	
				dataSet.setString("roundDate", roundDate);
				dataSet.setString("seqNo", seqNo);
				dataSet.setString("memId", memId);
				dataSet.setString("pointClss", pointClss);
				dataSet.setString("pointDetlCd", pointDetlCd);
				dataSet.setString("pointMemo", pointMemo);
				dataSet.setString("regIp", regIp);
				dataSet.setString("regAdminNo", String.valueOf(regAdminNo));
				dataSet.setString("regAdminNm", regAdminNm);
	
	
				AdmGolfTopPointUpdInsProc proc = (AdmGolfTopPointUpdInsProc)context.getProc("AdmGolfTopPointUpdInsProc");	
				DbTaoResult greenLstIns = (DbTaoResult) proc.execute(context, dataSet);  

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

