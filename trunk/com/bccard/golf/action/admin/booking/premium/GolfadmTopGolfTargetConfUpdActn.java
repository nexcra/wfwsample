/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfTargetConfUpdActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ ��ŷ������ �� ó��
*   �������  : Golf
*   �ۼ�����  : 2010-11-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmPreTimeListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfadmTopGolfTargetConfUpdActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ������ �� ó��";

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
			String seq				= parser.getParameter("seq","");           //idx
			String sort				= parser.getParameter("sort","1000"); 
			String type				= parser.getParameter("type","2"); 
			
			String modeType			= parser.getParameter("modeType",""); 
			String pgrs_yn			= parser.getParameter("pgrs_yn",""); 
			
			String[] arr_seq_no = parser.getParameterValues("bkngObjNo", ""); 		// �Ϸù�ȣ
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("seq",seq);
			dataSet.setString("sort",sort);
			dataSet.setString("type",type);
			dataSet.setString("pgrs_yn",pgrs_yn);
			dataSet.setString("modeType",modeType);
			//dataSet.setObject("arr_seq_no",arr_seq_no);
			dataSet.setString("regIp",request.getRemoteAddr());
			dataSet.setString("TITLE",TITLE);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmPreTimeListDaoProc proc = (GolfadmPreTimeListDaoProc)context.getProc("admPreTimeListDaoProc");
			DbTaoResult viewResult = (DbTaoResult) proc.getConfProc(context, request, dataSet, arr_seq_no);
			
			
			
			if(viewResult != null)
			{
				viewResult.next();
				String result = viewResult.getString("RESULT");
				if ("00".equals(result))
				{
					request.setAttribute("resultCode", "11");
					request.setAttribute("resultMsg", "���������� �򰡵Ǿ����ϴ�.");
					request.setAttribute("resultGoUrl", "admTopGolfTargetList.do");
				}				
				else
				{
					request.setAttribute("resultCode", "22");
					request.setAttribute("resultMsg", "������ �߻��Ͽ����ϴ�.");
					request.setAttribute("resultGoUrl", "");
				}
				
			}
			else
			{
				request.setAttribute("resultCode", "22");
				request.setAttribute("resultMsg", "������ �߻��Ͽ����ϴ�.");
				request.setAttribute("resultGoUrl", "");
			}
			
			
				
			
			
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
