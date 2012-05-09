/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : TpAdmLoginActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ����Ʈ ������ �α��� ���μ���
*   �������  : Topn
*   �ۼ�����  : 2009-03-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
 
package com.bccard.golf.action.admin.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.initech.dbprotector.CipherClient;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.GolfAdmLoginlnqTestDaoProc;
import com.bccard.golf.dbtao.proc.admin.GolfAdmLogUpdProc;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmLoginTestActn extends GolfActn {
	
	public static final String TITLE = "�񾾰���  ������ ID/PW ����";

	/***************************************************************************************
	* ��ž����Ʈ�����ڷα��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü.  
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		GolfAdminEtt userEtt = null;
		DbTaoResult taoResult = null;
		
		String subpage_key = "default";
		boolean isPsssOk =false;
		String oldPass = "";
		String account_id = "";
		String name = "";
		
		try {
			//debug("==== GolfAdmLoginActn start ===");
			
			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				return getActionResponse(context, "admin");
			}
			
			//2.�Է°� ��ȸ
			RequestParser parser = context.getRequestParser("default", request, response);
			String account		= parser.getParameter("id", "");
			String passwd		= parser.getParameter("passwd", "");
				//debug("================>id : " + account);
				//debug("================>PASSWRD : " + passwd);
			
			//3. ���������� ��ȸ
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("account", account);
			
			GolfAdmLoginlnqTestDaoProc proc = (GolfAdmLoginlnqTestDaoProc)context.getProc("GolfAdmLoginlnqTestDaoProc");
			taoResult = (DbTaoResult)proc.execute(context, dataSet);	// ������ ��ȸ

			
			if(taoResult != null && taoResult.isNext() ) {
				//debug("================>taoResult is not null ");
				taoResult.next();
				//debug("RESULT : "+taoResult.getString("RESULT"));
				//debug("DV_WAS_1ST : "+DV_WAS_1ST);
				if("00".equals(taoResult.getString("RESULT"))){
					String newPass	     = passwd.trim();								// ����ڿ��� �Է¹��� ��ȣ
										
					//byte[] oldPass		= (byte[])taoResult.getObject("PASWD");		// DB������Ǿ� �ִ� ��ȣ
					oldPass = taoResult.getString("PASWD");
					account_id = taoResult.getString("ACCOUNT");
					name = taoResult.getString("NAME");
					//isPsssOk = verifyPassWord( newPass , oldPass );						// ��й�ȣ üũ
				//	if(!"130.1.192.54".equals(DV_WAS_1ST))
				//	{
				//		isPsssOk = verifyPassWord( newPass , oldPass );						// ��й�ȣ üũ
				//	}
				//	else
				//	{
						//isPsssOk = true;
				//	}
					
						
						// �α��� üũ �߰�
						if(newPass.equals(oldPass)){
							isPsssOk = true;
						}
					
				}else {
					//debug("================>taoResult : " + taoResult.getString("RESULT"));
				}
		
			} else {
				//debug("================>taoResult is null ");
			}
			

			debug("======GolfAdmLoginActn==========>account_id : " + account_id);
			debug("======GolfAdmLoginActn==========>name : " + name);
			
			//isPsssOk = true; //�ӽ�
			debug("�α��� isPsssOk"+isPsssOk);
			//4. �α��� �����ÿ�  ���Ǹ����
			if (isPsssOk) {
				//debug("===========>���� ���");
				userEtt = new GolfAdminEtt();
				//debug("===========>���� ��� 1-1");
				userEtt.setMemNo("1");
				userEtt.setMemId(account_id);
				userEtt.setMemNm(name);
				userEtt.setLogin(true); 
				
				
				
				//userEtt.setMemNo(taoResult.getString("SEQ_NO"));
				//userEtt.setMemId(taoResult.getString("ACCOUNT"));
				//userEtt.setDocRoot(strDOC_ROOT_PATH);
				//userEtt.setWebRoot(strDOC_WEB_PATH);
				//userEtt.setMemNm(taoResult.getString("NAME"));
				//debug("isLogin : " + userEtt.isLogin());
				
				//----------------------HttpSession ���ǿ� �ֱ�----------
				session.setAttribute("SESSION_ADMIN", userEtt);
				//------------------END---------------------------------
				subpage_key = "admin";
				
			} else {	// CERT Error
				
				String rtnMsg = "��й�ȣ ����";
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,rtnMsg);
				throw new GolfException(msgEtt);
			}
			
			//debug("������  �α��� �Ͻ� �α� ���� ");
			//5. �ֱ��������� ����
			GolfAdmLogUpdProc proc1 = (GolfAdmLogUpdProc)context.getProc("GolfAdmLogUpdProc");
			int updRes = proc1.execute(context, dataSet);
			
			
			//debug("==== GolfAdmLoginActn end ===");
			
		} catch(Throwable t) {
			return errorHandler(context,request,response,t);
		} 
		
		return getActionResponse(context, subpage_key);
    }

}