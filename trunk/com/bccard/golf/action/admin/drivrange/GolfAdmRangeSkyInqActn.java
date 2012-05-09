/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeSkyInqActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 신청(sky72) 상세보기
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfAdmBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeSkyExcelDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeSkyInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmRangeSkyInqActn extends GolfActn{
	
	public static final String TITLE = "관리자 드림 골프레인지 신청(sky72) 상세보기";

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
			int int_able = 0;
			int int_done = 0;
			int int_can = 0;
			String memGrade = "";
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String rsvt_sql_no		= parser.getParameter("p_idx", "");
			String cdhd_id			= parser.getParameter("CDHD_ID","");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_sql_no);
			dataSet.setString("CDHD_ID", cdhd_id);
			
			debug(">>>>>>>>>>>>.    CDHD_ID:"+cdhd_id+" :: p_idx:"+rsvt_sql_no);
			// 04.실제 테이블(Proc) 조회
			GolfAdmRangeSkyInqDaoProc proc = (GolfAdmRangeSkyInqDaoProc)context.getProc("GolfAdmRangeSkyInqDaoProc");
			DbTaoResult rangeskyInq = proc.execute(context, dataSet);
			
			// 04-1. benefit 조회
			GolfAdmBkBenefitTimesDaoProc benefit_proc = (GolfAdmBkBenefitTimesDaoProc)context.getProc("GolfAdmBkBenefitTimesDaoProc");
			DbTaoResult benefit = benefit_proc.getDrivingSkyBenefit(context, dataSet);
			if(benefit.isNext()){
				benefit.next();
				int_able = benefit.getInt("DRGF_YR_ABLE");
				int_done = benefit.getInt("DRGF_YR_DONE");
				memGrade = benefit.getString("MEMGRADE");
			}
			int_can = int_able - int_done;
			
			paramMap.put("TOT_CNT", Integer.toString(int_able));
			paramMap.put("DONE_CNT", Integer.toString(int_done));
			paramMap.put("CAN_CNT", Integer.toString(int_can));
			paramMap.put("MEMGRADE", memGrade);
			
			
			
			// 04-2.하단 리스트
			dataSet.setLong("PAGE_NO", 1L);
			dataSet.setLong("RECORD_SIZE", 10L);
			dataSet.setString("START_DT", "");
			dataSet.setString("END_DT", "");
			dataSet.setString("RSVT_YN", "");
			dataSet.setString("ATD_YN", "");
			dataSet.setString("SEARCH_YN","Y");
			dataSet.setString("SEARCH_SEL", "TGU.CDHD_ID");
			dataSet.setString("SEARCH_WORD", cdhd_id);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmRangeSkyExcelDaoProc inner_proc = (GolfAdmRangeSkyExcelDaoProc)context.getProc("GolfAdmRangeSkyExcelDaoProc");
			DbTaoResult rangeskyListResult = (DbTaoResult) inner_proc.execute(context, request, dataSet);

			paramMap.put("CDHD_ID", cdhd_id);			
			request.setAttribute("rangeskyListResult", rangeskyListResult);
			request.setAttribute("rangeskyInqResult", rangeskyInq);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
