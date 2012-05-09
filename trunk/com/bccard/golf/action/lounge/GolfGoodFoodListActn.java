/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfGoodFoodListActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 골프장 주변맛집 리스트
*   적용범위  : Golf
*   작성일자  : 2009-06-09
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.lounge;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.category.GolfCateSelInqDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfGoodFoodListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfGoodFoodListActn extends GolfActn{
	
	public static final String TITLE = " 골프장 주변맛집 리스트";

	/***************************************************************************************
	* 골프 사용자화면
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
		String permission = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
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
			
			String sido	= parser.getParameter("s_sido", "");		// 지역
			String gugun	= parser.getParameter("s_gugun", "");		// 구군지역
			String dong	= parser.getParameter("s_dong", "");		// 상세지역
			String fd1_lev_cd	= parser.getParameter("s_fd1_lev_cd", "");		// 음식1차 분류
			String fd2_lev_cd	= parser.getParameter("s_fd2_lev_cd", "");		// 음식2차 분류
			String fd3_lev_cd	= parser.getParameter("s_fd3_lev_cd", "");		// 음식3차 분류
			String gf_area_cd		= parser.getParameter("s_gf_area_cd", "");  // 지역별
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			dataSet.setString("SIDO", sido);
			dataSet.setString("GUGUN", gugun);
			dataSet.setString("DONG", dong);
			dataSet.setString("FD1_LEV_CD", fd1_lev_cd);
			dataSet.setString("FD2_LEV_CD", fd2_lev_cd);
			dataSet.setString("FD3_LEV_CD", fd3_lev_cd);
			dataSet.setString("GF_AREA_CD", gf_area_cd);
			
			dataSet.setString("PT_CATEGORY_ID", "0000");
			dataSet.setString("CTG_CLSS", "10");
			
			// 접근권한 조회	
			String permissionColum = "ETHS_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			
			
			
			// 이용제한 체크
			//if (isLogin.equals("1") && intMemGrade < 4) { // 우량회원이상 접근
				
				// 04.실제 테이블(Proc) 조회
				GolfGoodFoodListDaoProc proc = (GolfGoodFoodListDaoProc)context.getProc("GolfGoodFoodListDaoProc");
				GolfCateSelInqDaoProc coopCtSelProc = (GolfCateSelInqDaoProc)context.getProc("GolfCateSelInqDaoProc");
				
				DbTaoResult goodfoodListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				
				// 코드 조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				DbTaoResult coopCtSel = coopCtSelProc.execute(context, request, dataSet); //음식분류코드
				
				
				// 전체 0건  [ 0/0 page] 형식 가져오기
				long totalRecord = 0L;
				long currPage = 0L;
				long totalPage = 0L;
				
				if (goodfoodListResult != null && goodfoodListResult.isNext()) {
					goodfoodListResult.first();
					goodfoodListResult.next();
					if (goodfoodListResult.getObject("RESULT").equals("00")) {
						totalRecord = Long.parseLong((String)goodfoodListResult.getString("TOTAL_CNT"));
						currPage = Long.parseLong((String)goodfoodListResult.getString("CURR_PAGE"));
						totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
					}
				}
				
				
				paramMap.put("totalRecord", String.valueOf(totalRecord));
				paramMap.put("currPage", String.valueOf(currPage));
				paramMap.put("totalPage", String.valueOf(totalPage));
				paramMap.put("resultSize", String.valueOf(goodfoodListResult.size()));
				
				request.setAttribute("goodfoodListResult", goodfoodListResult);
				request.setAttribute("record_size", String.valueOf(record_size));
				request.setAttribute("coopCtSel", coopCtSel);
				request.setAttribute("paramMap", paramMap);
				
			//} else {
				//subpage_key = "limitReUrl";
			//}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
