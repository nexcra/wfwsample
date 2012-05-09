/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBusPeopleViewActn
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 > 이벤트->골프장버스운행이벤트->신청관리 상세보기
*   적용범위  : Golf
*   작성일자  : 2009-09-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.golfbus;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfAdmBusPeopleViewActn extends AbstractAction {

	public static final String TITLE = "관리자 골드장 버스 운행 신청관리 상세보기";
	
	/**
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionResponse
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request,
		HttpServletResponse response) throws IOException, ServletException,
			BaseException
	{
		TaoConnection 		con 				= null;
		TaoResult 			result  			= null;	
		TaoResult 			resultUse  			= null;			
		Map 				paramMap 			= null;
		
		try {
			// form parameter parsing
			RequestParser parser 				= context.getRequestParser("default", request, response);						
			paramMap 							= (Map)request.getAttribute("paramMap");
			if(paramMap == null) 	   paramMap = parser.getParameterMap();
			String actnKey 						= super.getActionKey(context);		
			long page_no						= parser.getLongParameter("page_no", 1L);				// 페이지번호
			String p_idx						= parser.getParameter("p_idx");							// 날짜
			
			con = context.getTaoConnection("dbtao",null);
			
			// 관리자 로그인 정보
			HttpSession session 				= request.getSession(false);
			GolfAdminEtt userEtt 				= (GolfAdminEtt) session.getAttribute("SESSION_ADMIN");
						
			// Proc 파라메터 설정
			TaoDataSet input 					= new DbTaoDataSet(TITLE);
			input.setObject("userEtt", 			userEtt);
			input.setString("actnKey", 			actnKey);
			input.setString("Title", 			TITLE);					
			input.setLong("page_no",			page_no);
			
			
			if( !"".equals(p_idx) ) 
			{				
				input.setString("p_idx", 			p_idx.replaceAll("\\.", ""));
				
				// 게시판 상세보기 조회		
				result = con.execute("admin.event.golfbus.GolfAdmBusPeopleDetailInqDaoProc",input);				
				request.setAttribute("p_idx", p_idx);
			}
			String userId = "";
			if(result.isNext()){
				result.next();
				userId = result.getString("CDHD_ID");            //아이디
				String intMemGradeNM = result.getString("GRADE");       //등급

				if(intMemGradeNM.equals("챔피온")){
					String intMemGrade = "1";
					input.setString("intMemGrade",intMemGrade);
				}else if(intMemGradeNM.equals("블루")){
					String intMemGrade = "2";
					input.setString("intMemGrade",intMemGrade);
				}else if(intMemGradeNM.equals("골드")){
					String intMemGrade = "3";
					input.setString("intMemGrade",intMemGrade);
				}else if(intMemGradeNM.equals("화이트")){
					String intMemGrade = "4";
					input.setString("intMemGrade",intMemGrade);
				}
				input.setString("userId",userId);				
				input.setString("cdhd_id",userId);	
			}
			
			//이용내역
			resultUse = con.execute("admin.event.golfbus.GolfAdmBusUsedCkInqDaoProc",input);
			
			
			String cnt = "";
			String tot_cnt = "";
			
			if(resultUse.isNext()){
				resultUse.next();
				cnt = resultUse.getString("CNT");
				tot_cnt = resultUse.getString("TOT");
				
				debug("cnt : " + cnt);
				debug("tot_cnt : " + tot_cnt);
				
				paramMap.put("cnt",cnt);
				paramMap.put("tot_cnt",tot_cnt);
			}

			String can_cnt      = String.valueOf(Integer.parseInt(tot_cnt) - Integer.parseInt(cnt));
			
												
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token")); 
			paramMap.put("page_no"			, page_no+""		);
			paramMap.put("p_idx"			, p_idx);	
			paramMap.put("green_nm"			, parser.getParameter("green_nm"));	
			paramMap.put("golf_cmmn_code"			, parser.getParameter("golf_cmmn_code"));	
			paramMap.put("grade"					, parser.getParameter("grade"));	
			paramMap.put("sch_reg_aton_st"			, parser.getParameter("sch_reg_aton_st"));	
			paramMap.put("sch_reg_aton_ed"			, parser.getParameter("sch_reg_aton_ed"));	
			paramMap.put("sch_pu_date_st"			, parser.getParameter("sch_pu_date_st"));	
			paramMap.put("sch_pu_date_ed"			, parser.getParameter("sch_pu_date_ed"));	
			paramMap.put("sch_type"					, parser.getParameter("sch_type"));	
			paramMap.put("search_word"				, parser.getParameter("search_word"));						
			paramMap.put("can_cnt",can_cnt);
			paramMap.put("cnt",cnt);
			
						
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);
			request.setAttribute("userId", userId);

			
		} catch (BaseException be) {
			throw be;
		} catch (Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} finally {
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return getActionResponse(context, "default");
	}
				

}
