/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkTimeReserveActn
*   작성자    : 미디어포스 임은혜
*   내용      : 부킹 > xgolf > 예약
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.security.cryptography.*;

/******************************************************************************
* Golf
* @author	미디어포스 
* @version	1.0  
******************************************************************************/
public class GolfBkTimeReserveActn extends GolfActn{
	
	public static final String TITLE = "부킹 > xgolf > 예약";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.remove("memb_id");
			paramMap.remove("xFA");
			paramMap.remove("xPrc");
			paramMap.remove("xReser");
			
			String key = "adf34alkjdf";   
			String iv = "efef897akdjfkl";   
			  
			// 인스턴스 만들기. 
			StringEncrypter encrypter = new StringEncrypter(key, iv);   
			
			// 문자열 복호화.   
			String memb_id_enc			= parser.getParameter("memb_id", "").trim();		// 암호화된 아이디
			String memb_id 				= encrypter.decrypt(memb_id_enc);					// 아이디
			
			String xFA 					= parser.getParameter("xFA", "").trim();		// 주중 : 1 / 주말 : 2 / 그린피할인 : 3
			String xMsg					= parser.getParameter("xMsg", "1").trim();		// 1 : 일반부킹메세지  2:그린피메세지
			String xPrc 				= parser.getParameter("xPrc", "2").trim();		// 포인트차감 : 1 / 횟수차감 : 2
			String xReser 				= parser.getParameter("xReser", "").trim();		// 예약 : 1 / 취소 : 2

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			int intBkGrade = 0;		// 차감등급
			if(xFA.equals("1")){
				DbTaoResult nmWkdView = proc_times.getNmWkdBenefit(context, dataSet, request);
				nmWkdView.next();
				intBkGrade = nmWkdView.getInt("intBkGrade");
			}else if(xFA.equals("2")){
				DbTaoResult nmWkeView = proc_times.getNmWkeBenefit(context, dataSet, request);
				nmWkeView.next();
				intBkGrade = nmWkeView.getInt("intBkGrade");
			}

			debug("======================================================================");
			debug("===========GolfBkTimeReserveActn============memb_id_enc => " + memb_id_enc);
			debug("===========GolfBkTimeReserveActn============memb_id => " + memb_id);
			debug("===========GolfBkTimeReserveActn============xFA => " + xFA);
			debug("===========GolfBkTimeReserveActn============xMsg => " + xMsg);
			debug("===========GolfBkTimeReserveActn============xPrc => " + xPrc);
			debug("===========GolfBkTimeReserveActn============xReser => " + xReser);
			debug("======================================================================");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			dataSet.setString("memb_id", memb_id);
			dataSet.setString("xFA", xFA);
			dataSet.setString("xPrc", xPrc);
			dataSet.setString("xReser", xReser);
			dataSet.setInt("intBkGrade", intBkGrade);
						
			// 04.실제 테이블(Proc) 조회
			GolfBkTimeReserveDaoProc proc = (GolfBkTimeReserveDaoProc)context.getProc("GolfBkTimeReserveDaoProc");
			int addResult = proc.execute(context, dataSet, request);	
			
			
	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	        String resultMsgTrue = "";
	        String resultMsgFalse = "";
	        
	        if (xReser.equals("1")){
	        	
	        	resultMsgTrue = "등록이 정상적으로 처리 되었습니다.";
	        	resultMsgFalse = "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.";
	        	
				if (xMsg.equals("1")){
		        	returnUrlTrue = "GolfBkNmEnd.do";
		        	returnUrlFalse = "GolfBkNm.do";
		        }else{
		        	returnUrlTrue = "GolfBkWeekEnd.do";
		        	returnUrlFalse = "GolfBkWeek.do";
		        }
	        }else{
	        	// 취소일 경우
	        	resultMsgTrue = "취소가 정상적으로 처리 되었습니다.";
	        	resultMsgFalse = "취소가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.";
	        	
				if (xMsg.equals("1")){
		        	returnUrlTrue = "GolfBkNmEnd.do";
		        	returnUrlFalse = "GolfBkNm.do";
		        }else{
		        	returnUrlTrue = "GolfBkWeekEnd.do";
		        	returnUrlFalse = "GolfBkWeek.do";
		        }
	        }
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", resultMsgTrue);      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", resultMsgFalse);		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
