/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthSetProcChgActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ���� ���� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmUNEventInfoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmUNEventMkCntUpdateActn extends GolfActn {
	
	public static final String TITLE = "�μ� Ƚ�� ����";
	/***************************************************************************************
	* �񾾰��� �����ڷα��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx;

		//debug("==== GolfAdmAuthSetProcChgActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			// 1. �Ķ��Ÿ �� 
			String cupn_no	= parser.getParameter("cupn_no", "");	
			String fromUrl      = parser.getParameter("fromUrl" ,"");               // ���� ����
			String resStr      = "";               // ���� ����
			
			//2. ���� 
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("cupn_no",	cupn_no);
			
			
			// 3.��ũ ��ũ��Ʈ 
			GolfAdmUNEventInfoProc proc = (GolfAdmUNEventInfoProc)context.getProc("GolfAdmUNEventInfoProc");
			int updatecnt = proc.updatePrintCnt(context, cupn_no);
			if(updatecnt > 0){
				resStr = "Y";
			}else{
				resStr = "N";
			}
						
			Map paramMap = parser.getParameterMap();	
			request.setAttribute("cupn_no", cupn_no);			
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("resStr", resStr);
			request.setAttribute("fromUrl",fromUrl);
			
			//debug("==== GolfAdmAuthSetProcChgActn End ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
	

}
