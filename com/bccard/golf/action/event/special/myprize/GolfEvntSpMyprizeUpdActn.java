/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpMyprizeUpdActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한 레슨이벤트 > 나의당첨내역 > 결재
*   적용범위	: golf
*   작성일자	: 2009-07-07
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.special.myprize;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/ 
public class GolfEvntSpMyprizeUpdActn extends GolfActn{
	
	public static final String TITLE = "이벤트라운지 > 특별한 레슨이벤트 > 나의당첨내역 > 결재";
 
	/***************************************************************************************
	* 비씨탑포인트 관리자화면
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
			// 01.세션정보체크
			
			 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			String evnt_clss 	= "0003";
			String golf_svc_aplc_clss 	= "0005";
			String p_idx 		= parser.getParameter("p_idx","");
			String mode 		= parser.getParameter("mode","");
			String userId 		= parser.getParameter("userId","");
			String sex_clss 	= parser.getParameter("sex_clss","");
			String hp_ddd_no 	= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno 	= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno 	= parser.getParameter("hp_tel_sno","");
			String email 		= parser.getParameter("email","");
			String evnt_nm 		= parser.getParameter("evnt_nm","");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("p_idx",    	p_idx);
			dataSet.setString("mode",		mode);
			dataSet.setString("userId",		userId);
			dataSet.setString("sex_clss",	sex_clss);
			dataSet.setString("hp_ddd_no",	hp_ddd_no);
			dataSet.setString("hp_tel_hno",	hp_tel_hno);
			dataSet.setString("hp_tel_sno",	hp_tel_sno);
			dataSet.setString("email",		email);
			dataSet.setString("golf_svc_aplc_clss",golf_svc_aplc_clss);
			
			/*
			// 04.실제 테이블(Proc) 조회
			GolfEvntSpMyprizeUpdDaoProc proc = (GolfEvntSpMyprizeUpdDaoProc)context.getProc("GolfEvntSpMyprizeUpdDaoProc");
			DbTaoResult boardResult = (DbTaoResult)proc.execute(context, request,dataSet);
			request.setAttribute("boardResult", boardResult);
		 	*/
			
			// 05.모든 파라미터값을 맵에 담아 반환한다.	
			paramMap.put("p_idx", p_idx);
			paramMap.put("mode", mode);	
			paramMap.put("evnt_nm", evnt_nm);		
	        request.setAttribute("paramMap", paramMap); 	
			 
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
