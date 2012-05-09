/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBkMMDetActn
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
import java.util.Map;
import java.util.HashMap;


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
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfEvntBkMMDetActn extends GolfActn{
	
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
		String intMemGrade = "";
		String permission = "";
		
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
				intMemGrade = String.valueOf((int)usrEntity.getIntMemGrade());
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";				
			}
			debug(">>>>>>>>>>>  intMemGrade : "+intMemGrade);
/*
			if(!(intMemGrade.equals("1") || intMemGrade.equals("2") || intMemGrade.equals("2"))){
				subpage_key = "deny";
				return super.getActionResponse(context, subpage_key);				
			}
	*/		
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);			
			
			paramMap.put("title", TITLE);						
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("cdhd_id",userId);           //����� ID����
			dataSet.setString("userId",userId);            //����� ID���� 
			dataSet.setString("intMemGrade", intMemGrade); //����� ���

			// 04.���� ���̺�(Proc) ��ȸ
			
			GolfEvntBkMMDaoProc proc = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");
			DbTaoResult evntResult = (DbTaoResult)proc.execute(context, dataSet);
			String str_memGradeTxt = proc.getAllGrade(context, dataSet, userId);
			String cnt = "0";
			String tot_cnt = "0";
			String can_cnt = "0";

			
			// 06.�ѽ���ī��Ʈ ���ϱ�
			GolfBkBenefitTimesDaoProc proc_count = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult evntMMInq = proc_count.getPreBkEvntBenefit2(context, dataSet, request);
			if(evntMMInq.isNext()){
				evntMMInq.next();
				
				tot_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_PMI_NUM"));
				cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_DONE"));
				can_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_MO"));
				debug("Actn : ���Ǽ�: cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
				debug("Actn : �����Ǽ� : can_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + can_cnt);
				debug("Actn : �ѻ���� ���ִ°Ǽ� : tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				
			}

			paramMap.put("cnt",cnt);
			paramMap.put("tot_cnt",tot_cnt);
			paramMap.put("can_cnt",can_cnt);

			// 05. ���ٱ��� ��ȸ : ���� - 20091029 
			String permissionColum = "PMI_EVNT_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			
			debug(">>>>>>>>>> permission : "+permission);
			paramMap.put("permission", permission); 
			
			paramMap.put("memGradeNM", str_memGradeTxt);
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
