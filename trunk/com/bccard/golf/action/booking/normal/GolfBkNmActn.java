/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdFormActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 폼
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.normal;

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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.dbtao.proc.booking.normal.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfBkNmActn extends GolfActn{
	
	public static final String TITLE = "부킹 > 일반부킹";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String isLogin = "";
		int intMemGrade = 0;		// 공통등급
		int intMemberGrade = 0;		// 멤버십 등급
		int intCardGrade = 0;		// 카드 등급
		String memb_id = "";
		int memb_point = 0;
		int gen_WKD_BOKG = 0;	// 일반주중부킹횟수
		int gen_WKE_BOKG = 0;	// 일반주말부킹횟수
		int cy_MONEY = 0; 		// 사이버머니

		String penalty = "";
		String penalty_start = "";
		String penalty_end = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			 if(userEtt != null) {
				intMemGrade = userEtt.getIntMemGrade();
				memb_id = userEtt.getAccount();
				memb_point = userEtt.getCyberMoney();
				intMemberGrade = userEtt.getIntMemberGrade();
				intCardGrade = userEtt.getIntCardGrade();
			}

			if(memb_id != null && !"".equals(memb_id)){
				isLogin = "1";
			} else {
				isLogin = "0";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.) 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			

			// 04-01. 부킹 제한 조회
			GolfBkPenaltyDaoProc proc_penalty = (GolfBkPenaltyDaoProc)context.getProc("GolfBkPenaltyDaoProc");
			DbTaoResult penaltyView = proc_penalty.execute(context, dataSet, request);
			
			penaltyView.next();
			if(penaltyView.getString("RESULT").equals("00")){
				penalty = "Y";
				penalty_start = penaltyView.getString("BK_LIMIT_ST");
				penalty_end = penaltyView.getString("BK_LIMIT_ED");
			}else{
				penalty = "N";
			}
			paramMap.put("penalty", penalty);
			paramMap.put("penalty_start", penalty_start);
			paramMap.put("penalty_end", penalty_end);
//			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn ===  penalty => " + penalty + " / penalty_start : " + penalty_start + " / penalty_end : " + penalty_end);

			
			if(isLogin.equals("1")){
				// 04. 접근권한 조회	: 일반부킹은 접근제한 페이지 없음 => 접근권한 제한 기능을 설정하지 않음
				
				// 05. 부킹 횟수 조회 - 사이버 머니 사용내역은 회수에서 차감된다.
				// 주중
				GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
				DbTaoResult nmWkdView = proc_times.getNmWkdBenefit(context, dataSet, request);
				nmWkdView.next();
				gen_WKD_BOKG = nmWkdView.getInt("GEN_WKD_BOKG");
				cy_MONEY = nmWkdView.getInt("CY_MONEY");
				// 주말
				DbTaoResult nmWkeView = proc_times.getNmWkeBenefit(context, dataSet, request);
				nmWkeView.next();
				gen_WKE_BOKG = nmWkeView.getInt("GEN_WKE_BOKG");
			}
			
			paramMap.put("memb_id", memb_id);
			paramMap.put("day_cnt", gen_WKD_BOKG+"");
			paramMap.put("week_cnt", gen_WKE_BOKG+"");
			paramMap.put("memb_point", cy_MONEY+"");


			debug("ACTN : day_cnt : 주중 부킹 가능 횟수 : " + gen_WKD_BOKG+"");
			debug("ACTN : week_cnt : 주말 부킹 가능 횟수 : " + gen_WKE_BOKG+"");
			debug("ACTN : memb_point : 사이버머니 : " + memb_point);

			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
