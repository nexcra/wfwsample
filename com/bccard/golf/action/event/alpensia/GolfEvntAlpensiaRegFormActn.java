/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntAlpensiaRegFormActn
*   �ۼ���	: (��)�̵������ ������
*   ����		: �̺�Ʈ > ����þ� > ��ŷ ��û ��
*   �������	: Golf
*   �ۼ�����	: 2010-06-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.alpensia;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.alpensia.GolfEvntAlpensiaNoticeIfmDaoProc;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfEvntAlpensiaRegFormActn extends GolfActn {
	
	public static final String TITLE = "�̺�Ʈ > ����þ� > ��ŷ ��û ������";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {

		String subpage_key = "default";		
		DbTaoConnection con = null;

		// ���� ����
		String script = "";
		String userNm = "";
		String userId = "";
		String juminno = ""; 
		String juminno1 = ""; 
		String juminno2 = ""; 
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		String phone1 = "";
		String phone2 = "";
		String phone3 = "";
		
		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);


			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				juminno1	= juminno.substring(0, 6);
				juminno2	= juminno.substring(6, 13);

				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
				
				phone1 	= (String)usrEntity.getPhone1(); 
				phone2 	= (String)usrEntity.getPhone2(); 
				phone3 	= (String)usrEntity.getPhone3(); 
			}
			
			paramMap.put("userNm", userNm); 
			paramMap.put("userId", userId);
			paramMap.put("juminno1", juminno1);
			paramMap.put("juminno2", juminno2);
			paramMap.put("mobile1", mobile1);
			paramMap.put("mobile2", mobile2);
			paramMap.put("mobile3", mobile3);
			paramMap.put("phone1", phone1);
			paramMap.put("phone2", phone2);
			paramMap.put("phone3", phone3);
			
	        request.setAttribute("paramMap", paramMap);
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
