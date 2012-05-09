/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLoungTMListActn.java
*   작성자    : 서비스개발팀 강선영
*   내용      : TM회원 조회
*   적용범위  : Golf
*   작성일자  : 2009-07-17
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
public class GolfLoungTMListActn extends GolfActn {
	
	public static final String TITLE = "TM회원 조회"; 
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
			//
		

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			String jumin_no			= "";
			String hg_nm			= "";
			String tb_rslt_clss			= parser.getParameter("SCH_STATE", "00");
			long page_no			= parser.getLongParameter("page_no", 1);
			long record_size	= parser.getLongParameter("record_size", 20);
			String	sch_type		= parser.getParameter("SCH_TYPE", "");
			String	sch_text		= parser.getParameter("SCH_TEXT", "");
			String	acpt_chnl_clss		= parser.getParameter("acpt_chnl_clss", "");
			String	st_gb		= parser.getParameter("ST_GB", "1");
			String	sch_ez		= parser.getParameter("SCH_EZ", "N");
			
			
			
			//TM조회
			if (action_key.equals("admTmMemberList"))
			{
					acpt_chnl_clss="1";
			//모집인/제휴처 조회
			} else if (action_key.equals("admMojibList"))
			{
				if  ("".equals(acpt_chnl_clss)){
					acpt_chnl_clss="2";
				}
			}

			if ("jumin_no".equals(sch_type)) jumin_no=sch_text;
			if ("hg_nm".equals(sch_type)) hg_nm=sch_text;
			

			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
						

			dataSet.setString("acpt_chnl_clss", acpt_chnl_clss);
			dataSet.setString("start_date", sch_date_st); //
			dataSet.setString("end_date", sch_date_ed); //
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("hg_nm", hg_nm); //
			dataSet.setString("tb_rslt_clss", tb_rslt_clss); //
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size); //
			dataSet.setString("st_gb", st_gb); //
			dataSet.setString("sch_ez", sch_ez); //
						
			
			GolfLoungTMListProc proc = new GolfLoungTMListProc();
//			GolfLoungTMListProc proc = (GolfLoungTMListProc)context.getProc("GolfLoungTMListProc");

 
			taoResult = (DbTaoResult)proc.execute(context, dataSet);	// 
			taoResult.next();
			String sRESULT = taoResult.getString("RESULT");

			if ("00".equals(sRESULT))
				paramMap.put("total_cnt", taoResult.getString("TOT_CNT"));
			else
				paramMap.put("total_cnt", "0");

			paramMap.put("resultSize", String.valueOf(taoResult.size()));

			paramMap.put("page_no",String.valueOf(page_no));
			paramMap.put("SCH_STATE", tb_rslt_clss);
			paramMap.put("SCH_TYPE", sch_type);
			paramMap.put("SCH_TEXT", sch_text);
			paramMap.put("acpt_chnl_clss", acpt_chnl_clss);
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			paramMap.put("ST_GB",st_gb);
			paramMap.put("SCH_EZ",sch_ez);
			
			
			
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
