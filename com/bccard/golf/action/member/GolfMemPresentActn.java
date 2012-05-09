/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ���� > ���
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.member.*;

import com.bccard.golf.user.entity.UcusrinfoEntity;


/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemPresentActn extends GolfActn{
	
	public static final String TITLE = "ȸ�� > ���� ���� > è�Ǿ� ����ǰ ���õ��";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		int result = 0;
		String script = "";
				
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String gds_code 				= parser.getParameter("gds_code", "").trim();
			String rcvr_nm 					= parser.getParameter("rcvr_nm", "").trim();
			String zp1 						= parser.getParameter("zp1", "").trim();
			String zp2 						= parser.getParameter("zp2", "").trim();
			String addr 					= parser.getParameter("addr", "").trim();
			String dtl_addr					= parser.getParameter("dtl_addr", "").trim();
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "").trim();
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "").trim();
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "").trim();

			String call_actionKey 			= parser.getParameter("call_actionKey", "");
			String code 					= parser.getParameter("code", "");
			String evnt_no 					= parser.getParameter("evnt_no", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");
			String cupn_clss 				= parser.getParameter("cupn_clss", "");

		
			String openerType 				= parser.getParameter("openerTypeRe", "").trim();
			debug("=`=`=`=`=`=`= GolfMemPresentActn : openerType : " + openerType);
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.) 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("gds_code", gds_code);	
			dataSet.setString("rcvr_nm", rcvr_nm);	
			dataSet.setString("zp", zp1+""+zp2);	
			dataSet.setString("addr", addr);		
			dataSet.setString("dtl_addr", dtl_addr);	
			dataSet.setString("hp_ddd_no", hp_ddd_no);	
			dataSet.setString("hp_tel_hno", hp_tel_hno);	
			dataSet.setString("hp_tel_sno", hp_tel_sno);

			// 04.���� ���̺�(Proc) ��ȸ
			GolfMemPresentDaoProc proc = (GolfMemPresentDaoProc)context.getProc("GolfMemPresentDaoProc");			// ȸ��Ż�� ���μ���
			result = proc.execute(context, dataSet, request);
			//debug("=========result : " + result);



			String returnUrlTrue = call_actionKey  + ".do" ;
			String returnUrlFalse = "GolfMemPresentForm.do";
			if(openerType.equals("U")){	// ���׷��̵�
				returnUrlTrue = "GolfMtGradeUpdPop.do";
			}
				        
			if (result>0) {
				//script = "pop('GolfMemJoinPop.do','CyberMoney',660,693); self.close();";
				//request.setAttribute("resultMsg", "����ǰ�� ��û�Ǿ����ϴ�. Champion ��ȸ�� �������ֽñ� �ٶ��ϴ�.");
				//script = "opener.location.reload(); window.close()"; 
				//request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);
	        } else {
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");
				//script = "opener.location.reload(); window.close()"; 
				//request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlFalse);
	        }	
			
			// 05. Return �� ����
			paramMap.put("idx", "1");
			paramMap.put("code", code);
			paramMap.put("evnt_no", evnt_no);
			paramMap.put("cupn_ctnt", cupn_ctnt);
			paramMap.put("cupn_amt", cupn_amt); 
			paramMap.put("cupn_clss", cupn_clss);

			paramMap.put("actionKey", call_actionKey);
			paramMap.put("call_actionKey", call_actionKey);

	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
