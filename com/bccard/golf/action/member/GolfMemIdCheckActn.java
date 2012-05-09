/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfMemIdCheckActn
*   작성자	: (주)미디어포스
*   내용		: 사용자 > 회원가입 > 중복 아이디 체크
*   적용범위	: golf 
*   작성일자	: 2009-12-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.member.GolfMemJoinCorpDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfMemIdCheckActn extends GolfActn { 
	
	public static final String TITLE = "사용자 > 회원가입 > 중복 아이디 체크"; 

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		Connection con = null;
		
		try {
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			GolfMemJoinCorpDaoProc corpProc = (GolfMemJoinCorpDaoProc)context.getProc("GolfMemJoinCorpDaoProc");
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
		
			String account = parser.getParameter("account", "");				// 아이디
					
			dataSet.setString("account", account);			
			String idChkResult = corpProc.chkIdDuplicate(context, dataSet, request);	

			debug("## GolfMemIdCheckActn | 아이디 중복 체크 | account : " + account + " | 중복 여부 : " + idChkResult + "\n");

			request.setAttribute("idChkResult", idChkResult); 	// Y:아이디중복	  
			request.setAttribute("idChkAccount", account); 		  
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
			
}
