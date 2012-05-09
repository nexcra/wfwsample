/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ���� > ���
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

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
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemDelDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;

import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemDelActn extends GolfActn{
	
	public static final String TITLE = "���� > ����";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String userId = "";
		String memGrade = ""; 
		int intMemGrade = 0; 
		String email_id = "";
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		
		String one_month_later = "Y";	// ����ȸ�� ���� �Ѵ� ��� ����
		String payWay = "yr"; // mn: ��ȸ��
		int money_cnt = 0;	// ���Ǽ� 
		int payCancel = 0;	// ������� ���
		int memCancelResult = 0;	// ȸ�� ���� ���� ��Ұ��
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade = (int)usrEntity.getIntMemGrade(); 
				userMobile1 	= (String)usrEntity.getMobile1();
				userMobile2 	= (String)usrEntity.getMobile2();
				userMobile3 	= (String)usrEntity.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 04.���� ���̺�(Proc) ��ȸ
			GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");		// ������� ���μ���
			GolfMemDelDaoProc proc = (GolfMemDelDaoProc)context.getProc("GolfMemDelDaoProc");			// ȸ��Ż�� ���μ���
			

			if(intMemGrade==4){
				memCancelResult = proc.execute(context, dataSet, request);
				payCancel = 1;
				debug("=====GolfMemDelActn===== ����ȸ�� Ż��  => Ż��ó�� : " + memCancelResult);
			}else{
				DbTaoResult periodResult = proc.execute_period(context, dataSet, request);

				if (periodResult != null && periodResult.isNext()) {
					periodResult.first();
					periodResult.next();
					
					if(periodResult.getString("RESULT").equals("00")){
						one_month_later = (String) periodResult.getString("ONE_MONTH_LATER");
						payWay = (String) periodResult.getString("payWay");
						debug("=====GolfMemDelActn===== one_month_later : " + one_month_later + " / payWay : " + payWay);
												
						if(one_month_later.equals("N") && "yr".equals(payWay)){
							
							// ��볻�� ��������
							DbTaoResult moneyCntResult = proc.execute_money_cnt(context, dataSet, request);
							payCancel = 1;
							
							if (moneyCntResult != null && moneyCntResult.isNext()) {
								moneyCntResult.first();
								moneyCntResult.next();

								if(moneyCntResult.getString("RESULT").equals("00")){
									money_cnt = (int) moneyCntResult.getInt("MONEY_CNT");
									
									if(money_cnt==0){										
										payCancel = proc.execute_payCancel(context, dataSet, request);
										debug("=====GolfMemDelActn===== ��� ������ ���� ��� ���� ��� ���� ��� : " + payCancel);
										
										if(payCancel>0){
											memCancelResult = proc.execute(context, dataSet, request);
											debug("=====GolfMemDelActn===== ��� ������ ���� ��� Ż�� ��� : " + memCancelResult);
										}
									}else{
										memCancelResult = proc.execute(context, dataSet, request);
										payCancel = 1;
										debug("=====GolfMemDelActn===== ��� ������ �ִ°�� Ż�� ��� : " + memCancelResult);
									}
								}
								
							}
							
						}else{
							dataSet.setString("payWay", payWay);
							memCancelResult = proc.execute(context, dataSet, request);
							payCancel = 1;	
							debug("=====GolfMemDelActn== �Ѵ� ���� ����ȸ�� Ż���� : " + memCancelResult);						
						}
					}
				}
				
			}
			
			
			String returnUrlTrue = "";
			String returnUrlFalse = "GolfMemCcForm.do";
			String script = "";
			String emailTitle = "";
			String emailFileNm = "";
			
	        if(intMemGrade==4){
	        	returnUrlTrue = "GolfMemCcGeneralEnd.do";
				emailFileNm = "/email_tpl20.html";
	        }else{
	        	returnUrlTrue = "GolfMemCcChargeEnd.do";
				emailFileNm = "/email_tpl21.html";
	        }

			debug("GolfMemDelActn // payCancel(�����������) : " + payCancel + " / memCancelResult(ȸ��Ż�� ó�����) : " + memCancelResult);
	        
			if (payCancel>0 && memCancelResult>0) {

				
				// ���� ������
				usrEntity.setMemGrade("");
				usrEntity.setIntMemGrade(0);
				usrEntity.setCyberMoney(0);

				if (!email_id.equals("")) {

					String emailAdmin = "\"��ī��\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailTitle = "[Golf Loun.G] ��������� ���񽺰� �����Ǿ����ϴ�.";
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);
				}

				request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);
				//request.setAttribute("resultMsg", "������ ó�� �Ǿ����ϴ�.");      
	       
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ó������ ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }	
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(memCancelResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
