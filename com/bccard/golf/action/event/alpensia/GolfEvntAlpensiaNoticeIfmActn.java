/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvtAlpensiaNoticeIfmActn
*   �ۼ���	: (��)�̵������ ������
*   ����		: �̺�Ʈ > ����þ� > ��������
*   �������	: Golf
*   �ۼ�����	: 2010-06-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.alpensia;

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
import com.bccard.golf.dbtao.proc.event.alpensia.GolfEvntAlpensiaNoticeIfmDaoProc;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfEvntAlpensiaNoticeIfmActn extends GolfActn {
	
	public static final String TITLE = "�̺�Ʈ > ����þ� > ��������";
	
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
			//1. �Ķ��Ÿ �� 
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE); 
			
			GolfEvntAlpensiaNoticeIfmDaoProc proc = (GolfEvntAlpensiaNoticeIfmDaoProc)context.getProc("GolfEvntAlpensiaNoticeIfmDaoProc");
			DbTaoResult boardListInq = (DbTaoResult)proc.execute(context, request, input);
			request.setAttribute("boardListInq", boardListInq);
			
			
			//debug("==== GolfAdmBoardComListActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
