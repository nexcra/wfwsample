/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLoungTMCarzenListActn.java
*   작성자    : (주) 미디어포스
*   내용      : 카젠 TM회원 조회
*   적용범위  : Golf
*   작성일자  : 2010-07-16
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
/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfLoungTMCouponListActn extends GolfActn {
	
	public static final String TITLE = "TM 카젠 회원 조회"; 
	/***************************************************************************************
	* 비씨골프 관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		DbTaoResult taoResult = null;
		String subpage_key = "default";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();
			
			String action_key = super.getActionKey(context);
			debug(action_key);
		

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			String	sch_type		= parser.getParameter("SCH_TYPE", "");
			String	sch_text		= parser.getParameter("SCH_TEXT", "");
			long page_no			= parser.getLongParameter("page_no", 1);
			long record_size		= parser.getLongParameter("record_size", 20);			

			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
						

			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("sch_date_st", sch_date_st); 
			dataSet.setString("sch_date_ed", sch_date_ed);
			dataSet.setString("sch_type", sch_type);
			dataSet.setString("sch_text", sch_text);
			

			// 04.실제 테이블(Proc) 조회
//			GolfLoungTMCouponListProc proc = (GolfLoungTMCouponListProc)context.getProc("GolfLoungTMCouponListProc");
//			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
//			
//			listResult.next();
//			String result = listResult.getString("RESULT");
//			if ("00".equals(result))
//				paramMap.put("total_cnt", listResult.getString("TOT_CNT"));
//			else
//				paramMap.put("total_cnt", "0");
			
			
			paramMap.put("resultSize", String.valueOf(taoResult.size()));
			paramMap.put("page_no",String.valueOf(page_no));
			paramMap.put("SCH_TYPE", sch_type);
			paramMap.put("SCH_TEXT", sch_text);
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			
			request.setAttribute("paramMap",paramMap);
			request.setAttribute("page_no",String.valueOf(page_no));
			request.setAttribute("resultList",taoResult); 
			request.setAttribute("record_size", String.valueOf(record_size));

			
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		
		return getActionResponse(context, subpage_key);
		
	}
}
