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
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;
import com.initech.eam.nls.NLSHelper;



/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemBcJoinEndNewActn extends GolfActn{
	
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
		
		String returnUrl = "";
		String type = "bccard";
		
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
			paramMap.put("type", type);
			
			
			userId = parser.getParameter("memAccount", "");	// ȸ�����̵� simijoa81 : vriZE6WbWymLV6pPell2Zw==
			debug("GolfMemBcJoinEndActn : userId(��ȣȭ) = " + userId);
	        
			
			con = context.getDbConnection("default", null);	
			
			if(!(userId == null || userId.equals(""))){
							
				StringEncrypter receiver = new StringEncrypter("BCCARD", "GOLF");
				userId = receiver.decrypt(userId); 
//				userId = receiver.encrypt(userId); 
				debug("GolfMemBcJoinEndActn : userId = " + userId);
		        

				HttpSession session = request.getSession();
				ucusrinfo = proc.selectByAccount(con, userId);
				session.setAttribute("FRONT_ENTITY", ucusrinfo);
				session.setAttribute("SESSION_USER", ucusrinfo);
				ucusrinfo.setAccount(userId);
				
				
				if(!GolfUtil.empty(ucusrinfo.getSocid())){
					sso_id = ucusrinfo.getSocid();
					// ���ԿϷ� �������� ������.
					returnUrl = "GolfMemBcJoinFinal.do";
				}else{
					// �ֹε�Ϲ�ȣ ����������� ������.
					//returnUrl = "GolfMemBcJoinIpinForm.do?type="+type;
					
					// ��� ���ԿϷ� �������� ������.
					returnUrl = "GolfMemBcJoinFinal.do";
				}
				
				debug(" sso_id : " + sso_id + " / returnUrl" + returnUrl + " / type : " + type);
				
			}

	        paramMap.remove("userId_old");
	        paramMap.remove("userId");
	        paramMap.remove("INIpluginData");
	        
			paramMap.put("userId_old", userId);
	        paramMap.put("userId", userId);
	        paramMap.put("INIpluginData", "");
	        
			debug("GolfMemBcJoinEndActn2 : userId = " + userId);
			request.setAttribute("returnUrl", returnUrl);	
			//request.setAttribute("script", "alert('aa')");
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
}
