/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfFieldListActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 전국골프장 안내  리스트
*   적용범위  : Golf
*   작성일자  : 2009-06-03
************************** 수정이력 ****************************************************************
*    일자     작성자   변경사항
*  20110304  이경희   [http://www.bccard.com/-"Home > VIP서비스 > 골프 > 전국 골프장 안내 로 접속했는지 로그 출력
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
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfFieldListDaoProc;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfFieldListActn extends GolfActn{
	
	public static final String TITLE = " 전국골프장 안내 리스트";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String gf_nm = "";
		String hole_cd = "";
		String area_cd = "";
		boolean flag1 = false;
		boolean flag2 = false;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			
			paramMap.remove("s_gf_hole_cd");
			paramMap.remove("s_gf_area_cd");

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			String search_cd		= parser.getParameter("search_cd", "1"); // 검색 구분
			String gf_clss_cd		= parser.getParameter("s_gf_clss_cd", ""); // 구분
			String[] gf_hole_cd		= parser.getParameterValues("s_gf_hole_cd", "");  // 홀수
			String[] gf_area_cd		= parser.getParameterValues("s_gf_area_cd", "");  // 지역별
			String grnfee_mb		= parser.getParameter("s_grnfee_mb", ""); // 그린피 회원분류
			String grnfee_wk		= parser.getParameter("s_grnfee_wk", ""); // 그린피 주중
			String grnfee_wkend		= parser.getParameter("s_grnfee_wkend", ""); // 그린피 주말
			long grnfee_amt1		= parser.getLongParameter("s_grnfee_amt1", 0L); // 그린피 요금1
			long grnfee_amt2		= parser.getLongParameter("s_grnfee_amt2", 0L); // 그린피 요금2
			String gf_nm1		= parser.getParameter("s_gf_nm1", ""); //골프장명(상세검색)
			
			String sido		= parser.getParameter("s_sido", ""); //지역
			String gugun		= parser.getParameter("s_gugun", ""); //구군지역
			String dong		= parser.getParameter("s_dong", ""); //상세지역
			String gf_nm2		= parser.getParameter("s_gf_nm2", ""); //골프장명(지역검색)			
			
			String inBc = request.getAttribute("actnKey").toString();
			
			//[ http://www.bccard.com/->VIP서비스/골프/전국 골프장안내 ]에서 접속시
			if (inBc.substring(inBc.length()- 4, inBc.length()).equals("InBC")){	
				debug("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP서비스/골프/전국 골프장안내 '에서 접속 " );
			}			
			
			if (search_cd.equals("1")) { // 상세검색
				gf_nm = gf_nm1;
			} else if (search_cd.equals("2")) { // 지역검색
				gf_nm = gf_nm2;
			}
			
			//debug("gf_nm ===>"+ gf_nm);
			
			for (int i = 0; i < gf_hole_cd.length; i++) { 		
				if (gf_hole_cd[i] != null && gf_hole_cd[i].length() > 0) {
					hole_cd += ","+ gf_hole_cd[i];
					flag1 = true;
				}
			}
			
			for (int i = 0; i < gf_area_cd.length; i++) { 		
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					area_cd += ","+ gf_area_cd[i];
					flag2 = true;
				}
			}
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			dataSet.setString("SEARCH_CD", (GolfUtil.isNull(search_cd) ? "1" : search_cd));
			dataSet.setString("GF_CLSS_CD", gf_clss_cd);
			dataSet.setString("GRNFEE_MB", grnfee_mb);
			dataSet.setString("GRNFEE_WK", grnfee_wk);
			dataSet.setString("GRNFEE_WKEND", grnfee_wkend);
			dataSet.setLong("GRNFEE_AMT1", grnfee_amt1);
			dataSet.setLong("GRNFEE_AMT2", grnfee_amt2);
			dataSet.setString("SIDO", sido);
			dataSet.setString("GUGUN", gugun);
			dataSet.setString("DONG", dong);
			dataSet.setString("GF_NM", gf_nm);
			
			
			// 04.실제 테이블(Proc) 조회
			
			GolfFieldListDaoProc proc = (GolfFieldListDaoProc)context.getProc("GolfFieldListDaoProc");
			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			DbTaoResult golffieldListResult = (DbTaoResult) proc.execute(context, request, dataSet, gf_hole_cd, gf_area_cd);
			
			// 코드 조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopCpSel1 = coopCpSelProc.execute(context, dataSet, "0019", "Y"); //골프장구분코드
			DbTaoResult coopCpSel2 = coopCpSelProc.execute(context, dataSet, "0020", "Y"); //골프장홀수코드
			DbTaoResult coopCpSel3 = coopCpSelProc.execute(context, dataSet, "0021", "Y"); //골프장지역코드
			
			
			// 전체 0건  [ 0/0 page] 형식 가져오기
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
			
			if (golffieldListResult != null && golffieldListResult.isNext()) {
				golffieldListResult.first();
				golffieldListResult.next();
				if (golffieldListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)golffieldListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)golffieldListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
			
			if (gf_clss_cd.equals("")) paramMap.put("s_gf_clss_cd", "0001");
			
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));
			paramMap.put("resultSize", String.valueOf(golffieldListResult.size()));
			paramMap.put("gf_area_cd", (gf_area_cd.length == 1 ? gf_area_cd[0] : ""));
			
			request.setAttribute("golffieldListResult", golffieldListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			request.setAttribute("coopCpSel1", coopCpSel1);
			request.setAttribute("coopCpSel2", coopCpSel2);
			request.setAttribute("coopCpSel3", coopCpSel3);
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("HoleCd", (flag1 ? hole_cd.substring(1,hole_cd.length()) : ""));
	        request.setAttribute("AreaCd", (flag2 ? area_cd.substring(1,area_cd.length()) : ""));
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
