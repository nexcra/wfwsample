/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntBsLsnAcceptDetailActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 > 이벤트 >특별레슨 이벤트 >당첨자게시판관리
*   적용범위	: golf
*   작성일자	: 2009-07-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.accept;

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
import com.bccard.golf.dbtao.proc.admin.event.accept.GolfAdmEvntBsLsnAcceptDetailDaoProc;
import com.bccard.golf.dbtao.proc.admin.event.accept.GolfAdmEvntBsLsnInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBsLsnAcceptDetailActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 이벤트 >특별레슨 이벤트 >당첨자게시판관리 처리";

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
			String evnt_clss 	= "0003";
			String golf_svc_aplc_clss = "0005";
			String p_idx 		= parser.getParameter("p_idx","");
			String mode 		= parser.getParameter("mode","");
			String bltn_yn 		= parser.getParameter("bltn_yn","");
			String search_evnt 	= parser.getParameter("search_evnt","");
			String search_word 	= parser.getParameter("search_word","");
			String search_clss 	= parser.getParameter("search_clss","");
			String search_eps 	= parser.getParameter("search_eps","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("evnt_clss", 			evnt_clss);
			dataSet.setString("golf_svc_aplc_clss",	golf_svc_aplc_clss);
			dataSet.setString("p_idx",				p_idx);
			dataSet.setString("mode",				"RegFormInq");
			dataSet.setString("bltn_yn",			bltn_yn);
			dataSet.setString("search_word",		search_word);
			dataSet.setString("search_clss",		search_clss);
			dataSet.setString("search_eps",			search_eps);
			//dataSet.setString("search_evnt", 		search_evnt);
			dataSet.setString("evntListMode", 		"RegFormInq");
			dataSet.setLong("page_no", 				page_no);
			
			//이벤트 목록 조회
			GolfAdmEvntBsLsnInqDaoProc evnt_proc = (GolfAdmEvntBsLsnInqDaoProc)context.getProc("GolfAdmEvntBsLsnInqDaoProc");
			DbTaoResult evntInq = (DbTaoResult)evnt_proc.execute(context,request,dataSet);
			request.setAttribute("evntInq", evntInq);
			
		
			// 04.실제 테이블(Proc) 조회
			GolfAdmEvntBsLsnAcceptDetailDaoProc proc = (GolfAdmEvntBsLsnAcceptDetailDaoProc)context.getProc("GolfAdmEvntBsLsnAcceptDetailDaoProc");
			DbTaoResult boardDetail = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("boardDetail", boardDetail);	
		
			if(boardDetail.isNext()){
				boardDetail.next(); 
				if("00".equals(boardDetail.getString("RESULT"))){
					if("".equals(search_evnt) || "A".equals(search_evnt)){
						search_evnt = boardDetail.getString("evnt_seq_no");
					} 
				}
			}
			
			//if("A".equals(search_evnt)) search_evnt = "";
			
			//당첨자 목록
			dataSet.setString("mode",		 "PrzWinList");
			dataSet.setString("search_evnt", search_evnt);
			
			DbTaoResult przInq = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("przInq", przInq);
			
			
			//모든 파라미터값을 맵에 담아 반환한다.	
			paramMap.put("p_idx",			p_idx);
			/*
			paramMap.put("search_word", search_word);
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_eps",  search_eps);
			*/
			paramMap.put("search_evnt", search_evnt);
			
			paramMap.put("page_no", 	Long.toString(page_no));
	        request.setAttribute("paramMap", paramMap); 		
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
