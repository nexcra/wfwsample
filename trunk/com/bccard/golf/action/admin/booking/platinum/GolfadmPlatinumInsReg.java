/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPlatinumInsReg
*   �ۼ���    : ������
*   ����      : �÷�Ƽ�� ī�� ��ŷ ��� ó��
*   �������  : golf
*   �ۼ�����  : 2010-09-13
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.platinum;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.platinum.GolfadmPlatinumListDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0
******************************************************************************/
public class GolfadmPlatinumInsReg extends GolfActn{
	
	public static final String TITLE = "������ �÷�Ƽ�� ī�� ��ŷ ��� ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ�� 
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id	= (String)userEtt.getMemId(); 							
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);			
			
			String chng_mgr_id			= parser.getParameter("CHNG_MGR_ID", "").trim();		// �ݼ��� ����ھ��̵�
			String hope_rgn_code			= parser.getParameter("HOPE_RGN_CODE", "").trim();		// ȸ���籸��
			String titl 				= parser.getParameter("TITL", "").trim();					// ����
			String cdhd_id 				= parser.getParameter("CDHD_ID", "").trim();				// �ֹι�ȣ
			String fit_hope_club_clss 				= parser.getParameter("FIT_HOPE_CLUB_CLSS", "").trim();				// ���
			String note_mttr_expl 				= parser.getParameter("NOTE_MTTR_EXPL", "").trim();	// ī���ȣ
			String cncl_aton 				= parser.getParameter("CNCL_ATON", "").trim();		// �������
			String round_hope_date 			= parser.getParameter("ROUND_HOPE_DATE", "").trim();			//��ŷ����
			String tot_res_num 			= parser.getParameter("TOT_PERS_NUM", "").trim();			// �ݾ�D
			String email 			= parser.getParameter("EMAIL", "").trim();			// �������
			String ctnt 			= parser.getParameter("CTNT", "").trim();			//���
			String rsvt_yn 			= parser.getParameter("RSVT_YN", "").trim();			// ���࿩��
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("CHNG_MGR_ID", chng_mgr_id);				//
			dataSet.setString("HOPE_RGN_CODE", hope_rgn_code);				// 
			dataSet.setString("TITL", titl );				//
			dataSet.setString("CDHD_ID", cdhd_id );				//
			dataSet.setString("FIT_HOPE_CLUB_CLSS", fit_hope_club_clss );				//
			
			dataSet.setString("NOTE_MTTR_EXPL", note_mttr_expl );				//
			dataSet.setString("CNCL_ATON", cncl_aton );				//
			dataSet.setString("ROUND_HOPE_DATE",round_hope_date );				//
			dataSet.setString("TOT_PERS_NUM", tot_res_num);				//
			dataSet.setString("EMAIL", email);				//
			dataSet.setString("CTNT",ctnt );				//
			dataSet.setString("RSVT_YN", rsvt_yn);				//
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");
			int addResult = proc.insertBooking(context, dataSet);			
			
	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	       	returnUrlTrue = "admPlatinumList.do";
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "����� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
}
