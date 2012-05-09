/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admGrUpdActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100513	임은혜	유료가입일 수정 추가, 완전삭제시 TM회원은 TM회원 테이블에 취소처리 업데이트
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.member.*;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmMemUpdActn extends GolfActn{

	public static final String TITLE = "관리자 회원정보 수정";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {

			// 회원통합테이블 관련 수정사항 진행
			// 02.입력값 조회한다.
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 변수설정
			String bokg_LIMT_YN	= parser.getParameter("BOKG_LIMT_YN", "").trim();
			String bokg_LIMT_FIXN_STRT_DATE	= parser.getParameter("BOKG_LIMT_FIXN_STRT_DATE", "").trim();
			String bokg_LIMT_FIXN_END_DATE	= parser.getParameter("BOKG_LIMT_FIXN_END_DATE", "").trim();
			String cdhd_ID	= parser.getParameter("CDHD_ID", "").trim();
			
			bokg_LIMT_FIXN_STRT_DATE = GolfUtil.replace(bokg_LIMT_FIXN_STRT_DATE, "-", "");
			bokg_LIMT_FIXN_END_DATE = GolfUtil.replace(bokg_LIMT_FIXN_END_DATE, "-", "");

			String mod	= parser.getParameter("MOD", "").trim();	// 업데이트 구분값 : grade => 등급 업데이트
			String grade_old	= parser.getParameter("GRADE_OLD", "").trim();	// 기존 등급
			String grade_new	= parser.getParameter("GRADE_NEW", "").trim();	// 변경 등급
			String payBack		= parser.getParameter("payBack", "N");	// 연회비 환급여부

			String jumin_no = parser.getParameter("JUMIN_NO", "");
			String acrg_cdhd_join_date = parser.getParameter("ACRG_CDHD_JONN_DATE", "");
			String acrg_cdhd_end_date = parser.getParameter("ACRG_CDHD_END_DATE", "");
			acrg_cdhd_join_date = GolfUtil.replace(acrg_cdhd_join_date, "-", "");
			acrg_cdhd_end_date = GolfUtil.replace(acrg_cdhd_end_date, "-", "");
			
	        int editResult = 0;
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("BOKG_LIMT_YN", bokg_LIMT_YN);
			dataSet.setString("BOKG_LIMT_FIXN_STRT_DATE", bokg_LIMT_FIXN_STRT_DATE);
			dataSet.setString("BOKG_LIMT_FIXN_END_DATE", bokg_LIMT_FIXN_END_DATE);
			dataSet.setString("CDHD_ID", cdhd_ID);
			dataSet.setString("GRADE_OLD", grade_old);
			dataSet.setString("GRADE_NEW", grade_new);
			dataSet.setString("payBack", payBack);
			
			dataSet.setString("JUMIN_NO", jumin_no);
			dataSet.setString("ACRG_CDHD_JONN_DATE", acrg_cdhd_join_date);
			dataSet.setString("ACRG_CDHD_END_DATE", acrg_cdhd_end_date);

			// Proc 파일 정의 
			GolfAdmMemUpdDaoProc proc = (GolfAdmMemUpdDaoProc)context.getProc("GolfAdmMemUpdDaoProc");
			debug("mod : " + mod + " / payBack : " + payBack);
			
			// 04.실제 테이블(Proc) 조회
			if("grade".equals(mod)){
				
				//  등급변경일 경우 // 등급 변경일 경우 다른 프로세스를 탄다.
				editResult = proc.execute_grade(context, dataSet);	
				
				if (editResult == 1) {
					request.setAttribute("script", "parent.location.reload();");   
					request.setAttribute("resultMsg", "등급변경이 정상적으로 처리 되었습니다.");      	
		        } else {
					request.setAttribute("resultMsg", "등급변경이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
		        }
				
			} else if("pay".equals(mod)) {

				
				//  서비스해지일 경우 
				editResult = proc.execute_pay(context, request, dataSet);		      
		        				
				if (editResult == 1) {
					request.setAttribute("script", "parent.location.reload();");   
					request.setAttribute("resultMsg", "서비스 해지가 정상적으로 처리 되었습니다.");      	
		        } else {
					request.setAttribute("resultMsg", "서비스 해지가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
		        }
				
			} else if("del".equals(mod)) {

				
				//  회원 완전삭제일 경우 
				editResult = proc.execute_del(context, request, dataSet);		      
		        				
				if (editResult == 1) {
					request.setAttribute("script", "parent.location='admMemList.do';");   
					request.setAttribute("resultMsg", "회원삭제가 정상적으로 처리 되었습니다.");      	
		        } else {
					request.setAttribute("resultMsg", "회원삭제가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
		        } 
				
			} else {
				
				// 회원정보 변경일 경우	
				editResult = proc.execute(context, dataSet);		      

		        String returnUrlTrue = "admMemList.do";
		        String returnUrlFalse = "admMemList.do";
				
				if (editResult == 1) {
					request.setAttribute("returnUrl", returnUrlTrue);
					request.setAttribute("resultMsg", "수정이 정상적으로 처리 되었습니다.");      	
		        } else {
					request.setAttribute("returnUrl", returnUrlFalse);
					request.setAttribute("resultMsg", "수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
		        }
				
			}
			
			// 05. Return 값 세팅			
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
