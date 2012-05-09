/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemJoinPopActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ���� �˾�
*   �������  : golf
*   �ۼ�����  : 2009-05-19 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.myInfo;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;


import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0  
******************************************************************************/
public class GolfMemExtendPopActn extends GolfActn{
	
	public static final String TITLE = "ȸ�� > ���� �˾�";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		int topPoint = 0;					// ����Ʈ
		String golfPointComma = "";			// �ĸ��ִ� ����Ʈ
        int nMonth = 0;						// ���� ��
        int nDay = 0; 						// ���� ��
        String golfDate = "";				// ��� ����
        String presentText = "";			// ����ǰ ���� �ؽ�Ʈ
        Connection con = null;
        
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		String action_key = super.getActionKey(context);
		
		int intMemGrade = 0;

		try {
			// 01.��������üũ 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				intMemGrade = (int)usrEntity.getIntMemGrade();
			}
			debug("intMemGrade : " + intMemGrade);
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String openerType 				= parser.getParameter("openerType", "").trim();
			String openerTypeRe 			= parser.getParameter("openerTypeRe", "").trim();
			String money					= parser.getParameter("money", "1"); //1 :è�ǿ� 2:��� 3:���
			String realPayAmt				= parser.getParameter("realPayAmt", "0"); 

			String code 					= parser.getParameter("code", "");
			String evnt_no 					= parser.getParameter("evnt_no", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");
			String cupn_clss 				= parser.getParameter("cupn_clss", "");
			//-- 2009.11.12 �߰� 
			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 			= parser.getParameter("pmgds_pym_yn", "");
			String idx 						= parser.getParameter("idx", "1");

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
			String formtargetEx 			= parser.getParameter("formtargetEx", "");
			String call_actionKey 			= parser.getParameter("call_actionKey", action_key);
			
			String ctgo_seq		 			= parser.getParameter("ctgo_seq", "");
			// IBK ������ ȸ���� �Ϲ� ���� �����Ѵ�.
			if(ctgo_seq.equals("18")){
				ctgo_seq = "7";
			}
			String fromUrl 					= parser.getParameter("fromUrl", "");
			String payWay 					= parser.getParameter("payWay", "");
			
			debug("ctgo_seq : " + ctgo_seq);   

//			debug("formtarget =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + formtarget);
//			debug("idx =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + idx);
//			debug("openerType =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + openerType);
//			debug("openerTypeRe =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + openerTypeRe);
//
//			debug("code =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + code);
//			debug("evnt_no =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + evnt_no);
//			debug("cupn_ctnt =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_ctnt);
//			debug("cupn_amt =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_amt);
//			debug("cupn_type =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_type);
//			debug("pmgds_pym_yn =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + pmgds_pym_yn);


			if(openerType.equals("")){
				openerType = openerTypeRe;
			}
			paramMap.put("openerType", openerType); 
			 
			// 01. ��������üũ
			//debug("========= GolfMemJoinPopActn =========> ");
			HttpSession session	= request.getSession(true);	
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request); 
			
			UcusrinfoEntity coUserEtt   = (UcusrinfoEntity)session.getAttribute("COEVNT_ENTITY"); 
			
			String strMemChkNum = userEtt.getStrMemChkNum();
			String strEnterCorporation = "";
			
			if(coUserEtt != null)
			{
				strEnterCorporation = coUserEtt.getStrEnterCorporation();			
			}
			else
			{
				debug("coUserEtt null");
			}
			System.out.print("## GolfMemJoinPopActn |  | ID : "+userEtt.getAccount()+" | strMemChkNum :"+strMemChkNum+" | strEnterCorporation :"+strEnterCorporation+"\n");

			
			

			// ���� jsp ���� ���
			Random rand = new Random();
		    String st = String.valueOf( rand.nextInt(99999999) );
		    session.setAttribute("ParameterManipulationProtectKey",st);
			paramMap.put("ParameterManipulationProtectKey", st);
			

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ctgo_seq", ctgo_seq);
			
			
			// ������ ����ǰ ��������
			GolfMemPresentViewDaoProc present_proc = (GolfMemPresentViewDaoProc)context.getProc("GolfMemPresentViewDaoProc");
			DbTaoResult presentView = present_proc.execute(context, dataSet, request);
			// ������ ȸ�� ���� ��������
			DbTaoResult memView = present_proc.execute_extend(context, dataSet, request);

			
			// 02. ����Ʈ ��������
			//debug("========= GolfMemJoinPopActn =========> 1-2");
			//debug("�ֹε�Ϲ�ȣ : " + userEtt.getSocid()); 
			GolfPointInfoResetJtProc resetProc = (GolfPointInfoResetJtProc)context.getProc("GolfPointInfoResetJtProc");
			try
			{			
				TopPointInfoEtt pointInfo = resetProc.getTopPointInfoEtt(context, request , userEtt.getSocid());
				topPoint = pointInfo.getTopPoint().getPoint();
			}
			catch(Throwable ignore) {}

			
			//topPoint = 50000;
			golfPointComma = GolfUtil.comma(topPoint+"");

	        GregorianCalendar today = new GregorianCalendar ( );
	        nMonth = today.get ( today.MONTH ) + 1;
	        nDay = today.get ( today.DAY_OF_MONTH ); 
			golfDate = nMonth+"�� "+nDay+"��";
			
			// �ֹ���ȣ �������� 2009.12.28
			String order_no = "";
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			order_no = addPayProc.getOrderNo(context, dataSet);
			
			debug("## GolfMemJoinPopActn | order_no : " + order_no); 
			
			//paramMap.put("actionKey", action_key);
			paramMap.put("actionKey", call_actionKey);
			paramMap.put("golfPoint", topPoint+"");
			paramMap.put("golfPointComma", golfPointComma);
			paramMap.put("golfDate", golfDate);
			paramMap.put("userNM", userEtt.getName());	

			paramMap.put("idx", idx);
			paramMap.put("code", code);
			paramMap.put("evnt_no", evnt_no);
			paramMap.put("cupn_ctnt", cupn_ctnt);
			paramMap.put("cupn_amt", cupn_amt); 
			paramMap.put("cupn_clss", cupn_clss);
			//-- 2009.11.12 �߰� 
			paramMap.put("cupn_type", cupn_type); 
			paramMap.put("pmgds_pym_yn", pmgds_pym_yn);

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
			paramMap.put("order_no", order_no);
			
			paramMap.put("formtarget", formtarget);
			paramMap.put("formtargetEx", formtargetEx);
			paramMap.put("fromUrl", fromUrl);
			paramMap.put("payWay", payWay);

			paramMap.put("ctgo_seq", ctgo_seq);

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
