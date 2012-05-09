/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkFaqListActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ��ŷ > ��ŷ ���̵� > FAQ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.guide;

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
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComSelectListDaoProc;
import com.bccard.golf.dbtao.proc.booking.guide.*;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfBkFaqListActn extends GolfActn {
	
	public static final String TITLE = "FAQ ����Ʈ";
	
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
			Map paramMap = BaseAction.getParamToMap(request);
			
			//1. �Ķ��Ÿ �� 
			long page_no		= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size		= parser.getLongParameter("page_size", 10L);			// ����������¼�
			
			String sch_TEXT		= parser.getParameter("SCH_TEXT", "");
			String sch_FAQ_CLSS	= parser.getParameter("SCH_FAQ_CLSS", "");
			String sch_ORDER	= parser.getParameter("SCH_ORDER", "");
			String sch_SORT		= parser.getParameter("SCH_SORT", "");
			String sch_TEXT2	= parser.getParameter("SCH_TEXT2", "");
			
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			
			input.setString("SCH_TEXT",			sch_TEXT);
			input.setString("SCH_FAQ_CLSS",		sch_FAQ_CLSS);
			input.setString("SCH_ORDER",		sch_ORDER);
			input.setString("SCH_SORT",			sch_SORT);
			input.setString("SCH_TEXT2",		sch_TEXT2);
			
			// �ڵ尪 ����
			input.setString("boardCode", 		"0018");
			GolfAdmBoardComSelectListDaoProc proc_sel = (GolfAdmBoardComSelectListDaoProc)context.getProc("GolfAdmBoardComSelectListDaoProc");
			DbTaoResult boardSelectListInq = (DbTaoResult)proc_sel.execute(context, request, input);
			request.setAttribute("boardSelectListInq", boardSelectListInq);

			GolfBkFaqListDaoProc proc = (GolfBkFaqListDaoProc)context.getProc("GolfBkFaqListDaoProc");
			DbTaoResult boardListInq = (DbTaoResult)proc.execute(context, request, input);
			request.setAttribute("boardListInq", boardListInq);
			
			boardListInq.next();
			String result = boardListInq.getString("RESULT");
			if ("00".equals(result))
				request.setAttribute("total_cnt", boardListInq.getString("total_cnt"));
			else
				request.setAttribute("total_cnt", "0");

			paramMap.put("resultSize", String.valueOf(boardListInq.size()));
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComListActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
