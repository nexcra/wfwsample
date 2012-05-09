/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardUccSelThumbActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : UCC ���� ��� �����
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
public class GolfBoardUccSelThumbActn extends GolfActn{
	
	public static final String TITLE = "UCC ���� ��� �����";

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
//			�����̸� : sample_mysite_select_thumb.jsp
//			��ǥ�̹��� �������� - ���Ͼ��ε� �Ϸ��� ��ǥ�̹����� �����ϴ� ȭ��
//			----------------------------------------------------------------

//			������ ����ƿ��� ���� ����
//			����ƿ��� ������ ����Ű 
			String file_key = parser.getParameter("file_key");
//			������ �����ؼ� �����ֽ� ���� �������� �����Ǵ� ����Ű
			String client_key = parser.getParameter("client_key");
//			��ǥ�̹����� �����ϰ� ȣ���ؾ� �Ǵ� URL
			String call_url = parser.getParameter("call_url");

//			����ƿ��� ������ ��ǥ�̹��� ������ ���� �ӽ���ġ
			String thumbnail_img1 = parser.getParameter("thumbnail_img1");
			String thumbnail_img2 = parser.getParameter("thumbnail_img2");
			String thumbnail_img3 = parser.getParameter("thumbnail_img3");
			String thumbnail_img4 = parser.getParameter("thumbnail_img4");
			String thumbnail_img5 = parser.getParameter("thumbnail_img5");
			
//			---------------------------------------------------------------------------
//			�ش簪�� ���ؼ� DB�� �̿��ϱ� ���ؼ� �̺κп� �߰��Ͻʽÿ�

//			�߰�

//			---------------------------------------------------------------------------
			
//			��ǥ�̹����� �����ϰ� �ѱ�ž� �Ǵ� ����
//			��ǥ�̹����� ���������� ó�� �Ǿ������ ȣ��Ǵ� URL - ó���Ϸ�ȭ���� �����Ͻø� �˴ϴ�.
			String url_success2 = AppConfig.getAppProperty("URL_REAL")+"/golfUccFileRegEnd.do";
			url_success2 = url_success2.replaceAll("\\.\\.","");
//			��ǥ�̹��� ����ó���� ���еǾ��� ��� ȣ��Ǵ� URL - ó������ȭ���� �����Ͻø� �˴ϴ�.
			String url_error2 = AppConfig.getAppProperty("URL_REAL")+"/golfUccFileRegErr.do";
			url_error2 = url_error2.replaceAll("\\.\\.","");
//			String user_string4 = "DD";
			
			paramMap.put("call_url", call_url);	
			paramMap.put("client_key", client_key);	
			paramMap.put("url_success2", url_success2);	
			paramMap.put("url_error2", url_error2);	
			
			paramMap.put("thumbnail_img1", thumbnail_img1);	
			paramMap.put("thumbnail_img2", thumbnail_img2);	
			paramMap.put("thumbnail_img3", thumbnail_img3);	
			paramMap.put("thumbnail_img4", thumbnail_img4);	
			paramMap.put("thumbnail_img5", thumbnail_img5);			
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
