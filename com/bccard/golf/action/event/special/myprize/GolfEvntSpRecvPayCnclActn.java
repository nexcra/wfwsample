/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntSpRecvPayCnclActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: Ư���� �����̺�Ʈ ���� ���
*   �������	: golf
*   �ۼ�����	: 2009-07-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfEvntSpRecvPayCnclActn extends GolfActn{
		
		public static final String TITLE = "������û ���� ���";

		/***************************************************************************************
		* ���� �����ȭ��
		* @param context		WaContext ��ü. 
		* @param request		HttpServletRequest ��ü. 
		* @param response		HttpServletResponse ��ü. 
		* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
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


			
		    
			// 00.���̾ƿ� URL ����

			GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
			
			try {
				// 01.��������üũ
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
				
				// 02.�Է°� ��ȸ		
				RequestParser parser = context.getRequestParser(subpage_key, request, response);
				Map paramMap = BaseAction.getParamToMap(request);
				paramMap.put("title", TITLE);
				paramMap.put("userId", userId);
				paramMap.put("userNm", userNm);
				

				// Request �� ����
				String p_idx	= parser.getParameter("p_idx", "");
				String odr_no	= parser.getParameter("odr_no", "");
				String lesn_nm	= parser.getParameter("evnt_nm", "");
				String mobile1  = parser.getParameter("hp_ddd_no", "");
				String mobile2  = parser.getParameter("hp_tel_hno", "");
				String mobile3  = parser.getParameter("hp_tel_sno", "");
				
				String mobile = mobile1 + mobile2 + mobile3;
				
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("ODR_NO", odr_no);
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("p_idx",p_idx);
				dataSet.setString("mode","cncl");
				
				
				// 04.���� ���̺�(Proc) ��ȸ
				GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
				GolfPaymentInqDaoProc inqProc = (GolfPaymentInqDaoProc)context.getProc("GolfPaymentInqDaoProc");
				GolfPaymentUpdDaoProc updProc = (GolfPaymentUpdDaoProc)context.getProc("GolfPaymentUpdDaoProc");
				
				//ó��
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
				payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
				
				int cnclResult = 0;
				// ���� ���� �Ϸ�
				if (payCancelResult) { // ��~~ �����ؾ� �� ����
					cnclResult = updProc.execute(context, dataSet);		
				}	
				
		        if (cnclResult == 1) {
		        	
					//request.setAttribute("returnUrl", reUrl);
					//request.setAttribute("resultMsg", "");
					DbTaoResult resultCancel = (DbTaoResult)proc.execute(context, request, dataSet);
					if(resultCancel.isNext()){
						resultCancel.next();
						if("00".equals(resultCancel.getString("RESULT"))){
							resultMsg = "��ҽ����� ����ó�� �Ǿ����ϴ�.";
						}
					}
					
					
					//sms�߼�
					if (!mobile.equals("")) {
						
						debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "647";
						String message = "[Ư���ѷ����̺�Ʈ]"+userNm+"�� "+lesn_nm+" "+payEtt.getAmount()+"�� ������� - Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}	
		        } else {				
					//request.setAttribute("returnUrl", errReUrl);
		        	resultMsg = "��ҽ����� �����Ͽ����ϴ�.";	        		
		        }	
				
				// 05. Return �� ����
		        paramMap.put("evnt_nm",		lesn_nm);
		        paramMap.put("resultMsg", 	resultMsg);
		        paramMap.put("mode",		"payCncl");
		        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
				
			} catch(Throwable t) {
				debug(TITLE, t);
				//t.printStackTrace();
				throw new GolfException(TITLE, t);
			} 
			
			return super.getActionResponse(context, subpage_key);
			
		}
}
