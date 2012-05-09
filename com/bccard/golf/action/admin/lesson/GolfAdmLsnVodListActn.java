/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLsnVodListActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨동영상 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

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
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLsnVodListDaoProc;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmLsnVodListActn extends GolfActn{
	
	public static final String TITLE = "관리자 레슨동영상 리스트";

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
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// 코드값 지정 
			String svod_clss_code = "";	//일반동영상-0007, 프리미엄동영상-0045 
			String svod_lsn_code = "";	//일반동영상레슨-0013, 프리미엄동영상레슨-0046			
			
			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String is_premi			= parser.getParameter("is_p", "N");				//Y:프리미엄 동영상, N:일반 동영상
			
			String svod_clss		= parser.getParameter("svod_clss", ""); 		//0001:초보자레슨 0002:반복레슨 0003:포인트레슨 0004:PGA레슨 0005:이미지스윙 	
			String svod_lsn_clss	= parser.getParameter("svod_lsn_clss", ""); 	//0001:그립 0002:어드레스
			
 			// 관리자 타이틀 지정(일반 동영상, 프리미엄 동영상)
			if(is_premi.equals("Y")){
				paramMap.put("str_title", "프리미엄 동영상 리스트");		
				svod_clss_code = "0045";
				svod_lsn_code = "0046";				
			}else{
				paramMap.put("str_title", "레슨 동영상 리스트");
				svod_clss_code = "0007";
				svod_lsn_code = "0013";				
			}
			
			paramMap.put("svod_clss", svod_clss);
			paramMap.put("svod_lsn_clss", svod_lsn_clss);
			paramMap.put("is_p", is_premi);			
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SVOD_CLSS", svod_clss);
			dataSet.setString("SVOD_LSN_CLSS", svod_lsn_clss);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmLsnVodListDaoProc proc = (GolfAdmLsnVodListDaoProc)context.getProc("GolfAdmLsnVodListDaoProc");
			GolfAdmCodeSelDaoProc coodSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			DbTaoResult lsnVodListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult vodClssSel = (DbTaoResult) coodSelProc.execute(context, dataSet, svod_clss_code, "Y"); //동영상구분
			DbTaoResult vodLsnClssSel = (DbTaoResult) coodSelProc.execute(context, dataSet, svod_lsn_code, "Y"); //동영상레슨분류
			
			paramMap.put("resultSize", String.valueOf(lsnVodListResult.size()));
			
			request.setAttribute("lsnVodListResult", lsnVodListResult);
			request.setAttribute("vodClssSel", vodClssSel);
			request.setAttribute("vodLsnClssSel", vodLsnClssSel);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
