/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBusRegActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ->��������������̺�Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-09-30
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.golfbus;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntBusRegActn extends AbstractAction {

	public static final String TITLE = "������ ����� ���� ���� ��� ó��";
	
	/**
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionResponse
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request,
		HttpServletResponse response) throws IOException, ServletException,
			BaseException
	{
		TaoConnection 		con 				= null;
		TaoResult 			result  			= null;		
		Map 				paramMap 			= null;
		String				userId				= "";
		
		try {
			// form parameter parsing
			RequestParser parser 				= context.getRequestParser("default", request, response);						
			paramMap 							= (Map)request.getAttribute("paramMap");
			if(paramMap == null) 	   paramMap = parser.getParameterMap();
			String actnKey 						= super.getActionKey(context);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {
				userId = (String)usrEntity.getAccount(); 
			}
			 
			String green_nm						= parser.getParameter("green_nm");						// ������
			String teof_date					= parser.getParameter("teof_date");						// ��¥
			String co_nm						= parser.getParameter("co_nm");							// ������ �̸�
			String golf_mgz_dlv_pl_clss			= parser.getParameter("golf_mgz_dlv_pl_clss");			// ��û�ο�
			String hp_ddd_no					= parser.getParameter("hp_ddd_no");						// �޴���
			String hp_tel_hno					= parser.getParameter("hp_tel_hno");					// �޴���
			String hp_tel_sno					= parser.getParameter("hp_tel_sno");					// �޴���
			String email						= parser.getParameter("email");							// email
			
			String jumin1						= parser.getParameter("jumin1");						// �ֹι�ȣ1
			String jumin2						= parser.getParameter("jumin2");						// �ֹι�ȣ2
			String jumin 						= jumin1+"-"+jumin2;
			/*
			String[] arrtrNm = parser.getParameterValues("trNm");
			String[] arrtrTel1 = parser.getParameterValues("trTel1");
			String[] arrtrTel2 = parser.getParameterValues("trTel2");
			String[] arrtrTel3 = parser.getParameterValues("trTel3");
			String[] arrtrMem = parser.getParameterValues("trMem");
			
			int arrCnt = 0;
			arrCnt = arrtrNm.length;		// ��û�� ��
			String trAllValue = "";			// ��û�� ���

			for(int i=0; i<arrtrNm.length; i++ ) {
				if (i>0) {
					trAllValue += " / ";
				}
				trAllValue += arrtrNm[i];
				trAllValue += "|" + arrtrTel1[i];
				trAllValue += "-" + arrtrTel2[i];
				trAllValue += "-" + arrtrTel3[i];
				trAllValue += "|" + arrtrMem[i];
			}
			*/

			con = context.getTaoConnection("dbtao",null);

			// Proc �Ķ���� ����
			TaoDataSet input 					= new DbTaoDataSet(TITLE);
			input.setString("userId", 			userId);
			input.setString("actnKey", 			actnKey);
			input.setString("Title", 			TITLE);					
			
			input.setString("green_nm",			green_nm);
			input.setString("teof_date",		teof_date);
			input.setString("co_nm",			co_nm);
			input.setString("golf_mgz_dlv_pl_clss",		golf_mgz_dlv_pl_clss);
			input.setString("hp_ddd_no",		hp_ddd_no);
			input.setString("hp_tel_hno",		hp_tel_hno);
			input.setString("hp_tel_sno",		hp_tel_sno);
			input.setString("email",			email);
			input.setString("trAllValue",			jumin);		//�������� �ֹι�ȣ�� ��ü
			//input.setString("trAllValue",		trAllValue);
			//input.setInt("arrCnt",				arrCnt);

			// DB ó��
			result = con.execute("event.golfbus.GolfEvntBusInsDaoProc",input);	

			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));
			//paramMap.put("arrCnt", arrCnt + "");
						
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);
			
		} catch (BaseException be) {
			throw be;
		} catch (Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} finally {
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return getActionResponse(context, "default");
	}
	
	
}
