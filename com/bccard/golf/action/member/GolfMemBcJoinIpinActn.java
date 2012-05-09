/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemJoinPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 가입 팝업
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemBcJoinIpinActn extends GolfActn{
	
	public static final String TITLE = "회원 > 아이핀등록";

	
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

        String serverip = "";  // 서버아이피
        String devip = "";	   // 개발기 ip 정보
		String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);

		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		UcusrinfoEntity ucusrinfo = null;
		Connection con = null;

		UcusrinfoDaoProc proc = (UcusrinfoDaoProc) context.getProc("UcusrinfoDao");	


		try {	
			// 입력값 조회		
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
	        
			// 01. 사용자 정보 체크 
			con = context.getDbConnection("default", null);	
			HttpSession session = request.getSession();
			ucusrinfo = proc.selectByAccount(con, userId);
        	userId = ucusrinfo.getAccount();
        	userNm = ucusrinfo.getName();
        	userDi = ucusrinfo.getIpindiVal();
			data = userId+"|"+userNm+"|"+jumin_no+"|"+userDi;		// 사용자ID|이름|사용자주민번호|DI값

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
			

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			if(resCode.equals("01")){
				if(type.equals("golf")){
					
					// 골프라운지 회원이면 주민등록번호를 업데이트 해주고, 아니면 가입페이지로 돌린다.
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
					resultMsg = "해당 주민등록번호로 가입된 아이디가 이미 있습니다.";
				}else if(resCode.equals("03")){
					resultMsg = "IPIN DI값 변환중 오류가 발생했습니다.";
				}else if(resCode.equals("04")){
					resultMsg = "DB 업데이트중 오류 발생";
				}else if(resCode.equals("05")){
					resultMsg = "비씨카드 소지회원으로 전환해야합니다. (홈페이지내 비씨카드 소지회원 전환 메뉴에서 전환 가능)";
				}else if(resCode.equals("99")){
					resultMsg = "해당 사용자 데이터가 없습니다. ";
				}else if(resCode.equals("22")){
					resultMsg = "본인아님니다.";
				}else if(resCode.equals("23")){
					resultMsg = "해당 사용자 데이터 없습니다.";
				}else if(resCode.equals("24")){
					resultMsg = "시스템장애 (크레딧뱅크 시스템 오류) 입니다.";
				}else if(resCode.equals("25")){
					resultMsg = "주민등록번호 입력 오류입니다.";
				}else if(resCode.equals("50")){
					resultMsg = "정보도용 차단 요청 주민번호 입니다.";
				}
				resultMsg += " 다시 시도해 주세요.";
		        returnUrl = "GolfMemBcJoinIpinForm.do"; 
			}	       

	        debug("data : " + data + " / encData : " + encData + " / xmlUrl : " + xmlUrl + " / type : " + type + " / resCode : " + resCode + " / returnUrl : " + returnUrl);

	        paramMap.remove("INIpluginData");
			request.setAttribute("script", script);	
			request.setAttribute("resultMsg", resultMsg);	
			request.setAttribute("returnUrl", returnUrl);	
			request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
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
		//debug("==========날려보자========================="); 
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
		 	debug("==========================================>에러발생 시작====================================== ");
			

			StackTraceElement[] elem = e.getStackTrace();
			for(int i = 0 ; i< elem.length; i++){
				System.out.println( elem[i] );
			}
			debug("==========================================>에러 끝====================================== ");
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
