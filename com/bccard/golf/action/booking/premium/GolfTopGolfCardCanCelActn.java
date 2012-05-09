/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardRegActn
*   작성자    : 이정규
*   내용      :  골프카드 전용 부킹  > 부킹 취소처리
*   적용범위  : Golf
*   작성일자  : 2010-10-26
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstRegDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardCanCelActn extends GolfActn{
	
	public static final String TITLE = " 골프카드 전용 부킹  > 부킹 취소처리";

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
		
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		
		try { 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String script = "";

			String aplc_seq_no					= parser.getParameter("aplc_seq_no", "");
			String pgrs_yn					= parser.getParameter("pgrs_yn", "");
			String green_nm					= parser.getParameter("green_nm", "");
			String teof_date 				= parser.getParameter("teof_date", "");
			String teof_time 				= parser.getParameter("teof_time", "");
			String userNm 					= parser.getParameter("user_nm", "");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("APLC_SEQ_NO", aplc_seq_no);
			dataSet.setString("PGRS_YN", pgrs_yn);
			
			dataSet.setString("green_nm", green_nm);
			dataSet.setString("teof_date", teof_date);
			dataSet.setString("teof_time", teof_time);
			dataSet.setString("userNm", userNm);

			// 04.실제 테이블(Proc) 조회
			GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
			
			/*int cntJumin = (int) proc.execute_jumin(context, request, dataSet);
			int cntHp = (int) proc.execute_hp(context, request, dataSet);
			
			if(cntJumin>0){ 
				script = "alert('동일한 주민등록번호의 신청내역이 있습니다.'); history.back();";
			}else if(cntHp>0){
				script = "alert('동일한 핸드폰 번호의 신청내역이 있습니다.'); history.back();";
			}else{*/
				int appInt = (int) proc.app_upd_pro(context, request, dataSet);

				if(appInt>0){
					script = "alert('예약/부킹 취소가 처리되었습니다.'); location.href='GolfTopGolfCardStatus.do';";
					//담당자에게 sms통보	시스템이 안정화 되기 전까지만 담당자한테 문자 발송
					if("C".equals(pgrs_yn)){
						proc.cancelSmsExe(context, request, dataSet);
					}
			        
				}else{
					script = "alert('예약/부킹 취소가 처리되지 않았습니다. 다시 시도해 주시기 바랍니다.'); location.href='GolfTopGolfCardStatus.do';";
				}
			//}
			
			
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
	

}
