/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmOrdUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > ���� > ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2010-03-04
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
*20110323  �̰��� 	���̽�ĳ�� ����
***************************************************************************************************/
package com.bccard.golf.action.admin.event.shop;

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
import com.bccard.golf.dbtao.proc.admin.event.shop.GolfAdmOrdUpdDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfAdmOrdUpdActn extends GolfActn{

	public static final String TITLE = "������ > �̺�Ʈ > ���� > ���� ���� ó��";

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
			String buy_yn			= parser.getParameter("BUY_YN", "");
			String dlv_yn			= parser.getParameter("DLV_YN", "");
			String refund_yn		= parser.getParameter("REFUND_YN", "");
			String ord_no			= parser.getParameter("odr_no", "");
			String cdhd_id			= parser.getParameter("cdhd_id", "");
			String sttl_stat_clss	= parser.getParameter("sttl_stat_clss", "");
			String jumin_no			= parser.getParameter("jumin_no", "");
			
			if(GolfUtil.empty(cdhd_id)){
				cdhd_id = jumin_no;
			}
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("buy_yn", buy_yn);
			dataSet.setString("dlv_yn", dlv_yn);
			dataSet.setString("refund_yn", refund_yn);
			dataSet.setString("ord_no", ord_no);
			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("sttl_stat_clss", sttl_stat_clss);

			// Proc ���� ����
			GolfAdmOrdUpdDaoProc proc = (GolfAdmOrdUpdDaoProc)context.getProc("GolfAdmOrdUpdDaoProc");
			int editResult = proc.execute(context, request, dataSet);	
			
			if (editResult == 1) {
				request.setAttribute("resultMsg", "������ ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			request.setAttribute("returnUrl", "admOrdList.do?gubun=B");
				
			
			// 05. Return �� ����
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
