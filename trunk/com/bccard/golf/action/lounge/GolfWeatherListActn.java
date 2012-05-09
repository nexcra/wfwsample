/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfWeatherListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      :  ������ ���� 
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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.lounge.GolfWeatherListDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfFieldWthInqDaoProc;


/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfWeatherListActn extends GolfActn{
	
	public static final String TITLE = "������ ����";

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
			String code	= parser.getParameter("s_code", "");
			String code_nm	= parser.getParameter("s_code_nm", "");
			String rgn_nm	= parser.getParameter("s_rgn_nm", "������");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RGN_NM", rgn_nm);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfWeatherListDaoProc proc = (GolfWeatherListDaoProc)context.getProc("GolfWeatherListDaoProc");
			GolfWeatherListDaoProc proc2 = (GolfWeatherListDaoProc)context.getProc("GolfWeatherListDaoProc");
			GolfFieldWthInqDaoProc xmlproc = (GolfFieldWthInqDaoProc)context.getProc("GolfFieldWthInqDaoProc");
			
			DbTaoResult golfwthListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult golfwthResult = (DbTaoResult) proc2.execute2(context, request, dataSet);
			
			// �ش����� ù��° ������ ���� ��������
			String firstCode = "";
			String firstCodeNm = "";
			if (golfwthResult != null && golfwthResult.isNext()) {
				golfwthResult.first();
				golfwthResult.next();
				if (golfwthResult.getObject("RESULT").equals("00")) {
					firstCode = (String)golfwthResult.getString("GREEN_WEATH_CLSS");
					firstCodeNm = (String)golfwthResult.getString("GREEN_NM");
				}
			}
			
			if (GolfUtil.isNull(code)) code = firstCode;
			if (GolfUtil.isNull(code_nm)) code_nm = firstCodeNm;
			
			xmlEtt = (List) xmlproc.readXml(code);
			
			//debug("code ======> "+ code);
			
			paramMap.put("s_code", code);
			paramMap.put("s_code_nm", code_nm);
			paramMap.put("s_rgn_nm", rgn_nm);
			
			request.setAttribute("golfwthListResult", golfwthListResult);
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
