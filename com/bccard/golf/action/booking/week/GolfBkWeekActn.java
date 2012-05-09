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
package com.bccard.golf.action.booking.week;

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
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.dbtao.proc.booking.par.GolfBkParGrListDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfBkWeekActn extends GolfActn{
	
	public static final String TITLE = "관리자 부킹 골프장 수정 폼";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		String memb_id = "";
		int memb_point = 0;
		String isLogin = "";
		String day_cnt = "0";
		String week_cnt = "0";
		String permission = "";

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
			dataSet.setInt("intMemGrade",		intMemGrade);

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
				// 04. 접근권한 조회 - 주중 그린피 할인	
				String permissionColum = "WKD_GREEN_DC_APO_YN";
				GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
				DbTaoResult permissionView = proc_permission.execute(context, dataSet, memb_id, permissionColum);

				permissionView.next();
				if(permissionView.getString("RESULT").equals("00")){
					permission = permissionView.getString("LIMT_YN");					
				}else{
					permission = "N";
				}
			}

			if(permission.equals("Y")){
				day_cnt = "1";
				week_cnt = "1";	
			}else{
				day_cnt = "0";
				week_cnt = "0";
			}
			int cy_MONEY  = 0;
			
			//사이버머니 GET
			GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult nmWkdView = proc_times.getNmWkdBenefit(context, dataSet, request);
			nmWkdView.next();
			cy_MONEY = nmWkdView.getInt("CY_MONEY");
			
			
			debug(">>>>>>>>>>>>   permission :"+permission+" / day_cnt: "+day_cnt + " / week_cnt : "+week_cnt + " /cy_MONEY : "+cy_MONEY);
			
			
			paramMap.put("memb_id", memb_id);
			paramMap.put("day_cnt", day_cnt);
			paramMap.put("week_cnt", week_cnt);
			paramMap.put("memb_point", Integer.toString(cy_MONEY));
			request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
