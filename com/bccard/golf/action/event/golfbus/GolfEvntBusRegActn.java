/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBusRegActn
*   작성자    : (주)미디어포스 진현구
*   내용      : 이벤트->골프장버스운행이벤트
*   적용범위  : Golf
*   작성일자  : 2009-09-30
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
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntBusRegActn extends AbstractAction {

	public static final String TITLE = "관리자 골드장 버스 운행 등록 처리";
	
	/**
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionResponse
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request,
		HttpServletResponse response) throws IOException, ServletException,
			BaseException
	{
		TaoConnection 		con 				= null;
		TaoResult 			result  			= null;		
		Map 				paramMap 			= null;
		String				userId				= "";
		
		try {
			// form parameter parsing
			RequestParser parser 				= context.getRequestParser("default", request, response);						
			paramMap 							= (Map)request.getAttribute("paramMap");
			if(paramMap == null) 	   paramMap = parser.getParameterMap();
			String actnKey 						= super.getActionKey(context);

			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {
				userId = (String)usrEntity.getAccount(); 
			}
			 
			String green_nm						= parser.getParameter("green_nm");						// 골프장
			String teof_date					= parser.getParameter("teof_date");						// 날짜
			String co_nm						= parser.getParameter("co_nm");							// 예약자 이름
			String golf_mgz_dlv_pl_clss			= parser.getParameter("golf_mgz_dlv_pl_clss");			// 신청인원
			String hp_ddd_no					= parser.getParameter("hp_ddd_no");						// 휴대폰
			String hp_tel_hno					= parser.getParameter("hp_tel_hno");					// 휴대폰
			String hp_tel_sno					= parser.getParameter("hp_tel_sno");					// 휴대폰
			String email						= parser.getParameter("email");							// email
			
			String jumin1						= parser.getParameter("jumin1");						// 주민번호1
			String jumin2						= parser.getParameter("jumin2");						// 주민번호2
			String jumin 						= jumin1+"-"+jumin2;
			/*
			String[] arrtrNm = parser.getParameterValues("trNm");
			String[] arrtrTel1 = parser.getParameterValues("trTel1");
			String[] arrtrTel2 = parser.getParameterValues("trTel2");
			String[] arrtrTel3 = parser.getParameterValues("trTel3");
			String[] arrtrMem = parser.getParameterValues("trMem");
			
			int arrCnt = 0;
			arrCnt = arrtrNm.length;		// 신청자 수
			String trAllValue = "";			// 신청자 명단

			for(int i=0; i<arrtrNm.length; i++ ) {
				if (i>0) {
					trAllValue += " / ";
				}
				trAllValue += arrtrNm[i];
				trAllValue += "|" + arrtrTel1[i];
				trAllValue += "-" + arrtrTel2[i];
				trAllValue += "-" + arrtrTel3[i];
				trAllValue += "|" + arrtrMem[i];
			}
			*/

			con = context.getTaoConnection("dbtao",null);

			// Proc 파라메터 설정
			TaoDataSet input 					= new DbTaoDataSet(TITLE);
			input.setString("userId", 			userId);
			input.setString("actnKey", 			actnKey);
			input.setString("Title", 			TITLE);					
			
			input.setString("green_nm",			green_nm);
			input.setString("teof_date",		teof_date);
			input.setString("co_nm",			co_nm);
			input.setString("golf_mgz_dlv_pl_clss",		golf_mgz_dlv_pl_clss);
			input.setString("hp_ddd_no",		hp_ddd_no);
			input.setString("hp_tel_hno",		hp_tel_hno);
			input.setString("hp_tel_sno",		hp_tel_sno);
			input.setString("email",			email);
			input.setString("trAllValue",			jumin);		//비고사항을 주민번호로 대체
			//input.setString("trAllValue",		trAllValue);
			//input.setInt("arrCnt",				arrCnt);

			// DB 처리
			result = con.execute("event.golfbus.GolfEvntBusInsDaoProc",input);	

			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));
			//paramMap.put("arrCnt", arrCnt + "");
						
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);
			
		} catch (BaseException be) {
			throw be;
		} catch (Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} finally {
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return getActionResponse(context, "default");
	}
	
	
}
