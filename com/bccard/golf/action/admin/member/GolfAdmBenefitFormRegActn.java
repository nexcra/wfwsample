/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBenefitFormRegActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 관리자 회원혜택 관리 등록 폼
*   적용범위  : Golf
*   작성일자  : 2009-05-18
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
public class GolfAdmBenefitFormRegActn extends GolfActn {

	public static final String TITLE ="회원혜택관리 등록 폼";

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
			//debug("==== GolfAdmBenefitFormRegActn start ===");
			RequestParser parser = context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();
			String p_idx		= parser.getParameter("p_idx");		// 등급
			String bokg_knd		= "";	// 부킹종류코드(골프장별 혜택관리)
			debug("p_idx : " + p_idx); 
			
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			
			if( !"".equals(p_idx) ) {
				input.setString("p_idx", p_idx);
			
				//게시물 상세정보 execute
				GolfAdmBenefitDetailInqDaoProc proc1 = (GolfAdmBenefitDetailInqDaoProc)context.getProc("GolfAdmBenefitDetailInqDaoProc");
				DbTaoResult detailInq = (DbTaoResult)proc1.execute(context, request, input);
				
				if (detailInq != null ) {
					detailInq.next();
				}

				// 골프장별 혜택관리(파3)
				bokg_knd = "0001";	// 파3
				input.setString("bokg_knd", bokg_knd);
				DbTaoResult parResult = (DbTaoResult)proc1.execute_green(context, request, input);			

				request.setAttribute("parResult", parResult);			
				request.setAttribute("detailInq", detailInq);
				request.setAttribute("p_idx", p_idx);
			}
						
			//회원등급카테고리 목록
			GolfAdmBenefitCategoryInqDaoProc proc2 = (GolfAdmBenefitCategoryInqDaoProc)context.getProc("GolfAdmBenefitCategoryInqDaoProc");
			DbTaoResult categoryListInq = (DbTaoResult)proc2.execute(context, request, input);
			

			
			request.setAttribute("categoryListInq", categoryListInq);
			request.setAttribute("paramMap", paramMap);
			

		} catch(Throwable t) {
			//debug("==== GolfAdmBenefitFormRegActn Error ===");
			t.printStackTrace();
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
	}
}
