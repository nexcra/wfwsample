/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2004.10.29 [������]
* ���� : ���Ʈ���� ��� �Է� ��� �׼�
* ���� : 
* ���� : �ش������� ���Ʈ���� ��� �Է��Ѵ�.
******************************************************************************/
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
import com.bccard.waf.common.DateUtil;
 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/** ****************************************************************************
 * ���Ʈ���� ��� �Է� ��� ���� �׼�.
 * @author ������
 * @version 2004.10.29
 **************************************************************************** */
public class GolfadmTopPenaltyNewInsActn extends AbstractAction {

		public static final String TITLE	= "���Ʈ���� ���";

	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
			TaoConnection con = null;

		try {
				debug("=================================================================");
				debug("=================================================================");
				debug(" action GolfadmTopPenaltyNewInsActn.java");
				debug("=================================================================");
				debug("=================================================================");
				
				RequestParser parser = context.getRequestParser("default",request,response);
	
				String memId		  	= parser.getParameter("memId","");	        //ȸ����ȣ
				String pointClss		= parser.getParameter("pointClss","60");		//�г�Ƽ����
				String pointDetlCd	= parser.getParameter("pointDetlCd","");		//�г�Ƽ����
				String pointMemo		= parser.getParameter("pointMemo","");			//���೻��
				long affiGreenSeqNo = parser.getLongParameter("affiGreenSeqNo",0L);
				String roundDate		= parser.getParameter("roundDate","");			//��������
				String breachCont		= parser.getParameter("breachCont","");			//���볻��
				String penaltyApplyClss	= parser.getParameter("penaltyApplyClss","");	//���Ƽ���뱸��
				String setFrom			= parser.getParameter("setFrom","");			//�����������
				String setTo				= parser.getParameter("setTo","");		        //������������
				String penaltyResnCd	= parser.getParameter("penaltyResnCd","");		//���Ƽ�����ڵ�
				String regIp				= request.getRemoteAddr();						//��ŷó��IP
				 String bbs					= "0035";

				HttpSession session = request.getSession(true);
				String regAdminNo = null;
				String regAdminNm = null;
				GolfAdminEtt sessionEtt = (GolfAdminEtt)session.getAttribute("SESSION_ADMIN"); 
				if(sessionEtt != null) {
					regAdminNm 		= (String)sessionEtt.getMemNm();  //��ŷó�� �����ڸ�
					regAdminNo    = (String)sessionEtt.getMemNo();//��ŷó�� �����ڹ�ȣ
			}

			con = context.getTaoConnection("dbtao", null);
			
			//����Ʈ������ ���
			TaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("memId", memId);                      // ȸ����ȣ 
			dataSet.setString("pointClss", pointClss);              // �г�Ƽ���� 
			dataSet.setString("pointDetlCd", pointDetlCd);          // �г�Ƽ����
			dataSet.setString("pointMemo", pointMemo);              // ���೻��
			dataSet.setLong("affiGreenSeqNo", affiGreenSeqNo);	// ������		
			dataSet.setString("roundDate", roundDate);              // ��������
			dataSet.setString("breachCont", breachCont);            // ���볻��
			dataSet.setString("penaltyApplyClss", penaltyApplyClss);// �г�Ƽ���뱸��
			dataSet.setString("setFrom", setFrom);                  // �г�Ƽ �����������
			dataSet.setString("setTo", setTo);                      // �³�Ƽ ������������
			dataSet.setString("penaltyResnCd", penaltyResnCd);      // �г�Ƽ���� 
			dataSet.setString("regIp", regIp);                      // ������ IP 
			dataSet.setString("regAdminNo", String.valueOf(regAdminNo)); // ������ ��� 
			dataSet.setString("regAdminNm", regAdminNm);                 // ������ ���� 


			GolfadmTopPenaltyNewInsProc proc = (GolfadmTopPenaltyNewInsProc)context.getProc("GolfadmTopPenaltyNewInsProc");		 // ������ �߰�
			DbTaoResult greenLstIns = (DbTaoResult) proc.execute(context, dataSet);  // ������ �߰�

			// 05. ������ ����Ʈ (Sel_Proc) ��ȸramMap.put("greenNm",greenNm);
			GolfadmTopCodeSelDaoProc coodSelProc = (GolfadmTopCodeSelDaoProc)context.getProc("GolfadmTopCodeSelDaoProc");
			DbTaoResult codeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, bbs); //�Խ��� ����
			request.setAttribute("codeSelResult", codeSel);


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

