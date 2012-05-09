/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntCouponOrdListActn
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ������ > �̺�Ʈ > ���� > �������Ÿ���Ʈ
*   �������  : Golf
*   �ۼ�����  : 2011-04-13
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
***************************************************************************************************/
package com.bccard.golf.action.admin.event.coupon;

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
import com.bccard.golf.dbtao.proc.admin.event.coupon.GolfEvntCouponOrdListProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntCouponOrdListActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ > ���� > �������Ÿ���Ʈ";

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
			String ord_no      = parser.getParameter("ord_no" ,"");               // �ֹ���ȣ
			
			String sch_state    = parser.getParameter("SCH_STATE", "A");             // �˻�����
			String sch_text		= parser.getParameter("SCH_TEXT", "");              // �˻���
			String sch_date_st	= parser.getParameter("SCH_DATE_ST", "").replaceAll("-", "");           // ������
			String sch_date_ed	= parser.getParameter("SCH_DATE_ED", "").replaceAll("-", "");              // ������
			
			String type = parser.getParameter("type","");
			String cancel = parser.getParameter("cancel","N");
			String excelYn		= parser.getParameter("excelYn", "N");  
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);	
			dataSet.setString("sch_state",sch_state);
			dataSet.setString("sch_text",sch_text);
			dataSet.setString("sch_date_st",sch_date_st);
			dataSet.setString("sch_date_ed",sch_date_ed);
			dataSet.setString("excelYn",	excelYn);
			dataSet.setString("ord_no",	ord_no);
			
			// ���� ���̺�(Proc) ��ȸ		
			GolfEvntCouponOrdListProc proc = (GolfEvntCouponOrdListProc)context.getProc("GolfEvntCouponOrdListProc");
			DbTaoResult listResult =null;
			int cnt = 0;
			
			if(cancel.equals("Y")){
				
				//�������
				cnt = proc.payCancel(context, request, dataSet);
				
				//����� �ٽ� ������ �ҷ�����
				listResult = (DbTaoResult) proc.getList(context, request, dataSet);
				
				if (excelYn.equals("Y")){					
					subpage_key = "cpnXlsList";					
				}				
				
			}else {
				
				//�����ͺҷ�����
				listResult = (DbTaoResult) proc.getList(context, request, dataSet);
				
				if (excelYn.equals("Y")){				
					subpage_key = "cpnXlsList";					
				}
								
			}
			
			if("N".equals(excelYn)){
			
				listResult.next();
				String result = listResult.getString("RESULT");
				if ("00".equals(result))
					paramMap.put("total_cnt", listResult.getString("TOT_CNT"));
				else
					paramMap.put("total_cnt", "0");
			}	
			
			paramMap.put("SCH_STATE",sch_state);
			paramMap.put("SCH_TEXT",sch_text);
			paramMap.put("SCH_DATE_ST",sch_date_st);
			paramMap.put("SCH_DATE_ED",sch_date_ed);
			
			request.setAttribute("ListResult", listResult);
			
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("cancel", "N");
	      
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
}
