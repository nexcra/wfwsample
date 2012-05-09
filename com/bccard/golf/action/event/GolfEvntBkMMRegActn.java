/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBkMMRegActn
*   �ۼ���    : ������ ���弱
*   ����      : �������� ��ŷ �̺�Ʈ ��û ó��
*   �������  : golf
*   �ۼ�����  : 2009-09-10
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfEvntBkMMRegActn extends GolfActn{
	
	public static final String TITLE = "9�� VIP ��ŷ �̺�Ʈ ��û ó��";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userId = "";
		String isLogin = "";   	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		//String reUrl = super.getActionParam(context, "reUrl");
		//String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {					
				userId		= (String)usrEntity.getAccount();   
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";				
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);		

			String hg_nm		= parser.getParameter("hg_nm", "");              //������̸�                         
			String email		= parser.getParameter("email", "");				 //email                    
			String hp_ddd_no	= parser.getParameter("hp_ddd_no", "");			 //����� �ڵ��� ��ȣ 010,011���                             
			String hp_tel_hno	= parser.getParameter("hp_tel_hno", "");		 //����� �ڵ��� ��ȣ    
			String hp_tel_sno	= parser.getParameter("hp_tel_sno", "");		 //����� �ڵ��� ��ȣ              			           
			String teof_date	= parser.getParameter("teof_date", "");			 //��û����    
			String teof_time	= parser.getParameter("teof_time", "");			 //��û�ð�                        
			String green_nm		= parser.getParameter("green_nm", "");			 //��û�������                         
			String memo_expl	= parser.getParameter("memo_expl", "");			 //��û����                          
			String handy		= parser.getParameter("handy", "");				 //�ڵ�                   
			String cnt          = parser.getParameter("pucnt","0");				 //���� Ƚ��                      
			String tot_cnt      = parser.getParameter("tot_cnt","0");             //�� ���డ�ɼ�
			String can_cnt      = String.valueOf(Integer.parseInt(tot_cnt) - Integer.parseInt(cnt));  //�ܿ�Ƚ��

			paramMap.put("cnt",cnt);
			paramMap.put("tot_cnt",tot_cnt);
			paramMap.put("can_cnt",can_cnt);
			
			debug(">>>>>>>>>>>>>>>>>>    cnt : "+cnt);
			debug(">>>>>>>>>>>>>>>>>>    tot_cnt : "+tot_cnt);
			debug(">>>>>>>>>>>>>>>>>>    can_cnt : "+can_cnt);

			teof_date = GolfUtil.rplc(teof_date, "-", ""); ;
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("co_nm",hg_nm);
			dataSet.setString("email",email);
			dataSet.setString("hp_ddd_no",hp_ddd_no);
			dataSet.setString("hp_tel_hno",hp_tel_hno);
			dataSet.setString("hp_tel_sno",hp_tel_sno);			
			dataSet.setString("teof_date",teof_date);
			dataSet.setString("teof_time",teof_time);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("memo_expl",memo_expl);
			dataSet.setString("handy",handy);
			dataSet.setString("cdhd_id",userId);
			
			boolean flag  = true;
			String mess	  = "";
			int addResult = 0;			
			
			if (userId.trim().length() == 0) { flag = false; mess = "�����ID"; };
			if (hg_nm.trim().length() == 0) { flag = false; mess = "������ �̸�"; };
			if (email.trim().length() == 0) { flag = false; mess = "E_MAIL"; };
			if (hp_ddd_no.trim().length() == 0) { flag = false; mess = "�ڵ�����ȣ"; };
			if (hp_tel_hno.trim().length() == 0) { flag = false; mess = "�ڵ�����ȣ"; };
			if (hp_tel_sno.trim().length() == 0) { flag = false; mess = "�ڵ�����ȣ"; };
			if (teof_date.trim().length() == 0) { flag = false; mess = "��û����(��¥)"; };
			if (teof_time.trim().length() == 0) { flag = false; mess = "��û�ð�"; };
			if (handy.trim().length() == 0) { flag = false; mess = "�ڵ�"; };
			if (green_nm.trim().length() == 0) { flag = false; mess = "�����弱��"; };
			if (memo_expl.trim().length() == 0) { flag = false; mess = "��û����"; };	
			
			if (!flag){//�Է°� ���� (jsp���� ���͸��ص� ��Ȥ ���� ���� �Ѿ�� �ݺ�ó�� )
				addResult = 3;				
			}	

			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntBkMMDaoProc proc = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");
			if (flag){				
				addResult = proc.doInsert(context, dataSet);			
			}
			
	        if (addResult == 1) {
				//sms�߼�

				// SMS ���� ����
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", hg_nm);
				smsMap.put("sPhone1", hp_ddd_no);
				smsMap.put("sPhone2", hp_tel_hno);
				smsMap.put("sPhone3", hp_tel_sno);
				smsMap.put("sCallCenter", "15666578");

				debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
				String smsClss = "674";
				String message = "[Golf Loun.G] "+hg_nm+"��," + teof_date.substring(4,6) + "��" +teof_date.substring(6,8) + "��" + teof_time.substring(0,2) + "�ð��� " + green_nm + " ������ ��û �Ǿ����ϴ�" ;
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = "";
				if(!email.equals("msj9520")){
					smsRtn = smsProc.send(smsClss, smsMap, message);
				}
				debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);


				request.setAttribute("resultMsg", "���������� ��û�Ǿ����ϴ�.");
	        } else if (addResult == 2) { //�̹� ��û
				request.setAttribute("resultMsg", "������ ���ڷ� ��ûó���� �Ǿ� ��ʴϴ�. Ȯ���� �ٽ� ��û�Ͻñ� �ٶ��ϴ�.");
	        } else if (addResult == 3) { //�Է°� ���� (jsp���� ���͸��ص� ��Ȥ ���� ���� �Ѿ�� �ݺ�ó�� )
				request.setAttribute("resultMsg", mess+"��(��) ���� �Ǿ����ϴ�. ���� �޴� '��ŷ��û'�� �ٽ� Ŭ���Ͻð�  ��û�Ͻñ� �ٶ��ϴ�.\\n\\n�ݺ������� �߻��� �����ڿ� �����Ͻʽÿ�.");			
	        } else {
				request.setAttribute("resultMsg", "�� ������ �̺�Ʈ ��û�� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �߻��� �����ڿ� �����Ͻʽÿ�.");	      		
	        }
			request.setAttribute("returnUrl", "golfEvntMMInq.do");
				
			// 05. Return �� ����			
			//paramMap.put("addResult", String.valueOf(addResult));	
			
			DbTaoResult evntResult = (DbTaoResult)proc.getReserveList(context, dataSet);
			
			request.setAttribute("evntResult", evntResult);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
