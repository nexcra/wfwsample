/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemSkiSaleAuthActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��������� ��Ű�Ǳ� üũ
*   �������  : Golfloung
*   �ۼ�����  : 2009-11-17
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 * ��������� ��Ű�Ǳ� üũ Ŭ����.
 * @author
 * @version 2009-11-17
 **************************************************************************** */
public class GolfMemSkiSaleAuthActn extends AbstractAction {
	public static final String TITLE = "BC Golf ��Ű�Ǳ���üũ";
	
	/** ****************************************************************************
	* @version   2009.11.17
	* @author    
	**************************************************************************** */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		TaoConnection con = null;
		ResultException re;
		
		String subpage_key = "default";
		String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // ��ư
		String goPage = "/app/card/memberActn.do"; // �̵� �׼�

		try {
			// 01.��������üũ
			UcusrinfoEntity user = SessionUtil.getFrontUserInfo(request);
			//String account = user.getAccount();

			// 02.�Է°� ��ȸ		 
			RequestParser parser = context.getRequestParser(subpage_key, request, response);

			String key 	= "bcdnjfqhrskfrufw";
			String iv 	= "rufwpdnjfqhrskfb";
			StringEncrypter encrypter = new StringEncrypter(key, iv);
			String flag			= "NO";

			// Request �� ����
			String account = parser.getParameter("arg1","");
			
			debug("============ account : " + account);
			account = GolfUtil.replace(account, "^^^", "+");
			debug("============ account replace ^^^ - + : " + account);
			//account = encrypter.encrypt(account);
			//debug("============ account : " + account);
			
			String daccount = encrypter.decrypt(account);
			//URLEncoder.encode(encrypter.encrypt(checkId);
			debug("============ daccount : " + daccount);
		
			if("".equals(daccount)) {
				// �������� - ����
				re = new ResultException();
				re.setTitleImage("error");
				re.setTitleText(TITLE);
				re.setKey("USERCERT_ERROR");
				re.addButton(goPage, addButton);
				throw re;
			}
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("account", daccount);
			
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
//					flag 		= "true";
					//flag = encrypter.encrypt("true");
				}
			}

			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("flag",		flag);
			paramMap.put("account",		account);
			paramMap.put("daccount",	daccount);
			
			//request.setAttribute("flag", flag);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		}finally{
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		} 
		return getActionResponse(context, subpage_key);
	}
}

