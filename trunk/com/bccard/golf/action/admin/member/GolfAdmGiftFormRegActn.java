/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGiftFormRegActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 관리자 사은품관리 등록 폼 
*   적용범위  : Golf
*   작성일자  : 2009-08-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 
import java.util.Map;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmGiftFormRegActn extends GolfActn {

	public static final String TITLE ="사은품관리 등록 폼";

	/********************************************************************
	* EXECUTE
	* @param context		WaContext 객체.
	* @param request		HttpServletRequest 객체.
	* @param response		HttpServletResponse 객체.
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보.
	******************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		 
		DbTaoConnection con = null;

		try{
			debug("==== GolfAdmGiftFormRegActn start ===");
			
			RequestParser parser = context.getRequestParser("default", request, response);
			String p_idx		= parser.getParameter("p_idx");
			
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			if( !"".equals(p_idx)) {
				//게시물 상세정보
				input.setString("p_idx", p_idx);
	
				//게시물 상세정보 execute
				
				GolfAdmGiftDtlInqDaoProc proc1 = (GolfAdmGiftDtlInqDaoProc)context.getProc("GolfAdmGiftDtlInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				if (detailInq != null ) {
					detailInq.next();
				}
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
				
			}
			//debug("==== GolfAdmGiftFormRegActn 111111111111111111111 ===");
			//카테고리 목록
			GolfGiftCategoryInqDaoProc proc2 = (GolfGiftCategoryInqDaoProc)context.getProc("GolfGiftCategoryInqDaoProc");
			DbTaoResult categoryListInq = (DbTaoResult)proc2.execute(context, request, input);
			//request.setAttribute("categoryListInq", categoryListInq);
			//debug("==== GolfAdmGiftFormRegActn 2222222222222222222222 ===");
			Map paramMap = parser.getParameterMap();
			request.setAttribute("categoryListInq", categoryListInq);
			request.setAttribute("paramMap", paramMap);
			
			debug("==== GolfAdmGiftFormRegActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmCodeFormRegActn Error ===");
			t.printStackTrace();
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
	}
}
