/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeReserveActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��û ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-29
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.par;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmGrRegDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmGrUpdDaoProc;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfBkParTimeReserveActn extends GolfActn{
	
	public static final String TITLE = "��ŷ ��û ó��";

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
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String rsvt_SQL_NO = "";

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
			}
			//debug("==========email_id========> " + email_id);
			//email_id = "juckcity@nate.com";
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String affi_GREEN_SEQ_NO	= parser.getParameter("AFFI_GREEN_SEQ_NO", "");
			String bk_DATE				= parser.getParameter("BK_DATE", "");
			String hp_DDD_NO			= parser.getParameter("HP_DDD_NO", "");
			String hp_TEL_HNO			= parser.getParameter("HP_TEL_HNO", "");
			String hp_TEL_SNO			= parser.getParameter("HP_TEL_SNO", "");
			String payType				= parser.getParameter("payType", "");
			// SMS ���� 
			String hp_TEL				= hp_DDD_NO+hp_TEL_HNO+hp_TEL_SNO;
			//debug("==========userMobile1========> " + hp_DDD_NO);
			//debug("==========userMobile2========> " + hp_TEL_HNO);
			//debug("==========userMobile3========> " + hp_TEL_SNO);
			// �̸��� ����
			String green_NM				= parser.getParameter("GREEN_NM", "");
			String bk_DATE_EMAIL		= parser.getParameter("BK_DATE_EMAIL", "");
			String del_DATE				= parser.getParameter("DEL_DATE", "");

	        GregorianCalendar today = new GregorianCalendar ( );
	        int nYear = today.get ( today.YEAR );
	        int nMonth = today.get ( today.MONTH ) + 1;
	        int nDay = today.get ( today.DAY_OF_MONTH ); 
	        String strToday = nYear+"�� "+nMonth+"�� "+nDay+"��";
	       
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
//			debug("=========GolfBkParTimeReserveActn====affi_GREEN_SEQ_NO : " + affi_GREEN_SEQ_NO);
//			debug("=========GolfBkParTimeReserveActn====affi_GREEN_SEQ_NO : " + parser.getParameter("AFFI_GREEN_SEQ_NO", ""));
			dataSet.setString("AFFI_GREEN_SEQ_NO", affi_GREEN_SEQ_NO);
			dataSet.setString("BK_DATE", bk_DATE);
			dataSet.setString("HP_DDD_NO", hp_DDD_NO);
			dataSet.setString("HP_TEL_HNO", hp_TEL_HNO);
			dataSet.setString("HP_TEL_SNO", hp_TEL_SNO);
			dataSet.setString("payType", payType);
					

			// 04.���� ���̺�(Proc) ��ȸ 1. ��Ͽ��� Ȯ��
			GolfBkParTimeReserveDaoProc proc = (GolfBkParTimeReserveDaoProc)context.getProc("GolfBkParTimeReserveDaoProc");
			int fineResult = proc.execute(context, dataSet);
			debug("fineResult : " + fineResult);
			DbTaoResult rsView = new DbTaoResult("");
			DbTaoResult addResult = new DbTaoResult("");
			
			
			if (fineResult==0){
				// �̵̹�� 
				request.setAttribute("returnUrl", "GolfBkParTimeList.do");
				request.setAttribute("resultMsg", "�ش� ������ ����� ����Ǿ����ϴ�. �ٽ� �������ּ���.");  
				rsView.addString("RESULT", "01");
				addResult.addString("RESULT", "01");
			}else{
				// ����ó��
				GolfBkParTimeRsInsDaoProc proc2 = (GolfBkParTimeRsInsDaoProc)context.getProc("GolfBkParTimeRsInsDaoProc");
				addResult = proc2.execute(context, dataSet, request);
 
				


				if (addResult != null && addResult.isNext()) {
					addResult.first();
					addResult.next();
					String sRESULT = (String) addResult.getObject("RESULT");

					if ("00".equals(sRESULT))
					{
					

						rsvt_SQL_NO = (String) addResult.getObject("RSVT_SQL_NO");
						paramMap.put("RSVT_SQL_NO", rsvt_SQL_NO);
						paramMap.put("RSVT_SQL_NO_ENC", rsvt_SQL_NO);
						debug("==========�����ϰ� �����ȣ�� ������ ���� �������� ������ �׼� => rsvt_SQL_NO : " + rsvt_SQL_NO);
						

						//debug("====================GolfMemInsActn === ���Ϲ߼� ===");
						if (!email_id.equals("")) {

							String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
							String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
							String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
							String emailTitle = "";
							String emailFileNm = "";
							
							EmailSend sender = new EmailSend();
							EmailEntity emailEtt = new EmailEntity("EUC_KR");
							
							emailTitle = "[Golf Loun.G] ��3 ��ŷ�� �Ϸ�Ǿ����ϴ�.";
							emailFileNm = "/email_tpl02.html";						
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+strToday+"|"+green_NM+"|"+bk_DATE_EMAIL+"|"+del_DATE);
							
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setTo(email_id);
							//sender.send(emailEtt);
						}
						
						//sms�߼�
						if (!hp_TEL.equals("")) {

							// SMS ���� ����
							HashMap smsMap = new HashMap();
							
							smsMap.put("ip", request.getRemoteAddr());
							smsMap.put("sName", userNm);
							smsMap.put("sPhone1", hp_DDD_NO);
							smsMap.put("sPhone2", hp_TEL_HNO);
							smsMap.put("sPhone3", hp_TEL_SNO);
							
							//debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
							String smsClss = "637";
							String message = "[��3] "+userNm+"�� "+green_NM+" "+bk_DATE_EMAIL+" ����Ϸ� - Golf Loun.G";
							SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
							String smsRtn = smsProc.send(smsClss, smsMap, message);
							//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
						}			

						dataSet.setString("RSVT_SQL_NO", rsvt_SQL_NO);
						GolfBkParTimeRsViewDaoProc proc_view = (GolfBkParTimeRsViewDaoProc)context.getProc("GolfBkParTimeRsViewDaoProc");
						
						rsView = proc_view.execute(context, dataSet);
					} else if ("02".equals(sRESULT)) {
						request.setAttribute("returnUrl", "GolfBkParTimeList.do");
						request.setAttribute("resultMsg", "��û���ڷ� �̹� ������ �ϼ̽��ϴ�. 1�� 1ȸ ���� �Ͻ� �� �ֽ��ϴ�.");  
					} else {
						request.setAttribute("returnUrl", "GolfBkParTimeList.do");
						request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�. �ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
					}
		        } else {
					request.setAttribute("returnUrl", "GolfBkParTimeList.do");
					request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�. �ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        }

			}
			
			// 05. Return �� ����
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		
			request.setAttribute("RsView", rsView); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			request.setAttribute("addResult", addResult);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
