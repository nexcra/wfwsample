/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaChgFormActn
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 수정폼
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.mania;

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
import com.bccard.golf.dbtao.proc.admin.mania.GolfAdmManiaUpdFormDaoProc;
import com.bccard.golf.dbtao.proc.mania.GolfLimousineDaoProc;


/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmManiaChgFormActn extends GolfActn{
	
	public static final String TITLE = "관리자 골프장리무진할인신청관리 수정폼";

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
			long seq_no			= parser.getLongParameter("p_idx", 0);
			long page_no		= parser.getLongParameter("page_no", 1L);		// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);	// 페이지당출력수
			String str_userid 	= parser.getParameter("cdhd_id","");
			String subkey		= parser.getParameter("subkey", "");		
			String scoop_cp_cd	= parser.getParameter("scoop_cp_cd", ""); 		//0001:리무진할인 0002:골프잡지
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("RECV_NO", seq_no);
		
		
			
			 
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmManiaUpdFormDaoProc proc = (GolfAdmManiaUpdFormDaoProc)context.getProc("GolfAdmManiaUpdFormDaoProc");
			DbTaoResult maniaInq = proc.execute(context, dataSet);
			
			//회원등급 get
			int intMemGrade = proc.getIntMemGrade(context, dataSet, str_userid);
			String column = "";
			
			if(intMemGrade > 0){
				//할인 정책 : Champion - 30% DC / Blue, Black - 20% DC / else Norm
				if(intMemGrade == 1){
					column = "PCT30_DC_PRIC";
				}else if(intMemGrade == 2 || intMemGrade == 5 ||  intMemGrade == 6 || intMemGrade == 7){
					column = "PCT20_DC_PRIC";
				}else{
					column = "NORM_PRIC";
				}
				
				//04.리무진 금액 관련 테이블 조회
				GolfLimousineDaoProc coopCpSelProc = (GolfLimousineDaoProc)context.getProc("GolfLimousineDaoProc");
				DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet ,column); //제휴업체
				request.setAttribute("coopCpSel", coopCpSel);	
				
			}else{
				//04.리무진 금액 관련 테이블 조회
				GolfLimousineDaoProc coopCpSelProc = (GolfLimousineDaoProc)context.getProc("GolfLimousineDaoProc");
				DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet); //제휴업체
				request.setAttribute("coopCpSel", coopCpSel);	
				
			}
			
	
			
			// 05. Return 값 세팅			
			//debug("maniaInq.size() ::> " + maniaInq.size());
			
			paramMap.put("page_no", String.valueOf(page_no));
			paramMap.put("record_size", String.valueOf(record_size));
			paramMap.put("cdhd_id", str_userid);
			paramMap.put("subkey", subkey);
			paramMap.put("scoop_cp_cd", scoop_cp_cd);
			
			
			request.setAttribute("maniaInqResult", maniaInq);	
			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}