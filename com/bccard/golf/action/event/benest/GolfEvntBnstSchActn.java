/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > ����Ʈ 
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.benest;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstSchDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntBnstSchActn extends GolfActn{
	
	public static final String TITLE = "���򺣳׽�Ʈ �˻�";

	/***************************************************************************************
	* ���� �����ȭ��
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

		// �����Լ�
		String resultMsg = "";
		String returnUrl = "";

		
		try { 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String jumin_no1					= parser.getParameter("jumin_no1", "");
			String jumin_no2					= parser.getParameter("jumin_no2","");
			String hp_ddd_no					= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno					= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno					= parser.getParameter("hp_tel_sno","");
			
			
			debug(" / jumin_no1 : " + jumin_no1 + " / jumin_no2 : " + jumin_no2 + " / hp_ddd_no : " + hp_ddd_no
					 + " / hp_tel_hno : " + hp_tel_hno + " / hp_tel_sno : " + hp_tel_sno);
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("jumin_no", jumin_no1+jumin_no2);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			
			
			int evtBnstRegCnt = 0;			// �̺�Ʈ ��� ����
			String evnt_pgrs_clss = "";		// �̺�Ʈ ���౸�� �ڵ� R:��û, A:���, P:��������, B:Ȯ��, C:�������, E:�������
			String aplc_seq_no = "";		// ��û�Ϸù�ȣ

			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntBnstSchDaoProc proc = (GolfEvntBnstSchDaoProc)context.getProc("GolfEvntBnstSchDaoProc");
			DbTaoResult evtBnstReg = (DbTaoResult) proc.execute(context, request, dataSet);
			

			resultMsg = "";
			returnUrl = "GolfEvntBnstSchForm.do";  
 
			debug("aplc_seq_no : " + aplc_seq_no + " / resultMsg : " + resultMsg + " / returnUrl : " + returnUrl);
			

			paramMap.put("juminno1", jumin_no1);
			paramMap.put("juminno2", jumin_no2);
			paramMap.put("mobile1", hp_ddd_no);
			paramMap.put("mobile2", hp_tel_hno);
			paramMap.put("mobile3", hp_tel_sno);
	        request.setAttribute("evtBnstReg", evtBnstReg);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
