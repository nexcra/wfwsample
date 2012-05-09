/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGoodFoodChgFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ���� ����
*   �������  : golf
*   �ۼ�����  : 2009-05-28
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
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmGoodFoodUpdFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmGoodFoodSelDaoProc;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmGoodFoodChgFormActn extends GolfActn{
	
	public static final String TITLE = "������ ���� ���� ��";

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
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");

			// Request �� ����
			long fd_seq_no	= parser.getLongParameter("p_idx", 0);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("FD_SEQ_NO", fd_seq_no);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmGoodFoodUpdFormDaoProc proc = (GolfAdmGoodFoodUpdFormDaoProc)context.getProc("GolfAdmGoodFoodUpdFormDaoProc");
			GolfAdmGoodFoodSelDaoProc coopGfSelproc = (GolfAdmGoodFoodSelDaoProc)context.getProc("GolfAdmGoodFoodSelDaoProc");
			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			// ���α׷� ����ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult goodfoodInq = proc.execute(context, dataSet);
			DbTaoResult coopGfSel = coopGfSelproc.execute(context, dataSet);
			DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0021", "Y"); //�����ڵ�
			
			// 05. Return �� ����			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			request.setAttribute("goodfoodInqResult", goodfoodInq);	
			request.setAttribute("coopGfSel", coopGfSel);
			request.setAttribute("coopCpSel", coopCpSel);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}