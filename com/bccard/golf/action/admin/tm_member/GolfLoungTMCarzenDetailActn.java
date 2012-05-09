/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLoungTMCarzenDetailActn
*   작성자    : 서비스개발팀 강선영
*   내용      : 관리자 TM회원 상세보기
*   적용범위  : golf
*   작성일자  : 2009-07-19
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
 
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn; 
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Golf
* @author	
* @version	1.0 
******************************************************************************/
public class GolfLoungTMCarzenDetailActn extends GolfActn{
	
	public static final String TITLE = "관리자 > TM관리 > TM회원보기 ";
	
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
			String jumin_no			= parser.getParameter("jumin_no", "");
			String work_date		= parser.getParameter("work_date", "");
			String ST_YEAR         	= parser.getParameter("ST_YEAR","");
			String ST_MONTH         = parser.getParameter("ST_MONTH","");
			String ST_DAY			= parser.getParameter("ST_DAY","");
			String ED_YEAR         	= parser.getParameter("ED_YEAR","");
			String ED_MONTH         = parser.getParameter("ED_MONTH","");
			String ED_DAY			= parser.getParameter("ED_DAY","");
			String st_gb			= parser.getParameter("ST_GB","1");
			String sch_scal			= parser.getParameter("SCH_SCAL","");
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("work_date", work_date);
			
			// 04.실제 테이블(Proc) 조회
			GolfLoungTMCarzenDetailProc proc = new GolfLoungTMCarzenDetailProc();
			//GolfLoungTMDetailProc proc = (GolfLoungTMDetailProc)context.getProc("GolfLoungTMDetailProc");
			DbTaoResult dUpdFormResult = proc.execute(context, dataSet);
						
			request.setAttribute("UpdFormResult", dUpdFormResult);	
			
			paramMap.put("ST_YEAR",ST_YEAR);
			paramMap.put("ST_MONTH",ST_MONTH);
			paramMap.put("ST_DAY",ST_DAY);
			paramMap.put("ED_YEAR",ED_YEAR);
			paramMap.put("ED_MONTH",ED_MONTH);
			paramMap.put("ED_DAY",ED_DAY);
			paramMap.put("ST_GB",st_gb);
			paramMap.put("SCH_SCAL",sch_scal);
			
			
	        
			request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			

	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		}  
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
