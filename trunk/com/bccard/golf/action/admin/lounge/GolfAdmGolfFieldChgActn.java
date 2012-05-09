/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGolfFieldChgActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 골프장 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
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
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmGolfFieldUpdDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmGolfFieldChgActn extends GolfActn{
	
	public static final String TITLE = "관리자 골프장 수정 처리";

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

			long gf_seq_no	= parser.getLongParameter("p_idx", 0L);// 일련번호
			
			/**기본정보 입력**/
			String gf_nm = parser.getParameter("gf_nm", "");	// 골프장명
			String cp_nm = parser.getParameter("cp_nm", "");	// 회사명
			String gf_clss_cd = parser.getParameter("gf_clss_cd", "");	// 구분
			String gf_hole_cd = parser.getParameter("gf_hole_cd", "");	// 홀수
			String gf_area_cd = parser.getParameter("gf_area_cd", "");	// 지역
			String open_date = parser.getParameter("open_date", "");	// 개장일
			String zipcode1 = parser.getParameter("zipcode1", "");	// 우편번호1
			String zipcode2 = parser.getParameter("zipcode2", "");	// 우편번호2
			String zipaddr = parser.getParameter("zipaddr", "");	// 주소
			String detailaddr = parser.getParameter("detailaddr", "");	// 세부주소
			String addr_clss  = parser.getParameter("addr_clss"); //주소구분(구:1, 신:2)
			String weath_cd = parser.getParameter("weath_cd", "");	// 날씨지역코드
			String subf = parser.getParameter("subf", "");	// 부대시설
			String url = parser.getParameter("url", "");	// 홈페이지
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// 전화DDD번호
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// 전화국번호
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// 전화일련번호
			String fx_ddd_no = parser.getParameter("fx_ddd_no", "");	// 팩스DDD번호
			String fx_tel_hno = parser.getParameter("fx_tel_hno", "");	// 팩스전화국번호
			String fx_tel_sno = parser.getParameter("fx_tel_sno", "");	// 팩스전화일련번호
			String gf_search = parser.getParameter("gf_search", "");	// 찾아오시는 길
			String img_nm = parser.getParameter("img_nm", "");	// 골프장이미지
			String map_nm = parser.getParameter("map_nm", "");	// 약도이미지
			String titl = parser.getParameter("titl", "");	// 제목
			String ctnt = parser.getParameter("ctnt", "");	// 내용
			
			String zipcode = zipcode1 + zipcode2;
			
			/**라운딩 정보 입력**/
			String rsv_ddd_no = parser.getParameter("rsv_ddd_no", "");	// 예약전화DDD번호
			String rsv_tel_hno = parser.getParameter("rsv_tel_hno", "");	// 예약전화국번호
			String rsv_tel_sno = parser.getParameter("rsv_tel_sno", "");	// 예약전화일련번호
			String mb_day = parser.getParameter("mb_day", "");	// 회원의날
			String sls_end_day = parser.getParameter("sls_end_day", "");	// 휴장일
			String caddie_sys = parser.getParameter("caddie_sys", "");	// 캐디시스템
			String cart_sys = parser.getParameter("cart_sys", "");	// 카트시스템
			
			/**예약 정보**/
			String mb_day_rsvt = parser.getParameter("mb_day_rsvt", "");	// 회원의날 회원
			String nmb_day_rsvt = parser.getParameter("nmb_day_rsvt", "");	// 회원의날 비회원
			String wkend_mb_rsvt = parser.getParameter("wkend_mb_rsvt", "");	// 주말 회원
			String wkend_nmb_rsvt = parser.getParameter("wkend_nmb_rsvt", "");	// 주말 비회원
			String wk_mb_rsvt = parser.getParameter("wk_mb_rsvt", "");	// 주중 회원
			String wk_nmb_rsvt = parser.getParameter("wk_nmb_rsvt", "");	// 주중 비회원
			
			/**요금 정보**/
			long grnfee_wk_mb_amt = parser.getLongParameter("grnfee_wk_mb_amt", 0L);	// 그린피 주중 회원 요금
			long grnfee_wk_nmb_amt = parser.getLongParameter("grnfee_wk_nmb_amt", 0L);	// 그린피 주중 비회원 요금
			long grnfee_wk_wmb_amt = parser.getLongParameter("grnfee_wk_wmb_amt", 0L);	// 그린피 주중 주중회원 요금
			long grnfee_wk_fmb_amt = parser.getLongParameter("grnfee_wk_fmb_amt", 0L);	// 그린피 주중 가족회원 요금
			long grnfee_wkend_mb_amt = parser.getLongParameter("grnfee_wkend_mb_amt", 0L);	// 그린피 주말 회원 요금
			long grnfee_wkend_nmb_amt = parser.getLongParameter("grnfee_wkend_nmb_amt", 0L);	// 그린피 주말 비회원 요금
			long grnfee_wkend_wmb_amt = parser.getLongParameter("grnfee_wkend_wmb_amt", 0L);	// 그린피 주말 주중회원 요금
			long grnfee_wkend_fmb_amt = parser.getLongParameter("grnfee_wkend_fmb_amt", 0L);	// 그린피 주말 가족회원 요금
			String caddie_mb_amt = parser.getParameter("caddie_mb_amt", "");	// 캐디피 회원 요금
			String caddie_nmb_amt = parser.getParameter("caddie_nmb_amt", "");	// 캐디피 비회원 요금
			String caddie_wmb_amt = parser.getParameter("caddie_wmb_amt", "");	// 캐디피 주중회원 요금
			String caddie_fmb_amt = parser.getParameter("caddie_fmb_amt", "");	// 캐디피 가족회원 요금
			String cart_mb_amt = parser.getParameter("cart_mb_amt", "");	// 카트료 회원 요금
			String cart_nmb_amt = parser.getParameter("cart_nmb_amt", "");	// 카트료 비회원 요금
			String cart_wmb_amt = parser.getParameter("cart_wmb_amt", "");	// 카트료 주중회원 요금
			String cart_fmb_amt = parser.getParameter("cart_fmb_amt", "");	// 카트료 가족회원 요금
			
			String orgImg_nm = parser.getParameter("orgImg_nm", "");	// 기존 골프장이미지
			String orgMap_nm = parser.getParameter("orgMap_nm", "");	// 기존 약도이미지
			
			
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
			//========================== 파일 업로드 End =============================================================//
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			
			dataSet.setLong("GF_SEQ_NO", gf_seq_no);
			dataSet.setString("GF_NM", gf_nm);
			dataSet.setString("CP_NM", cp_nm);
			dataSet.setString("GF_CLSS_CD", gf_clss_cd);
			dataSet.setString("GF_HOLE_CD", gf_hole_cd);
			dataSet.setString("GF_AREA_CD", gf_area_cd);
			dataSet.setString("OPEN_DATE", GolfUtil.toDateFormat(open_date));
			dataSet.setString("ZIPCODE", zipcode);
			dataSet.setString("ZIPADDR", zipaddr);
			dataSet.setString("DETAILADDR", detailaddr);
			dataSet.setString("ADDR_CLSS", addr_clss);			
			dataSet.setString("WEATH_CD", weath_cd);
			dataSet.setString("SUBF", subf);
			dataSet.setString("URL", url);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("FX_DDD_NO", fx_ddd_no);
			dataSet.setString("FX_TEL_HNO", fx_tel_hno);
			dataSet.setString("FX_TEL_SNO", fx_tel_sno);
			dataSet.setString("GF_SEARCH", gf_search);
			dataSet.setString("IMG_NM", img_nm);
			dataSet.setString("MAP_NM", map_nm);
			dataSet.setString("TITL", titl);
			dataSet.setString("CTNT", ctnt);
			
			dataSet.setString("RSV_DDD_NO", rsv_ddd_no);
			dataSet.setString("RSV_TEL_HNO", rsv_tel_hno);
			dataSet.setString("RSV_TEL_SNO", rsv_tel_sno);
			dataSet.setString("MB_DAY", mb_day);
			dataSet.setString("SLS_END_DAY", sls_end_day);
			dataSet.setString("CADDIE_SYS", caddie_sys);
			dataSet.setString("CART_SYS", cart_sys);
			
			dataSet.setString("MB_DAY_RSVT", mb_day_rsvt);
			dataSet.setString("NMB_DAY_RSVT", nmb_day_rsvt);
			dataSet.setString("WKEND_MB_RSVT", wkend_mb_rsvt);
			dataSet.setString("WKEND_NMB_RSVT", wkend_nmb_rsvt);
			dataSet.setString("WK_MB_RSVT", wk_mb_rsvt);
			dataSet.setString("WK_NMB_RSVT", wk_nmb_rsvt);
			
			dataSet.setLong("GRNFEE_WK_MB_AMT", grnfee_wk_mb_amt);
			dataSet.setLong("GRNFEE_WK_NMB_AMT", grnfee_wk_nmb_amt);
			dataSet.setLong("GRNFEE_WK_WMB_AMT", grnfee_wk_wmb_amt);
			dataSet.setLong("GRNFEE_WK_FMB_AMT", grnfee_wk_fmb_amt);
			dataSet.setLong("GRNFEE_WKEND_MB_AMT", grnfee_wkend_mb_amt);
			dataSet.setLong("GRNFEE_WKEND_NMB_AMT", grnfee_wkend_nmb_amt);
			dataSet.setLong("GRNFEE_WKEND_WMB_AMT", grnfee_wkend_wmb_amt);
			dataSet.setLong("GRNFEE_WKEND_FMB_AMT", grnfee_wkend_fmb_amt);
			dataSet.setString("CADDIE_MB_AMT", caddie_mb_amt);
			dataSet.setString("CADDIE_NMB_AMT", caddie_nmb_amt);
			dataSet.setString("CADDIE_WMB_AMT", caddie_wmb_amt);
			dataSet.setString("CADDIE_FMB_AMT", caddie_fmb_amt);
			dataSet.setString("CART_MB_AMT", cart_mb_amt);
			dataSet.setString("CART_NMB_AMT", cart_nmb_amt);
			dataSet.setString("CART_WMB_AMT", cart_wmb_amt);
			dataSet.setString("CART_FMB_AMT", cart_fmb_amt);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmGolfFieldUpdDaoProc proc = (GolfAdmGolfFieldUpdDaoProc)context.getProc("GolfAdmGolfFieldUpdDaoProc");
			
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
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admGolfFieldList.do");
				request.setAttribute("resultMsg", "골프장 수정이 정상적으로 처리 되었습니다.");      	
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
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admGolfFieldChgForm.do");
				request.setAttribute("resultMsg", "골프장 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
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
