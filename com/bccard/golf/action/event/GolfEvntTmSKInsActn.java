/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntTmMovieCpnPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > TM 영화 예매권 이벤트 > 쿠폰번호 출력 팝업
*   적용범위  : Golf
*   작성일자  : 2010-03-19
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
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntTmSKInsProc;
import com.bccard.golf.dbtao.proc.event.tmMovie.GolfEvntTmMovieProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	JSEUN
* @version	1.0
******************************************************************************/
public class GolfEvntTmSKInsActn extends GolfActn{
	
	public static final String TITLE = "이벤트 > TM 영화 예매권 이벤트 > 쿠폰번호 출력 팝업";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보.  
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		DbTaoResult  result =  new DbTaoResult("");

		String userSocid = "";
		String userNm = "";
		String userEmail = "";
		String msg = "";

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			 if(usrEntity != null) {
				 userSocid 	= (String)usrEntity.getSocid();
				 userNm 	= (String)usrEntity.getName();
				 userEmail 	= (String)usrEntity.getEmail1();
			 }
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String ddd_no 		= parser.getParameter("ddd_no", "");
			String hp_ddd_no 	= parser.getParameter("hp_ddd_no", "");
			String hp_tel_hno 	= parser.getParameter("hp_tel_hno", "");
			String hp_tel_sno 	= parser.getParameter("hp_tel_sno", "");
			String cdhd_id 		= parser.getParameter("cdhd_id", "");
			String hp 			= hp_ddd_no + "-" + hp_tel_hno + "-" + hp_tel_sno;
			
//			debug("ddd_no : " + ddd_no + " / hp_ddd_no : " + hp_ddd_no + " / hp_tel_hno : " + hp_tel_hno + " / hp_tel_sno : " + hp_tel_sno + " / cdhd_id : " + cdhd_id);

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno"	, hp_tel_hno);
			dataSet.setString("hp_tel_sno"	, hp_tel_sno);
			dataSet.setString("cdhd_id"	, cdhd_id);
			
			// 04.실제 테이블(Proc) 조회
			GolfEvntTmSKInsProc proc = (GolfEvntTmSKInsProc)context.getProc("GolfEvntTmSKInsProc");

			// 쿠폰번호
			String bcd_hp_ddd_no = "";
			String bcd_hp_tel_hno = "";
			String bcd_hp_tel_sno = "";
			String bcd_hp = "";
			String cp_bcd = "";
			
			DbTaoResult bcdResult = proc.get_cp_bcd(context, request, dataSet);
			if(bcdResult!=null && bcdResult.isNext()){
				bcdResult.next();
				cp_bcd = bcdResult.getString("CO_NM");
				bcd_hp_ddd_no = bcdResult.getString("HP_DDD_NO");
				bcd_hp_tel_hno = bcdResult.getString("HP_TEL_HNO");
				bcd_hp_tel_sno = bcdResult.getString("HP_TEL_SNO");
				bcd_hp = bcd_hp_ddd_no + "-" + bcd_hp_tel_hno + "-" + bcd_hp_tel_sno;
			}
			dataSet.setString("cp_bcd"	, cp_bcd);	
//			debug("cp_bcd : " + cp_bcd);
			
			if(!GolfUtil.empty(cp_bcd) && !hp.equals(bcd_hp)){
				msg = "이미 " + bcd_hp + " 번으로 신청 하셨습니다.";
			}else{
			
				// 통신
				String return_code = "";		// 리턴 코드
				String return_msg = "";			// 오류 메세지
				String return_coupon = "";		// 쿠폰 번호
				int aplIns = 0;					// 신청 테이블 저장 여부
				
				DbTaoResult coupResult = proc.sendCoup(context, request, dataSet);
				if(coupResult!=null && coupResult.isNext()){
					coupResult.next();
					return_code = coupResult.getString("return_code");
					return_msg = coupResult.getString("return_msg");
					return_coupon = coupResult.getString("return_coupon");
					aplIns = coupResult.getInt("aplIns");
				}
				
				debug("return_code : " + return_code + " / return_msg : " + return_msg + " / return_coupon : " + return_coupon + " / aplIns : " + aplIns);
						
				
				
				// 리턴 페이지 지정
				if(return_code.equals("00")){
					// 정상
					if(GolfUtil.empty(cp_bcd)){
						msg = "전송이 완료 되었습니다.";
					}else{
						msg = "재전송이 완료 되었습니다.";
					}
					
					// 사은품을 등록해준다.
					GolfEvntTmMovieProc proc_tmMovie = (GolfEvntTmMovieProc)context.getProc("GolfEvntTmMovieProc");
	
					result.addString("cupn", return_coupon);
					result.addString("pwin_grd", "1");
	
					dataSet.setString("tm_evt_no"	, "119");
					dataSet.setString("socid"		, userSocid);
					dataSet.setString("userNm"		, userNm);
					dataSet.setString("email"		, userEmail);							
					
					
					synchronized(this) {	// 동시 유저 발생시 같은 max 값 얻어오는걸 방지
						int cupnTmMovie = (int) proc_tmMovie.getDplCheck(context, request, dataSet);
						if(cupnTmMovie==0){
							int doUpdate = proc_tmMovie.insertCupnNumberSK(context, request, dataSet, result);
						}
					}
					
				}else{
					msg = "정상적으로 처리 되지 않았습니다. ";	
					msg += "["+return_msg+"]";
				}
			}

			String script = "alert('"+msg+"');";	
			script += "top.window.close();";			

//			debug("script : " + script);
							
			
			// 05. Return 값 세팅
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t); 
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
