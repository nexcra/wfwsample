/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmSysWidgetRegActn
*   작성자    : 미디어포스 임은혜
*   내용      : 관리자 > 시스템 > 위젯 등록
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.system.widget;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.system.widget.*;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0
******************************************************************************/
public class GolfadmSysWidgetRegActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 시스템 > 위젯 등록";

	/***************************************************************************************
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
            

			// 첨부파일 업로드///////////////////////////////////////////////////////////////
			//String imgPath 				= AppConfig.getAppProperty("WIDGET");
			String imgPath 				= "/widget";
			String tmpPath  			= AppConfig.getAppProperty("UPLOAD_TMP_PATH");	
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String realPath  			= AppConfig.getAppProperty("CONT_IMG_PATH") + imgPath;	
			realPath = realPath.replaceAll("\\.\\.","");
			String annx_file_nm			= parser.getParameter("ANNX_FILE_NM", "").trim();		// 배너이미지
			
			if(annx_file_nm != null && !"".equals(annx_file_nm)) setFiles(tmpPath, realPath, annx_file_nm);
            ////////////////////////////////////////////////////////////////////////////////////
			
			
			String eps_yn 				= parser.getParameter("EPS_YN", "").trim();
			String mvpt_annx_file_path 	= parser.getParameter("MVPT_ANNX_FILE_PATH", "").trim();
			
			//debug("EPS_YN => " + EPS_YN);
			//debug("ANNX_FILE_NM => " + ANNX_FILE_NM);
			//debug("MVPT_ANNX_FILE_PATH => " + MVPT_ANNX_FILE_PATH);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("EPS_YN", eps_yn);
			dataSet.setString("ANNX_FILE_NM", annx_file_nm);
			dataSet.setString("MVPT_ANNX_FILE_PATH", mvpt_annx_file_path);
						
			// 04.실제 테이블(Proc) 조회
			GolfadmSysWidgetRegDaoProc proc = (GolfadmSysWidgetRegDaoProc)context.getProc("GolfadmSysWidgetRegDaoProc");
			int addResult = proc.execute(context, dataSet);			
			
	        String returnUrlTrue = "admSysWidgetList.do";
	        String returnUrlFalse = "admSysWidgetRegForm.do";
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
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
