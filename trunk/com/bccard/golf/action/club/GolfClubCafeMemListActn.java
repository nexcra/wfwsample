/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubCafeMemListActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 회원 리스트
*   적용범위  : Golf
*   작성일자  : 2009-07-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.club.GolfClubMasterDaoProc;
import com.bccard.golf.dbtao.proc.club.GolfClubMemListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfClubCafeMemListActn extends GolfActn{
	
	public static final String TITLE = "동호회 회원 리스트";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
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
			paramMap.put("title", TITLE);

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 12);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String club_code		= parser.getParameter("club_code", "");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CDHD_ID", userId);			
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("CLUB_CODE", club_code);
			dataSet.setString("JOIN_TYPE", "Y"); // 대기자만.

			paramMap.put("search_word", search_word);		
			// 04.실제 테이블(Proc) 조회
			GolfClubMemListDaoProc proc = (GolfClubMemListDaoProc)context.getProc("GolfClubMemListDaoProc");
			GolfClubMasterDaoProc clubProc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			DbTaoResult memListResult = (DbTaoResult) proc.execute(context, request, dataSet);
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
			
			// 클럽회원 인증
			String clubMemChk = clubProc.getClubMemChk(context, dataSet);

			if (!userId.equals(clubAdmId) && !clubMemChk.equals("Y")) {
				subpage_key = "errReUrl";
				request.setAttribute("returnUrl", "golfClubCafeMain.do");
				request.setAttribute("resultMsg", "동호회 회원만 열람이 가능합니다.");		
			} else {				
				// 전체 0건  [ 0/0 page] 형식 가져오기
				long totalRecord = 0L;
				long currPage = 0L;
				long totalPage = 0L;
				
				if (memListResult != null && memListResult.isNext()) {
					memListResult.first();
					memListResult.next();
					if (memListResult.getObject("RESULT").equals("00")) {
						totalRecord = Long.parseLong((String)memListResult.getString("TOTAL_CNT"));
						currPage = Long.parseLong((String)memListResult.getString("CURR_PAGE"));
						totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
					}
				}
				
				paramMap.put("totalRecord", String.valueOf(totalRecord));
				paramMap.put("currPage", String.valueOf(currPage));
				paramMap.put("totalPage", String.valueOf(totalPage));		
			}
			
			paramMap.put("resultSize", String.valueOf(memListResult.size()));			
			request.setAttribute("memListResult", memListResult);
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
