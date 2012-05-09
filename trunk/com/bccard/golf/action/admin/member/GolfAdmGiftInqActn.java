/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGiftInqActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ ����ǰ���� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-08-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmGiftlnqDaoProc;


/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmGiftInqActn extends GolfActn {
	
	public static final String TITLE = "������ ����ǰ���� ����Ʈ";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		
	DbTaoConnection con = null;
	ResultException rx;

	//debug("==== GolfAdmGiftInqActn start ===");
	 
	try {
		RequestParser	parser	= context.getRequestParser("default", request, response);
		//1. �Ķ��Ÿ �� 
		long page_no		= parser.getLongParameter("page_no", 1L);				// ��������ȣ
		long page_size		= parser.getLongParameter("page_size", 10L);			// ����������¼�	
		String total_cnt 	= "0";
		String search_yn	= parser.getParameter("search_yn", "N");				// �˻�����
		
		String sch_type		= parser.getParameter("SCH_TYPE", "");					// �˻�����
		String sch_text		= parser.getParameter("SCH_TEXT", "");					// ����
		String sch_snd_yn 	= parser.getParameter("SCH_SND_YN","A");				// �߼ۿ���
		String sch_join_chnl = parser.getParameter("SCH_JOIN_CHNL","A");			// ����ȸ������					

		String st_year 			= parser.getParameter("ST_YEAR","");
		String st_month 		= parser.getParameter("ST_MONTH","");
		String st_day 			= parser.getParameter("ST_DAY","");
		String ed_year 			= parser.getParameter("ED_YEAR","");
		String ed_month 		= parser.getParameter("ED_MONTH","");
		String ed_day 			= parser.getParameter("ED_DAY","");
		
		String sch_date_st		= st_year+st_month+st_day;
		String sch_date_ed		= ed_year+ed_month+ed_day;
					
		//2.��ȸ
		DbTaoDataSet input = new DbTaoDataSet(TITLE);
		input.setString("search_yn",	search_yn);
		input.setString("sch_date_st", sch_date_st);
		input.setString("sch_date_ed", sch_date_ed);
		input.setString("sch_type", sch_type);
		input.setString("sch_text", sch_text);
		input.setString("sch_snd_yn", sch_snd_yn);
		input.setString("sch_join_chnl", sch_join_chnl);		
		input.setLong("page_no",		page_no);
		input.setLong("page_size",		page_size);
		 
		GolfAdmGiftlnqDaoProc proc = (GolfAdmGiftlnqDaoProc)context.getProc("GolfAdmGiftlnqDaoProc");		
		DbTaoResult giftListInq = (DbTaoResult)proc.execute(context, request, input);
		
		/*
		if(giftListInq != null && giftListInq.isNext() ) {
			giftListInq.next();
			if("00".equals(giftListInq.getString("RESULT"))) {
				total_cnt = giftListInq.getString("total_cnt");

			}
		}
		*/
		
		//���԰�� ���
		DbTaoResult join_channel = (DbTaoResult)proc.getJoinChnlInq(context, request, input);
		
		Map paramMap = parser.getParameterMap();
		paramMap.put("ST_YEAR",st_year);
		paramMap.put("ST_MONTH",st_month);
		paramMap.put("ST_DAY",st_day);
		paramMap.put("ED_YEAR",ed_year);
		paramMap.put("ED_MONTH",ed_month);
		paramMap.put("ED_DAY",ed_day);
		 
		paramMap.put("total_cnt", 	total_cnt);
		paramMap.put("SCH_TYPE", 	sch_type);
		paramMap.put("SCH_TEXT", 	sch_text);
		paramMap.put("SCH_SND_YN", 	sch_snd_yn);
		paramMap.put("SCH_JOIN_CHNL", 	sch_join_chnl);
		paramMap.put("page_size", parser.getParameter("page_size", "10"));
		
		request.setAttribute("giftListInq", giftListInq);
		request.setAttribute("join_channel", join_channel);
		request.setAttribute("paramMap", paramMap); 
		
	//	debug("==== GolfAdmGiftInqActn end ==="); 
		
	}catch(Throwable t) {
		return errorHandler(context,request,response,t);
	}finally{
		try{ if(con  != null) con.close();  }catch( Exception ignored){}
	}
	return super.getActionResponse(context);
		
	}
}
