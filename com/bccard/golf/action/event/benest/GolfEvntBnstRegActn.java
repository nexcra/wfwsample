/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBnstRegActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 월례회 > 등록처리
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100524	임은혜	6월 이벤트
***************************************************************************************************/
package com.bccard.golf.action.event.benest;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstRegDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntBnstRegActn extends GolfActn{
	
	public static final String TITLE = "이벤트 > 월례회 > 등록처리";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout"); 
		request.setAttribute("layout", layout);
		
		try { 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String script = "";

			String bkg_pe_nm					= parser.getParameter("bkg_pe_nm", "");
			String jumin_no1					= parser.getParameter("jumin_no1", "");
			String jumin_no2					= parser.getParameter("jumin_no2","");
			String hp_ddd_no					= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno					= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno					= parser.getParameter("hp_tel_sno","");
			String hadc_num						= parser.getParameter("hadc_num","");
			String email						= parser.getParameter("email","");
			String chdh_non_cdhd_clss			= parser.getParameter("chdh_non_cdhd_clss","");
			String cdhd_id						= parser.getParameter("cdhd_id","");
			String cdhd_grd_seq_no				= parser.getParameter("cdhd_grd_seq_no","");
			String green_nm						= parser.getParameter("green_nm","");
			String trm_unt						= parser.getParameter("trm_unt","");		//월례회seq
			String green_id						= parser.getParameter("green_id","");
			String rsvt_date					= parser.getParameter("rsvt_date","");
			String rsv_time						= parser.getParameter("rsv_time","090000");
			String note							= parser.getParameter("note",""); 
			String cus_rmrk						= parser.getParameter("cus_rmrk","");

			int compn_no						= parser.getIntParameter("compn_no");
			String arr_compn_bkg_pe_nm			= parser.getParameter("arr_compn_bkg_pe_nm","");
			String arr_compn_hp_ddd_no			= parser.getParameter("arr_compn_hp_ddd_no","");
			String arr_compn_hp_tel_hno			= parser.getParameter("arr_compn_hp_tel_hno","");
			String arr_compn_hp_tel_sno			= parser.getParameter("arr_compn_hp_tel_sno","");
			String arr_compn_cdhd_grd_seq_no	= parser.getParameter("arr_compn_cdhd_grd_seq_no","");
			
			paramMap.remove("compn_bkg_pe_nm");
			paramMap.remove("compn_hp_ddd_no");
			paramMap.remove("compn_hp_tel_hno");
			paramMap.remove("compn_hp_tel_sno");
			paramMap.remove("compn_cdhd_grd_seq_no");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("bkg_pe_nm", bkg_pe_nm);
			dataSet.setString("chdh_non_cdhd_clss", chdh_non_cdhd_clss);
			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("jumin_no", jumin_no1+jumin_no2);
			dataSet.setString("cdhd_grd_seq_no", cdhd_grd_seq_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("email", email);
			dataSet.setString("hadc_num", hadc_num);
			dataSet.setString("green_nm", green_nm);
			dataSet.setString("trm_unt", trm_unt);		//월례회 시퀀스
			dataSet.setString("green_id", green_id);
			dataSet.setString("rsvt_date", rsvt_date.replaceAll("-",""));
			dataSet.setString("rsv_time", rsv_time);
			dataSet.setString("note", note);
			dataSet.setString("cus_rmrk", cus_rmrk);

			dataSet.setInt("compn_no", compn_no);
			dataSet.setString("arr_compn_bkg_pe_nm", arr_compn_bkg_pe_nm);
			dataSet.setString("arr_compn_hp_ddd_no", arr_compn_hp_ddd_no);
			dataSet.setString("arr_compn_hp_tel_hno", arr_compn_hp_tel_hno);
			dataSet.setString("arr_compn_hp_tel_sno", arr_compn_hp_tel_sno);
			dataSet.setString("arr_compn_cdhd_grd_seq_no", arr_compn_cdhd_grd_seq_no);
			

			// 04.실제 테이블(Proc) 조회
			GolfEvntBnstRegDaoProc proc = (GolfEvntBnstRegDaoProc)context.getProc("GolfEvntBnstRegDaoProc");
			int cntJumin = (int) proc.execute_jumin(context, request, dataSet);
			int cntHp = (int) proc.execute_hp(context, request, dataSet);
			
			
			if(cntJumin>0){ 
				script = "alert('동일한 주민등록번호의 신청내역이 있습니다.'); history.back();";
			}else if(cntHp>0){
				script = "alert('동일한 핸드폰 번호의 신청내역이 있습니다.'); history.back();";
			}else{
				int evtBnstReg = (int) proc.execute(context, request, dataSet);

				if(evtBnstReg>0){
					script = "alert('신청등록이 처리되었습니다.'); window.top.close();";
				}else{
					script = "alert('신청등록이 처리되지 않았습니다. 다시 시도해 주시기 바랍니다.'); location.href='GolfEvntBnstRegForm.do';";
				}
			}
			
			
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
