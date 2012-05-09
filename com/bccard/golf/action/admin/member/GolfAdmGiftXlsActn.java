/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGiftXlsActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ȸ������ >  ����ǰ���� > ����
*   �������  : Golf
*   �ۼ�����  : 2009-08-25
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
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmGiftXlsDaoProc;

/******************************************************************************
* Topn
* @author	(��)�̵������ 
* @version	1.0  
******************************************************************************/
public class GolfAdmGiftXlsActn extends GolfActn{ 
	
	public static final String TITLE = "������ > ȸ������ >  ����ǰ���� > ����";

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
		
		//debug("==== GolfAdmGiftXlsActn start ===");
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String search_yn	= parser.getParameter("search_yn", "N");				// �˻�����
			String sch_date_st	= parser.getParameter("SCH_DATE_ST", "");				// ��û��
			String sch_date_ed	= parser.getParameter("SCH_DATE_ED", "");				// ��û��
			String sch_type		= parser.getParameter("SCH_TYPE", "");					// �˻�����
			String sch_text		= parser.getParameter("SCH_TEXT", "");					// ����
			String sch_snd_yn 	= parser.getParameter("SCH_SND_YN","A");				// �߼ۿ���
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("search_yn", search_yn);
			dataSet.setString("sch_date_st", sch_date_st);
			dataSet.setString("sch_date_ed", sch_date_ed);
			dataSet.setString("sch_type", sch_type);
			dataSet.setString("sch_text", sch_text);
			dataSet.setString("sch_snd_yn", sch_snd_yn);
			
			//debug("==== search_yn ++++++++++++++++++++++++++ ==="+search_yn);
			//debug("==== sch_type ++++++++++++++++++++++++++ ==="+sch_type);
			//debug("==== sch_text ++++++++++++++++++++++++++ ==="+sch_text);
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmGiftXlsDaoProc proc = (GolfAdmGiftXlsDaoProc)context.getProc("GolfAdmGiftXlsDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);

			request.setAttribute("ListResult", listResult);
	        request.setAttribute("paramMap", paramMap);
	        
	        //debug("==== GolfAdmGiftXlsActn end ===");
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
