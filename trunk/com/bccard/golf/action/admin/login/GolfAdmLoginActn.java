/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLoginActn
*   �ۼ���    : (��)�̵������
*   ����      : ����Ʈ ������ �α��� ���μ���
*   �������  : Golf
*   �ۼ�����  : 2009-03-19
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
*20110325   �̰��� 	 master���ѿ� leekh �߰� (���Ŀ� �ϵ��ڵ� ��������)
***************************************************************************************************/
 
package com.bccard.golf.action.admin.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfElecAauthProcess;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.GolfAdmLoginlnqDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmLoginActn extends GolfActn {
	
	public static final String TITLE = "�񾾰���  ������ ID/PW ����";

	/***************************************************************************************
	* ��ž����Ʈ�����ڷα��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü.  
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	 * @throws BaseException 
	***************************************************************************************/
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws BaseException {		

		HttpSession session = null;  
		GolfAdminEtt userEtt = null;
		String subpage_key = "default";
		boolean isPsssOk =false;
		String rtnMsg = ""; 
		
		String mem_id = null;
		String user_nm = null;
		String user_dn = null;
		String user_nm_yn = null;
		String adm_clss = null;
		
		GolfElecAauthProcess elecAath = new GolfElecAauthProcess();//������������
		String validCert = "false";
		String clientAuth = "false";
		String[] semiCertVal = new String[2]; 		
		
		try {
			
			//1.��������üũ
			session = request.getSession();
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				return getActionResponse(context, "admin");
			}			
			
			semiCertVal = elecAath.semiCert(request, response);
			
			validCert = semiCertVal[0];
			clientAuth = semiCertVal[1];
			
			if (validCert.equals("false")){
				rtnMsg = "�ùٸ� �������� �ƴմϴ�.";
			}
			
			if (clientAuth.equals("false")){
				debug("������ �ʿ���� ������");				
			}

			debug (" ### validCert : " + validCert +", clientAuth : " + clientAuth);
			
			if (validCert.equals("true") && clientAuth.equals("true")){
				
				RequestParser parser = context.getRequestParser("standard", request, response);
				String jumin_no = parser.getParameter("jumin_no1","") + parser.getParameter("jumin_no2","");
	
				GolfAdmLoginlnqDaoProc proc = (GolfAdmLoginlnqDaoProc)context.getProc("GolfAdmLoginlnqDaoProc");
				
//				if(!elecAath.isValidCert(request, response, jumin_no, "3")){ 
//					
//					elecAath.setValidCertMsg(session);					
//					String sessionMsg = (String)session.getAttribute("validCertMsg");
//					request.setAttribute("msg", sessionMsg);				
//					
//					return super.getActionResponse(context, "default");	
//					
//				}			
				
				String userDn = "";//elecAath.getUserDn();
	
				//3. �������̺��� ����� DN������ ��ȸ
				DbTaoDataSet dataset = new DbTaoDataSet(TITLE);
	            dataset.setString("userDn", userDn );
	            dataset.setString("jumin_no", jumin_no );
	           
				// 4. ����� DN�� �ٸ���� ������Ʈ ���ش�. 
	            int dBuserDnChg = proc.getDbUserDn(context, dataset);
				//debug("================>dBuserDnChg : " + dBuserDnChg);
	            
	            TaoResult tUser = proc.execute(context, dataset);
	            
				if(tUser.isFirst()) tUser.next();
			
				if ("01".equals(tUser.getString("RESULT"))){
					rtnMsg = "��ϵ� ����ڰ� �ƴմϴ�.";
				} else { 
									
					mem_id = tUser.getString("ACCOUNT");
					
					if(mem_id.equals("admin") ||  mem_id.equals("zeil")|| mem_id.equals("j_yoonho") || mem_id.equals("hoyeon0721")
							|| mem_id.equals("judy")|| mem_id.equals("eundeng2") || mem_id.equals("bccard")|| mem_id.equals("gungom")  
							|| mem_id.equals("steve2") || mem_id.equals("mongina")|| mem_id.equals("leekh")){					
						adm_clss = "master";
					}else{
						adm_clss = "default";
					}
					user_nm = tUser.getString("NAME");
					user_dn = tUser.getString("AZT_CFT_PROOF_VAL");
					user_nm_yn = tUser.getString("NM_YN");
								
					if(GolfUtil.empty(user_dn)){			
						// ���� ������ �μ�Ʈ -> ���� ��ȸ -> �α���
						if(!user_nm_yn.equals("0")){			
							isPsssOk = true;
						}else{	
							rtnMsg = "������ ���� ���� ����";
						}
					}else{			
						if(!user_nm_yn.equals("0")){			
							isPsssOk = true;
						}else{
							rtnMsg = "������ ���� ���� ����";
						}
					}
								
				}
				
				//4. �α��� �����ÿ�  ���Ǹ����
				if (isPsssOk) {
					
					userEtt = new GolfAdminEtt();
					userEtt.setMemNo("1");
					userEtt.setMemId(mem_id);
					userEtt.setMemNm(user_nm);
					userEtt.setLogin(true); 
					userEtt.setAdm_clss(adm_clss);
					session.setAttribute("SESSION_ADMIN", userEtt);
					session.setMaxInactiveInterval(2400);
	
					subpage_key = "admin";
					
				} else {	
					MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,rtnMsg);
					throw new GolfException(msgEtt);
				}				
			}
			
		} catch(Exception t) {	
			t.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
		}		
		
		request.setAttribute("msg", rtnMsg);
		return getActionResponse(context, subpage_key);
		
    }
	
}