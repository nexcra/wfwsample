/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardUccFileRegFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : UCC ���� ��� ��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.bbs;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.text.*;

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
import com.bccard.golf.dbtao.DbTaoDataSet;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfBoardUccFileRegFormActn extends GolfActn{
	
	public static final String TITLE = "UCC ���� ��� ��";

	/***************************************************************************************
	* Golf ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
						
//			----------------------------------------------------------------
//			����� ���� JSP�ҽ� 
//			�����̸� : sample_mysite_upload.jsp
//			��ǥ�̹��� �������� - ù��° ������ ���Ͽø��⸦ ���� ���� �ҽ�
//			----------------------------------------------------------------

//			�����ھ��̵� - ����� ���̵� �ƴ� ������ȣ����2 ������ ���̵� - [������ ���̵�� �����ϼ���]
			String company_id = "bccard";
//			UCC ������ ���� ���� ����Ű - MyGabia���� Ȯ���� ���� - [������ ����Ű�� �����ϼ���]
			String certy_key = "DVPF-7CYW8-FY2C";									
//			Unix TimeStamp - [�������� ������]
			Timestamp certy_time = new Timestamp( System.currentTimeMillis() );

//			md5 ��ȣȭ ����
//			====================�������� ������===============================
			String param = company_id + certy_key + certy_time;

			StringBuffer md5 = new StringBuffer(); 

			try { 
				byte[] digest = java.security.MessageDigest.getInstance("MD5").digest(param.getBytes()); 

				for (int i = 0; i < digest.length; i++) { 
					md5.append(Integer.toString((digest[i] & 0xf0) >> 4, 16)); 
					md5.append(Integer.toString(digest[i] & 0x0f, 16)); 
				} 

			} catch(java.security.NoSuchAlgorithmException ne) { 
				ne.printStackTrace(); 
			} 

//			�������Ȱ� - [�������� ������]
			String certy_value = md5.toString();
//			====================================================================

//			������ �����ϴ� ���ε� �ҷ��� ���Ͽ� ���� ������  - [����Ͻú��ʷ� �⺻���������� ������ �����ص� �˴ϴ�]
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddhhmmss");
			java.util.Date currentTime = new java.util.Date();
			String today = formatter.format(currentTime);
//			-------------------------------------------------------------------
			String client_key = company_id +"_"+ today;			

//			������ ���������� ���ε� �Ǿ������ ȣ��Ǵ� URL - [��ǥ�̹����� �����ϴ� ȭ������ �����Ǿ�� �մϴ�]
			String url_success1 = AppConfig.getAppProperty("URL_REAL")+"/golfUccSelThumb.do";
			url_success1 = url_success1.replaceAll("\\.\\.","");
//			���� ���ε尡 �������� ��� ȣ��Ǵ� URL - [���е� �����޼����� ���ؼ� ó���ǵ��� �����Ǿ�� �մϴ�]
			String url_error1 = AppConfig.getAppProperty("URL_REAL")+"/golfUccFileRegErr.do";
			url_error1 = url_error1.replaceAll("\\.\\.","");
			
//			����ϴ� ���ڵ��ӵ� (���� : Mbps) ��) 200,400(default),600,800,1000
			String encoding_speed = "400";
//			����ϴ� ȭ��ũ�� (���� | ���� - ���� pixel) ��) 320|240, 400|300(default), 640|480, 720|480
			String encoding_screen = "400|300"; 
//			��ǥ�̹����� �ڵ�,�������θ� ���� - [�������� ��ǥ�̹����� �����Ѵٸ� �״�� �νʽÿ�]
			String auto_flag = "M";

//			��������� - ���� ���ڸ� �ٲ㼭 ��� �߰��ϽǼ� �ֽ��ϴ�.
			String user_string1 = "���������1";
			String user_string2 = "���������2";
			String user_string3 = "���������3";
//			String user_string4 = "���������4";

			String class_code = "";	
//			class_code (�з��ڵ�)�� ����ҽÿ��� 
//			http://admin.flv.gabia.com/flash_response/get_class.php?company_id=�ͻ���̵�
//			�� ȣ���ϼż� ������ XML�� �Ľ��ؼ� ����Ͻø� �˴ϴ�.
//			��������� : sample_mysite_class_xml.jsp

//			�з������ MyGabia�� ������ȣ����2 ������������ �����մϴ�.
//			������ �ͻ���̵�� ��ϵǾ� �ִ� �⺻�з��� �ڵ� �Էµ˴ϴ�. 
//			���� : �߸��� �з��ڵ尡 �Ѿ�ý� ������ ���ϵǸ� ��ϵ��� �ʽ��ϴ�. 
			
			// 05. Return �� ����
			paramMap.put("certy_value", certy_value);
			paramMap.put("certy_time", certy_time);
			paramMap.put("company_id", company_id);
			paramMap.put("client_key", client_key);
			paramMap.put("url_success1", url_success1);
			paramMap.put("url_error1", url_error1);
			paramMap.put("encoding_speed", encoding_speed);
			paramMap.put("encoding_screen", encoding_screen);
			paramMap.put("auto_flag", auto_flag);
			
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
