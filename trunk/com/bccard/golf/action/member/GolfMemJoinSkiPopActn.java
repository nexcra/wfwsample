/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemJoinSkyPopActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ��Ű�Ǹű� �̺�Ʈ �۾�
*   �������  : golf
*   �ۼ�����  : 2009-05-19 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfMemJoinSkiPopActn extends GolfActn{
	public static final String TITLE = "ȸ�� > ��Ű�Ǳ� ����";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
        ResultException re;
		String subpage_key = "default";
		TaoConnection con = null;
		
		try {
			String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // ��ư
			String goPage = "/app/card/memberActn.do"; // �̵� �׼�
			int topPoint = 0;					// ����Ʈ
			String golfPointComma = "";			// �ĸ��ִ� ����Ʈ
	        int nMonth = 0;						// ���� ��
	        int nDay = 0; 						// ���� ��
	        String golfDate = "";				// ��� ����
	        String flag = "NO";
			String memClass = "";
			String account = "";
			// 00.���̾ƿ� URL ����
			String layout = super.getActionParam(context, "layout");
			request.setAttribute("layout", layout);

			String action_key = super.getActionKey(context);
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			String openerType 				= parser.getParameter("openerType", "").trim();
			String openerTypeRe 			= parser.getParameter("openerTypeRe", "").trim();
			//String money					= parser.getParameter("money", "1"); //1 :è�ǿ� 2:��� 3:���
			//String realPayAmt				= parser.getParameter("realPayAmt", "0"); 

			
			String code 					= parser.getParameter("code", "");
			String evnt_no 					= parser.getParameter("evnt_no", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");
			String cupn_clss 				= parser.getParameter("cupn_clss", "");
			//-- 2009.11.12 �߰� 
			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 			= parser.getParameter("pmgds_pym_yn", "N");		// Y:��ǰ���� N:��ǰ������
			
			debug("openerType =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + openerType);
			debug("openerTypeRe =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + openerTypeRe);
			debug("code =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + code);
			debug("evnt_no =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + evnt_no);
			debug("cupn_ctnt =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_ctnt);
			debug("cupn_amt =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_amt);
			debug("cupn_type =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_type);
			debug("pmgds_pym_yn =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + pmgds_pym_yn);

			if(openerType.equals("")){
				openerType = openerTypeRe;
			}
			paramMap.put("openerType", openerType);
			 
			// 01. ��������üũ
			//debug("========= GolfMemJoinSkyPopActn =========> ");
			HttpSession session	= request.getSession(true);	
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request); 
			
			if( userEtt == null ){
				/*re = new ResultException();
				re.setTitleImage("error");
				re.setTitleText(TITLE);
				re.setKey("UHL004_Ind_Ret");
				re.addButton(goPage, addButton);
				throw re;*/
			} else {
				memClass = userEtt.getmemberClssCard();
				account = userEtt.getAccount();
				
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("account", account);

				con = context.getTaoConnection("dbtao", null);
				TaoResult result = con.execute("member.GolfMemSkiSaleAuthDaoProc", dataSet);

				String affinm="";
				if(result.isNext()) {
					result.next();

					if("00".equals(result.getString("RESULT"))) {
						affinm = result.getString("AFFI_FIRM_NM");
						debug("=============== " + affinm);
						if("SKI".equals(affinm)) {
							flag = "OK";
						}
					}
				}

				// ���� jsp ���� ���
				Random rand = new Random();
			    String st = String.valueOf( rand.nextInt(99999999) );
			    session.setAttribute("ParameterManipulationProtectKey",st);
				paramMap.put("ParameterManipulationProtectKey", st);
				paramMap.put("flag",		flag);

				debug("�ֹε�Ϲ�ȣ : " + userEtt.getSocid()); 

				//GolfPointInfoResetJtProc resetProc = (GolfPointInfoResetJtProc)context.getProc("GolfPointInfoResetJtProc");
				//try
				//{
				//	if("1".equals(userEtt.getMemberClss())) {
				//		TopPointInfoEtt pointInfo = resetProc.getTopPointInfoEtt(context, request , userEtt.getSocid());
				//		topPoint = pointInfo.getTopPoint().getPoint();					
				//	}
				//}
				//catch(Throwable ignore) {}

				//topPoint = 50000;
				golfPointComma = GolfUtil.comma(topPoint+"");
		        GregorianCalendar today = new GregorianCalendar ( );
		        nMonth = today.get ( today.MONTH ) + 1;
		        nDay = today.get ( today.DAY_OF_MONTH ); 
				golfDate = nMonth+"�� "+nDay+"��";

				paramMap.put("actionKey", action_key);
				paramMap.put("golfPoint", topPoint+"");
				paramMap.put("golfPointComma", golfPointComma);
				paramMap.put("golfDate", golfDate);
				paramMap.put("userNM", userEtt.getName());	
				paramMap.put("code", code);
				paramMap.put("evnt_no", evnt_no);
				paramMap.put("cupn_ctnt", cupn_ctnt);
				paramMap.put("cupn_amt", cupn_amt); 
				paramMap.put("cupn_clss", cupn_clss);
				//-- 2009.11.12 �߰� 
				paramMap.put("cupn_type", cupn_type); 
				paramMap.put("pmgds_pym_yn", pmgds_pym_yn);
			}


			paramMap.put("account", account);

			//request.setAttribute("presentView", presentView);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		}finally{
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
