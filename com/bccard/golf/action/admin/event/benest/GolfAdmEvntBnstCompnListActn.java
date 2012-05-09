/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBnstListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > ���򺣳׽�Ʈ > ���� ����Ʈ(��������)
*   �������  : Golf
*   �ۼ�����  : 2010-03-23
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.benest;

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
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntBnstCompnListDaoProc;
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntMngListDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBnstCompnListActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ > ����ȸ > �����ڸ���Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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
			String trm_unt			= parser.getParameter("trm_unt", "");
			String type			= parser.getParameter("type", "");
			
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			dataSet.setString("trm_unt", trm_unt);
			
			
			// 04.���� ���̺�(Proc) ��ȸ 
			GolfAdmEvntBnstCompnListDaoProc proc = (GolfAdmEvntBnstCompnListDaoProc)context.getProc("GolfAdmEvntBnstCompnListDaoProc");
			// 04-1. ���� ���̺�
			DbTaoResult listReResult = (DbTaoResult) proc.execute_list(context, request, dataSet);

			//����ȸ���� �������� ��������
			GolfAdmEvntMngListDaoProc proc1 = (GolfAdmEvntMngListDaoProc)context.getProc("GolfAdmEvntMngListDaoProc");
			
			// 04-1. ����ȸ �󼼺���
			DbTaoResult viewResult = (DbTaoResult) proc1.get_cost(context, request, dataSet);
			
			
			
			// 04-2. ������ ���̺�
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			String max_seq_no = "";
			while(listResult != null && listResult.isNext()){
				listResult.next();
				max_seq_no = listResult.getString("max_seq_no");
			}
			debug("max_seq_no : " + max_seq_no);
						
			// 04-3. ��������Ʈ
			DbTaoResult payResult = (DbTaoResult) proc.execute_pay(context, request, dataSet);
			paramMap.put("trm_unt", trm_unt);	
			

			request.setAttribute("listReResult", listReResult);
			request.setAttribute("listResultTot", max_seq_no);
			request.setAttribute("ListResult", listResult);
			request.setAttribute("viewResult", viewResult);		//����ȸ���� ��������
			request.setAttribute("payResult", payResult);
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("type", type);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
