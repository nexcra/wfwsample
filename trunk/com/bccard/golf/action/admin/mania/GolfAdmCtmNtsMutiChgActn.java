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

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.mania.GolfAdmCtmNtsMutiUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


/******************************************************************************
* Topn
* @author (주)만세커뮤니케이션
* @version 1.0
******************************************************************************/
public class GolfAdmCtmNtsMutiChgActn extends GolfActn{
 
 public static final String TITLE = "관리자 골프장리무진할인신청관리 수정처리";

 /***************************************************************************************
  * 비씨탑포인트 관리자화면
  * @param context  WaContext 객체. 
  * @param request  HttpServletRequest 객체. 
  * @param response  HttpServletResponse 객체. 
  * @return ActionResponse Action 처리후 화면에 디스플레이할 정보. 
  ***************************************************************************************/
 
 public ActionResponse execute( WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

  String subpage_key = "default"; 
  GolfAdminEtt userEtt = null;
  String admin_no = "";
  
  // 00.레이아웃 URL 저장
  String layout = super.getActionParam(context, "layout");
  request.setAttribute("layout", layout);

  try {
   // 01.세션정보체크
     HttpSession session = request.getSession(true);
     userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
     if(userEtt != null && !"".equals(userEtt.getMemId())){    
      admin_no = (String)userEtt.getMemNo();        
     }
    
     
     // 02.입력값 조회  
     RequestParser parser = context.getRequestParser(subpage_key, request, response);
     Map paramMap = BaseAction.getParamToMap(request);
     paramMap.put("title", TITLE);
     paramMap.remove("cidx");

     String subkey   	= parser.getParameter("subkey", "");  		// 서브메뉴구분키
     String prize_yn   	= parser.getParameter("prize_yn", "");  	// 결제진행여부
     String[] seq_no    = parser.getParameterValues("cidx", ""); 	// 일련번호
     String bbs   		= parser.getParameter("bbs", "");
     
   
     // 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
     DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
     dataSet.setString("ADMIN_NO", admin_no);
     dataSet.setString("PRIZE_YN", prize_yn);
     dataSet.setString("SUBKEY", subkey); 


     // 04.실제 테이블(Proc) 조회
     GolfAdmCtmNtsMutiUpdDaoProc proc = (GolfAdmCtmNtsMutiUpdDaoProc)context.getProc("GolfAdmCtmNtsMutiUpdDaoProc");
   
     // 리무진할인 신청 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     int editResult = 0;
   
     if (seq_no != null && seq_no.length > 0) {
      editResult = proc.execute(context, dataSet, seq_no); 
     }   
    
     // 정상처리되었으면 (처리건수와 선택건수(seq_no.length)가 같다)
   if (editResult == seq_no.length) {
	   if("0062".equals(bbs)) request.setAttribute("returnUrl", "admCtmNtsList.do?bbs=0062");
	   else request.setAttribute("returnUrl", "admCtmNtsList.do?bbs=0035");
       request.setAttribute("resultMsg", "정상적으로 처리 되었습니다."); 

   // 정상처리 되지 않았으면
   }else{
	   if("0062".equals(bbs)) request.setAttribute("returnUrl", "admCtmNtsList.do?bbs=0062");
	   else  request.setAttribute("returnUrl", "admCtmNtsList.do?bbs=0035");
	  
       request.setAttribute("resultMsg", "정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");          
   }

   // 05. Return 값 세팅   
   //paramMap.put("editResult", String.valueOf(editResult));   
   request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.   
   
  } catch(Throwable t) {

   throw new GolfException(TITLE, t);
  }
  
  return super.getActionResponse(context, subpage_key);
  
 }
}

