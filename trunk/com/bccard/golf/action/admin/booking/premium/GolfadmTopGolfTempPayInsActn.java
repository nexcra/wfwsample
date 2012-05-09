/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfTempPayInsActn
*   �ۼ���    : shin cheong gwi
*   ����      : ������ ����
*   �������  : golfloung 
*   �ۼ�����  : 2010-12-02
************************** �����̷� ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopGolfTempPayInsProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfadmTopGolfTempPayInsActn extends GolfActn {
	
	public static final String TITLE = "������ > ������ ����  ����"; 

	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException
	{
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String viewType = "default";
		
		String adminno = "";
		int admin_no = 0;
		String admin_nm = "";
		boolean rtn_flag = false;
		
		try
		{
			// 01.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(viewType, request, response);
			Map paramMap = BaseAction.getParamToMap(request);			
			paramMap.put("title", TITLE);
			
			String regIp = request.getRemoteAddr();
			String memid = parser.getParameter("memid", "0");
			String account = parser.getParameter("account", "");
			String usrName = parser.getParameter("usrName", "");
			String socid = parser.getParameter("socid", "");
			String temp_pay_yn = parser.getParameter("yn", "");
			String access = parser.getParameter("access", "");
			long pageNo = parser.getLongParameter("pageNo", 1L);
			String sh_id = parser.getParameter("sh_id", "");
			String sh_nm = parser.getParameter("sh_nm", "");
			
			
			// 02.��������üũ 
			HttpSession session = request.getSession(true);
			GolfAdminEtt userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){	
				adminno	= (String)userEtt.getMemNo();	
				admin_no = Integer.parseInt(adminno);
				admin_nm = (String)userEtt.getMemNm();						
			}		
			
			// 03.Proc �� ���� �� ���� 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setInt("admNo", admin_no);
				dataSet.setString("admNm", admin_nm);
				dataSet.setString("regIp", regIp);
				dataSet.setString("memid", memid);
				dataSet.setString("account", account);
				dataSet.setString("temp_pay_yn", temp_pay_yn);
			
			// 04.Proc ����
			GolfadmTopGolfTempPayInsProc instance = GolfadmTopGolfTempPayInsProc.getInstance();
			rtn_flag = instance.execute(context, request, dataSet);
			
			if(rtn_flag){
				debug("Success!!!!!!!!!!");
			}else{
				debug("Fail!!!!!!!!!!!");
			}
			
			// 05. Parameter Set
			paramMap.put("pageNo", String.valueOf(pageNo));
			paramMap.put("sh_id", sh_id);
			paramMap.put("sh_nm", sh_nm);
			request.setAttribute("paramMap", paramMap);
			
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);
	}
}
