/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemJoinPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 가입 팝업
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemJoinNocardDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemJoinNoCardActn extends GolfActn{
	
	public static final String TITLE = "회원 > 서비스 이용약관";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		String sece_yn = "";
		String cdhd_id = "";
		String isGolfMem = "N";
		int intMemGrade = 0;
		String memGrade = "";
		String jumin_no_golf = "";	// 중복가입 방지를 위해 추가
		
		String isTmMem = "N";
		String jumin_no = "";
		String join_chnl = "";
		
		String isSameJumin = "N";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01. 세션정보체크
			//debug("========= GolfMemJoinPopActn =========> ");

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memType = userEtt.getMemberClss();
			String memId = userEtt.getAccount();
			String memSocid = userEtt.getSocid();
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			//dataSet.setString("payType", payType);	
			
			// 이미 가입한 회원인지 알아본다 
			// 가입한 회원이면 해당 세션을 구워서 전페이지로 돌린다.
			// 04.실제 테이블(Proc) 조회
			GolfMemJoinNocardDaoProc proc = (GolfMemJoinNocardDaoProc)context.getProc("GolfMemJoinNocardDaoProc");
			DbTaoResult noCardResult = proc.execute(context, dataSet, request);	

			if (noCardResult != null && noCardResult.isNext()) {
				noCardResult.first();
				noCardResult.next();
				if(noCardResult.getString("RESULT").equals("00")){
					sece_yn = (String) noCardResult.getString("SECE_YN");
					cdhd_id = (String) noCardResult.getString("CDHD_ID");
					intMemGrade = (int) noCardResult.getInt("intMemGrade");
					memGrade = (String) noCardResult.getString("memGrade");
					jumin_no_golf = (String) noCardResult.getString("JUMIN_NO");
					
					// 아이디는 같지만 주민등록번호가 다를경우 가입을 막는다.
					if(!jumin_no_golf.equals(memSocid)){
						isSameJumin = "Y";
					}
					
					if(!cdhd_id.equals("")){
						if(!sece_yn.equals("Y") || GolfUtil.empty(sece_yn)){
							isGolfMem = "Y";
							userEtt.setMemGrade(memGrade);
							userEtt.setIntMemGrade((int)intMemGrade);
						}
					}
				}
			}
			
			// TM 회원인지 알아본다.
			DbTaoResult noCardTmResult = proc.tm_execute(context, dataSet, request);	

			if (noCardTmResult != null && noCardTmResult.isNext()) {
				noCardTmResult.first();
				noCardTmResult.next();
				if(noCardTmResult.getString("RESULT").equals("00")){
					jumin_no = (String) noCardTmResult.getString("JUMIN_NO");
					join_chnl = (String) noCardTmResult.getString("JOIN_CHNL");
					
					if(!GolfUtil.empty(jumin_no) && !GolfUtil.empty(join_chnl)){
						//if(join_chnl.equals("1") || join_chnl.equals("2")){
							isTmMem = "Y";
						//}
					}
				}
			}

			debug("sece_yn : " + sece_yn + " / cdhd_id : " + cdhd_id + " / isGolfMem : " + isGolfMem + " / intMemGrade : " + intMemGrade 
					+ " / memGrade" + memGrade + " / isTmMem : " + isTmMem + " / jumin_no : " + jumin_no + " / join_chnl : " + join_chnl
					+ " / jumin_no_golf" + jumin_no_golf + " / isSameJumin : " + isSameJumin + " / memId" + memId + " / memType : " + memType); 
			
			paramMap.put("isGolfMem", isGolfMem);
			paramMap.put("isTmMem", isTmMem);
			paramMap.put("isSameJumin", isSameJumin);
			paramMap.put("memType", memType);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
