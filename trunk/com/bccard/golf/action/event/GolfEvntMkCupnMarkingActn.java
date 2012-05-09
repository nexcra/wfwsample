/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntMkCupnmarkingActn
*   작성자    : (주)미디어포스 이정규
*   내용      : 사용 쿠폰 마킹 처리
*   적용범위  : Golf
*   작성일자  : 2010-09-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntMkCupnMarkingActn
*   작성자    : 이정규
*   내용      : 쿠폰마킹 처리
*   적용범위  : golf
*   작성일자  : 2010-09-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardComtInsDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntMkMemberProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfEvntMkCupnMarkingActn extends GolfActn{
	
	public static final String TITLE = "사용 쿠폰 마킹 처리";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		request.setAttribute("layout", layout);
		String user_id ="";
		String jumin_no ="";

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				user_id		= (String)usrEntity.getAccount(); 
				jumin_no		= (String)usrEntity.getSocid(); 
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			
			String strResultCode = "";
			
			//가맹점 번호 
			String mer_no = parser.getParameter("card_no", "");
			String cupn_no = parser.getParameter("cupn_no", "");
			String seq_no = parser.getParameter("seq_no", "");
			debug("cupn_no = "+cupn_no);
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("MER_NO", mer_no);
			dataSet.setString("CUPN_NO", cupn_no);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("JUMIN_NO", jumin_no);
			
			GolfEvntMkMemberProc proc = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
			
			/*/04 출력 쿠폰 번호 가져와서
			DbTaoResult couponNum = (DbTaoResult) proc.getCouponNum(context, request, dataSet);	//쿠폰번호 가져오기
			
			if (couponNum != null && couponNum.isNext()) {
				couponNum.first(); 
				couponNum.next();
				if(couponNum.getString("RESULT").equals("00")){
					cupn_no = couponNum.getString("CUPN_NO");
					dataSet.setString("CUPN_NO", cupn_no);
				}
			}*/
			
			//05  인쇄 횟수 증가
			int updateMarking = proc.updatePrtHit(context, mer_no, cupn_no);
			
			if(updateMarking>0)
			{				
				strResultCode = "Y";
			}else{
				strResultCode = "N";
			}
			
			// 04.실제 테이블(Proc) 조회
			DbTaoResult evntMkMemberAppDetail = proc.getMkMemberAppDetail(context, dataSet);
			DbTaoResult evntMkPrcGroundDetail = proc.evntMkPrcGroundDetail(context, dataSet);
			DbTaoResult getMkMember = proc.getMkMember(context, request, dataSet);		//골프 연습장 정보
			
			
			// 05. Return 값 세팅	 		
			//debug("lessonInq.size() ::> " + lessonInq.size());			
			request.setAttribute("evntMkMemberAppDetail", evntMkMemberAppDetail);
			request.setAttribute("evntMkPrcGroundDetail", evntMkPrcGroundDetail);
			request.setAttribute("getMkMember", getMkMember);
			
			
			// 06. Return 값 세팅			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
	        request.setAttribute("strResultCode", strResultCode);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}

