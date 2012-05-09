/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGiftRegActn
*   작성자     : (주)미디어포스 조은미	
*   내용        : 관리자 사은품관리 등록 처리
*   적용범위  : Golf
*   작성일자  : 2009-08-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmGiftRegDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmGiftRegActn extends GolfActn  {
	
	public static final String TITLE = "관리자 사이버머니 관리 등록 처리";
	
	/***************************************************************************************
	* 비씨골프 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx;

	//	debug("==== GolfAdmGiftRegActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. 파라메타 값 
			String search_yn	= parser.getParameter("search_yn", "N");					// 검색여부
			String mode			= parser.getParameter("mode", "ins");						// 처리구분
			
			String search_clss	= "";
			String search_word	= "";
			
			if("Y".equals(search_yn)){
				search_clss		= parser.getParameter("search_clss");						// 검색구분
				search_word		= parser.getParameter("search_word");						// 검색어
			}
			long page_no		= parser.getLongParameter("page_no", 1L);				// 페이지번호 
			long page_size		= parser.getLongParameter("page_size", 10L);			// 페이지당출력수
			
			String cdhd_id					= parser.getParameter("cdhd_id"); 
			String rcvr_nm					= parser.getParameter("rcvr_nm"); 
			String golf_tmnl_gds_code		= parser.getParameter("golf_tmnl_gds_code"); 
			String hp_ddd_no				= parser.getParameter("hp_ddd_no");
			String hp_tel_hno				= parser.getParameter("hp_tel_hno"); 
			String hp_tel_sno				= parser.getParameter("hp_tel_sno");
			String zp1						= parser.getParameter("zp1"); 	
			String zp2						= parser.getParameter("zp2"); 
			String zp = zp1+zp2;
			String addr						= parser.getParameter("addr"); 	
			String dtl_addr					= parser.getParameter("dtl_addr");
			String addr_clss				= parser.getParameter("addr_clss"); //주소구분(구:1, 신:2)
			String memo_ctnt				= parser.getParameter("memo_ctnt"); 	
			String snd_yn					= parser.getParameter("snd_yn"); 
			String p_idx					= parser.getParameter("p_idx", "");
			String acrg_cdhd_jonn_date		= parser.getParameter("acrg_cdhd_jonn_date", "");
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
			}
			input.setString("mode",						mode);
			input.setString("cdhd_id",					cdhd_id);
			input.setString("rcvr_nm",					rcvr_nm);
			input.setString("golf_tmnl_gds_code",		golf_tmnl_gds_code);
			input.setString("hp_ddd_no",				hp_ddd_no);
			input.setString("hp_tel_hno",				hp_tel_hno);
			input.setString("hp_tel_sno",				hp_tel_sno);
			input.setString("zp",						zp);
			input.setString("addr",						addr);
			input.setString("dtl_addr",					dtl_addr);
			input.setString("addr_clss",				addr_clss);
			input.setString("memo_ctnt",				memo_ctnt);
			input.setString("snd_yn",					snd_yn);
			input.setLong("page_no",					page_no);
			input.setLong("page_size",					page_size);
			input.setString("p_idx",					p_idx);
			input.setString("acrg_cdhd_jonn_date",		acrg_cdhd_jonn_date);
			
			//debug("snd_yn:::::::::::::::::::::::::::"+snd_yn);
			
			Map paramMap = parser.getParameterMap();	
			
			// 3. DB 처리 
			GolfAdmGiftRegDaoProc proc = (GolfAdmGiftRegDaoProc)context.getProc("GolfAdmGiftRegDaoProc");
			DbTaoResult giftInq = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("giftInq", giftInq);	
			request.setAttribute("paramMap", paramMap);
			
		//	debug("==== GolfAdmGiftRegActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
