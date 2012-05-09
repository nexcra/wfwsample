/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMojibInsFormActn.java
*   작성자    : E4NET 은장선
*   내용      : 모집인 가입폼
*   적용범위  : Golf
*   작성일자  : 2009-09-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

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

import com.bccard.waf.common.DateUtil;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.proc.admin.tm_member.GolfAdmMojibProc;
/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfAdmMojibInsFormActn extends GolfActn {
	
	public static final String TITLE = "모집인 가입폼"; 
	/***************************************************************************************
	* 비씨골프 관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		DbTaoResult taoResult = null;
		DbTaoResult taoCode = null;		
		String subpage_key = "default";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();	
		

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			
			
			//GolfAdmMojibProc proc = (GolfAdmMojibProc)context.getProc("GolfAdmMojibProc");
			GolfAdmMojibProc proc = new GolfAdmMojibProc();
			
			taoResult = (DbTaoResult)proc.getCommonCode(context, dataSet , "0050");			
			
			taoCode =  (DbTaoResult)proc.getCommonCode(context, dataSet , "0051");	
			
			request.setAttribute("taoResult", taoResult);	
			request.setAttribute("taoCode", taoCode);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	

		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		
		return getActionResponse(context, subpage_key);
		
	}
}
