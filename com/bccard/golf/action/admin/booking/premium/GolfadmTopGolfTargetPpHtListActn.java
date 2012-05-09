/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfTargetPpHtListActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ ��ŷ������
*   �������  : Golf
*   �ۼ�����  : 2010-11-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;


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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmPreTimeListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfadmTopGolfTargetPpHtListActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ������";

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
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request �� ����
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size		= parser.getLongParameter("record_size", 10);		// ����������¼�		
			
			String seq				= parser.getParameter("seq","");           //idx
			String sort				= parser.getParameter("sort","1000"); 
			String type				= parser.getParameter("type","2"); 
			
						
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no); 
			dataSet.setLong("RECORD_SIZE", record_size);
			
			dataSet.setString("seq",seq);
			dataSet.setString("sort",sort);
			dataSet.setString("CDHD_ID", seq);
			
			// 04.���� ���̺�(Proc) ��ȸ
			//GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
			
			GolfadmPreTimeListDaoProc proc = (GolfadmPreTimeListDaoProc)context.getProc("admPreTimeListDaoProc");
			DbTaoResult listResult = null;
			DbTaoResult listResult2 = null;
			
			if("2".equals(type))
			{
				listResult = (DbTaoResult) proc.execute_status(context, request, dataSet);
				
				//��ü��� ��
				listResult2 = (DbTaoResult) proc.execute_status2(context, request, dataSet);
			}
			
			
			
			
			String dataVal1 = "0";
			String dataVal2 = "0";
			String dataVal3 = "0";
			String dataVal4 = "0";
			String dataVal5 = "0";
			String dataVal6 = "0";
			String conm		= "";
			
			if (listResult2 != null && listResult2.isNext()) {
				listResult2.first();
				listResult2.next();
				if (listResult2.getObject("RESULT").equals("00")) {
					dataVal1 = (String)listResult2.getString("dataVal1");
					dataVal2 = (String)listResult2.getString("dataVal2");
					dataVal3 = (String)listResult2.getString("dataVal3");
					dataVal4 = (String)listResult2.getString("dataVal4");
					dataVal5 = (String)listResult2.getString("dataVal5");
					dataVal6 = (String)listResult2.getString("dataVal6");
					conm	 = (String)listResult2.getString("CO_NM");
				}
			}
			
			
			
			paramMap.put("resultSize", String.valueOf(listResult.size()));
			paramMap.put("page_no",String.valueOf(page_no));
			
			paramMap.put("seq",seq);
			paramMap.put("sort",sort);
			
			paramMap.put("conm",conm);
			paramMap.put("dataVal1",dataVal1);
			paramMap.put("dataVal2",dataVal2);
			paramMap.put("dataVal3",dataVal3);
			paramMap.put("dataVal4",dataVal4);
			paramMap.put("dataVal5",dataVal5);
			paramMap.put("dataVal6",dataVal6);
			
			
			request.setAttribute("listResult", listResult);	
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
