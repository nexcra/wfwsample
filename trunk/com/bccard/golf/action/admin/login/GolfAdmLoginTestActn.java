/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : TpAdmLoginActn
*   작성자    : (주)미디어포스 권영만
*   내용      : 포인트 관리자 로그인 프로세스
*   적용범위  : Topn
*   작성일자  : 2009-03-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmLoginTestActn extends GolfActn {
	
	public static final String TITLE = "비씨골프  관리자 ID/PW 검증";

	/***************************************************************************************
	* 비씨탑포인트관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체.  
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
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
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				return getActionResponse(context, "admin");
			}
			
			//2.입력값 조회
			RequestParser parser = context.getRequestParser("default", request, response);
			String account		= parser.getParameter("id", "");
			String passwd		= parser.getParameter("passwd", "");
				//debug("================>id : " + account);
				//debug("================>PASSWRD : " + passwd);
			
			//3. 관리자정보 조회
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("account", account);
			
			GolfAdmLoginlnqTestDaoProc proc = (GolfAdmLoginlnqTestDaoProc)context.getProc("GolfAdmLoginlnqTestDaoProc");
			taoResult = (DbTaoResult)proc.execute(context, dataSet);	// 관리자 조회

			
			if(taoResult != null && taoResult.isNext() ) {
				//debug("================>taoResult is not null ");
				taoResult.next();
				//debug("RESULT : "+taoResult.getString("RESULT"));
				//debug("DV_WAS_1ST : "+DV_WAS_1ST);
				if("00".equals(taoResult.getString("RESULT"))){
					String newPass	     = passwd.trim();								// 사용자에게 입력받은 암호
										
					//byte[] oldPass		= (byte[])taoResult.getObject("PASWD");		// DB에저장되어 있는 암호
					oldPass = taoResult.getString("PASWD");
					account_id = taoResult.getString("ACCOUNT");
					name = taoResult.getString("NAME");
					//isPsssOk = verifyPassWord( newPass , oldPass );						// 비밀번호 체크
				//	if(!"130.1.192.54".equals(DV_WAS_1ST))
				//	{
				//		isPsssOk = verifyPassWord( newPass , oldPass );						// 비밀번호 체크
				//	}
				//	else
				//	{
						//isPsssOk = true;
				//	}
					
						
						// 로그인 체크 추가
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
			
			//isPsssOk = true; //임시
			debug("로그인 isPsssOk"+isPsssOk);
			//4. 로그인 성공시에  세션만들기
			if (isPsssOk) {
				//debug("===========>세션 담기");
				userEtt = new GolfAdminEtt();
				//debug("===========>세션 담기 1-1");
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
				
				//----------------------HttpSession 세션에 넣기----------
				session.setAttribute("SESSION_ADMIN", userEtt);
				//------------------END---------------------------------
				subpage_key = "admin";
				
			} else {	// CERT Error
				
				String rtnMsg = "비밀번호 오류";
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,rtnMsg);
				throw new GolfException(msgEtt);
			}
			
			//debug("관리자  로그인 일시 로그 저장 ");
			//5. 최근접속일자 저장
			GolfAdmLogUpdProc proc1 = (GolfAdmLogUpdProc)context.getProc("GolfAdmLogUpdProc");
			int updRes = proc1.execute(context, dataSet);
			
			
			//debug("==== GolfAdmLoginActn end ===");
			
		} catch(Throwable t) {
			return errorHandler(context,request,response,t);
		} 
		
		return getActionResponse(context, subpage_key);
    }

}