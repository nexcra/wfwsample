/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEgolfExpoRegActn
*   작성자    : shin cheong gwi
*   내용      : 이데일리 골프 엑스포 온라인 사전등록 
*   적용범위  : Golf
*   작성일자  : 2012-04-03
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.mania;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfEgolfExpoRegActn extends GolfActn {

	public static final String TITLE = "이델리일 골프엑스포  온라인 사전등록";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException
	{
		String subpage_key = "default";
				
		// 레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String errReUrl = super.getActionParam(context, "errReUrl"); 
		request.setAttribute("layout", layout);
		
		try
		{			
			// 입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String golf_svc_aplc_clss = parser.getParameter("golf_svc_aplc_clss", "0013");		// 이데일리 골프 엑스포 구분코드
			String usrNm = parser.getParameter("usrNm", "");	
			String usrSex = parser.getParameter("usrSex", "");
			String usrBirth = parser.getParameter("usrBirth", "");
			usrBirth = usrBirth.replaceAll("[-]", "");
			String usrJob = parser.getParameter("usrJob", "");
			String usr_ddd_no = parser.getParameter("usr_ddd_no", "");
			String usr_tel_hno = parser.getParameter("usr_tel_hno", "");
			String usr_tel_sno = parser.getParameter("usr_tel_sno", "");				
			String usr_hp_ddd_no = parser.getParameter("usr_hp_ddd_no", "");
			String usr_hp_hno = parser.getParameter("usr_hp_hno", "");
			String usr_hp_sno = parser.getParameter("usr_hp_sno", "");
			String usr_email = parser.getParameter("usr_email", "");
			String usr_zip1 = parser.getParameter("zip_code1", "");
			String usr_zip2 = parser.getParameter("zip_code2", "");
			String usr_zip = usr_zip1 + usr_zip2;
			String usr_addr = parser.getParameter("zipaddr", "");
			String usr_dtl_addr = parser.getParameter("detailaddr", "");
			String usrId = parser.getParameter("usrId", "");
			String usr_cdhd_gn = !usrId.equals("") ? "2" : "1";
						
			// Proc 에 던질 값 세팅
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("golf_svc_aplc_clss", golf_svc_aplc_clss);		
				dataSet.setString("usrNm", usrNm);
				dataSet.setString("usrSex", usrSex);					
				dataSet.setString("usrBirth", usrBirth);
				dataSet.setString("usrJob", usrJob);
				dataSet.setString("usr_ddd_no", usr_ddd_no);			
				dataSet.setString("usr_tel_hno", usr_tel_hno);
				dataSet.setString("usr_tel_sno", usr_tel_sno);
				dataSet.setString("usr_hp_ddd_no", usr_hp_ddd_no);		
				dataSet.setString("usr_hp_hno", usr_hp_hno);
				dataSet.setString("usr_hp_sno", usr_hp_sno);
				dataSet.setString("usr_email", usr_email);
				dataSet.setString("usr_zip", usr_zip);
				dataSet.setString("usr_addr", usr_addr);
				dataSet.setString("usr_dtl_addr", usr_dtl_addr);
				dataSet.setString("usrId", usrId);
				dataSet.setString("usr_cdhd_gn", usr_cdhd_gn);
								
		
			debug(usrNm+"--"+usrSex+"--"+usrBirth+"--"+usrJob+"--"+usr_ddd_no+"--"+usr_tel_hno+"--"+usr_tel_sno);
			debug(usr_hp_ddd_no+"--"+usr_hp_hno+"--"+usr_hp_sno+"--"+usr_email+"--"+usr_zip1+"--"+usr_zip2);
			debug(usr_addr+"--"+usr_dtl_addr);
			
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);		
	}
}
