/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfLessonUccInqActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ���� > ģ���� ucc ���� ���
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
import com.bccard.golf.dbtao.proc.lesson.GolfLessonUccInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/  
public class GolfLessonUccInqActn extends GolfActn{
	
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
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no",    	page_no);
			dataSet.setString("bbrd_clss",    bbrd_clss);
		
			// 04.���� ���̺�(Proc) ��ȸ
			GolfLessonUccInqDaoProc proc = (GolfLessonUccInqDaoProc)context.getProc("GolfLessonUccInqDaoProc");
			DbTaoResult lessonUccInq = (DbTaoResult)proc.execute(context, request,dataSet);
			request.setAttribute("lessonUccInq", lessonUccInq);
		
			
			// 05.�ѰԽù� �� ��ȸ
			String ttCnt = proc.getTtCount(context,dataSet);
			
			// 06.�� ������ ���
			int intTtCnt = Integer.parseInt(ttCnt);
			int ttPag = 0;
			if(intTtCnt > 0){
				ttPag = intTtCnt/5;
			}else{
				ttPag = 1;
			}
			
			if(ttPag == 0) ttPag = 1;
			
			// 05.��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		
			paramMap.put("page_no", Long.toString(page_no));
			paramMap.put("ttCnt", ttCnt);
			paramMap.put("ttPag", Integer.toString(ttPag));
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
