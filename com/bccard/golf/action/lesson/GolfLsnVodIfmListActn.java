/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLsnVodListActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨동영상 리스트
*   적용범위  : Golf
*   작성일자  : 2009-06-02
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

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.lesson.GolfLsnVodListDaoProc;
import com.bccard.golf.dbtao.proc.code.GolfCodeSelDaoProc;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfLsnVodIfmListActn extends GolfActn{
	
	public static final String TITLE = "레슨동영상 리스트";

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
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lesson");

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");

			String svod_clss		= parser.getParameter("svod_clss", ""); //0001:초보자레슨 0002:반복레슨 0003:포인트레슨 0004:PGA레슨 0005:이미지스윙	
			String svod_lsn_clss		= parser.getParameter("svod_lsn_clss", ""); //0001:그립 0002:어드레스	

			paramMap.put("svod_clss", svod_clss);
			paramMap.put("svod_lsn_clss", svod_lsn_clss);
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SVOD_CLSS", svod_clss);
			dataSet.setString("SVOD_LSN_CLSS", svod_lsn_clss);
			
			// 04.실제 테이블(Proc) 조회
			GolfLsnVodListDaoProc proc = (GolfLsnVodListDaoProc)context.getProc("GolfLsnVodListDaoProc");
			GolfCodeSelDaoProc coodSelProc = (GolfCodeSelDaoProc)context.getProc("GolfCodeSelDaoProc");
			DbTaoResult lsnVodListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult vodClssSel = (DbTaoResult) coodSelProc.execute(context, dataSet, "0007", "Y"); //동영상구분
			DbTaoResult vodLsnClssSel = (DbTaoResult) coodSelProc.execute(context, dataSet, "0013", "Y"); //동영상레슨분류			
			
			if (svod_clss.equals("0001")) {
				paramMap.put("MenuNm", "초보자 레슨");
			} else if  (svod_clss.equals("0002")) {
				paramMap.put("MenuNm", "반복 레슨");
			}  else if  (svod_clss.equals("0003")) {
				paramMap.put("MenuNm", "포인트 레슨");	
			}  else if  (svod_clss.equals("0004")) {
				paramMap.put("MenuNm", "PGA 레슨");	
			}  else if  (svod_clss.equals("0005")) {
				paramMap.put("MenuNm", "이미지 스윙");	
			} 
			
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
