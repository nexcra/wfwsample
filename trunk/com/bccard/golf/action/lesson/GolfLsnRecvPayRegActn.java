/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLessonRegActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨신청 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.lesson;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.lesson.GolfLessonInsDaoProc;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.msg.MsgEtt;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.SmsSendProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfLsnRecvPayRegActn extends GolfActn{
	
	public static final String TITLE = "레슨신청 처리";
	private static final String GO_URL = "";
	private static final String GO_BTN = "";

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
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
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
			paramMap.put("userNm", userNm);

debug("+1+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
			
			// STEP 1. 입력값에 대한 세션체크
			String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
			if ( st_s == null ) st_s = "";
			request.getSession().removeAttribute("ParameterManipulationProtectKey");

			String st_p = request.getParameter("ParameterManipulationProtectKey");
			if ( st_p == null ) st_p = "";

			if ( !st_p.equals(st_s) ) {
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
			//	throw new EtaxException(msgEtt);
			}			
				
			// 공통 
			String ip = request.getRemoteAddr();  
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD2");		// 가맹점 번호(766559864) //topn : 745300778
			String iniplug = parser.getParameter("KVPpluginData", "");	// ISP 인증값
			String sum		 = parser.getParameter("realPayAmt", "0");	// 결제금액
			if(sum != null && !"".equals(sum)){
				sum = StrUtil.replace(sum,",","");
			}

			String cardNo		= parser.getParameter("card_no", "0");				// 카드번호
			String insTerm		= parser.getParameter("ins_term", "00");			// 할부개월수
			String siteType		= parser.getParameter("site_type", "1");			// 사이트 구분 1: 비씨, 2:지자체	
			
			// STEP 1_2. 파라미터 입력
			HashMap kvpMap = null;
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}			

			// STEP 1_3. 공인인증값이 있을 경우 유효성 검사..
			String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// 사용자 아이디
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// 사인값
			String pcg         = "";														// 개인/법인 구분
			String ispCardNo   = "";														// isp카드번호
			String valdlim	   = "";														// 만료 일자
			String pid = null;																// 개인아이디

			if(kvpMap != null) {
				pcg         = (String)kvpMap.get("PersonCorpGubun");		// 개인/법인 구분
				ispCardNo   = (String)kvpMap.get("CardNo");					// isp카드번호
				valdlim		= (String)kvpMap.get("CardExpire");				// 만료 일자
				if ( "2".equals(pcg) ) {
					pid = (String)kvpMap.get("BizId");								// 사업자번호
				} else {
					pid = (String)kvpMap.get("Pid");									// 개인 주민번호
				}
			} else {
				ispCardNo = 	parser.getParameter("isp_card_no","");	// 하나비자카드 경우
			}
			
			if ( valdlim.length() == 6 ) {
				valdlim = valdlim.substring(2);											
			}
			// STEP 5. 승인처리
			payEtt.setMerMgmtNo(merMgmtNo);
			payEtt.setCardNo(ispCardNo);
			payEtt.setValid(valdlim);			
			payEtt.setAmount(sum);
			payEtt.setInsTerm(insTerm);
			payEtt.setRemoteAddr(ip);

			boolean payResult = false;
			payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
					
debug("+4++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");			

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
			String nowDate = String.valueOf(cal.get(Calendar.DATE));
			String nowDay = nowYear +"년 "+ nowMonth +"월 "+ nowDate +"일";
	
			long recv_no	= parser.getLongParameter("p_idx", 0L);// 레슨일렬번호
			String lsn_type_cd = parser.getParameter("slsn_type_cd", "");
			String sex = parser.getParameter("sex", "");	// 성별
			String email_id = parser.getParameter("email_id", "");	// E-mail
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// 전화ddd번호
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// 전화국번호
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// 전화일련번호		
			String tel_no = chg_ddd_no+"-"+chg_tel_hno+"-"+chg_tel_sno;
			String lsn_expc_clss = parser.getParameter("lsn_expc_clss", "");	// 골프경험코드
			String mttr = parser.getParameter("mttr", "");	// 특이사항
			String lsn_nm = parser.getParameter("lsn_nm", "");	// 레슨명
			// 이메일에서 사용
			String phone = chg_ddd_no + chg_tel_hno + chg_tel_sno;
			String lsn_expc_clss_nm = "";
			if (lsn_expc_clss.equals("0001")) lsn_expc_clss_nm="하 (처음 골프를 접함)";
			if (lsn_expc_clss.equals("0002")) lsn_expc_clss_nm="중 (레슨 경험 있음)";
			if (lsn_expc_clss.equals("0003")) lsn_expc_clss_nm="상 (골프 경험 다수)";
			String sex_nm = "";
			if (sex.equals("F")) sex_nm="여";
			if (sex.equals("M")) sex_nm="남";
			// 이메일에서 사용

			String lsn_seq_type = nowYear+"G";
			if (lsn_type_cd.equals("0002")) lsn_seq_type =  nowYear+"S";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CSTMR_ID", userId);
			dataSet.setString("LSN_SEQ_TYPE", lsn_seq_type);			
			dataSet.setLong("RECV_NO", recv_no);
			dataSet.setString("LSN_TYPE_CD", lsn_type_cd);
			dataSet.setString("SEX", sex);
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("LSN_EXPC_CLSS", lsn_expc_clss);
			dataSet.setString("MTTR", mttr);
			dataSet.setString("STTL_AMT", sum);


			// 04.실제 테이블(Proc) 조회
			GolfLessonInsDaoProc proc = (GolfLessonInsDaoProc)context.getProc("GolfLessonInsDaoProc");
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");

			int addResult = 0;
			String aplc_seq_no = "";

			// 결제 승인 완료
			if (payResult) { // 꼭~~ 수정해야 할 사항
				// 레슨 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				addResult = proc.execute(context, dataSet);
				aplc_seq_no = proc.getMaxSeqNo(context, dataSet);				

				// 결제 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				String sttl_mthd_clss = "";
				if (insTerm.equals("00")) sttl_mthd_clss="0001";
				else sttl_mthd_clss="0002";
				
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", "0006");
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("STTL_AMT", sum);
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());
				dataSet.setString("AUTH_NO", payEtt.getUseNo());
				dataSet.setString("STTL_GDS_SEQ_NO", aplc_seq_no);

				if (addResult == 1) {
					// 결제 저장 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = addResult + addPayProc.execute(context, dataSet);
				}
			}			

			HashMap smsMap = new HashMap();
			
			smsMap.put("ip", request.getRemoteAddr());
			smsMap.put("sName", userNm);
			smsMap.put("sPhone1", chg_ddd_no);
			smsMap.put("sPhone2", chg_tel_hno);
			smsMap.put("sPhone3", chg_tel_sno);
			
			boolean payCancelResult = false;
	        if (addResult == 2) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "");	

				// 메일발송
