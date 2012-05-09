/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLsnRecvPayCnclActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������û ���� ���
*   �������  : golf
*   �ۼ�����  : 2009-06-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mania;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentUpdDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentInqDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfManiaPayCnclActn extends GolfActn{
	
	public static final String TITLE = "������û ���� ���";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
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
		
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";

		String mobile = mobile1 + mobile2 + mobile3;
	    
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

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
				mobile1 = (String)usrEntity.getMobile1();
				mobile2 = (String)usrEntity.getMobile2();
				mobile3 = (String)usrEntity.getMobile3();
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
			String odr_no			= parser.getParameter("odr_no", "");
			String lesn_nm	= parser.getParameter("lesn_nm", "");

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ODR_NO", odr_no);
			dataSet.setString("CDHD_ID", userId);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
			GolfPaymentInqDaoProc inqProc = (GolfPaymentInqDaoProc)context.getProc("GolfPaymentInqDaoProc");
			GolfPaymentUpdDaoProc updProc = (GolfPaymentUpdDaoProc)context.getProc("GolfPaymentUpdDaoProc");

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
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "");

				//sms�߼�
				if (!mobile.equals("")) {
					
					debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "647";
					String message = "[����������]"+userNm+"�� "+lesn_nm+" "+payEtt.getAmount()+"�� �������";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}	
	        } else {				
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "���� ��Ұ� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }	
			
			// 05. Return �� ����
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
