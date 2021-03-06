/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admGrUpdFormActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 폼
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.par;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfAdmBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.par.*;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfadmParRsViewActn extends GolfActn{
	
	public static final String TITLE = "관리자 부킹 골프장 수정 폼";
	
	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체.  
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

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

			// Request 값 저장
			String rsvt_sql_no		= parser.getParameter("RSVT_SQL_NO", "");
			String cdhd_id 			= parser.getParameter("CDHD_ID","");
			String sort 			= parser.getParameter("sort","");
			
			int int_able = 0;
			int int_done = 0;
			int int_can = 0;
			String memGrade = "";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO"	,rsvt_sql_no);
			dataSet.setString("CDHD_ID"		,cdhd_id);
			debug("PAR3  >>>>>>>>>>  CDHD_ID :"+cdhd_id);
			
			// 04.실제 테이블(Proc) 조회
			GolfadmParRsViewDaoProc proc = (GolfadmParRsViewDaoProc)context.getProc("GolfadmParRsViewDaoProc");
			DbTaoResult bkView = proc.execute(context, dataSet);
						
			// 04-1. 멤버 Grade 조회
			GolfAdmBkBenefitTimesDaoProc benefit_proc = (GolfAdmBkBenefitTimesDaoProc)context.getProc("GolfAdmBkBenefitTimesDaoProc");
			DbTaoResult bkBenefit = benefit_proc.getParBenefit(context, dataSet);
			
			if(bkBenefit.isNext()){
				bkBenefit.next();
				int_able = bkBenefit.getInt("PAR_3_YR_ABLE");
				int_done = bkBenefit.getInt("PAR_3_YR_DONE");
				memGrade = bkBenefit.getString("MEMGRADE");
			}
			
			int_can = int_able - int_done ;
			debug(">>>>>>  int_able : "+int_able+" :: int_done : "+int_done+" :: int_can : "+int_can +" :: memGrade : "+memGrade);
			
			paramMap.put("TOT_CNT", Integer.toString(int_able));
			paramMap.put("DONE_CNT", Integer.toString(int_done));
			paramMap.put("CAN_CNT", Integer.toString(int_can));
			paramMap.put("MEMGRADE", memGrade);
			
			
			// 04-2. 하단 리스트 조회
			dataSet.setLong("page_no", 1L);
			dataSet.setLong("record_size", 10L);
			dataSet.setString("SCH_GR_SEQ_NO", "");
			dataSet.setString("SCH_RSVT_YN", "");
			dataSet.setString("SCH_DATE", "");
			dataSet.setString("SCH_DATE_ST", "");
			dataSet.setString("SCH_DATE_ED", "");
			dataSet.setString("SEARCH_YN","Y");
			dataSet.setString("SCH_TEXT", cdhd_id);
			dataSet.setString("SCH_TYPE", "T1.CDHD_ID");
			dataSet.setString("SORT", sort);
			dataSet.setString("LISTTYPE", "XLS");
			
			
			GolfadmParRsListDaoProc inner_proc = (GolfadmParRsListDaoProc)context.getProc("GolfadmParRsListDaoProc");
			DbTaoResult listResult = (DbTaoResult) inner_proc.execute(context, request, dataSet);
			
			 
			// 05. Return 값 세팅		
			paramMap.put("CDHD_ID", cdhd_id); //ID
			
			request.setAttribute("BkView", bkView);	//예약상세정보
			request.setAttribute("ListResult", listResult); //하단 목록
			request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
