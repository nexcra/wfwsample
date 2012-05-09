/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopMemFrameActn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 패널티관리  > 패널티관리  등록/수정 -> 패널티대상회원 조회
*   적용범위  : Golf
*   작성일자  : 2010-12-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
* 
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import com.bccard.waf.core.RequestParser;
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
import com.bccard.waf.common.DateUtil;
 
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/******************************************************************************
 * 회원정보 목록 조회 수행 액션.
 * @author  김상범
 * @version 2010.12.08
 **************************************************************************** */
public class GolfadmTopMemInqActn extends AbstractAction {

		public static final String TITLE ="부킹대상 회원조회(POPUP)";

		/** ***********************************************************************
		* Proc 실행.
		* @param con Connection
		* @param context WaContext 객체
		* @param request HttpServletRequest 객체
		* @param response HttpServletResponse 객체
		************************************************************************ */    
    public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {    
			TaoConnection con = null;
			String curActionKey = getActionKey(context);

		try {
			TaoDataSet dataSet = new DbTaoDataSet(TITLE);            
			RequestParser parser = context.getRequestParser("default",request,response);
			String name = parser.getParameter("name","");
			String account = parser.getParameter("account","");
			String title = parser.getParameter("title","");
			long pageNo = parser.getLongParameter("pageNo",1L);
			long recordsInPage  = 10L;//parser.getLongParameter("recordsInPage", 10);  // 페이지당출력수

			dataSet.setString("name",name);
			dataSet.setString("account",account);
			dataSet.setLong("pageNo",pageNo);
			dataSet.setLong("recordsInPage",recordsInPage);

			con = context.getTaoConnection("dbtao",null);            
			
			DbTaoResult boardInqLst = null;
			if("penalty".equals(title)){	//부킹패널티 회원조회
                                        debug("------------------------ lproc --------------------");
                                        debug("-------------  penalty GolfadmTopPenaltyMemInqProc  ---------------");
                                        debug("------------------------ lproc --------------------");
					GolfadmTopPenaltyMemInqProc proc = (GolfadmTopPenaltyMemInqProc)context.getProc("GolfadmTopPenaltyMemInqProc");		 
				 	boardInqLst = (DbTaoResult) proc.execute(context, dataSet);  
				//boardInqLst = con.execute("GolfadmTopPenaltyMemInqProc", dataSet);
			}else {	//일반 회원조회
                                        debug("------------------------ lproc --------------------");
                                        debug("-------------  GolfadmTopMemInqProc ---------------");
                                        debug("------------------------ lproc --------------------");

					GolfadmTopMemInqProc proc = (GolfadmTopMemInqProc)context.getProc("GolfadmTopMemInqProc");		 
				 	boardInqLst = (DbTaoResult) proc.execute(context, dataSet);  

			//	boardInqLst = con.execute("GolfadmTopMemInqProc", dataSet);
			}

			Map paramMap = parser.getParameterMap();

			paramMap.put("name", name);
			paramMap.put("account", account);
			paramMap.put("title", title);
			paramMap.put("pageNo", String.valueOf(pageNo));
			paramMap.put("recordsInPage", String.valueOf(recordsInPage));
			if(boardInqLst.isNext()){
				boardInqLst.next();
				if("00".equals(boardInqLst.getString("result"))){
					paramMap.put("recordCnt", String.valueOf(boardInqLst.getLong("recordCnt")));
				}else{
					paramMap.put("recordCnt", "0");
				}
			}

			request.setAttribute("taoResult",boardInqLst);
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
			try { con.close(); } catch(Throwable ignore) {}
		}
		return getActionResponse(context); // response key
	}
}
