/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 가입 > 등록
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.sky.GolfBkSkyTimeRsInsDaoProc;
import com.bccard.golf.dbtao.proc.booking.sky.GolfBkSkyTimeRsViewDaoProc;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;

import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0  
******************************************************************************/
public class GolfMembershipActn extends GolfActn{
	
	public static final String TITLE = "가입 > 등록";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String rejoin_YN = "";
		String memGrade = "";
		int intMemGrade = 0;
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);		 	
				 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String rsvt_SQL_NO			= parser.getParameter("RSVT_SQL_NO", "");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_SQL_NO);
					
			String strMemChk = "";	// 1:개인, 5:법인
			String join_date = "";
			String upd_pay = "";
			String join_chnl = "";
			
			// 04.실제 테이블(Proc)	
			if(usrEntity != null) {		
				GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
				DbTaoResult addResult = proc.reJoinExecute(context, dataSet, request);
				if (addResult != null && addResult.isNext()) {
					addResult.first();
					addResult.next();
					rejoin_YN = (String) addResult.getObject("RESULT");
					memGrade = (String) addResult.getObject("memGrade");
					intMemGrade = (int) addResult.getInt("intMemGrade");
					join_date = (String) addResult.getObject("join_date");
					upd_pay = (String) addResult.getObject("upd_pay");
					join_chnl = (String) addResult.getObject("join_chnl");
				}
				debug("rejoin_YN => " + rejoin_YN+"memGrade => " + memGrade+"intMemGrade => " + intMemGrade);
				
				strMemChk = usrEntity.getStrMemChkNum();
			}
			 
			debug("strMemChk:"+strMemChk);
			

			String strGolfCardYn = "N";
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			if (mbr != null) 
			{	
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				if( cardList.size() > 0 )
				{
					cardInfo = (CardInfoEtt)cardList.get(0);
					strGolfCardYn	= "Y";
				}

			}
			System.out.print("### strGolfCardYn:"+strGolfCardYn);
			
						
			request.setAttribute("strGolfCardYn", strGolfCardYn); 		//골프카드유무
			
			paramMap.put("REJOIN_YN", rejoin_YN);
			paramMap.put("memGrade", memGrade);
			paramMap.put("intMemGrade", intMemGrade+"");
			paramMap.put("join_date", join_date);
			paramMap.put("upd_pay", upd_pay);
			paramMap.put("join_chnl", join_chnl);
			
			// 05. Return 값 세팅
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
	        request.setAttribute("strMemChk", strMemChk); 
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
