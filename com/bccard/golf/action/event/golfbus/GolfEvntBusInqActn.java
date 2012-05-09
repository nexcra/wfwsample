/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBusInqActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : �̺�Ʈ->��������������̺�Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-09-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.golfbus;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntBusInqActn extends AbstractAction {

	public static final String Title = "�̺�Ʈ->��������������̺�Ʈ";

	/***********************************************************************
	 * �׼�ó��.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return ��������
	 **********************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		TaoConnection	con			= null;
		RequestParser	parser		= context.getRequestParser("default", request, response);
		TaoResult 		result  	= null;

		String userNm = ""; 
		String userId = "";
		String userMoblie1 = "";
		String userMoblie2 = "";
		String userMoblie3 = "";
		String permission = "";
		
		try {
			
			Map paramMap 			= parser.getParameterMap();
			
			//PATH ��������
			String imgPath			= AppConfig.getAppProperty("GIFT_PATH"); 	
			
			//1. �Ķ��Ÿ �� 
			String actnKey 			= super.getActionKey(context);									
			
			con = context.getTaoConnection("dbtao",null);
						
			// �Ķ���� ����
			TaoDataSet input = new DbTaoDataSet(Title);
			input.setString("actnKey", 		actnKey);
			input.setString("Title", 			Title);			

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			if(usrEntity != null) 
			{
				userNm		= (String)usrEntity.getName(); 
				userId 		= (String)usrEntity.getAccount();
				userMoblie1  = (String)usrEntity.getMobile1();
				userMoblie2  = (String)usrEntity.getMobile2();
				userMoblie3  = (String)usrEntity.getMobile3();
			}

			// ���� ��ȸ			
			result = con.execute("event.golfbus.GolfEvntBusInqDaoProc",input); 							
			
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));   
			paramMap.put("co_nm", userNm);
			paramMap.put("hp_ddd_no", userMoblie1);
			paramMap.put("hp_tel_hno", userMoblie2);
			paramMap.put("hp_tel_sno", userMoblie3);
			
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);
			
			DbTaoDataSet dataSet = new DbTaoDataSet(Title);

			//���� ��ȸ : ������ �ص�
			String permissionColum = "LMS_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				
			}else{
				permission = "N";
			}
			
			
			
		} catch (Throwable be) {			
			throw new GolfException(Title, be);
		} finally {
			try { if(con != null) { con.close(); } else {;} } catch(Throwable ignore) {}
		}
		return super.getActionResponse(context);
	}
}