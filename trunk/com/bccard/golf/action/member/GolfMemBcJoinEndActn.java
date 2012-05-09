/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemJoinPopActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ���� �˾�
*   �������  : golf
*   �ۼ�����  : 2009-05-19 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemJoinNocardDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemTmInsDaoProc;
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;
import com.initech.eam.nls.NLSHelper;



/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemBcJoinEndActn extends GolfActn{
	
	public static final String TITLE = "ȸ�� > BC ȸ������ �Ϸ�";

	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";

		int intMemGrade = 0;
		String memGrade = "";

		String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
		String sso_id = "";
		String userAcount = "";
		String email_id = "";
		String memSocid = "";			// �ֹε�Ϲ�ȣ
		int insResult = 0;				// ȸ�����Կ���
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		UcusrinfoEntity ucusrinfo = null;
		Connection con = null;

		UcusrinfoDaoProc proc = (UcusrinfoDaoProc) context.getProc("UcusrinfoDao");	


		try {			
			// 01. ��������üũ
	        UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
        	usrEntity = new UcusrinfoEntity(); 
			
			// �Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			userId = parser.getParameter("userId", "");	// ȸ�����̵� 
			memSocid = parser.getParameter("socid", ""); 
        
		
			con = context.getDbConnection("default", null);	
			
			if(!(userId == null || userId.equals(""))){
							
//				StringEncrypter receiver = new StringEncrypter("BCCARD", "GOLF");
//				userId = receiver.decrypt(userId); 
//				userId = receiver.encrypt(userId); 
		        paramMap.put("userId", userId);
				debug("GolfMemBcJoinEndActn : userId = " + userId);
		        

				HttpSession session = request.getSession();
				ucusrinfo = proc.selectByAccount(con, userId);
				session.setAttribute("FRONT_ENTITY", ucusrinfo);
				session.setAttribute("SESSION_USER", ucusrinfo);
				email_id = ucusrinfo.getEmail1();
				if(GolfUtil.empty(memSocid)){memSocid=ucusrinfo.getSocid();}
				

		        GregorianCalendar nowDate = new GregorianCalendar ( );

		        int sYear = nowDate.get ( nowDate.YEAR );
		        int sMonth = nowDate.get ( nowDate.MONTH ) + 1;
		        int sDay = nowDate.get ( nowDate.DAY_OF_MONTH );
		        String sDate = sYear + "�� " + sMonth + "�� " + sDay + "��";
				
				debug("## GolfMemBcJoinEndActn | userId : " + userId + " / sso_domain : " + sso_domain 
				+ " / userAcount : " + userAcount + " / memSocid : " + memSocid + " / email_id : " + email_id
				+ " / sDate : " + sDate);
				
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("userId", userId);
				dataSet.setString("memSocid", memSocid);
				
				// ����ȸ������
				dataSet.setString("payType", "");	
				dataSet.setString("moneyType", "4");	
				dataSet.setString("insType", "");	
				dataSet.setString("payWay", "");
				dataSet.setString("STTL_AMT", "");
				dataSet.setString("CODE_NO", "");
				dataSet.setString("JOIN_CHNL", "0001");

				GolfMemTmInsDaoProc tmProc = (GolfMemTmInsDaoProc)context.getProc("GolfMemTmInsDaoProc");
				int isTMmem = tmProc.isTmMemExecute(context, dataSet, request);
				
				if(isTMmem>0){
					DbTaoResult tmView = tmProc.execute(context, dataSet, request);

					if (tmView != null && tmView.isNext()) {
						tmView.first();
						tmView.next();
						insResult = 1;
						intMemGrade = (int) tmView.getInt("intMemGrade");	
						memGrade = (String) tmView.getString("memGrade");
					}	
				}else{
					GolfMemInsDaoProc insProc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
					insResult = insProc.execute(context, dataSet, request);
					intMemGrade = 8;
					memGrade = "white";
				}

		        
				if(insResult>0){

					usrEntity.setMemGrade(memGrade);
					usrEntity.setIntMemGrade((int)intMemGrade);
					
					if (!email_id.equals("")) {

						try{
							String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
							String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
							String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
							String emailTitle = "";
							String emailFileNm = "";
							
							imgPath = "<img src=\"http://www.bccard.com";
							hrefPath = "<a href=\"http://www.bccard.com";
							
							EmailSend sender = new EmailSend();
							EmailEntity emailEtt = new EmailEntity("EUC_KR");
							
							emailTitle = "[Golf Loun.G] ��������� ���� ������ ���ϵ帳�ϴ�.";
							emailFileNm = "/email_tpl27.html";
	
							debug("## GolfMemBcJoinEndActn | email_id : " + email_id + " / emailFileNm : " + emailFileNm 
									+ " / imgPath : " + imgPath + " / hrefPath : " + hrefPath);
							
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, sDate);
							
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setTo(email_id);
							sender.send(emailEtt);
						}catch(Exception e){
							 
						}
					}
				}
			}
			

	        paramMap.put("intMemGrade", intMemGrade+"");
	        paramMap.put("memGrade", memGrade);
	        paramMap.put("userId_old", userId);
	        paramMap.put("sso_id", memSocid);

			request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}

	/** ****************************************************************************
	 * getSsoId
	 * @param request	  HttpServletRequest
	 **************************************************************************** */
	public String getSsoId(HttpServletRequest request) {
		return CookieManager.getCookieValue(SECode.USER_ID, request);
	}
    	
}
