/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmWorkBookChgActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 연습장 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

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
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmWorkBookUpdDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmWorkBookChgActn extends GolfActn{
	
	public static final String TITLE = "관리자 연습장 수정 처리";

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
			String exec_type_cd = parser.getParameter("exec_type_cd", "");	// 연습장종류코드
			String gf_nm = parser.getParameter("gf_nm", "");	// 연습장명
			String zipcode1 = parser.getParameter("zipcode1", "");	// 우편번호1
			String zipcode2 = parser.getParameter("zipcode2", "");	// 우편번호2
			String zipaddr = parser.getParameter("zipaddr", "");	// 주소
			String detailaddr = parser.getParameter("detailaddr", "");	// 상세주소
			String addr_clss = parser.getParameter("addr_clss"); //주소구분(구:1, 신:2)
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// 전화ddd번호
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// 전화국번호
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// 전화일련번호
			String img_nm = parser.getParameter("img_nm", "");	// 첨부이미지
			String url = parser.getParameter("url", "");	// 홈페이지
			String gf_search = parser.getParameter("gf_search", "");	// 찾아오시는길
			String mttr = parser.getParameter("mttr", "");	// 주의사항
			long cupn_seq_no = parser.getLongParameter("cupn_seq_no", 0L);	// 쿠폰일련번호
			
			String orgImg_nm = parser.getParameter("orgImg_nm", "");	// 기존 첨부이미지
			
			String zipcode = zipcode1 + zipcode2;
			
			//========================== 파일 업로드 Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
			realPath = realPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			tmpPath = tmpPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			String subDir = "/drivrange";

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
			//========================== 파일 업로드 End =============================================================//
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);			
			dataSet.setLong("GF_SEQ_NO", gf_seq_no);
			dataSet.setString("EXEC_TYPE_CD", exec_type_cd);
			dataSet.setString("GF_NM", gf_nm);
			dataSet.setString("ZIPCODE", zipcode);
			dataSet.setString("ZIPADDR", zipaddr);
			dataSet.setString("DETAILADDR", detailaddr);
			dataSet.setString("ADDR_CLSS", addr_clss);			
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("IMG_NM", img_nm);
			dataSet.setString("URL", url);
			dataSet.setString("GF_SEARCH", gf_search);
			dataSet.setString("MTTR", mttr);
			dataSet.setLong("CUPN_SEQ_NO", cupn_seq_no);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmWorkBookUpdDaoProc proc = (GolfAdmWorkBookUpdDaoProc)context.getProc("GolfAdmWorkBookUpdDaoProc");
			
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
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admWorkBookList.do");
				request.setAttribute("resultMsg", "연습장 수정이 정상적으로 처리 되었습니다.");      	
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
				//========================== 파일 업로드 End =============================================================//
				request.setAttribute("returnUrl", "admWorkBookChgForm.do");
				request.setAttribute("resultMsg", "연습장 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
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
