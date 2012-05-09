/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemTmInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ���� > ���ó�� > TM
*   �������  : golf 
*   �ۼ�����  : 2009-07-29
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100601	������	 DM ��ȭ�̺�Ʈ �˾�â ������ ���� 
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;
import com.bccard.golf.dbtao.proc.event.tmMovie.GolfEvntTmMovieProc;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemTmInsActn extends GolfActn{
	
	public static final String TITLE = "���� > TM ���";

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
		String join_chnl = "";
		int addResult = 0;
		String userSocid = "";
		int ibkResult = 0;		// ibk �Ⱓ���� ���
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userSocid 	= (String)usrEntity.getSocid();
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
						
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMemTmInsDaoProc proc = (GolfMemTmInsDaoProc)context.getProc("GolfMemTmInsDaoProc");
			//int addResult = proc.execute(context, dataSet, request);	
			DbTaoResult tmView = proc.execute(context, dataSet, request);
			
			if (tmView != null && tmView.isNext()) {
				tmView.first();
				tmView.next();
				addResult =  (int) tmView.getInt("addResult");
				intMemGrade = (int) tmView.getInt("intMemGrade");	
				memGrade = (String) tmView.getString("memGrade");	
				join_chnl = (String) tmView.getString("joinChnl");			
				
				// ���ȸ���� ��� ���ñⰣ�� 2�� �÷��ش�.
				ibkResult = proc.execute_ibk(context, dataSet, request);
				if(ibkResult>0){

					HashMap smsMap = new HashMap();
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", usrEntity.getName());
					smsMap.put("sPhone1", usrEntity.getMobile1());
					smsMap.put("sPhone2", usrEntity.getMobile2());
					smsMap.put("sPhone3", usrEntity.getMobile3());

					SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");   
					GregorianCalendar cal = new GregorianCalendar();
			        cal.add(cal.MONTH, 14);
			        Date edDate = cal.getTime();
			        String strEdDate = fmt.format(edDate);	// ����ȸ���Ⱓ ������
			        
					String smsClss = "674";
		        	String message = "IBK�¼��۷��̵� �̺�Ʈ�� ���ᰡ�����ڰ� "+strEdDate+"���� 2��������Ǿ����ϴ�.";

					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					
				}
			}	
			debug("GolfMemTmIns :: memGrade : " + memGrade + " / intMemGrade : " + intMemGrade);

        	String returnUrlTrue = "GolfMemJoinEnd.do";
        	String returnUrlFalse =  "GolfMemJoinNoCard.do";
        	String script = "parent.location.href='/app/golfloung/html/common/member_join_finish.jsp'";

			if (addResult == 1) {

				usrEntity.setMemGrade(memGrade);
				usrEntity.setIntMemGrade((int)intMemGrade);
				usrEntity.setCyberMoney(0);

				if (!email_id.equals("")) {

					String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String emailTitle = "";
					String emailFileNm = "";
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailTitle = "[Golf Loun.G] ��������� ���� ������ ���ϵ帳�ϴ�.";
					emailFileNm = "/email_tpl19.html";
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+memGrade);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);  
				}
				
				//�̺�Ʈ ���� �߱�
