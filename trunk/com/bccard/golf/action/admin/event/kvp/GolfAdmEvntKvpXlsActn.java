/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntKvpListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > Kvp�̺�Ʈ ȸ�� > ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2010-05-31
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.kvp;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.kvp.GolfAdmEvntKvpXlsDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntKvpXlsActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ > Kvp�̺�Ʈ ȸ�� > ���� ����Ʈ";

	/***************************************************************************************
	* ��������� ������ȭ��
	* @param context		WaContext ��ü.  
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.  
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// �˻���		
			String sch_yn					= parser.getParameter("sch_yn", "");
			String sch_date_gubun			= parser.getParameter("sch_date_gubun", "");	
			String st_year 					= parser.getParameter("st_year","");
			String st_month 				= parser.getParameter("st_month","");
			String st_day 					= parser.getParameter("st_day","");
			String ed_year 					= parser.getParameter("ed_year","");
			String ed_month 				= parser.getParameter("ed_month","");
			String ed_day 					= parser.getParameter("ed_day","");
			String sch_date_st				= st_year+st_month+st_day;
			String sch_date_ed				= ed_year+ed_month+ed_day;	
			if(sch_date_gubun.equals("PU_DATE")){
				if(GolfUtil.empty(sch_date_st))	sch_date_st = sch_date_st+"000000";
				if(GolfUtil.empty(sch_date_ed))	sch_date_ed = sch_date_st+"999999";
			}
			String sch_type					= parser.getParameter("sch_type", "");	
			String sch_text					= parser.getParameter("sch_text", "");
			String sch_pgrs_yn				= parser.getParameter("sch_pgrs_yn", "");				// ���࿩��
			String sch_cslt_yn				= parser.getParameter("sch_cslt_yn", "");				// ���Կ���	
			String sch_rsvt_cdhd_grd_seq_no	= parser.getParameter("sch_rsvt_cdhd_grd_seq_no", "");	// ȸ�����
			String sch_golf_lesn_rsvt_no	= parser.getParameter("sch_golf_lesn_rsvt_no", "");		// ����Ƚ��
						
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("sch_yn", sch_yn);
			dataSet.setString("sch_date_gubun", sch_date_gubun);
			dataSet.setString("sch_date_st", sch_date_st);
			dataSet.setString("sch_date_ed", sch_date_ed);
			dataSet.setString("sch_type", sch_type);
			dataSet.setString("sch_text", sch_text);
			dataSet.setString("sch_pgrs_yn", sch_pgrs_yn);
			dataSet.setString("sch_cslt_yn", sch_cslt_yn);
			dataSet.setString("sch_rsvt_cdhd_grd_seq_no", sch_rsvt_cdhd_grd_seq_no);
			dataSet.setString("sch_golf_lesn_rsvt_no", sch_golf_lesn_rsvt_no);
			
			
			// 04.���� ���̺�(Proc) ��ȸ 
			GolfAdmEvntKvpXlsDaoProc proc = (GolfAdmEvntKvpXlsDaoProc)context.getProc("GolfAdmEvntKvpXlsDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			paramMap.put("resultSize", String.valueOf(listResult.size()));
			paramMap.put("st_year",st_year);
			paramMap.put("st_month",st_month);
			paramMap.put("st_day",st_day); 
			paramMap.put("ed_year",ed_year);
			paramMap.put("ed_month",ed_month);
			paramMap.put("ed_day",ed_day);

			request.setAttribute("ListResult", listResult);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
