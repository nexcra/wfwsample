/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfWeatherPopInqActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �����ڵ� �˻�
*   �������  : golf
*   �ۼ�����  : 2009-06-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.weather;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.weather.GolfWeatherPopInqDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfWeatherPopInqActn extends GolfActn{
	
	public static final String TITLE = "������ �����ڵ� �˻�";

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
			paramMap.put("RurlPath", AppConfig.getAppProperty("URL_REAL"));

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			
			String gf_nm = parser.getParameter("gf_nm", ""); //�������
			
			
			String rgn_nm	= parser.getParameter("s_rgn_nm", ""); //������
			
			
			if(rgn_nm.equals("1")) rgn_nm = "������";
			if(rgn_nm.equals("2")) rgn_nm = "������";
			if(rgn_nm.equals("3")) rgn_nm = "��û�ϵ�";
			if(rgn_nm.equals("4")) rgn_nm = "��û����";
			if(rgn_nm.equals("5")) rgn_nm = "����ϵ�";
			if(rgn_nm.equals("6")) rgn_nm = "���󳲵�";
			if(rgn_nm.equals("7")) rgn_nm = "���ϵ�";
			if(rgn_nm.equals("8")) rgn_nm = "��󳲵�";
			if(rgn_nm.equals("9")) rgn_nm = "���ֵ�";
			
			String fmnm = parser.getParameter("fmnm", "");
			String cd = parser.getParameter("cd", "");
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("GF_NM", gf_nm);
			dataSet.setString("RGN_NM", rgn_nm);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfWeatherPopInqDaoProc proc = (GolfWeatherPopInqDaoProc)context.getProc("GolfWeatherPopInqDaoProc");
			
			// �����ڵ� ��ȸ :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult weatherInq = proc.execute(context, request, dataSet);
			
			// 05. Return �� ����			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			paramMap.put("fmnm", fmnm);
			paramMap.put("cd", cd);
			
			paramMap.put("resultSize", String.valueOf(weatherInq.size()));
			
			request.setAttribute("weatherInqResult", weatherInq);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
