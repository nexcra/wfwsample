/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemSkiSaleAuthActn
*   작성자    : (주)미디어포스 진현구
*   내용      : 골프라운지 스키판권 체크
*   적용범위  : Golfloung
*   작성일자  : 2009-11-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
 * 골프라운지 스키판권 체크 클래스.
 * @author
 * @version 2009-11-17
 **************************************************************************** */
public class GolfMemSkiSaleAuthActn extends AbstractAction {
	public static final String TITLE = "BC Golf 스키판권자체크";
	
	/** ****************************************************************************
	* @version   2009.11.17
	* @author    
	**************************************************************************** */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		TaoConnection con = null;
		ResultException re;
		
		String subpage_key = "default";
		String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // 버튼
		String goPage = "/app/card/memberActn.do"; // 이동 액션

		try {
			// 01.세션정보체크
			UcusrinfoEntity user = SessionUtil.getFrontUserInfo(request);
			//String account = user.getAccount();

			// 02.입력값 조회		 
			RequestParser parser = context.getRequestParser(subpage_key, request, response);

			String key 	= "bcdnjfqhrskfrufw";
			String iv 	= "rufwpdnjfqhrskfb";
			StringEncrypter encrypter = new StringEncrypter(key, iv);
			String flag			= "NO";

			// Request 값 저장
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
				// 인증실패 - 오류
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
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
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

