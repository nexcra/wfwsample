/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmUNEventWinListActn
*   �ۼ���    : E4NET ���弱
*   ����      : ������ > ��ŷ > �����̾� > ������ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-08-05
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
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmUNEventInfoProc;

/******************************************************************************
* Topn
* @author	E4NET
* @version	1.0
******************************************************************************/
public class GolfAdmUNEventWinListActn extends GolfActn{
	
	public static final String TITLE = "������ > ���ΰ��� > ȸ������ > ȸ������Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�	
			String evnt_no      = parser.getParameter("evnt_no" ,"");               // �̺�Ʈ ��ȣ
			String use_no      = parser.getParameter("use_no" ,"");               // ��ǰ����
			
			String sch_state    = parser.getParameter("SCH_STATE", "A");             // �˻�����
			String sch_text		= parser.getParameter("SCH_TEXT", "");              // �˻���
			String sch_date_st	= parser.getParameter("SCH_DATE_ST", "").replaceAll("-", "");           // ������
			String sch_date_ed	= parser.getParameter("SCH_DATE_ED", "").replaceAll("-", "");              // ������
			
			String type = parser.getParameter("type","");
			
			String excelYn		= parser.getParameter("excelYn", "N");  
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);	
			dataSet.setString("evnt_no", evnt_no);
			dataSet.setString("use_no", use_no);
			dataSet.setString("sch_state",sch_state);
			dataSet.setString("sch_text",sch_text);
			dataSet.setString("sch_date_st",sch_date_st);
			dataSet.setString("sch_date_ed",sch_date_ed);
			dataSet.setString("excelYn",	excelYn);
			
			// 03.���� ���̺�(Proc) ��ȸ
			GolfAdmUNEventInfoProc proc = (GolfAdmUNEventInfoProc)context.getProc("GolfAdmUNEventInfoProc");
			DbTaoResult listResult =null;
			if(type.equals("excel")){
				listResult = (DbTaoResult) proc.detExcelResult(context, request, dataSet);
			}else{
				listResult = (DbTaoResult) proc.detResult(context, request, dataSet);
			}
			
			if("N".equals(excelYn))
			{
			
				listResult.next();
				String result = listResult.getString("RESULT");
				if ("00".equals(result))
					paramMap.put("total_cnt", listResult.getString("TOT_CNT"));
				else
					paramMap.put("total_cnt", "0");
			}
			paramMap.put("resultSize", String.valueOf(listResult.size()));
			paramMap.put("evnt_no",evnt_no);
			paramMap.put("use_no",use_no);
			paramMap.put("SCH_STATE",sch_state);
			paramMap.put("SCH_TEXT",sch_text);
			paramMap.put("SCH_DATE_ST",sch_date_st);
			paramMap.put("SCH_DATE_ED",sch_date_ed);

			request.setAttribute("ListResult", listResult);			
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