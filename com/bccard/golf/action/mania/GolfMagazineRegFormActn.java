/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMagazineRegFormActn
*   작성자    : (주)미디어포스 천선정
*   내용      : 골프잡지 구독신청폼
*   적용범위  : golf
*   작성일자  : 2009-10-30
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.mania;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.mania.GolfLimousineDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMagazineRegFormActn extends GolfActn{
	public static final String TITLE = "골프잡지 구독신청폼";

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
		String permission = "";
		int intMemGrade = 0; 
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
//			 01.세션정보체크
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
			
			//03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			debug("---------------------------------------intMemGrade() ::> " + intMemGrade);

			// 이용제한 체크
			if (isLogin.equals("1") ) { // 미가입회원 접근금지
				
				// 접근권한 조회	
				String permissionColum = "MGZ_SBSC_APO_YN";
				GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
				DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

				permissionView.next();
				if(permissionView.getString("RESULT").equals("00")){
					permission = permissionView.getString("LIMT_YN");
					
				}else{
					permission = "N";
				}
				
				
				//접근권한 체크
				if (permission.equals("N")) { // 	
					
					subpage_key = "limitReUrl";
				}
				else
				{
			
					//04.실제 테이블(Proc) 조회
					GolfLimousineDaoProc coopCpSelProc = (GolfLimousineDaoProc)context.getProc("GolfLimousineDaoProc");
					//DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0012", "Y"); //제휴업체
					DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet); //제휴업체
					
					// 05. Return 값 세팅	
					//@@@@@@@ 테이블 요청 처리완료 될때까지 주석처리-----------------@request.setAttribute("coopCpSel", coopCpSel);
			        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
			        request.setAttribute("coopCpSel", coopCpSel);
			        paramMap.put("userNm", userNm);
			        paramMap.put("memGrade", memGrade);

				} 
			
			
			
			
			}
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
