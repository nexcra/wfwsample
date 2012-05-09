/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthUserInsActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : BBS 
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.bccard.golf.common.GolfActn;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.proc.admin.*;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmAuthUserInsActn extends GolfActn  {

	public static final String TITLE="�񾾰��� ��� ���� �Է�";

	/***************************************************************************************
	* �񾾰��� BBSȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
    public ActionResponse execute( WaContext context, HttpServletRequest request, HttpServletResponse response)
		throws BaseException {

		String rtnMsg = "";
		String rtnCode = "";
		int isOk = 0;

        try{
        	//debug("==== GolfAdmAuthUserInsActn start ===");
			// ������� ȭ������
			RequestParser parser = context.getRequestParser("default", request, response);
	    	String ip_addr = request.getRemoteAddr();

			DbTaoDataSet dataSet = new DbTaoDataSet("Golf ��� ���� �Է�");
			dataSet.setString("account", parser.getParameter("ACCOUNT", ""));		// ���̵�
			dataSet.setString("passwd", parser.getParameter("PASSWD", ""));			// ��ȣ
			dataSet.setString("jumin_no", parser.getParameter("JUMIN_NO", ""));		// �ֹε�Ϲ�ȣ
			dataSet.setString("name", parser.getParameter("HG_NM", ""));			// ����
			dataSet.setString("email", parser.getParameter("EMAIL", ""));			// �̸���
			dataSet.setString("com_nm", parser.getParameter("COM_NM", ""));			// ��ü��
			dataSet.setString("prs_nm", parser.getParameter("PRS_NM", ""));			// ��ǥ�ڸ�
			dataSet.setString("zipcode1", parser.getParameter("ZIPCODE1", ""));		// �����ȣ
			dataSet.setString("zipcode2", parser.getParameter("ZIPCODE2", ""));		// �����ȣ
			dataSet.setString("zipaddr", parser.getParameter("ZIPADDR", ""));		// �ּ�
			dataSet.setString("detailaddr", parser.getParameter("DETAILADDR", ""));	// ���ּ�
			dataSet.setString("tel1", parser.getParameter("TEL1", ""));				// ��ȭ1
			dataSet.setString("tel2", parser.getParameter("TEL2", ""));				// ��ȭ2
			dataSet.setString("tel3", parser.getParameter("TEL3", ""));				// ��ȭ3
			dataSet.setString("fax1", parser.getParameter("FX_DDD_NO", ""));		// �ѽ�1
			dataSet.setString("fax2", parser.getParameter("FX_TEL_HNO", ""));		// �ѽ�2
			dataSet.setString("fax3", parser.getParameter("FX_TEL_SNO", ""));		// �ѽ�3
			dataSet.setString("hp_tel_no1", parser.getParameter("HP_DDD_NO", ""));	// �ڵ���1
			dataSet.setString("hp_tel_no2", parser.getParameter("HP_TEL_HNO", ""));	// �ڵ���2
			dataSet.setString("hp_tel_no3", parser.getParameter("HP_TEL_SNO", ""));	// �ڵ���3
			dataSet.setString("memo", parser.getParameter("MEMO", ""));				// �޸�
			dataSet.setString("ip_addr", ip_addr);									// ���Ӿ�����
			dataSet.setString("INDV_INFO_RPES_NM", parser.getParameter("INDV_INFO_RPES_NM", ""));	// ��������å���ڼ���

			GolfAdmAuthUserInsDaoProc proc1 = (GolfAdmAuthUserInsDaoProc) context.getProc("GolfAdmAuthUserInsDaoProc");
			isOk = proc1.execute(context, dataSet);	
			
			if (isOk > 0  ) {
				rtnMsg = "��Ͽ� �����Ͽ����ϴ�.";
				rtnCode = "00";
			} else if (isOk == -1  ) {
					rtnMsg = "�ߺ��Ǵ� �ֹε�Ϲ�ȣ��  �ֽ��ϴ�.";
					rtnCode = "02";
			} else {
				rtnMsg = "��Ͽ� �����Ͽ����ϴ�.";
				rtnCode = "01";
			}

	    	Map paramMap = parser.getParameterMap();
		    //paramMap.put("rtnMsg", rtnMsg);
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("rtnCode", rtnCode);
			request.setAttribute("rtnMsg", rtnMsg);
		    
		    //debug("==== GolfAdmAuthUserInsActn End ===" + rtnMsg);
        }catch(Throwable t){
        	//debug("==== GolfAdmAuthUserInsActn Error ========" + t);
        	return errorHandler(context,request,response,t);
	    }
        return super.getActionResponse(context);
	}//execute end
}
