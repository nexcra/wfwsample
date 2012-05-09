/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopViewActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > �󼼺��� 
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
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopViewDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfEvntShopViewActn extends GolfActn{
	
	public static final String TITLE = "���� > �󼼺���";

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
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String gds_code		= parser.getParameter("gds_code", "");			// GDS_CODE
			String img_root = AppConfig.getAppProperty("TOP_IMG_URL_MAPPING_DIR");
			paramMap.put("img_root", img_root);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("gds_code", gds_code);
			

			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntShopViewDaoProc proc = (GolfEvntShopViewDaoProc)context.getProc("GolfEvntShopViewDaoProc");
			DbTaoResult goodsEtt = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// 04-2. �ɼ� = �ɼǻ�ǰ �� '����'�� �ִ� ��� '���û��׾���'���� �ٲ㼭 ǥ���ϱ�
			String goodsOptDisy = "1";
			DbTaoResult goodsOptListResult = (DbTaoResult) proc.execute_opt(context, request, dataSet);	
			if (goodsOptListResult !=null && goodsOptListResult.isNext() && goodsOptListResult.size() > 0) {
				goodsOptListResult.next();		
				if (goodsOptListResult.size() == 1 && goodsOptListResult.getObject("SGL_LST_ITM_DTL_CTNT").equals("����")) {	goodsOptDisy = "0";	}
			}
			
			paramMap.put("goodsOptDisy", goodsOptDisy);
			if(goodsOptListResult != null) request.setAttribute("optResult", goodsOptListResult);
			if(goodsOptListResult != null) paramMap.put("optResultSize", String.valueOf(goodsOptListResult.size()));
			
	        request.setAttribute("goodsEtt", goodsEtt);
	        request.setAttribute("paramMap", paramMap);
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