/*
				debug("##################################TM �̺�Ʈ ó�� ����##############################");

				String from_date = "";
				String to_date   = "";
				String cupn      = ""; 
				String currDate  = DateUtil.currdate("yyyyMMdd");

				dataSet.setString("evnt_no","110");
				GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
				DbTaoResult evntInterpark = (DbTaoResult) inter.eventDateCheck(context, request, dataSet);
				
				if (evntInterpark != null && evntInterpark.isNext()) {
					evntInterpark.first(); 
					evntInterpark.next();
					if(evntInterpark.getString("RESULT").equals("00")){
						from_date = evntInterpark.getString("FROM_DATE");
						to_date = evntInterpark.getString("TO_DATE");
						debug("from_date ~ to_date >>>>>>>>>>>>>>>" + from_date + "~" + to_date + ",���糯¥ : "+ currDate);
					}
				}

				if((Integer.parseInt(from_date) <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt(to_date))){
					debug("�Ⱓ���� ���� �̺�Ʈ ������");
					boolean doUpdate = false;
					int cnt = 1;
					synchronized(this) {	// ���� ���� �߻��� ���� max �� �����°� ����
						DbTaoResult cupnInterpark = (DbTaoResult) inter.cupnNumber(context, request, dataSet);
						if (cupnInterpark != null && cupnInterpark.isNext()) {
							cupnInterpark.first();
							cupnInterpark.next();
							if(cupnInterpark.getString("RESULT").equals("00")){
								cupn = cupnInterpark.getString("CUPN");
								debug("������ȣ >>>>>>>>>>>>>>>>>>>>>>>>>" + cupn);
							}else if(cupnInterpark.getString("RESULT").equals("01")){
								request.getSession().removeAttribute("isInterpark");
								request.setAttribute("script", script);
								request.setAttribute("returnUrl", returnUrlTrue);
								return super.getActionResponse(context, subpage_key);
							}
						}
						dataSet.setString("email"	, email_id);
						dataSet.setString("socid"	, usrEntity.getSocid());
						dataSet.setString("cupn"	, cupn);
						dataSet.setString("userNm"	, userNm);
						dataSet.setString("ea_info"	, "");

						cnt = (int)inter.getDplCheck(context, request, dataSet);

						//Thread.sleep(5000);

						if(cnt == 0){
							debug("TM �̺�Ʈ���� �μ�Ʈ or ������Ʈ");
							doUpdate = (boolean) inter.insertCupnNumber(context, request, dataSet);
						}
					}	// synchronized

					if(cnt == 0){

						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String emailTitle = "";
						String emailFileNm = "";
						String useYN = "N";

						if(doUpdate == true){	
							request.getSession().setAttribute("isTm","Y");
							emailTitle = "TM���Ա�� �̺�Ʈ ��������";
							emailFileNm = "/email_interpark.html";	
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setTo(email_id);
							sender.send(emailEtt);
						}
					}				 
				} //�̺�Ʈ �����߱� ��...
				*/
				
				
				debug("isTM>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + request.getSession().getAttribute("isTm"));
				
				// TMȸ�� ��ȭ���ű� ���� �̺�Ʈ ó�� ���� 
				dataSet.setString("tm_evt_no", "119");
				dataSet.setString("userSocid", userSocid);
				
				// 1) ������ Ŭ���ϰ� ������ ��� ���� üũ
				String currDate  = DateUtil.currdate("yyyyMMdd");
				String isTmMovie = (String)request.getSession().getAttribute("isTmMovie");
				debug("GolfmemInsActn:::isTmMovie : " + isTmMovie + " / userSocid : " + userSocid);
				if(isTmMovie == null){
					isTmMovie = "N";
				}
				
				if(isTmMovie.equals("Y") ){
					
					if(join_chnl.equals("0103")){
						// ī�� TM SK ������
						script = " window.open('GolfEvntTmSKInfoPop.do','tmInfo', 'width=539, height=294'); " + script;
						request.getSession().removeAttribute("isTmMovie");
				
					}else{
						// ���ű� ó�� ����
						GolfEvntTmMovieProc proc_tmMovie = (GolfEvntTmMovieProc)context.getProc("GolfEvntTmMovieProc");
						
						// 2) TMȸ������ Ȯ��
						String tmMovieTmCheck = (String) proc_tmMovie.isTmCheck(context, request, dataSet);		
						debug("GolfmemInsActn:::tmMovieTmCheck : " + tmMovieTmCheck );		
						
						// 2010 07 ���� ȸ���� ��ȭ���ű� 08���ʹ� ���������� �߱��Ѵ�.
						if(!GolfUtil.empty(tmMovieTmCheck)){
						
							if(tmMovieTmCheck.equals("SK")){
								
								// ī�� TM SK ������
								script = " window.open('GolfEvntTmSKInfoPop.do','tmInfo', 'width=539, height=294'); " + script;
								request.getSession().removeAttribute("isTmMovie");
								
							}else{
							
								// 3) �̺�Ʈ �Ⱓ üũ
								String from_date = "";
								String to_date   = "";
								
								DbTaoResult tmMovieDateCheck = (DbTaoResult) proc_tmMovie.eventDateCheck(context, request, dataSet);
								
								if (tmMovieDateCheck != null && tmMovieDateCheck.isNext()) {
									tmMovieDateCheck.first(); 
									tmMovieDateCheck.next();
									if(tmMovieDateCheck.getString("RESULT").equals("00")){
										from_date = tmMovieDateCheck.getString("FROM_DATE");
										to_date = tmMovieDateCheck.getString("TO_DATE");
										debug("GolfmemInsActn:::from_date ~ to_date >>>>>>>>>>>>" + from_date + "~" + to_date + ",���糯¥ : "+ currDate);
									}
								}
						
								if((Integer.parseInt(from_date) <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt(to_date))){
									
									// 4) ����600(����)�� ������ �̺�Ʈ ���� ���� ���� 
									String tmEvtCntYn = (String) proc_tmMovie.eventCountYn(context, request, dataSet);		
									debug("GolfmemInsActn:::tmEvtCntYn : " + tmEvtCntYn );			
									if(tmEvtCntYn.equals("Y")){
										
										// 5) ��ȭ���ű� ���޿��� Ȯ�� - 1�� �̻��ϰ��
										int useEvtCpnCnt = (int) proc_tmMovie.useEvtCpnCnt(context, request, dataSet);	
										debug("GolfmemInsActn:::useEvtCpnCnt : " + useEvtCpnCnt );			
										
										if(useEvtCpnCnt>0){
											// 5-1) 1���̻��̸�  ������ȣ 4�� �ٽ� ������
											script = " window.open('GolfEvntTmMovieCpnPop.do','tmCoupon', 'width=474, height=202'); " + script;
											
										}else{
											// 6) ��������� �������� ��� �̺�Ʈ �ȳ� �˾� ���
											script = " window.open('GolfEvntTmMovieInfoPop.do','tmInfo', 'width=539, height=695'); " + script;
										}											
										
										request.getSession().removeAttribute("isTmMovie");
						
									} else {	// 4) ����600(����)�� ������ �̺�Ʈ ���� ���� ����  
										request.getSession().removeAttribute("isTmMovie");
									}
						
								} else {	// 3) �̺�Ʈ �Ⱓ üũ
									request.getSession().removeAttribute("isTmMovie");
								}
							}// ���Կ��� ���� �̺�Ʈ ���� ����
							
							
							
						} else {	// 2) TMȸ������ Ȯ��
							request.getSession().removeAttribute("isTmMovie");
						}
					}	// 1) ������ Ŭ���ϰ� ������ ��� ���� üũ
					// TMȸ�� ��ȭ���ű� ���� �̺�Ʈ ó�� ����
				}

				debug("GolfmemInsActn:::script : " + script + " / returnUrlTrue : " + returnUrlTrue );	
				request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);
				//request.setAttribute("resultMsg", "����� ���������� ó�� �Ǿ����ϴ�.");      	
				
	        } else {

				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }	
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));
			paramMap.put("join_chnl", join_chnl);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			request.setAttribute("join_chnl", join_chnl);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
