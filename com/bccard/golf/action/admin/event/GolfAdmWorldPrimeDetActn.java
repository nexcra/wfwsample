/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmWorldPrimeDetActn.java
*   �ۼ���    : ������
*   ����      : ������ > �̺�Ʈ > ���� ������ > ��û���� > �� ���� 
*   �������  : Golf
*   �ۼ�����  : 2010-08025
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

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmWorldPrimeDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	������
* @version	1.0
******************************************************************************/
public class GolfAdmWorldPrimeDetActn extends GolfActn{
	
	public static final String TITLE = "������ ���� ������ ��û�� �󼼺���";

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
		
		DbTaoResult evntMMdetResult = null;
		DbTaoResult evntMMdetPayResult = null;
		DbTaoResult goodBookingResult = null;
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request �� ����
			String aplc_seq_no          = parser.getParameter("aplc_seq_no","");        //�����ȣ
			
			String payOrderNo 	= "";
			String userJuminNo	= "";
			String csltTn		= "";
			if(!"".equals(aplc_seq_no))
			{
								
				// 03.Proc �� ���� �� ���� 
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("aplc_seq_no",aplc_seq_no);
				
				// 04.���� ���̺�(Proc) ��ȸ
				GolfAdmWorldPrimeDaoProc proc = (GolfAdmWorldPrimeDaoProc)context.getProc("GolfAdmWorldPrimeDaoProc");
				evntMMdetResult = (DbTaoResult) proc.getDetail(context, request, dataSet);
				if(evntMMdetResult!=null)
				{
					evntMMdetResult.next();
					payOrderNo 		= StrUtil.isNull(evntMMdetResult.getString("CO_NM"), "");
					userJuminNo	 	= StrUtil.isNull(evntMMdetResult.getString("JUMIN_NO"), "");
					csltTn		 	= StrUtil.isNull(evntMMdetResult.getString("CSLT_YN"), "");
					
					debug("## GolfAdmWorldPrimeDetActn | payOrderNo : "+payOrderNo+" | userJuminNo : "+userJuminNo);
					
					dataSet.setString("payOrderNo",payOrderNo);
					dataSet.setString("userJuminNo",userJuminNo);
					
					// �������̺� ��ȸ
					if(!"".equals(payOrderNo))
					{												
						evntMMdetPayResult = (DbTaoResult) proc.getPayDetail(context, request, dataSet);
					}
										
					
					//��ǰ ������� ��������
					if(!"".equals(userJuminNo))
					{						
						goodBookingResult = (DbTaoResult) proc.getBookingDetail(context, request, dataSet);
						
						
					}
					
					
					
					
					
				}						
				
				
				
				
				
			}
			
			
			
			paramMap.put("aplc_seq_no",aplc_seq_no);
			paramMap.put("csltTn",csltTn);
			
	        request.setAttribute("evntMMdetResult", evntMMdetResult);
	        request.setAttribute("evntMMdetPayResult", evntMMdetPayResult);
	        request.setAttribute("goodBookingResult", goodBookingResult);
	        request.setAttribute("paramMap", paramMap);
			
			
			
			//GolfEvntBkMMDaoProc proc2 = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");

			

			
				
				/*
				 * if(evntMMdetResult.isNext()){
				evntMMdetResult.next();
				String name = evntMMdetResult.getString("BKG_PE_NM");            //���̵�
				debug("Actn : �̸� : BKG_PE_NM>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + name);
			}
				String intMemGradeNM = evntMMdetResult.getString("GRADE");       //���
				debug("============ GRADE : " + intMemGradeNM);
				
				String intMemGrade = "3";
				if("Champion".equals(intMemGradeNM)){
					intMemGrade = "1";
				}else if("Blue".equals(intMemGradeNM)||"Black".equals(intMemGradeNM)){
					intMemGrade = "2";
				}else if("White".equals(intMemGradeNM)){
					intMemGrade = "0";
				}else { //Gold,NHƼŸ��,NH�÷�Ƽ��,������������
					intMemGrade = "3"; 
				}
				dataSet.setString("intMemGrade",intMemGrade);
				dataSet.setString("userId",userId);				 
				dataSet.setString("cdhd_id",userId);	
			}*/

			//DbTaoResult evntBkMMInq = proc2.getReserveList(context, dataSet);
			
			/*DbTaoResult evntRevList = proc2.execute(context, dataSet);
			

			String cnt = "";
			String tot_cnt = "";
			
			/*if(evntBkMMInq.isNext()){
				evntBkMMInq.next();
				cnt = evntBkMMInq.getString("CNT");
				tot_cnt = evntBkMMInq.getString("TOT");
				debug("cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
				debug("tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				paramMap.put("cnt",cnt);
				paramMap.put("tot_cnt",tot_cnt);
			}
			String can_cnt      = String.valueOf(Integer.parseInt(tot_cnt) - Integer.parseInt(cnt));
		

			String can_cnt = "0";
			String blockDate = "";
			
			GolfBkBenefitTimesDaoProc proc_count = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult evntMMInq = proc_count.getAdmPreBkEvntBenefit(context, dataSet, request);
			if(evntMMInq.isNext()){
				evntMMInq.next();
				
				tot_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_PMI_NUM"));
				cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_DONE"));
				can_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_MO"));
				blockDate = evntMMInq.getString("blockDate");
				 
				debug("Actn : ���Ǽ�: cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
				debug("Actn : �����Ǽ� : can_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + can_cnt);
				debug("Actn : �ѻ���� ���ִ°Ǽ� : tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				debug("Actn : ������ : blockDate>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + blockDate);
			}
			
			paramMap.put("tot_cnt",tot_cnt);*/
			
			/*paramMap.put("page_no",String.valueOf(page_no));
			paramMap.put("green_nm",green_nm);			
			paramMap.put("golf_cmmn_code",golf_cmmn_code);
			paramMap.put("grade",grade	);		
			paramMap.put("sch_reg_aton_st",sch_reg_aton_st);	
			paramMap.put("sch_reg_aton_ed",	sch_reg_aton_ed);	
			paramMap.put("sch_pu_date_st",	sch_pu_date_st);	
			paramMap.put("sch_pu_date_ed",sch_pu_date_ed	);	
			paramMap.put("sch_type"	,	sch_type	);	
			paramMap.put("search_word",		search_word);	
			paramMap.put("can_cnt",can_cnt);
			paramMap.put("cnt",cnt);*/
			
			//debug("Actn : �̸� : BKG_PE_NM>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + evntMMdetResult.getString("BKG_PE_NM"));
				
			//request.setAttribute("evntRevList" , evntRevList);
			//request.setAttribute("record_size", String.valueOf(record_size));
	        
	        
	        
	        

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
