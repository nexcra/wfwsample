/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemChkCorporationActn
*   �ۼ���     : �̵������ ������
*   ����        : ȸ�� > ��ȸ�� ��ȯ > ��
*   �������  : golf 
*   �ۼ�����  : 2009-07-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMemChkCorporationActn extends GolfActn { 
	
	private static final String BSNINPT = "BSNINPT";					// �����ӿ� ��ȸ����
	public static final String TITLE = "�����÷��� ���ؼ� ��������� ���� - ȸ�����Խ� 20% ����"; 

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		Connection con = null;
		try {
			Map paramMap = BaseAction.getParamToMap(request);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			String str_msg_enc = parser.getParameter("message", "");
			String str_msg = "";
			String strType = "";	// ���۱��а� (Y:�α���, N:Ż��)
			String strMemId = "";
			String strMemAccount = "";
			String peCk = "";
			
			debug("## GolfMemChkCorporationActn | str_msg_enc:"+str_msg_enc+"\n");
			
			if(!("".equals(str_msg_enc) || str_msg_enc == null)){

//				StringEncrypter sender = new StringEncrypter("GOLF", "TEST");
//				str_msg_enc = sender.encrypt(str_msg_enc);
				
//				debug("## GolfMemChkCorporationActn | str_msg_enc_enc:"+str_msg_enc+"\n");

				/*�޴� ��*/
				StringEncrypter receiver = new StringEncrypter("BCCARD", "GOLF");
				str_msg = receiver.decrypt(str_msg_enc);
				debug("## GolfMemChkCorporationActn | str_msg:"+str_msg+"\n");

				// ���۱��а� ����
				strType = getSubString(str_msg,0,1).trim();
				
				// mem_id��(����) ����
				strMemId = getSubString(str_msg,58,8).trim();
				
				// account��(���̵�) ����  
				strMemAccount = getSubString(str_msg,66,40).trim();	

				debug("## GolfMemChkCorporationActn | strType:" + strType + " | strMemId:" + strMemId + " | strMemAccount:" + strMemAccount + "\n");
				
			}
			
			System.out.print("## GolfMemChkCorporationActn | strType : " + strType + " | strMemId : "+strMemId+" | strMemAccount : "+strMemAccount+"\n");						
			 
			UcusrinfoEntity ucusrinfo = null;			
			con = context.getDbConnection("default", null);
			
			// mem_id�� �ִ� ���
			if(!("".equals(strMemId) || strMemId == null)){
				
				UcusrinfoDaoProc proc = (UcusrinfoDaoProc)context.getProc("UcusrinfoDao");
				
				
				ucusrinfo= proc.selectByAccountCot( con, strMemId);	// ����ȸ������ �˻�	
				
				if(!("".equals(strMemAccount) || strMemAccount == null)){
					peCk = proc.selectPeByCkNum( con, strMemAccount);	// ����ȸ������ �˻�
				}
				
			}
			
			// �α��� 
			if (strType.equals("Y")){

				// ���� ȸ���� ���
				if (ucusrinfo != null) {	
					
					// ���� ȸ���� ��� ���� ����
					HttpSession session = request.getSession(true); 
					UcusrinfoEntity usrEntity = (UcusrinfoEntity)session.getAttribute("COEVNT_ENTITY"); // �⺻����  | COEVNT_ENTITY ���Ǹ��� �߰�����
					 			
					// �α����� �� �� �����ΰ�� usrEntity�� null���̹Ƿ� ��ü ��������ߵ�. 5566268
					if(usrEntity == null){	
						usrEntity = new UcusrinfoEntity(); 
						System.out.print("## GolfMemChkCorporationActn | usrEntity null --> ���� �۾� \n");
					}
					  
					usrEntity.setStrEnterCorporation("Y");
					usrEntity.setStrEnterCorporationMemId(strMemId);
					usrEntity.setStrEnterCorporationAccountId(strMemAccount);
					
					//���Ǳ���
					session.setAttribute("COEVNT_ENTITY", usrEntity);	
					session.setAttribute("FRONT_ENTITY", null);	
					
					debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | strEnterCorporation : " + usrEntity.getStrEnterCorporation());
					debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | strEnterCorporationMemId : " + usrEntity.getStrEnterCorporationMemId());
					
					// ����ī��(6)�� ���
					if("6".equals(ucusrinfo.getStrCoMemType())){					
						
						if("Y".equals(peCk)){
							//������������ �̵�
							debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | ���� ����ī���̰� ����ȸ���� ��� - �������� �̵�");
							request.setAttribute("returnUrl", "");	
							request.setAttribute("resultMsg", "");	
							request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
							request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
						}
						else{
							debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | ���� ����ī���ε� ����ȸ���� �ƴѰ�� - ������������ �̵�");
							request.setAttribute("returnUrl", "");	
							request.setAttribute("resultMsg", "");	
							request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
							request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.						
						}
										
					}
					else{
						debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | ����ī��ȸ���� ��� ž����ī�� �������� üũ");
						
						//ž����ī�� �����ϰ� �ִ��� üũ
						String memClss= ucusrinfo.getMemberClss();
						String strBizNo = "";
						//������ ��쿡�� ��������
						if("5".equals(memClss))
						{
							System.out.println("## GolfEvntCorporationActn | ����ȸ���̹Ƿ� ����ڹ�ȣ �������� | ID : "+ucusrinfo.getAccount()+" | memClss : "+memClss+"\n");
							strBizNo = selectBizNo(ucusrinfo.getAccount(), con);
							
							/** *****************************************************************
							 *Card������ �о����
							 ***************************************************************** */
							System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 ���� ȣ�� <<<<<<<<<<<<"+"\n");
							JoltInput cardInput_pt = new JoltInput(BSNINPT);
							cardInput_pt.setServiceName(BSNINPT);
							
							//����ī�� ��������
							if("6".equals(ucusrinfo.getStrCoMemType() ))
							{
								System.out.println("## GolfCtrlServ | checkJolt ����ī�� ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
								cardInput_pt.setString("fml_trcode", "MHL0230R0100");
								cardInput_pt.setString("fml_arg1", "3");				// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
								cardInput_pt.setString("fml_arg2", ucusrinfo.getSocid());	// �ֹι�ȣ						
								cardInput_pt.setString("fml_arg3", strBizNo);				// ����ڹ�ȣ
								cardInput_pt.setString("fml_arg4", "2");				// 1.���� 2.���
							}
							else
							{
								System.out.println("## GolfCtrlServ | checkJolt ����ī�� ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
								cardInput_pt.setString("fml_trcode", "MHL0230R0100");
								cardInput_pt.setString("fml_arg1", "2");				// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
								cardInput_pt.setString("fml_arg2", "");					// �ֹι�ȣ						
								cardInput_pt.setString("fml_arg3", strBizNo);				// ����ڹ�ȣ
								cardInput_pt.setString("fml_arg4", "2");				// 1.���� 2.���
							}
							
							JtProcess jt_pt = new JtProcess();
							java.util.Properties prop_pt = new java.util.Properties();
							prop_pt.setProperty("RETURN_CODE","fml_ret1");
							
							TaoResult cardinfo_pt = null;
							String resultCode_pt = "";	
							boolean existsData = false;
							String cardType = "";
							
							String joinNo = "";
							
							cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
							
							resultCode_pt = cardinfo_pt.getString("fml_ret1");
							System.out.println("## resultCode_pt ::  " + resultCode_pt+"\n");
							
							
							
							
							
							if("6".equals(ucusrinfo.getStrCoMemType() ))
							{
							
							
							
							
							
							
								if ( !"00".equals(resultCode_pt) && !"02".equals(resultCode_pt) ) {		// 00 ����, 02 ������ȸ ����
									System.out.println("## ���� ���� ���� ���� \n");
									
									//������������ �̵�
									request.setAttribute("returnUrl", "");	
									//20100209 ������� ������û
									//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
									request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
									request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
									
									
								}else{
	
									while( cardinfo_pt.isNext() ) {
										
										if(!existsData){																			
											existsData = true;
										}
										
										cardinfo_pt.next();
										
										System.out.println("## GolfMemChkCorporationActn | ����ī�� üũ ����  | ID : "+ucusrinfo.getAccount()+"\n");
										
										try{
										
										
											cardType 	= (String) cardinfo_pt.getString("fml_ret4");	//ī������ 1:����ī�� / 2:PTī�� / 3:�Ϲ�ī��								
											joinNo 		= (String) cardinfo_pt.getString("fml_ret8");	//��ǻ�ڵ�						
																	
											
		//									- ��ǰ�� :  ���� ���� �÷�Ƽ������ī�� / �����ڵ�
		//									 �� ���Ǿ����÷�Ƽ������_ĳ����     / 030478
		//									 �� ���Ǿ����÷�Ƽ������_�ƽþƳ�  / 030481
		//									 �� ���Ǿ����÷�Ƽ������_�����װ�  / 030494
		//									 �� �泲���� Familyī��  / 394033
		//								     * IBK APT �����̾�ī��-�Ϲ�(�����ڵ� : 740276) 
		//								     * IBK APT �����̾�ī��-��ī���н�(�����ڵ� : 740289) 
		//								     * IBK APT �����̾�ī��-�ƽþƳ�(�����ڵ� : 740292) 
											
											System.out.println("## GolfMemChkCorporationActn | ����ī�� üũ ����  | ID : "+ucusrinfo.getAccount()+" | cardType : "+cardType+"\n");
											
											if("1".equals(cardType)){
												
												// ž����ī�� ���� ���� üũ 
												if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
												//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //�׽�Ʈ�� �������� �������
												{
																								
													
													System.out.println("## GolfMemChkCorporationActn | ž����ī�� ���� | ID : "+ucusrinfo.getAccount()+"\n");
													
													if("Y".equals(peCk)){
														//������������ �̵�
														debug("## GolfEvntCorporationActn | ž����ī�� ���� | �����÷��� ���ؼ� ��������� ���� | ���� ����ī���̰� ����ȸ���� ��� - �������� �̵�");
														request.setAttribute("returnUrl", "");	
														request.setAttribute("resultMsg", "");	
														request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
														request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
														break;
													}
													else{
														debug("## GolfEvntCorporationActn | ž����ī�� ���� | �����÷��� ���ؼ� ��������� ���� | ���� ����ī���ε� ����ȸ���� �ƴѰ�� - ������������ �̵�");
														request.setAttribute("returnUrl", "");	
														request.setAttribute("resultMsg", "");	
														request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
														request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.						
													}
													
													
												}
												
																						
												
											}
											else
											{
												debug("## GolfMemChkCorporationActn | 88");	
												//������������ �̵�
												request.setAttribute("returnUrl", "");	
												//20100209 ������� ������û
												//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
												request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
												request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
											}
										
										} 
										catch(Throwable t) {
											debug("## GolfMemChkCorporationActn | 44");	
											//������������ �̵�
											request.setAttribute("returnUrl", "");	
											//20100209 ������� ������û
											//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
											request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
											request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
										}
										
										
									}
									
									if(!existsData){
									debug("## GolfMemChkCorporationActn | 55");	
									//������������ �̵�
									request.setAttribute("returnUrl", "");	
									//20100209 ������� ������û
									//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
									request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
									request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
									}
									
									
								}
							
							}
							else
							{
								
								if("00".equals(resultCode_pt))
								{
									
									while( cardinfo_pt.isNext() ) {
										
										if(!existsData){																			
											existsData = true;
										}
										
										cardinfo_pt.next();
										
										System.out.println("## GolfMemChkCorporationActn | ����ī�� üũ ����  | ID : "+ucusrinfo.getAccount()+"\n");
										
										try{
										
										
											cardType 	= (String) cardinfo_pt.getString("fml_ret4");	//ī������ 1:����ī�� / 2:PTī�� / 3:�Ϲ�ī��								
											joinNo 		= (String) cardinfo_pt.getString("fml_ret8");	//��ǻ�ڵ�						
																	
											
		//									- ��ǰ�� :  ���� ���� �÷�Ƽ������ī�� / �����ڵ�
		//									 �� ���Ǿ����÷�Ƽ������_ĳ����     / 030478
		//									 �� ���Ǿ����÷�Ƽ������_�ƽþƳ�  / 030481
		//									 �� ���Ǿ����÷�Ƽ������_�����װ�  / 030494
		//									 �� �泲���� Familyī��  / 394033
		//								     * IBK APT �����̾�ī��-�Ϲ�(�����ڵ� : 740276) 
		//								     * IBK APT �����̾�ī��-��ī���н�(�����ڵ� : 740289) 
		//								     * IBK APT �����̾�ī��-�ƽþƳ�(�����ڵ� : 740292) 
											
											System.out.println("## GolfMemChkCorporationActn | ����ī�� üũ ����  | ID : "+ucusrinfo.getAccount()+" | cardType : "+cardType+"\n");
											
											if("1".equals(cardType)){
												
												// ž����ī�� ���� ���� üũ 
												if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
												//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //�׽�Ʈ�� �������� �������
												{
																								
													
													System.out.println("## GolfMemChkCorporationActn | ž����ī�� ���� | ID : "+ucusrinfo.getAccount()+"\n");
													
													if("Y".equals(peCk)){
														//������������ �̵�
														debug("## GolfEvntCorporationActn | ž����ī�� ���� | �����÷��� ���ؼ� ��������� ���� | ���� ����ī���̰� ����ȸ���� ��� - �������� �̵�");
														request.setAttribute("returnUrl", "");	
														request.setAttribute("resultMsg", "");	
														request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
														request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
														break;
													}
													else{
														debug("## GolfEvntCorporationActn | ž����ī�� ���� | �����÷��� ���ؼ� ��������� ���� | ���� ����ī���ε� ����ȸ���� �ƴѰ�� - ������������ �̵�");
														request.setAttribute("returnUrl", "");	
														request.setAttribute("resultMsg", "");	
														request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
														request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.						
													}
													
													
												}
												
																						
												
											}
											else
											{
												debug("## GolfMemChkCorporationActn | 88");	
												//������������ �̵�
												request.setAttribute("returnUrl", "");	
												//20100209 ������� ������û
												//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
												request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
												request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
											}
										
										} 
										catch(Throwable t) {
											debug("## GolfMemChkCorporationActn | 44");	
											//������������ �̵�
											request.setAttribute("returnUrl", "");	
											//20100209 ������� ������û
											//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
											request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
											request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
										}
										
										
									}
									if(!existsData){
										debug("## GolfMemChkCorporationActn | 55");	
										//������������ �̵�
										request.setAttribute("returnUrl", "");	
										//20100209 ������� ������û
										//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
										request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
										request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
									}
								}
								else if("01".equals(resultCode_pt))
								{
									
									// ���ϰ��� 01�ϰ�� �ѹ� �� ������ ������.
									
									/** *****************************************************************
									 *Card������ �о����
									 ***************************************************************** */
									System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 ���� ȣ�� <<<<<<<<<<<<"+"\n");
									JoltInput cardInput_pt2 = new JoltInput(BSNINPT);
									cardInput_pt2.setServiceName(BSNINPT);
									
									System.out.println("## GolfCtrlServ | checkJolt ���ϰ��� 01�ϰ�� �ѹ� �� ������ ������. ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
									cardInput_pt2.setString("fml_trcode", "MHL0230R0100");
									cardInput_pt2.setString("fml_arg1", "3");				// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
									cardInput_pt2.setString("fml_arg2", ucusrinfo.getSocid());	// �ֹι�ȣ						
									cardInput_pt2.setString("fml_arg3", strBizNo);				// ����ڹ�ȣ
									cardInput_pt2.setString("fml_arg4", "2");				// 1.���� 2.���
									
									cardinfo_pt = jt_pt.call(context, request, cardInput_pt2, prop_pt);			
									
									resultCode_pt = cardinfo_pt.getString("fml_ret1");
									debug("## resultCode_pt ::  " + resultCode_pt);
									
									if("00".equals(resultCode_pt))
									{
										
										while( cardinfo_pt.isNext() ) {
											
											if(!existsData){																			
												existsData = true;
											}
											
											cardinfo_pt.next();
											
											System.out.println("## GolfMemChkCorporationActn | ����ī�� üũ ����  | ID : "+ucusrinfo.getAccount()+"\n");
											
											try{
											
											
												cardType 	= (String) cardinfo_pt.getString("fml_ret4");	//ī������ 1:����ī�� / 2:PTī�� / 3:�Ϲ�ī��								
												joinNo 		= (String) cardinfo_pt.getString("fml_ret8");	//��ǻ�ڵ�						
																		
												
			//									- ��ǰ�� :  ���� ���� �÷�Ƽ������ī�� / �����ڵ�
			//									 �� ���Ǿ����÷�Ƽ������_ĳ����     / 030478
			//									 �� ���Ǿ����÷�Ƽ������_�ƽþƳ�  / 030481
			//									 �� ���Ǿ����÷�Ƽ������_�����װ�  / 030494
			//									 �� �泲���� Familyī��  / 394033
			//								     * IBK APT �����̾�ī��-�Ϲ�(�����ڵ� : 740276) 
			//								     * IBK APT �����̾�ī��-��ī���н�(�����ڵ� : 740289) 
			//								     * IBK APT �����̾�ī��-�ƽþƳ�(�����ڵ� : 740292) 
												
												System.out.println("## GolfMemChkCorporationActn | ����ī�� üũ ����  | ID : "+ucusrinfo.getAccount()+" | cardType : "+cardType+"\n");
												
												if("1".equals(cardType)){
													
													// ž����ī�� ���� ���� üũ 
													if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
													//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //�׽�Ʈ�� �������� �������
													{
																									
														
														System.out.println("## GolfMemChkCorporationActn | ž����ī�� ���� | ID : "+ucusrinfo.getAccount()+"\n");
														
														if("Y".equals(peCk)){
															//������������ �̵�
															debug("## GolfEvntCorporationActn | ž����ī�� ���� | �����÷��� ���ؼ� ��������� ���� | ���� ����ī���̰� ����ȸ���� ��� - �������� �̵�");
															request.setAttribute("returnUrl", "");	
															request.setAttribute("resultMsg", "");	
															request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
															request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
															break;
														}
														else{
															debug("## GolfEvntCorporationActn | ž����ī�� ���� | �����÷��� ���ؼ� ��������� ���� | ���� ����ī���ε� ����ȸ���� �ƴѰ�� - ������������ �̵�");
															request.setAttribute("returnUrl", "");	
															request.setAttribute("resultMsg", "");	
															request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
															request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.						
														}
														
														
													}
													
																							
													
												}
												else
												{
													debug("## GolfMemChkCorporationActn | 88");	
													//������������ �̵�
													request.setAttribute("returnUrl", "");	
													//20100209 ������� ������û
													//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
													request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
													request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
												}
											
											} 
											catch(Throwable t) {
												debug("## GolfMemChkCorporationActn | 44");	
												//������������ �̵�
												request.setAttribute("returnUrl", "");	
												//20100209 ������� ������û
												//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
												request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
												request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
											}
											
											
										}
										if(!existsData){
											debug("## GolfMemChkCorporationActn | 55");	
											//������������ �̵�
											request.setAttribute("returnUrl", "");	
											//20100209 ������� ������û
											//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
											request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
											request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
										}
										
										
										
										
										
									}
									else
									{
										debug("## GolfMemChkCorporationActn | 55");	
										//������������ �̵�
										request.setAttribute("returnUrl", "");	
										//20100209 ������� ������û
										//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
										request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
										request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
									}
									
									
									
									
								}
								else
								{
									debug("## GolfMemChkCorporationActn | 55");	
									//������������ �̵�
									request.setAttribute("returnUrl", "");	
									//20100209 ������� ������û
									//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
									request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
									request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
									debug("## ���� ���� ���� ����");						
								}
									
								
								
								
								
								
								
								
								
								
								
								
								
								
								
								
								
								
							}
							
						}
						else
						{
							debug("## GolfMemChkCorporationActn | 66");	
							//������������ �̵�
							request.setAttribute("returnUrl", "");	
							//20100209 ������� ������û
							//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
							request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
							request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
						}
						
						
						
						
						
						
					}
					
					
					
				}
				else{
					debug("## GolfMemChkCorporationActn | 77");	
					//������������ �̵�
					debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | ��Ÿ");
					request.setAttribute("returnUrl", "");	
					request.setAttribute("resultMsg", "");	
					request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
					request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
				}
  
			}
			// Ż�� - ����ȸ���� ��츸 ����  
			else if (strType.equals("N") && peCk.equals("Y")){
				
				debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | ����ȸ�� Ż�� ó���� �̵� | strMemAccount : " + strMemAccount);
				
				// ȸ�����̵� ��ȣȭ
				StringEncrypter sender = new StringEncrypter("BCCARD", "GOLF");
				String strMemAccountEnc = sender.encrypt(strMemAccount);
				
				paramMap.put("memAccount", strMemAccountEnc);	
				request.setAttribute("returnUrl", "/app/golfloung/GolfMemDelOut.do");	
				request.setAttribute("resultMsg", "");	
				request.setAttribute("script", "");	
				request.setAttribute("paramMap", paramMap); 
				 
			}
						  
			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
	

	public static String getSubString(String str, int startIndex, int length) { 
		
		byte[] b1 = null; 
		byte[] b2 = null; 
	
		try { 
			if (str == null) { 
				return ""; 
			} 
		
			b1 = str.getBytes(); 
			b2 = new byte[length]; 
		
			if (length > (b1.length - startIndex)) { 
				length = b1.length - startIndex; 
			} 
		
			System.arraycopy(b1, startIndex, b2, 0, length); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	
		return new String(b2); 
		
	}
	/**
	 * �÷��� �������� ���θ� üũ | 2009.10.29 | �ǿ���  
	 * 
	 * @param con ����
	 * @param account ����
	 * @return ����� ����
	 * @throws BaseException ���ܰ� �߻��ϴ� ���
	 */
	public String selectBizNo(String account, Connection con) throws BaseException {
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			//Connection con = null;
			
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	D.MEM_CLSS, B.BUZ_NO");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			sb.append(" WHERE");
			sb.append(" 	A.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE IS NULL ");

			String query = sb.toString();

			String coChk = "N"; 
			try {
								
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {
					coChk = rs.getString("BUZ_NO");
				}
				
			}catch (Throwable t) {
			} finally {
				/*try {
					if (con != null)
						con.close();
				} catch (Throwable ignored) {
				}*/
			}

			return coChk;
		}
	
}
