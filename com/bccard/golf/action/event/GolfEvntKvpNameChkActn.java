/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntKvpNameChkActn 
*   작성자	: (주)미디어포스 임은혜
*   내용		: KVP > 실명인증
*   적용범위	: golf
*   작성일자	: 2010-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;

import com.bccard.common.NameCheck;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntKvpNameChkActn extends GolfActn{
	
	public static final String TITLE = "KVP 처리";
	private static final String SITEID = "I829";		// 한신평 코드
	private static final String SITEPW = "44463742";	// 한신평 PASSWORD

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
				

		try {

			String socid = ""; 			// 주민번호
			String social_id_1 = "";
			String social_id_2 = ""; 
			String name = ""; 
			String ddd_no = ""; 
			String tel_hno = ""; 
			String tel_sno = ""; 
			String hp_ddd_no = ""; 
			String hp_tel_hno = ""; 
			String hp_tel_sno = ""; 
			String email = "";  
			String idx = ""; 

			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			// 기본 조회 
			social_id_1 = (String)parser.getParameter("social_id_1").trim();
			social_id_2 = (String)parser.getParameter("social_id_2").trim();
			socid = social_id_1 + social_id_2; 
			name = (String)parser.getParameter("name","").trim(); 
			
			ddd_no = (String)parser.getParameter("ddd_no",""); 
			tel_hno = (String)parser.getParameter("tel_hno",""); 
			tel_sno = (String)parser.getParameter("tel_sno",""); 
			hp_ddd_no = (String)parser.getParameter("hp_ddd_no",""); 
			hp_tel_hno = (String)parser.getParameter("hp_tel_hno",""); 
			hp_tel_sno = (String)parser.getParameter("hp_tel_sno",""); 
			email = (String)parser.getParameter("email",""); 
			idx = (String)parser.getParameter("idx",""); 
						
			debug(">> 한신평 실명인증"); 
			// 준회원 가입시 한신평 실명인증
			NameCheck nm = new NameCheck(); 
			nm.setChkName(name);
			String rtn = nm.setJumin(socid + SITEPW);
			nm.setSiteCode(SITEID);

			if("0".equals(rtn)) {
				nm.setSiteCode(SITEID);
				nm.setTimeOut(30000);
				rtn = nm.getRtn().trim(); 
			} 
			debug(">> 한신평 실명인증 > Return = " + rtn); 
			
			String msg = "";
			String script = "";
			String nameChk = "N";
			
			if("1".equals(rtn)) { 
				// 정상응답
				msg = "실명인증에 성공했습니다."; 
				nameChk = "Y";
			} else if("2".equals(rtn)) { 
				// 본인아님
				msg = "실명인증에 실패했습니다[본인 아님]. 다시  입력해 주십시오";
			} else if("3".equals(rtn)) {
				// 자료 없음
				msg = "실명인증에 실패했습니다[자료 없음]. 다시  입력해 주십시오";
			} else if("4".equals(rtn)) {
				// 시스템장애 (크레딧뱅크 이상)
				msg = "실명인증에 실패했습니다[시스템장애 (크레딧뱅크 이상)]. 다시  입력해 주십시오";
			} else if("5".equals(rtn)) {
				// 주민번호 오류
				msg = "실명인증에 실패했습니다[주민번호 오류]. 다시  입력해 주십시오";
			} else if("50".equals(rtn)) {
				// 정보도용 차단 요청 주민번호
				msg = "실명인증에 실패했습니다[정보도용 차단 요청 주민번호]. 다시  입력해 주십시오";
			} else  {
				// System ERROR
				msg = "실명인증에 실패했습니다[System ERROR]. 다시  입력해 주십시오";
			} 
			
			script += "parent.iForm.nameChk.value='"+nameChk+"';";
			script += "parent.iForm.social_id_1.value='"+social_id_1+"';";
			script += "parent.iForm.social_id_2.value='"+social_id_2+"';";
			script += "parent.iForm.name.value='"+name+"';";
			script += "parent.iForm.ddd_no.value='"+ddd_no+"';";
			script += "parent.iForm.tel_hno.value='"+tel_hno+"';";
			script += "parent.iForm.tel_sno.value='"+tel_sno+"';";
			script += "parent.iForm.hp_ddd_no.value='"+hp_ddd_no+"';";
			script += "parent.iForm.hp_tel_hno.value='"+hp_tel_hno+"';";
			script += "parent.iForm.hp_tel_sno.value='"+hp_tel_sno+"';";
			script += "parent.iForm.email.value='"+email+"';";
			script += "parent.iForm.idx.value='"+idx+"';";
			
//			script += "parent.iForm.social_id_1.readOnly='true';";
//			script += "parent.iForm.social_id_2.readOnly='true';";
//			script += "parent.iForm.name.readOnly='true';";

			script += "parent.iForm.social_id_1_old.value='"+social_id_1+"';";
			script += "parent.iForm.social_id_2_old.value='"+social_id_2+"';";
			script += "parent.iForm.name_old.value='"+name+"';";
			
			script += "alert('"+msg+"');";


			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
