/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfLessonUccDetailActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ���� > ģ���� ucc ���� �󼼺���
*   �������	: golf
*   �ۼ�����	: 2009-07-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.lesson;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.lesson.GolfLessonUccDetailDaoProc;
import com.bccard.golf.dbtao.proc.lesson.GolfLessonUccPreNextDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/  
public class GolfLessonUccDetailActn extends GolfActn{
	
	public static final String TITLE = "���� > ģ���� ucc ���� ���";
 
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
			
			String bbrd_clss 	= "0022";
			String idx 			= parser.getParameter("idx","");
			
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("idx",    	idx);
			dataSet.setString("bbrd_clss",  bbrd_clss);
			    
			// 04.���� ���̺�(Proc) ��ȸ
			GolfLessonUccDetailDaoProc proc = (GolfLessonUccDetailDaoProc)context.getProc("GolfLessonUccDetailDaoProc");
			DbTaoResult lessonUccDetail = (DbTaoResult)proc.execute(context, request,dataSet);	
			request.setAttribute("lessonUccDetail", lessonUccDetail);

			// 05.�����Խù� ��ȸ
			dataSet.setString("chk",  "pre");
			GolfLessonUccPreNextDaoProc proc_pre = (GolfLessonUccPreNextDaoProc)context.getProc("GolfLessonUccPreNextDaoProc");
			DbTaoResult lessonUccPre = (DbTaoResult)proc_pre.execute(context, request,dataSet);	
			request.setAttribute("lessonUccPre", lessonUccPre);
			
			// 06.�����Խù� ��ȸ
			dataSet.setString("chk",  "next");
			GolfLessonUccPreNextDaoProc proc_next = (GolfLessonUccPreNextDaoProc)context.getProc("GolfLessonUccPreNextDaoProc");
			DbTaoResult lessonUccNext = (DbTaoResult)proc_next.execute(context, request,dataSet);	
			request.setAttribute("lessonUccNext", lessonUccNext);
			
			
			
			// 05.��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			paramMap.put("idx", idx);
			paramMap.put("img_path", AppConfig.getAppProperty("IMG_URL_REAL")+"/lesson");
	        request.setAttribute("paramMap", paramMap); 	
			
			
			 
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
