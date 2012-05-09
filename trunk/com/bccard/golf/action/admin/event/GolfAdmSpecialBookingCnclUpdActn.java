/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmSpecialBookingCnclUpdActn
*   �ۼ���    : ������ ���弱
*   ����      : ������ > �̺�Ʈ->VIP��ŷ�̺�Ʈ->���������ŷ 
*   �������  : Golf
*   �ۼ�����  : 2009-09-17
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSpecialBookingDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;

/******************************************************************************
* Golf
* @author	������ ���弱
* @version	1.0
******************************************************************************/
public class GolfAdmSpecialBookingCnclUpdActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾� ��ŷ �̺�Ʈ ��÷�� ����Ʈ";

	/***************************************************************************************
	* ���� ������ȭ��
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
			// 01.��������üũ
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request �� ����
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size		= parser.getLongParameter("record_size", 10);		// ����������¼�		
			String green_nm				= parser.getParameter("green_nm","");           //��û�������
			String golf_cmmn_code		= parser.getParameter("golf_cmmn_code","");     //�����ڵ�
			String grade				= parser.getParameter("grade","");              //����ڵ�� �ѱ۸�
			String sch_yn               = parser.getParameter("sch_yn","");             
			String sch_reg_aton_st		= parser.getParameter("sch_reg_aton_st","");    //��ȸ ��û ������
			String sch_reg_aton_ed		= parser.getParameter("sch_reg_aton_ed","");    //��ȸ ��û ������
			String sch_pu_date_st		= parser.getParameter("sch_pu_date_st","");     //��ȸ ��ŷ ������
			String sch_pu_date_ed		= parser.getParameter("sch_pu_date_ed","");     //��ȸ ��ŷ ������
			String sch_type				= parser.getParameter("sch_type","");           //�̸�,ID��ȸ ����
			String search_word			= parser.getParameter("search_word","");        //��ȸ ��
			String aplc_seq_no			= parser.getParameter("aplc_seq_no","");        //�����ȣ

			String sch_chng_aton_st		= parser.getParameter("sch_chng_aton_st","");   //��ȸ ������� ������
			String sch_chng_aton_ed		= parser.getParameter("sch_chng_aton_ed","");   //��ȸ ������� ������
			String doyn			        = parser.getParameter("doyn","");               //ó������
			String cslt_yn              = parser.getParameter("cslt_yn","");            //ó���ڵ尪
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("golf_cmmn_code",golf_cmmn_code);
			dataSet.setString("grade",grade);
			dataSet.setString("sch_yn",sch_yn);
			dataSet.setString("sch_reg_aton_st",sch_reg_aton_st);
			dataSet.setString("sch_reg_aton_ed",sch_reg_aton_ed);
			dataSet.setString("sch_pu_date_st",sch_pu_date_st);
			dataSet.setString("sch_pu_date_ed",sch_pu_date_ed);
			dataSet.setString("sch_type",sch_type);
			dataSet.setString("search_word",search_word);
			dataSet.setString("actnKey","admSpecialBookingCanList");
			dataSet.setString("sch_chng_aton_st",sch_chng_aton_st);
			dataSet.setString("sch_chng_aton_ed",sch_chng_aton_ed);
			dataSet.setString("doyn",doyn);
			dataSet.setString("aplc_seq_no",aplc_seq_no);
			dataSet.setString("cslt_yn",cslt_yn);

			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmSpecialBookingDaoProc proc = (GolfAdmSpecialBookingDaoProc)context.getProc("GolfAdmSpecialBookingDaoProc");
			GolfEvntBkMMDaoProc proc2 = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");

			DbTaoResult evntCncl    = proc.setFinalCncl(context, request, dataSet);

			if(evntCncl.isNext()){
				evntCncl.next();
				if(evntCncl.getString("RESULT").equals("00")){
					String msg = "���������� ó�� �Ǿ����ϴ�.";
					paramMap.put("msg",msg);
				}				
			}
		
			DbTaoResult evntMMListResult = (DbTaoResult) proc.execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(evntMMListResult.size()));
			paramMap.put("page_no",String.valueOf(page_no));

			
			paramMap.put("green_nm",green_nm);			
			paramMap.put("golf_cmmn_code",golf_cmmn_code);
			paramMap.put("grade",grade	);		
			paramMap.put("sch_reg_aton_st",sch_reg_aton_st);	
			paramMap.put("sch_reg_aton_ed",	sch_reg_aton_ed);	
			paramMap.put("sch_pu_date_st",	sch_pu_date_st);	
			paramMap.put("sch_pu_date_ed",sch_pu_date_ed);	
			paramMap.put("sch_type"	,	sch_type	);	
			paramMap.put("search_word",		search_word);	
			paramMap.put("total_cnt",String.valueOf(evntMMListResult.size()));
			paramMap.put("sch_chng_aton_st",sch_chng_aton_st);	
			paramMap.put("sch_chng_aton_ed",sch_chng_aton_ed);	
			paramMap.put("doyn",doyn);	
		
			request.setAttribute("evntMMListResult", evntMMListResult);	
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
