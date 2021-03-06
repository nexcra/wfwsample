/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardNoticeViewActn
*   작성자    : 미디어포스 권영만
*   내용      : 탑골프카드 공지사항 상세보기
*   적용범위  : Golf
*   작성일자  : 2010-10-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardComtListDaoProc;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardNoticeViewActn extends GolfActn{
	
	public static final String TITLE = "탑골프카드 공지사항 상세보기";

	/***************************************************************************************
	* Golf 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
				
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String actnKey = super.getActionKey(context);
		request.setAttribute("layout", layout);
		try {
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			HashMap resultMap = new HashMap();
			paramMap.put("title", TITLE);

			// Request 값 저장
			String seq_no			= parser.getParameter("p_idx", "75"); 
			String bbs				= parser.getParameter("bbs", "0062");
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String sch_field_cd		= parser.getParameter("sch_field_cd", ""); 
			String sch_clss_cd		= parser.getParameter("sch_clss_cd", ""); 
			String sch_sec_cd		= parser.getParameter("sch_sec_cd", ""); 
			String sch_hd_yn		= parser.getParameter("sch_hd_yn", ""); 	
			String reply_clss		= parser.getParameter("reply_clss", "0001");	
			String comt_papeing		= parser.getParameter("comt_papeing", "Y");	
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("BBS", bbs);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SCH_FIELD_CD", sch_field_cd);
			dataSet.setString("SCH_CLSS_CD", sch_clss_cd);
			dataSet.setString("SCH_SEC_CD", sch_sec_cd);
			dataSet.setString("SCH_HD_YN", sch_hd_yn);
			dataSet.setString("REPLY_CLSS", reply_clss);
			dataSet.setString("actnKey", actnKey);
			
			// 04.실제 테이블(Proc) 조회
			GolfBoardInqDaoProc proc = (GolfBoardInqDaoProc)context.getProc("GolfBoardInqDaoProc");
			GolfBoardComtListDaoProc proc2 = (GolfBoardComtListDaoProc)context.getProc("GolfBoardComtListDaoProc");
			resultMap = proc.execute(context, resultMap, dataSet);
			
			DbTaoResult bbsComtListResult = null;

			if (comt_papeing.equals("Y")) {
				bbsComtListResult = (DbTaoResult) proc2.execute(context, request, dataSet);			
			} else {
				bbsComtListResult = (DbTaoResult) proc2.execute_noPageing(context, request, dataSet);
			}

			// 데이터가 있으면 이전/다음/조회수 업데이트 실행
			DbTaoResult preNextInfoResult = null;
			if (resultMap.get("RESULT").equals("00")) {
				
				// 이전글 다음글 번호 가져오기
				preNextInfoResult = proc.getPreNextInfo(context, dataSet);
				
				// 조회수 업데이트
				int readCntUpdResult = proc.readCntUpd(context, dataSet);
				if (readCntUpdResult > 0) {
					Integer readCnt = new Integer(resultMap.get("INOR_NUM").toString());
					resultMap.put("INOR_NUM",  String.valueOf(readCnt.intValue() + 1));
				}
			}
			
			paramMap.put("bbsReListSize", String.valueOf(bbsComtListResult.size()));
			
			// 05. Return 값 세팅
			request.setAttribute("bbsInqResult", resultMap);
			request.setAttribute("bbsComtListResult", bbsComtListResult);
			request.setAttribute("preNextInfoResult", preNextInfoResult);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
