/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMojibDetailActn
*   �ۼ���    : ���񽺰����� ������
*   ����      : ������ TMȸ�� �󼼺���
*   �������  : golf
*   �ۼ�����  : 2009-07-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

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
import com.bccard.golf.dbtao.proc.admin.tm_member.GolfAdmMojibProc;


/******************************************************************************
* Golf
* @author	
* @version	1.0 
******************************************************************************/
public class GolfAdmMojibDetailActn extends GolfActn{
	
	public static final String TITLE = "������ > TM���� > TMȸ������ ";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

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
			String jumin_no			= parser.getParameter("jumin_no", "");
			String work_date		= parser.getParameter("work_date", "");
			String page_no          = parser.getParameter("page_no","");
			String start_date       = parser.getParameter("start_date","");
			String end_date         = parser.getParameter("end_date","");
			String sch_type         = parser.getParameter("SCH_TYPE","");
			String sch_text         = parser.getParameter("SCH_TEXT","");
			String sch_state		= parser.getParameter("SCH_STATE","");

			if(jumin_no == null || jumin_no == ""){				
				jumin_no = (String)request.getAttribute("jumin_no");
				debug("jumin_no111>>>>>>>>>>>>>>>>>>>>" + jumin_no);
			}			
			
			paramMap.put("jumin_no",jumin_no);
			paramMap.put("work_date",work_date);
			paramMap.put("page_no",page_no);
			paramMap.put("start_date",start_date);
			paramMap.put("end_date",end_date);
			paramMap.put("sch_type",sch_type);
			paramMap.put("sch_text",sch_text);
			paramMap.put("sch_state",sch_state);

			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("work_date", work_date);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmMojibProc proc = new GolfAdmMojibProc();
			//GolfLoungTMDetailProc proc = (GolfLoungTMDetailProc)context.getProc("GolfLoungTMDetailProc");

			DbTaoResult dUpdFormResult = (DbTaoResult)proc.getDetail(context, dataSet);
			DbTaoResult taoResult = (DbTaoResult)proc.getCommonCode(context, dataSet,"0050");		

			DbTaoResult taoCode =  (DbTaoResult)proc.getCommonCode(context, dataSet , "0051");	
			
			request.setAttribute("taoCode", taoCode);

			request.setAttribute("UpdFormResult", dUpdFormResult);	
			request.setAttribute("taoResult", taoResult);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		}  
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
