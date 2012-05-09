/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfLsnPreVodUpdActn
*   작성자	: (주)미디어포스 
*   내용		: 사용자 > 레슨 > 프리미엄 동영상 > 조회수 업데이트
*   적용범위	: Golf
*   작성일자	: 2009-12-21
************************** 수정이력 ****************************************************************
*    일자     작성자   변경사항
*  20110304  이경희   [http://www.bccard.com/->Home > VIP서비스 > 골프 > 골프 VIP동영상] -조회수 업데이트 로 접속했는지 로그 출력
*  20110512  이경희   [http://golfloung.familykorail.com/-> Home > 교양마당 > 골프레슨 > 동영상레슨] - 조회수 업데이트 로 접속했는지 로그 출력
***************************************************************************************************/
package com.bccard.golf.action.lesson;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.lesson.GolfLsnVodListDaoProc;


/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfLsnPreVodUpdActn extends GolfActn{ 
	
	public static final String TITLE = "사용자 > 레슨 > 프리미엄 동영상 > 조회수 업데이트";

	/***************************************************************************************
	* 골프 관리자화면
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

			// Request 값 저장
			String seq_no = parser.getParameter("SEQ_NO", "");
			debug("## GolfLsnPreVodUpdActn | seq_no : " + seq_no + "\n");     
			
			String in = request.getAttribute("actnKey").toString();
			
			//[ http://www.bccard.com/->VIP서비스/골프/~ ]에서 접속시
			if (in.equals("golfVodHitCntInBC")){	
				info("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP서비스/골프/골프 VIP동영상'에서 접속 (조회수 업데이트)" );
			}
			
			//코레일 [ http://golfloung.familykorail.com]에서 접속시
			if (in.equals("golfVodHitCntInKorail")){	
				info("## "+this.getClass().getName()+" | 'http://golfloung.familykorail.com -> 코레일'에서 접속 (조회수 업데이트)" );
			}
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);

			// 실제 테이블(Proc) 조회
			GolfLsnVodListDaoProc proc = (GolfLsnVodListDaoProc)context.getProc("GolfLsnVodListDaoProc");
			int result = proc.updateInqrNum(context, request, dataSet);	

	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
