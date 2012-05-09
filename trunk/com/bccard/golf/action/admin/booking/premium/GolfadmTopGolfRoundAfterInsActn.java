/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfRoundAfterInsActn
*   작성자    : shin cheong gwi
*   내용      : 라운딩후기
*   적용범위  : golfloung
*   작성일자  : 2010-11-24
************************** 수정이력 ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfRoundAfterInsProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfadmTopGolfRoundAfterInsActn extends GolfActn {

public static final String TITLE = "관리자 > 라운딩후기 / Q&A"; 
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException
	{
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String viewType = "default";
		boolean rtn_flag = false;
		String adminno = "";
		int admin_no = 0;
		String admin_nm = "";
		
		try
		{
			// 01.입력값 조회		
			RequestParser parser = context.getRequestParser(viewType, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			//HttpSession session = request.getSession(true); 
			
			// 라운딩 후기 입력시
			String tab_idx = parser.getParameter("tab_idx", "");								// 탭매뉴 코드
			String actn_key = parser.getParameter("actn_key", "");								// 액션명
			String board_cd = parser.getParameter("board_cd", "");								// 게시코드
			String board_subj = parser.getParameter("board_subj", "");							// 제목
			String board_text = parser.getParameter("board_text", "");							// 내용(텍스트)
			String board_data = parser.getParameter("board_data", "");							// 내용(clob)
			String board_html_yn = parser.getParameter("board_html_yn", "N");
			String scor_appl_yn = parser.getParameter("scor_appl_yn","0");
			String replyyn = parser.getParameter("replyyn", "N");	
			String access = parser.getParameter("access", "");
			String search_type = parser.getParameter("search_type", "BOARD_SUBJ");						// 검색여부
			String search_word = parser.getParameter("search_word", "");						// 검색어	
			String search_type2 = parser.getParameter("search_type2", "BOARD_SUBJ");						// 검색여부
			String search_word2 = parser.getParameter("search_word2", "");						// 검색어				
			long pageNo = parser.getLongParameter("pageNo", 0L);
			long pageNo2 = parser.getLongParameter("pageNo2", 0L);
			long ref_no = parser.getLongParameter("ref_no", 0L);
			long ans_lev = parser.getLongParameter("ans_lev", 0L);
			long ans_stg = parser.getLongParameter("ans_stg", 0L);
						
			// 뎃글 등록시
			long board_no = parser.getLongParameter("board_no", 0L);
			long seq_no = parser.getLongParameter("seq_no", 0L);
			String add_cont = parser.getParameter("add_cont", "");
						
			/*int memNo= 0;
			String memb_id ="";
			String memb_nm = "";
			String memSocid = ""; 
			String checkYn = "N";
			String strMemChkNum = "";
			*/
			
			// 02.세션정보체크 
			HttpSession session = request.getSession(true);
			GolfAdminEtt userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){	
				adminno	= (String)userEtt.getMemNo();	
				admin_no = Integer.parseInt(adminno);
				//admin_nm = (String)userEtt.getMemNm();
				admin_nm = "비씨골프";
				//debug("---------------------->"+userEtt.getMemId()+"--"+userEtt.getAdm_clss());
			}			
			
			DbTaoDataSet ds = new DbTaoDataSet(TITLE);
				ds.setString("board_cd", board_cd);
				ds.setString("board_subj", board_subj);
				ds.setString("board_text", board_text);
				ds.setString("board_data", board_data);				
				ds.setString("board_html_yn", board_html_yn);
				ds.setString("replyyn", replyyn);
				ds.setString("scor_appl_yn", scor_appl_yn);
				ds.setLong("ref_no", ref_no);
				ds.setLong("ans_lev", ans_lev);
				ds.setLong("ans_stg", ans_stg);		
				ds.setInt("reg_no", admin_no);
				ds.setString("reg_nm", admin_nm);	
				ds.setLong("board_no", board_no);
				ds.setLong("seq_no", seq_no);
				ds.setString("add_cont", add_cont);
				ds.setString("access", access);
			
			GolfAdmTopGolfRoundAfterInsProc instance = GolfAdmTopGolfRoundAfterInsProc.getInstance();			
			
			if(access.equals("comment")){
				rtn_flag = instance.add_execute(context, request, ds);
				viewType = actn_key+"_"+access;
			}else if(access.equals("comment_del")){
				rtn_flag = instance.comment_del_execute(context, request, ds);
				viewType = actn_key+"_comment";
			}else if(access.equals("del")){
				rtn_flag = instance.del_execute(context, request, ds);
				viewType = actn_key;
			}else{				
				rtn_flag = instance.execute(context, request, ds);	
				viewType = actn_key;
			}
			if(rtn_flag){
				debug("Successe!!!!!!!!!!!!!!!!!!!!!!!");
			}else{
				debug("Failure!!!!!!!!!!!!!!!!!!!!!!!!");
			}			
			
			paramMap.put("board_cd", board_cd);
			paramMap.put("tab_idx", tab_idx);
			paramMap.put("board_no", String.valueOf(board_no));
			paramMap.put("pageNo", String.valueOf(pageNo));
			paramMap.put("pageNo2", String.valueOf(pageNo2));
			paramMap.put("search_type", search_type);
			paramMap.put("search_word", search_word);
			paramMap.put("search_type2", search_type2);
			paramMap.put("search_word2", search_word2);
			request.setAttribute("paramMap", paramMap);			
			
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);		
	}
}
