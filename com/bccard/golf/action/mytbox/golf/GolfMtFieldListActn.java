/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtFieldListActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 마이티박스(골프장정보)
*   적용범위  : Golf
*   작성일자  : 2009-06-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.golf;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.mytbox.golf.GolfMtFieldListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfMtFieldListActn extends GolfActn{
	
	public static final String TITLE = "마이티박스(골프장정보)";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); //이름
				memClss		= (String)usrEntity.getMemberClss(); //등급번호
				userId		= (String)usrEntity.getAccount(); //아이디
				juminno 	= (String)usrEntity.getSocid(); //주민번호
				memGrade 	= (String)usrEntity.getMemGrade(); //등급
				intMemGrade	= (int)usrEntity.getIntMemGrade(); //등급번호
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			
			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			dataSet.setString("ADMIN_NO", userId);
			
			
			// 04.실제 테이블(Proc) 조회
			
			GolfMtFieldListDaoProc proc = (GolfMtFieldListDaoProc)context.getProc("GolfMtFieldListDaoProc");
			
			DbTaoResult golfmtfieldListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			
			// 전체 0건  [ 0/0 page] 형식 가져오기
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
			
			if (golfmtfieldListResult != null && golfmtfieldListResult.isNext()) {
				golfmtfieldListResult.first();
				golfmtfieldListResult.next();
				if (golfmtfieldListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)golfmtfieldListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)golfmtfieldListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
			
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));
			paramMap.put("resultSize", String.valueOf(golfmtfieldListResult.size()));
			
			request.setAttribute("golfmtfieldListResult", golfmtfieldListResult);
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
