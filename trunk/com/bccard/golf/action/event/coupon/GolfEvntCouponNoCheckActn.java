/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntCouponNoCheckActn
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : �̺�Ʈ�����/����������̺�Ʈ/���������̺�Ʈ/�׸�����������->�������� ��ȣ Ȯ��
*   �������  : Golf
*   �ۼ�����  : 2011-04-11
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
***************************************************************************************************/
package com.bccard.golf.action.event.coupon;

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
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopOrdDaoProc;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopViewDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntCouponNoCheckActn extends GolfActn{
	
	public static final String TITLE = "�������� ��ȣ Ȯ��";

	/***************************************************************************************
	* ���� �ֹ� ����
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
		String userNm = "";
		String userId = "";
		String juminno = ""; 
		String juminno1 = ""; 
		String juminno2 = ""; 
		String mobile = "";
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		String chdh_non_cdhd_clss = "2";	// 1:ȸ��, 2:��ȸ��
		
		try { 
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				juminno1	= juminno.substring(0, 6);
				juminno2	= juminno.substring(6, 13);

				mobile 	= (String)usrEntity.getMobile(); 
				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
				chdh_non_cdhd_clss = "1";
				
			}
			
			paramMap.put("userNm", userNm); 
			paramMap.put("userId", userId);
			paramMap.put("juminno1", juminno1);
			paramMap.put("juminno2", juminno2);
			paramMap.put("mobile1", mobile1);
			paramMap.put("mobile2", mobile2);
			paramMap.put("mobile3", mobile3);
			paramMap.put("chdh_non_cdhd_clss", chdh_non_cdhd_clss);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
