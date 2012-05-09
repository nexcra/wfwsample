/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkJoinExceltActn
*   작성자    : 인턴사원 김현봉
*   내용      : 관리자 > 이벤트 > 프리미엄 부킹 이벤트 > 신청 관리 > 엑셀
*   적용범위  : Golf
*   작성일자  : 2009-09-03 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBkJoinExcelActn extends GolfActn{
		
	public static final String TITLE = "관리자 프리미엄 부킹 이벤트 신청 관리 엑셀 저장";

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

		String actionKey = super.getActionKey(context);
		debug("actionKey=========>"+actionKey);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.remove("chkEvent_yn");

			// Request 값 저장
			long page_no			= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size		= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String sgr_nm			= parser.getParameter("sgr_nm", "");
			String sprize_yn		= parser.getParameter("sprize_yn", "");
			String sbkps_sdate		= parser.getParameter("sbkps_sdate", "");
			String sbkps_edate		= parser.getParameter("sbkps_edate", "");
			String sevnt_from		= parser.getParameter("sevnt_from", "");
			String sevnt_to			= parser.getParameter("sevnt_to", "");

			String sort			= parser.getParameter("SORT", "0001"); //0001:프리미엄 0002:파3부킹
			
			sbkps_sdate = sbkps_sdate.length() == 10 ? DateUtil.format(sbkps_sdate, "yyyy-MM-dd", "yyyyMMdd"): "";
			sbkps_edate = sbkps_edate.length() == 10 ? DateUtil.format(sbkps_edate, "yyyy-MM-dd", "yyyyMMdd"): "";
			sevnt_from = sevnt_from.length() == 10 ? DateUtil.format(sevnt_from, "yyyy-MM-dd", "yyyyMMdd"): "";
			sevnt_to = sevnt_to.length() == 10 ? DateUtil.format(sevnt_to, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SGR_NM", sgr_nm);
			dataSet.setString("SPRIZE_YN", sprize_yn);
			dataSet.setString("SBKPS_SDATE", sbkps_sdate);
			dataSet.setString("SBKPS_EDATE", sbkps_edate);
			dataSet.setString("SEVNT_FROM", sevnt_from);
			dataSet.setString("SEVNT_TO", sevnt_to);
			dataSet.setString("SORT", sort);
			
			
			// 04.실제 테이블(Proc) 조회														
			//GolfAdmEvntBkJoinExcelDaoProc proc = (GolfAdmEvntBkJoinExcelDaoProc)context.getProc("GolfAdmEvntBkJoinExcelDaoProc");
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");

			DbTaoResult evntBkJoinListResult	= null;
			DbTaoResult titimeGreenListResult	= null;

			
			if (actionKey.equals("admEvntBkJoin9Excel"))
			{					  

				dataSet.setString("LESN_SEQ_NO","21");
				//evntBkJoinListResult = (DbTaoResult) proc.execute2(context, request, dataSet);

			} else {
				//evntBkJoinListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				titimeGreenListResult = (DbTaoResult) proc2.execute(context, request, dataSet);

			}
			paramMap.put("resultSize", String.valueOf(evntBkJoinListResult.size()));

			request.setAttribute("evntBkJoinListResult", evntBkJoinListResult);
			request.setAttribute("titimeGreenListResult", titimeGreenListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
