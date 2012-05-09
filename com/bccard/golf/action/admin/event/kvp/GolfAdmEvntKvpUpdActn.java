/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntKvpUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > Kvp > ���� ����
*   �������  : golf
*   �ۼ�����  : 2010-03-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.kvp;

import java.io.IOException;
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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.kvp.GolfAdmEvntKvpUpdDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfAdmEvntKvpUpdActn extends GolfActn{

	public static final String TITLE = "������ > �̺�Ʈ > Kvp > ���� ����";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 02.�Է°� ��ȸ�Ѵ�.
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// �������� ����
			String upd_type			= parser.getParameter("upd_type", "");
			String aplc_seq_no		= parser.getParameter("aplc_seq_no", "");
			String pgrs_yn			= parser.getParameter("pgrs_yn", "");
			String pay_no			= parser.getParameter("pay_no", "");
			String jumin_no			= parser.getParameter("jumin_no", "");
			String cslt_yn			= parser.getParameter("cslt_yn", "");
			String pay_box			= parser.getParameter("pay_box", "");
			String cdhd_id			= parser.getParameter("cdhd_id", "");
			int cmmCode				= Integer.parseInt(parser.getParameter("CMMCODE", ""));
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			dataSet.setString("pgrs_yn", pgrs_yn);
			dataSet.setString("pay_no", pay_no);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("cslt_yn", cslt_yn);
			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setInt("cmmCode", cmmCode);

			// Proc ���� ����
			GolfAdmEvntKvpUpdDaoProc proc = (GolfAdmEvntKvpUpdDaoProc)context.getProc("GolfAdmEvntKvpUpdDaoProc");

			// ���Ϻ���
			int editResult = 0; 
			String script = "";
			String upd_type_str = "";
			String resultMsg = "";
			
			
			if(upd_type.equals("upd")){		// ��޼��� 
				editResult = proc.execute(context, request, dataSet);	
				upd_type_str = "���࿩�� ����";
				script = "parent.location.href='admEvntKvpList.do';";
			}else if(upd_type.equals("cancel")){
				editResult = 1;
				resultMsg = proc.execute_cancel(context, request, dataSet);	
				upd_type_str = "�������";
				resultMsg = upd_type_str+" "+resultMsg;
				script = "parent.location.reload();";
			}
			
			
			if (editResult > 0) {
				if(resultMsg.equals("")){
					resultMsg = upd_type_str+"��(��) ���������� ó�� �Ǿ����ϴ�.";
				}
	        } else {
				resultMsg = upd_type_str+"��(��) ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.";		
	        }


			debug("upd_type : " + upd_type + " / aplc_seq_no : " + aplc_seq_no + " / pgrs_yn : " + pgrs_yn + " / editResult : " + editResult 
					+ " / script : " + script + " / resultMsg : " + resultMsg);
			 

			// 05. Return �� ����
			request.setAttribute("script", script);
			request.setAttribute("resultMsg", resultMsg);  
			request.setAttribute("returnUrl", "admEvntKvpList.do");
			
			paramMap.remove("pay_box"); 
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
