/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkCheckUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ > Ȯ��/��� > ���
*   �������  : golf
*   �ۼ�����  : 2009-05-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.check;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.check.*;
import com.bccard.golf.dbtao.proc.booking.sky.*;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfBkCheckUpdActn extends GolfActn{
	
	public static final String TITLE = "��ŷ > Ȯ��/��� > ���";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String userId = "";
		String email_id = "";
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userMobile1 	= (String)usrEntity.getMobile1();
				userMobile2 	= (String)usrEntity.getMobile2();
				userMobile3 	= (String)usrEntity.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
			}
			 /*
			debug("==========email_id========> " + email_id);
			debug("==========userMobile1========> " + userMobile1);
			debug("==========userMobile2========> " + userMobile2);
			debug("==========userMobile3========> " + userMobile3);
			email_id = "simijoa@hanmail.net";
			userMobile1 = "010";
			userMobile2 = "9192";
			userMobile3 = "4738";
			*/
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String type = parser.getParameter("type", "");
			String idx = parser.getParameter("idx", "");
			String seq = parser.getParameter("seq", "");
			String returnUrl = parser.getParameter("returnUrl", "");
			//debug("==========returnUrl========> " + returnUrl);
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("type", type);
			dataSet.setString("idx", idx);
			dataSet.setString("userId", userId);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfBkCheckUpdDaoProc proc = (GolfBkCheckUpdDaoProc)context.getProc("GolfBkCheckUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);
						
	        if (editResult == 1) {

				//sms�߼�
				if (!userMobile.equals("")) {

					// SMS ���� ����
					HashMap smsMap = new HashMap();
					
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", userNm);

					String smsClss = "";
					String message = "";
					
					//debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					
					if(type.equals("M")){
						// �����̾�

						String gr_nm = "";
						
						dataSet.setString("TIME_SEQ_NO",	seq);
						GolfBkPreTimeRsViewDaoProc proc_mail = (GolfBkPreTimeRsViewDaoProc)context.getProc("GolfBkPreTimeRsViewDaoProc");
						DbTaoResult rsView = proc_mail.execute_cancel(context, dataSet);
						rsView.first();
						rsView.next();
						
						userMobile1 = (String) rsView.getObject("HP_DDD_NO");
						userMobile2 = (String) rsView.getObject("HP_TEL_HNO");
						userMobile3 = (String) rsView.getObject("HP_TEL_SNO");

						if(!GolfUtil.empty((String) rsView.getObject("RL_GREEN_NM"))){
							gr_nm = (String) rsView.getObject("RL_GREEN_NM");
						}else if(!GolfUtil.empty((String) rsView.getObject("GR_NM"))){
							gr_nm = (String) rsView.getObject("GR_NM");
						}else{
							gr_nm = "";
						}
						
						smsMap.put("sPhone1", userMobile1);
						smsMap.put("sPhone2", userMobile2);
						smsMap.put("sPhone3", userMobile3);
						
						smsClss = "636";
						message = "[VIP��ŷ] "+userNm+"�� "+ gr_nm +" "+(String) rsView.getObject("BKPS_DATE")+" "+(String) rsView.getObject("BKPS_TIME")+":"+(String) rsView.getObject("BKPS_MINUTE")+" ������� - Golf Loun.G";
						
					}else if(type.equals("P")){
						// ��3��ŷ

						dataSet.setString("RSVT_SQL_NO",	idx);
						GolfBkParTimeRsViewDaoProc proc_mail = (GolfBkParTimeRsViewDaoProc)context.getProc("GolfBkParTimeRsViewDaoProc");
						DbTaoResult rsView = proc_mail.execute(context, dataSet);
						rsView.first();
						rsView.next();
						
						userMobile1 = (String) rsView.getObject("HP_DDD_NO");
						userMobile2 = (String) rsView.getObject("HP_TEL_HNO");
						userMobile3 = (String) rsView.getObject("HP_TEL_SNO");

						smsMap.put("sPhone1", userMobile1);
						smsMap.put("sPhone2", userMobile2);
						smsMap.put("sPhone3", userMobile3);
						
						smsClss = "638";
						message = "[��3] "+userNm+"�� "+(String) rsView.getObject("GREEN_NM")+" "+(String) rsView.getObject("BK_DATE")+ " ������� - Golf Loun.G";
						
					}else if(type.equals("S")){
						// Sky72 �帲�ὺ

						dataSet.setString("RSVT_SQL_NO",	idx);
						GolfBkSkyTimeRsViewDaoProc proc_mail = (GolfBkSkyTimeRsViewDaoProc)context.getProc("GolfBkSkyTimeRsViewDaoProc");
						DbTaoResult rsView = proc_mail.execute(context, dataSet);
						rsView.first();
						rsView.next();
						
						userMobile1 = (String) rsView.getObject("HP_DDD_NO");
						userMobile2 = (String) rsView.getObject("HP_TEL_HNO");
						userMobile3 = (String) rsView.getObject("HP_TEL_SNO");

						smsMap.put("sPhone1", userMobile1);
						smsMap.put("sPhone2", userMobile2);
						smsMap.put("sPhone3", userMobile3);

						smsClss = "642";
						message = "[�帲�ὺ]"+userNm+"�� "+(String) rsView.getObject("HOLE")+"Ȧ "+(String) rsView.getObject("BK_DATE")+" "+(String) rsView.getObject("BK_TIME")+" ������� - Golf Loun.G";
					}					

					debug("==========userMobile1========> " + userMobile1);
					debug("==========userMobile2========> " + userMobile2);
					debug("==========userMobile3========> " + userMobile3);
					
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}			
				
				request.setAttribute("returnUrl", returnUrl);
				request.setAttribute("resultMsg", "������ ��� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrl);
				request.setAttribute("resultMsg", "������Ұ� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
	
	        request.setAttribute("paramMap", paramMap);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
