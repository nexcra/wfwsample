/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemBkScoreActn
*   작성자    : (주)미디어포스 이경희
*   내용      : 관리자 >회원관리>회원리스트>상세(부킹점수)
*   적용범위  : Golf
*   작성일자  : 2011-05-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemBkScoreDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmMemBkScoreActn extends GolfActn{
	
	public static final String TITLE = "관리자 >회원관리>회원리스트>상세(부킹점수)";

	/***************************************************************************************
	* 
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보.  
	***************************************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);		
		String topGolfCardYn 	= "N";		//탑골프카드 소지 여부
		
		boolean result = false ;
		
		try {
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);	
			
			/*
			 * top골프 카드 회원인지 체크
			 * */			
			String cdhd_ID		= parser.getParameter("CDHD_ID", "");
			String name		= parser.getParameter("NAME", "");
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);		
			dataSet.setString("CDHD_ID", cdhd_ID);			
			
			//탑골프회원인지 조회 
			GolfAdmMemBkScoreDaoProc proc = (GolfAdmMemBkScoreDaoProc)context.getProc("GolfAdmMemBkScoreDaoProc");			
			result = proc.execute(context, request, dataSet);

			HashMap	getPrveInfo = proc.getMemberInfo(context, dataSet);
			
			if(result){ 
				
				topGolfCardYn = "Y";
				
				// 02.입력값 조회		
				int defaultDate			= parser.getIntParameter("defaultDate", 5);
				paramMap.put("defaultDate", String.valueOf(defaultDate));
			      
		        GolfTopGolfCardListDaoProc proc1 = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
				
				dataSet.setString("memId", cdhd_ID);												//회원아이디 
				dataSet.setInt("memNo", Integer.parseInt(getPrveInfo.get("MEMID").toString()));		//회원고유번호 10832273
				dataSet.setString("memSocId", getPrveInfo.get("SOCID").toString());					//주민
				dataSet.setString("memberClss", getPrveInfo.get("MEMBERCLSS").toString());			//MEMBERCLSS			
				dataSet.setString("roundDate", "");
				
				DbTaoResult	getScore = (DbTaoResult) proc1.get_score(context, request, dataSet);

				request.setAttribute("topGolfCardYn", topGolfCardYn);				
				request.setAttribute("getScore", getScore);
				request.setAttribute("taoResult0", getScore);
												
			}
			
			paramMap.put("CDHD_ID", cdhd_ID);
			paramMap.put("NAME", name);
			paramMap.put("topGolfCardYn", topGolfCardYn);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}	
}
