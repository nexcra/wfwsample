/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBsRegFormActn
*   작성자    : (주)만세커뮤니케이션 박유빈
*   내용      : 관리자 BC Golf 이벤트 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.File;
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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSLessonRegDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmBsRegFormActn extends GolfActn{
	
	public static final String TITLE = "관리자 BC Golf 이벤트 등록 처리";

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
		String admin_no = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		debug("----------------------    in ---------------------------------------");
		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no	= (String)userEtt.getMemNo(); 							
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			long seq = parser.getLongParameter("seq", 0L);
            String evnt_nm 		= parser.getParameter("evnt_nm", "");
            String img_nm 		= parser.getParameter("evnt_img", "");
            String les_st 		= parser.getParameter("les_st", "");
            String les_en 		= parser.getParameter("les_en", "");
            String les_pay_nor 	= parser.getParameter("les_pay_nor", "");
            String les_pay_dc 	= parser.getParameter("les_pay_dc", "");
            String evnt_st 		= parser.getParameter("evnt_st", "");
            String evnt_en 		= parser.getParameter("evnt_en", "");
            String mo_pe 		= parser.getParameter("mo_pe", "");
            String ctnt 		= parser.getParameter("ctnt", "");
            String disp_yn 		= parser.getParameter("disp_yn", "");
            String evnt_bnf 	= parser.getParameter("evnt_bnf", "");
            String affi_firm 	= parser.getParameter("affi_firm", "");
            
            debug("ctnt : "+ctnt);
            
			if(!"".equals(les_st)){
				if(les_st.indexOf("-") > 0) les_st = les_st.replaceAll("-", "");
			}
			if(!"".equals(les_en)){
				if(les_en.indexOf("-") > 0) les_en = les_en.replaceAll("-", "");
			}
			if(!"".equals(evnt_st)){
				if(evnt_st.indexOf("-") > 0) evnt_st = evnt_st.replaceAll("-", "");
			}
			if(!"".equals(evnt_en)){
				if(evnt_en.indexOf("-") > 0) evnt_en = evnt_en.replaceAll("-", "");
			}
			
			debug("===================================  1===============================================");
			
			//========================== 파일 업로드 Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 	
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH");
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "/event";

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
			dataSet.setString("ADMIN_NO", admin_no);
            dataSet.setLong("EVNT_SEQ_NO", seq);
            dataSet.setString("EVNT_NM", evnt_nm);
            dataSet.setString("EVNT_ST", evnt_st);
            dataSet.setString("EVNT_EN", evnt_en);
            dataSet.setString("LES_ST", les_st);
            dataSet.setString("LES_EN", les_en);
            dataSet.setString("LES_PAY_NOR", les_pay_nor);
            dataSet.setString("LES_PAY_DC", les_pay_dc);
            dataSet.setString("IMG_NM", img_nm);
            dataSet.setString("MO_PE", mo_pe);
            dataSet.setString("CTNT", ctnt);
            dataSet.setString("DISP_YN", disp_yn); 
            dataSet.setString("EVNT_BNF", evnt_bnf);
            dataSet.setString("AFFI_FIRM", affi_firm);
            
            GolfAdmSLessonRegDaoProc proc = (GolfAdmSLessonRegDaoProc)context.getProc("GolfAdmSLessonRegDaoProc");
            
            debug("===================================  2===============================================");
			// 레슨 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            int addResult = proc.execute(context,request, dataSet,paramMap);			
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", "/app/golf/admEvntBcList.do");
				request.setAttribute("resultMsg", "레슨 BC Golf 이벤트 등록이 정상적으로 처리 되었습니다.");      	
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
				request.setAttribute("returnUrl", "/app/golf/admEvntBcRegForm.do");
				request.setAttribute("resultMsg", "레슨 BC Golf 이벤트 등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
	        debug("===================================  3===============================================");
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
