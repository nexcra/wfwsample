/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLsnRecvPayCnclActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨신청 결제 취소
*   적용범위  : golf
*   작성일자  : 2009-06-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentUpdDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentInqDaoProc;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmLsnRecvPayCnclActn extends GolfActn{
	
	public static final String TITLE = "레슨신청 결제 취소";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
	    
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request 값 저장
			String odr_no			= parser.getParameter("odr_no", "");
			String cdhd_id			= parser.getParameter("cdhd_id", "");

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ODR_NO", odr_no);
			dataSet.setString("CDHD_ID", cdhd_id);
			
			// 04.실제 테이블(Proc) 조회
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
			GolfPaymentInqDaoProc inqProc = (GolfPaymentInqDaoProc)context.getProc("GolfPaymentInqDaoProc");
			GolfPaymentUpdDaoProc updProc = (GolfPaymentUpdDaoProc)context.getProc("GolfPaymentUpdDaoProc");

			DbTaoResult payInfoResult = (DbTaoResult) inqProc.getPaymentInfo(context, dataSet);
			if (payInfoResult != null && payInfoResult.isNext()) {
				payInfoResult.first();
				payInfoResult.next();
				if (payInfoResult.getObject("RESULT").equals("00")) {
					payEtt.setMerMgmtNo((String)payInfoResult.getString("MER_NO"));
					payEtt.setCardNo((String)payInfoResult.getString("CARD_NO"));
					payEtt.setValid((String)payInfoResult.getString("VALD_DATE").substring(3, 4));
					payEtt.setAmount((String)payInfoResult.getString("STTL_AMT"));					
					String insTerm = (String)payInfoResult.getString("INS_MCNT");
					if (insTerm.length() == 1) insTerm = "0"+insTerm;
					payEtt.setInsTerm(insTerm);
					payEtt.setUseNo((String)payInfoResult.getString("AUTH_NO"));
					payEtt.setRemoteAddr(request.getRemoteAddr());
				}
			}
			
			boolean payCancelResult = false;
			payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출

			debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");		
			debug("=====> MerMgmtNo : " + payEtt.getMerMgmtNo());
			debug("=====> CardNo : " + payEtt.getCardNo());
			debug("=====> Valid : " + payEtt.getValid());
			debug("=====> Amount : " + payEtt.getAmount());
			debug("=====> InsTerm : " + payEtt.getInsTerm());
			debug("=====> UseNo : " + payEtt.getUseNo());
			debug("=====> RemoteAddr : " + payEtt.getRemoteAddr());
			debug("=====> payCancelResult : " + payCancelResult);			
			debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); 
			
			int cnclResult = 0;
			// 결제 승인 완료
			if (payCancelResult) { // 꼭~~ 수정해야 할 사항
				cnclResult = updProc.execute(context, dataSet);		
			}	
			
	        if (cnclResult == 1) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "");	
	        } else {				
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "결제 취소가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }	
			
			// 05. Return 값 세팅
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
