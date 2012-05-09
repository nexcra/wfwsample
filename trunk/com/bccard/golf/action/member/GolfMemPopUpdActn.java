/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemPopUpdActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : �̺�Ʈ-> ȸ������ ���� �˾� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-10-15
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*2011.11.08  ���ּ� �ϸ鼭 ������� �ʴ� ������� ó���� ���� ���� ���� �� �׽�Ʈ ��
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMemPopUpdActn extends AbstractAction {

	public static final String Title = "�̺�Ʈ->ȸ������ ���� �˾� ó��";
	static final String JoltXAServiceName = "BSXINPT";
    static final String JoltServiceName = "BSNINPT";
    //static final String TSN025 = "MPX0250R0100";
    static final String TSN025 = "MHL0260R0100";
    static final String TSN300 = "MHB3000I0103"; 
    private static final String BSNINPT = "BSNINPT";					// �����ӿ� ��ȸ����

	/***********************************************************************
	 * �׼�ó��.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return ��������
	 **********************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		TaoConnection	con			= null;
		RequestParser	parser		= context.getRequestParser("default", request, response);
		String subpage_key = "default";
		
		ResultException re;
		String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // ��ư
		String goPage = "/app/golfloung/memberActn.do"; // �̵� �׼�
		
		HttpSession session = request.getSession( false );
		
		try {
			
			Map paramMap 			= parser.getParameterMap();																				
			
			UcusrinfoEntity bcuser = SessionUtil.getFrontUserInfo(request);

			String cardUser = bcuser.getMemberClss();
			String cusNo = "";
			String bnk = "";
			String resultCode_pt = "";

			/** *****************************************************************
			 * 1. �α��� ������ ī������ ����
			 ***************************************************************** */
			//System.out.println("## MemUpdActlog | Jolt MHL0230R0100 ���� ȣ�� ");
			//JoltInput cardInput_pt = new JoltInput(BSNINPT);
			//cardInput_pt.setServiceName(BSNINPT);
			//cardInput_pt.setString("fml_trcode", "MHL0230R0100");
			//cardInput_pt.setString("fml_arg1", "1");	// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
			//cardInput_pt.setString("fml_arg2", bcuser.getSocid());	// �ֹι�ȣ				
			//cardInput_pt.setString("fml_arg3", " ");	// ����ڹ�ȣ
			//cardInput_pt.setString("fml_arg4", "1");	// 1.���� 2.���
			System.out.println("## MemUpdActlog | Jolt MHL0160R0100 ���� ȣ�� ");
			JoltInput cardInput_pt = new JoltInput(BSNINPT);
			cardInput_pt.setServiceName(BSNINPT);
			cardInput_pt.setString("fml_trcode", "MHL0160R0100");
			cardInput_pt.setString("fml_arg1", bcuser.getSocid());
			
			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult cardinfo_pt = null;
			
			try
			{
				cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
				
				resultCode_pt = cardinfo_pt.getString("fml_ret1");
				
				if ( "01".equals(resultCode_pt))
				{
					while( cardinfo_pt.isNext() ) 
					{
						cusNo 		= cardinfo_pt.getString("fml_ret5");	//�����ڵ�	
						break;
					}					
					
				}
								
				
			}catch(Throwable t) {
				t.printStackTrace();
				request.setAttribute("result", "01");
				System.out.print(" ## MemUpdActlog | ������� ���� : ī�尡 ���� | ID : "+bcuser.getAccount());
				
			}
			
						
			// �α��� ���� Ȯ��
			if(bcuser != null)
			{
				//�α��ν� ȸ������ �������� ����
				IndModifyOnlineProc proc  = (IndModifyOnlineProc)context.getProc("IndModifyOnlineProc");
				
				TaoResult output = null;
				JoltInput input = null;
				JtProcess jt = new JtProcess();
								
				String retCode = "";
				
				System.out.print(" ## MemUpdActlog | ���� ID : "+bcuser.getAccount()+" | getBankListCodes : "+bnk+"\n" );
				
				try {
					if(!"5".equals(cardUser) && "01".equals(resultCode_pt)) {	
						/** *****************************************************************
						 * 2. �α��� ������ ȸ������ �������� ����
						 ***************************************************************** */
						input = new JoltInput(JoltServiceName);
						input.setString("fml_trcode", TSN025); 
						input.setString("fml_arg1", "1");
						input.setString("fml_arg2", bcuser.getSocid());
						input.setString("fml_arg3", cusNo);
	
						java.util.Properties prop = new java.util.Properties();
						prop.setProperty("RETURN_CODE", "fml_ret1");
	
						String cardNo = "";
						String ret1 = "";
						System.out.print(" ## MemUpdActlog | 2�ܰ� ���ͳ�ī��������ȸ  ������ȸ��� ID : "+bcuser.getAccount()+" |getBankListCodes : "+bnk+ " | retCode : "+retCode+" \n");
						do {
							TaoResult result = jt.call(context, request, input, prop);
							ret1 = result.getString("fml_ret1");
							if ( "00".equals(ret1) ) {         // ���� (�������ڷ�)
								// ���Ͼ���...
							} else if ( "01".equals(ret1) ) {  // �����ڷ�����
								input.setString("fml_arg4", result.getString("fml_ret2") );
							} else {   // 02 �ֹι�ȣ Not found, 99 �ý��� ���
								subpage_key = "error";
								re = new ResultException();
								re.setTitleImage("error");
								re.setTitleText("ȸ����������");
								re.setKey("UPX025_R01_" + retCode);
								re.addButton(goPage, addButton);
								throw re;
							}

							// ������ �ڷ�(00)�� ��쿡 �Ѱǵ� ���� �� �ֱ� ������ fml_ret3 �� �����ϴ��� Ȯ���Ѵ�.
							if ( ("00".equals(ret1)||"01".equals(ret1)) && result.containsKey("fml_ret3") ) {
								boolean isFinal     = false;  // ����ī�忩��
								boolean isCard     = false;  // ����ī�忩��

								while( result.isNext() ) {
									result.next();

									isFinal     = "1".equals( result.getString("fml_ret11") );     // 1:����ī��,2:����ī��ƴ�,3:��Ϲ̿Ϸ�(�ٷ�ī��)
									isCard		= "1".equals( result.getString("fml_ret10") );     // 1:���� 2:���� 3:���� 4:����
									
									debug("isFinal:" + isFinal);
									debug("isCard:" + isFinal);
									
									debug("frm_ret12:" + result.getString("fml_ret12") );
									debug("frm_ret16:" + result.getString("fml_ret16") );

									if ( isFinal && isCard) {
										cardNo 		= result.getString("fml_ret3");	//�����ڵ�
										debug("=============== cardNo : " + cardNo);
										break;
									}
								}
							}
						} while ( "01".equals(ret1) );  // �����ڷ� �־ ����..
						
						if("".equals(cardNo)) {
							subpage_key = "error";
							re = new ResultException();
							re.setTitleImage("error");
							re.setTitleText("ȸ����������");
							re.setKey("UPX025_R01_" + retCode);
							re.addButton(goPage, addButton);
							throw re;
						}
						
						/** *****************************************************************
						 * 3. �α��� ������ BIN���� �������� ����
						 ***************************************************************** */
						TaoResult binResult = null;
						JoltInput entity_bin = new JoltInput();
						entity_bin.setServiceName("BSNINPT");
						entity_bin.setString("fml_trcode", "MHA0010R0700"); //�� ��ȣ��ȸ
						entity_bin.setString("fml_arg1", cardNo.substring(0,6) );

						java.util.Properties prop_bin = new java.util.Properties();
						prop_bin.setProperty("RETURN_CODE","fml_ret1");

						JtProcess jtproc_bin = new JtProcess();
						binResult = jtproc_bin.call(context, request, entity_bin, prop_bin);
						debug(binResult.toString());
						String bin_ret_code = binResult.getString("fml_ret1").trim(); 
						String bankcode = "00"; 
						if ("00".equals(bin_ret_code))
						{
							bankcode = binResult.getString("fml_ret2").trim();		// ȸ�����ȣ
						} else {	// 01.�ش� BIN��ȣ ������, 99.�ý������
							subpage_key = "error";
							re = new ResultException();
							re.setTitleImage("error");
							re.setTitleText("ȸ����������");
							re.setKey("UPX025_R01_" + retCode);
							re.addButton(goPage, addButton);
							throw re;
						}

						// ���� �������� ���� �����ڵ庯��
						if ("20".equals(bankcode)) {
							bankcode = "24";
						} else if("13".equals(bankcode)) {
							bankcode = "11";
						} else if("14".equals(bankcode)) {
							bankcode = "12";
						}
						
						/** *****************************************************************
						 * 3. ȸ������ ���� UPDATE ����
						 ***************************************************************** */
						TaoResult output1 = null;
	
						input = new JoltInput(JoltXAServiceName);
	
						//String cardNo = output.getString("fml_ret5");
						String name = bcuser.getName();
						
						debug("bankcode:"+bankcode);
						String bankAlias = "";
						//String bankAlias = context.getCode("BANKCODE",bankcode).getAlias();
						
						String detailAddr = parser.getParameter("detailaddr", "");
						
						
						// WafService (������� ���� �Է� : �ѱ۹��� )
						if ( request instanceof com.bccard.waf.action.ServiceRequest ) {
	
							detailAddr = new String(detailAddr.getBytes("ISO-8859-1"), "UTF-8");
							
						}
						 
						// ������ �κ� ����
						input.setString("fml_trcode", TSN300 );
						
						input.setString("fml_arg96", "4");						// ���� , �������� : 1.����,2.MT,3.FTP,4.BCNS/���ͳ� ��
						input.setString("fml_arg102", "BCC");					// startchar ���� "BCC"
						input.setString("fml_arg103", "6020");					// ���� ������ȣ
						input.setString("fml_arg104", "000000000000");			// �ŷ� ������ȣ - ����
						input.setString("fml_arg107", bankcode);				// ȸ�����ȣ
						input.setString("fml_arg108", "02");					// �߱ޱ��� - ���� 02
						input.setString("fml_arg110", "00000000000");			// ��޴ܸ���ȣ - ���� 00000000000
						input.setString("fml_arg111", "1");						// ó������ - ���� 1
						input.setString("fml_arg113", "1");						// ��ûī�屸�� - ���� :1 , 2:����, 3:����, 4:���� 
						input.setString("fml_arg114", "41");					// ��û�߱ޱ��� - ���� 41
						input.setString("fml_arg121", "1001000");				// �μ���ȣ ???
						input.setString("fml_arg122", "19941245");				// ������ȣ ???
						input.setString("fml_arg117", "6");						// ������� - 6 (PC���)
						input.setString("fml_arg118", "1");						// ������ü - 1 ����
						input.setString("fml_arg119", name);					// �Ű��� - �Ű��μ���
						input.setString("fml_arg120", "1");						// �Ű��ΰ��� - 1 ����
						input.setString("fml_arg40", "000000");   				// ���޾�ü�ڵ�
						input.setString("fml_arg1", cardNo) ;					// �����ڵ�� ��������ȣ(��������ŷ:501240)
						input.setString("fml_arg13", parser.getParameter("tel_ddd_no", "")) ;		// ������ȭ��ȣ
						input.setString("fml_arg14", parser.getParameter("tel_tel_hno", "")) ;		// ������ȭ��ȣ1
						input.setString("fml_arg15", parser.getParameter("tel_tel_sno", "")) ;		// ������ȭ��ȣ2
						input.setString("fml_arg16", parser.getParameter("zipcode1", "") + parser.getParameter("zipcode2", ""));	// ���ÿ����ȣ
						input.setString("fml_arg17", this.cpReplace(detailAddr)) ;				// ���� �������ּ�
						
						String mob0 = parser.getParameter("hp_ddd_no", "");
						String mob1 = parser.getParameter("hp_tel_hno", "");
						String mob2 = parser.getParameter("hp_tel_sno", "");
						
/*		���� �Ϸ� �� �׽�Ʈ ���				
						String addrClss = parser.getParameter("addr_clss", ""); // �ּұ����ڵ� => 1:���ּ�, 2:���ּ�, 3:�̽���籸�ּ�  (3���� ���ͳݰ� ����)
						
						if ( addrClss.equals("2") ) {
							hmap4.put("paraQ1252", this.cpReplace(zipaddr)) ;  // ���ּ��� paraQ1252(home_addr)��  �⺻�ּ�
							hmap4.put("paraQ1311", this.cpReplace(detailAddr)) ; // ���ּ��� paraQ1252(home_nw_addr_2)��  ���ּ�
							hmap4.put("paraQ1310", addrClss) ; // �����ּұ����ڵ� 
							hmap4.put("paraQ1312", parser.getParameter("roadCode", "")) ;	//���� ���θ� �ڵ�							
						}else {
							hmap4.put("paraQ1252", this.cpReplace(detailAddr)) ; // ���ּ�  paraQ1252(home_addr)��   ���ּ� ��
							//hmap4.put("paraQ1310", addrClss) ; // �����ּұ����ڵ� 
						}							

*/						
					
	
						if ( "".equals(mob1)) {
							mob0 = "";
							mob1 = "";
							mob2 = "";
						} 
						System.out.print(" ## MemUpdActlog | ID : "+bcuser.getAccount()+" | mob0 : "+mob0+" | mob1 : "+mob1+" | mob2 : "+mob2+"\n");					
						
						input.setString("fml_arg65",mob0) ;			// ���ݱ���
						input.setString("fml_arg66",mob1) ;			// ���ݹ�ȣ1
						input.setString("fml_arg67",mob2) ;			// ���ݹ�ȣ2
						 
						//debug("input : " + input.toString());
						System.out.print(" ## MemUpdActlog | ID : "+bcuser.getAccount()+" | input : "+input.toString()+"\n");
						
						java.util.Properties prop2 = new java.util.Properties();
						prop2.setProperty("RETURN_CODE", "fml_ret1");
	
						output1 = jt.call(context, request, input, prop2);
			            retCode = output1.getString("fml_ret1");
			            
			            System.out.print(" ## MemUpdActlog | 2�ܰ� ��������ó����� ID : "+bcuser.getAccount()+" | bankcode : "+bankcode+ " | retCode : "+retCode+" \n");
						
						if( !"1".equals(retCode) ){
							re = new ResultException();
							re.setTitleImage("error");
							re.setTitleText("ȸ����������");
							re.setKey("UHB003_Pb_Svc_" + retCode);
							re.addButton(goPage, addButton);
							throw re;
						}
					}
					/** *****************************************************************
					 * 4. ȸ������ ���� WEB DB UPDATE ����
					 ***************************************************************** */
					if (proc.getMemberInfoUpdatePop(context, parser, session, request)) {
						String msg = "## MemUpdActlog | WEBDB Update ���� " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
						request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
						System.out.println(msg);
						
						/** *****************************************************************
						 * 5. ����ȸ������ ���� WEB DB UPDATE
						 ***************************************************************** */
						if("5".equals(cardUser)) {
							if (proc.getMemberInfoUpdate5(context, parser, session, request)) {
								msg = "## MemUpdActlog | ���� WEBDB Update ���� " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
								request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
								System.out.println(msg);
								
								request.setAttribute("resultChk", "00");
							} else {
								msg = "## MemUpdActlog | ���� WEBDB Update ���� | " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
								request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
								System.out.println(msg);
								
								request.setAttribute("resultChk", "01");
							}
						} else {
							request.setAttribute("resultChk", "00");
						}
					} else {
						String msg = "## MemUpdActlog | WEBDB Update ���� | " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
						request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
						System.out.println(msg);
						
						request.setAttribute("resultChk", "01");
					}

					request.setAttribute("result", "00");
					System.out.print(" ## MemUpdActlog | ���������� ��������Ϸ� | ID : "+bcuser.getAccount()+" \n");
				
				} catch(Throwable t) {
					t.printStackTrace();
					request.setAttribute("result", "01");
					System.out.print(" ## MemUpdActlog | ���� ���� | ID : "+bcuser.getAccount()+" \n");
				}
			}
			else
			{
				//��α��ν�
				request.setAttribute("result", "01");
				System.out.print(" ## MemUpdActlog | ��α��� ���� "+" \n");
			}
									
			
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));   
			
			request.setAttribute("paramMap", paramMap);
			

			
		} catch (Throwable be) {			
			throw new GolfException(Title, be);
		} finally {
			try { if(con != null) { con.close(); } else {;} } catch(Throwable ignore) {}
		}
		return super.getActionResponse(context);
	}
	
	 /**
     * XSS �� ��ȯ
     * @param sContent
     * @return String
     */
	public String cpReplace(String sContent) {
		sContent = StrUtil.replace(sContent, "<", "&lt;");
		sContent = StrUtil.replace(sContent, ">", "&gt;");
		sContent = StrUtil.replace(sContent, "\"", "&#034;");
		sContent = StrUtil.replace(sContent, "\'", "&#039;");
		sContent = StrUtil.replace(sContent, "\n", "<br>");
		return sContent;
	}
}