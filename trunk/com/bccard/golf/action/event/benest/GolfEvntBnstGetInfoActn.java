/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBnstBaseFormActn
*   작성자    : 이정규
*   내용      : 이벤트 라운지 > 골프 라운지 이벤트 > 진행중인 이벤트 > 참가신청
*   적용범위  : Golf
*   작성일자  : 2010-10-05
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100524	임은혜	6월 이벤트
* golfloung		20110323	이경희 	보이스캐디쇼핑 
***************************************************************************************************/
package com.bccard.golf.action.event.benest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntMngBaseDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntBnstGetInfoActn extends GolfActn{
	
	public static final String TITLE = "참가신청 폼 >  주민번호로 나머지 정보 세팅";

	/***************************************************************************************
	* 골프 사용자화면 
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		// 리턴 변수
		String script = "";
		String socid ="";
		String gubun ="";
		
		try { 

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			socid = parser.getParameter("JUMIN_NO");
			gubun = parser.getParameter("gubun");
			
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE);			
			
			// param가져오기 SEQ_NO
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("gubun", gubun);// shop : 보이스캐디
			
			GolfEvntMngBaseDaoProc proc = (GolfEvntMngBaseDaoProc)context.getProc("GolfEvntMngBaseDaoProc");
			
			// 04. 신청폼 상세보기
			
			dataSet.setString("socid", socid);
			DbTaoResult appResult = null ;
			
			
			if (gubun != null && gubun != ""){
				
				if (gubun.equals("shop")){//보이스캐디 쇼핑				
					appResult = (DbTaoResult) proc.getShopUserInfo(context, request, dataSet);
					subpage_key = "ifrShop";
				}
				
			}else{//월례회에서 사용				
				appResult = (DbTaoResult) proc.getUserInfo(context, request, dataSet);
			}
			
			paramMap.put("socid", socid);	
			paramMap.put("gubun", gubun);
			request.setAttribute("appResult", appResult);
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("socid", socid);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
