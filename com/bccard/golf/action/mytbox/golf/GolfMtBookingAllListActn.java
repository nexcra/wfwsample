/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkCheckAllListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 > 최근 부킹 확인
*   적용범위  : Golf
*   작성일자  : 2009-05-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.golf;

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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.check.*;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMtBookingAllListActn extends GolfActn{
	
	public static final String TITLE = "최근 부킹 확인";

	/***************************************************************************************
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
			dataSet.setString("LISTTYPE", "ALL");


			// 04.실제 테이블(Proc) 조회 - 나의 부킹 전체 정보 
			GolfBkCheckAllViewDaoProc proc = (GolfBkCheckAllViewDaoProc)context.getProc("GolfBkCheckAllViewDaoProc");
			DbTaoResult allView = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("AllView", allView);
			
			// 04.실제 테이블(Proc) 조회 - 프리미엄부킹
			GolfBkCheckPreListDaoProc proc1 = (GolfBkCheckPreListDaoProc)context.getProc("GolfBkCheckPreListDaoProc");
			DbTaoResult preList = (DbTaoResult) proc1.execute(context, request, dataSet);
			request.setAttribute("PreList", preList);
			
			// 04.실제 테이블(Proc) 조회 - 파3부킹
			GolfBkCheckParListDaoProc proc2 = (GolfBkCheckParListDaoProc)context.getProc("GolfBkCheckParListDaoProc");
			DbTaoResult parList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("ParList", parList);
			
			// 04.실제 테이블(Proc) 조회 - 제주그린피할인
			GolfBkCheckJjListDaoProc proc3 = (GolfBkCheckJjListDaoProc)context.getProc("GolfBkCheckJjListDaoProc");
			DbTaoResult jjList = (DbTaoResult) proc3.execute(context, request, dataSet);
			request.setAttribute("JjList", jjList);

			// 04.실제 테이블(Proc) 조회 - 스카이 72
			GolfBkCheckSkyListDaoProc proc4 = (GolfBkCheckSkyListDaoProc)context.getProc("GolfBkCheckSkyListDaoProc");
			DbTaoResult skyList = (DbTaoResult) proc4.execute(context, request, dataSet);
			request.setAttribute("SkyList", skyList);
							
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
