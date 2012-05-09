/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemPopInqActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : �̺�Ʈ-> ȸ������ ���� �˾�
*   �������  : Golf
*   �ۼ�����  : 2009-10-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*2011.11.08  ���ּ� �ϸ鼭 ������� �ʴ� ������� ó���� ���� ���� ���� �� �׽�Ʈ ��
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
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
public class GolfMemPopInqActn extends AbstractAction {

	public static final String Title = "�̺�Ʈ->ȸ������ ���� �˾�";
	static final String JoltServiceName = "BSNINPT";
	static final String TSN301 = "MHK3010R0100";		// SMS ȸ�� ���� ��ȸ
	static final String TSN040 = "MHL0040R0100";		// ����ȸ�� ���� ��ȸ
	static final String TSN060 = "MHL0060R0100";

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
		
		ResultException re;
		String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // ��ư
		String goPage = "/app/card/memberActn.do"; // �̵� �׼�
		
		try {
			
			Map paramMap 			= parser.getParameterMap();																				
			
			UcusrinfoEntity user = SessionUtil.getFrontUserInfo(request);
			
			if(user != null)
			{
				//�α��ν� ȸ������ �������� ����
				IndModifyOnlineProc proc  = (IndModifyOnlineProc)context.getProc("IndModifyOnlineProc");
				
				JtProcess jt = new JtProcess();			
				
				String zipcode2[] = new String[2];
				
				try {

					String co_zipcode2[] = new String[2];
					String zipcode = "";
					String co_zipcode = "";
					

					try {
						if("1".equals(user.getMemberClss())) {
							JoltInput entity = new JoltInput(JoltServiceName);
							entity.setString("fml_trcode", TSN040);
							entity.setString("fml_arg1", user.getSocid());
				
							java.util.Properties prop = new java.util.Properties();
							prop.setProperty("RETURN_CODE", "fml_ret1");
				
							TaoResult taoResult = null;
							taoResult = jt.call(context, request, entity, prop);
				
							String rtnCode = ""; // �����ڵ�
							if (taoResult.containsKey("fml_ret1")){
								rtnCode = taoResult.getString("fml_ret1").trim(); // �����ڵ�
							}
				
							if( !"1".equals(rtnCode) ){
								re = new ResultException();
								re.setTitleImage("error");
								re.setTitleText(Title);
								re.setKey("UHL004_Ind_Ret_" + rtnCode);
								re.addButton(goPage, addButton);
								throw re;
							}
				
							if (taoResult.containsKey("fml_ret3")){
								zipcode = taoResult.getString("fml_ret3").trim(); // �����ȣ
							}
				
				
							if("".equals(zipcode.trim())) {
								zipcode2[0] = "";
								zipcode2[1] = "";
							}else {
								zipcode2 = proc.phoneMobile(zipcode, "-"); // ���� �����ȣ�и�
							}

							if (taoResult.containsKey("fml_ret8")){
								co_zipcode = taoResult.getString("fml_ret8").trim(); // �ٹ�ó �����ȣ
							}
				
							if("".equals(co_zipcode.trim())) {
								co_zipcode2[0] = "";
								co_zipcode2[1] = "";
							}else {
								co_zipcode2 = proc.phoneMobile(co_zipcode,"-");// �ٹ�ó �����ȣ�и�
							}
				
							// ������ �����ȣ���� ����
							request.setAttribute("zipcode_0",zipcode2[0]);
							request.setAttribute("zipcode_1",zipcode2[1]);
							request.setAttribute("co_zipcode_0",co_zipcode2[0]);
							request.setAttribute("co_zipcode_1",co_zipcode2[1]);
				
							request.setAttribute("NAME",  taoResult.containsKey("fml_ret2")?taoResult.getString("fml_ret2"):"");
							request.setAttribute("zipaddr",taoResult.containsKey("fml_ret16")?taoResult.getString("fml_ret16"):"");
							request.setAttribute("detailaddr",taoResult.containsKey("fml_ret17")?taoResult.getString("fml_ret17"):"");
							request.setAttribute("co_zipaddr",taoResult.containsKey("fml_ret18")?taoResult.getString("fml_ret18"):"");
							request.setAttribute("co_detailaddr",taoResult.containsKey("fml_ret19")?taoResult.getString("fml_ret19"):"");
							request.setAttribute("phone_0",taoResult.containsKey("fml_ret5")?taoResult.getString("fml_ret5"):"");
							request.setAttribute("phone_1",taoResult.containsKey("fml_ret6")?taoResult.getString("fml_ret6"):"");
					   		request.setAttribute("phone_2",taoResult.containsKey("fml_ret7")?taoResult.getString("fml_ret7"):"");
							request.setAttribute("co_phone_0",taoResult.containsKey("fml_ret10")?taoResult.getString("fml_ret10"):"");
							request.setAttribute("co_phone_1",taoResult.containsKey("fml_ret11")?taoResult.getString("fml_ret11"):"");
							request.setAttribute("co_phone_2",taoResult.containsKey("fml_ret12")?taoResult.getString("fml_ret12"):"");
							request.setAttribute("co_name",taoResult.containsKey("fml_ret13")?taoResult.getString("fml_ret13"):"");
							request.setAttribute("position_name",taoResult.containsKey("fml_ret14")?taoResult.getString("fml_ret14"):"");
							request.setAttribute("fml_ret20",taoResult.containsKey("fml_ret20")?taoResult.getString("fml_ret20"):"");
							request.setAttribute("socid",user.getSocid());
				
							// 1113 ���忡�� �ִ� �ڵ��� ��ȣ�� ����
							request.setAttribute("mobile_0",taoResult.getString("fml_ret21"));
							request.setAttribute("mobile_1",taoResult.getString("fml_ret22"));
							request.setAttribute("mobile_2",taoResult.getString("fml_ret23"));
							
							paramMap.put("mobile_0", taoResult.getString("fml_ret21"));   
							paramMap.put("mobile_1", taoResult.getString("fml_ret22"));   
							paramMap.put("mobile_2", taoResult.getString("fml_ret23")); 

							paramMap.put("zipcode1", zipcode2[0]);   
							paramMap.put("zipcode2", zipcode2[1]);   
							paramMap.put("zipaddr", taoResult.containsKey("fml_ret16")?taoResult.getString("fml_ret16"):"");   
							paramMap.put("detailaddr", taoResult.containsKey("fml_ret17")?taoResult.getString("fml_ret17"):"");

					   		paramMap.put("phone_0", taoResult.containsKey("fml_ret5")?taoResult.getString("fml_ret5"):"");   
							paramMap.put("phone_1", taoResult.containsKey("fml_ret6")?taoResult.getString("fml_ret6"):"");   
							paramMap.put("phone_2", taoResult.containsKey("fml_ret7")?taoResult.getString("fml_ret7"):"");  
						}
					} catch(Throwable t) {
						//t.printStackTrace();
						re = new ResultException();
						re.setTitleImage("error");
						re.setTitleText(Title);
						re.addButton(goPage, addButton);
						re.setKey("SYSTEM_ERROR");
						throw re;
					}

					Hashtable rsHash = null;
					try {
						rsHash = proc.getInfoChange(context,user.getAccount(),user.getMemberClss());
					} catch (Throwable t) {
						re = new ResultException();
						re.setTitleImage("error");
						re.setKey("MODIFY_MEMBER_02");
						re.setTitleText(Title);
						re.addButton(goPage, addButton);
						re = new ResultException();
						throw re;
					}

					// DB���� ���� ����Ÿ�� ����
					request.setAttribute("MEMBER_CLSS",(String)rsHash.get("MEMBER_CLSS"));
					request.setAttribute("SOCID_1",(String)rsHash.get("SOCID_1"));
					request.setAttribute("SOCID_2",(String)rsHash.get("SOCID_2"));
					request.setAttribute("SEX",(String)rsHash.get("SEX"));
					request.setAttribute("ACCOUNT",(String)rsHash.get("ACCOUNT"));
					request.setAttribute("PASSWD",(String)rsHash.get("PASSWD"));
					request.setAttribute("PASSWDQ",(String)rsHash.get("PASSWDQ"));
					request.setAttribute("PASSWDA",(String)rsHash.get("PASSWDA"));
					request.setAttribute("ENAME",(String)rsHash.get("ENAME"));
					request.setAttribute("BIRTH_1",(String)rsHash.get("BIRTH_1"));
					request.setAttribute("BIRTH_2",(String)rsHash.get("BIRTH_2"));
					request.setAttribute("BIRTH_3",(String)rsHash.get("BIRTH_3"));
					request.setAttribute("SOLAR",(String)rsHash.get("SOLAR"));
					request.setAttribute("JOB",(String)rsHash.get("JOB"));
					request.setAttribute("JOBTYPE",(String)rsHash.get("JOBTYPE"));

					request.setAttribute("RECOMM_ACCOUNT",(String)rsHash.get("RECOMM_ACCOUNT"));
					request.setAttribute("ADDRESS",(String)rsHash.get("ADDRESS"));
					request.setAttribute("EMAIL",(String)rsHash.get("EMAIL"));
					request.setAttribute("MAILING",(String)rsHash.get("MAILING"));
					request.setAttribute("CARD_RECV_YN",(String)rsHash.get("CARD_RECV_YN"));
					request.setAttribute("TOUR_RECV_YN",(String)rsHash.get("TOUR_RECV_YN"));
					request.setAttribute("SHOPPING_RECV_YN",(String)rsHash.get("SHOPPING_RECV_YN"));
					request.setAttribute("CTNT_RECV_YN",(String)rsHash.get("CTNT_RECV_YN"));
					request.setAttribute("EMAIL_URL",(String)rsHash.get("EMAIL_URL"));
					request.setAttribute("RECV_YN",(String)rsHash.get("RECV_YN"));

					if(!"1".equals(user.getMemberClss())) {
						request.setAttribute("NAME",(String)rsHash.get("NAME"));
						request.setAttribute("socid",(String)rsHash.get("SOCID"));

						String mobile = (String)rsHash.get("MOBILE");
						String mobile2[] = new String[3];
						if("".equals(mobile.trim())) {
							mobile2[0] = "";
							mobile2[1] = "";
							mobile2[2] = "";
						}else {
							mobile2 = proc.phoneMobile(mobile, "-"); // ���� �����ȣ�и�
						}

					paramMap.put("mobile_0", mobile2[0]);   
					paramMap.put("mobile_1", mobile2[1]);   
					paramMap.put("mobile_2", mobile2[2]); 

						request.setAttribute("mobile_0",mobile2[0]);
						request.setAttribute("mobile_1",mobile2[1]);
						request.setAttribute("mobile_2",mobile2[2]);

						String phone = (String)rsHash.get("PHONE");

						String phone2[] = new String[3];

						if("".equals(phone.trim()) || "--".equals(phone.trim())) {
							phone2[0] = "";
							phone2[1] = "";
							phone2[2] = "";
						}else {
							phone2 = proc.phoneMobile(phone, "-"); // ���� �����ȣ�и�
						}
						request.setAttribute("phone_0", phone2[0]);
						request.setAttribute("phone_1", phone2[1]);
				   		request.setAttribute("phone_2", phone2[2]);

			   		paramMap.put("phone_0", phone2[0]);   
					paramMap.put("phone_1", phone2[1]);   
					paramMap.put("phone_2", phone2[2]); 


						zipcode = (String)rsHash.get("ZIPCODE"); // �����ȣ

						if("".equals(zipcode.trim())) {
							zipcode2[0] = "";
							zipcode2[1] = "";
						}else {
							zipcode2 = proc.phoneMobile(zipcode, "-"); // ���� �����ȣ�и�
						}

				   		request.setAttribute("zipcode_0",zipcode2[0]);
				   		request.setAttribute("zipcode_1",zipcode2[1]);
					paramMap.put("zipcode1", zipcode2[0]);   
					paramMap.put("zipcode2", zipcode2[1]);
				   		request.setAttribute("zipaddr",(String)rsHash.get("ZIPADDR"));
				   		request.setAttribute("detailaddr",(String)rsHash.get("DETAILADDR"));

					paramMap.put("zipaddr", (String)rsHash.get("ZIPADDR"));   
					paramMap.put("detailaddr", (String)rsHash.get("DETAILADDR"));

			/*			co_zipcode = (String)rsHash.get("ZIPCODE"); // �����ȣ
						if("".equals(co_zipcode.trim())) {
							co_zipcode2[0] = "";
							co_zipcode2[1] = "";
						}else {
							co_zipcode2 = proc.phoneMobile(co_zipcode,"-");// �ٹ�ó �����ȣ�и�
						}
						*/
					}

					// ucusrinfo, identity ������ sync. ����
					IndModifyOnlineProc procModify  = (IndModifyOnlineProc)context.getProc("IndModifyOnlineProc");

					try {
						if("1".equals(user.getmemberClssCard())) {
							procModify.setMemberInfo(context, request);
						}
					} catch(Throwable t) {
						//t.printStackTrace();
						debug("Exception : " + t.getMessage());

					/*****************************************************************************
				         * LOG �� ����� ���� �۾�
				         * REG|ShowIndActn.IndModifyOnlineProc.setMemberInfo(context, request)|ȸ������|�ֹι�ȣ|����|�̸�|����IP|���۽ð�
				        ******************************************************************************/
				        String msg = "REG|ShowIndActn.IndModifyOnlineProc.setMemberInfo(context, request)|1|" + user.getSocid() + "|" + user.getAccount() + "|" + request.getAttribute("NAME") + "|" + request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss") + t.getMessage();

				        //BcLog.memberLog("���� ȸ������ ��ȸ WEB DB Sync. ����");
				        //BcLog.memberLog(msg);
				        System.out.println("���� ȸ������ ��ȸ WEB DB Sync. ����");
						System.out.println(msg);

					}
					
					
					 
					
				
				
				} catch(Throwable t) {
					t.printStackTrace();
					re = new ResultException();
					re.setTitleImage("error");
					re.setTitleText(Title);
					re.addButton(goPage, addButton);
					re.setKey("SYSTEM_ERROR");
					throw re;
				}
			
			
				request.setAttribute("result", "00");
			}
			else
			{
				//��α��ν�
				request.setAttribute("result", "01");
				
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
}