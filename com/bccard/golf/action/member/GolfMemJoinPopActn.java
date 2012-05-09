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
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentViewDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfMemJoinPopActn extends GolfActn{
	
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
		debug(action_key);

		try {
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = parser.getParameterMap();
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
			String call_actionKey 			= parser.getParameter("call_actionKey", action_key);
			
			debug (" ### zp1 : " + zp1 + ", zp2 : "  + zp2 + ", zipaddr : " + zipaddr + ", detailaddr : " + detailaddr  + ", addr_clss : " + addr_clss);

			debug("formtarget : " + formtarget + " / idx : " + idx + " / openerType : " + openerType + " / openerTypeRe : " + openerTypeRe + " / code : " + code 
					+ " / evnt_no : " + evnt_no + " / cupn_ctnt : " + cupn_ctnt + " / cupn_amt : " + cupn_amt + " / cupn_type : " + cupn_type 
					+ " / pmgds_pym_yn : " + pmgds_pym_yn + " / gds_code_name : " + gds_code_name);

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

			
			//VIPī�� üũ �߰�
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);			
			String select_grade_no = "";
			String toDate  = DateUtil.currdate("yyyyMMdd");			
			String fromDate =  DateUtil.dateAdd('M', -3, toDate, "yyyyMMdd");
			fromDate = fromDate.substring(0,6) + "01";
			double sum_money = 0;
			double select_sum_money = 0;
			double temp_sum_money = 0;
			String select_card_no = "";
			String vipCardPayAmt	= "0";
			String vipCardYn 	= "N";		//Vipī�� ���� ����
			if(mbr != null)
			{
				select_grade_no = mbr.getVipMaxGrade();
				
				debug("## VIPī�� ���� üũ ���� | select_grade_no : "+select_grade_no);
				List cardVipList = mbr.getCardVipInfoList();
								
				if( cardVipList!=null && cardVipList.size() > 0 )
				{
					
					if(!"00".equals(select_grade_no))	// �÷�Ƽ�� ȸ���� ���	
					{
						
						for (int i = 0; i < cardVipList.size(); i++) 
						{
							vipCardYn = "Y";
							try { 
								
							
								CardVipInfoEtt record = (CardVipInfoEtt)cardVipList.get(i);
								String grade 		= (String)record.getVipGrade();
								String expDate 		= (String)record.getExpDate();
								String cardType 	= (String)record.getCardType();
								String cardNo 		= (String)record.getCardNo(); 
								String last_cardApp = (String)record.getCardAppType();
								String last_cardNo 	= (String)record.getLastCardNo();
								String bankNo 		= (String)record.getBankNo();
								String reg_date 	= (String)record.getAppDate();
								String cardJoinDate	= StrUtil.isNull((String)record.getCardJoinDate(), "");
								
								debug("## "+i+"��° | cardJoinDate : "+cardJoinDate+" | grade : "+grade+" | cardNo : "+cardNo+" | bankNo : "+bankNo+" | reg_date : "+reg_date+" | last_cardApp : "+last_cardApp+" | cardType : "+cardType);												
								
								//����üũ
								/*
								CardAppType
								11:�ű�, 12:�߰��ű�, 21:�Ѽ���߱�, 22:��޺�����߱�, 
								24:�н���߱�, 25:��޺���н���߱�, 31:�Ϲݰ���, 32:��޺����Ϲݰ���, 
								33:�ڵ�����, 34:��޺����ڵ�����, 35:���ⰻ��, 36:��޺������ⰻ��, 
								37:��������, 38:��޺�����������, 41:���Ű�
								CardType
								1:����,2:����,3:����,4:����
								*/
								if( "03".equals(grade) || "12".equals(grade) )
								{
									
									if(expDate.equals("00")){
										
										if(cardType.equals("1") || cardType.equals("3"))
										{
											sum_money = getSumMoney(cardNo,cardType,sum_money,context,request,response);
											debug("## ����ī��("+cardNo+"):"+sum_money);
											
											if(last_cardApp.equals("21") || last_cardApp.equals("24") || last_cardApp.equals("31") || last_cardApp.equals("33") || last_cardApp.equals("35") || last_cardApp.equals("37")){
												sum_money = getSumMoney(last_cardNo,cardType,sum_money,context,request,response);
												debug("## ����ī��("+last_cardNo+"):"+sum_money);
											}
											
										}
										
										
									}
									
									
									//����üũ
									//ī�尡 �߱޳�¥�� 3���� �̳��� ��� üũ
									String ckDate =  DateUtil.dateAdd('M', 3, cardJoinDate, "yyyyMMdd");											
									debug("## GolfMemJoinPopActn | ī��߱޳�¥ �� | ���ó�¥ : "+toDate+" | ī��߱޳�¥ : "+cardJoinDate+ " | ī��3�����񱳳�¥ : "+ckDate);
									
									if( Integer.parseInt(toDate) < Integer.parseInt(ckDate) )
									{
										debug("## GolfMemJoinPopActn | 3�����̳� �߱�ī���Դϴ�. last_cardApp : "+last_cardApp+" | ī��߱� 3���� �̳� ��");
										sum_money = 10000000; //�űԸ� ���� õ���� �־� �켱�ο�.....
									}
									
									/*
									if(!(Integer.parseInt(fromDate) <= Integer.parseInt(reg_date))){
	
									} else {
										//��, 30���� �̸��ΰ�� �ֱ� ���� ī��߱����ڷ� 3���� �̸� ���� 1�� 5õ�� ����. 3���� ��� ȸ���� 2���� ����
										//��, �ű� 3���� �̳��� �Ѽ���߱�, �н���߱� �� ī�带 ��߱� �纯��õ� ����üũ���� ����ó�� �Ѵ�.(1��5õ�� ���� ����)
										if (last_cardApp.equals("11") || last_cardApp.equals("12") || last_cardApp.equals("21") || last_cardApp.equals("22") || last_cardApp.equals("24") || last_cardApp.equals("25")   ) {
											debug(" last_cardApp : "+last_cardApp+" | �ű� 3���� �̳��� �Ѽ���߱�, �н���߱� ��");
											sum_money = 10000000; //�űԸ� ���� õ���� �־� �켱�ο�.....
	
										}
									}
									*/	
									temp_sum_money = sum_money;
									
									// ���� ���� ī��,ȸ����,������ �켱
									if ( select_sum_money < temp_sum_money )	{
										select_sum_money = temp_sum_money;
										select_card_no = cardNo;
										//select_bank_no = bankNo;
									}
									
									
									
									
									
									
								}
							} catch(Throwable t) {}
							//vipCardYn = "Y";
						
						}
						
						
						// PTī�尡  03,12,30,91 ���ϰ�� 1��5õ�� ����
						if( "03".equals(select_grade_no) || "12".equals(select_grade_no) || "30".equals(select_grade_no) || "91".equals(select_grade_no)  )
						{
							vipCardPayAmt = "15000";
						
						}
						
						
						debug("## �����ݾ� sum_money : " + sum_money);
						
						// ��, PTī�尡 03 , 12 �� ��쿣 �ֱ�3������ �������Ǳݾ��� 30���� �̻��� ��츸 1��5õ�� ����
						if( "03".equals(select_grade_no) || "12".equals(select_grade_no) )
						{
																		
							if (sum_money >= 300000) {
								
								vipCardPayAmt = "15000";
								
							}else if (sum_money < 300000) {
								
								vipCardPayAmt = "20000";
								
							}
						}
						
																		
						// SBS��������� ȸ������ 5õ�� �������� ȸ���� 2���� ���� (���� ���̺� ���� �� ����Ÿ ���� ����)
					
					}
					else
					{
						vipCardYn = "N";
						debug("## VIP�÷�Ƽ�� ȸ�� �ƴ�");						
					}					
				}
				else
				{
					debug("## VIPī�� ���� ����.");	
					vipCardYn = "N";
				}
			}
			System.out.print("## VIPī�� ���� �����ݾ� : vipCardPayAmt : "+vipCardPayAmt+" | select_card_no : "+select_card_no+" \n");
			paramMap.put("vipCardPayAmt", vipCardPayAmt);
			request.setAttribute("vipCardPayAmt", vipCardPayAmt);
			request.setAttribute("select_card_no", select_card_no);
			request.setAttribute("vipCardYn", vipCardYn);		
			
			
			
			
			
			///////////////////////////////////////////////////////////////////////////////////////
			// ����ȸ���� ������ ��θ� ���Ͽ� �Դ��� üũ�Ͽ� 20% �������� ���� CORPDSMEM1102
			//
			if( "Y".equals(strEnterCorporation) )
			{				 
					//��������
					System.out.print("## GolfMemJoinPopActn | ����ȸ�� 20% ���� ���� ���� | ID : "+userEtt.getAccount()+"\n");
					code = "CORPDSMEM1102";
					
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
					
					dataSet.setString("CODE", code); //���޾�ü�ڵ�
					dataSet.setString("SITE_CLSS", "10");//���޾�ü�ڵ�
					dataSet.setString("EVNT_NO", "111");//���޾�ü�ڵ�
					dataSet.setString("EVNT_NO2", "112");//���������ڵ�
					GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
					DbTaoResult codeCheck = proc.codeExecute(context, dataSet, request);
					debug("===================codeCheck : " + codeCheck);
					
					
					if (codeCheck != null && codeCheck.isNext()) 
					{
						codeCheck.first();
						codeCheck.next();
						debug("===================memGrade : " + codeCheck.getString("RESULT"));
						if(codeCheck.getString("RESULT").equals("00"))
						{
							
							evnt_no = (String) codeCheck.getString("EVNT_NO");
							cupn_ctnt = (String) codeCheck.getString("CUPN_CTNT");
							cupn_amt = ""+codeCheck.getInt("CUPN_AMT");
							cupn_clss = (String) codeCheck.getString("CUPN_CLSS");

							System.out.print("## GolfMemJoinPopActn | ��������Ǵ�üũ | ID : "+userEtt.getAccount()+" | code : "+code+" | cupn_ctnt : "+cupn_ctnt+" | cupn_amt : "+cupn_amt+" | cupn_clss : "+cupn_clss+ "\n");
							
							request.setAttribute("strMemChkNum", strMemChkNum);	
							request.setAttribute("strEnterCorporation", strEnterCorporation);	
							
							
						}
						
					}
				
			}
			
			

			// ���� jsp ���� ���
			Random rand = new Random();
		    String st = String.valueOf( rand.nextInt(99999999) );
		    session.setAttribute("ParameterManipulationProtectKey",st);
			paramMap.put("ParameterManipulationProtectKey", st);
			

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("cpn_code", code); //���޾�ü�ڵ�
			dataSet.setString("vipCardPayAmt", vipCardPayAmt);
			
			// ������ ����ǰ ��������
			GolfMemPresentViewDaoProc present_proc = (GolfMemPresentViewDaoProc)context.getProc("GolfMemPresentViewDaoProc");
			DbTaoResult presentView = present_proc.execute(context, dataSet, request);
			// ȸ������ ��������
			DbTaoResult memView = present_proc.execute_mem(context, dataSet, request);

			
			// 02. ����Ʈ ��������
			//debug("========= GolfMemJoinPopActn =========> 1-2");
			debug("�ֹε�Ϲ�ȣ : " + userEtt.getSocid()); 
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
			
			paramMap.put("present_call_url", action_key);

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
	/***********************************************************************
	 * ����üũ ����
	 **********************************************************************/
	public double getSumMoney(String cardNo, String cardType, double sum,WaContext context, HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, BaseException{

		
		JtProcess process = new JtProcess();
		String joltServiceName = "BSNINPT";
		String toDay  = DateUtil.currdate("yyyyMMdd");
		
		String toDate  = DateUtil.dateAdd('M', -1, toDay, "yyyyMMdd");
		int datcount = DateUtil.getMonthlyDayCount(
						Integer.parseInt(toDate.substring(0,4)),
						Integer.parseInt(toDate.substring(4,6))); // �ش���� ����
		toDate = toDate.substring(0,6) + Integer.toString(datcount);
		debug("toDate : " + toDate);
		String fromDate =  DateUtil.dateAdd('M', -3, toDay, "yyyyMMdd");
		fromDate = fromDate.substring(0,6) + "01";		

		if(cardType.equals("1") || cardType.equals("3")){  //1:���� 3:��������
			// 2008-10-13 ����
			JoltInput entity = new JoltInput(joltServiceName);					
			entity.setServiceName(joltServiceName);
			entity.setString("fml_trcode", "MGA0100R1600");

			TaoResult jout = null;

			entity.setString("fml_arg1", cardNo);		// ��ȣ: ����:�ֹι�ȣ/���:ȸ����ȸ����ȣ/���:ī���ȣ				
			entity.setString("fml_arg2", "3");			// ����,�������: '1':�ֹι�ȣ(����),'2':ȸ����ȸ����ȣ(���)'3':ī���ȣ(���)
			entity.setString("fml_arg3", fromDate);		// �̿���ȸ��_FROM, YYYYMMDD(����Ѵ�)
			entity.setString("fml_arg4", toDate);		// �̿���ȸ��_TO, YYYYMMDD(����Ѵ�)
			entity.setString("fml_arg5", " ");			// ISP����(1.ISP, ������ : SPACE)
			entity.setString("fml_arg7", "1");			// ����ī���������

			jout = process.call(context, request, entity);

			String rescode = jout.getString("fml_ret1");
			
				if ("0".equals(rescode)) {
					sum = sum + jout.getDouble("fml_retd3") + jout.getDouble("fml_retd5");
					debug("sum_money### >> " + sum);
			} 
		}
		return sum;
	}
}
