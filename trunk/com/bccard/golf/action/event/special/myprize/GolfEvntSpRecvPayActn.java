/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpRecvPayActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한 레슨이벤트 > 나의당첨내역 결재안내
*   적용범위	: golf
*   작성일자	: 2009-07-11
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.special.myprize;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentInqDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/ 
public class GolfEvntSpRecvPayActn extends GolfActn{
	
	public static final String TITLE = "이벤트라운지 > 특별한 레슨이벤트 > 나의당첨내역 결재안내";
 
	/***************************************************************************************
	* 비씨탑포인트 관리자화면 
	* @param context		WaContext 객체.  
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String juminno = "";  
		String memGrade = ""; 
		String userSex = "";
		
		int intMemGrade = 0; 
		int myPointResult =  0;
		
		// 00.레이아웃 URL 저장 
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try { 
			// 01.세션정보체크
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				userSex		= (String)usrEntity.getSex();
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
				
			}
			 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			String realPayAmt	= parser.getParameter("realPayAmt","0");
			String p_idx		= parser.getParameter("p_idx","");
			String evnt_seq_no	= parser.getParameter("evnt_seq_no","");
			String evnt_nm		= parser.getParameter("evnt_nm","");
			String reg_aton		= parser.getParameter("reg_aton","");
			String status		= parser.getParameter("status","");
			String email		= parser.getParameter("email","");
			String hp_ddd_no	= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno	= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno	= parser.getParameter("hp_tel_sno","");

			//금액의 ,부분 삭제
			realPayAmt = realPayAmt.trim();
			
			// 결제 jsp 에서 사용
			Random rand = new Random();
		    String st = String.valueOf( rand.nextInt(99999999) );
		    session.setAttribute("ParameterManipulationProtectKey",st);
			
		    // 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SESS_CSTMR_ID", userId);
			
			// 04.실제 테이블(Proc) 조회
			GolfPaymentInqDaoProc proc = (GolfPaymentInqDaoProc)context.getProc("GolfPaymentInqDaoProc");
		    
			//나의 포인트정보 가져오기 jolt
			if(juminno != null && !"".equals(juminno)) {
				myPointResult = (int)proc.getMyPointInfo(context, request, juminno);
			}
			
			//날짜셋팅
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
			String nowDate = String.valueOf(cal.get(Calendar.DATE));
			
			
			// 05.모든 파라미터값을 맵에 담아 반환한다.	
			paramMap.put("p_idx", p_idx);
			paramMap.put("evnt_seq_no", evnt_seq_no);
			paramMap.put("reg_aton", reg_aton);
			paramMap.put("status", status);
			paramMap.put("evnt_nm", evnt_nm);
			paramMap.put("email", email);
			paramMap.put("hp_ddd_no", hp_ddd_no);
			paramMap.put("hp_tel_hno", hp_tel_hno);
			paramMap.put("hp_tel_sno", hp_tel_sno);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("userSex", userSex);
			paramMap.put("nowMonth", nowMonth);
			paramMap.put("nowDate", nowDate);
			paramMap.put("myPoint", String.valueOf(myPointResult));
			paramMap.put("realPayAmt", realPayAmt);
			paramMap.put("ParameterManipulationProtectKey", st);
	        request.setAttribute("paramMap", paramMap); 	
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
