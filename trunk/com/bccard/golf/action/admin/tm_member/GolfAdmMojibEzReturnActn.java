/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMojibUpdActn.java
*   �ۼ���    : E4NET ���弱
*   ����      : ������ ����
*   �������  : Golf
*   �ۼ�����  : 2009-09-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
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

import com.bccard.waf.common.DateUtil;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.security.cryptography.Base64Encoder;
import com.bccard.golf.dbtao.proc.admin.tm_member.GolfAdmMojibEzReturnProc;
/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfAdmMojibEzReturnActn extends GolfActn {
	
	public static final String TITLE = "������ ����"; 
	/***************************************************************************************
	* �񾾰��� �����ڷα��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		String subpage_key = "default";
		int re_result = 0;
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();		

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);	

			// ���� ����
			String script = "";
			String resultMsg = "";
			String returnUrl = "";

			String enc_aspOrderNum			= parser.getParameter("aspOrderNum","");
			String enc_orderNum				= parser.getParameter("orderNum","");
			String enc_result				= parser.getParameter("result","");
			
			String jumin_no1				= parser.getParameter("jumin_no1","");
			String jumin_no2				= parser.getParameter("jumin_no2","");
			String jumin_no = jumin_no1+""+jumin_no2;
			
			String aspOrderNum			= "";
			String orderNum				= "";
			String result				= "";
			

			if(!GolfUtil.empty(enc_aspOrderNum))	aspOrderNum = new String(Base64Encoder.decode(enc_aspOrderNum));
			if(!GolfUtil.empty(enc_orderNum)) 		orderNum 	= new String(Base64Encoder.decode(enc_orderNum));
			if(!GolfUtil.empty(enc_result)) 		result 		= new String(Base64Encoder.decode(enc_result));
			
			if(GolfUtil.empty(result)){
				result = "yes";
			}
			
			if(result.equals("no")){
				script = "alert('�̹� ��� �Ǿ��ų� ��������Ʈ ��ҿ� ������ �־����ϴ�.')";
			}else{

				dataSet.setString("aspOrderNum",aspOrderNum);
				dataSet.setString("orderNum",orderNum);
				dataSet.setString("jumin_no",jumin_no);
				
				GolfAdmMojibEzReturnProc proc = new GolfAdmMojibEzReturnProc();
				re_result = (int)proc.updState(context, dataSet);	
				
				if(re_result>0){
					script = "alert('��������Ʈ�� ��� �Ǿ����ϴ�.'); parent.location.reload();";
				}else{
					script = "alert('�̹� ��� �Ǿ��ų� ��������Ʈ ��ҿ� ������ �־����ϴ�.')";
				}
			}
			
			
			paramMap.put("result", result+"");
			request.setAttribute("paramMap", paramMap);
			
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}

		return super.getActionResponse(context, subpage_key);
		
	}
}
