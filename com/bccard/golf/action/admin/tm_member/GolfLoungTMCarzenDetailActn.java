/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLoungTMCarzenDetailActn
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
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Golf
* @author	
* @version	1.0 
******************************************************************************/
public class GolfLoungTMCarzenDetailActn extends GolfActn{
	
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
			String ST_YEAR         	= parser.getParameter("ST_YEAR","");
			String ST_MONTH         = parser.getParameter("ST_MONTH","");
			String ST_DAY			= parser.getParameter("ST_DAY","");
			String ED_YEAR         	= parser.getParameter("ED_YEAR","");
			String ED_MONTH         = parser.getParameter("ED_MONTH","");
			String ED_DAY			= parser.getParameter("ED_DAY","");
			String st_gb			= parser.getParameter("ST_GB","1");
			String sch_scal			= parser.getParameter("SCH_SCAL","");
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("work_date", work_date);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfLoungTMCarzenDetailProc proc = new GolfLoungTMCarzenDetailProc();
			//GolfLoungTMDetailProc proc = (GolfLoungTMDetailProc)context.getProc("GolfLoungTMDetailProc");
			DbTaoResult dUpdFormResult = proc.execute(context, dataSet);
						
			request.setAttribute("UpdFormResult", dUpdFormResult);	
			
			paramMap.put("ST_YEAR",ST_YEAR);
			paramMap.put("ST_MONTH",ST_MONTH);
			paramMap.put("ST_DAY",ST_DAY);
			paramMap.put("ED_YEAR",ED_YEAR);
			paramMap.put("ED_MONTH",ED_MONTH);
			paramMap.put("ED_DAY",ED_DAY);
			paramMap.put("ST_GB",st_gb);
			paramMap.put("SCH_SCAL",sch_scal);
			
			
	        
			request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			

	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		}  
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
