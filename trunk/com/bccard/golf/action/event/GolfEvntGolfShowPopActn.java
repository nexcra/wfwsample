/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntGolfShowPopActn
*   �ۼ���	: (��)�̵������ ������
*   ����		: �����ڶ�ȸ ���� ��� 
*   �������	: golf
*   �ۼ�����	: 2010-05-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntGolfShowPopDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntGolfShowPopActn extends GolfActn{
	
	public static final String TITLE = "�����ڶ�ȸ ���� ���";

	/***************************************************************************************
	* ���� �����ȭ��
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
		
		String userId = "";
		String userNm = ""; 
		String memGrade = ""; 
		String memMobile = "";
		String memMobile1 = "";
		String memMobile2 = "";
		String memMobile3 = "";
		String result = "";
		String printYn = "N";
		

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
				userNm		= (String)usrEntity.getName(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				memMobile 	= (String)usrEntity.getMobile();
				memMobile1 	= (String)usrEntity.getMobile1();
				memMobile2 	= (String)usrEntity.getMobile2(); 
				memMobile3 	= (String)usrEntity.getMobile3(); 
			}

			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("memGrade", memGrade);
			paramMap.put("memMobile", memMobile);
			paramMap.put("memMobile1", memMobile1);
			paramMap.put("memMobile2", memMobile2);
			paramMap.put("memMobile3", memMobile3);

			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("userId", userId);

			GolfEvntGolfShowPopDaoProc proc = (GolfEvntGolfShowPopDaoProc)context.getProc("GolfEvntGolfShowPopDaoProc");
			if(usrEntity != null) {
				// 04.���� ���̺�(Proc) ��ȸ
				DbTaoResult golfShowPopResult = (DbTaoResult) proc.execute(context, dataSet);

				if(golfShowPopResult!=null && golfShowPopResult.isNext()){
					golfShowPopResult.next();
					result = golfShowPopResult.getString("RESULT");
					if(result.equals("01")){
						printYn = "Y";
					}
				}
			}
			
			paramMap.put("printYn", printYn);
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
