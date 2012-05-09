/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmSpecialBookingCnclUpdActn
*   작성자    : 이포넷 은장선
*   내용      : 관리자 > 이벤트->VIP부킹이벤트->명문골프장부킹 
*   적용범위  : Golf
*   작성일자  : 2009-09-17
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
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSpecialBookingDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;

/******************************************************************************
* Golf
* @author	이포넷 은장선
* @version	1.0
******************************************************************************/
public class GolfAdmSpecialBookingCnclUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 프리미엄 부킹 이벤트 당첨자 리스트";

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

		try {
			// 01.세션정보체크
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request 값 저장
			long page_no			= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size		= parser.getLongParameter("record_size", 10);		// 페이지당출력수		
			String green_nm				= parser.getParameter("green_nm","");           //신청골프장명
			String golf_cmmn_code		= parser.getParameter("golf_cmmn_code","");     //예약코드
			String grade				= parser.getParameter("grade","");              //사용자등급 한글명
			String sch_yn               = parser.getParameter("sch_yn","");             
			String sch_reg_aton_st		= parser.getParameter("sch_reg_aton_st","");    //조회 신청 시작일
			String sch_reg_aton_ed		= parser.getParameter("sch_reg_aton_ed","");    //조회 신청 종료일
			String sch_pu_date_st		= parser.getParameter("sch_pu_date_st","");     //조회 부킹 시작일
			String sch_pu_date_ed		= parser.getParameter("sch_pu_date_ed","");     //조회 부킹 종료일
			String sch_type				= parser.getParameter("sch_type","");           //이름,ID조회 여부
			String search_word			= parser.getParameter("search_word","");        //조회 명
			String aplc_seq_no			= parser.getParameter("aplc_seq_no","");        //예약번호

			String sch_chng_aton_st		= parser.getParameter("sch_chng_aton_st","");   //조회 취소일자 시작일
			String sch_chng_aton_ed		= parser.getParameter("sch_chng_aton_ed","");   //조회 취소일자 종료일
			String doyn			        = parser.getParameter("doyn","");               //처리여부
			String cslt_yn              = parser.getParameter("cslt_yn","");            //처리코드값
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("golf_cmmn_code",golf_cmmn_code);
			dataSet.setString("grade",grade);
			dataSet.setString("sch_yn",sch_yn);
			dataSet.setString("sch_reg_aton_st",sch_reg_aton_st);
			dataSet.setString("sch_reg_aton_ed",sch_reg_aton_ed);
			dataSet.setString("sch_pu_date_st",sch_pu_date_st);
			dataSet.setString("sch_pu_date_ed",sch_pu_date_ed);
			dataSet.setString("sch_type",sch_type);
			dataSet.setString("search_word",search_word);
			dataSet.setString("actnKey","admSpecialBookingCanList");
			dataSet.setString("sch_chng_aton_st",sch_chng_aton_st);
			dataSet.setString("sch_chng_aton_ed",sch_chng_aton_ed);
			dataSet.setString("doyn",doyn);
			dataSet.setString("aplc_seq_no",aplc_seq_no);
			dataSet.setString("cslt_yn",cslt_yn);

			
			// 04.실제 테이블(Proc) 조회
			GolfAdmSpecialBookingDaoProc proc = (GolfAdmSpecialBookingDaoProc)context.getProc("GolfAdmSpecialBookingDaoProc");
			GolfEvntBkMMDaoProc proc2 = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");

			DbTaoResult evntCncl    = proc.setFinalCncl(context, request, dataSet);

			if(evntCncl.isNext()){
				evntCncl.next();
				if(evntCncl.getString("RESULT").equals("00")){
					String msg = "정상적으로 처리 되었습니다.";
					paramMap.put("msg",msg);
				}				
			}
		
			DbTaoResult evntMMListResult = (DbTaoResult) proc.execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(evntMMListResult.size()));
			paramMap.put("page_no",String.valueOf(page_no));

			
			paramMap.put("green_nm",green_nm);			
			paramMap.put("golf_cmmn_code",golf_cmmn_code);
			paramMap.put("grade",grade	);		
			paramMap.put("sch_reg_aton_st",sch_reg_aton_st);	
			paramMap.put("sch_reg_aton_ed",	sch_reg_aton_ed);	
			paramMap.put("sch_pu_date_st",	sch_pu_date_st);	
			paramMap.put("sch_pu_date_ed",sch_pu_date_ed);	
			paramMap.put("sch_type"	,	sch_type	);	
			paramMap.put("search_word",		search_word);	
			paramMap.put("total_cnt",String.valueOf(evntMMListResult.size()));
			paramMap.put("sch_chng_aton_st",sch_chng_aton_st);	
			paramMap.put("sch_chng_aton_ed",sch_chng_aton_ed);	
			paramMap.put("doyn",doyn);	
		
			request.setAttribute("evntMMListResult", evntMMListResult);	
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
