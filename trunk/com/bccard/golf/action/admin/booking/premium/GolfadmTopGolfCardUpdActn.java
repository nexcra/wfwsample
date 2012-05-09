/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfCardUpdActn.java
*   작성자    : 이정규
*   내용      : 관리자 > 부킹 > 탑골프카드전용 부킹 수정
*   적용범위  : Golf
*   작성일자  : 2010-10-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*	2010-11-03
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfCardDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	이포넷 은장선  
* @version	1.0
******************************************************************************/
public class GolfadmTopGolfCardUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 부킹 > 탑골프카드전용 부킹 수정";

	/***************************************************************************************
	* 골프 관리자화면
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
			// 01.세션정보체크

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request 값 저장
			String green_nm				= parser.getParameter("GREEN_NM","");           //신청골프장명            
			String aplc_seq_no          = parser.getParameter("aplc_seq_no","");        //예약번호
			String pgrs_yn				= parser.getParameter("PGRS_YN","");    //상태
			String teof_date              = parser.getParameter("TEOF_DATE","");            //부킹일자
			String teof_time              = parser.getParameter("TEOF_TIME","");            //부킹시간
			String chng_aton              = parser.getParameter("CHNG_ATON","");            //확정시간
			String temp_aton = "";
			String temp_time = "";
			
			String golf_lesn_rsvt_no = parser.getParameter("golf_lesn_rsvt_no",""); 	//티타음 등록번호
			
			//대상관리에서 넘어온건지 확인
			String type             	 = parser.getParameter("type","");
			
			
			if(pgrs_yn.equals("B") && !chng_aton.equals("")){
				temp_aton = chng_aton.substring(0,2) + ":"+ chng_aton.substring(2,4);
			}
			if(teof_time.length() >= 4){
				temp_time = teof_time.substring(0,2) + ":"+ teof_time.substring(2,4);
			}
			
			String userNm = parser.getParameter("u_name","");            //이름
			String userId = parser.getParameter("u_id","");            //id
			
			String hp_DDD_NO = parser.getParameter("hp_no1","");            //핸폰1
			String hp_TEL_HNO = parser.getParameter("hp_no2","");            //핸폰2
			String hp_TEL_SNO = parser.getParameter("hp_no3","");            //핸폰3
			String email = parser.getParameter("email","");					//email
			
			GregorianCalendar today = new GregorianCalendar ( );
	        int nYear = today.get ( today.YEAR );
	        int nMonth = today.get ( today.MONTH ) + 1;
	        int nDay = today.get ( today.DAY_OF_MONTH ); 
	        String strToday = nYear+"년 "+nMonth+"월 "+nDay+"일";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("aplc_seq_no",aplc_seq_no);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("pgrs_yn",pgrs_yn);
			dataSet.setString("teof_date",teof_date);
			dataSet.setString("teof_time",teof_time);
			dataSet.setString("chng_aton",chng_aton);
			
			dataSet.setString("golf_lesn_rsvt_no",golf_lesn_rsvt_no);
			
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmTopGolfCardDaoProc proc = (GolfAdmTopGolfCardDaoProc)context.getProc("GolfAdmTopGolfCardDaoProc");
			
			int evntUpd = proc.execute_update(context, request, dataSet);
			int ttimeUpd = 0;
			
			
			if("B".equals(pgrs_yn)){
				ttimeUpd = proc.execute_epsYn(context, request, dataSet);
			}
			
	        if (evntUpd == 1 ) {
	        	request.setAttribute("returnUrl", "admTopGolfCardList.do");
				request.setAttribute("resultMsg", "부킹예약 상세정보가 수정 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", "admTopGolfCardView.do");
				request.setAttribute("resultMsg", "부킹예약 상세정보가 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
	        
	        if("2".equals(type))
	        {
	        	 if (evntUpd == 1) {
	 				request.setAttribute("returnUrl", "admTopGolfTargetPpList.do");
	 				request.setAttribute("resultMsg", "부킹예약 상세정보가 수정 되었습니다.");      	
	 	        } else {
	 				request.setAttribute("returnUrl", "admTopGolfTargetPpView.do");
	 				request.setAttribute("resultMsg", "부킹예약 상세정보 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	 	        }
	        }
	
	        request.setAttribute("paramMap", paramMap);

			paramMap.put("green_nm",green_nm);			
			paramMap.put("aplc_seq_no",aplc_seq_no);
	        request.setAttribute("paramMap", paramMap);
	        
	        
	        if(pgrs_yn.equals("B")){
				// SMS 관련 셋팅
	        	try {
				HashMap smsMap = new HashMap(); 
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", hp_DDD_NO);
				smsMap.put("sPhone2", hp_TEL_HNO);
				smsMap.put("sPhone3", hp_TEL_SNO);
				
				String smsClss = "637";
				String message = "[Golf Loun.G] "+userNm+"님,"+green_nm+ " "+teof_date+" "+temp_aton +" 부킹확정되었습니다";
				//String message = "[VIP부킹] "+userNm+"님 "+gl_green_nm+" "+course+" "+bk_DATE+" "+bkps_TIME+":"+bkps_MINUTE+" 예약완료- Golf Loun.G";
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = smsProc.send(smsClss, smsMap, message);
	        	} catch(Throwable t) {}
				// 이메일 보내기
				if(!email.equals("")){
					try {
					String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\"";
					String hrefPath = "<a href=\"";
					String emailTitle = "";
					String emailFileNm = "";
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailTitle = "[Golf Loun.G] TOP골프카드 전용부킹 확정 안내";
					emailFileNm = "/email_tpl28.html";						
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+userId+"|"+green_nm + "|" + teof_date + "|" + temp_aton + "|" + strToday);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle); 
					emailEtt.setTo(email);
					sender.send(emailEtt);
					} catch(Throwable t) {}
				}
			
			}else if(pgrs_yn.equals("F")){
				// SMS 관련 셋팅
				try {
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", hp_DDD_NO);
				smsMap.put("sPhone2", hp_TEL_HNO);
				smsMap.put("sPhone3", hp_TEL_SNO);
				
				String smsClss = "637";
				String message = "[Golf Loun.G] "+userNm+"님,"+green_nm+ " "+teof_date+" "+temp_time +" 실패 되었습니다.";
				//String message = "[VIP부킹] "+userNm+"님 "+gl_green_nm+" "+course+" "+bk_DATE+" "+bkps_TIME+":"+bkps_MINUTE+" 예약완료- Golf Loun.G";
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = smsProc.send(smsClss, smsMap, message);
			} catch(Throwable t) {}
			}
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
