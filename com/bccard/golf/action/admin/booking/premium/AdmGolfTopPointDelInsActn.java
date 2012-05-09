/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admGolfTopPointDelIns.Actn
*   작성자    : 김상범
*   내용      : 관리자 > 부킹 > 포인트관리  > 포인트관리 리스트 삭제
*   적용범위  : Golf
*   작성일자  : 2010-11-19
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
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.DateUtil;
 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;

import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/** ****************************************************************************
 * 페널트관리 삭제입력 수행 액션.
 * @author  김상범
 * @version 2010.12.07
 **************************************************************************** */
public class AdmGolfTopPointDelInsActn extends AbstractAction {

	public static final String TITLE	= "페널트관리 삭제";
		public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
			TaoConnection con = null;

			try {
					RequestParser parser = context.getRequestParser("default",request,response);
		
					String seqNo		= parser.getParameter("seqNo","");			//일련번호
					String memId		= parser.getParameter("memId","");			//회원번호
		
					con = context.getTaoConnection("dbtao", null);
		
					//포인트정보에 삭제
					TaoDataSet dataSet = new DbTaoDataSet(TITLE);			
		
					dataSet.setString("seqNo", seqNo);
					dataSet.setString("memId", memId);
					//dataSet.setString("regIp", regIp);
					//dataSet.setString("regAdminNo", String.valueOf(regAdminNo));
					//dataSet.setString("regAdminNm", regAdminNm);
		
					AdmGolfTopPointDelInsProc proc = (AdmGolfTopPointDelInsProc)context.getProc("AdmGolfTopPointDelInsProc");		
					DbTaoResult greenLstIns = (DbTaoResult) proc.execute(context, dataSet); 
			//		TaoResult greenLstIns = con.execute("GolfadmTopPenaltyDelInsProc", dataSet);

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

