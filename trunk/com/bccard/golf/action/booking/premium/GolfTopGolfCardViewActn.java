/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfTopGolfCardViewActn
*   �ۼ���    : ������
*   ����      : top���� ��ŷ �󼼳���
*   �������  : Golf
*   �ۼ�����  : 2010-10-20
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.*;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.dbtao.proc.booking.premium.GolfBkPreGrViewDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfTopGolfCardViewActn extends GolfActn{
	
	public static final String TITLE = "Top����ī�� �����ŷ > ȸ����ŷ > ƼŸ�� ����Ʈ-�󼼺���";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String permission = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			int cyberMoney = 0;
			String userNm = "";
			String userId = "";
			
			String msg = "";		//���� ����
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				userNm = userEtt.getName();
				userId = userEtt.getAccount();
			}
			
			// 02.�Է°� ��ȸ		
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE); 	

			// 03. ������ idx ��������
			String affi_green_seq_no = parser.getParameter("AFFI_GREEN_SEQ_NO", "");
			String nYear = parser.getParameter("nYear", "");
			String nMonth = parser.getParameter("nMonth", "");
			String nDay = parser.getParameter("nDay", "");
			String green_nm = parser.getParameter("green_nm", "");
			
			if(green_nm.equals("����")){
				msg = "kwang"; 
			}else if(green_nm.equals("����")){
				msg = "sun";
			}
			
			/*
			 * ���̳� ��¥�� 10���� ������ �տ� 0�� �ٿ���
			 * */
			if(nMonth.length() == 1){
				nMonth = "0"+nMonth;
			}
			if(nDay.length() == 1){ 
				nDay = "0"+nDay;
			}
			
			String teof_date = nYear+nMonth+nDay;
			dataSet.setString("affi_green_seq_no", affi_green_seq_no); 
			dataSet.setString("teof_date", teof_date);
			
			
			// 04.���� ���̺�(Proc) ��ȸ - ����� ��ȸ
			GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
			DbTaoResult getPaly = (DbTaoResult) proc.getPanalty(context, request, dataSet);
			request.setAttribute("getPaly", getPaly);
			
			
			//ƼŸ�Ӹ���Ʈ
			DbTaoResult listResult = (DbTaoResult) proc.getTtimelist(context, request, dataSet);
			
			paramMap.put("AFFI_GREEN_SEQ_NO", affi_green_seq_no);	
			paramMap.put("userNm", userNm);	
			paramMap.put("userId", userId);
			paramMap.put("GREEN_NM", green_nm);
			paramMap.put("v_TEOF_DATE", nYear+"��"+nMonth+"��"+nDay+"��");
			paramMap.put("TEOF_DATE", teof_date);
			
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("msg", msg);
	        request.setAttribute("listResult", listResult);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
