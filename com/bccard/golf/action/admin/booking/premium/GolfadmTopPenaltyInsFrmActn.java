/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopPenaltyListActn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 패널티관리  > 패널티관리  등록/수정 폼
*   적용범위  : Golf
*   작성일자  : 2010-11-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;
   

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
import com.bccard.waf.common.DateUtil;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/******************************************************************************
* Topn
* @author	 
* @version	1.0 
******************************************************************************/
public class GolfadmTopPenaltyInsFrmActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 부킹 > 패널티관리  > 패널티관리 수정폼 ";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		debug("***********************************************************************************");
		debug(" Action  GolfadmTopPenaltyInsFrmActn.java 수정 폼 execute");
		debug("***********************************************************************************");
		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		//TaoConnection con = null;
		String layout = super.getActionParam(context, "layout");
		String actnKey = getActionKey(context);

		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			
			debug("action GolfadmTopPenaltyInsFrmActn.java try");
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			String roundDateFmt	= parser.getParameter("roundDateFmt", "");
			String roundDate	= parser.getParameter("roundDate", "");
			String seqNo		= parser.getParameter("seqNo", "");
			String pointDetlCd	= parser.getParameter("pointDetlCd", "");
			String name		= parser.getParameter("name", "");
			String memId		= parser.getParameter("memId", "");
			String pointMemo	= parser.getParameter("pointMemo", "");
			String penaltyApplyClss	= parser.getParameter("penaltyApplyClss", "");
			String penaltyResnCd	= parser.getParameter("penaltyResnCd", "");
			String key		= parser.getParameter("key", "");
                        long affiGreenSeqNo     = parser.getLongParameter("affiGreenSeqNo",0L) ;
			String greenNM  	= parser.getParameter("greenNM", "");
			String bbs ="0035";
			String greenNm="";
      
	
			Map paramMap = parser.getParameterMap();
			
			paramMap.put("title", TITLE);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setLong("affiGreenSeqNo", affiGreenSeqNo);

			if("upd".equals(key)) {
				String regionClss	= parser.getParameter("s_regionClss");
				String greenNo		= parser.getParameter("s_greenNo");
				String setFromFmt	= parser.getParameter("setDateFromFmt");
				String setFrom		= parser.getParameter("setDateFrom");
				String setToFmt		= parser.getParameter("setDateToFmt");
				String setTo		= parser.getParameter("setDateTo");
                                affiGreenSeqNo          = parser.getLongParameter("affiGreenSeqNo",0L) ;
				greenNM		= parser.getParameter("greenNM");

				paramMap.put("setFromFmt", setFromFmt);
				paramMap.put("setFrom", setFrom);
				paramMap.put("setToFmt", setToFmt);
				paramMap.put("setTo", setTo);
				//paramMap.put("regionClss", regionClss);
				//paramMap.put("greenNo", greenNo);
				paramMap.put("affiGreenSeqNo",String.valueOf(affiGreenSeqNo));
				paramMap.put("greenNm",greenNm);
				paramMap.put("greenNM",greenNM);
			}
              

					paramMap.put("roundDateFmt", roundDateFmt);
					paramMap.put("roundDate", roundDate);
					//paramMap.put("seqNo", seqNo);
					paramMap.put("affiGreenSeqNo", String.valueOf(affiGreenSeqNo));
					paramMap.put("greenNM", greenNM);
		                        debug("action GolfadmTopPenaltyInsFrmActn.java key greenNM ["+greenNM+"]"); 
					paramMap.put("pointDetlCd", pointDetlCd);
					paramMap.put("name", name);
					paramMap.put("memId", memId);
					paramMap.put("pointMemo", pointMemo);
					paramMap.put("penaltyApplyClss", penaltyApplyClss);
					paramMap.put("penaltyResnCd", penaltyResnCd);
					paramMap.put("key", key);
					debug("action GolfadmTopPenaltyInsFrmActn.java key "+key);
					paramMap.put("greenNm",greenNm);
					debug("action GolfadmTopPenaltyInsFrmActn.java key greenNm ["+greenNm+"]"); 
					
					
					
					// 05. 골프장 리스트 (Sel_Proc) 조회
					GolfadmTopCodeSelDaoProc coodSelProc = (GolfadmTopCodeSelDaoProc)context.getProc("GolfadmTopCodeSelDaoProc");
					DbTaoResult codeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, bbs); //게시판 구분
					request.setAttribute("codeSelResult", codeSel);
					

					request.setAttribute("paramMap",paramMap);

		} catch(BaseException be) {
			throw be;
		} catch(Throwable t) {
			MsgEtt ett = null;
			if ( t instanceof MsgHandler ) {
				ett = ((MsgHandler)t).getMsgEtt();
				ett.setTitle(TITLE);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,t.getMessage());
			}
			throw new GolfException(ett,t);
		} finally {
			try {  } catch(Throwable ignore) {}
		}
		return getActionResponse(context, subpage_key); // response key
	}

}
