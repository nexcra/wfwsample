/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemRichCardInsActn
*   �ۼ���    : �̵������ �ǿ���
*   ����      : ���� > ��ġī�� ���ó��
*   �������  : golf 
*   �ۼ�����  : 2010-09-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemRichCardInsActn extends GolfActn{
	
	public static final String TITLE = "���� > ��ġī�� ���ó��";
	static final String JoltServiceName = "BSNINPT";
	static final String TSN025 = "MHL0260R0100";
	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		String userNm = ""; 
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intMemberGrade = 0; 
		int intCardGrade = 0; 
		String email_id = "";
		int addResult = 0;
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
		 	
			 if(usrEntity != null) {
				 userId		= (String)usrEntity.getAccount();
				 userNm		= (String)usrEntity.getName(); 
				 email_id 	= (String)usrEntity.getEmail1(); 
				 userMobile1 = (String)usrEntity.getMobile1();
				userMobile2 = (String)usrEntity.getMobile2();
				userMobile3 = (String)usrEntity.getMobile3();
			}			
			String strResultCode	= "99";
						
			// 02.�Է°� ��ȸ		
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			RequestParser parser 	= context.getRequestParser("default",request,response); 
			
			String accede 			= StrUtil.isNull(parser.getParameter("accede"), "N");
			String richCardYn	= "N";
			int addMemResult 		= 0;
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CSTMR_ID", userId);
			
				//���ǿ��� �ٽ� �ѹ� üũ
				if("Y".equals(accede))
				{
					
					System.out.print("## GolfMemRichCardInsActn VIPī�� : userId : "+userId+"\n");
					
					//��ġī�� �������� üũ
					try {
						List richCardList = mbr.getRichCardInfoList();
						if( richCardList!=null && richCardList.size() > 0 )
						{
							for (int i = 0; i < richCardList.size(); i++) 
							{
								
								richCardYn = "Y";
								debug("## ��ġī�� ���� ȸ��");
							}
						}
						else
						{
							richCardYn = "N";
							debug("## ��ġī�� �̼���");					
						}
					} catch(Throwable t) 
					{
						richCardYn = "N";
						debug("## ��ġī�� üũ ����");	
					}
					
					
					if("Y".equals(richCardYn))
					{
						dataSet.setString("strCode", "0017");	// Rich&Rich ������� �Է�
						dataSet.setString("joinMode", "acrgJoin");
						GolfMemCardInsDaoProc mem_proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
						debug("## GolfMemRichCardInsActn | ȸ������ DB �Է� ó�� ����");
						addMemResult = mem_proc.execute(context, dataSet, request);	
						debug("## GolfMemRichCardInsActn | ȸ������ DB �Է� ó�� ��� addMemResult : "+addMemResult);
						
						if (addMemResult == 1) {
							try {
								DbTaoResult tmView = mem_proc.cardExecute(context, dataSet, request);

								if (tmView != null && tmView.isNext()) {
									tmView.first();
									tmView.next();
									memGrade = (String) tmView.getString("memGrade");	
									intMemGrade = (int) tmView.getInt("intMemGrade");	
									intMemberGrade = (int) tmView.getInt("intMemberGrade");	
									intCardGrade = (int) tmView.getInt("intCardGrade");	
									
								}
							} catch(Throwable t) {}
							usrEntity.setMemGrade(memGrade);				//��޸�
							usrEntity.setIntMemGrade(intMemGrade);		//������
							usrEntity.setIntMemberGrade(intMemberGrade);	//��������ó��
							usrEntity.setIntCardGrade(intCardGrade);		//ī����ó��
							usrEntity.setCyberMoney(0);
							
							try { 
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
							} catch(Throwable t) {}
							
							// ���� ������
							try { 
								HashMap smsMap = new HashMap();
								smsMap.put("ip", request.getRemoteAddr());
								smsMap.put("sName", userNm);
								smsMap.put("sPhone1", userMobile1);
								smsMap.put("sPhone2", userMobile2);
								smsMap.put("sPhone3", userMobile3);
							        
								String smsClss = "674";
								String message = "��������� Rich&Richȸ������ ���������� ���ԵǼ̽��ϴ�. �����մϴ�."; 
				 
								SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
								String smsRtn = smsProc.send(smsClss, smsMap, message);
							} catch(Throwable t) {}
							
							request.setAttribute("returnUrl", "");
							request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp'");								
							request.setAttribute("resultMsg", "");	
							debug("�������� ���� ����");
							strResultCode = "11";
							
						}
						else
						{
							
							request.setAttribute("returnUrl", "GolfMemRichCardJoin.do");
							request.setAttribute("resultMsg", "ȸ�������� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ���Ե��� ���� ��� �����ڿ� �����Ͻʽÿ�.");
							debug("ȸ������ DBó�� ����");
							strResultCode = "44";
						}
						
					}
					else
					{
						debug("��ġī�尡 ����");
						request.setAttribute("returnUrl", "GolfMemRichCardJoin.do");
						request.setAttribute("resultMsg", "Rich&Rich ī�尡 �����ϴ�.");
						strResultCode = "77";
					}
					
					
				
						
						
					
					
					
					
					
					
					
					
					
					
					
					
					
					
				}
				else
				{
					debug("���� ���ΰ� Ȯ�ε��� ����");
					request.setAttribute("returnUrl", "GolfMemRichCardJoin.do");
					request.setAttribute("resultMsg", "����� �������ּ���.");
					strResultCode = "88";
				}
				
				
				
			
			
			
			
			System.out.print("## GolfMemRichCardInsActn ��ġī�� ���ó�� : userId : "+userId+" | strResultCode : "+strResultCode+" \n");
			
			
			
			// 05. Return �� ����				
			paramMap.put("editResult", String.valueOf(addResult));	
	        request.setAttribute("paramMap", paramMap); 
	        request.setAttribute("strResultCode", strResultCode);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
