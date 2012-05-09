/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardChgFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ����Խ��� ���� ��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.bbs;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.bccard.golf.dbtao.proc.bbs.GolfBoardUpdFormDaoProc;
import com.bccard.golf.dbtao.proc.code.GolfCodeSelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfBoardChgFormActn extends GolfActn{
	
	public static final String TITLE = "����Խ��� ���� ��";

	/***************************************************************************************
	* Golf ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String admin_no = "";
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/bbs");

			// Request �� ����
			String seq_no	= parser.getParameter("p_idx", "");
			String bbs	= parser.getParameter("bbs", "");
			String id	= parser.getParameter("id", "");
			
			if (!id.trim().equals(userId)) {
				subpage_key = "errReUrl";
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "������ �ۼ��� �۸� ������ �����մϴ�.");   				
			}
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("BBS", bbs);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfBoardUpdFormDaoProc proc = (GolfBoardUpdFormDaoProc)context.getProc("GolfBoardUpdFormDaoProc");
			GolfCodeSelDaoProc coodSelProc = (GolfCodeSelDaoProc)context.getProc("GolfCodeSelDaoProc");
			DbTaoResult bbsChgResult = proc.execute(context, dataSet);
			DbTaoResult codeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, bbs, "Y"); //�Խ��� ����
			
			// 05. Return �� ����			
			request.setAttribute("bbsChgResult", bbsChgResult);		
			request.setAttribute("codeSelResult", codeSel);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
