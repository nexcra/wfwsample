/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfCardViewActn
*   �ۼ���    : ������
*   ����      : ������ > ��ŷ > ž����ī������ ��ŷ �󼼺���
*   �������  : Golf
*   �ۼ�����  : 2010-10-15
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfCardDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;

/******************************************************************************
* Golf
* @author	������ ���弱
* @version	1.0
******************************************************************************/
public class GolfadmTopGolfCardViewActn extends GolfActn{
	
	public static final String TITLE = "������ > ��ŷ > ž����ī������ ��ŷ �󼼺���";

	/***************************************************************************************
	* ���� ������ȭ��
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
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request �� ����
			String aplc_seq_no          = parser.getParameter("aplc_seq_no","");        //�����ȣ
			String golf_lesn_rsvt_no          = parser.getParameter("golf_lesn_rsvt_no","");        //ttime seq
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no",aplc_seq_no);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmTopGolfCardDaoProc proc = (GolfAdmTopGolfCardDaoProc)context.getProc("GolfAdmTopGolfCardDaoProc");

			DbTaoResult viewResult = (DbTaoResult) proc.getDetail(context, request, dataSet);

			paramMap.put("aplc_seq_no",aplc_seq_no);
			paramMap.put("golf_lesn_rsvt_no",golf_lesn_rsvt_no);
			
			request.setAttribute("viewResult", viewResult);	
	        request.setAttribute("paramMap", paramMap);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
