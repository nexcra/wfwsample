/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntKvpPopActn 
*   작성자	: (주)미디어포스 임은혜
*   내용		: KVP event
*   적용범위	: golf
*   작성일자	: 2010-05-20
*   note :  http://www.golfloung.com/app/golfloung/view/golf/member/ktOlleh/kt_olleh.jsp?serviceFlag=ollehClubGolf
*   		http://develop.golfloung.com:13300/app/golfloung/view/golf/member/ktOlleh/kt_olleh.jsp?serviceFlag=ollehClubGolf
*           테스트시 serviceFlag 에 아무값이나 할당
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.kvp;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntGolfShowPopDaoProc;
import com.bccard.golf.dbtao.proc.event.kvp.GolfEvntKvpPopDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntKvpPopActn extends GolfActn{
	
	public static final String TITLE = "KVP event";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		boolean ktTrue = false;
				
		try {
				
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String idx = parser.getParameter("idx", "");
			String flag = parser.getParameter("flag", "");
			String serviceFlag = parser.getParameter("serviceFlag", "");
			String ollehKtValue = parser.getParameter("ollehKtValue", "");
			String currentBirthDate = parser.getParameter("currentBirthDate", "");
			String firstFlag = parser.getParameter("firstFlag", "");
			String pay = parser.getParameter("pay", "");
			String order_no = parser.getParameter("allat_order_no", "");

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			if (pay.equals("Y")){				
				GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
				order_no = addPayProc.getOrderNo(context, dataSet);				
			}
			
			paramMap.put("order_no", order_no); 	
			
			debug (" #### firstFlag : " + firstFlag + ", serviceFlag : " + serviceFlag + ", ollehKtValue : " + ollehKtValue + ", currentBirthDate : " + currentBirthDate + ", len : " + currentBirthDate.trim().length());
			
			if (firstFlag.equals("Y")){
				
				//기존 올레클럽회원 
				if ( serviceFlag != null && !serviceFlag.equals("null")){
				
					if (serviceFlag.trim().equals("ollehClubGolf")){						
						ktTrue  = true;
					}
					
				}
					
				//생일자(superstar 회원)관련				
				if ( ollehKtValue != null && !ollehKtValue.equals("null")){	
	
					// R : 올레클럽 회원 + 생일자(슈퍼스타), M : 올레클럽 미회원 + 생일자(슈퍼스타)
					if ( currentBirthDate.trim().length() == 8 && (ollehKtValue.trim().equals("R") || ollehKtValue.trim().equals("M")) ){
						ktTrue  = true; 
					}			
					
				}
				
			}else {
				ktTrue  = true;
			}	
			
			if (ktTrue){				
			
				if (firstFlag.equals("Y")){
					subpage_key = "memdis";		
				}				
	
				// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)					
				dataSet.setString("idx", idx);
				
				GolfEvntKvpPopDaoProc proc = (GolfEvntKvpPopDaoProc)context.getProc("GolfEvntKvpPopDaoProc");
				DbTaoResult resultKvpPop = proc.execute(context, request, dataSet);	
				
				paramMap.put("idx", idx);
			
				String gds_code 				= parser.getParameter("gds_code", "");
				String name 					= parser.getParameter("name", "");
				String zp1 						= parser.getParameter("zp1", "");
				String zp2 						= parser.getParameter("zp2", "");
				String zipaddr 					= parser.getParameter("addr", "");
				String detailaddr 				= parser.getParameter("dtl_addr", "");
				String addr_clss 				= parser.getParameter("addr_clss", "");
				String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "");
				String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "");
				String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "");
				String gds_code_name 			= parser.getParameter("gds_code_name", "");
				String formtarget 				= parser.getParameter("formtarget", "");
						
				String realPayAmt 				= parser.getParameter("realPayAmt", "");
				String social_id_1 				= parser.getParameter("social_id_1", "");
				String social_id_2 				= parser.getParameter("social_id_2", "");			
				String email 					= parser.getParameter("email", "");
				String ddd_no 					= parser.getParameter("ddd_no", "");
				String tel_hno 					= parser.getParameter("tel_hno", "");
				String tel_sno 					= parser.getParameter("tel_sno", "");
	
				paramMap.put("gds_code", gds_code);
				paramMap.put("name", name);
				paramMap.put("zp1", zp1);
				paramMap.put("zp2", zp2);
				paramMap.put("zipaddr", zipaddr);
				paramMap.put("detailaddr", detailaddr);
				paramMap.put("addr_clss", addr_clss);
				paramMap.put("hp_ddd_no", hp_ddd_no);
				paramMap.put("hp_tel_hno", hp_tel_hno);
				paramMap.put("hp_tel_sno", hp_tel_sno);
				paramMap.put("gds_code_name", gds_code_name);				
				paramMap.put("formtarget", formtarget);
				
				paramMap.put("realPayAmt", realPayAmt);
				paramMap.put("social_id_1", social_id_1);
				paramMap.put("social_id_2", social_id_2);
				paramMap.put("socid", social_id_1 + social_id_2);
				paramMap.put("email", email);
				paramMap.put("ddd_no", ddd_no);
				paramMap.put("tel_hno", tel_hno);
				paramMap.put("tel_sno", tel_sno);
				paramMap.put("serviceFlag", serviceFlag);
				paramMap.put("ollehKtValue", ollehKtValue);
				paramMap.put("currentBirthDate", currentBirthDate);
								
		        request.setAttribute("resultKvpPop", resultKvpPop);		
		        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
		        
			}else {
				
				subpage_key = "fault";				
				String script = "";
				
				if ( !serviceFlag.trim().equals("ollehClubGolf")
						&& (ollehKtValue.trim().length() == 0) ) {
					script += "alert('KT Olleh Club 회원이 아닙니다.');";				
					script += "parent.top.window.close();";
				}				

				
				if ( serviceFlag.trim().length() == 0
						&& ( currentBirthDate.trim().length() != 8 || !(ollehKtValue.trim().equals("R") || ollehKtValue.trim().equals("M") ) ) )
				{
					script += "alert('SuperStar 회원이 아닙니다.');";				
					script += "parent.top.window.close();";
				}						
								
				request.setAttribute("script", script);				
		        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.				
		        
			}
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
