/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLessonListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �������α׷� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

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
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLsnRecvListDaoProc;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmLsnRecvListActn extends GolfActn{
	
	public static final String TITLE = "������ �������α׷� ����Ʈ";

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
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");

			String ssex	= parser.getParameter("ssex", ""); //����
			String scoop_cp_cd		= parser.getParameter("scoop_cp_cd", ""); //0001:���̽����� 0002:������������Ʈ	
			String slsn_type_cd		= parser.getParameter("slsn_type_cd", "0001"); //0001:�Ϲݷ��� 0002:Ư������	
			String suser_clss		= parser.getParameter("suser_clss", ""); //ȸ�����
			String slsn_expc_clss		= parser.getParameter("slsn_expc_clss", ""); //��������
			
			paramMap.put("scoop_cp_cd", scoop_cp_cd);
			paramMap.put("slsn_type_cd", slsn_type_cd);
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);

			dataSet.setString("SSEX", ssex);
			dataSet.setString("SCOOP_CP_CD", scoop_cp_cd);
			dataSet.setString("SLSN_TYPE_CD", slsn_type_cd);
			dataSet.setString("SUSER_CLSS", suser_clss);
			dataSet.setString("SLSN_EXPC_CLSS", slsn_expc_clss);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLsnRecvListDaoProc proc = (GolfAdmLsnRecvListDaoProc)context.getProc("GolfAdmLsnRecvListDaoProc");
			GolfAdmCodeSelDaoProc coodSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			DbTaoResult lsnRecvListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult coopCpSel = (DbTaoResult) coodSelProc.execute(context, dataSet, "0004", "Y"); //���޾�ü
			DbTaoResult lsnTypeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, "0003", "Y"); //��������
			DbTaoResult lsnExpcSel = (DbTaoResult) coodSelProc.execute(context, dataSet, "0006", "Y"); //��������

			paramMap.put("resultSize", String.valueOf(lsnRecvListResult.size()));
			
			request.setAttribute("lsnRecvListResult", lsnRecvListResult);
			request.setAttribute("coopCpSel", coopCpSel);	
			request.setAttribute("lsnTypeSel", lsnTypeSel);	
			request.setAttribute("lsnExpcSel", lsnExpcSel);	
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
