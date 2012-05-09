/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPreTimeListDaoProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 관리자 프리미엄 티타임 리스트 처리
*   적용범위  : golf
*   작성일자  : 2010-12-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.action.admin.booking.premium;

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
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopBkngStatisDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


public class GolfadmTopBkngApplyStatisActn extends GolfActn{
 

	public static final String TITLE = "부킹신청대비결과";
	
	/***************************************************************************************
	 * 비씨탑포인트 관리자화면 
	 * @param context  WaContext 객체. 
	 * @param request  HttpServletRequest 객체. 
	 * @param response  HttpServletResponse 객체. 
	 * @return ActionResponse Action 처리후 화면에 디스플레이할 정보. 
	 ***************************************************************************************/	 
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
	
		String subpage_key = "default"; 
		  
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		  
		try {
			// 01.세션정보체크
			   
			// 02.입력값 조회  
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request 값 저장
			String mode = parser.getParameter("mode", "INIT");
			String diff = parser.getParameter("diff", "0");
			String yyyy = parser.getParameter("yyyy");
			String from = parser.getParameter("from");
			String to   = parser.getParameter("to");
			String objClss = parser.getParameter("bkngObjClss", "0");
			String repMbNo = parser.getParameter("repMbNo", "00");
			String bkngObjClssNm = parser.getParameter("bkngObjClssNm" );
			String repMbNoNm = parser.getParameter("repMbNoNm");
			
			String memberClss = parser.getParameter("parm1");
			String bkngStat   = parser.getParameter("parm2");
			   
			paramMap.put("diff", diff);
			paramMap.put("yyyy", yyyy);
			paramMap.put("from", from);
			paramMap.put("to", to);
			paramMap.put("bkngObjClss", objClss);
			paramMap.put("repMbNo", repMbNo);
			paramMap.put("bkngObjClssNm", bkngObjClssNm);
			paramMap.put("repMbNoNm", repMbNoNm);
			paramMap.put("memberClss", memberClss);
			paramMap.put("bkngStat", bkngStat);
			
			debug (" test----------------diff[" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"],objClss[" + objClss +"],repMbNo[" + repMbNo +"]");
			debug (" test----------------bkngObjClssNm[" + bkngObjClssNm +"],repMbNoNm[" + repMbNoNm +"],memberClss[" + memberClss +"],bkngStat[" + bkngStat +"]");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);   
			debug (" @@@@@@@@@@====01 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");
//			if (mode.equals("EXCELDETAIL")){
//				dataSet.setString("memberClss", memberClss);
//				dataSet.setString("bkngStat", bkngStat);	  
//			}else {
//				dataSet.setString("diff", diff);
//				dataSet.setString("yyyy", yyyy);
//				dataSet.setString("from", from);
//				dataSet.setString("to", to);
//			}
//			
//			dataSet.setString("bkngObjClss", objClss);     
//			dataSet.setString("repMbNo", repMbNo);
			
			
			dataSet.setString("memberClss", memberClss);
			dataSet.setString("bkngStat", bkngStat);	  
		
			dataSet.setString("diff", diff);
			dataSet.setString("yyyy", yyyy);
			dataSet.setString("from", from);
			dataSet.setString("to", to);
			
			
			dataSet.setString("bkngObjClss", objClss);     
			dataSet.setString("repMbNo", repMbNo);
			debug (" @@@@@@@@@@====02 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");
			
			GolfadmTopBkngStatisDaoProc instance = GolfadmTopBkngStatisDaoProc.getInstance();
			   
			DbTaoResult listResult = null;
			DbTaoResult listResult2 = null;
			debug (" @@@@@@@@@@====03 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");
			debug (" test----------------mode : " + mode);
			   
			if (!"INIT".equals(mode)) {
			    
				// 04.실제 테이블(Proc) 조회 - 리스트
				if (mode.equals("EXCELDETAIL")){
					debug (" @@@@@@@@@@====1 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");	
					listResult = instance.excelDetail(context, request, dataSet);
				}else {					
				    listResult = instance.execute(context, request, dataSet);
				}
				
				request.setAttribute("BkngApplyStatis", listResult);
			    
			}
				
			//회원사 조회
			listResult2 = instance.getGreenList(context, request, dataSet);
			
			request.setAttribute("TitimeGreenList", listResult2);
			request.setAttribute("paramMap", paramMap);
			   
			if (mode.equals("EXCEL")) { subpage_key = "excel"; }
			if (mode.equals("EXCELDETAIL")) { subpage_key = "excelDetail"; }
			if (mode.equals("PRINT")) { subpage_key = "print"; } 
		         
		} catch(Throwable t) {
			debug(TITLE, t);
			t.printStackTrace(); 
		    throw new GolfException(TITLE, t);
		} 
		  
		return super.getActionResponse(context, subpage_key);

	
	} 
  

}