/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMainWeatherActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ���� ���� > ���� ����
*   �������  : golf
*   �ۼ�����  : 2010-02-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.main;

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
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.proc.lounge.GolfFieldWthInqDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfMainWeatherActn extends GolfActn{
	
	public static final String TITLE = "���� ���� > ��������";

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
			
			// 02.�Է°� ��ȸ		
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// ����.��� ���� ȣ��
			GolfFieldWthInqDaoProc xmlproc = (GolfFieldWthInqDaoProc)context.getProc("GolfFieldWthInqDaoProc");

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowDate = Integer.toString(cal.get(Calendar.DATE));
			cal.add(Calendar.DATE, 1);			
			String nowDate1 = Integer.toString(cal.get(Calendar.DATE));
			cal.add(Calendar.DATE, 1);			
			String nowDate2 = Integer.toString(cal.get(Calendar.DATE));
			int nowHour = cal.get(Calendar.HOUR_OF_DAY);

			String nowAmPm = "";
			
			if (nowHour >= 5 && nowHour < 11) {
				nowAmPm = "AM";
			}
			if (nowHour >= 11 && nowHour < 5) {
				nowAmPm = "PM";
			}
			xmlEtt = (List) xmlproc.readXml("CC001");
			request.setAttribute("xmlListResult", xmlEtt);
			paramMap.put("nowAmPm", nowAmPm);
			paramMap.put("nowDate", nowDate);
			paramMap.put("nowDate1", nowDate1);
			paramMap.put("nowDate2", nowDate2);
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
