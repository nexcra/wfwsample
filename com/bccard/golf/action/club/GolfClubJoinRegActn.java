/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubJoinRegActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 > 동호회 가입 처리
*   적용범위  : golf
*   작성일자  : 2009-07-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.club;

import java.io.File;
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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.club.GolfClubJoinInsDaoProc;
import com.bccard.golf.dbtao.proc.club.GolfClubMasterDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfClubJoinRegActn extends GolfActn{
	
	public static final String TITLE = "동호회 가입 처리";

	/***************************************************************************************
	* 골프 사용자화면
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
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
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
			paramMap.put("userNm", userNm);
			paramMap.put("userId", userId);

			String club_code = parser.getParameter("club_code", "");
			String greet_ctnt = parser.getParameter("greet_ctnt", "");

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CLUB_SEQ_NO", club_code);
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("CDHD_NM", userNm);
			dataSet.setString("GREET_CTNT", greet_ctnt);
			dataSet.setString("CLUB_CODE", club_code);
			
			// 04.실제 테이블(Proc) 조회
			GolfClubMasterDaoProc clubProc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			DbTaoResult clubMasterResult = (DbTaoResult) clubProc.execute(context, dataSet);
			
			// 클럽회원 인증
			String clubMemChk = clubProc.getClubMemChk(context, dataSet);
			int addResult = 0 ;
			if (clubMemChk.equals("Y")) {
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "이미 가입된 동호회 입니다.");		
			} else {	
			
				// 동호회 가입방법 코드
				String club_jonn_mthd_clss = "";
				String cdhd_num_limt_yn = ""; // 회원제한여부
				int limt_cdhd_num = 0; // 제한회원수
				int mem_cnt = 0; //현재회원
				if (clubMasterResult != null && clubMasterResult.isNext()) {
					clubMasterResult.first();
					clubMasterResult.next();
					if (clubMasterResult.getObject("RESULT").equals("00")) {
						club_jonn_mthd_clss = (String)clubMasterResult.getString("CLUB_JONN_MTHD_CLSS");
						cdhd_num_limt_yn = (String)clubMasterResult.getString("CDHD_NUM_LIMT_YN");
						if (cdhd_num_limt_yn.equals("Y")) {
							limt_cdhd_num = Integer.parseInt(clubMasterResult.getString("LIMT_CDHD_NUM"));
						}

						mem_cnt = Integer.parseInt(clubMasterResult.getString("MEM_CNT"));
					}
				}
				//
				boolean joinChk = true;
				if (cdhd_num_limt_yn.equals("Y")) {
					if (mem_cnt >= limt_cdhd_num) joinChk = false;
				}
				
				if (joinChk) {
					// 동호회 가입 체크
					if (club_jonn_mthd_clss.equals("R")) { //즉시가입
						dataSet.setString("JONN_YN", "Y");				
					} else { //승인가입
						dataSet.setString("JONN_YN", "W");				
					}
				}
					
				GolfClubJoinInsDaoProc proc = (GolfClubJoinInsDaoProc)context.getProc("GolfClubJoinInsDaoProc");
				addResult = proc.execute(context, dataSet);			

		        if (addResult == 1) {
					request.setAttribute("returnUrl", reUrl);
					request.setAttribute("resultMsg", "");      	
		        } else {
					request.setAttribute("returnUrl", errReUrl);
					request.setAttribute("resultMsg", "동호회 가입이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
		        }
			}
			
			
			// 05. Return 값 세팅
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
