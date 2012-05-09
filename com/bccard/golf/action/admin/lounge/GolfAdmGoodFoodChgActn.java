/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGoodFoodChgActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 맛집 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lounge;

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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmGoodFoodUpdDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmGoodFoodChgActn extends GolfActn{
	
	public static final String TITLE = "관리자 맛집 수정 처리";

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

			long fd_seq_no = parser.getLongParameter("p_idx", 0L);// 일련번호
			
			String gf_seq_no = parser.getParameter("gf_seq_no", "");	// 골프장코드
			String fd_nm = parser.getParameter("fd_nm", "");	// 맛집명
			String new_yn = parser.getParameter("new_yn", "");	// 새글여부
			String best_yn = parser.getParameter("best_yn", "");	// 베스트여부
			String fd_area_cd = parser.getParameter("fd_area_cd", "");	// 지역코드
			String region_green_nm = parser.getParameter("region_green_nm", "");	// 골프장직접입력
			String fd1_lev_cd = parser.getParameter("fd1_lev_cd", "");	// 음식1차분류코드
			String fd2_lev_cd = parser.getParameter("fd2_lev_cd", "");	// 음식2차분류코드
			String fd3_lev_cd = parser.getParameter("fd3_lev_cd", "");	// 음식3차분류코드
			String img_nm = parser.getParameter("img_nm", "");	// 맛집이미지
			String zipcode1 = parser.getParameter("zipcode1", "");	// 우편번호1
			String zipcode2 = parser.getParameter("zipcode2", "");	// 우편번호2
			String zipaddr = parser.getParameter("zipaddr", "");	// 주소
			String detailaddr = parser.getParameter("detailaddr", "");	// 세부주소
			String addr_clss = parser.getParameter("addr_clss"); //주소구분(구:1, 신:2)
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// 전화DDD번호
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// 전화국번호
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// 전화일련번호
			String road_srch = parser.getParameter("road_srch", "");	// 찾아오시는길
			String map_nm = parser.getParameter("map_nm", "");	// 약도이미지
			String parking_yn = parser.getParameter("parking_yn", "");	// 주차가능여부
			String start_hh = parser.getParameter("start_hh", "");	// 영업시간 시1
			String start_mi = parser.getParameter("start_mi", "");	// 영업시간 분1
			String end_hh = parser.getParameter("end_hh", "");	// 영업시간 시2
			String end_mi = parser.getParameter("end_mi", "");	// 영업시간 분2
			String sls_end_day = parser.getParameter("sls_end_day", "");	// 휴무일
			String url = parser.getParameter("url", "");	// 홈페이지
			String fd_menu = parser.getParameter("fd_menu", "");	// 주요메뉴
			String ctnt = parser.getParameter("ctnt", "");	// 내용
			String main_banner_img = parser.getParameter("main_banner_img", "");	// 메인배너이미지
			String main_banner_url = parser.getParameter("main_banner_url", "");	// 메인배너URL
			String main_eps_yn = parser.getParameter("main_eps_yn", "N");	// 메인노출여부
			String main_rprs_img = parser.getParameter("main_rprs_img", "");	// 메인대표이미지
			String main_rprs_img_url = parser.getParameter("main_rprs_img_url", "");	// 메인대표이미지URL
			String main_rprs_img_eps_yn = parser.getParameter("main_rprs_img_eps_yn", "N");	// 메인대표이미지노출여부
			
			String orgImg_nm = parser.getParameter("orgImg_nm", "");	// 기존 맛집이미지
			String orgMap_nm = parser.getParameter("orgMap_nm", "");	// 기존 약도이미지
			String orgMain_banner_img = parser.getParameter("orgMain_banner_img", "");	// 기존 메인배너이미지
			String orgMain_rprs_img = parser.getParameter("orgMain_rprs_img", "");	// 기존 메인대표이미지			
			
			String zipcode = zipcode1 + zipcode2;
			String sls_strt_time = start_hh + start_mi;
			String sls_end_time = end_hh + end_mi;
			
			//========================== 파일 업로드 Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "/lounge";

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
			
			if ( !GolfUtil.isNull(main_rprs_img)) {
                File tmp = new File(tmpPath,main_rprs_img);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = main_rprs_img.substring(0, main_rprs_img.lastIndexOf('.'));
                    String ext = main_rprs_img.substring(main_rprs_img.lastIndexOf('.'));

                    File listAttch = new File(createPath, main_rprs_img);
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
			dataSet.setLong("FD_SEQ_NO", fd_seq_no);

			dataSet.setString("GF_SEQ_NO", gf_seq_no);
			dataSet.setString("FD_NM", fd_nm);
			dataSet.setString("NEW_YN", new_yn);
			dataSet.setString("BEST_YN", best_yn);
			dataSet.setString("FD_AREA_CD", fd_area_cd);
			dataSet.setString("REGION_GREEN_NM", region_green_nm);
			dataSet.setString("FD1_LEV_CD", fd1_lev_cd);
			dataSet.setString("FD2_LEV_CD", fd2_lev_cd);
			dataSet.setString("FD3_LEV_CD", fd3_lev_cd);
			dataSet.setString("IMG_NM", img_nm);
			dataSet.setString("ZIPCODE", zipcode);
			dataSet.setString("ZIPADDR", zipaddr);
			dataSet.setString("DETAILADDR", detailaddr);
			dataSet.setString("ADDR_CLSS", addr_clss);			
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("ROAD_SRCH", road_srch);
			dataSet.setString("MAP_NM", map_nm);
			dataSet.setString("PARKING_YN", parking_yn);
			dataSet.setString("SLS_STRT_TIME", sls_strt_time);
			dataSet.setString("SLS_END_TIME", sls_end_time);
			dataSet.setString("SLS_END_DAY", sls_end_day);
			dataSet.setString("URL", url);
			dataSet.setString("FD_MENU", fd_menu);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("MAIN_BANNER_IMG", main_banner_img);
			dataSet.setString("MAIN_BANNER_URL", main_banner_url);
			dataSet.setString("MAIN_EPS_YN", main_eps_yn);
			dataSet.setString("MAIN_RPRS_IMG", main_rprs_img);
			dataSet.setString("MAIN_RPRS_IMG_URL", main_rprs_img_url);
			dataSet.setString("MAIN_RPRS_IMG_EPS_YN", main_rprs_img_eps_yn);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmGoodFoodUpdDaoProc proc = (GolfAdmGoodFoodUpdDaoProc)context.getProc("GolfAdmGoodFoodUpdDaoProc");
			
			// 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
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
				if ( !GolfUtil.isNull(main_banner_img) && !GolfUtil.isNull(orgMain_banner_img)) {
                    File realAttch = new File(realPath + subDir, orgMain_banner_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				if ( !GolfUtil.isNull(main_rprs_img) && !GolfUtil.isNull(orgMain_rprs_img)) {
                    File realAttch = new File(realPath + subDir, orgMain_rprs_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admGoodFoodList.do");
				request.setAttribute("resultMsg", "맛집 수정이 정상적으로 처리 되었습니다.");      	
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
				
				if ( !GolfUtil.isNull(main_rprs_img)) {
                    File tmpAttch = new File(tmpPath, main_rprs_img);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, main_rprs_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admGoodFoodChgForm.do");
				request.setAttribute("resultMsg", "맛집 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
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
