/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemberShipPopActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 회원권 시세표 인쇄
*   적용범위  : Golf
*   작성일자  : 2009-06-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.lounge;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfMemberShipListDaoProc;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfMemberShipPopActn extends GolfActn{
	
	public static final String TITLE = "회원권 시세표 인쇄";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String gf_nm = "";
		String price_cd = "";
		String area_cd = "";
		boolean flag1 = false;
		boolean flag2 = false;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			paramMap.remove("s_gf_hole_cd");
			paramMap.remove("s_gf_area_cd");
			
			
			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			String[] gf_price_cd		= parser.getParameterValues("s_gf_price_cd", "");  // 가격별
			String[] gf_area_cd		= parser.getParameterValues("s_gf_area_cd", "");  // 지역별
			
			for (int i = 0; i < gf_price_cd.length; i++) { 		
				if (gf_price_cd[i] != null && gf_price_cd[i].length() > 0) {
					price_cd += ","+ gf_price_cd[i];
					flag1 = true;
				}
			}
			
			//debug("flag1 ====> "+ flag1);
			
			for (int i = 0; i < gf_area_cd.length; i++) { 		
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					area_cd += ","+ gf_area_cd[i];
					flag2 = true;
				}
			}
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			
			// 04.실제 테이블(Proc) 조회
			GolfMemberShipListDaoProc proc = (GolfMemberShipListDaoProc)context.getProc("GolfMemberShipListDaoProc");
			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			DbTaoResult membershipListResult = (DbTaoResult) proc.execute(context, request, dataSet, gf_price_cd, gf_area_cd);
			
			// 코드 조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0021", "Y"); //골프장지역코드
			
			
			paramMap.put("resultSize", String.valueOf(membershipListResult.size()));
			
			request.setAttribute("membershipListResult", membershipListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			request.setAttribute("coopCpSel", coopCpSel);
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("PriceCd", (flag1 ? price_cd.substring(1,price_cd.length()) : ""));
	        request.setAttribute("AreaCd", (flag2 ? area_cd.substring(1,area_cd.length()) : ""));
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
