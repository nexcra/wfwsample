/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemMgmtRegActn
*   작성자     : (주)미디어포스 천선정	
*   내용        : 관리자 회원등록관리 처리
*   적용범위  : Golf
*   작성일자  : 2009-11-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmBenefitRegDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemMgmtRegDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/

public class GolfAdmMemMgmtRegActn extends GolfActn  {
	
	public static final String TITLE = "관리자 회원혜택 관리 등록 처리"; 
	
	/***************************************************************************************
	* 비씨골프 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx; 

		//debug("==== GolfAdmCodeRegActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();
			//1. 파라메타 값 
			String mode				= parser.getParameter("mode", "ins");						// 처리구분
			long page_no			= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size			= parser.getLongParameter("page_size", 10L);			// 페이지당출력수	
			
			String p_idx			= parser.getParameter("p_idx", "");
			String cmmn_code		= parser.getParameter("CMMN_CODE", "");
			String cmmn_code_nm		= parser.getParameter("CMMN_CODE_NM", "");
			String expl				= parser.getParameter("EXPL", "");
			String use_yn			= parser.getParameter("USE_YN", "");
			String cdhd_sq1_ctgo 	= parser.getParameter("CDHD_SQ1_CTGO","");
			
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("mode",				mode.trim());
			input.setString("p_idx",			p_idx.trim());
			input.setString("cmmn_code",		cmmn_code.trim());
			input.setString("cmmn_code_nm",		cmmn_code_nm);
			input.setString("expl",				expl);
			input.setString("use_yn",			use_yn.trim());
			input.setString("cdhd_sq1_ctgo",	cdhd_sq1_ctgo.trim());
			
			
			// 3. DB 처리 
			GolfAdmMemMgmtRegDaoProc proc = (GolfAdmMemMgmtRegDaoProc)context.getProc("GolfAdmMemMgmtRegDaoProc");
			DbTaoResult result = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("result", result);						
					
			paramMap.put("page_no",String.valueOf(page_no));
			paramMap.put("page_size",String.valueOf(page_size));
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmCodeRegActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
