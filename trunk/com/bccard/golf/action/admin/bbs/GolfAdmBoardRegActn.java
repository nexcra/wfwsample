/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardRegActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 공통게시판 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-27
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.bbs;

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
import com.bccard.golf.dbtao.proc.admin.bbs.GolfAdmBoardInsDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmBoardRegActn extends GolfActn{
	
	public static final String TITLE = "관리자 공통게시판 등록 처리";

	/***************************************************************************************
	* 골프 관리자화면
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
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
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
			
			String bbs = parser.getParameter("bbs", "");
			String field_cd = parser.getParameter("field_cd", "");
			String clss_cd = parser.getParameter("clss_cd", "");
			String sec_cd = parser.getParameter("sec_cd", "");
			String titl = parser.getParameter("titl", "");
			String ctnt = parser.getParameter("ctnt", "");									
			String id = parser.getParameter("id", "");
			String hg_nm = parser.getParameter("hg_nm", "");
			String email_id = parser.getParameter("email_id", "");
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");
			String eps_yn = parser.getParameter("eps_yn", "Y");
			String file_nm = parser.getParameter("file_nm", "");
			String pic_nm = parser.getParameter("pic_nm", "");
			String hd_yn = parser.getParameter("hd_yn", "N");
			String del_yn = parser.getParameter("del_yn", "N");
			String reg_ip_addr = request.getRemoteAddr();
			String best_yn = parser.getParameter("best_yn", "N");
			String new_yn = parser.getParameter("new_yn", "N");
			String coop_yn = parser.getParameter("coop_yn", "N");
			
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			// 나모 에디터 이미지용 설정					
			// imgPath : 실제 컨텐츠 안의 이미지가 저장되는 폴더이름
			// 게시판 마다 업로드될 이미지 폴더 imgPath 설정은 : /WEB-INF/config/config.xml 에서 추가하세요.
			String imgPath = "/bbs";
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

			// 나모 에디터  MIME 디코딩하기 하기
			if(!ctnt.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// 이미지조회URL 셋팅
				mime.setSavePath( contImgPath + imgPath );		// 파일 저장 경로 셋팅			
	            mime.decode(ctnt);                     		// MIME 디코딩
	            mime.saveFile();                           		// 포함한 파일 저장하기
	            ctnt = mime.getBodyContent();        	// 내용가져오기
			}
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setString("BBS", bbs);	
			dataSet.setString("FIELD_CD", field_cd);
			dataSet.setString("CLSS_CD", clss_cd);
			dataSet.setString("SEC_CD", sec_cd);
			dataSet.setString("TITL", titl);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("ID", id);
			dataSet.setString("HG_NM", hg_nm);
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("EPS_YN", eps_yn);
			dataSet.setString("FILE_NM", file_nm);
			dataSet.setString("PIC_NM", pic_nm);
			dataSet.setString("HD_YN", hd_yn);
			dataSet.setString("DEL_YN", del_yn);
			dataSet.setString("REG_IP_ADDR", reg_ip_addr);
			dataSet.setString("BEST_YN", best_yn);
			dataSet.setString("NEW_YN", new_yn);
			dataSet.setString("COOP_YN", coop_yn);
						
			// 04.실제 테이블(Proc) 조회
			GolfAdmBoardInsDaoProc proc = (GolfAdmBoardInsDaoProc)context.getProc("GolfAdmBoardInsDaoProc");
			int addResult = proc.execute(context, dataSet);			
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "게시물 등록이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "게시물 등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
