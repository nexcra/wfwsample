/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntCouponNoSchActn
*   작성자    : (주)미디어포스 이경희
*   내용      : 이벤트라운지/골프라운지이벤트/진행중인이벤트/그린피할인쿠폰->할인쿠폰 번호 확인 검색
*   적용범위  : Golf
*   작성일자  : 2011-04-13
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
***************************************************************************************************/
package com.bccard.golf.action.event.coupon;


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
import com.bccard.golf.dbtao.proc.event.coupon.GolfEvntCouponNoSchProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntCouponNoSchActn extends GolfActn{
	
	public static final String TITLE = "할인쿠폰 번호 확인 검색";

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
		
		try { 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String jumin_no1					= parser.getParameter("jumin_no1", "");
			String jumin_no2					= parser.getParameter("jumin_no2","");
			String hp_ddd_no					= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno					= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno					= parser.getParameter("hp_tel_sno","");			
			
			debug(" / jumin_no1 : " + jumin_no1 + " / jumin_no2 : " + jumin_no2 + " / hp_ddd_no : " + hp_ddd_no
					 + " / hp_tel_hno : " + hp_tel_hno + " / hp_tel_sno : " + hp_tel_sno);
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("jumin_no", jumin_no1+jumin_no2);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);

			GolfEvntCouponNoSchProc proc = (GolfEvntCouponNoSchProc)context.getProc("GolfEvntCouponNoSchProc");
			DbTaoResult couponNoList = (DbTaoResult) proc.execute(context, request, dataSet);

			paramMap.put("juminno1", jumin_no1);
			paramMap.put("juminno2", jumin_no2);
			paramMap.put("mobile1", hp_ddd_no);
			paramMap.put("mobile2", hp_tel_hno);
			paramMap.put("mobile3", hp_tel_sno);
	        request.setAttribute("couponNoList", couponNoList);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
