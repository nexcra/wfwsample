/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ����Խ��� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.club;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.club.GolfBoardListDaoProc;
import com.bccard.golf.dbtao.proc.club.GolfBoardMasterDaoProc;
import com.bccard.golf.dbtao.proc.club.GolfClubMasterDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfBoardListActn extends GolfActn{
	
	public static final String TITLE = "����Խ��� ����Ʈ";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

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
			paramMap.put("title", TITLE);

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 12);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			String club_code		= parser.getParameter("club_code", "");
			String bbs_code		= parser.getParameter("bbs_code", "");
			
			paramMap.put("club_code", club_code);
			paramMap.put("bbs_code", bbs_code);
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SCH_CLUB_SEQ_NO", club_code);
			dataSet.setString("SCH_BBRD_SEQ_NO", bbs_code);
			dataSet.setString("CLUB_CODE", club_code);
			dataSet.setString("CDHD_ID", userId);
			 
			// 04.���� ���̺�(Proc) ��ȸ
			GolfBoardListDaoProc proc = (GolfBoardListDaoProc)context.getProc("GolfClubBoardListDaoProc");
			GolfBoardMasterDaoProc bbsProc = (GolfBoardMasterDaoProc)context.getProc("GolfClubBoardMasterDaoProc");
			GolfClubMasterDaoProc clubProc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			
			DbTaoResult bbsListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult bbsMasterResult = (DbTaoResult) bbsProc.boardInfo(context, request, dataSet);
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
						
			// ��ü 0��  [ 0/0 page] ���� ��������
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
			
			if (bbsListResult != null && bbsListResult.isNext()) {
				bbsListResult.first();
				bbsListResult.next();
				if (bbsListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)bbsListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)bbsListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
			
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));
			paramMap.put("resultSize", String.valueOf(bbsListResult.size()));
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"club/"+club_code);
			paramMap.put("clubAdmChk", clubAdmChk);
			paramMap.put("clubMemChk", clubMemChk);
			
			request.setAttribute("bbsListResult", bbsListResult);
			request.setAttribute("bbsMasterResult", bbsMasterResult);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
