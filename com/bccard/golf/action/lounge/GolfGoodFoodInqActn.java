/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfGoodFoodInqActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      :  골프장 주변맛집 상세보기
*   적용범위  : golf
*   작성일자  : 2009-06-10
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfGoodFoodInqActn extends GolfActn{
	
	public static final String TITLE = "골프장 주변맛집 상세보기";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
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
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("intMemGrade", String.valueOf(intMemGrade));

			// Request 값 저장
			long fd_seq_no	= parser.getLongParameter("p_idx", 0L);
			String reply_clss		= parser.getParameter("reply_clss", "0003");	
			String comt_papeing		= parser.getParameter("comt_papeing", "Y");	
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("FD_SEQ_NO", fd_seq_no);
			dataSet.setString("SEQ_NO",  String.valueOf(fd_seq_no));
			dataSet.setString("REPLY_CLSS", reply_clss);
			
			// 이용제한 체크
			//if (isLogin.equals("1") && intMemGrade < 4) { // 우량회원이상 접근
				
				// 04.실제 테이블(Proc) 조회
				GolfGoodFoodInqDaoProc proc = (GolfGoodFoodInqDaoProc)context.getProc("GolfGoodFoodInqDaoProc");
				GolfBoardComtListDaoProc proc2 = (GolfBoardComtListDaoProc)context.getProc("GolfBoardComtListDaoProc");
				GolfGoodFoodSelDaoProc coopGfSelproc = (GolfGoodFoodSelDaoProc)context.getProc("GolfGoodFoodSelDaoProc");
				
				// 프로그램 상세조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				resultMap = proc.execute(context, resultMap, dataSet);
				DbTaoResult coopGfSel = coopGfSelproc.execute(context, dataSet);
				
				DbTaoResult bbsComtListResult = null;
				if (comt_papeing.equals("Y")) {
					bbsComtListResult = (DbTaoResult) proc2.execute(context, request, dataSet);			
				} else {
					bbsComtListResult = (DbTaoResult) proc2.execute_noPageing(context, request, dataSet);
				}
				
				//	데이터가 있으면 이전/다음 실행
				DbTaoResult preNextInfoResult = null;
				if (resultMap.get("RESULT").equals("00")) {
					
					// 이전글 다음글 번호 가져오기
					preNextInfoResult = proc.getPreNextInfo(context, dataSet);
				}
				
				// 05. Return 값 세팅			
				//debug("lessonInq.size() ::> " + lessonInq.size());
				paramMap.put("bbsReListSize", String.valueOf(bbsComtListResult.size()));
				
				request.setAttribute("goodfoodInqResult",resultMap);	
				request.setAttribute("bbsComtListResult", bbsComtListResult);
				request.setAttribute("coopGfSel", coopGfSel);			
				request.setAttribute("preNextInfoResult", preNextInfoResult);
			    request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
			    
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
