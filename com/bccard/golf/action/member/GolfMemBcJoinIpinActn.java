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

import java.net.URLConnection;
import java.net.URLEncoder;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.*;


import java.io.FileInputStream;

import org.w3c.dom.Node;

import java.net.InetAddress;

import java.io.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import java.io.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;



import java.net.URL;
import java.net.URLConnection;





/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemBcJoinIpinActn extends GolfActn{
	
	public static final String TITLE = "ȸ�� > �����ɵ��";

	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		String userNm = "";
		String socId = "";
		String userDi = "";
		String type = "";

		String data = "";
		String encData = "";
		String returnUrl = "";
		String resultMsg = "";
        String xmlUrl = "";
        String script = "";

        String serverip = "";  // ����������
        String devip = "";	   // ���߱� ip ����
		String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);

		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		UcusrinfoEntity ucusrinfo = null;
		Connection con = null;

		UcusrinfoDaoProc proc = (UcusrinfoDaoProc) context.getProc("UcusrinfoDao");	


		try {	
			// �Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String jumin_no1 = parser.getParameter("jumin_no1", "");
			String jumin_no2 = parser.getParameter("jumin_no2", "");
			String jumin_no = jumin_no1+jumin_no2;
			userId = parser.getParameter("userId", "");			
			type = parser.getParameter("type", "");			
			

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt!=null){
				if(GolfUtil.empty(userId)){
					userId = userEtt.getAccount();
				}
			}

	        try {
	        	serverip = InetAddress.getLocalHost().getHostAddress();
	        } catch(Throwable t) {}

	        try {
	        	devip = AppConfig.getAppProperty("DV_WAS_1ST");
	        } catch(Throwable t) {}
	        
			// 01. ����� ���� üũ 
			con = context.getDbConnection("default", null);	
			HttpSession session = request.getSession();
			ucusrinfo = proc.selectByAccount(con, userId);
        	userId = ucusrinfo.getAccount();
        	userNm = ucusrinfo.getName();
        	userDi = ucusrinfo.getIpindiVal();
			data = userId+"|"+userNm+"|"+jumin_no+"|"+userDi;		// �����ID|�̸�|������ֹι�ȣ|DI��

	        StringEncrypter encrypter = new StringEncrypter("_bccard_", "IPIN_SOC_CERT");

	        if(data != null && !"".equals(data)) {
	             encData = URLEncoder.encode(encrypter.encrypt(data)); 
	        }

	        if(serverip.equals(devip)){
	        	xmlUrl = "http://develop.bccard.com/app/card/BcIpinSocCertActn.do?key="+encData;
	        }else{
	        	xmlUrl = "http://www.bccard.com/app/card/BcIpinSocCertActn.do?key="+encData;
	        }

			paramMap.put("userId", userId);
			paramMap.put("socid", jumin_no);
			paramMap.put("url", xmlUrl);
			
			String lastSeq = this.getXmlCallBack(context, request, response, paramMap);
			String resCode = this.getXmlParse(lastSeq,"result_key");
//			resCode = "01";
			

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			if(resCode.equals("01")){
				if(type.equals("golf")){
					
					// ��������� ȸ���̸� �ֹε�Ϲ�ȣ�� ������Ʈ ���ְ�, �ƴϸ� ������������ ������.
					dataSet.setString("jumin_no", jumin_no);	
					GolfMemInsDaoProc proc_ipin = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
					int intUpdresult = proc_ipin.ipinExecute(context, dataSet, request);
					if(intUpdresult>0){
						script = "window.top.location='/'";
					}else{
						returnUrl = "GolfMemJoinNoCard.do";
					}
				}else{
					returnUrl = "GolfMemBcJoinFinal.do";
				}
			}else{
				if(resCode.equals("02")){
					resultMsg = "�ش� �ֹε�Ϲ�ȣ�� ���Ե� ���̵� �̹� �ֽ��ϴ�.";
				}else if(resCode.equals("03")){
					resultMsg = "IPIN DI�� ��ȯ�� ������ �߻��߽��ϴ�.";
				}else if(resCode.equals("04")){
					resultMsg = "DB ������Ʈ�� ���� �߻�";
				}else if(resCode.equals("05")){
					resultMsg = "��ī�� ����ȸ������ ��ȯ�ؾ��մϴ�. (Ȩ�������� ��ī�� ����ȸ�� ��ȯ �޴����� ��ȯ ����)";
				}else if(resCode.equals("99")){
					resultMsg = "�ش� ����� �����Ͱ� �����ϴ�. ";
				}else if(resCode.equals("22")){
					resultMsg = "���ξƴԴϴ�.";
				}else if(resCode.equals("23")){
					resultMsg = "�ش� ����� ������ �����ϴ�.";
				}else if(resCode.equals("24")){
					resultMsg = "�ý������ (ũ������ũ �ý��� ����) �Դϴ�.";
				}else if(resCode.equals("25")){
					resultMsg = "�ֹε�Ϲ�ȣ �Է� �����Դϴ�.";
				}else if(resCode.equals("50")){
					resultMsg = "�������� ���� ��û �ֹι�ȣ �Դϴ�.";
				}
				resultMsg += " �ٽ� �õ��� �ּ���.";
		        returnUrl = "GolfMemBcJoinIpinForm.do"; 
			}	       

	        debug("data : " + data + " / encData : " + encData + " / xmlUrl : " + xmlUrl + " / type : " + type + " / resCode : " + resCode + " / returnUrl : " + returnUrl);

	        paramMap.remove("INIpluginData");
			request.setAttribute("script", script);	
			request.setAttribute("resultMsg", resultMsg);	
			request.setAttribute("returnUrl", returnUrl);	
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

	public String getXmlCallBack(WaContext waContext, HttpServletRequest request, HttpServletResponse response, Map paramMap)  throws IOException, ServletException, GolfException{
		
		String decrypted = "";
		String  url = "";
		//debug("==========��������========================="); 
		try{
			 
			url 	= paramMap.get("url").toString();
			
			//debug("==================================="+url+htcKey+sndParam);
			
			URL tempURL = new URL(url);
			
			InputStream tempInputStream = tempURL.openStream();
			
			InputStreamReader isr = new InputStreamReader(tempInputStream, "utf-8");
			
			StringBuffer sb = new StringBuffer();
			
			int curByte;
			
			while ((curByte = isr.read()) != -1) {
			 sb.append((char)curByte);
			}
			
			isr.close(); 
			tempInputStream.close();
			
			//out.clearBuffer();
			response.reset();
			//response.setStatus( HttpStatus.SC_OK );
			response.setContentType("text/xml; charset=utf-8");
			decrypted = String.valueOf(sb);
			
		}catch(Exception e){
			
			/*
		 	debug("==========================================>�����߻� ����====================================== ");
			

			StackTraceElement[] elem = e.getStackTrace();
			for(int i = 0 ; i< elem.length; i++){
				System.out.println( elem[i] );
			}
			debug("==========================================>���� ��====================================== ");
			*/
		}finally{ 
		}
		return decrypted;
	}
	
	public String getXmlParse(String ctnt, String findStr)  throws IOException{
		String returnCode = "";
		
		try{
			//DOMParser xmlParser = new DOMParser();			
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
			org.jdom.Document doc = builder.build(new StringReader(ctnt));
			org.jdom.Element root = doc.getRootElement();
			
			returnCode = root.getChildText(findStr);		
			
		}catch(Exception e){
		
			
		}
		return returnCode;	
	}

}
