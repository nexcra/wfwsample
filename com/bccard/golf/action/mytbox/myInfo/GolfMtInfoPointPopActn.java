/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComListActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.myInfo;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.*;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.*;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfMtInfoPointPopActn extends GolfActn {
	
	public static final String TITLE = "������ �Խ��� ����Ʈ";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;
		ResultException rx;

		//debug("==== GolfAdmBoardComListActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();	
			//1. �Ķ��Ÿ �� 
			long page_no		= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size		= parser.getLongParameter("page_size", 10L);			// ����������¼�
						
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);

			GolfMtInfoPointPopDaoProc proc = (GolfMtInfoPointPopDaoProc)context.getProc("GolfMtInfoPointPopDaoProc");
			DbTaoResult myPointList = (DbTaoResult)proc.execute(context, request, input);
			request.setAttribute("myPointList", myPointList);
			
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComListActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
