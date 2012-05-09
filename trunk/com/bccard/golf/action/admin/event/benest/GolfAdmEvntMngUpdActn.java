/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntMngUpdActn.java
*   작성자    :	이정규
*   내용      : 월례회 상세 보기 수정 처리
*   적용범위  : golf
*   작성일자  : 2010-09-30
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.benest;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntMngListDaoProc;
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLessonUpdDaoProc;

import java.io.File;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmEvntMngUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 이벤트 > 월례회> 월례회 수정처리";

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
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String seq_no = parser.getParameter("SEQ_NO", "");	//SEQ
			String green_nm = parser.getParameter("GREEN_NM", "");	//SEQ
			//부킹 가능일자
			String bltn_strt_date = parser.getParameter("BLTN_STRT_DATE", "").replaceAll("-", "");	//게시기간
			String bltn_end_date = parser.getParameter("BLTN_END_DATE", "").replaceAll("-", "");	// 게시기간
			String evnt_strt_date = parser.getParameter("EVNT_STRT_DATE", "").replaceAll("-", "");	// 이벤트기간
			String evnt_end_date = parser.getParameter("EVNT_END_DATE", "").replaceAll("-", "");	// 이벤트기간
			
			String cpo_amt = parser.getParameter("CPO_AMT", "");//챔피언
			String acrg_cdhd_amt = parser.getParameter("ACRG_CDHD_AMT", "");	// 유료
			String free_cdhd_amt = parser.getParameter("FREE_CDHD_AMT", "");	// 무료
			String titl_img = parser.getParameter("TITL_IMG", ""); //타이틀이미지
			String evnt_bnft_expl = parser.getParameter("EVNT_BNFT_EXPL", "");	//이벤트 혜택 설명
			
			String orgTitl_img = parser.getParameter("orgTitl_img", "");	// 기존 타이틀이미지
			
			//신청 날자
			String app_date = parser.getParameter("REG_DATE","");
			
			
			String[] app_date_arr = app_date.split("/");

			////////////////////////////////////////////////////////////////////////////////////////////////
			// 나모 에디터 이미지용 설정					
			// imgPath : 실제 컨텐츠 안의 이미지가 저장되는 폴더이름
			// 게시판 마다 업로드될 이미지 폴더 imgPath 설정은 : /WEB-INF/config/config.xml 에서 추가하세요.
			String imgPath = "/benest";
			String mapDir = AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");
			mapDir = mapDir.replaceAll("\\.\\.","");
			
			if ( mapDir == null )  mapDir = "/";
            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
           
            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH"); 
            contImgPath = contImgPath.replaceAll("\\.\\.","");
            if ( contImgPath == null ) contImgPath = "";      
           
            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
            contAtcPath = contAtcPath.replaceAll("\\.\\.","");
            if ( contAtcPath == null ) contAtcPath = "";
            
			// 나모 에디터  MIME 디코딩하기 하기
			if(!evnt_bnft_expl.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// 이미지조회URL 셋팅
				mime.setSavePath( contImgPath + imgPath );		// 파일 저장 경로 셋팅			
	            mime.decode(evnt_bnft_expl);                     		// MIME 디코딩
	            mime.saveFile();                           		// 포함한 파일 저장하기
	            evnt_bnft_expl = mime.getBodyContent();        	// 내용가져오기
			}
            ////////////////////////////////////////////////////////////////////////////////////
			
			
			//========================== 파일 업로드 Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 	
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			String subDir = "/benest"; 

			if ( !GolfUtil.isNull(titl_img)) {
                File tmp = new File(tmpPath,titl_img);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = titl_img.substring(0, titl_img.lastIndexOf('.'));
                    String ext = titl_img.substring(titl_img.lastIndexOf('.'));

                    File listAttch = new File(createPath, titl_img);
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
			
			//========================== 파일 업로드 End =============================================================//
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			//dataSet.setString("REG_DATE", seq_no);
			dataSet.setString("GREEN_NM", green_nm);
			dataSet.setString("BLTN_STRT_DATE", bltn_strt_date);
			dataSet.setString("BLTN_END_DATE", bltn_end_date);
			dataSet.setString("EVNT_STRT_DATE", evnt_strt_date);
			dataSet.setString("EVNT_END_DATE", evnt_end_date);
			dataSet.setString("CPO_AMT", cpo_amt.replaceAll(",", ""));
			dataSet.setString("ACRG_CDHD_AMT", acrg_cdhd_amt.replaceAll(",",""));
			dataSet.setString("FREE_CDHD_AMT", free_cdhd_amt.replaceAll(",",""));
			dataSet.setString("TITL_IMG", titl_img);
			dataSet.setString("EVNT_BNFT_EXPL", evnt_bnft_expl);
			
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmEvntMngListDaoProc proc = (GolfAdmEvntMngListDaoProc)context.getProc("GolfAdmEvntMngListDaoProc");
			
			//04-1 신청날짜 저장하기
			int deleteResult = 0;
			int appResult = 0;
			
			deleteResult = proc.execute_app_delete(context,dataSet);
			
			for(int i = 0 ; i < app_date_arr.length ; i++){
				dataSet.setString("REG_DATE_INDC_INFO",app_date_arr[i] );
				dataSet.setString("REG_DATE",app_date_arr[i].substring(0,10) );
				appResult = proc.excute_app_update(context,dataSet);
			}
			
			
			// 월례회 이벤트 수정 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute_update(context, dataSet);		 	
			
	        if (editResult == 1 && appResult ==1 && deleteResult  > 0 ) {
	        	debug("모두 성공!!!!!!");
				//========================== 파일 업로드 Start =============================================================//
				if ( !GolfUtil.isNull(titl_img) && !GolfUtil.isNull(orgTitl_img)) {
                    File realAttch = new File(realPath + subDir, orgTitl_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admEvntMngList.do");
				request.setAttribute("resultMsg", "해당 월례회 수정이 정상적으로 처리 되었습니다.");      	
	        } else {
				//========================== 파일 업로드 Start =============================================================//
				if ( !GolfUtil.isNull(titl_img)) {
                    File tmpAttch = new File(tmpPath, titl_img);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, titl_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admEvntMngView.do");
				request.setAttribute("resultMsg", "해당 월례회 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
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
