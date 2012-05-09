/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfFieldWthInqActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      :  ���������� �ȳ� �󼼺���(��������)
*   �������  : golf
*   �ۼ�����  : 2009-06-17
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.lounge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import com.bccard.golf.dbtao.proc.lounge.GolfFieldWthInqDaoProc;


/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfFieldWthInqActn extends GolfActn{
	
	public static final String TITLE = "���������� �ȳ� �󼼺���(��������)";

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
		List xmlEtt = new ArrayList();
		
		try {
			
			// 01.��������üũ
			
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int nowHour = cal.get(Calendar.HOUR_OF_DAY);

			String nowAmPm = "";
			
			if (nowHour >= 5 && nowHour < 11) {
				nowAmPm = "AM";
			}
			if (nowHour >= 11 && nowHour < 5) {
				nowAmPm = "PM";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("nowAmPm", nowAmPm);
			
			// Request �� ����
			String code	= parser.getParameter("code", "");
			
			//debug("code ====> "+ code);

			GolfFieldWthInqDaoProc proc = (GolfFieldWthInqDaoProc)context.getProc("GolfFieldWthInqDaoProc");
			xmlEtt = (List) proc.readXml(code); 
			
			request.setAttribute("xmlListResult", xmlEtt);
	        request.setAttribute("paramMap", paramMap);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
