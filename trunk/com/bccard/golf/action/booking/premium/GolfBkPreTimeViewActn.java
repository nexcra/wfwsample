/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeViewActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ƼŸ�� ����
*   �������  : golf
*   �ۼ�����  : 2009-05-27
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.*;
import java.text.*;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.*;

/******************************************************************************
* Topn
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfBkPreTimeViewActn extends GolfActn{
	
	public static final String TITLE = "��ŷ ƼŸ�� ����";

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
			String idx			= parser.getParameter("idx", "");			
			
			Calendar cal = Calendar.getInstance();
			cal.set(parser.getIntParameter("nYear", 0), parser.getIntParameter("nMonth", 0)-1, parser.getIntParameter("nDay", 0));
			SimpleDateFormat nFormat = new SimpleDateFormat ("yyyy�� MM�� dd��");
			SimpleDateFormat dbFormat = new SimpleDateFormat ("yyyyMMdd");
			
			String nDate	= nFormat.format(cal.getTime());
			String dbDate	= dbFormat.format(cal.getTime());
			
			paramMap.put("nDate", nDate);
		
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", idx);
			dataSet.setString("dbDate", dbDate);
			
			
			// 04-1. ������ ���� ���� ���̺�(Proc) ��ȸ
			GolfBkPreGrViewDaoProc proc = (GolfBkPreGrViewDaoProc)context.getProc("GolfBkPreGrViewDaoProc");
			DbTaoResult grUpd = proc.execute(context, dataSet);
			
			// 04-2. �ش���, �ش������ ƼŸ�Ӹ���Ʈ ���� ���̺�(Proc) ��ȸ
			GolfBkPreGrTimeListDaoProc proc2 = (GolfBkPreGrTimeListDaoProc)context.getProc("GolfBkPreGrTimeListDaoProc");
			DbTaoResult timeList = (DbTaoResult) proc2.execute(context, request, dataSet);
			

			request.setAttribute("SEQ_NO", idx);
			request.setAttribute("GrUpd", grUpd);	
			request.setAttribute("TimeList", timeList);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
