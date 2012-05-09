/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpRecvPayCnclActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 특별한 레슨이벤트 결제 취소
*   적용범위	: golf
*   작성일자	: 2009-07-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.special.myprize;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.special.myprize.GolfEvntSpMyprizeUpdDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentInqDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentUpdDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfEvntSpRecvPayCnclActn extends GolfActn{
		
		public static final String TITLE = "레슨신청 결제 취소";

		/***************************************************************************************
		* 골프 사용자화면
		* @param context		WaContext 객체. 
		* @param request		HttpServletRequest 객체. 
		* @param response		HttpServletResponse 객체. 
		* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
		***************************************************************************************/
		
		public ActionResponse  execute(WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

			String subpage_key = "default";	
			String userNm = ""; 
			String memClss ="";
			String userId = "";
			String isLogin = ""; 
			String juminno = ""; 
			String memGrade = ""; 
			int intMemGrade = 0; 
			int myPointResult =  0;
			String resultMsg = "";


			
		    
			// 00.레이아웃 URL 저장

			GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
			
			try {
				// 01.세션정보체크
				HttpSession session = request.getSession(true);
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
				paramMap.put("userId", userId);
				paramMap.put("userNm", userNm);
				

				// Request 값 저장
				String p_idx	= parser.getParameter("p_idx", "");
				String odr_no	= parser.getParameter("odr_no", "");
				String lesn_nm	= parser.getParameter("evnt_nm", "");
				String mobile1  = parser.getParameter("hp_ddd_no", "");
				String mobile2  = parser.getParameter("hp_tel_hno", "");
				String mobile3  = parser.getParameter("hp_tel_sno", "");
				
				String mobile = mobile1 + mobile2 + mobile3;
				
				// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("ODR_NO", odr_no);
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("p_idx",p_idx);
				dataSet.setString("mode","cncl");
				
				
				// 04.실제 테이블(Proc) 조회
				GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
				GolfPaymentInqDaoProc inqProc = (GolfPaymentInqDaoProc)context.getProc("GolfPaymentInqDaoProc");
				GolfPaymentUpdDaoProc updProc = (GolfPaymentUpdDaoProc)context.getProc("GolfPaymentUpdDaoProc");
				
				//처리
				GolfEvntSpMyprizeUpdDaoProc proc = (GolfEvntSpMyprizeUpdDaoProc)context.getProc("GolfEvntSpMyprizeUpdDaoProc");

				DbTaoResult payInfoResult = (DbTaoResult) inqProc.getPaymentInfo(context, dataSet);
				if (payInfoResult != null && payInfoResult.isNext()) {
					payInfoResult.first();
					payInfoResult.next();
					if (payInfoResult.getObject("RESULT").equals("00")) {
						payEtt.setMerMgmtNo((String)payInfoResult.getString("MER_NO"));
						payEtt.setCardNo((String)payInfoResult.getString("CARD_NO"));
						payEtt.setValid((String)payInfoResult.getString("VALD_DATE").trim());
						payEtt.setAmount((String)payInfoResult.getString("STTL_AMT"));					
						String insTerm = (String)payInfoResult.getString("INS_MCNT");
						if (insTerm.length() == 1) insTerm = "0"+insTerm;
						payEtt.setInsTerm(insTerm);
						payEtt.setUseNo((String)payInfoResult.getString("AUTH_NO"));
						payEtt.setRemoteAddr(request.getRemoteAddr());
					}
				}

				debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");		
				debug("=====> MerMgmtNo : " + payEtt.getMerMgmtNo());
				debug("=====> CardNo : " + payEtt.getCardNo());
				debug("=====> Valid : " + payEtt.getValid());
				debug("=====> Amount : " + payEtt.getAmount());
				debug("=====> InsTerm : " + payEtt.getInsTerm());
				debug("=====> UseNo : " + payEtt.getUseNo());
				debug("=====> RemoteAddr : " + payEtt.getRemoteAddr());
				debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				

				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", mobile1);
				smsMap.put("sPhone2", mobile2);
				smsMap.put("sPhone3", mobile3);
				
				boolean payCancelResult = false;
				payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
				
				int cnclResult = 0;
				// 결제 승인 완료
				if (payCancelResult) { // 꼭~~ 수정해야 할 사항
					cnclResult = updProc.execute(context, dataSet);		
				}	
				
		        if (cnclResult == 1) {
		        	
					//request.setAttribute("returnUrl", reUrl);
					//request.setAttribute("resultMsg", "");
					DbTaoResult resultCancel = (DbTaoResult)proc.execute(context, request, dataSet);
					if(resultCancel.isNext()){
						resultCancel.next();
						if("00".equals(resultCancel.getString("RESULT"))){
							resultMsg = "취소승인이 정상처리 되었습니다.";
						}
					}
					
					
					//sms발송
					if (!mobile.equals("")) {
						
						debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "647";
						String message = "[특별한레슨이벤트]"+userNm+"님 "+lesn_nm+" "+payEtt.getAmount()+"원 결제취소 - Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}	
		        } else {				
					//request.setAttribute("returnUrl", errReUrl);
		        	resultMsg = "취소승인이 실패하였습니다.";	        		
		        }	
				
				// 05. Return 값 세팅
		        paramMap.put("evnt_nm",		lesn_nm);
		        paramMap.put("resultMsg", 	resultMsg);
		        paramMap.put("mode",		"payCncl");
		        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
				
			} catch(Throwable t) {
				debug(TITLE, t);
				//t.printStackTrace();
				throw new GolfException(TITLE, t);
			} 
			
			return super.getActionResponse(context, subpage_key);
			
		}
}
