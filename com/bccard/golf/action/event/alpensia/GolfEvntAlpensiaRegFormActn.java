/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntAlpensiaRegFormActn
*   작성자	: (주)미디어포스 임은혜
*   내용		: 이벤트 > 알펜시아 > 부킹 신청 폼
*   적용범위	: Golf
*   작성일자	: 2010-06-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.alpensia;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.alpensia.GolfEvntAlpensiaNoticeIfmDaoProc;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfEvntAlpensiaRegFormActn extends GolfActn {
	
	public static final String TITLE = "이벤트 > 알펜시아 > 부킹 신청 페이지";
	
	/***************************************************************************************
	* 비씨골프 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {

		String subpage_key = "default";		
		DbTaoConnection con = null;

		// 리턴 변수
		String script = "";
		String userNm = "";
		String userId = "";
		String juminno = ""; 
		String juminno1 = ""; 
		String juminno2 = ""; 
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		String phone1 = "";
		String phone2 = "";
		String phone3 = "";
		
		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);


			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				juminno1	= juminno.substring(0, 6);
				juminno2	= juminno.substring(6, 13);

				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
				
				phone1 	= (String)usrEntity.getPhone1(); 
				phone2 	= (String)usrEntity.getPhone2(); 
				phone3 	= (String)usrEntity.getPhone3(); 
			}
			
			paramMap.put("userNm", userNm); 
			paramMap.put("userId", userId);
			paramMap.put("juminno1", juminno1);
			paramMap.put("juminno2", juminno2);
			paramMap.put("mobile1", mobile1);
			paramMap.put("mobile2", mobile2);
			paramMap.put("mobile3", mobile3);
			paramMap.put("phone1", phone1);
			paramMap.put("phone2", phone2);
			paramMap.put("phone3", phone3);
			
	        request.setAttribute("paramMap", paramMap);
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
