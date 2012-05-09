/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMainActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ���� ����
*   �������  : golf
*   �ۼ�����  : 2009-07-02
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
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.main.GolfMainDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfFieldWthInqDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfMainActn extends GolfActn{
	
	public static final String TITLE = "���� ����";

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
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			//String lsn_seq_no = parser.getParameter("p_idx", "");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			//dataSet.setString("LSN_SEQ_NO", lsn_seq_no);
			
			// 04.���� ���̺�(Proc) ��ȸ
			//GolfMainDaoProc proc = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
			
			// ���� ȣ�� => CMS
//			GolfMainDaoProc proc1 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
//			DbTaoResult mainLessonList = proc1.getLessonList(context, dataSet, 3, 0);			
//			request.setAttribute("mainLessonList", mainLessonList);	
//			paramMap.put("lessonImgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lesson");
			
			// ��3 ȣ�� => CMS
//			GolfMainDaoProc proc2 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
//			DbTaoResult mainParList = proc2.getParList(context, dataSet, 3, 0);			
//			request.setAttribute("mainParList", mainParList);	
//			paramMap.put("ParImgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/bk_gr");
			
			// ��3 ��ũ�� ȣ��
			GolfMainDaoProc proc3 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
			DbTaoResult mainParScrollList = proc3.getParScrollList(context, dataSet);			
			request.setAttribute("mainParScrollList", mainParScrollList);	
			
			// �Խ��� ȣ�� (��������)
			GolfMainDaoProc proc4 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
			DbTaoResult mainGolfNewsList = proc4.getNewsList(context, dataSet, 3, 30);			
			request.setAttribute("mainGolfNewsList", mainGolfNewsList);
			
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

			// ��ǥ ���� ȣ�� => CMS
//			GolfMainDaoProc proc5 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
//			DbTaoResult mainMainGoodFoodList = proc5.getMainGoodFoodList(context, dataSet, 1, 0);			
//			request.setAttribute("mainMainGoodFoodList", mainMainGoodFoodList);

			// ���� ȣ��
			GolfMainDaoProc proc6 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
			DbTaoResult mainGoodFoodList = proc6.getGoodFoodList(context, dataSet, 3, 50);			
			request.setAttribute("mainGoodFoodList", mainGoodFoodList);
			paramMap.put("goodFoodImgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
