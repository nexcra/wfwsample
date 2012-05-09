/**************************************************************************
*	Ŭ������	: UpdateIndActn
*	�ۼ���		: ������
*	����		: ���ͳ�ȸ���������� ó���κ�
*	�������	: bccard��ü
*	�ۼ�����	: 2004.02.3
************************** �����̷� ******************************************
* ����������	���뿹����	�����Ϸ���	����Ϸ���	�ۼ���	�������
* 2004.12.03										�ӰǱ�	SMS ���� ���� Ȯ�� ���� ����
* 2006.08.29												���̻��ī�� �������� ���Ű� ���� ȸ���� ����
*															ȸ�����ȣ 13 -> 11, 14 -> 12
* 2006.09.26										hklee	�¶��� ������ȯ
*															(UHB003_Pb_Svc  => BSXINPT(MHB3000I0103)
* 2008.11.24	2008.11.25	2008.11.24				hklee	����� ���� �ѱ۹���
* 2008.12.11	2008.12.11	2008.12.11	2008.12.11	���뱹	������� ȸ���� ��� �˾� �ȶ�쵵�� ��ȸ
* 2009.06.11                    2009.06.11                      �ȱ���  UPX025_R01 => MPX0250R010
* 2009.10.14										������	����ȸ���������� ���� ����
**************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.jolt.JtTransactionProc;
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
import com.bccard.waf.tao.jolt.JoltOutput;

/**
 * UpdateInd Action
 * @version   2004.02.03
 * @author    <A href="mailto:kjhyun@e4net.net">hyun kwang joon</A>
 **/
public class UpdateIndActn extends AbstractAction {

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
			String todayDate  = DateUtil.currdate("yyyyMM");
			SimpleDateFormat format = new SimpleDateFormat( "yyyyMM" );
			
