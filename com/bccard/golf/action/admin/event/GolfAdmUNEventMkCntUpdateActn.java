/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthSetProcChgActn
*   작성자    : (주)미디어포스 조은미
*   내용      : 관리자 권한 설정 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmUNEventInfoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmUNEventMkCntUpdateActn extends GolfActn {
	
	public static final String TITLE = "인쇄 횟수 변경";
	/***************************************************************************************
	* 비씨골프 관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx;

		//debug("==== GolfAdmAuthSetProcChgActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			// 1. 파라메타 값 
			String cupn_no	= parser.getParameter("cupn_no", "");	
			String fromUrl      = parser.getParameter("fromUrl" ,"");               // 수정 숫자
			String resStr      = "";               // 수정 숫자
			
			//2. 삭제 
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("cupn_no",	cupn_no);
			
			
			// 3.링크 스크립트 
			GolfAdmUNEventInfoProc proc = (GolfAdmUNEventInfoProc)context.getProc("GolfAdmUNEventInfoProc");
			int updatecnt = proc.updatePrintCnt(context, cupn_no);
			if(updatecnt > 0){
				resStr = "Y";
			}else{
				resStr = "N";
			}
						
			Map paramMap = parser.getParameterMap();	
			request.setAttribute("cupn_no", cupn_no);			
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("resStr", resStr);
			request.setAttribute("fromUrl",fromUrl);
			
			//debug("==== GolfAdmAuthSetProcChgActn End ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
	

}
