/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardInqActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ����Խ��� ���� �󼼺���
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.club;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.club.GolfClubBoardInqDaoProc;
import com.bccard.golf.dbtao.proc.club.GolfClubBoardComtListDaoProc;
import com.bccard.golf.dbtao.proc.club.GolfClubMasterDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfClubBoardInqActn extends GolfActn{
	
	public static final String TITLE = "����Խ��� �󼼺���";

	/***************************************************************************************
	* Golf ������ȭ��
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
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("intMemGrade", String.valueOf(intMemGrade));

			// Request �� ����
			String seq_no	= parser.getParameter("p_idx", "");
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String club_code		= parser.getParameter("club_code", "");
			String bbs_code		= parser.getParameter("bbs_code", "");
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SCH_CLUB_SEQ_NO", club_code);
			dataSet.setString("SCH_BBRD_SEQ_NO", bbs_code);
			dataSet.setString("CLUB_CODE", club_code);
			dataSet.setString("CDHD_ID", userId);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfClubBoardInqDaoProc proc = (GolfClubBoardInqDaoProc)context.getProc("GolfClubBoardInqDaoProc");
			GolfClubBoardComtListDaoProc proc2 = (GolfClubBoardComtListDaoProc)context.getProc("GolfClubBoardComtListDaoProc");
			GolfClubMasterDaoProc clubProc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			
			resultMap = proc.execute(context, resultMap, dataSet);			
			DbTaoResult bbsComtListResult = (DbTaoResult) proc2.execute(context, request, dataSet);		
			DbTaoResult clubMasterResult = (DbTaoResult) clubProc.execute(context, dataSet);	

			// ��� �α��� ����..
			String clubAdmId = "";
			if (clubMasterResult != null && clubMasterResult.isNext()) {
				clubMasterResult.first();
				clubMasterResult.next();
				if (clubMasterResult.getObject("RESULT").equals("00")) {
					clubAdmId = (String)clubMasterResult.getString("OPN_PE_ID");
				}
			}
			String clubAdmChk = "N";
			if (userId.equals(clubAdmId)) clubAdmChk = "Y";
			
			// Ŭ��ȸ�� ����
			String clubMemChk = clubProc.getClubMemChk(context, dataSet);
			
			// �����Ͱ� ������ ����/����/��ȸ�� ������Ʈ ����
			DbTaoResult preNextInfoResult = null;
			if (resultMap.get("RESULT").equals("00")) {
				
				// ������ ������ ��ȣ ��������
				preNextInfoResult = proc.getPreNextInfo(context, dataSet);
				
				// ��ȸ�� ������Ʈ
				int readCntUpdResult = proc.readCntUpd(context, dataSet);
				if (readCntUpdResult > 0) {
					Integer readCnt = new Integer(resultMap.get("INQR_NUM").toString());
					resultMap.put("INQR_NUM",  String.valueOf(readCnt.intValue() + 1));
				}
			}
			
			paramMap.put("bbsReListSize", String.valueOf(bbsComtListResult.size()));
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"club/"+club_code);
			paramMap.put("clubAdmChk", clubAdmChk);
			paramMap.put("clubMemChk", clubMemChk);
			
			// 05. Return �� ����
			request.setAttribute("bbsInqResult", resultMap);
			request.setAttribute("bbsComtListResult", bbsComtListResult);
			request.setAttribute("preNextInfoResult", preNextInfoResult);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