			// �α��� ���� Ȯ��
			if(bcuser != null)
			{
				if(!"5".equals(cardUser)) {
					/** *****************************************************************
					 * 1. �α��� ������ ����
					 ***************************************************************** */
					System.out.println("## MemUpdActlog | Jolt MHL0160R0100 ���� ȣ�� ");
					JoltInput cardInput_pt = new JoltInput(BSNINPT);
					cardInput_pt.setServiceName(BSNINPT);
					cardInput_pt.setString("fml_trcode", "MHL0160R0100");
					cardInput_pt.setString("fml_arg1", bcuser.getSocid());
					//cardInput_pt.setString("fml_trcode", "MHL0230R0100");
					//cardInput_pt.setString("fml_arg1", "1");	// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
					//cardInput_pt.setString("fml_arg2", bcuser.getSocid());	// �ֹι�ȣ				
					//cardInput_pt.setString("fml_arg3", " ");	// ����ڹ�ȣ
					//cardInput_pt.setString("fml_arg4", "1");	// 1.���� 2.���
		
					JtProcess jt_pt = new JtProcess();
					java.util.Properties prop_pt = new java.util.Properties();
					prop_pt.setProperty("RETURN_CODE","fml_ret1");
					
					TaoResult cardinfo_pt = null;

					try
					{
						cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
						
						resultCode_pt = cardinfo_pt.getString("fml_ret1");	// 01 : ����, 02:�ش��ֹι�ȣ����, 03:�ý��ۿ���, 04:����ȸ�� ȸ�������� ����.
						
						if ( "01".equals(resultCode_pt))
						{
							while( cardinfo_pt.isNext() ) 
							{
								cusNo 		= cardinfo_pt.getString("fml_ret5");	//ȸ�����ȣ
								debug("==============cusNo : " + cusNo);
								break;
							}					
						}
						
					}catch(Throwable t) {
						//t.printStackTrace();
						request.setAttribute("result", "01");
						System.out.print(" ## MemUpdActlog | ������� ���� : ī�尡 ���� | ID : "+bcuser.getAccount());
						
					}
				}

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
						 * 2. �α��� ������ ���ͳ�ī��������ȸ ����
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
						String cardValdDate = "";
						
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
									
									//ī����ȿ�Ⱓ �߰�
									cardValdDate = result.getString("fml_ret6");
									
									debug("frm_ret12:" + result.getString("fml_ret12") );
									debug("frm_ret16:" + result.getString("fml_ret16") );
									debug("fml_ret6:" + cardValdDate );
									
									
									
									if ( isFinal && isCard) {
										cardNo 		= result.getString("fml_ret3");	//�����ڵ�
										debug("=============== cardNo : " + cardNo);
										
										
										//��ȿ�Ⱓ ��
										debug("## ��ȿ�Ⱓ�� | todayDate : "+todayDate+" | ī����ȿ�Ⱓ : "+cardValdDate);
										
								        Date end_date = format.parse( cardValdDate );
								        Date current_date = format.parse( todayDate );

								        if ( current_date.getTime() > end_date.getTime() )
								        {
								            debug("## ��ȿ�Ⱓ ����� ���� ����ī�� ��");
								        }
								        else
								        {
								        	debug("## ��ȿ�Ⱓ ��밡��");
								        	break;
								        }

										
										
										
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
						 * 4. ȸ������ ���� UPDATE ����
						 ***************************************************************** */
						input = new JoltInput(JoltXAServiceName);
	
						String name = bcuser.getName();

						TaoResult output1 = null;							
						input = new JoltInput(JoltXAServiceName);
						debug("bankcode:"+bankcode);
						String bankAlias = "";
						
						String detailAddr = parser.getParameter("detailaddr", "");
						String co_detailaddr = parser.getParameter("co_detailaddr");
	
						String co_name = parser.getParameter("co_name");
						String position_name = parser.getParameter("position_name");
						
						// WafService (������� ���� �Է� : �ѱ۹��� )
						if ( request instanceof com.bccard.waf.action.ServiceRequest ) {
	
							detailAddr = new String(detailAddr.getBytes("ISO-8859-1"), "UTF-8");
							co_detailaddr = new String(co_detailaddr.getBytes("ISO-8859-1"), "UTF-8");
							
							co_name = new String(co_name.getBytes("ISO-8859-1"), "UTF-8");
							position_name = new String(position_name.getBytes("ISO-8859-1"), "UTF-8");
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
						input.setString("fml_arg13", parser.getParameter("phone_0", "")) ;		// ������ȭ��ȣ
						input.setString("fml_arg14", parser.getParameter("phone_1", "")) ;		// ������ȭ��ȣ1
						input.setString("fml_arg15", parser.getParameter("phone_2", "")) ;		// ������ȭ��ȣ2
						input.setString("fml_arg16", parser.getParameter("zipcode1", "") + parser.getParameter("zipcode2", ""));	// ���ÿ����ȣ
		
						String addrClss = parser.getParameter("addrClss", ""); // �ּұ����ڵ� => 1:���ּ�, 2:���ּ�, 3:�̽���籸�ּ�  (3���� ���ͳݰ� ����)
						
						if ( addrClss.equals("2") ) {
							input.setString("fml_arg17", this.cpReplace(parser.getParameter("zipaddr", ""))) ;// ���ּ���  �⺻�ּ�
							input.setString("fml_arg202", this.cpReplace(parser.getParameter("detailaddr", ""))) ; // ���ּ���  ���ּ�
							input.setString("fml_arg201", addrClss) ; // �����ּұ����ڵ�  '2'
							input.setString("fml_arg203", parser.getParameter("roadCode", "")) ;	//���� ���θ� �ڵ�							
						}else {
							input.setString("fml_arg17", this.cpReplace(parser.getParameter("detailaddr", ""))) ;// ���ּ� ������
							input.setString("fml_arg201", addrClss) ; // �����ּұ����ڵ� '1'
						}

						String fml_arg58 = parser.getParameter("fml_arg58", "");
	
						if(!"".equals(fml_arg58)) {			//���� �� �����ΰ��
							input.setString("fml_arg58","1");
							input.setString("fml_arg11","1");
						}else{											//������ �ִ°��
	
							input.setString("fml_arg8", co_name);
							input.setString("fml_arg9", position_name);
	
							input.setString("fml_arg53",parser.getParameter("co_phone_0"));
							input.setString("fml_arg54",parser.getParameter("co_phone_1"));
							input.setString("fml_arg55",parser.getParameter("co_phone_2"));
							
							if(co_detailaddr != null){
								
								String addrClss2 = parser.getParameter("addrClss2", ""); // �ּұ����ڵ� => 1:���ּ�, 2:���ּ�, 3:�̽���籸�ּ�  (3���� ���ͳݰ� ����)
								
								input.setString("fml_arg56",parser.getParameter("co_zipcode1") + parser.getParameter("co_zipcode2"));
								
								if ( addrClss2.equals("2") ) {
									input.setString("fml_arg57", this.cpReplace(parser.getParameter("co_zipaddr", ""))) ;// ���ּ���  �⺻�ּ�
									input.setString("fml_arg205", this.cpReplace(parser.getParameter("co_detailaddr", ""))) ; // ���ּ���  ���ּ�
									input.setString("fml_arg204", addrClss2) ; // �ٹ��ּұ����ڵ�  '2'
									input.setString("fml_arg206", parser.getParameter("roadCode2", "")) ;	//�ٹ� ���θ� �ڵ�							
								}else {
									input.setString("fml_arg57", this.cpReplace(parser.getParameter("co_detailaddr", ""))) ;// ���ּ� ������
									input.setString("fml_arg204", addrClss2) ; // �ٹ��ּұ����ڵ� '1'
								}								
								
							}

						}
						
						String mob0 = parser.getParameter("mobile_0", "");
						String mob1 = parser.getParameter("mobile_1", "");
						String mob2 = parser.getParameter("mobile_2", "");
	
						if ( "".equals(mob1)) {
							mob0 = "";
							mob1 = "";
							mob2 = "";
						}
						
						input.setString("fml_arg65",mob0) ;			// ���ݱ���
						input.setString("fml_arg66",mob1) ;			// ���ݹ�ȣ1
						input.setString("fml_arg67",mob2) ;			// ���ݹ�ȣ2

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
				
					String msg = "";
					/** *****************************************************************
					 * 4. ȸ������ ���� WEB DB UPDATE ����
					 ***************************************************************** */
					if (proc.getMemberInfoUpdate2(context, parser, session, request, cardUser)) {
						msg = "## MemUpdActlog | WEBDB Update ���� " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
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
						msg = "## MemUpdActlog | WEBDB Update ���� | " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
						request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
						System.out.println(msg);

						request.setAttribute("resultChk", "01");
					}

					request.setAttribute("result", "00");
					System.out.print(" ## MemUpdActlog | ���������� ��������Ϸ� | ID : "+bcuser.getAccount()+" \n");
				
				} catch(Throwable t) {
					//t.printStackTrace();
					subpage_key = "error";
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
		return super.getActionResponse(context, subpage_key);
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
