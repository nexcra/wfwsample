/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemCardInsActn
*   �ۼ���    : �̵������ �ǿ���
*   ����      : ���� > ���ó�� > ī��ȸ��
*   �������  : golf 
*   �ۼ�����  : 2009-08-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.login.CardNhInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemCardInsActn extends GolfActn{
	
	public static final String TITLE = "���� > ī��ȸ�� ���";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intMemberGrade = 0; 
		int intCardGrade = 0; 
		String email_id = "";
		String golfCardYn = "N";
		String golfCardNhYn = "N";
		String strCardNhType = "";
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				email_id 	= (String)usrEntity.getEmail1(); 
			}
			String strCardJoinNo	= "";
			
			if (mbr != null) 
			{	
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				
				if( cardList.size() > 0 )
				{
					cardInfo = (CardInfoEtt)cardList.get(0);
					strCardJoinNo = cardInfo.getJoinNo();	// �����ڵ�
					golfCardYn = "Y";
				}
				
				// ���� ����ī�� ���� üũList cardList = mbr.getCardInfoList();
				List cardNhList = mbr.getCardNhInfoList();
				CardNhInfoEtt cardNhInfo = new CardNhInfoEtt();
				
				if( cardNhList.size() > 0 )
				{
					cardNhInfo = (CardNhInfoEtt)cardNhList.get(0);
					strCardNhType = cardNhInfo.getCardType();	// ī������
					golfCardNhYn = "Y";
				}
			}
			
						
			// 02.�Է°� ��ȸ		
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			RequestParser parser = context.getRequestParser("default",request,response); 
			String strCode = parser.getParameter("strCode");
			
			// 03.Proc �� ���� �� ���� 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("strCode", strCode);	
						
			// 04.���� ���̺� ��ȸ
			GolfMemCardInsDaoProc proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
			int addResult = proc.execute(context, dataSet, request);		

        	String returnUrlTrue = "GolfMemJoinEnd.do";
        	String returnUrlFalse =  "GolfMemJoinNoCard.do";
        	String script = "parent.location.href='/app/golfloung/html/common/member_join_finish.jsp'";

			if (addResult == 1) {
				
				DbTaoResult tmView = proc.cardExecute(context, dataSet, request);

				if (tmView != null && tmView.isNext()) {
					tmView.first();
					tmView.next();
					memGrade = (String) tmView.getString("memGrade");	
					intMemGrade = (int) tmView.getInt("intMemGrade");	
					intMemberGrade = (int) tmView.getInt("intMemberGrade");	
					intCardGrade = (int) tmView.getInt("intCardGrade");	
					
				}

				usrEntity.setMemGrade(memGrade);				//��޸�
				usrEntity.setIntMemGrade(intMemGrade);		//������
				usrEntity.setIntMemberGrade(intMemberGrade);	//��������ó��
				usrEntity.setIntCardGrade(intCardGrade);		//ī����ó��
				usrEntity.setCyberMoney(0);
				

				if (email_id != null && !email_id.equals("")) {

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
				
				request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);
				//request.setAttribute("resultMsg", "����� ���������� ó�� �Ǿ����ϴ�.");      	
				
	        } else {

				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }	
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
