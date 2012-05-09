/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrRegActn
*   작성자    : 미디어포스 임은혜
*   내용      : 마이티박스 > 나의 골프정보 > 골프입력
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.golf;

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
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.mytbox.golf.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0
******************************************************************************/
public class GolfMtScoreRegActn extends GolfActn{
	
	public static final String TITLE = "골프입력";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
            String userId	= "";
            
            if(usrEntity != null) 
        	{
        		userId		= (String)usrEntity.getAccount(); 
        	}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);			
						
			String green_NM 				= parser.getParameter("GREEN_NM", "").trim();
			String round_YEAR 				= parser.getParameter("ROUND_YEAR", "").trim();
			String round_MONTH 				= parser.getParameter("ROUND_MONTH", "").trim();
			String round_DAY 				= parser.getParameter("ROUND_DAY", "").trim();
			String round_DATE 				= round_YEAR+round_MONTH+round_DAY;
			String cdhd_ID 					= userId;
			String curs_NM 				= parser.getParameter("CURS_NM", "").trim();
			String hadc_NUM 				= parser.getParameter("HADC_NUM", "").trim();
			int hole_01_SCOR 				= parser.getIntParameter("HOLE01_SCOR", 0);
			int hole_02_SCOR 				= parser.getIntParameter("HOLE02_SCOR", 0);
			int hole_03_SCOR 				= parser.getIntParameter("HOLE03_SCOR", 0);
			int hole_04_SCOR 				= parser.getIntParameter("HOLE04_SCOR", 0);
			int hole_05_SCOR 				= parser.getIntParameter("HOLE05_SCOR", 0);
			int hole_06_SCOR 				= parser.getIntParameter("HOLE06_SCOR", 0);
			int hole_07_SCOR 				= parser.getIntParameter("HOLE07_SCOR", 0);
			int hole_08_SCOR 				= parser.getIntParameter("HOLE08_SCOR", 0);
			int hole_09_SCOR 				= parser.getIntParameter("HOLE09_SCOR", 0);
			int hole_10_SCOR 				= parser.getIntParameter("HOLE10_SCOR", 0);
			int hole_11_SCOR 				= parser.getIntParameter("HOLE11_SCOR", 0);
			int hole_12_SCOR 				= parser.getIntParameter("HOLE12_SCOR", 0);
			int hole_13_SCOR 				= parser.getIntParameter("HOLE13_SCOR", 0);
			int hole_14_SCOR 				= parser.getIntParameter("HOLE14_SCOR", 0);
			int hole_15_SCOR 				= parser.getIntParameter("HOLE15_SCOR", 0);
			int hole_16_SCOR 				= parser.getIntParameter("HOLE16_SCOR", 0);
			int hole_17_SCOR 				= parser.getIntParameter("HOLE17_SCOR", 0);
			int hole_18_SCOR 				= parser.getIntParameter("HOLE18_SCOR", 0);
			int hit_CNT 					= parser.getIntParameter("HIT_CNT", 0);
			int eg_NUM 						= parser.getIntParameter("EG_NUM", 0);
			int bid_NUM 					= parser.getIntParameter("BID_NUM", 0);
			int par_NUM 					= parser.getIntParameter("PAR_NUM", 0);
			int bog_NUM 					= parser.getIntParameter("BOG_NUM", 0);
			int dob_BOG_NUM 				= parser.getIntParameter("DOB_BOG_NUM", 0);
			int trp_BOG_NUM 				= parser.getIntParameter("TRP_BOG_NUM", 0);
			int etc_BOG_NUM 				= parser.getIntParameter("ETC_BOG_NUM", 0);
			String round_MEMO_CTNT 			= parser.getParameter("ROUND_MEMO_CTNT", "").trim();
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("GREEN_NM", green_NM);
			dataSet.setString("ROUND_DATE", round_DATE);
			dataSet.setString("CDHD_ID", cdhd_ID);
			dataSet.setString("CURS_NM", curs_NM);
			dataSet.setString("HADC_NUM", hadc_NUM);
			dataSet.setInt("HOLE01_SCOR", hole_01_SCOR);
			dataSet.setInt("HOLE02_SCOR", hole_02_SCOR);
			dataSet.setInt("HOLE03_SCOR", hole_03_SCOR);
			dataSet.setInt("HOLE04_SCOR", hole_04_SCOR);
			dataSet.setInt("HOLE05_SCOR", hole_05_SCOR);
			dataSet.setInt("HOLE06_SCOR", hole_06_SCOR);
			dataSet.setInt("HOLE07_SCOR", hole_07_SCOR);
			dataSet.setInt("HOLE08_SCOR", hole_08_SCOR);
			dataSet.setInt("HOLE09_SCOR", hole_09_SCOR);
			dataSet.setInt("HOLE10_SCOR", hole_10_SCOR);
			dataSet.setInt("HOLE11_SCOR", hole_11_SCOR);
			dataSet.setInt("HOLE12_SCOR", hole_12_SCOR);
			dataSet.setInt("HOLE13_SCOR", hole_13_SCOR);
			dataSet.setInt("HOLE14_SCOR", hole_14_SCOR);
			dataSet.setInt("HOLE15_SCOR", hole_15_SCOR);
			dataSet.setInt("HOLE16_SCOR", hole_16_SCOR);
			dataSet.setInt("HOLE17_SCOR", hole_17_SCOR);
			dataSet.setInt("HOLE18_SCOR", hole_18_SCOR);
			dataSet.setInt("HIT_CNT", hit_CNT);
			dataSet.setInt("EG_NUM", eg_NUM);
			dataSet.setInt("BID_NUM", bid_NUM);
			dataSet.setInt("PAR_NUM", par_NUM);
			dataSet.setInt("BOG_NUM", bog_NUM);
			dataSet.setInt("DOB_BOG_NUM", dob_BOG_NUM);
			dataSet.setInt("TRP_BOG_NUM", trp_BOG_NUM);
			dataSet.setInt("ETC_BOG_NUM", etc_BOG_NUM);
			dataSet.setString("ROUND_MEMO_CTNT", round_MEMO_CTNT);
						
			// 04.실제 테이블(Proc) 조회
			GolfMtScoreRegDaoProc proc = (GolfMtScoreRegDaoProc)context.getProc("GolfMtScoreRegDaoProc");
			int addResult = proc.execute(context, dataSet, request);			
				        
	        String returnUrlTrue = "GolfMtScoreList.do";
	        String returnUrlFalse = "GolfMtScoreRegForm.do";
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
