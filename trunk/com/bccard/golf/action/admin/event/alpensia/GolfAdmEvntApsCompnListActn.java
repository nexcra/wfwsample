/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntApsCompnListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 알펜시아 > 상세보기
*   적용범위  : Golf 
*   작성일자  : 2010-06-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.alpensia;

import java.io.IOException;
import java.util.Calendar;
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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.alpensia.GolfAdmEvntApsCompnListDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntApsCompnListActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 이벤트 > 알펜시아 > 상세보기";

	/***************************************************************************************
	* 골프라운지 관리자화면
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
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// 검색값		APLC_SEQ_NO
			String aplc_seq_no			= parser.getParameter("aplc_seq_no", "");	
			String golf_svc_aplc_clss	= parser.getParameter("golf_svc_aplc_clss", "");	
			

			String max_seq_no = "0";
			DbTaoResult teamResult = null;
			DbTaoResult listResult = null;
			DbTaoResult payResult = null;
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			
			
			// 04.실제 테이블(Proc) 조회 
			GolfAdmEvntApsCompnListDaoProc proc = (GolfAdmEvntApsCompnListDaoProc)context.getProc("GolfAdmEvntApsCompnListDaoProc");
			
			// 04-1. 예약 내역
			DbTaoResult listReResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// 04-2. 팀테이블
			if(!golf_svc_aplc_clss.equals("8001")){
				teamResult = (DbTaoResult) proc.execute_team(context, request, dataSet);
			}
			
			// 04-3. 동반자 테이블
			listResult = (DbTaoResult) proc.execute_list(context, request, dataSet);
			while(listResult != null && listResult.isNext()){
				listResult.next();
				max_seq_no = listResult.getString("max_seq_no");
				
			}
						
			// 04-4. 결제리스트
			payResult = (DbTaoResult) proc.execute_pay(context, request, dataSet);
			
			// 04-5. 신청금액
			String code = "";		// 공통코드
			code = "0059";
			dataSet.setString("code", code);
			DbTaoResult amtResult = (DbTaoResult) proc.execute_amt(context, request, dataSet);
			
			code = "0060";
			dataSet.setString("code", code);
			DbTaoResult optResult = (DbTaoResult) proc.execute_opt(context, request, dataSet);
			
			
						

			request.setAttribute("listReResult", listReResult);
			request.setAttribute("listResultTot", max_seq_no);
			request.setAttribute("ListResult", listResult);
			request.setAttribute("payResult", payResult);
			request.setAttribute("teamResult", teamResult);
			request.setAttribute("golf_svc_aplc_clss", golf_svc_aplc_clss);
			request.setAttribute("amtResult", amtResult);
			request.setAttribute("optResult", optResult);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
