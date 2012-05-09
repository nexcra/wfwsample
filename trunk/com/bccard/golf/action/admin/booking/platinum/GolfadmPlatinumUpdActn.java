/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPlatinumUpdActn
*   �ۼ���    : ������
*   ����      : ������ > ��ŷ > �÷�Ƽ�� > ����ó��
*   �������  : golf
*   �ۼ�����  : 2010-09-13
************************** �����̷� ****************************************************************
*    ����      �ۼ���   �������
*  20110523   �̰���  �÷�Ƽ����ŷ���� ��������߰�
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.platinum;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.platinum.GolfadmPlatinumListDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.sky.GolfadmSkyRsUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfadmPlatinumUpdActn extends GolfActn{
	
	public static final String TITLE = "������ > ��ŷ > �÷�Ƽ�� > ����ó��"; 

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int lessonDelResult = 0;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

	        
			// Request �� ����
			String golf_svc_rsvt_no = parser.getParameter("GOLF_SVC_RSVT_NO", "");
			String note_mttr_expl = parser.getParameter("NOTE_MTTR_EXPL", "");
			String cncl_aton = parser.getParameter("CNCL_ATON", "");
			String rount_hope_date = parser.getParameter("ROUND_HOPE_DATE", "");
			String hope_rgn_code = parser.getParameter("HOPE_RGN_CODE", "");
			String email = parser.getParameter("EMAIL", "");
			String tot_pers_num = parser.getParameter("TOT_PERS_NUM", "");
			String ctnt = parser.getParameter("CTNT", "");
			String rsvt_yn = parser.getParameter("RSVT_YN", "");
			String chng_mgr_id = parser.getParameter("CHNG_MGR_ID", "");
			String fit_hope_club_clss = parser.getParameter("FIT_HOPE_CLUB_CLSS", "");
			String delcheck = parser.getParameter("delcheck", "N");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GOLF_SVC_RSVT_NO", golf_svc_rsvt_no);
			dataSet.setString("NOTE_MTTR_EXPL", note_mttr_expl);
			dataSet.setString("CNCL_ATON", cncl_aton);
			dataSet.setString("ROUND_HOPE_DATE", rount_hope_date);
			dataSet.setString("HOPE_RGN_CODE", hope_rgn_code);
			dataSet.setString("EMAIL", email);
			dataSet.setString("TOT_PERS_NUM", tot_pers_num);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("RSVT_YN", rsvt_yn);
			dataSet.setString("CHNG_MGR_ID", chng_mgr_id);
			dataSet.setString("FIT_HOPE_CLUB_CLSS", fit_hope_club_clss);
			

			if ( delcheck.equals("Y") ){ //������ư Ŭ���� 
				
				GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");		
				int editResult = proc.executeDelete(context, dataSet);				

				request.setAttribute("returnUrl", "admPlatinumList.do");
				
				if (editResult == 1) {
					request.setAttribute("resultMsg", "�����Ǿ����ϴ�.");  
				}else if (editResult == 2) {					
					request.setAttribute("resultMsg", "�̹� ����� �������� ���� �Ұ��մϴ�.");					
				}else {					
					request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");					
				}
			
			}else { //������ư Ŭ����
				
				// 04.���� ���̺�(Proc) ��ȸ
				GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");		
				int editResult = proc.executeUpdate(context, dataSet);
				
				if (editResult == 1) {
					request.setAttribute("returnUrl", "admPlatinumList.do");
					request.setAttribute("resultMsg", "���� �������� ���� �Ǿ����ϴ�.");  
				}else{
					request.setAttribute("returnUrl", "admPlatinumList.do");
					request.setAttribute("resultMsg", "���� ������ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");
				}

			}
			
	
	        request.setAttribute("paramMap", paramMap);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
