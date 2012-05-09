/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaChgActn
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 수정처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.mania;

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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.mania.GolfAdmManiaUpdDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmManiaChgActn extends GolfActn{
	
	public static final String TITLE = "관리자 골프장리무진할인신청관리 수정처리";

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
		String admin_id = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				admin_id	= (String)userEtt.getMemId();		
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			long seq_no	= parser.getLongParameter("p_idx", 0L);				// 일련번호
			if(seq_no == 0) seq_no	= parser.getLongParameter("cidx", 0L); 	// 결제진행/취소 수정일때 번호값처리
			String str_userid 	= parser.getParameter("cdhd_id","");
			String scoop_cp_cd 	= parser.getParameter("scoop_cp_cd", ""); 	// 0001:리무진할인 0002:골프잡지
			String prize_yn		= parser.getParameter("prize_yn", ""); 		// 결제여부
			String start_area 	= parser.getParameter("start_area", "");	// 출발장소
			String pu_date 		= parser.getParameter("pu_date", "");		// 픽업날짜
			String start_hh 	= parser.getParameter("start_hh", "");		// 픽업시간 시
			String start_mi 	= parser.getParameter("start_mi", "");		// 픽업시간 분
			String tee_date 	= parser.getParameter("tee_date", "");		// 티오프날짜
			String end_hh 		= parser.getParameter("end_hh", "");		// 티오프시간 시
			String end_mi 		= parser.getParameter("end_mi", "");		// 티오프시간 분
			String gf_nm 		= parser.getParameter("gf_nm", "");			// 골프장명
			String car_type_code= parser.getParameter("ckd_code", "");		// 차량
			String take_no 		= parser.getParameter("take_no", "");		// 승차인원
			String couns_yn 	= parser.getParameter("couns_yn", "");		// 상담여부
			String sttl_amt		= parser.getParameter("sttl_amt", ""); 		// 결제금액
			
			String zp1			= parser.getParameter("zp1", ""); 			// 우편번호
			String zp2			= parser.getParameter("zp2", ""); 			// 우편번호
			String zipaddr		= parser.getParameter("zipaddr", ""); 		// 주소
			String detailaddr	= parser.getParameter("detailaddr", ""); 	// 상세주소
			String subkey		= parser.getParameter("subkey", "");		
		
			pu_date = pu_date.length() == 10 ? DateUtil.format(pu_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			tee_date = tee_date.length() == 10 ? DateUtil.format(tee_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setLong("RECV_NO", seq_no);
			dataSet.setString("PRIZE_YN", prize_yn);
			
			dataSet.setString("STR_PLC", start_area);
			dataSet.setString("PIC_DATE", pu_date);
			dataSet.setString("START_HH", start_hh);
			dataSet.setString("START_MI", start_mi);
			dataSet.setString("TOFF_DATE", tee_date);
			dataSet.setString("END_HH", end_hh);
			dataSet.setString("END_MI", end_mi);
			dataSet.setString("GCC_NM", gf_nm);
			dataSet.setString("CKD_CODE", car_type_code);
			dataSet.setString("TK_PRS", take_no);
			dataSet.setString("CNSL_YN", couns_yn);
			dataSet.setString("STTL_AMT", sttl_amt);
			dataSet.setString("ZP1", zp1);
			dataSet.setString("ZP2", zp2);
			dataSet.setString("ADDR", zipaddr);
			dataSet.setString("ADDR2", detailaddr);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmManiaUpdDaoProc proc = (GolfAdmManiaUpdDaoProc)context.getProc("GolfAdmManiaUpdDaoProc");
			
			// 리무진할인 신청 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet);			
			
	        if (editResult == 1) {
				
	        	
	        	if (scoop_cp_cd.equals("0003")) {
	        		
	        		request.setAttribute("returnUrl", "admMagazineChgForm.do");
	        		request.setAttribute("resultMsg", "잡지구독할인 신청 프로그램 수정이 정상적으로 처리 되었습니다."); 	

	        	}else{
	        		if (!GolfUtil.isNull(prize_yn)) { 	//진행여부 (진행/취소) 수정시
	        			request.setAttribute("returnUrl", "admManiaList.do");
	        			
	        		}
	        		else{								//상담여부 (완료/대기) 수정시
	        			request.setAttribute("returnUrl", "admManiaChgForm.do");
	        		}
	        		request.setAttribute("resultMsg", "리무진할인 신청 프로그램 수정이 정상적으로 처리 되었습니다."); 
	        	}
	        
	        } else {
				request.setAttribute("returnUrl", "admManiaInq.do");
				request.setAttribute("resultMsg", "할인 신청 프로그램 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅		
	        paramMap.put("p_idx", String.valueOf(seq_no));
	        paramMap.put("cdhd_id", str_userid);
	        paramMap.put("subkey", subkey);
			paramMap.put("scoop_cp_cd", scoop_cp_cd); 
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
