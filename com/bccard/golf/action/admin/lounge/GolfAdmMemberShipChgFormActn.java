/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemberShipChgFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ȸ���� ����
*   �������  : golf
*   �ۼ�����  : 2009-07-06
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmMemberShipUpdFormDaoProc;


/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmMemberShipChgFormActn extends GolfActn{
	
	public static final String TITLE = "������ ȸ���� ���� ��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
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
			long green_memrtk_nm_seq_no	= parser.getLongParameter("p_idx", 0);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("GREEN_MEMRTK_NM_SEQ_NO", green_memrtk_nm_seq_no);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmMemberShipUpdFormDaoProc proc = (GolfAdmMemberShipUpdFormDaoProc)context.getProc("GolfAdmMemberShipUpdFormDaoProc");
			
			// ���α׷� ����ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult membershipInq = proc.execute(context, dataSet);
			
			// 05. Return �� ����			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			request.setAttribute("membershipInqResult", membershipInq);	
		    request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
