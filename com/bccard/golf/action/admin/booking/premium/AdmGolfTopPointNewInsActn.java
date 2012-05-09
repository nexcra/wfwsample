/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : AdmGolfTopPointNewInsActn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 포인트 관리  > 포인트관리 신규/수정 등록 화면
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
import com.bccard.waf.common.DateUtil;
 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/** ****************************************************************************
 * 포인트관리 등록 입력 출력 수행 액션.
 * @author 이훈주
 * @version 2004.10.29
 **************************************************************************** */
public class AdmGolfTopPointNewInsActn extends AbstractAction {

	public static final String TITLE = "관리자 > 부킹 > 포인트관리  > 포인트관리 신규등록 ";

		public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
			debug("***********************************************************************************");
			debug(" Action  AdmGolfTopPointNewInsActn.java 신규 등록 execute");
			debug("***********************************************************************************");
			
			TaoConnection con = null;
			
		try {
					debug("action AdmGolfTopPointNewInsActn.java try");
					RequestParser parser = context.getRequestParser("default",request,response);
					
					String roundDate		= parser.getParameter("roundDate","");		//라운드일자
					String memId				= parser.getParameter("memId","");			//회원번호
					String pointClss		= parser.getParameter("pointClss","50");	//포인트구분
					String pointDetlCd	= parser.getParameter("pointDetlCd","");	//포인트세부코드
					String pointMemo		= parser.getParameter("pointMemo");			//포인트내용
					String regIp				= request.getRemoteAddr();					//부킹처리IP

					HttpSession session = request.getSession(true);
					String regAdminNo = null;
					String regAdminNm = null;
					GolfAdminEtt sessionEtt = (GolfAdminEtt)session.getAttribute("SESSION_ADMIN"); 
					if(sessionEtt != null) {
						regAdminNm 		= (String)sessionEtt.getMemNm();  //부킹처리 관리자명
						regAdminNo    = (String)sessionEtt.getMemNo();//부킹처리 관리자번호
					}

					con = context.getTaoConnection("dbtao", null);
			
					//포인트정보에 등록
					TaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
					dataSet.setString("roundDate", roundDate);
					dataSet.setString("memId", memId);
					dataSet.setString("pointClss", pointClss);
					dataSet.setString("pointDetlCd", pointDetlCd);
					dataSet.setString("pointMemo", pointMemo);
					dataSet.setString("regIp", regIp);
					dataSet.setString("regAdminNo", String.valueOf(regAdminNo));
					dataSet.setString("regAdminNm", regAdminNm);

					AdmGolfTopPointNewInsProc proc = (AdmGolfTopPointNewInsProc)context.getProc("AdmGolfTopPointNewInsProc");		 // 강선영 추가
					DbTaoResult greenLstIns = (DbTaoResult) proc.execute(context, dataSet);  // 강선영 추가

//					TaoResult greenLstIns = con.execute("BookingPointNewInsProc", dataSet);

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

