/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLessonChgActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨프로그램 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

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
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLessonUpdDaoProc;

import java.io.File;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmLessonChgActn extends GolfActn{
	
	public static final String TITLE = "관리자 레슨프로그램 수정 처리";

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

			String lsn_seq_no	= parser.getParameter("p_idx", "");// 레슨일렬번호
			String lsn_type_cd = parser.getParameter("lsn_type_cd", "");	// 레슨타입코드
			String lsn_nm = parser.getParameter("lsn_nm", "");	// 레슨명
			String evnt_yn = parser.getParameter("evnt_yn", "");	// 이벤트여부
			String img_nm = parser.getParameter("img_nm", "");	// 레슨이미지
			String lsn_prd_clss = parser.getParameter("lsn_prd_clss", "");	// 레슨기간타입
			String lsn_start_dt = parser.getParameter("lsn_start_dt", "");	// 레슨시작일
			String lsn_end_dt = parser.getParameter("lsn_end_dt", "");	// 레슨종료일
			String aplc_end_dt = parser.getParameter("aplc_end_dt", "");	// 신청마감일
			String lsn_prd_info = parser.getParameter("lsn_prd_info", "");	// 레슨기간정보
			int lsn_sttl_cst = parser.getIntParameter("lsn_sttl_cst", 0);	// 레슨정상비용
			int lsn_dc_cst = parser.getIntParameter("lsn_dc_cst", 0);	// 레슨할인비용
			String lsn_pl = parser.getParameter("lsn_pl", "");	// 장소
			String map_nm = parser.getParameter("map_nm", "");	// 약도이미지
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// 전화ddd번호
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// 전화국번호
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// 전화일련번호			
			String aplc_mthd = parser.getParameter("aplc_mthd", "");	// 신청방법
			String aplc_lete_num = parser.getParameter("aplc_lete_num", "0");	// 신청제한인원
			String lsn_intd = parser.getParameter("lsn_intd", "");	// 한줄소개
			String coop_cp_cd = parser.getParameter("coop_cp_cd", "");	// 제휴업체코드
			String coop_rmrk = parser.getParameter("coop_rmrk", "");	// 제휴안내문구
			String lsn_ctnt = parser.getParameter("lsn_ctnt", "");	// 레슨내용
			String orgImg_nm = parser.getParameter("orgImg_nm", "");	// 기존 레슨이미지
			String orgMap_nm = parser.getParameter("orgMap_nm", "");	// 기존 약도이미지
			int lsn_dc_rt = parser.getIntParameter("lsn_dc_rt", 0);	// 레슨할인률
			String main_banner_img = parser.getParameter("main_banner_img", "");	// 메인배너이미지
			String main_banner_url = parser.getParameter("main_banner_url", "");	// 메인배너URL
			String main_eps_yn = parser.getParameter("main_eps_yn", "N");	// 메인노출여부

			////////////////////////////////////////////////////////////////////////////////////////////////
			// 나모 에디터 이미지용 설정					
			// imgPath : 실제 컨텐츠 안의 이미지가 저장되는 폴더이름
			// 게시판 마다 업로드될 이미지 폴더 imgPath 설정은 : /WEB-INF/config/config.xml 에서 추가하세요.
			String imgPath = "/lesson";
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
			if(!lsn_ctnt.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// 이미지조회URL 셋팅
				mime.setSavePath( contImgPath + imgPath );		// 파일 저장 경로 셋팅			
	            mime.decode(lsn_ctnt);                     		// MIME 디코딩
	            mime.saveFile();                           		// 포함한 파일 저장하기
	            lsn_ctnt = mime.getBodyContent();        	// 내용가져오기
			}
            ////////////////////////////////////////////////////////////////////////////////////
			
			lsn_start_dt = lsn_start_dt.length() == 10 ? DateUtil.format(lsn_start_dt, "yyyy-MM-dd", "yyyyMMdd"): "";
			lsn_end_dt = lsn_end_dt.length() == 10 ? DateUtil.format(lsn_end_dt, "yyyy-MM-dd", "yyyyMMdd"): "";
			aplc_end_dt = aplc_end_dt.length() == 10 ? DateUtil.format(aplc_end_dt, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			//========================== 파일 업로드 Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 	
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			String subDir = "/lesson";

			if ( !GolfUtil.isNull(img_nm)) {
                File tmp = new File(tmpPath,img_nm);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = img_nm.substring(0, img_nm.lastIndexOf('.'));
                    String ext = img_nm.substring(img_nm.lastIndexOf('.'));

                    File listAttch = new File(createPath, img_nm);
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
			
			if ( !GolfUtil.isNull(map_nm)) {
                File tmp = new File(tmpPath,map_nm);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = map_nm.substring(0, map_nm.lastIndexOf('.'));
                    String ext = map_nm.substring(map_nm.lastIndexOf('.'));

                    File listAttch = new File(createPath, map_nm);
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
			
			if ( !GolfUtil.isNull(main_banner_img)) {
                File tmp = new File(tmpPath,main_banner_img);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = main_banner_img.substring(0, main_banner_img.lastIndexOf('.'));
                    String ext = main_banner_img.substring(main_banner_img.lastIndexOf('.'));

                    File listAttch = new File(createPath, main_banner_img);
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
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setString("LSN_SEQ_NO", lsn_seq_no);
			dataSet.setString("LSN_TYPE_CD", lsn_type_cd);
			dataSet.setString("LSN_NM", lsn_nm);
			dataSet.setString("EVNT_YN", evnt_yn);
			dataSet.setString("IMG_NM", img_nm);
			dataSet.setString("LSN_PRD_CLSS", lsn_prd_clss);
			dataSet.setString("LSN_START_DT", lsn_start_dt);
			dataSet.setString("LSN_END_DT", lsn_end_dt);
			dataSet.setString("APLC_END_DT", aplc_end_dt);
			dataSet.setString("LSN_PRD_INFO", lsn_prd_info);
			dataSet.setInt("LSN_STTL_CST", lsn_sttl_cst);
			dataSet.setInt("LSN_DC_CST", lsn_dc_cst);
			dataSet.setString("LSN_PL", lsn_pl);
			dataSet.setString("MAP_NM", map_nm);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);			
			dataSet.setString("APLC_MTHD", aplc_mthd);
			dataSet.setString("APLC_LETE_NUM", aplc_lete_num);
			dataSet.setString("LSN_INTD", lsn_intd);
			dataSet.setString("COOP_CP_CD", coop_cp_cd);
			dataSet.setString("COOP_RMRK", coop_rmrk);
			dataSet.setString("LSN_CTNT", lsn_ctnt);
			dataSet.setInt("LSN_DC_RT", lsn_dc_rt);
			dataSet.setString("MAIN_BANNER_IMG", main_banner_img);
			dataSet.setString("MAIN_BANNER_URL", main_banner_url);
			dataSet.setString("MAIN_EPS_YN", main_eps_yn);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmLessonUpdDaoProc proc = (GolfAdmLessonUpdDaoProc)context.getProc("GolfAdmLessonUpdDaoProc");
			
			// 레슨 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet);			
			
	        if (editResult == 1) {
				//========================== 파일 업로드 Start =============================================================//
				if ( !GolfUtil.isNull(img_nm) && !GolfUtil.isNull(orgImg_nm)) {
                    File realAttch = new File(realPath + subDir, orgImg_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				if ( !GolfUtil.isNull(map_nm) && !GolfUtil.isNull(orgMap_nm)) {
                    File realAttch = new File(realPath + subDir, orgMap_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admLessonList.do");
				request.setAttribute("resultMsg", "레슨 프로그램 수정이 정상적으로 처리 되었습니다.");      	
	        } else {
				//========================== 파일 업로드 Start =============================================================//
				if ( !GolfUtil.isNull(img_nm)) {
                    File tmpAttch = new File(tmpPath, img_nm);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, img_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				if ( !GolfUtil.isNull(map_nm)) {
                    File tmpAttch = new File(tmpPath, map_nm);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, map_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				
				if ( !GolfUtil.isNull(main_banner_img)) {
                    File tmpAttch = new File(tmpPath, main_banner_img);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, main_banner_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admLessonChgForm.do");
				request.setAttribute("resultMsg", "레슨 프로그램 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
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
