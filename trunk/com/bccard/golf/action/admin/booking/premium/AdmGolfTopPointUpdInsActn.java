/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : AdmGolfTopPointUpdInsActn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 패널티관리  > 포인트관리 수정 입력 액션 
*   적용범위  : Golf
*   작성일자  : 2010-12-29
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
 * @version 2010.12.29
 **************************************************************************** */
public class AdmGolfTopPointUpdInsActn extends AbstractAction {

	public static final String TITLE	= "포인트관리 수정";
		public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {

				TaoConnection con = null;
				debug("***********************************************************************************");
				debug(" Action  AdmGolfTopPointUpdInsActn.java 수정 액션 execute");
				debug("***********************************************************************************");

		try {
			// 01.정보가져오기 
				RequestParser parser = context.getRequestParser("default",request,response);
	
				String roundDate        = parser.getParameter("roundDate","");          //라운드일자
				String seqNo            = parser.getParameter("seqNo","");              //일련번호
				String memId            = parser.getParameter("memId","");              //회원번호
				String pointClss        = parser.getParameter("pointClss","50");        //포인트구분
				String pointDetlCd      = parser.getParameter("pointDetlCd","");        //포인트세부코드
				String pointMemo        = parser.getParameter("pointMemo");             //포인트내용
				String regIp            = request.getRemoteAddr();                      //부킹처리IP
	                      
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
	
				dataSet.setString("roundDate", roundDate);
				dataSet.setString("seqNo", seqNo);
				dataSet.setString("memId", memId);
				dataSet.setString("pointClss", pointClss);
				dataSet.setString("pointDetlCd", pointDetlCd);
				dataSet.setString("pointMemo", pointMemo);
				dataSet.setString("regIp", regIp);
				dataSet.setString("regAdminNo", String.valueOf(regAdminNo));
				dataSet.setString("regAdminNm", regAdminNm);
	
	
				AdmGolfTopPointUpdInsProc proc = (AdmGolfTopPointUpdInsProc)context.getProc("AdmGolfTopPointUpdInsProc");	
				DbTaoResult greenLstIns = (DbTaoResult) proc.execute(context, dataSet);  

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

