/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopPenaltyUpdInsActn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 패널티관리  > 패널티관리 수정 입력 액션 
*   적용범위  : Golf
*   작성일자  : 2010-12-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import com.bccard.waf.core.RequestParser;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Map;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.DateUtil;
 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;


/** ****************************************************************************
 * 포인트관리 수정입력 수행 액션.
 * @author  김상범
 * @version 2010.12.08
 **************************************************************************** */
public class GolfadmTopPenaltyUpdInsActn extends AbstractAction {

	public static final String TITLE	= "페널트관리 수정";
		public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {

				TaoConnection con = null;
				debug("***********************************************************************************");
				debug(" Action  GolfadmTopPenaltyUpdInsActn.java 수정 액션 execute");
				debug("***********************************************************************************");

		try {
			// 01.정보가져오기 
				RequestParser parser = context.getRequestParser("default",request,response);
	
				String memId		= parser.getParameter("memId","");			//회원번호
				String seqNo		= parser.getParameter("seqNo","");			//일련번호
				String pointClss	= parser.getParameter("pointClss","60");		//패널티구분
				String pointDetlCd	= parser.getParameter("pointDetlCd","");		//패널티종류
				String pointMemo	= parser.getParameter("pointMemo","");			//위약내용
				long affiGreenSeqNo     = parser.getLongParameter("affiGreenSeqNo",0L);         //골프장번호
				String greenNM  	= parser.getParameter("greenNM","");			//위약일자
				debug(" action try GolfadmTopPenaltyUpdInsActn.java ["+greenNM);
				String roundDate	= parser.getParameter("roundDate","");			//위약일자
				String breachCont	= parser.getParameter("breachCont","");			//적용내용
				String penaltyApplyClss	= parser.getParameter("penaltyApplyClss","");	//페널티적용구분
				String setFrom		= parser.getParameter("setFrom","");			//적용시작일자
				String setTo		= parser.getParameter("setTo","");			//적용종료일자
				String penaltyResnCd	= parser.getParameter("penaltyResnCd","");		//페널티사유코드
				String regIp		= request.getRemoteAddr();				//부킹처리IP	
				String bbs              ="0035"	;
	                      
				// 02.세션 정보가져오기 

				HttpSession session = request.getSession(true);
	
				String regAdminNo = null;
				String regAdminNm = null;
				GolfAdminEtt sessionEtt = (GolfAdminEtt)session.getAttribute("SESSION_ADMIN"); 
				if(sessionEtt != null) {
					regAdminNo    = (String)sessionEtt.getMemNm();  //부킹처리 관리자명
					regAdminNo    = (String)sessionEtt.getMemNo();//부킹처리 관리자번호
				}
				con = context.getTaoConnection("dbtao", null);
	
				// DataSet 정보 담기 
				TaoDataSet dataSet = new DbTaoDataSet(TITLE);			
	
				dataSet.setString("memId", memId);                        // 회원번호 
				dataSet.setString("seqNo", seqNo);                        // 일련번호 
				dataSet.setString("pointClss", pointClss);                // 패널티구분
				dataSet.setString("pointDetlCd", pointDetlCd);            // 패널티종류
				dataSet.setString("pointMemo", pointMemo);                // 위약내용
				dataSet.setLong("affiGreenSeqNo", affiGreenSeqNo);	// 골프장		
				dataSet.setString("greenNM", greenNM);                  // 골프장명  
				debug(" action dataSet GolfadmTopPenaltyUpdInsActn.java ["+greenNM);
				dataSet.setString("roundDate", roundDate);              // 위약일자
				dataSet.setString("breachCont", breachCont);            // 적용내용
				dataSet.setString("penaltyApplyClss", penaltyApplyClss);// 패널티적용구분
				dataSet.setString("setFrom", setFrom);                  // 패널티 적용시작일자
				dataSet.setString("setTo", setTo);                      // 태널티 적용종료일자
				dataSet.setString("penaltyResnCd", penaltyResnCd);      // 패널티사유 
				dataSet.setString("regIp", regIp);                      // 적용한 IP 
				dataSet.setString("regAdminNo", String.valueOf(regAdminNo)); // 적용자 사번 
				dataSet.setString("regAdminNm", regAdminNm);                 // 적용자 성명
	
				GolfadmTopPenaltyUpdInsProc proc = (GolfadmTopPenaltyUpdInsProc)context.getProc("GolfadmTopPenaltyUpdInsProc");		 // 강선영 추가
				DbTaoResult greenLstIns = (DbTaoResult) proc.execute(context, dataSet);  // 강선영 추가
	
				// 05. 골프장 리스트 (Sel_Proc) 조회ramMap.put("greenNm",greenNm);
				GolfadmTopCodeSelDaoProc coodSelProc = (GolfadmTopCodeSelDaoProc)context.getProc("GolfadmTopCodeSelDaoProc");
				DbTaoResult codeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, bbs); //게시판 구분
				request.setAttribute("codeSelResult", codeSel);

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
			try { con.close(); } catch(Throwable ignore) {}
		}
		return getActionResponse(context); // response key
	}
}

