/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBnstBaseFormActn
*   �ۼ���    : ������
*   ����      : �̺�Ʈ ����� > ���� ����� �̺�Ʈ > �������� �̺�Ʈ > ������û
*   �������  : Golf
*   �ۼ�����  : 2010-10-05
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100524	������	6�� �̺�Ʈ
* golfloung		20110323	�̰��� 	���̽�ĳ����� 
***************************************************************************************************/
package com.bccard.golf.action.event.benest;

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
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntMngBaseDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntBnstGetInfoActn extends GolfActn{
	
	public static final String TITLE = "������û �� >  �ֹι�ȣ�� ������ ���� ����";

	/***************************************************************************************
	* ���� �����ȭ�� 
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

		// ���� ����
		String script = "";
		String socid ="";
		String gubun ="";
		
		try { 

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			socid = parser.getParameter("JUMIN_NO");
			gubun = parser.getParameter("gubun");
			
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE);			
			
			// param�������� SEQ_NO
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("gubun", gubun);// shop : ���̽�ĳ��
			
			GolfEvntMngBaseDaoProc proc = (GolfEvntMngBaseDaoProc)context.getProc("GolfEvntMngBaseDaoProc");
			
			// 04. ��û�� �󼼺���
			
			dataSet.setString("socid", socid);
			DbTaoResult appResult = null ;
			
			
			if (gubun != null && gubun != ""){
				
				if (gubun.equals("shop")){//���̽�ĳ�� ����				
					appResult = (DbTaoResult) proc.getShopUserInfo(context, request, dataSet);
					subpage_key = "ifrShop";
				}
				
			}else{//����ȸ���� ���				
				appResult = (DbTaoResult) proc.getUserInfo(context, request, dataSet);
			}
			
			paramMap.put("socid", socid);	
			paramMap.put("gubun", gubun);
			request.setAttribute("appResult", appResult);
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("socid", socid);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
