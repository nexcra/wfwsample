/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > ����Ʈ 
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.shop;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntShopListActn extends GolfActn{
	
	public static final String TITLE = "���� ����Ʈ";

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
		
		try { 
			String img_root = AppConfig.getAppProperty("TOP_IMG_URL_MAPPING_DIR");
			String brnd_seq_no = AppConfig.getAppProperty("TOP_BRAND_SEQ");			
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("brnd_seq_no", brnd_seq_no);

			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntShopListDaoProc proc = (GolfEvntShopListDaoProc)context.getProc("GolfEvntShopListDaoProc");		
			
			dataSet.setString("brnd_clss", "001");
			DbTaoResult evntShopListResult1 = (DbTaoResult) proc.execute(context, request, dataSet); 

			dataSet.setString("brnd_clss", "002");
			DbTaoResult evntShopListResult2 = (DbTaoResult) proc.execute(context, request, dataSet); 

			dataSet.setString("brnd_clss", "003");
			DbTaoResult evntShopListResult3 = (DbTaoResult) proc.execute(context, request, dataSet); 

			dataSet.setString("brnd_clss", "004");		
			DbTaoResult evntShopListResult4 = (DbTaoResult) proc.execute(context, request, dataSet); 
					
			
			request.setAttribute("evntShopListResult1", evntShopListResult1);
			request.setAttribute("evntShopListResult2", evntShopListResult2);
			request.setAttribute("evntShopListResult3", evntShopListResult3);
			request.setAttribute("evntShopListResult4", evntShopListResult4);
			
			paramMap.put("img_root", img_root);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
