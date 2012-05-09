/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfLessonUccInqActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 레슨 > 친절한 ucc 레슨 목록
*   적용범위	: golf
*   작성일자	: 2009-07-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.lesson;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.lesson.GolfLessonUccInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/  
public class GolfLessonUccInqActn extends GolfActn{
	
	public static final String TITLE = "레슨 > 친절한 ucc 레슨 목록";
 
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
			
			String bbrd_clss 	= "0022";
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no",    	page_no);
			dataSet.setString("bbrd_clss",    bbrd_clss);
		
			// 04.실제 테이블(Proc) 조회
			GolfLessonUccInqDaoProc proc = (GolfLessonUccInqDaoProc)context.getProc("GolfLessonUccInqDaoProc");
			DbTaoResult lessonUccInq = (DbTaoResult)proc.execute(context, request,dataSet);
			request.setAttribute("lessonUccInq", lessonUccInq);
		
			
			// 05.총게시물 수 조회
			String ttCnt = proc.getTtCount(context,dataSet);
			
			// 06.총 페이지 계산
			int intTtCnt = Integer.parseInt(ttCnt);
			int ttPag = 0;
			if(intTtCnt > 0){
				ttPag = intTtCnt/5;
			}else{
				ttPag = 1;
			}
			
			if(ttPag == 0) ttPag = 1;
			
			// 05.모든 파라미터값을 맵에 담아 반환한다.		
			paramMap.put("page_no", Long.toString(page_no));
			paramMap.put("ttCnt", ttCnt);
			paramMap.put("ttPag", Integer.toString(ttPag));
			paramMap.put("img_path", AppConfig.getAppProperty("IMG_URL_REAL")+"/lesson");
	        request.setAttribute("paramMap", paramMap); 	
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
