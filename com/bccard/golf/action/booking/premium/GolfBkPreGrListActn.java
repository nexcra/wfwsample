/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreGrListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 골프장 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

import com.bccard.golf.common.ChkChgSocIdException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스  
* @version	1.0 
******************************************************************************/
public class GolfBkPreGrListActn extends GolfActn{
	
	public static final String TITLE = "프리미엄 부킹 리스트";
	private static final String BSNINPT = "BSNINPT";					// 프레임웍 조회서비스
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		int intCardGrade = 0;
		String memb_id = "";
		String permission = ""; 
		String cardGolfYn = "";			// 골프부킹서비스제공구분
		String cardGolfStartDay = "";	// 골프부킹서비스시작일자
		String golfStartDate = "";	// 현재 일자-1년

        int sYear = 0;
        int sMonth = 0;
        int sDay = 0; 
		
		// 00.레이아웃 URL 저장 
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			
			// 01.세션정보체크 
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memb_id = userEtt.getAccount();				// 회원 아이디
				intMemGrade = userEtt.getIntMemGrade();
				intCardGrade = userEtt.getIntCardGrade();		// 카드 등급
			}
			
			//debug("GolfBkPreGrListActn ==================== intMemGrade => " + intMemGrade);
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
									
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setInt("intMemGrade",		intMemGrade);
			
			// 04. 접근권한 조회	
			String permissionColum = "PMI_BOKG_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, memb_id, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			
			// 기업은행카드 회원 조회
			if(intCardGrade>0)
			{
				GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
				if (mbr != null) 
				{	
					List cardList = mbr.getCardInfoList();
					CardInfoEtt cardInfo = new CardInfoEtt();
					
					if( cardList.size() > 0 )
					{
						//서비스 제공구분에 1이 있고 서비스 시작일자에서 일년이 지나지 않은경우 부킹 할 수 있도록 해준다.
						cardGolfYn = cardInfo.getGolfYn();
						cardGolfStartDay = cardInfo.getGolfStartDay();
						//cardGolfYn = "1";
						//cardGolfStartDay = "20080911";
						
						debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`= cardGolfYn : " + cardGolfYn);
						debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`= cardGolfStartDay : " + cardGolfStartDay);

						if(!GolfUtil.empty(cardGolfYn) && !GolfUtil.empty(cardGolfStartDay)){

					        GregorianCalendar nowDate = new GregorianCalendar ( );
					        nowDate.add(Calendar.YEAR, -1);

					        sYear = nowDate.get ( nowDate.YEAR );
					        sMonth = nowDate.get ( nowDate.MONTH ) + 1;
					        sDay = nowDate.get ( nowDate.DAY_OF_MONTH ); 
					        
					        golfStartDate = sYear + "";
					        if (sMonth<10) golfStartDate = golfStartDate + "0";
					        golfStartDate = golfStartDate + sMonth ;
					        if (sDay<10) golfStartDate = golfStartDate + "0";
					        golfStartDate = golfStartDate + sDay;
					        
					        int intGolfStartDate = Integer.parseInt(golfStartDate);
							int intCardGolfStartDay = Integer.parseInt(cardGolfStartDay);
							debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`= intGolfStartDate : " + intGolfStartDate);
							debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`= intCardGolfStartDay : " + intCardGolfStartDay);
							
							if(cardGolfYn.equals("1") && intCardGolfStartDay>intGolfStartDate){ 								
								permission = "Y";
							}
						}
					}
				}
			}
			
			
			paramMap.put("permission", permission);

			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === intMemGrade => " + intMemGrade);
			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === permission => " + permission);

			// 04.실제 테이블(Proc) 조회
			if(permission.equals("Y")){
				GolfBkPreGrListDaoProc proc = (GolfBkPreGrListDaoProc)context.getProc("GolfBkPreGrListDaoProc");
				DbTaoResult grListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				paramMap.put("resultSize", String.valueOf(grListResult.size()));
				request.setAttribute("GrListResult", grListResult);
			}
						
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
