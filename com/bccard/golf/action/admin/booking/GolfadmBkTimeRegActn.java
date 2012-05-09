/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admBkTimeRegActn
*   �ۼ���    : �̵������ ������
*   ����      : ������ ��ŷ ƼŸ�� ���
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0   
******************************************************************************/
public class GolfadmBkTimeRegActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ ƼŸ�� ��� ó��"; 

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
		String admin_no = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no	= (String)userEtt.getMemNo(); 							
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE); 

			String sort 				= parser.getParameter("SORT", "").trim();			// ��ϱ����ڵ�
			String gr_seq_no 			= parser.getParameter("GR_SEQ_NO", "").trim();		// ��ŷ�������Ϸù�ȣ
			String course 				= parser.getParameter("COURSE", "").trim();			// �ڽ�
			String bkps_date 			= parser.getParameter("BKPS_DATE", "").trim();		// ��¥
			String bkps_time 			= parser.getParameter("BKPS_TIME", "").trim();		// ƼŸ��
			String free_memo 			= parser.getParameter("FREE_MEMO", "").trim();		// ������ ����
			String sky_code 			= parser.getParameter("SKY_CODE", "").trim();		// Ȧ����
			String free_yn 				= parser.getParameter("FREE_YN", "").trim();		// ��3��ŷ������
			String par_free 			= parser.getParameter("PAR_FREE", "").trim();		// ������ ����
			String evnt_yn	 			= parser.getParameter("EVNT_YN", "N").trim();		// 
			String close_yn	 			= parser.getParameter("CLOSE_YN", "N").trim();		// �����Ͽ���
			String holy_yn	 			= parser.getParameter("HOLY_YN", "N").trim();		// �����Ͽ���
			
			if("".equals(evnt_yn) || evnt_yn == null) evnt_yn="N";
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			debug("## sky_code : "+sky_code);
			
			dataSet.setString("SORT", sort);
			dataSet.setString("GR_SEQ_NO", gr_seq_no);
			dataSet.setString("COURSE", course);
			dataSet.setString("BKPS_DATE", bkps_date);
			dataSet.setString("BKPS_TIME", bkps_time);
			dataSet.setString("FREE_MEMO", free_memo);
			dataSet.setString("SKY_CODE", sky_code);
			dataSet.setString("FREE_YN", free_yn);
			dataSet.setString("PAR_FREE", par_free);
			dataSet.setString("evnt_yn", evnt_yn);
			dataSet.setString("CLOSE_YN", close_yn);
			dataSet.setString("HOLY_YN", holy_yn);
						
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmBkTimeRegDaoProc proc = (GolfadmBkTimeRegDaoProc)context.getProc("admBkTimeRegDaoProc");
			int addResult = proc.execute(context, request, dataSet);
			
			// 05.��� ������ ����
			String returnTrueUrl = "";
			String returnFalseUrl = "";
			
			if (sort.equals("0001")){
				returnTrueUrl = "admPreTimeList.do";
				returnFalseUrl = "admPreTimeRegForm.do";
			} else if (sort.equals("0002")){
				returnTrueUrl = "admParTimeList.do";
				returnFalseUrl = "admParTimeRegForm.do";
			} else if (sort.equals("0003")){
				returnTrueUrl = "admSkyTimeList.do";
				returnFalseUrl = "admSkyTimeRegForm.do";
			} else if (sort.equals("1000")){
				returnTrueUrl = "admPreTimeList.do";
				returnFalseUrl = "admPreTimeRegForm.do";
			} else {
				returnTrueUrl = "admSkyTimeList.do";
				returnFalseUrl = "admSkyTimeRegForm.do";
			}
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnTrueUrl);
				request.setAttribute("resultMsg", "����� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnFalseUrl);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 06. Return �� ����			
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
