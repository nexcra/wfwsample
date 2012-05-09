/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntKvpUpdActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > Kvp > 정보 수정
*   적용범위  : golf
*   작성일자  : 2010-03-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.kvp;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.kvp.GolfAdmEvntKvpUpdDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfAdmEvntKvpUpdActn extends GolfActn{

	public static final String TITLE = "관리자 > 이벤트 > Kvp > 정보 수정";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 02.입력값 조회한다.
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 수정사항 변수
			String upd_type			= parser.getParameter("upd_type", "");
			String aplc_seq_no		= parser.getParameter("aplc_seq_no", "");
			String pgrs_yn			= parser.getParameter("pgrs_yn", "");
			String pay_no			= parser.getParameter("pay_no", "");
			String jumin_no			= parser.getParameter("jumin_no", "");
			String cslt_yn			= parser.getParameter("cslt_yn", "");
			String pay_box			= parser.getParameter("pay_box", "");
			String cdhd_id			= parser.getParameter("cdhd_id", "");
			int cmmCode				= Integer.parseInt(parser.getParameter("CMMCODE", ""));
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			dataSet.setString("pgrs_yn", pgrs_yn);
			dataSet.setString("pay_no", pay_no);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("cslt_yn", cslt_yn);
			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setInt("cmmCode", cmmCode);

			// Proc 파일 정의
			GolfAdmEvntKvpUpdDaoProc proc = (GolfAdmEvntKvpUpdDaoProc)context.getProc("GolfAdmEvntKvpUpdDaoProc");

			// 리턴변수
			int editResult = 0; 
			String script = "";
			String upd_type_str = "";
			String resultMsg = "";
			
			
			if(upd_type.equals("upd")){		// 등급수정 
				editResult = proc.execute(context, request, dataSet);	
				upd_type_str = "진행여부 수정";
				script = "parent.location.href='admEvntKvpList.do';";
			}else if(upd_type.equals("cancel")){
				editResult = 1;
				resultMsg = proc.execute_cancel(context, request, dataSet);	
				upd_type_str = "승인취소";
				resultMsg = upd_type_str+" "+resultMsg;
				script = "parent.location.reload();";
			}
			
			
			if (editResult > 0) {
				if(resultMsg.equals("")){
					resultMsg = upd_type_str+"이(가) 정상적으로 처리 되었습니다.";
				}
	        } else {
				resultMsg = upd_type_str+"이(가) 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.";		
	        }


			debug("upd_type : " + upd_type + " / aplc_seq_no : " + aplc_seq_no + " / pgrs_yn : " + pgrs_yn + " / editResult : " + editResult 
					+ " / script : " + script + " / resultMsg : " + resultMsg);
			 

			// 05. Return 값 세팅
			request.setAttribute("script", script);
			request.setAttribute("resultMsg", resultMsg);  
			request.setAttribute("returnUrl", "admEvntKvpList.do");
			
			paramMap.remove("pay_box"); 
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
