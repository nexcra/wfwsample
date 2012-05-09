/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLessonRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������û ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mania;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.golf.dbtao.proc.payment.GolfPaymentInqDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfManiaPayActn extends GolfActn{
	
	public static final String TITLE = "������û ���� ȭ��";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	 
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int myPointResult =  0;
	    
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}


			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
			String nowDate = String.valueOf(cal.get(Calendar.DATE));
			
			// ���� jsp ���� ���
			Random rand = new Random();
		    String st = String.valueOf( rand.nextInt(99999999) );
		    session.setAttribute("ParameterManipulationProtectKey",st);
		    
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = parser.getParameterMap();
			paramMap.put("title", TITLE);
			paramMap.put("ParameterManipulationProtectKey", st);


			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SESS_CSTMR_ID", userId);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfPaymentInqDaoProc proc = (GolfPaymentInqDaoProc)context.getProc("GolfPaymentInqDaoProc");

			//ȸ��������������
//			DbTaoResult userInfoResult = (DbTaoResult) proc.getMyMemberInfo(context,dataSet);			
//			
//			if (userInfoResult != null && userInfoResult.isNext()) {
//				userInfoResult.first();
//				userInfoResult.next();
//				if (userInfoResult.getObject("RESULT").equals("00")) {
//					userJuminNo = userInfoResult.getString("SOCID");
//				}
//			}

			debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			debug("userJuminNo ::: > " + juminno);
			//���� ����Ʈ���� �������� jolt
			if(juminno != null && !"".equals(juminno)) {
				myPointResult = (int)proc.getMyPointInfo(context, request, juminno);
			}
			
			debug("myPointResult ::: > " + myPointResult);
			debug("st :: > " + st);
			debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
						
			// 05. Return �� ����	
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("juminno", juminno);
			paramMap.put("myPoint", String.valueOf(myPointResult));
			paramMap.put("nowMonth", nowMonth);
			paramMap.put("nowDate", nowDate);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
