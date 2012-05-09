/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntMkMemberListActn
*   작성자    : 이정규
*   내용      : BC Golf 신청 테이블에서 확인
*   적용범위  : Golf
*   작성일자  : 2010-08-31
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntBcListDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntMkMemberProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

public class GolfEvntMkMemberListActn extends GolfActn{
	
	public static final String TITLE = "BC Golf 마케팅 대상 연습장 골프";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			 
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 04.실제 테이블(Proc) 조회 
			// 04-01 마케팅 회원정보 가져오기
			// session 에서 주민등록 번호 가져오기
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			String juminno = ""; 
			if(usrEntity != null) {
				juminno 	= (String)usrEntity.getSocid(); 
			}
			dataSet.setString("JUMIN_NO", juminno);
			
			
			GolfEvntMkMemberProc proc = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
			//고객정보
			DbTaoResult evntMkMemberResult = (DbTaoResult) proc.getMkMember(context, request, dataSet);
			String card_no = "";
			
			if (evntMkMemberResult != null && evntMkMemberResult.isNext()) {
				evntMkMemberResult.next();
				if(evntMkMemberResult.getString("RESULT").equals("00")){
					card_no = evntMkMemberResult.getString("MER_NO");
					dataSet.setString("CARD_NO", card_no);	//가맹점 번호
				}
			}
			//고객 쿠폰 발급 리스트 정보
			DbTaoResult evntMkMemberAppListResult = (DbTaoResult) proc.getMkMemberAppList(context, request, dataSet);
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("evntMkMemberResult", evntMkMemberResult);	//고객정보
			request.setAttribute("evntMkMemberAppListResult", evntMkMemberAppListResult);	//고객 쿠폰 발급 리스트 정보
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
