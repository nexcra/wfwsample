/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmPricesAllChgFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �ü� ��ü����
*   �������  : Golf
*   �ۼ�����  : 2009-07-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lounge;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.text.SimpleDateFormat; 

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
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmPricesAllUpdFormDaoProc;


/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmPricesAllChgFormActn extends GolfActn{
	
	public static final String TITLE = "������ �ü� ��ü���� ��";

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
		
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
			String nowDay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			
			/*
			SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
			Date toDay = new Date(); 
			String nowDate = DateFormat.format(toDay);
			*/
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			
			// Request �� ����
			String qut_year	= parser.getParameter("qut_year", nowYear);		// �⵵
			String qut_month	= parser.getParameter("qut_month", GolfUtil.lpad(nowMonth, 2, "0"));	// ��
			String qut_day	= parser.getParameter("qut_day", GolfUtil.lpad(nowDay, 2, "0"));	// �� 
			
			String qut_date = qut_year + qut_month + qut_day;
			
			//debug("fee_date :::: >>>> " + fee_date);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("QUT_DATE", qut_date);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmPricesAllUpdFormDaoProc proc = (GolfAdmPricesAllUpdFormDaoProc)context.getProc("GolfAdmPricesAllUpdFormDaoProc");
			
			// ���α׷� ����ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult pricesAllInq = proc.execute(context, dataSet);
			
			// 05. Return �� ����			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			paramMap.put("qut_year", qut_year);
			paramMap.put("qut_month", qut_month);
			paramMap.put("qut_day", qut_day);
			
			request.setAttribute("pricesAllInqResult", pricesAllInq);	
			request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
