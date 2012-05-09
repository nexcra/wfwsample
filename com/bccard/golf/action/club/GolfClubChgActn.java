/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubJoinRegActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 > 동호회 가입 처리
*   적용범위  : golf
*   작성일자  : 2009-07-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.club;

import java.io.File;
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.club.GolfClubMasterDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfClubChgActn extends GolfActn{
	
	public static final String TITLE = "동호회 내정보관리 수정 처리";

	/***************************************************************************************
	* 골프 사용자화면
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
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0"; 
				userNm	= "";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userNm", userNm);
			paramMap.put("userId", userId);

			String club_code = parser.getParameter("club_code", "");
			String club_nm = parser.getParameter("club_nm", "");
			String club_sbjt_ctnt = parser.getParameter("club_sbjt_ctnt", "");
			String club_img = parser.getParameter("club_img", "");
			String club_intd_ctnt = parser.getParameter("club_intd_ctnt", "");
			String club_jonn_mthd_clss = parser.getParameter("club_jonn_mthd_clss", "");

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CLUB_SEQ_NO", club_code);
			dataSet.setString("CLUB_NM", club_nm);
			dataSet.setString("CLUB_SBJT_CTNT", club_sbjt_ctnt);
			dataSet.setString("CLUB_IMG", club_img);
			dataSet.setString("CLUB_INTD_CTNT", club_intd_ctnt);
			dataSet.setString("CLUB_JONN_MTHD_CLSS", club_jonn_mthd_clss);
			dataSet.setString("CLUB_CODE", club_code);
			dataSet.setString("OPN_PE_ID", userId);

			// 04.실제 테이블(Proc) 조회			
			GolfClubMasterDaoProc proc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			DbTaoResult clubMasterResult = (DbTaoResult) proc.execute(context, dataSet);			

			// 운영자 로그인 정보..
			String clubAdmId = "";
			if (clubMasterResult != null && clubMasterResult.isNext()) {
				clubMasterResult.first();
				clubMasterResult.next();
				if (clubMasterResult.getObject("RESULT").equals("00")) {
					clubAdmId = (String)clubMasterResult.getString("OPN_PE_ID");
				}
			}
			int editResult = 0;

			//========================== 파일 업로드 Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "/club/club_img";
			
			// 운영자가 아니라면 메인페이지로 강제 이동
			if (!userId.equals(clubAdmId)) {
				subpage_key = "errReUrl";
				request.setAttribute("returnUrl", "golfClubMain.do");
				request.setAttribute("resultMsg", "");	
			} else {
				if ( !GolfUtil.isNull(club_img)) {
	                File tmp = new File(tmpPath,club_img);
	                if ( tmp.exists() ) {

	        			File createPath  =	new	File(realPath + subDir);
	        			if (!createPath.exists()){
	        				createPath.mkdirs();
	        			}

	                    String name = club_img.substring(0, club_img.lastIndexOf('.'));
	                    String ext = club_img.substring(club_img.lastIndexOf('.'));

	                    File listAttch = new File(createPath, club_img);
	                    int i=0;
	                    while ( listAttch.exists() ) {
	                    	listAttch = null;
	                    	listAttch = new File(createPath, name + String.valueOf(i) + ext );
	                        i++;
	                    }

	                    if ( tmp.renameTo(listAttch) ) {
	            			tmp.delete();
	                    }
	                }				
				}

				editResult = proc.updClubInfo(context, dataSet);	
			}
			
			
	        if (editResult == 1) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "동호회 수정이 정상적으로 처리 되었습니다.");      	
	        } else {
				//========================== 파일 업로드 Start =============================================================//
				if ( !GolfUtil.isNull(club_img)) {
                    File tmpAttch = new File(tmpPath, club_img);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, club_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "동호회 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
