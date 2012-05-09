/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBusInqActn
*   작성자    : (주)미디어포스 권영만
*   내용      : 이벤트->골프장버스운행이벤트
*   적용범위  : Golf
*   작성일자  : 2009-09-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.golfbus;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntBusInqActn extends AbstractAction {

	public static final String Title = "이벤트->골프장버스운행이벤트";

	/***********************************************************************
	 * 액션처리.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return 응답정보
	 **********************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		TaoConnection	con			= null;
		RequestParser	parser		= context.getRequestParser("default", request, response);
		TaoResult 		result  	= null;

		String userNm = ""; 
		String userId = "";
		String userMoblie1 = "";
		String userMoblie2 = "";
		String userMoblie3 = "";
		String permission = "";
		
		try {
			
			Map paramMap 			= parser.getParameterMap();
			
			//PATH 가져오기
			String imgPath			= AppConfig.getAppProperty("GIFT_PATH"); 	
			
			//1. 파라메타 값 
			String actnKey 			= super.getActionKey(context);									
			
			con = context.getTaoConnection("dbtao",null);
						
			// 파라메터 설정
			TaoDataSet input = new DbTaoDataSet(Title);
			input.setString("actnKey", 		actnKey);
			input.setString("Title", 			Title);			

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			if(usrEntity != null) 
			{
				userNm		= (String)usrEntity.getName(); 
				userId 		= (String)usrEntity.getAccount();
				userMoblie1  = (String)usrEntity.getMobile1();
				userMoblie2  = (String)usrEntity.getMobile2();
				userMoblie3  = (String)usrEntity.getMobile3();
			}

			// 일정 조회			
			result = con.execute("event.golfbus.GolfEvntBusInqDaoProc",input); 							
			
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));   
			paramMap.put("co_nm", userNm);
			paramMap.put("hp_ddd_no", userMoblie1);
			paramMap.put("hp_tel_hno", userMoblie2);
			paramMap.put("hp_tel_sno", userMoblie3);
			
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);
			
			DbTaoDataSet dataSet = new DbTaoDataSet(Title);

			//권한 조회 : 생성만 해둠
			String permissionColum = "LMS_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				
			}else{
				permission = "N";
			}
			
			
			
		} catch (Throwable be) {			
			throw new GolfException(Title, be);
		} finally {
			try { if(con != null) { con.close(); } else {;} } catch(Throwable ignore) {}
		}
		return super.getActionResponse(context);
	}
}