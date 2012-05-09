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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
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
public class GolfMemChkTmActn extends GolfActn { 
	
	public static final String TITLE = "TM ���Ͽ��� ����"; 

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
			
			// mem_id�� �ִ� ���
			if(!("".equals(strMemId) || strMemId == null)){
				
				UcusrinfoDaoProc proc = (UcusrinfoDaoProc)context.getProc("UcusrinfoDao");
				con = context.getDbConnection("default", null);
				
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
						debug("## GolfEvntCorporationActn | �����÷��� ���ؼ� ��������� ���� | ����ī��ȸ���� ��� - �˷� ó��");
						//������������ �̵�
						request.setAttribute("returnUrl", "");	
						//20100209 ������� ������û
						//request.setAttribute("resultMsg", "����ī�� �����ڴ� ����ȸ������ ��������� ����� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�.");	
						request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
						request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	 
					}
				}
				else{
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
	
	
}
