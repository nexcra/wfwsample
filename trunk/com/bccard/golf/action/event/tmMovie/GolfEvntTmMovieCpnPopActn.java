/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntTmMovieCpnPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > TM 영화 예매권 이벤트 > 쿠폰번호 출력 팝업
*   적용범위  : Golf
*   작성일자  : 2010-03-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.tmMovie;

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
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.GolfBkPreTimeResultDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkWinListDaoProc;
import com.bccard.golf.dbtao.proc.event.tmMovie.GolfEvntTmMovieProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	JSEUN
* @version	1.0
******************************************************************************/
public class GolfEvntTmMovieCpnPopActn extends GolfActn{
	
	public static final String TITLE = "이벤트 > TM 영화 예매권 이벤트 > 쿠폰번호 출력 팝업";

	/***************************************************************************************
	* 골프 사용자화면
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
		
		String userSocid = "";
		String userNm = "";
		String userEmail = "";

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			 if(usrEntity != null) {
				 userSocid 	= (String)usrEntity.getSocid();
				 userNm 	= (String)usrEntity.getName();
				 userEmail 	= (String)usrEntity.getEmail1();
			 }
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("userSocid", userSocid);
			dataSet.setString("tm_evt_no", "119");
			dataSet.setString("email"	, userEmail);
			dataSet.setString("socid"	, userSocid);
			dataSet.setString("userNm"	, userNm);
			
			// 04.실제 테이블(Proc) 조회
			GolfEvntTmMovieProc proc_tmMovie = (GolfEvntTmMovieProc)context.getProc("GolfEvntTmMovieProc");

			// 1) 영화예매권 지급여부 확인 - 1건 이상일경우
			int useEvtCpnCnt = (int) proc_tmMovie.useEvtCpnCnt(context, request, dataSet);	
			debug("GolfmemInsActn:::useEvtCpnCnt : " + useEvtCpnCnt );		
			
			if(useEvtCpnCnt==0){
				
				// 2) 할인쿠폰 다운받기 로직 처리
				synchronized(this) {	// 동시 유저 발생시 같은 max 값 얻어오는걸 방지
					int cupnTmMovie = (int) proc_tmMovie.cupnNumber(context, request, dataSet);
				}
			}

			// 리스트
			DbTaoResult cpnList = proc_tmMovie.cpnList(context, request, dataSet);
			request.setAttribute("cpnList", cpnList);				
			
			// 05. Return 값 세팅
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
