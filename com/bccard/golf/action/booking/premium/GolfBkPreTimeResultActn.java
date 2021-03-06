/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeResultActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 신청 내용 확인
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.*;

/******************************************************************************
* Topn
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfBkPreTimeResultActn extends GolfActn{
	
	public static final String TITLE = "부킹 신청 내용 확인"; 

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
			// 01.세션정보체크, 변수 지정
			int pmi_bokg = 0;
			int yoil_num = 0;
			String bkps_date_real = "";
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String seq_NO			= parser.getParameter("idx", "");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.) 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_NO);
			
			// 04.실제 테이블(Proc) 조회
			GolfBkPreTimeResultDaoProc proc = (GolfBkPreTimeResultDaoProc)context.getProc("GolfBkPreTimeResultDaoProc");
			DbTaoResult timeView = proc.execute(context, dataSet);
			if(timeView != null){
				timeView.next();
				yoil_num = timeView.getInt("BKPS_YOIL_NUM");
				bkps_date_real = timeView.getString("BK_DATE_REAL");
				dataSet.setString("bkps_date_real", bkps_date_real);
				dataSet.setInt("yoil_num", yoil_num);
				debug("bkps_date_real : " + bkps_date_real);

				// 05. 부킹 횟수 조회 - 사이버 머니 사용내역은 회수에서 차감된다.
				GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
				if(yoil_num==1 || yoil_num==7){
					// 주말
					DbTaoResult pmiWkeView = proc_times.getPmiWkeBenefitWeek(context, dataSet, request);
					pmiWkeView.next();
					pmi_bokg = pmiWkeView.getInt("PMI_WKE_BOKG");
				}else{
					// 주중
					DbTaoResult pmiWkdView = proc_times.getPmiWkdBenefitWeek(context, dataSet, request);
					pmiWkdView.next();
					pmi_bokg = pmiWkdView.getInt("PMI_WKD_BOKG");
				}
			}
			paramMap.put("pmi_bokg", pmi_bokg+"");

			// 05. Return 값 세팅
			request.setAttribute("SEQ_NO", seq_NO);	
			request.setAttribute("TimeView", timeView);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
