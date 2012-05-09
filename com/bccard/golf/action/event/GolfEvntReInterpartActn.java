/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntReInterpartActn
*   작성자    : E4NET 은장선
*   내용      : 이벤트 > 인터파크이벤트 > 인터파크유입 체크
*   적용범위  : Golf
*   작성일자  : 2009-08-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.bccard.golf.dbtao.proc.event.GolfEvntBkWinListDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	JSEUN
* @version	1.0
******************************************************************************/
public class GolfEvntReInterpartActn extends GolfActn{
	
	public static final String TITLE = "프리미엄 부킹 이벤트 당첨자 리스트";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		
		
		try {
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 01.세션정보체크
			String isInterpark = (String)request.getSession().getAttribute("isInterpark");
			String jumin_no		= "";  //주민등록번호
			String cupn			= "";  //쿠포번호
			String email		= "";  //e-mail
			String pwin_date	= "";  //당첨일자
			String userId		= "";  //ID
			

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			if(usrEntity != null) {
				userId          = (String)usrEntity.getAccount(); 
				jumin_no        = (String)usrEntity.getSocid();
			}

			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("socid"   , jumin_no);

			GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
			DbTaoResult cpnInfo = (DbTaoResult) inter.getCpnNumber(context, request, dataSet);
			String useYN = (String) inter.getUseYN(context, request, dataSet);

			request.setAttribute("useYN", useYN);
			if (cpnInfo != null && cpnInfo.isNext()) {
				cpnInfo.first();
				cpnInfo.next();
				if(cpnInfo.getString("RESULT").equals("00")){
					cupn		= cpnInfo.getString("CUPN");
					email		= cpnInfo.getString("EMAIL");    
					pwin_date   = DateUtil.format(cpnInfo.getString("PWIN_DATE"),"yyyymmdd","yyyy/mm/dd");

					request.setAttribute("availCpn"  ,		"Y");
					request.setAttribute("cupn"      ,		cupn);
					request.setAttribute("email"    ,		email);
					request.setAttribute("userId"    ,		userId);
					request.setAttribute("pwin_date" ,		pwin_date);
				}else if(cpnInfo.getString("RESULT").equals("01")){					
					subpage_key = "notavail";
					request.setAttribute("availCpn","N");
				}
			}


		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
