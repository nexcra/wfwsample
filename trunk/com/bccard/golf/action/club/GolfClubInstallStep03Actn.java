/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardRegFormActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 답변 폼
*   적용범위  : golf
*   작성일자  : 2009-05-28
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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.club.GolfClubMasterDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfClubInstallStep03Actn extends GolfActn{
	
	public static final String TITLE = "공통게시판 등록 폼";

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
			
			//	02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);

			String golf_club_ctgo = parser.getParameter("golf_club_ctgo", "");
			String club_intd_ctnt = GolfUtil.getUrl(parser.getParameter("club_intd_ctnt", ""));
			String club_nm = GolfUtil.getUrl(parser.getParameter("club_nm", ""));
			String club_sbjt_ctnt = GolfUtil.getUrl(parser.getParameter("club_sbjt_ctnt", ""));
			String club_opn_prps_ctnt = GolfUtil.getUrl(parser.getParameter("club_opn_prps_ctnt", ""));
			String cdhd_num_limt_yn = parser.getParameter("cdhd_num_limt_yn", "");
			String club_jonn_mthd_clss = parser.getParameter("club_jonn_mthd_clss", "");
			String limt_cdhd_num = parser.getParameter("limt_cdhd_num", "");
			String club_img = parser.getParameter("club_img", "");
			
			String cdhd_num_limt_yn_nm ="";
			if (cdhd_num_limt_yn.equals("Y")) cdhd_num_limt_yn_nm="회원제한";
			if (cdhd_num_limt_yn.equals("N")) cdhd_num_limt_yn_nm="무제한";
			String club_jonn_mthd_clss_nm = "";
			if (club_jonn_mthd_clss.equals("A")) club_jonn_mthd_clss_nm="승인가입";
			if (club_jonn_mthd_clss.equals("R")) club_jonn_mthd_clss_nm="즉시가입";
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GOLF_CLUB_CTGO", golf_club_ctgo);
			
			// 04.실제 테이블(Proc) 조회
			GolfClubMasterDaoProc proc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			DbTaoResult clubCateSel = proc.getClubCateMemCnt(context, dataSet); //동호회 카테고리
			
			// 선택한 카테고리명
			String golf_club_ctgo_nm = proc.getClubCateNm(context, dataSet);
			paramMap.put("club_nm", club_nm);								//동호회명
			paramMap.put("club_sbjt_ctnt", club_sbjt_ctnt);					//동호회주제
			paramMap.put("golf_club_ctgo_nm", golf_club_ctgo_nm);		
			paramMap.put("club_intd_ctnt_chg", club_intd_ctnt);				//동호회소개
			paramMap.put("club_opn_prps_ctnt_chg", club_opn_prps_ctnt);		//동호회취지
			paramMap.put("cdhd_num_limt_yn_nm", cdhd_num_limt_yn_nm);		
			paramMap.put("club_jonn_mthd_clss_nm", club_jonn_mthd_clss_nm);
			paramMap.put("golf_club_ctgo", golf_club_ctgo);
			paramMap.put("cdhd_num_limt_yn", cdhd_num_limt_yn);				//회원수제한
			paramMap.put("club_jonn_mthd_clss", club_jonn_mthd_clss);		//가입설정방법
			paramMap.put("limt_cdhd_num", limt_cdhd_num);					//회원수
			paramMap.put("club_img", club_img);								//이미지

			request.setAttribute("clubCateSel", clubCateSel);	
	        request.setAttribute("paramMap", paramMap);		        
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
