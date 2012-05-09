/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemHstListActn
*   �ۼ���    : �强��
*   ����      : ������ > ���ΰ��� > ȸ������ > ȸ������Ʈ > ��޺��泻��
*   �������  : Golf
*   �ۼ�����  : 2010-11-10
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

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComSelectListDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Topn
* @author	�强�� 
* @version	1.0 
******************************************************************************/
public class GolfAdmMemHstListActn extends GolfActn{
	
	public static final String TITLE = "������ > ���ΰ��� > ȸ������ > ȸ������Ʈ  > ��޺��泻��";

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
			long hst_page_no	= parser.getLongParameter("hst_page_no", 1L);		// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 20);		// ����������¼�	
			String cdhd_ID		= parser.getParameter("CDHD_ID", "");	
			String juminNo		= parser.getParameter("JUMIN_NO");
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("hst_page_no", hst_page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("CDHD_ID", cdhd_ID);
						
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmMemHstListDaoProc proc = (GolfAdmMemHstListDaoProc)context.getProc("GolfAdmMemHstListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(listResult.size()));
			
			request.setAttribute("ListResult", listResult);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("JUMIN_NO", juminNo);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
