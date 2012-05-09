/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardListActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자     작성자   변경사항
*  20110304  이경희   [http://www.bccard.com/-"Home > VIP서비스 > 골프 > 골프 규칙&매너 로 접속했는지 로그 출력 및 초기값 설정
*  					  [http://www.bccard.com/-"Home > VIP서비스 > 골프 > 골프 용어 대사전 로 접속했는지 로그 출력 및 초기값 설정
***************************************************************************************************/
package com.bccard.golf.action.bbs;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardListDaoProc;
import com.bccard.golf.dbtao.proc.code.GolfCodeSelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfBoardListActn extends GolfActn{
	
	public static final String TITLE = "공통게시판 리스트";

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
		int intCyberMoney = 0; 
		boolean flag = true;
		
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
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //사이버머니
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
			paramMap.put("RurlPath", AppConfig.getAppProperty("URL_REAL"));
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/bbs");

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");

			String bbs		= parser.getParameter("bbs", "0035");
			String sch_field_cd		= parser.getParameter("sch_field_cd", ""); 
			String sch_clss_cd		= parser.getParameter("sch_clss_cd", ""); 
			String sch_sec_cd		= parser.getParameter("sch_sec_cd", ""); 
			String sch_hd_yn		= parser.getParameter("sch_hd_yn", ""); 
			String sreg_sdate		= parser.getParameter("sreg_sdate", "");
			String sreg_edate		= parser.getParameter("sreg_edate", "");
			String sort		= parser.getParameter("sort", "");
			
			sreg_sdate = sreg_sdate.length() == 10 ? DateUtil.format(sreg_sdate, "yyyy-MM-dd", "yyyyMMdd"): "";
			sreg_edate = sreg_edate.length() == 10 ? DateUtil.format(sreg_edate, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			String inBc = request.getAttribute("actnKey").toString();
			
			//[ http://www.bccard.com/->VIP서비스/골프/~ ]에서 접속시
			if (inBc.equals("golfRulListInBC")){
				debug("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP서비스/골프/골프 규칙&매너'에서 접속 " );				
				bbs = "0038";
			}else if (inBc.equals("golfDicListInBC")){
				debug("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP서비스/골프/골프 용어 대사전'에서 접속 " );
				bbs = "0037";
			}
			
			// 특정게시판 이용제한 체크
			/*
			if (bbs.equals("0036")){ // 골프칼럼
				if (isLogin.equals("0")) { // 모든회원 접근
					flag = false;
				}
			}
			*/
			
			//debug("bbs1 ====> "+ bbs);
			
			paramMap.put("bbs", bbs);
			paramMap.put("sch_field_cd", sch_field_cd);
			paramMap.put("sch_clss_cd", sch_clss_cd);
			paramMap.put("sch_sec_cd", sch_sec_cd);
			paramMap.put("search_word", search_word);
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("BBS", bbs);
			dataSet.setString("SCH_FIELD_CD", sch_field_cd);
			dataSet.setString("SCH_CLSS_CD", sch_clss_cd);
			dataSet.setString("SCH_SEC_CD", sch_sec_cd.toUpperCase());
			dataSet.setString("SCH_HD_YN", sch_hd_yn);
			dataSet.setString("SREG_SDATE", sreg_sdate);
			dataSet.setString("SREG_EDATE", sreg_edate);
			dataSet.setString("SORT", sort);
			
			// 이용제한 체크
			//if (flag) {
				
				// 04.실제 테이블(Proc) 조회
				GolfBoardListDaoProc proc = (GolfBoardListDaoProc)context.getProc("GolfBoardListDaoProc");
				GolfCodeSelDaoProc coodSelProc = (GolfCodeSelDaoProc)context.getProc("GolfCodeSelDaoProc");
				DbTaoResult bbsListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				DbTaoResult codeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, bbs, "Y"); //게시판 구분
				
				// 전체 0건  [ 0/0 page] 형식 가져오기
				long totalRecord = 0L;
				long currPage = 0L;
				long totalPage = 0L;
				
				if (bbsListResult != null && bbsListResult.isNext()) {
					bbsListResult.first();
					bbsListResult.next();
					if (bbsListResult.getObject("RESULT").equals("00")) {
						totalRecord = Long.parseLong((String)bbsListResult.getString("TOTAL_CNT"));
						currPage = Long.parseLong((String)bbsListResult.getString("CURR_PAGE"));
						totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
					}
				}
				
				paramMap.put("totalRecord", String.valueOf(totalRecord));
				paramMap.put("currPage", String.valueOf(currPage));
				paramMap.put("totalPage", String.valueOf(totalPage));
				paramMap.put("resultSize", String.valueOf(bbsListResult.size()));
				
				request.setAttribute("bbsListResult", bbsListResult);
				request.setAttribute("codeSelResult", codeSel);
				request.setAttribute("record_size", String.valueOf(record_size));
		        request.setAttribute("paramMap", paramMap);
		        
			//} else {
			//	subpage_key = "limitReUrl";
			//}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
