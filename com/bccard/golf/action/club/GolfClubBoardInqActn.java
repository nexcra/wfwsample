/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardInqActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 수정 상세보기
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfClubBoardInqActn extends GolfActn{
	
	public static final String TITLE = "공통게시판 상세보기";

	/***************************************************************************************
	* Golf 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
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
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		try {
			// 01.세션정보체크
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
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			HashMap resultMap = new HashMap();
			paramMap.put("title", TITLE);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("intMemGrade", String.valueOf(intMemGrade));

			// Request 값 저장
			String seq_no	= parser.getParameter("p_idx", "");
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String club_code		= parser.getParameter("club_code", "");
			String bbs_code		= parser.getParameter("bbs_code", "");
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SCH_CLUB_SEQ_NO", club_code);
			dataSet.setString("SCH_BBRD_SEQ_NO", bbs_code);
			dataSet.setString("CLUB_CODE", club_code);
			dataSet.setString("CDHD_ID", userId);
			
			// 04.실제 테이블(Proc) 조회
			GolfClubBoardInqDaoProc proc = (GolfClubBoardInqDaoProc)context.getProc("GolfClubBoardInqDaoProc");
			GolfClubBoardComtListDaoProc proc2 = (GolfClubBoardComtListDaoProc)context.getProc("GolfClubBoardComtListDaoProc");
			GolfClubMasterDaoProc clubProc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			
			resultMap = proc.execute(context, resultMap, dataSet);			
			DbTaoResult bbsComtListResult = (DbTaoResult) proc2.execute(context, request, dataSet);		
			DbTaoResult clubMasterResult = (DbTaoResult) clubProc.execute(context, dataSet);	

			// 운영자 로그인 정보..
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
			
			// 클럽회원 인증
			String clubMemChk = clubProc.getClubMemChk(context, dataSet);
			
			// 데이터가 있으면 이전/다음/조회수 업데이트 실행
			DbTaoResult preNextInfoResult = null;
			if (resultMap.get("RESULT").equals("00")) {
				
				// 이전글 다음글 번호 가져오기
				preNextInfoResult = proc.getPreNextInfo(context, dataSet);
				
				// 조회수 업데이트
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
			
			// 05. Return 값 세팅
			request.setAttribute("bbsInqResult", resultMap);
			request.setAttribute("bbsComtListResult", bbsComtListResult);
			request.setAttribute("preNextInfoResult", preNextInfoResult);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
