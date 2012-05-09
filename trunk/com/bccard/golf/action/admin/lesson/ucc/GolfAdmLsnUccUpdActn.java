/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmLsnUccDetailActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  레슨 > UCC 레슨 상세보기
*   적용범위	: golf
*   작성일자	: 2009-07-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson.ucc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.lesson.ucc.GolfAdmLsnUccUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmLsnUccUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 레슨 UCC 상세보기";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체.  
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		GolfAdminEtt userEtt = null;
		String subpage_key = "default";	
		String admId = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admId	= (String)userEtt.getMemId(); 							
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
		

			// Request 값 저장
			String bbrd_clss 	= "0022";
			String idx 			= parser.getParameter("idx","");
			String mode 		= parser.getParameter("mode","");
			String ctnt 		= parser.getParameter("ctnt","");
			String answ_ctnt    = parser.getParameter("answ_ctnt","");
			String hg_nm	    = parser.getParameter("hg_nm","");
			String eps_yn	    = parser.getParameter("eps_yn","");
			String mimeData 	= parser.getParameter("mimeData", "").trim();	// 코스소개
			
			String search_clss 	= parser.getParameter("search_clss","");
			String search_word 	= parser.getParameter("search_word","");
			String search_answ 	= parser.getParameter("search_answ","");
			String page_no		= parser.getParameter("page_no","1");
			
			//입력과 수정의 경우에만 나모에디터 코딩
			if("upd".equals(mode)){
				// 나모 에디터  MIME 디코딩하기 하기///////////////////////////////////////////////////////////////
				String imgPath 				= AppConfig.getAppProperty("BK_LSN_UCC");
				imgPath = imgPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
				String mapDir 				= AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");		
				mapDir = mapDir.replaceAll("\\.\\.","");
				if ( mapDir == null )  mapDir = "/";
	            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
	            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH"); 
	            contImgPath = contImgPath.replaceAll("\\.\\.","");
	            if ( contImgPath == null ) contImgPath = "";   
	            
	            if(!mimeData.equals("")){
		            MimeData mime = new MimeData();
					mime.setSaveURL ( mapDir + imgPath );			// 이미지조회URL 셋팅
					mime.setSavePath( contImgPath + imgPath );		// 파일 저장 경로 셋팅			
		            mime.decode(mimeData);                     		// MIME 디코딩
		            mime.saveFile();                           		// 포함한 파일 저장하기
		            ctnt = mime.getBodyContent();        			// 내용가져오기
				}
	            /////////////////////////////////////////////////////////////////////////////////////////////
			}
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("bbrd_clss", 	bbrd_clss);
			dataSet.setString("idx", 		idx);
			dataSet.setString("mode", 		mode);
			dataSet.setString("admId", 		admId);
			dataSet.setString("ctnt", 		ctnt);
			dataSet.setString("answ_ctnt", 	answ_ctnt);
			dataSet.setString("hg_nm", 		hg_nm);
			dataSet.setString("eps_yn", 	eps_yn);
			
		
			// 04.실제 테이블(Proc) 조회
			GolfAdmLsnUccUpdDaoProc proc = (GolfAdmLsnUccUpdDaoProc)context.getProc("GolfAdmLsnUccUpdDaoProc");
			DbTaoResult lessonUccResult = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("lessonUccResult", lessonUccResult);	
			
			
			// 모든 파라미터값을 맵에 담아 반환한다.		
			paramMap.put("title", TITLE);
			paramMap.put("idx", idx);	
			paramMap.put("mode", mode);		
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_word", search_word);
			paramMap.put("search_answ", search_answ);
			paramMap.put("page_no", 	page_no);
	        request.setAttribute("paramMap", paramMap); 	
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
