/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBkRecv9RegActn
*   작성자    : 
*   내용      : 프리미엄 부킹 이벤트 신청 처리
*   적용범위  : golf
*   작성일자  : 2009-09-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;


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
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkRecvIns9DaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfEvntBkRecv9RegActn extends GolfActn{
	
	public static final String TITLE = "9월 VIP 부킹 이벤트 신청 처리";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int myPointResult =  0;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		//String reUrl = super.getActionParam(context, "reUrl");
		//String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String golf_svc_aplc_clss = parser.getParameter("GOLF_SVC_APLC_CLSS", "0004"); //임시이벤트 번호

			String lesn_seq_no = parser.getParameter("LESN_SEQ_NO", "21"); //개발기 85 운영기 21

			String hg_nm = parser.getParameter("hg_nm", "");
			String email = parser.getParameter("email", "");
			String hp_ddd_no = parser.getParameter("hp_ddd_no", "");
			String hp_tel_hno = parser.getParameter("hp_tel_hno", "");
			String hp_tel_sno = parser.getParameter("hp_tel_sno", "");
			String teof_date = parser.getParameter("teof_date", "");
			String teof_time = parser.getParameter("teof_time", "");
			String green_nm = parser.getParameter("green_nm", "");
			String memo_expl = parser.getParameter("memo_expl", "");

			teof_date = GolfUtil.rplc(teof_date, "-", ""); ;
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);


			dataSet.setString("GOLF_SVC_APLC_CLSS", golf_svc_aplc_clss);		
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("CO_NM", hg_nm);
			dataSet.setString("EMAIL", email);
			dataSet.setString("HP_DDD_NO", hp_ddd_no);
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			dataSet.setString("TEOF_DATE", teof_date);
			dataSet.setString("TEOF_TIME", teof_time);
			dataSet.setString("GREEN_NM", green_nm);
			dataSet.setString("MEMO_EXPL", memo_expl);
			dataSet.setString("LESN_SEQ_NO", lesn_seq_no);

			
			// 04.실제 테이블(Proc) 조회
			
			GolfEvntBkRecvIns9DaoProc proc = (GolfEvntBkRecvIns9DaoProc)context.getProc("GolfEvntBkRecvIns9DaoProc");
			int addResult = proc.execute(context, dataSet);

			DbTaoResult evntResult = (DbTaoResult)proc.getResult(context, dataSet);
			
	        if (addResult == 1) {
				request.setAttribute("resultMsg", "1");
				//sms발송

				// SMS 관련 셋팅
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", hg_nm);
				smsMap.put("sPhone1", hp_ddd_no);
				smsMap.put("sPhone2", hp_tel_hno);
				smsMap.put("sPhone3", hp_tel_sno);
				smsMap.put("sCallCenter", "15666578");
				
				//debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
				String smsClss = "674";
				String message = "[Golf Loun.G] "+hg_nm+"님 "+green_nm+ ","+ teof_date.substring(4,6) + "월" +teof_date.substring(6,8) + "일 " + teof_time.substring(0,2) + "시대 VIP부킹 이벤트 신청하셨습니다." ;
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = "";
				if(!email.equals("msj9520")){
					smsRtn = smsProc.send(smsClss, smsMap, message);
				}
				//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);


	        } else if (addResult == 2) { //이미 신청
				request.setAttribute("resultMsg", "2");
	        } else {
				request.setAttribute("resultMsg", "프리미엄 부킹 이벤트 신청이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));		
			
			request.setAttribute("evntResult", evntResult);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
