/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : AdmGolfTopPointLstInqActn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 포인트 관리  > 포인트관리 리스트 조회
*   적용범위  : Golf  
*   작성일자  : 2010-12-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;
   

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
import com.bccard.waf.common.DateUtil;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/******************************************************************************
* Topn
* @author	 
* @version	1.0  
******************************************************************************/

public class AdmGolfTopPointLstInqActn extends GolfActn {
	
	public static final String TITLE = "관리자 > 부킹 > 포인트관리  > 포인트관리 리스트 조회";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	*********************************** ****************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		debug("***********************************************************************************");
		debug("*************                                                        **************");
		debug("action GolfadmTopPenaltyLstActn.java execute");
		debug("*************                                                        **************");
		debug("***********************************************************************************");
		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String actnKey = getActionKey(context);

		request.setAttribute("layout", layout);
		
		try {
					// 01.세션정보체크
			
					debug("action AdmGolfTopPointLstInqActn.java try");
					// 02.입력값 조회		
					RequestParser parser = context.getRequestParser(subpage_key, request, response);
					Map paramMap = BaseAction.getParamToMap(request);
					paramMap.put("title", TITLE);
		
					// 날짜 초기값 설정
					String nowYear      =   DateUtil.currdate("yyyy");             //시스템 날짜(year)
					String nowMonth     =   DateUtil.currdate("MM");               //시스템 날짜 (month)
					int term            =   DateUtil.getMonthlyDayCount(Integer.parseInt(nowYear),Integer.parseInt(nowMonth));       // 말일
					String dateFromFmt  =   nowYear + "." + nowMonth + "." + "01";                  //기본 시작일
					String dateToFmt    =   nowYear + "." + nowMonth + "." + String.valueOf(term);  //기본 종료일
					String dateFrom 	=   DateUtil.format(dateFromFmt,"yyyy.MM.dd","yyyyMMdd");   //기본 시작일 포멧
					String dateTo       =   DateUtil.format(dateToFmt,"yyyy.MM.dd","yyyyMMdd");     //기본 종료일 포멧	
		
					String roundDateFrom = parser.getParameter("roundDateFrom",dateFrom);
					String roundDateFromFmt = parser.getParameter("roundDateFromFmt",dateFromFmt);
					String roundDateTo = parser.getParameter("roundDateTo",dateTo);
					String roundDateToFmt = parser.getParameter("roundDateToFmt",dateToFmt);
					String pointDetlCd = parser.getParameter("pointDetlCd", null);
					long pageNo = parser.getLongParameter("pageNo",1L);
					long recordsInPage = 10L;//parser.getLongParameter("recordsInPage",10L);

					// Request 값 저장


					// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
					dataSet.setString("roundDateFrom",roundDateFrom);
					dataSet.setString("roundDateTo",roundDateTo);
					dataSet.setString("pointDetlCd",pointDetlCd);
					dataSet.setLong("pageNo",pageNo);
					dataSet.setLong("recordsInPage",recordsInPage);
					// 04.실제 테이블(Proc) 조회
					debug("before action AdmGolfTopPointLstInqActn.java con xecute before");			
	  	//  TaoResult boardInqLst = con.execute("AdmGolfTopPointLstInqProc", dataSet);
					AdmGolfTopPointLstInqProc proc = (AdmGolfTopPointLstInqProc)context.getProc("AdmGolfTopPointLstInqProc");
					debug("after action AdmGolfTopPointLstInqActn.java con xecute after");	

					paramMap.put("roundDateFrom", roundDateFrom);
					paramMap.put("roundDateFromFmt", roundDateFromFmt);
					paramMap.put("roundDateTo", roundDateTo);
					paramMap.put("roundDateToFmt", roundDateToFmt);
					paramMap.put("pointDetlCd", pointDetlCd);
					paramMap.put("pageNo", String.valueOf(pageNo));
					paramMap.put("recordsInPage", String.valueOf(recordsInPage));
					debug("laction pointDetlCd"+ pointDetlCd);
					debug("laction pageNo"+ pageNo);
					debug("laction recordsInPage"+ recordsInPage);
					DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
					if(listResult.isNext()){
							listResult.next();
							if("00".equals(listResult.getString("result"))){
									paramMap.put("recordCnt", String.valueOf(listResult.getLong("recordCnt")));
							}else{
								paramMap.put("recordCnt", "0");
							}
					}

					request.setAttribute("listResult", listResult);	
					request.setAttribute("paramMap", paramMap);
			
					} catch(Throwable t) {
						debug(TITLE, t);
						throw new GolfException(TITLE, t);
					} 
					
					return super.getActionResponse(context, subpage_key);
					
				}

}
