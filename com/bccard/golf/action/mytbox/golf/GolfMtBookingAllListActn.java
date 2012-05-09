/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkCheckAllListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ > �ֱ� ��ŷ Ȯ��
*   �������  : Golf
*   �ۼ�����  : 2009-05-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.golf;

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
import com.bccard.golf.dbtao.proc.booking.check.*;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMtBookingAllListActn extends GolfActn{
	
	public static final String TITLE = "�ֱ� ��ŷ Ȯ��";

	/***************************************************************************************
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
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("LISTTYPE", "ALL");


			// 04.���� ���̺�(Proc) ��ȸ - ���� ��ŷ ��ü ���� 
			GolfBkCheckAllViewDaoProc proc = (GolfBkCheckAllViewDaoProc)context.getProc("GolfBkCheckAllViewDaoProc");
			DbTaoResult allView = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("AllView", allView);
			
			// 04.���� ���̺�(Proc) ��ȸ - �����̾���ŷ
			GolfBkCheckPreListDaoProc proc1 = (GolfBkCheckPreListDaoProc)context.getProc("GolfBkCheckPreListDaoProc");
			DbTaoResult preList = (DbTaoResult) proc1.execute(context, request, dataSet);
			request.setAttribute("PreList", preList);
			
			// 04.���� ���̺�(Proc) ��ȸ - ��3��ŷ
			GolfBkCheckParListDaoProc proc2 = (GolfBkCheckParListDaoProc)context.getProc("GolfBkCheckParListDaoProc");
			DbTaoResult parList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("ParList", parList);
			
			// 04.���� ���̺�(Proc) ��ȸ - ���ֱ׸�������
			GolfBkCheckJjListDaoProc proc3 = (GolfBkCheckJjListDaoProc)context.getProc("GolfBkCheckJjListDaoProc");
			DbTaoResult jjList = (DbTaoResult) proc3.execute(context, request, dataSet);
			request.setAttribute("JjList", jjList);

			// 04.���� ���̺�(Proc) ��ȸ - ��ī�� 72
			GolfBkCheckSkyListDaoProc proc4 = (GolfBkCheckSkyListDaoProc)context.getProc("GolfBkCheckSkyListDaoProc");
			DbTaoResult skyList = (DbTaoResult) proc4.execute(context, request, dataSet);
			request.setAttribute("SkyList", skyList);
							
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
