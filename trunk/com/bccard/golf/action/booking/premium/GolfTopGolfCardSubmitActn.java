/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardSubmitActn.java
*   작성자    : 이정규
*   내용      :  Top골프카드 전용 부킹  > top골프 부킹 확인 화면 신청처리
*   적용범위  : Golf
*   작성일자  : 2010-10-29
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.HashMap;
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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstRegDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardSubmitActn extends GolfActn{
	
	public static final String TITLE = " Top골프카드 전용부킹 > 회원부킹 > 티타임 리스트-상세보기 > 부킹정보 확인 화면";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout"); 
		request.setAttribute("layout", layout);
		
		try { 
			String memb_id ="";
			String memSocid = "";
			String checkYn = "N";
			// 01.세션정보체크 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memb_id = userEtt.getAccount();				// 회원 아이디
				memSocid = userEtt.getSocid();
			}
			
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String script = "";

			String green_nm						= parser.getParameter("GREEN_NM", "");
			String teof_date					= parser.getParameter("TEOF_DATE", "");
			String teof_time					= parser.getParameter("TEOF_TIME","");
			String co_nm						= parser.getParameter("CO_NM","");
			String cdhd_id						= parser.getParameter("CDHD_ID","");
			String email_id						= parser.getParameter("EMAIL_ID","");
			
			String hp_ddd_no					= parser.getParameter("HP_DDD_NO","");
			String hp_tel_hno					= parser.getParameter("HP_TEL_HNO","");
			String hp_tel_sno					= parser.getParameter("HP_TEL_SNO","");
			String memp_expl					= parser.getParameter("MEMO_EXPL","");
			String bkg_pe_nm					= parser.getParameter("BKG_PE_NM","");
			String breach_amt					= parser.getParameter("BREACH_AMT","").replaceAll(",","");
			
			//AFFI_GREEN_SEQ_NO
			String affi_green_seq_no			= parser.getParameter("AFFI_GREEN_SEQ_NO","");
			
			paramMap.put("GREEN_NM", green_nm);
			paramMap.put("TEOF_DATE", teof_date);
			paramMap.put("TEOF_TIME", teof_time);
			paramMap.put("CO_NM", co_nm);
			paramMap.put("CDHD_ID", cdhd_id);
			paramMap.put("EMAIL_ID", email_id);
			paramMap.put("HP_DDD_NO", hp_ddd_no);
			paramMap.put("HP_TEL_HNO", hp_tel_hno);
			paramMap.put("HP_TEL_SNO", hp_tel_sno);
			paramMap.put("MEMO_EXPL", memp_expl);
			paramMap.put("BKG_PE_NM", bkg_pe_nm);
			paramMap.put("BREACH_AMT", breach_amt);
			paramMap.put("AFFI_GREEN_SEQ_NO", affi_green_seq_no);
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);	//코스를 구해야함
			dataSet.setString("BOKG_ABLE_DATE", teof_date);
			dataSet.setString("AFFI_GREEN_SEQ_NO", affi_green_seq_no);
			
			GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
			String golf_rsvt_curs_nm = (String)proc.getCourse(context, request, dataSet);
			
			paramMap.put("GOLF_RSVT_CURS_NM", golf_rsvt_curs_nm);
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
