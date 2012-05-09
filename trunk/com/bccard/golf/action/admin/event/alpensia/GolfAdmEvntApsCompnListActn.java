/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntApsCompnListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > ����þ� > �󼼺���
*   �������  : Golf 
*   �ۼ�����  : 2010-06-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.alpensia;

import java.io.IOException;
import java.util.Calendar;
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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.alpensia.GolfAdmEvntApsCompnListDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntApsCompnListActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ > ����þ� > �󼼺���";

	/***************************************************************************************
	* ��������� ������ȭ��
	* @param context		WaContext ��ü.  
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.  
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// �˻���		APLC_SEQ_NO
			String aplc_seq_no			= parser.getParameter("aplc_seq_no", "");	
			String golf_svc_aplc_clss	= parser.getParameter("golf_svc_aplc_clss", "");	
			

			String max_seq_no = "0";
			DbTaoResult teamResult = null;
			DbTaoResult listResult = null;
			DbTaoResult payResult = null;
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			
			
			// 04.���� ���̺�(Proc) ��ȸ 
			GolfAdmEvntApsCompnListDaoProc proc = (GolfAdmEvntApsCompnListDaoProc)context.getProc("GolfAdmEvntApsCompnListDaoProc");
			
			// 04-1. ���� ����
			DbTaoResult listReResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// 04-2. �����̺�
			if(!golf_svc_aplc_clss.equals("8001")){
				teamResult = (DbTaoResult) proc.execute_team(context, request, dataSet);
			}
			
			// 04-3. ������ ���̺�
			listResult = (DbTaoResult) proc.execute_list(context, request, dataSet);
			while(listResult != null && listResult.isNext()){
				listResult.next();
				max_seq_no = listResult.getString("max_seq_no");
				
			}
						
			// 04-4. ��������Ʈ
			payResult = (DbTaoResult) proc.execute_pay(context, request, dataSet);
			
			// 04-5. ��û�ݾ�
			String code = "";		// �����ڵ�
			code = "0059";
			dataSet.setString("code", code);
			DbTaoResult amtResult = (DbTaoResult) proc.execute_amt(context, request, dataSet);
			
			code = "0060";
			dataSet.setString("code", code);
			DbTaoResult optResult = (DbTaoResult) proc.execute_opt(context, request, dataSet);
			
			
						

			request.setAttribute("listReResult", listReResult);
			request.setAttribute("listResultTot", max_seq_no);
			request.setAttribute("ListResult", listResult);
			request.setAttribute("payResult", payResult);
			request.setAttribute("teamResult", teamResult);
			request.setAttribute("golf_svc_aplc_clss", golf_svc_aplc_clss);
			request.setAttribute("amtResult", amtResult);
			request.setAttribute("optResult", optResult);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
