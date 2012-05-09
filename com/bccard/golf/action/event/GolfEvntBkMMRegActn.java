/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBkMMRegActn
*   작성자    : 이포넷 은장선
*   내용      : 명문골프장 부킹 이벤트 신청 처리
*   적용범위  : golf
*   작성일자  : 2009-09-10
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfEvntBkMMRegActn extends GolfActn{
	
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
		String userId = "";
		String isLogin = "";   	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		//String reUrl = super.getActionParam(context, "reUrl");
		//String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {					
				userId		= (String)usrEntity.getAccount();   
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";				
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);		

			String hg_nm		= parser.getParameter("hg_nm", "");              //사용자이름                         
			String email		= parser.getParameter("email", "");				 //email                    
			String hp_ddd_no	= parser.getParameter("hp_ddd_no", "");			 //사용자 핸드폰 번호 010,011등등                             
			String hp_tel_hno	= parser.getParameter("hp_tel_hno", "");		 //사용자 핸드폰 번호    
			String hp_tel_sno	= parser.getParameter("hp_tel_sno", "");		 //사용자 핸드폰 번호              			           
			String teof_date	= parser.getParameter("teof_date", "");			 //신청일자    
			String teof_time	= parser.getParameter("teof_time", "");			 //신청시간                        
			String green_nm		= parser.getParameter("green_nm", "");			 //신청골프장명                         
			String memo_expl	= parser.getParameter("memo_expl", "");			 //요청사항                          
			String handy		= parser.getParameter("handy", "");				 //핸디                   
			String cnt          = parser.getParameter("pucnt","0");				 //예약 횟수                      
			String tot_cnt      = parser.getParameter("tot_cnt","0");             //총 예약가능수
			String can_cnt      = String.valueOf(Integer.parseInt(tot_cnt) - Integer.parseInt(cnt));  //잔여횟수

			paramMap.put("cnt",cnt);
			paramMap.put("tot_cnt",tot_cnt);
			paramMap.put("can_cnt",can_cnt);
			
			debug(">>>>>>>>>>>>>>>>>>    cnt : "+cnt);
			debug(">>>>>>>>>>>>>>>>>>    tot_cnt : "+tot_cnt);
			debug(">>>>>>>>>>>>>>>>>>    can_cnt : "+can_cnt);

			teof_date = GolfUtil.rplc(teof_date, "-", ""); ;
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("co_nm",hg_nm);
			dataSet.setString("email",email);
			dataSet.setString("hp_ddd_no",hp_ddd_no);
			dataSet.setString("hp_tel_hno",hp_tel_hno);
			dataSet.setString("hp_tel_sno",hp_tel_sno);			
			dataSet.setString("teof_date",teof_date);
			dataSet.setString("teof_time",teof_time);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("memo_expl",memo_expl);
			dataSet.setString("handy",handy);
			dataSet.setString("cdhd_id",userId);
			
			boolean flag  = true;
			String mess	  = "";
			int addResult = 0;			
			
			if (userId.trim().length() == 0) { flag = false; mess = "사용자ID"; };
			if (hg_nm.trim().length() == 0) { flag = false; mess = "예약자 이름"; };
			if (email.trim().length() == 0) { flag = false; mess = "E_MAIL"; };
			if (hp_ddd_no.trim().length() == 0) { flag = false; mess = "핸드폰번호"; };
			if (hp_tel_hno.trim().length() == 0) { flag = false; mess = "핸드폰번호"; };
			if (hp_tel_sno.trim().length() == 0) { flag = false; mess = "핸드폰번호"; };
			if (teof_date.trim().length() == 0) { flag = false; mess = "신청일자(날짜)"; };
			if (teof_time.trim().length() == 0) { flag = false; mess = "신청시간"; };
			if (handy.trim().length() == 0) { flag = false; mess = "핸디"; };
			if (green_nm.trim().length() == 0) { flag = false; mess = "골프장선택"; };
			if (memo_expl.trim().length() == 0) { flag = false; mess = "요청사항"; };	
			
			if (!flag){//입력값 누락 (jsp에서 필터링해도 간혹 값이 없이 넘어와 반복처리 )
				addResult = 3;				
			}	

			// 04.실제 테이블(Proc) 조회
			GolfEvntBkMMDaoProc proc = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");
			if (flag){				
				addResult = proc.doInsert(context, dataSet);			
			}
			
	        if (addResult == 1) {
				//sms발송

				// SMS 관련 셋팅
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", hg_nm);
				smsMap.put("sPhone1", hp_ddd_no);
				smsMap.put("sPhone2", hp_tel_hno);
				smsMap.put("sPhone3", hp_tel_sno);
				smsMap.put("sCallCenter", "15666578");

				debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
				String smsClss = "674";
				String message = "[Golf Loun.G] "+hg_nm+"님," + teof_date.substring(4,6) + "월" +teof_date.substring(6,8) + "일" + teof_time.substring(0,2) + "시간대 " + green_nm + " 골프장 신청 되었습니다" ;
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = "";
				if(!email.equals("msj9520")){
					smsRtn = smsProc.send(smsClss, smsMap, message);
				}
				debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);


				request.setAttribute("resultMsg", "정상적으로 신청되었습니다.");
	        } else if (addResult == 2) { //이미 신청
				request.setAttribute("resultMsg", "동일한 일자로 신청처리가 되어 계십니다. 확인후 다시 신청하시길 바랍니다.");
	        } else if (addResult == 3) { //입력값 누락 (jsp에서 필터링해도 간혹 값이 없이 넘어와 반복처리 )
				request.setAttribute("resultMsg", mess+"이(가) 누락 되었습니다. 왼쪽 메뉴 '부킹신청'을 다시 클릭하시고  신청하시길 바랍니다.\\n\\n반복적으로 발생시 관리자에 문의하십시오.");			
	        } else {
				request.setAttribute("resultMsg", "명문 골프장 이벤트 신청이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 발생시 관리자에 문의하십시오.");	      		
	        }
			request.setAttribute("returnUrl", "golfEvntMMInq.do");
				
			// 05. Return 값 세팅			
			//paramMap.put("addResult", String.valueOf(addResult));	
			
			DbTaoResult evntResult = (DbTaoResult)proc.getReserveList(context, dataSet);
			
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
