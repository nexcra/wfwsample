/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemSmartCardInsActn
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ���� > ���ó�� 
*   �������  : golf 
*   �ۼ�����  : 20110608
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

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
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemSmartCardInsActn extends GolfActn{
	
	public static final String TITLE = "���� > ����Ʈī�� ����";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		int insResult = 0; 
		int grd = 0;
		
		String script = "";
		String returnUrl = "";
		String resultMsg = "";
		
		String userNm = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		String memGrade = "";
		int intMemGrade = 0;		
		
		try {			
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userMobile1 = (String)usrEntity.getMobile1();
				userMobile2 = (String)usrEntity.getMobile2();
				userMobile3 = (String)usrEntity.getMobile3();
			}
			 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			//dataSet.setString("moneyType", "13");  // ����Ʈ ī�� ��� �Ҵ� �����.. **
			
			int vals[]					= new int[2];
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMemSmartDaoProc proc = (GolfMemSmartDaoProc)context.getProc("GolfMemSmartDaoProc");			
			//insResult = proc.execute(context, dataSet, request);			
			vals = proc.execute(context, dataSet, request);
			
			//grd moneyType
		
			
			insResult = vals[0];
			grd = vals[1];
			
			dataSet.setString("moneyType", Integer.toString(grd));	
			
			
			if(insResult>0){
				
				// ȸ�� ��� ��������  
				GolfMemInsDaoProc memIns_proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
				DbTaoResult gradeView = memIns_proc.gradeExecute(context, dataSet, request);
				
				if (gradeView != null && gradeView.isNext()) {
					gradeView.first();
					gradeView.next();
					if(gradeView.getString("RESULT").equals("00")){
						memGrade = (String) gradeView.getString("memGrade").trim();	
						intMemGrade = (int) gradeView.getInt("intMemGrade");
					}
				} 				

				// ȸ����� ���� ����
				usrEntity.setMemGrade(memGrade);
				usrEntity.setIntMemberGrade((int)intMemGrade);
				usrEntity.setIntMemGrade((int)intMemGrade);
				usrEntity.setCyberMoney(0);
				
				// ���� ������ - ��������� ȸ�������� �Ϸ�Ǿ����� 00�� 00�ϱ��� ���񽺸� �̿��� �� �ֽ��ϴ�
				// ���� ������ - ��������� Goldȸ�������� �Ϸ�Ǿ�����, 00�� 00�ϱ��� ���� �̿� �����մϴ�. (����)
				HashMap smsMap = new HashMap();
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", userMobile1);
				smsMap.put("sPhone2", userMobile2);
				smsMap.put("sPhone3", userMobile3);

				SimpleDateFormat fmt = new SimpleDateFormat("MM�� dd��");   
				GregorianCalendar cal = new GregorianCalendar();
		        cal.add(cal.MONTH, 3);
		        Date edDate = cal.getTime();
		        String strEdDate = fmt.format(edDate);	// ����ȸ���Ⱓ ������
		        
				String smsClss = "674";
				//String message = "��������� Smart ȸ�������� �Ϸ�Ǿ�����, "+strEdDate+"���� ���� �̿� �����մϴ�."; 
				String message = "��������� Smart ȸ�������� �Ϸ�Ǿ����ϴ�.";
 
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = smsProc.send(smsClss, smsMap, message);
				
				returnUrl = "GolfMemJoinEnd.do";
				
			}else{
				
				resultMsg = "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.";
				returnUrl = "GolfMemMonth.do";
				
			}

			
			// 05. Return �� ����		
			request.setAttribute("script", script);
			request.setAttribute("returnUrl", returnUrl);
			request.setAttribute("resultMsg", resultMsg);  
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
