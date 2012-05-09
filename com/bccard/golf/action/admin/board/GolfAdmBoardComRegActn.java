/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardComRegActn
*   작성자     : (주)미디어포스 임은혜	
*   내용        : 관리자 게시판 등록 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.board;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComRegDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmGrRegDaoProc;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.msg.MsgEtt;


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
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
import com.bccard.golf.common.AppConfig;



/******************************************************************************
* Golf
* @author	(주)미디어포스   
* @version	1.0
******************************************************************************/
public class GolfAdmBoardComRegActn extends GolfActn  {
	
	public static final String TITLE = "관리자 게시판 등록 처리";
	
	/***************************************************************************************
	* 비씨골프 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		ResultException rx;
		GolfAdminEtt userEtt = null;
		String admin_id = "";

		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		//debug("==== GolfAdmBoardComRegActn start ===");
		
		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id	= (String)userEtt.getMemId(); 							
			}
			
			//1. 파라메타 값 
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();	
			
			String bbrd_clss			= parser.getParameter("boardid", "");
			String golf_bokg_faq_clss	= parser.getParameter("GOLF_BOKG_FAQ_CLSS", "");
			String titl					= parser.getParameter("TITL", "");
			String eps_yn				= parser.getParameter("EPS_YN", "");
			String golf_clm_clss		= parser.getParameter("GOLF_CLM_CLSS", "");

			////////////////////////////////////////////////////////////////////////////////////////////////
			// 나모 에디터 이미지용 설정					
			// imgPath : 실제 컨텐츠 안의 이미지가 저장되는 폴더이름
			// 게시판 마다 업로드될 이미지 폴더 imgPath 설정은 : /WEB-INF/config/config.xml 에서 추가하세요.
			String imgPath = AppConfig.getAppProperty("BK_NOTICE");
			String mapDir = AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");
			mapDir = mapDir.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			if ( mapDir == null )  mapDir = "/";
            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
           
            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH");  
            contImgPath = contImgPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
            if ( contImgPath == null ) contImgPath = "";      
           
            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
            contAtcPath = contAtcPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
            if ( contAtcPath == null ) contAtcPath = "";
 			////////////////////////////////////////////////////////////////////////////////////////////////

			String ctnt 				= parser.getParameter("CTNT", "").trim();	
			String mimeData 			= parser.getParameter("mimeData", "").trim();	
			
			/////////////////////////////////////////////////////////////////////////////////////

            
			// 나모 에디터  MIME 디코딩하기 하기
			if(!mimeData.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// 이미지조회URL 셋팅
				mime.setSavePath( contImgPath + imgPath );		// 파일 저장 경로 셋팅			
	            mime.decode(mimeData);                     		// MIME 디코딩
	            mime.saveFile();                           		// 포함한 파일 저장하기
	            ctnt = mime.getBodyContent();        	// 내용가져오기
			}
            ////////////////////////////////////////////////////////////////////////////////////

			// 첨부파일 업로드///////////////////////////////////////////////////////////////
			String tmpPath  			= AppConfig.getAppProperty("UPLOAD_TMP_PATH");	
			tmpPath = tmpPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			String realPath  			= AppConfig.getAppProperty("CONT_IMG_PATH") + imgPath;	
			realPath = realPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			String annx_file_nm 		= parser.getParameter("ANNX_FILE_NM", "").trim();		// 배너이미지
			
			if(annx_file_nm != null && !"".equals(annx_file_nm)) setFiles(tmpPath, realPath, annx_file_nm);
            ////////////////////////////////////////////////////////////////////////////////////
            
						
			//2.조회
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			dataSet.setString("BBRD_CLSS",					bbrd_clss);
			dataSet.setString("GOLF_BOKG_FAQ_CLSS",			golf_bokg_faq_clss);
			dataSet.setString("TITL",						titl);
			dataSet.setString("CTNT",						ctnt);
			dataSet.setString("EPS_YN",						eps_yn);
			dataSet.setString("ANNX_FILE_NM",				annx_file_nm);
			dataSet.setString("golf_clm_clss",				golf_clm_clss);
						
			
			// 3. DB 처리 
			GolfAdmBoardComRegDaoProc proc = (GolfAdmBoardComRegDaoProc)context.getProc("GolfAdmBoardComRegDaoProc");
			int addResult = proc.execute(context, request, dataSet);

	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	        
			debug("bbrd_clss => "+ bbrd_clss);
	        if (bbrd_clss.equals("0001")){
	        	returnUrlTrue = "admBkNotiList.do";
	        	returnUrlFalse = "admBkNotiRegForm.do";
	        }else{
	        	returnUrlTrue = "admBkFaqList.do";
	        	returnUrlFalse = "admBkFaqRegForm.do";
	        }

			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			debug("returnUrl => "+ returnUrlTrue);

			paramMap.put("BBRD_CLSS", bbrd_clss);
			paramMap.put("addResult", String.valueOf(addResult));
			request.setAttribute("paramMap", paramMap);
			

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
	/**
	 * 파일저장
	 */
	private void setFiles(String tmpPath, String realPath, String imgName)throws BaseException {

		File real_mk = new File(realPath);
		if (!real_mk.exists()) {
			real_mk.mkdirs();
		}

		File tmp = new File(tmpPath, imgName);
		File real = new File(realPath, imgName);
		File tmp_s = new File(tmpPath, "S_"+imgName);
		File real_s = new File(realPath, "S_"+imgName);
			
		// 템프에서 리얼디렉토리로 이동
		tmp.renameTo(real);
		tmp_s.renameTo(real_s);
	}	
}