//				if (!email_id.equals("")) {
//					String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
//					String emailTitle = userNm +"님 특별한 레슨 결제가 완료되었습니다.";
//					String emailFileNm = "/email_tpl11.html";
//					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
//					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
//										
//					EmailSend sender = new EmailSend();
//					EmailEntity emailEtt = new EmailEntity("EUC_KR");
//					
//					emailEtt.setFrom(emailAdmin);
//					emailEtt.setSubject(emailTitle);
//					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+nowDay+"|"+lsn_nm+"|"+userNm+"|"+sex_nm+"|"+tel_no+"|"+email_id+"|"+lsn_expc_clss_nm+"|"+mttr+"|"+nowDay+"|"+sum);
//					emailEtt.setTo(email_id);
//					sender.send(emailEtt);
//				}
				
				//sms발송
				if (!phone.equals("")) {
					
					debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "647";
					String message = "[특별한레슨]"+userNm+"님 "+lsn_nm+" "+sum+"원 결제완료";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}	
	        } else if (addResult == 9) { //한번 더 체크함
	        	// DB저장 실패시 승인취소 전문	        	
				payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
	        	
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "이미 신청하셨습니다.");
	        } else {
	        	// DB저장 실패시 승인취소 전문
				payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
				
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "레슨신청이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }			        
			        
			// 05. Return 값 세팅			
			paramMap.put("aplc_seq_no", aplc_seq_no);		
			paramMap.put("editResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
}
