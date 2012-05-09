/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointAdmlnqDaoActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ���� ��ܸ޴� ��������
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.common;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.initech.dbprotector.CipherClient;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.GolfAdmlnqDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmlnqDaoActn extends GolfActn {
	
	public static final String TITLE = "�񾾰���  ������  1depth �޴� ��������";
	/***************************************************************************************
	* �񾾰��� �����ڷα��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		GolfAdminEtt userEtt = null;
		DbTaoResult taoResult = null;
		String subpage_key = "default";
		String mem_nm = "";
		String mem_id = "";
		String mem_no = "";
		String rtnCode = "";
		String rtnMsg = "";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();	

			//debug("GolfPointAdmlnqDaoProc start ");
			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				mem_nm 		= (String)userEtt.getMemNm(); 
				mem_id		= (String)userEtt.getMemId(); 
				mem_no 		= (String)userEtt.getMemNo(); 
				
			}
			//2.���� ��ȸ
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("account", mem_id);
			dataSet.setString("log_p_idx", mem_no);

			GolfAdmlnqDaoProc proc = (GolfAdmlnqDaoProc)context.getProc("GolfAdmlnqDaoProc");
			taoResult = (DbTaoResult)proc.execute(context, dataSet);	// ������ ��ȸ

			if (taoResult != null ) {
				taoResult.next();
			}
			if (taoResult == null || !"00".equals(taoResult.getString("RESULT"))) {	// ��ġ�Ҷ�..
				rtnCode = "01"; 	// ������ ���°��
				rtnMsg = "������ �޴��� �����ϴ�.";
			}
			//debug("=============> rtnCode : " +  rtnCode);
			request.setAttribute("paramMap",paramMap);
			request.setAttribute("rtnCode",rtnCode);
			request.setAttribute("rtnMsg",rtnMsg);
			request.setAttribute("result",taoResult);
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
				
		return getActionResponse(context, subpage_key);
		
	}
}
