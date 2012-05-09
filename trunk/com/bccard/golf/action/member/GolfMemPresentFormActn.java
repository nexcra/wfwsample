/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : ����� Ŭ���Ͽ콺 > ���̹� �Ӵ� �ȳ� > ���̹� �Ӵ� �ȳ�
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ ����
*   �������  : golf
*   �ۼ�����  : 2009-05-25
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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;

/******************************************************************************
* Golf 
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMemPresentFormActn extends GolfActn{
	
	public static final String TITLE = "����� Ŭ���Ͽ콺 > ���̹� �Ӵ� �ȳ� > ���̹� �Ӵ� �ȳ�";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String actnKey = super.getActionKey(context);
		
		String name = "";	
		String zipaddr = "";	
		String detailaddr = "";	
		String addrClss = "";
		String zp1 = "";	
		String zp2 = "";
		String pre_name = "";

		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);			
			Map paramMap = parser.getParameterMap();
			
			paramMap.put("title", TITLE);
			String openerType 				= parser.getParameter("openerType", "");
			String call_actionKey 			= parser.getParameter("call_actionKey", "");
			String code 					= parser.getParameter("code", "");
			String evnt_no 					= parser.getParameter("evnt_no", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");
			String cupn_clss 				= parser.getParameter("cupn_clss", "");
			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 			= parser.getParameter("pmgds_pym_yn", "");
			
			String gds_code 				= parser.getParameter("gds_code", "");
			name 							= parser.getParameter("rcvr_nm", "");
			zp1 							= parser.getParameter("zp1", "");
			zp2 							= parser.getParameter("zp2", "");
			zipaddr 						= parser.getParameter("addr", "");
			detailaddr 						= parser.getParameter("dtl_addr", "");
			addrClss 						= parser.getParameter("addrClss", "");
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "");
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "");
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "");
			String gds_code_name 			= parser.getParameter("gds_code_name", "");
			String formtarget 				= parser.getParameter("formtarget", "");
			String formtargetEx 			= parser.getParameter("formtargetEx", "");
			String ctgo_seq 				= parser.getParameter("ctgo_seq", "");
			String fromUrl 					= parser.getParameter("fromUrl", "");
			String payWay 					= parser.getParameter("payWay", "");
			String realPayAmt 				= parser.getParameter("realPayAmt", "");
			
			String flag 					= parser.getParameter("flag", "");
			String idx 						= parser.getParameter("idx", "");
			
			String social_id_1 				= parser.getParameter("social_id_1", "");
			String social_id_2 				= parser.getParameter("social_id_2", "");			
			String email 					= parser.getParameter("email", "");
			String ddd_no 					= parser.getParameter("ddd_no", "");
			String tel_hno 					= parser.getParameter("tel_hno", "");
			String tel_sno 					= parser.getParameter("tel_sno", "");		
			String order_no 				= parser.getParameter("allat_order_no", "");
			
			paramMap.put("idx", idx);
			paramMap.put("social_id_1", social_id_1);
			paramMap.put("social_id_2", social_id_2);
			paramMap.put("email", email);
			paramMap.put("ddd_no", ddd_no);
			paramMap.put("tel_hno", tel_hno);
			paramMap.put("tel_sno", tel_sno);
			paramMap.put("order_no", order_no);
			
			debug("============== call_actionKey :" + call_actionKey);
			
			paramMap.put("openerTypeRe", openerType);
			paramMap.put("openerType", openerType);
			paramMap.put("fromUrl", fromUrl);
			paramMap.put("payWay", payWay);
			paramMap.put("realPayAmt", realPayAmt);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMemPresentFormDaoProc proc = (GolfMemPresentFormDaoProc)context.getProc("GolfMemPresentFormDaoProc");
			DbTaoResult userInfo = proc.execute(context, dataSet, request);			
			
			if(flag.equals("") && !flag.equals("KT")) {
	
				if (userInfo != null && userInfo.isNext() && "".equals(gds_code)) {
					userInfo.first();
					userInfo.next();
					name = (String) userInfo.getString("NAME");	
					zipaddr = (String) userInfo.getString("ZIPADDR");	
					detailaddr = (String) userInfo.getString("DETAILADDR");
					addrClss = (String) userInfo.getString("ADDRCLSS");
					zp1 = (String) userInfo.getString("ZP1");	
					zp2 = (String) userInfo.getString("ZP2");			
				}
				
			}
			
			paramMap.put("name", name);
			paramMap.put("zipaddr", zipaddr);
			paramMap.put("detailaddr", detailaddr);
			paramMap.put("addr_clss", addrClss);
			paramMap.put("zp1", zp1);
			paramMap.put("zp2", zp2);			
			paramMap.put("hp_ddd_no", hp_ddd_no);
			paramMap.put("hp_tel_hno", hp_tel_hno);
			paramMap.put("hp_tel_sno", hp_tel_sno);
			paramMap.put("gds_code", gds_code);
			paramMap.put("gds_code_name", gds_code_name);
			paramMap.put("formtarget", formtarget);
			paramMap.put("formtargetEx", formtargetEx);

			if(flag.equals("") && !flag.equals("KT")) {
				// ������ ����ǰ ��������
				GolfMemPresentViewDaoProc present_proc = (GolfMemPresentViewDaoProc)context.getProc("GolfMemPresentViewDaoProc");
				DbTaoResult presentView = present_proc.execute(context, dataSet, request);
	
				if (presentView != null && presentView.isNext()) {
					presentView.first();
					presentView.next();
					if(presentView.getString("RESULT").equals("00")){
						pre_name = (String) presentView.getString("PRE_NAME");	
					}
				}
			}
			
			paramMap.put("pre_name", pre_name);
			paramMap.put("actionKey", call_actionKey);
			paramMap.put("code", code);
			paramMap.put("evnt_no", evnt_no);
			paramMap.put("cupn_ctnt", cupn_ctnt);
			paramMap.put("cupn_amt", cupn_amt); 
			paramMap.put("cupn_clss", cupn_clss);
			paramMap.put("cupn_type", cupn_type);
			paramMap.put("pmgds_pym_yn", pmgds_pym_yn);
			paramMap.put("ctgo_seq", ctgo_seq);

			// 05. Return �� ����
			//request.setAttribute("userInfo", userInfo);	
			//request.setAttribute("presentView", presentView);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
