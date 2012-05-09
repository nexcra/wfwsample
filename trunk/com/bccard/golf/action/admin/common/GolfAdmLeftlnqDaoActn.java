/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointAdmLeftlnqDaoActn
*   작성자    : (주)미디어포스 조은미
*   내용      : 골프 왼쪽 메뉴 가져오기
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.common;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.initech.dbprotector.CipherClient;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.GolfAdmLeftlnqDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmLeftlnqDaoActn extends GolfActn {
	
	public static final String TITLE = "비씨골프  관리자  2depth 메뉴 가져오기"; 
	/***************************************************************************************
	* 비씨골프 관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		GolfAdminEtt userEtt = null;
		DbTaoResult taoResult = null;
		String subpage_key = "default";
		String mem_nm = "";
		String mem_id = "";
		String mem_no = "";
		String rtnCode = "";
		String rtnMsg = "";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();	
			String m0_idx		= parser.getParameter("m0_idx");
			
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				mem_nm 		= (String)userEtt.getMemNm(); 
				mem_id		= (String)userEtt.getMemId(); 
				mem_no 		= (String)userEtt.getMemNo(); 
				//debug("Left admin mem_nm="+mem_nm);
			}
			//2.권한 조회
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("account", mem_id);
			dataSet.setString("log_p_idx", mem_no);
			dataSet.setString("m0_idx", m0_idx);
			
			
			GolfAdmLeftlnqDaoProc proc = (GolfAdmLeftlnqDaoProc)context.getProc("GolfAdmLeftlnqDaoProc");
			taoResult = (DbTaoResult)proc.execute(context, dataSet);	// 관리자 조회
			
			//debug("=============> rtnCode : " +  rtnCode);
			request.setAttribute("paramMap",paramMap);
			request.setAttribute("rtnCode",rtnCode);
			request.setAttribute("rtnMsg",rtnMsg);
			request.setAttribute("result",taoResult);
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		
		return getActionResponse(context, subpage_key);
		
	}
}
