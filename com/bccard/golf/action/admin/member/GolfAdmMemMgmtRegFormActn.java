/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemMgmtRegFormActn
*   작성자     : (주)미디어포스 천선정
*   내용        : 관리자 회원 등급관리 상세보기
*   적용범위  : Golf
*   작성일자  : 2009-11-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmCyberBenefitRegDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemMgmtDetailDaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-03-31
 **************************************************************************** */

public class GolfAdmMemMgmtRegFormActn extends GolfActn  {
	
	public static final String TITLE = "관리자 회원 등급관리 상세보기";
	
	/***************************************************************************************
	* 비씨골프 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. 파라메타 값 

			String p_idx		= parser.getParameter("p_idx", "");						//	게시물번호
			long page_no		= parser.getLongParameter("page_no", 1L);				// 	페이지번호 
			long page_size		= parser.getLongParameter("page_size", 10L);			// 	페이지당출력수	
			
			
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("p_idx",		p_idx);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			
			
			// 맵 선언
			Map paramMap = parser.getParameterMap();	
			
			// 3. DB 처리 
			GolfAdmMemMgmtDetailDaoProc proc = (GolfAdmMemMgmtDetailDaoProc)context.getProc("GolfAdmMemMgmtDetailDaoProc");
			DbTaoResult detailInq = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("DetailInq", detailInq);						
			 
			paramMap.put("p_idx", p_idx);
			paramMap.put("page_no", String.valueOf(page_no));
			paramMap.put("page_size", String.valueOf(page_size));
			
			request.setAttribute("paramMap", paramMap);
			
			
		}catch(Throwable t) {
			debug("==== GolfAdmMemMgmtRegFormActn Error ===");
			return errorHandler(context,request,response,t);
		
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
