/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemVipCardInsActn
*   �ۼ���    : �̵������ �ǿ���
*   ����      : ���� > VIPī�� ���ó��
*   �������  : golf 
*   �ۼ�����  : 2010-09-14
************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.02.15    �̰���	   ISP������� �߰�
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
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
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemVipCardInsActn extends GolfActn{
	
	public static final String TITLE = "���� > VIPī�� ���ó��";
	static final String JoltServiceName = "BSNINPT";
	static final String TSN025 = "MHL0260R0100";
	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		String userNm = ""; 
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intMemberGrade = 0; 
		int intCardGrade = 0; 
		String email_id = "";
		int addResult = 0;
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)	
		String ispCardNo   	= "";// ispī���ȣ
		String ip = request.getRemoteAddr();
		String memSocid = "";
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
				userNm		= (String)usrEntity.getName(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userMobile1 = (String)usrEntity.getMobile1();
				userMobile2 = (String)usrEntity.getMobile2();
				userMobile3 = (String)usrEntity.getMobile3();				
				memSocid = (String)usrEntity.getSocid();			//-  �ֹε�Ϲ�ȣ			
				
			}			
			String strResultCode	= "99";
						
			// 02.�Է°� ��ȸ		
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			RequestParser parser 	= context.getRequestParser("default",request,response); 
			
			String insMode 			= StrUtil.isNull(parser.getParameter("insMode"), "");
			//String accede 			= StrUtil.isNull(parser.getParameter("accede"), "N");
			String vipCardPayAmt	= "";
			String cardSel			= StrUtil.isNull(parser.getParameter("cardSel"), "");
			
			double sum_money = 0;
			//double select_sum_money = 0;
			//double temp_sum_money = 0;
			//String select_card_no = "";
			boolean payResult = false;			 
			String toDate  = DateUtil.currdate("yyyyMMdd");	
			String fromDate =  DateUtil.dateAdd('M', -3, toDate, "yyyyMMdd");
			fromDate = fromDate.substring(0,6) + "01";
						
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			
			GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// ������ ��ȣ(766559864) //topn : 745300778
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CSTMR_ID", userId);
			
			if("vipCard".equals(insMode))
			{
					
					//VIP �ش� ī��� ���� ���� ����								
					//List cardVipList 		= mbr.getCardVipInfoList();
					String vipCardNo		= cardSel;
					String vipCardExpDate	= "";
					//String vipCardExpDate	= StrUtil.isNull(mbr.getVipCardExpDate(), "");					
					String insTerm 			= "00";	//�Ͻú�
					System.out.print("## GolfMemVipCardInsActn VIPī�� : userId : "+userId+" | vipCardNo : "+vipCardNo+"\n");
					
					
					if("".equals(vipCardNo) )
					{
						request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
						request.setAttribute("resultMsg", "VIPī�尡 �����ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");
						debug("VIPī�尡 ����");
						strResultCode = "77";
					}
					else
					{
						// SBS��������� ȸ������ 5õ�� �������� ȸ���� 2���� ���� 
						// �ڷᰡ �ִٸ� 5õ�� ���� ���� => 2���� ����(����üũ OK �Ǵ��� 2���� ����)
						// �ڷᰡ ���ٸ� ���� ���� ó�� (1��5õ�� ����, ����üũ�� ���� ����)
						int resultCk = 0;

						dataSet.setString("socid", usrEntity.getSocid());	
						GolfMemCardInsDaoProc proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
						
						try{
							resultCk = proc.sbsMemberCk(context, dataSet, request);
						}catch(Throwable t){}
						
						String checkCardYn = "N";
						//������ ī�尡 VIPī������ �ٽ� �ѹ� Ȯ��
						List cardVipList = mbr.getCardVipInfoList();
						if( cardVipList!=null && cardVipList.size() > 0 )
						{
							for (int i = 0; i < cardVipList.size(); i++) 
							{								
								sum_money = 0;
								int usedMoney = 0;
								
								try { 
									
									CardVipInfoEtt record = (CardVipInfoEtt)cardVipList.get(i);
									String cardNo 		= StrUtil.isNull((String)record.getCardNo(), ""); 
									String grade 		= (String)record.getVipGrade();
									String expDate 		= (String)record.getExpDate();
									String cardType 	= (String)record.getCardType();
									String last_cardApp = (String)record.getCardAppType();
									String last_cardNo 	= (String)record.getLastCardNo();									
									String reg_date 	= StrUtil.isNull((String)record.getAppDate(), "");
									String cardJoinDate	= StrUtil.isNull((String)record.getCardJoinDate(), "");
									
									System.out.print("## GolfMemVipCardInsActn VIPī�� �� Ȯ�� : userId : "+userId+" | vipCardNo : "+vipCardNo+" | cardNo : "+cardNo+" | grade : "+grade+"\n");
									if(vipCardNo.equals(cardNo))
									{			
										debug("## VIPī�� ����");
										
										
										//�ش� ī���� �����ݾ� ���ϱ�
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
										
										
										// PTī�尡  03,12,30,91 ���ϰ�� 1��5õ�� ����
										if( "03".equals(grade) || "12".equals(grade) || "30".equals(grade) || "91".equals(grade)  )
										{
											vipCardPayAmt = "15000";
										
										}
										else
										{
											vipCardPayAmt = "20000";
										}
										
										if( "03".equals(grade) || "12".equals(grade) )
										{
											
											if("00".equals(expDate)){
												
												// 1:����,2:����,3:����,4:����
												if("1".equals(cardType) || "3".equals(cardType))
												{
													sum_money = getSumMoney(cardNo,cardType,sum_money,context,request,response);
													usedMoney = (int) sum_money;
													debug("## GolfMemVipCardInsActn | ���� ī���ȣ : "+cardNo+" | �����ݾ� : "+usedMoney);
													
													if(last_cardApp.equals("21") || last_cardApp.equals("24") || last_cardApp.equals("31") || last_cardApp.equals("33") || last_cardApp.equals("35") || last_cardApp.equals("37")){
														sum_money = getSumMoney(last_cardNo,cardType,sum_money,context,request,response);
														usedMoney = (int) sum_money;
														debug("## GolfMemVipCardInsActn | ���� ī���ȣ : "+last_cardNo+" | �����ݾ� : "+usedMoney);
													}
													
												}
												
												
											}
											
											//ī�尡 �߱޳�¥�� 3���� �̳��� ��� üũ
											String ckDate =  DateUtil.dateAdd('M', 3, cardJoinDate, "yyyyMMdd");											
											debug("## GolfMemVipCardInsActn | ī��߱޳�¥ �� | ���ó�¥ : "+toDate+" | ī��߱޳�¥ : "+cardJoinDate+ " | ī��3�����񱳳�¥ : "+ckDate);
											
											if( Integer.parseInt(toDate) < Integer.parseInt(ckDate) )
											{
												debug("## GolfMemVipCardInsActn | 3�����̳� �߱�ī���Դϴ�. last_cardApp : "+last_cardApp+" | ī��߱� 3���� �̳� ��");
												sum_money = 10000000; //�űԸ� ���� õ���� �־� �켱�ο�.....
												usedMoney = (int) sum_money;
											}
											/*
											
											if(!(Integer.parseInt(fromDate) <= Integer.parseInt(reg_date))){
			
											} else {
												//��, 30���� �̸��ΰ�� �ֱ� ���� ī��߱����ڷ� 3���� �̸� ���� 1�� 5õ�� ����. 3���� ��� ȸ���� 2���� ����
												//��, �ű� 3���� �̳��� �Ѽ���߱�, �н���߱� �� ī�带 ��߱� �纯��õ� ����üũ���� ����ó�� �Ѵ�.(1��5õ�� ���� ����)
												if (last_cardApp.equals("11") || last_cardApp.equals("12") || last_cardApp.equals("21") || last_cardApp.equals("22") || last_cardApp.equals("24") || last_cardApp.equals("25")   ) {
													debug(" last_cardApp : "+last_cardApp+" | �ű� 3���� �̳��� �Ѽ���߱�, �н���߱� ��");
													sum_money = 10000000; //�űԸ� ���� õ���� �־� �켱�ο�.....
													usedMoney = (int) sum_money;
												}
											}
																							
											*/
											if (usedMoney >= 300000) {
												
												vipCardPayAmt = "15000";
												
											}else if (usedMoney < 300000) {
												
												vipCardPayAmt = "20000";
												
											}
										
										}
																		
										
										//SBSȸ���ϰ�� ������ 2���� 
										if(resultCk>0)
										{
											vipCardPayAmt = "20000";
										}
										
										
																																							
									
										
									}
									
									
								System.out.print("## GolfMemVipCardInsActn ����VIPī��: userId : "+userId+" | cardNo : "+cardNo+" | vipCardPayAmt : "+vipCardPayAmt+"\n");
								
								} catch(Throwable t) {checkCardYn = "N";}
								
							
							}
						}
						if(!"".equals(vipCardPayAmt))
						{
							checkCardYn = "Y";
						}
						
						
						// ISP���� üũ
						String iniplug 		= parser.getParameter("KVPpluginData", "");					// ISP ������
						HashMap kvpMap 		= null;
						String pcg         	= "";														// ����/���� ����						
						String valdlim	   	= "";														// ���� ����
						String pid 			= null;														// ���ξ��̵�						
						String assName 		= "";														// ����̸�						
						
						String host_ip 		= java.net.InetAddress.getLocalHost().getHostAddress();
						
						if(iniplug !=null && !"".equals(iniplug)) {
							kvpMap = payProc.getKvpParameter( iniplug );
						}	
						
						if(kvpMap != null) {
							ispAccessYn = "Y";
							pcg         = (String)kvpMap.get("PersonCorpGubun");		// ����/���� ����
							ispCardNo   = (String)kvpMap.get("CardNo");					// ispī���ȣ
							assName		= (String)kvpMap.get("AssociationName");		// ����̸�
							valdlim		= (String)kvpMap.get("CardExpire");				// ���� ����
							if ( "2".equals(pcg) ) {
								pid = (String)kvpMap.get("BizId");								// ����ڹ�ȣ
							} else {
								pid = (String)kvpMap.get("Pid");									// ���� �ֹι�ȣ
							}
						} else {
							ispCardNo = parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
						}
						
						if ( valdlim.length() == 6 ) {
							valdlim = valdlim.substring(2);											
						}
						System.out.print("## GolfMemVipCardInsActn ISP�������� : userId : "+userId+" | ispCardNo : "+ispCardNo+"\n");
						
						debug("## VIPī�� �� | vipCardNo : "+vipCardNo+" = ispCardNo : "+ispCardNo);
						
						if(vipCardNo.equals(ispCardNo))
						{
							checkCardYn = "Y";
						}
						else
						{
							checkCardYn = "N";
						}
						
						 
						
						System.out.print("## GolfMemVipCardInsActn VIPī�� : userId : "+userId+" | �����ݾ� : "+vipCardPayAmt+" | checkCardYn : "+checkCardYn+" | vipCardNo : "+vipCardNo+"\n");
									
						
						if("Y".equals(checkCardYn))
						{
							//ī����ȿ�Ⱓ ��������
							try { 
								JoltInput input = null;
								JtProcess jt = new JtProcess();
								
								input = new JoltInput(JoltServiceName);
								input.setString("fml_trcode", TSN025); 
								input.setString("fml_arg1", "3");				//1:�ֹι�ȣ 2:����ڹ�ȣ 3: ī���ȣ
								input.setString("fml_arg2",vipCardNo);
								input.setString("fml_arg3", "");
								
								java.util.Properties prop = new java.util.Properties();
								prop.setProperty("RETURN_CODE", "fml_ret1");
								String ret1 = "";
								do {
									TaoResult result = jt.call(context, request, input, prop);
									vipCardExpDate = result.getString("fml_ret6").trim(); //��ȿ�Ⱓ
									
									
								} while ( "01".equals(ret1) );
							} catch(Throwable t) {}
							if(!"".equals(vipCardExpDate)) vipCardExpDate = vipCardExpDate.substring(2,6);
							
							System.out.print("## GolfMemVipCardInsActn VIPī�� : userId : "+userId+" | vipCardNo : "+vipCardNo+" | vipCardExpDate : "+vipCardExpDate+"\n");							
							
							//���� ���� �ֱ�
							payEtt.setMerMgmtNo(merMgmtNo);
							payEtt.setCardNo(vipCardNo);
							payEtt.setValid(vipCardExpDate);			
							payEtt.setAmount(vipCardPayAmt);
							payEtt.setInsTerm(insTerm);
							payEtt.setRemoteAddr(ip);
							
							debug("## ����ó�� ����");
							
							//����ó�� ����	
							try { 
								payResult = payProc.executePayAuth(context, request, payEtt);
							} catch(Throwable t) {}
							debug("## GolfMemVipCardInsActn ����ó�� ��� payResult : "+payResult);
							
							
							
							if (payResult) 
							{
								addResult = 1;
								
								// ���� ���� ���̺� INSERT 
								String sttl_mthd_clss = "";
								if (insTerm.equals("00")) sttl_mthd_clss="0001";
								else sttl_mthd_clss="0002";
								
								dataSet.setString("CDHD_ID", userId);
								dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
								dataSet.setString("STTL_GDS_CLSS", "0003");
								dataSet.setString("STTL_STAT_CLSS", "N");
								dataSet.setString("STTL_AMT", vipCardPayAmt);
								dataSet.setString("MER_NO", merMgmtNo);
								dataSet.setString("CARD_NO", vipCardNo);
								dataSet.setString("VALD_DATE", vipCardExpDate);
								dataSet.setString("INS_MCNT", insTerm.toString());
								dataSet.setString("AUTH_NO", payEtt.getUseNo());
								dataSet.setString("STTL_GDS_SEQ_NO", "");
								dataSet.setString("STTL_MINS_NM", "BCPTī��");
								
								if (addResult == 1) {
									// ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
									try {
										addResult = addResult + addPayProc.execute(context, dataSet);
									} catch(Throwable t) {}
								}
								debug("## GolfMemVipCardInsActn ���� ���� ���� ��� addResult : "+addResult);
								
								boolean payCancelResult = false;
								if (addResult == 2) //�������� ������
								{								
									
									int addMemResult = 0;
									GolfMemCardInsDaoProc mem_proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
									dataSet.setString("strCode", "0003");	// Gold������� �Է�	
									try {
										debug("## GolfMemVipCardInsActn | ȸ������ DB �Է� ó�� ����");
										addMemResult = mem_proc.vipCardMemIns(context, dataSet, request);	
										debug("## GolfMemVipCardInsActn | ȸ������ DB �Է� ó�� ��� addMemResult : "+addMemResult);
									} catch(Throwable t) {}
									
									if (addMemResult == 1) {
										try {
											DbTaoResult tmView = mem_proc.cardExecute(context, dataSet, request);
		
											if (tmView != null && tmView.isNext()) {
												tmView.first();
												tmView.next();
												memGrade = (String) tmView.getString("memGrade");	
												intMemGrade = (int) tmView.getInt("intMemGrade");	
												intMemberGrade = (int) tmView.getInt("intMemberGrade");	
												intCardGrade = (int) tmView.getInt("intCardGrade");	
												
											}
										} catch(Throwable t) {}
										usrEntity.setMemGrade(memGrade);				//��޸�
										usrEntity.setIntMemGrade(intMemGrade);		//������
										usrEntity.setIntMemberGrade(intMemberGrade);	//��������ó��
										usrEntity.setIntCardGrade(intCardGrade);		//ī����ó��
										usrEntity.setCyberMoney(0);
										
										
										debug("## email_id : "+email_id);
										
										try { 
											if (!email_id.equals("")) {
		
												String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
												String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
												String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
												String emailTitle = "";
												String emailFileNm = "";
												
												EmailSend sender = new EmailSend();
												EmailEntity emailEtt = new EmailEntity("EUC_KR");
												
												emailTitle = "[Golf Loun.G] ��������� ���� ������ ���ϵ帳�ϴ�.";
												emailFileNm = "/email_tpl19.html";
												emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+memGrade);
												
												emailEtt.setFrom(emailAdmin);
												emailEtt.setSubject(emailTitle);
												emailEtt.setTo(email_id);
												sender.send(emailEtt);  
											}
										} catch(Throwable t) {}
										
										// ���� ������
										try { 
											HashMap smsMap = new HashMap();
											smsMap.put("ip", request.getRemoteAddr());
											smsMap.put("sName", userNm);
											smsMap.put("sPhone1", userMobile1);
											smsMap.put("sPhone2", userMobile2);
											smsMap.put("sPhone3", userMobile3);
										        
											String smsClss = "674";
											String message = "��������� Goldȸ������ ���������� ���ԵǼ̽��ϴ�. �����մϴ�."; 
							 
											SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
											String smsRtn = smsProc.send(smsClss, smsMap, message);
										} catch(Throwable t) {}
										
										request.setAttribute("returnUrl", "GolfMemVipCardEnd.do");
										//request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp'");		
										request.setAttribute("resultMsg", vipCardPayAmt+"�� ������� �Ǿ����ϴ�.");	
										debug("�������� ���� ����");
										strResultCode = "11";
										
									}
									else
									{
										try { 
											payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
										} catch(Throwable t) {}
										request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
										request.setAttribute("resultMsg", "ȸ�������� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ���Ե��� ���� ��� �����ڿ� �����Ͻʽÿ�.");
										debug("ȸ������ DBó�� ����");
										strResultCode = "44";
										veriResCode = "3";
									}
								
								} else if (addResult == 9) { //�ѹ� �� üũ��
						        	// DB���� ���н� ������� ����	   
									try { 
										payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
									} catch(Throwable t) {}
									request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
									request.setAttribute("resultMsg", "�̹� ��û�ϼ̽��ϴ�.");
									debug("�������� ���� ����");
									strResultCode = "33";
									veriResCode = "3";
						        } else {
						        	// DB���� ���н� ������� ����
						        	try { 
						        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
						        	} catch(Throwable t) {}
									request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
									request.setAttribute("resultMsg", "VIPī������� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");
									debug("�������� ���� ����");
									strResultCode = "33";
									veriResCode = "3";
						        }	
								
								debug("## GolfMemVipCardInsActn ���� ���� ���� ��� payCancelResult : "+payCancelResult);
							
							}
							else
							{
								request.setAttribute("script", "");
								request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
								request.setAttribute("resultMsg", "BC VIP ī������� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");
								debug("���� ����");
								strResultCode = "22";
								veriResCode = "3";
								
								// �������н� ���� ���� 	
								try {
								int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);				
								debug("�������г���������  result_fail : " + result_fail);
								} catch(Throwable t) {}								
								
							}
						
						}
						else
						{
							request.setAttribute("script", "");
							request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
							request.setAttribute("resultMsg", "���� ������ �����Ͻô� ī�� ������  "+ assName + " " 
									+ ispCardNo.substring(0, 4)+"-"+ispCardNo.substring(4, 8)+"-****-"+ispCardNo.substring(12, 16)+"�Դϴ�.\\n\\n�ٽ� Ȯ���Ͽ� �ֽʽÿ�.");
							
							debug("���� ���� 33");
							strResultCode = "22";
							veriResCode = "3";
						}
						
						
					}
									
				
			}
			
			System.out.print("## GolfMemVipCardInsActn VIPī�� ���ó�� : userId : "+userId+" | insMode : "+insMode+" | strResultCode : "+strResultCode+" \n");
			
			// 05. Return �� ����				
			paramMap.put("editResult", String.valueOf(addResult));	
	        request.setAttribute("paramMap", ""); 
	        request.setAttribute("strResultCode", strResultCode);
	        
		} catch(Throwable t) {
			veriResCode = "3";
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
						
			if(ispAccessYn.equals("Y")){
				
				//ISP���� �α� ���
				HashMap hmap = new HashMap();
				hmap.put("ispAccessYn", ispAccessYn);
				hmap.put("veriResCode", veriResCode);
				hmap.put("title", TITLE);
				hmap.put("memName", userNm);
				hmap.put("memSocid", memSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", ip);
				hmap.put("className", "GolfMemVipCardInsActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}

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
