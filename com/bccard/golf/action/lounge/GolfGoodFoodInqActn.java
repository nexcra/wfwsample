/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfGoodFoodInqActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      :  ������ �ֺ����� �󼼺���
*   �������  : golf
*   �ۼ�����  : 2009-06-10
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.lounge;

import java.io.IOException;
import java.util.HashMap;
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
import com.bccard.golf.dbtao.proc.lounge.GolfGoodFoodInqDaoProc;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfGoodFoodSelDaoProc;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardComtListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfGoodFoodInqActn extends GolfActn{
	
	public static final String TITLE = "������ �ֺ����� �󼼺���";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
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
			HashMap resultMap = new HashMap();
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("intMemGrade", String.valueOf(intMemGrade));

			// Request �� ����
			long fd_seq_no	= parser.getLongParameter("p_idx", 0L);
			String reply_clss		= parser.getParameter("reply_clss", "0003");	
			String comt_papeing		= parser.getParameter("comt_papeing", "Y");	
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("FD_SEQ_NO", fd_seq_no);
			dataSet.setString("SEQ_NO",  String.valueOf(fd_seq_no));
			dataSet.setString("REPLY_CLSS", reply_clss);
			
			// �̿����� üũ
			//if (isLogin.equals("1") && intMemGrade < 4) { // �췮ȸ���̻� ����
				
				// 04.���� ���̺�(Proc) ��ȸ
				GolfGoodFoodInqDaoProc proc = (GolfGoodFoodInqDaoProc)context.getProc("GolfGoodFoodInqDaoProc");
				GolfBoardComtListDaoProc proc2 = (GolfBoardComtListDaoProc)context.getProc("GolfBoardComtListDaoProc");
				GolfGoodFoodSelDaoProc coopGfSelproc = (GolfGoodFoodSelDaoProc)context.getProc("GolfGoodFoodSelDaoProc");
				
				// ���α׷� ����ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				resultMap = proc.execute(context, resultMap, dataSet);
				DbTaoResult coopGfSel = coopGfSelproc.execute(context, dataSet);
				
				DbTaoResult bbsComtListResult = null;
				if (comt_papeing.equals("Y")) {
					bbsComtListResult = (DbTaoResult) proc2.execute(context, request, dataSet);			
				} else {
					bbsComtListResult = (DbTaoResult) proc2.execute_noPageing(context, request, dataSet);
				}
				
				//	�����Ͱ� ������ ����/���� ����
				DbTaoResult preNextInfoResult = null;
				if (resultMap.get("RESULT").equals("00")) {
					
					// ������ ������ ��ȣ ��������
					preNextInfoResult = proc.getPreNextInfo(context, dataSet);
				}
				
				// 05. Return �� ����			
				//debug("lessonInq.size() ::> " + lessonInq.size());
				paramMap.put("bbsReListSize", String.valueOf(bbsComtListResult.size()));
				
				request.setAttribute("goodfoodInqResult",resultMap);	
				request.setAttribute("bbsComtListResult", bbsComtListResult);
				request.setAttribute("coopGfSel", coopGfSel);			
				request.setAttribute("preNextInfoResult", preNextInfoResult);
			    request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			    
			//} else {
				//subpage_key = "limitReUrl";
			//}
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
