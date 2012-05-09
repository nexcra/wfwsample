/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGolfFieldListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ������ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lounge;

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
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmGolfFieldListDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmGolfFieldListActn extends GolfActn{
	
	public static final String TITLE = "������ ������ ����Ʈ";

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
			
			String gf_area_cd	= parser.getParameter("s_gf_area_cd", "");		// ������
			String gf_clss_cd	= parser.getParameter("s_gf_clss_cd", "");		// ȸ����/�ۺ�
			String gf_hole_cd	= parser.getParameter("s_gf_hole_cd", "");		// Ȧ��
			String search_sel	= parser.getParameter("search_sel", "");		// �����˻�����
			String search_word	= parser.getParameter("search_word", "");		// �����˻�����
			
			
			//debug("page_no :::: >>>> " + page_no);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("GF_AREA_CD", gf_area_cd);
			dataSet.setString("GF_CLSS_CD", gf_clss_cd);
			dataSet.setString("GF_HOLE_CD", gf_hole_cd);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmGolfFieldListDaoProc proc = (GolfAdmGolfFieldListDaoProc)context.getProc("GolfAdmGolfFieldListDaoProc");
			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			DbTaoResult golffieldListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// �ڵ� ��ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopCpSel1 = coopCpSelProc.execute(context, dataSet, "0019", "Y"); //�����屸���ڵ�
			DbTaoResult coopCpSel2 = coopCpSelProc.execute(context, dataSet, "0020", "Y"); //������Ȧ���ڵ�
			DbTaoResult coopCpSel3 = coopCpSelProc.execute(context, dataSet, "0021", "Y"); //�����������ڵ�

			paramMap.put("resultSize", String.valueOf(golffieldListResult.size()));
			
			request.setAttribute("golffieldListResult", golffieldListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			request.setAttribute("coopCpSel1", coopCpSel1);
			request.setAttribute("coopCpSel2", coopCpSel2);
			request.setAttribute("coopCpSel3", coopCpSel3);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
