/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBnstListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 가평베네스트 > 구매 리스트(수정사항)
*   적용범위  : Golf
*   작성일자  : 2010-03-23
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.benest;

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
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntBnstCompnListDaoProc;
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntMngListDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBnstCompnListActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 이벤트 > 월례회 > 동반자리스트";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
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
			String trm_unt			= parser.getParameter("trm_unt", "");
			String type			= parser.getParameter("type", "");
			
			
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			dataSet.setString("trm_unt", trm_unt);
			
			
			// 04.실제 테이블(Proc) 조회 
			GolfAdmEvntBnstCompnListDaoProc proc = (GolfAdmEvntBnstCompnListDaoProc)context.getProc("GolfAdmEvntBnstCompnListDaoProc");
			// 04-1. 예약 테이블
			DbTaoResult listReResult = (DbTaoResult) proc.execute_list(context, request, dataSet);

			//월례회에서 가격정보 가져오기
			GolfAdmEvntMngListDaoProc proc1 = (GolfAdmEvntMngListDaoProc)context.getProc("GolfAdmEvntMngListDaoProc");
			
			// 04-1. 월례회 상세보기
			DbTaoResult viewResult = (DbTaoResult) proc1.get_cost(context, request, dataSet);
			
			
			
			// 04-2. 동반자 테이블
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			String max_seq_no = "";
			while(listResult != null && listResult.isNext()){
				listResult.next();
				max_seq_no = listResult.getString("max_seq_no");
			}
			debug("max_seq_no : " + max_seq_no);
						
			// 04-3. 결제리스트
			DbTaoResult payResult = (DbTaoResult) proc.execute_pay(context, request, dataSet);
			paramMap.put("trm_unt", trm_unt);	
			

			request.setAttribute("listReResult", listReResult);
			request.setAttribute("listResultTot", max_seq_no);
			request.setAttribute("ListResult", listResult);
			request.setAttribute("viewResult", viewResult);		//월례회에서 가격정보
			request.setAttribute("payResult", payResult);
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("type", type);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
