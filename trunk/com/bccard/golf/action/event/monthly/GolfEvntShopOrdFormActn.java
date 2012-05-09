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
package com.bccard.golf.action.event.monthly;

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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopListDaoProc;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopViewDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntShopOrdFormActn extends GolfActn{
	
	public static final String TITLE = "���� �ֹ� ������";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		
		String userNm = "";
		String userId = "";
		String juminno = ""; 
		String juminno1 = ""; 
		String juminno2 = ""; 
		String zip_code1 = "";
		String zip_code2 = "";
		String zipaddr = "";
		String detailaddr = "";
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try { 
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String gds_code			= parser.getParameter("gds_code", "");			// ��ǰ�ڵ�
			String sgl_lst_itm_code	= parser.getParameter("sgl_lst_itm_code", "");	// �ɼ�
			String qty				= parser.getParameter("qty","");				// ����
			
			paramMap.put("gds_code", gds_code);
			paramMap.put("sgl_lst_itm_code", sgl_lst_itm_code);
			paramMap.put("qty", qty);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntShopViewDaoProc proc = (GolfEvntShopViewDaoProc)context.getProc("GolfEvntShopViewDaoProc");
			
			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				juminno1	= juminno.substring(0, 6);
				juminno2	= juminno.substring(6, 13);
				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
				
				dataSet.setString("userId", userId);
				DbTaoResult memEtt = (DbTaoResult) proc.execute_mem(context, request, dataSet);
				if (memEtt !=null && memEtt.isNext() && memEtt.size() > 0) {
					memEtt.next();							
					zip_code1 = memEtt.getString("ZIP_CODE1");
					zip_code2 = memEtt.getString("ZIP_CODE2");
					zipaddr = memEtt.getString("ZIPADDR");
					detailaddr = memEtt.getString("DETAILADDR");
				}

				paramMap.put("userNm", userNm);
				paramMap.put("userId", userId);
				paramMap.put("juminno1", juminno1);
				paramMap.put("juminno2", juminno2);
				paramMap.put("mobile1", mobile1);
				paramMap.put("mobile2", mobile2);
				paramMap.put("mobile3", mobile3);
				paramMap.put("zip_code1", zip_code1);
				paramMap.put("zip_code2", zip_code2);
				paramMap.put("zipaddr", zipaddr);
				paramMap.put("detailaddr", detailaddr);
			}
			
			debug("userNm : " + userNm + " / userId : " + userId + " / juminno : " + juminno + " / juminno1 : " + juminno1 + " / juminno2 : " + juminno2
					+ " / mobile1 : " + mobile1 + " / mobile2 : " + mobile2 + " / mobile3 : " + mobile3
					+ " / zip_code1 : " + zip_code1 + " / zip_code2 : " + zip_code2 + " / zipaddr : " + zipaddr + " / detailaddr : " + detailaddr);
			

	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
