/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkParGrListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 골프장 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.par;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfBkParGrListActn extends GolfActn{
	
	public static final String TITLE = "관리자 부킹골프장프로그램 리스트";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		String permission = "";
		String memb_id = "";
		String isLogin = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null) {
				intMemGrade = userEtt.getIntMemGrade();
				memb_id = userEtt.getAccount();
			}

			if(memb_id != null && !"".equals(memb_id)){
				isLogin = "1";
			} else {
				isLogin = "0";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setInt("intMemGrade",		intMemGrade);
			
			/*
			// 04. 접근권한 조회	
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("PAR_3_BOKG_APO_YN");
			}else{
				permission = "N";
			}
			*/
			paramMap.put("permission", permission);

			// 04.실제 테이블(Proc) 조회
			//if(permission.equals("Y")){
				GolfBkParGrListDaoProc proc = (GolfBkParGrListDaoProc)context.getProc("GolfBkParGrListDaoProc");
				DbTaoResult grListResult = (DbTaoResult) proc.execute(context, request, dataSet);
	
				paramMap.put("resultSize", String.valueOf(grListResult.size()));
				request.setAttribute("GrListResult", grListResult);
			//}
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
