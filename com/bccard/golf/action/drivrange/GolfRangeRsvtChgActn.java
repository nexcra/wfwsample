/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRangeRsvtChgActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : SKY72드림골프레인지 예약취소 처리
*   적용범위  : golf
*   작성일자  : 2009-06-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.drivrange;

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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmCyberBenefitDetailInqDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfRangeRsvtUpdDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfRangeRsvtChgActn extends GolfActn{
	
	public static final String TITLE = "SKY72드림골프레인지 예약취소 처리";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
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
		int intCyberMoney = 0; 
		
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
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //사이버머니
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
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);

			String rsvt_sql_no	= parser.getParameter("p_idx", "");// 예약번호
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_sql_no);
			dataSet.setString("GF_ID", userId);
			
			//debug("GF_ID =====> "+ userId);
			
			/* 9월 이벤트 때문에 막음
			// 사이버머니 정책 가져오기
			GolfAdmCyberBenefitDetailInqDaoProc proc1 = (GolfAdmCyberBenefitDetailInqDaoProc)context.getProc("GolfAdmCyberBenefitDetailInqDaoProc");
			
			DbTaoResult detailInq1 = (DbTaoResult) proc1.execute(context, request, dataSet);
			
			String drvr_amt = "";
			if (detailInq1 != null && detailInq1.isNext()) {
				detailInq1.first();
				detailInq1.next();
				if (detailInq1.getObject("RESULT").equals("00")) {
					drvr_amt = (String)detailInq1.getString("DV_RG");
				}
			}
			 
			
			dataSet.setString("DRVR_AMT", drvr_amt);
			9월 이벤트 때문에 막음 */
			
			// 이용제한 체크
			//if (isLogin.equals("1") && (intMemGrade < 3 || intCyberMoney > 0)) { // 골드회원이상 및 사이버머니 1원 이상 

			if (isLogin.equals("1")) { //9월이벤트 때문에 풀어놓음
				
				// 04.실제 테이블(Proc) 조회
				GolfRangeRsvtUpdDaoProc proc = (GolfRangeRsvtUpdDaoProc)context.getProc("GolfRangeRsvtUpdDaoProc");
				
				//  프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				int editResult = proc.execute(context, request, dataSet);			
				
		        if (editResult == 1) {
					request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "예약취소가 완료 되었습니다.");      	
		        } else {
					request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "예약취소가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
		        }
				
				// 05. Return 값 세팅			
				paramMap.put("editResult", String.valueOf(editResult));			
		        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
			
			} else {
				subpage_key = "limitReUrl";
			}
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
