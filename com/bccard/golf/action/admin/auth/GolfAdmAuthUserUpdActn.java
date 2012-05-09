/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthUserUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����(action)
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfUtil;
//import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfAdminEtt;
//import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.*;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import com.bccard.golf.msg.MsgEtt;
/*************************************************************
 * �ۼ��� �׼�
 * @author 
 * @version 2008.12.19 
 ************************************************************/
public class GolfAdmAuthUserUpdActn extends GolfActn {

	public static final String TITLE ="GOLF ��� ���� ����PROC";

	/********************************************************************
	* EXECUTE
	* @param context		WaContext ��ü.
	* @param request		HttpServletRequest ��ü.
	* @param response		HttpServletResponse ��ü.
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.
	******************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) {
		String rtnCode = "";
		String rtnMsg = "";

		try{
			//debug("==== GolfAdmAuthUserUpdActn ���� start ===");			
			String ins_result = "01";
			RequestParser parser = context.getRequestParser("default", request, response);

			String ip_addr = request.getRemoteAddr();

			//debug("p_idx============" + parser.getParameter("p_idx","") + "/n");

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("name", parser.getParameter("HG_NM", ""));			// ����
			dataSet.setString("jumin_no", parser.getParameter("JUMIN_NO", ""));		// �ֹε�Ϲ�ȣ
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
			dataSet.setString("p_idx", parser.getParameter("p_idx", ""));		// seq
			dataSet.setString("INDV_INFO_RPES_NM", parser.getParameter("INDV_INFO_RPES_NM", ""));		// ��������å����

			GolfAdmAuthUserUpdDaoProc proc = (GolfAdmAuthUserUpdDaoProc)context.getProc("GolfAdmAuthUserUpdDaoProc");
			int isOk = proc.execute(context, dataSet);
			
			if (isOk > 0  ) {
				rtnCode = "00";
				rtnMsg = "������ �����Ͽ����ϴ�.";

				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"USER_DN_DIFFERENT", null);
				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_EXCEPTION", new String[]{contTitle});
				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"JT_RET_ERR", new String[]{contTitle, comm_seqno});
				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"�����Է�.");
				//throw new GolfException(msgEtt);
			} else {
				rtnCode = "01"; // ������ �ȵȰ��
				rtnMsg = "������ �����Ͽ����ϴ�.";
			}

			Map paramMap = parser.getParameterMap();	
			//paramMap.put("comm_seqno", comm_seqno);

			request.setAttribute("paramMap", paramMap);
			request.setAttribute("rtnMsg",rtnMsg);
			request.setAttribute("rtnCode",rtnCode);

			//debug("==== GolfAdmAuthUserUpdActn ���� End ===");
		} catch(Throwable t) {
			//debug("==== GolfAdmAuthUserUpdActn ���� Error ===");
			return errorHandler(context,request,response,t);
		}finally{
		}
		return super.getActionResponse(context);
	}
}