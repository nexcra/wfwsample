/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMtGradePayActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� ���׷��̵� > ���� ��� ����
*   �������  : golf
*   �ۼ�����  : 2010-07-09
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.myInfo;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentViewDaoProc;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.GolfMtGradePayDaoProc;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.GolfMtInfoViewDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMtGradePayActn extends GolfActn{
	
	public static final String TITLE = "ȸ�� ���׷��̵� > ���� ��� ����";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			String idx					= parser.getParameter("idx", "");	// ���׷��̵� �� ���
			String ctgo_seq				= GolfUtil.lpad(idx, 4, "0");
			
			
			
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// ����
			String fromUrl 					= parser.getParameter("fromUrl", "");
			String code 					= parser.getParameter("code", "");
			String evnt_no 					= parser.getParameter("evnt_no", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");
			String cupn_clss 				= parser.getParameter("cupn_clss", "");
			
			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 			= parser.getParameter("pmgds_pym_yn", "");
			String realPayAmt 				= parser.getParameter("realPayAmt", "");
			
			String userNm	= "";
			int topPoint 	= 0;					// ����Ʈ
			String golfPointComma = "";			// �ĸ��ִ� ����Ʈ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null)
			{
				userNm = userEtt.getName();
				
				
				GolfPointInfoResetJtProc resetProc = (GolfPointInfoResetJtProc)context.getProc("GolfPointInfoResetJtProc");
				try
				{			
					TopPointInfoEtt pointInfo = resetProc.getTopPointInfoEtt(context, request , userEtt.getSocid());
					topPoint = pointInfo.getTopPoint().getPoint();
					golfPointComma = GolfUtil.comma(topPoint+"");
				}
				catch(Throwable ignore) {}
				
			}
			
			paramMap.put("fromUrl", fromUrl);
			paramMap.put("realPayAmt", realPayAmt);

			paramMap.put("idx", idx);
			paramMap.put("code", code);
			paramMap.put("evnt_no", evnt_no);
			paramMap.put("cupn_ctnt", cupn_ctnt);
			paramMap.put("cupn_amt", cupn_amt); 
			paramMap.put("cupn_clss", cupn_clss);
			
			paramMap.put("cupn_type", cupn_type); 
			paramMap.put("pmgds_pym_yn", pmgds_pym_yn);

			// ����ǰ

			String gds_code 				= parser.getParameter("gds_code", "");
			String name 					= parser.getParameter("rcvr_nm", "");
			String zp1 						= parser.getParameter("zp1", "");
			String zp2 						= parser.getParameter("zp2", "");
			String zipaddr 					= parser.getParameter("addr", "");
			String detailaddr 				= parser.getParameter("dtl_addr", "");
			String addr_clss 				= parser.getParameter("addr_clss", "");
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "");
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "");
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "");
			String gds_code_name 			= parser.getParameter("gds_code_name", "");
			String formtarget 				= parser.getParameter("formtarget", "");
			String openerType 				= parser.getParameter("openerType", "");
			
			
			debug("idx : " + idx + " / ctgo_seq : " + ctgo_seq + " / fromUrl : " + fromUrl + " / cupn_type : " + cupn_type 
					+ " / code : " + code + " / evnt_no : " + evnt_no + " / cupn_ctnt : " + cupn_ctnt + " / cupn_amt : " + cupn_amt
					+ " / cupn_clss : " + cupn_clss + " / pmgds_pym_yn : " + pmgds_pym_yn + " / gds_code_name : " + gds_code_name);

			paramMap.put("gds_code", gds_code);
			paramMap.put("name", name);
			paramMap.put("zp1", zp1);
			paramMap.put("zp2", zp2);
			paramMap.put("zipaddr", zipaddr);
			paramMap.put("detailaddr", detailaddr);
			paramMap.put("addr_clss", addr_clss);
			paramMap.put("hp_ddd_no", hp_ddd_no);
			paramMap.put("hp_tel_hno", hp_tel_hno);
			paramMap.put("hp_tel_sno", hp_tel_sno);
			paramMap.put("gds_code_name", gds_code_name);

			paramMap.put("formtarget", formtarget);
			paramMap.put("openerType", openerType);
			

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ctgo_seq", ctgo_seq);
			
			// 04.���� ���̺�(Proc) ��ȸ
			// ������Ʈ �� ȸ������ ��������
			String up_anl_fee = "";
			GolfMtGradePayDaoProc pay_proc = (GolfMtGradePayDaoProc)context.getProc("GolfMtGradePayDaoProc");
			DbTaoResult memView = pay_proc.execute(context, dataSet, request);
			if (memView != null && memView.isNext()){	
				memView.next();
				up_anl_fee = memView.getString("ANL_FEE");
				up_anl_fee = GolfUtil.replace(up_anl_fee, ",", "");
			}
			debug("up_anl_fee : " + up_anl_fee);
			paramMap.put("up_anl_fee", up_anl_fee);
			
			// ������ ����ǰ ��������
			GolfMemPresentViewDaoProc present_proc = (GolfMemPresentViewDaoProc)context.getProc("GolfMemPresentViewDaoProc");
			DbTaoResult presentView = present_proc.execute(context, dataSet, request);

			// ���������
			String upd_pay = "";
			String anl_fee = "";
			String payWay = "";
			GolfMtInfoViewDaoProc proc = (GolfMtInfoViewDaoProc)context.getProc("GolfMtInfoViewDaoProc");
			DbTaoResult myInfo = proc.execute(context, dataSet, request);
			if (myInfo != null && myInfo.isNext()){	
				myInfo.next();
				upd_pay = myInfo.getString("UPD_PAY");
				anl_fee = myInfo.getString("ANL_FEE");
				payWay = myInfo.getString("payWay");
			}
			debug("upd_pay : " + upd_pay + " / anl_fee : " + anl_fee + " / payWay : " + payWay);
			paramMap.put("upd_pay", upd_pay); 
			paramMap.put("anl_fee", anl_fee); 
			paramMap.put("payWay", payWay); 
			
			int int_anl_fee = 0;
			String txt_anl_fee = "";
			
			if(upd_pay.equals("half")){
				int_anl_fee = Integer.parseInt(up_anl_fee)-Integer.parseInt(anl_fee);
				txt_anl_fee = GolfUtil.comma(int_anl_fee+"");
			}
			paramMap.put("txt_anl_fee", txt_anl_fee); 
			

			// �ֹ���ȣ �������� 2009.12.28
			String order_no = "";
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			order_no = addPayProc.getOrderNo(context, dataSet);
			
			debug("## GolfMemJoinPopActn | order_no : " + order_no); 
			paramMap.put("order_no", order_no);
			paramMap.put("userNM", userNm);	
			paramMap.put("golfPoint", topPoint+"");
			paramMap.put("golfPointComma", golfPointComma);
			
			
			// 05. Return �� ����			
	        request.setAttribute("presentView", presentView);
			request.setAttribute("memView", memView);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
	        
	        
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
