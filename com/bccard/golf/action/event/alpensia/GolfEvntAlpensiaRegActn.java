/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntAlpensiaRegActn
*   작성자	: (주)미디어포스 임은혜
*   내용		: 이벤트 > 알펜시아 > 부킹 신청 페이지
*   적용범위	: Golf
*   작성일자	: 2010-06-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.alpensia;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.alpensia.GolfEvntAlpensiaRegDaoProc;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfEvntAlpensiaRegActn extends GolfActn {
	
	public static final String TITLE = "이벤트 > 알펜시아 > 부킹 신청";
	
	/***************************************************************************************
	* 비씨골프 프로세스
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
			int cntJumin = 0;
			int cntHp = 0;
			int evtBnstReg = 0;		// 디비입력 결과

			String golf_svc_aplc_clss			= parser.getParameter("golf_svc_aplc_clss", "");	// 부킹 구분
			String bkg_pe_nm					= parser.getParameter("bkg_pe_nm", "");				// 예약자 이름
			String hp_ddd_no					= parser.getParameter("hp_ddd_no","");				// 핸드폰
			String hp_tel_hno					= parser.getParameter("hp_tel_hno","");				// 핸드폰
			String hp_tel_sno					= parser.getParameter("hp_tel_sno","");				// 핸드폰
			String hadc_num						= parser.getParameter("hadc_num","");				// 핸디캡수
			String trm_unt						= parser.getParameter("trm_unt","");				// 1:1박2일, 2:2박3일
			String cdhd_id						= parser.getParameter("cdhd_id","");				// 예약자 아이디
			String cdhd_grd_seq_no				= parser.getParameter("cdhd_grd_seq_no","");		// 인원수	(1:1팀 신청, 2:단체 신청)
			String rsvt_date					= parser.getParameter("rsvt_date","");				// 예약일
			String rsv_time						= parser.getParameter("rsv_time","");				// 예약시간
			rsvt_date = GolfUtil.replace(rsvt_date, "-", "");
			String cus_rmrk						= parser.getParameter("cus_rmrk","");				// 고객요청사항
			cus_rmrk = GolfUtil.replace(cus_rmrk, "* 부킹 신청시 추가 요청 사항 기재 바랍니다.", "");

			String jumin_no1					= parser.getParameter("jumin_no1", "");				// 주민등록번호
			String jumin_no2					= parser.getParameter("jumin_no2","");				// 주민등록번호
			String ddd_no						= parser.getParameter("ddd_no","");					// 전화번호
			String tel_hno						= parser.getParameter("tel_hno","");				// 전화번호
			String tel_sno						= parser.getParameter("tel_sno","");				// 전화번호
			String email						= parser.getParameter("email","");					// 이메일
			int sel_pnum						= parser.getIntParameter("sel_pnum",0);				// 인원수
			int pnum							= parser.getIntParameter("pnum",0);					// 인원수
			int tnum							= parser.getIntParameter("tnum",0);					// 팀수
			if(cdhd_grd_seq_no.equals("1"))	tnum = 1;												// 인원이 1이면 1팀으로 본다.
			String opt_yn						= parser.getParameter("opt_yn","");					// 숙소구분-옵션사용구분코드 Y:2인1실 N:4인1실
			String compn_opt_yn					= "";					// 동반자 - 숙소구분-옵션사용구분코드 Y:2인1실 N:4인1실
			String compn_bkg_pe_nm				= "";					// 동반자 - 이름
			String compn_hp_ddd_no				= "";					// 동반자 - 연락처
			String compn_hp_tel_hno				= "";					// 동반자 - 연락처
			String compn_hp_tel_sno				= "";					// 동반자 - 연락처
			for(int i=0; i<tnum; i++){
				compn_opt_yn += "||" + parser.getParameter("compn_opt_yn"+i,"");
				for(int j=0; j<pnum; j++){
					compn_bkg_pe_nm += "||" + parser.getParameter("compn"+i+"_bkg_pe_nm"+j,"");
					compn_hp_ddd_no += "||" + parser.getParameter("compn"+i+"_hp_ddd_no"+j,"");
					compn_hp_tel_hno += "||" + parser.getParameter("compn"+i+"_hp_tel_hno"+j,"");
					compn_hp_tel_sno += "||" + parser.getParameter("compn"+i+"_hp_tel_sno"+j,"");
				}
			}
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("golf_svc_aplc_clss", golf_svc_aplc_clss);
			dataSet.setString("bkg_pe_nm", bkg_pe_nm);
			dataSet.setString("trm_unt", trm_unt);
			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("cdhd_grd_seq_no", cdhd_grd_seq_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("hadc_num", hadc_num);
			dataSet.setString("rsvt_date", rsvt_date);
			dataSet.setString("rsv_time", rsv_time);
			dataSet.setString("cus_rmrk", cus_rmrk);

			dataSet.setString("jumin_no", jumin_no1+jumin_no2);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("email", email);
			dataSet.setInt("pnum", pnum);
			dataSet.setInt("tnum", tnum);
			dataSet.setString("opt_yn", opt_yn);
			dataSet.setString("compn_opt_yn", compn_opt_yn);
			dataSet.setString("compn_bkg_pe_nm", compn_bkg_pe_nm);
			dataSet.setString("compn_hp_ddd_no", compn_hp_ddd_no);
			dataSet.setString("compn_hp_tel_hno", compn_hp_tel_hno);
			dataSet.setString("compn_hp_tel_sno", compn_hp_tel_sno);
			
			
			// 04.실제 테이블(Proc) 조회
			GolfEvntAlpensiaRegDaoProc proc = (GolfEvntAlpensiaRegDaoProc)context.getProc("GolfEvntAlpensiaRegDaoProc");
//			cntJumin = (int) proc.execute_jumin(context, request, dataSet);
//			cntHp = (int) proc.execute_hp(context, request, dataSet);
			
			if(cntJumin>0){ 
				script = "alert('동일한 주민등록번호의 신청내역이 있습니다.'); history.back();";
			}else if(cntHp>0){
				script = "alert('동일한 핸드폰 번호의 신청내역이 있습니다.'); history.back();";
			}else{
				evtBnstReg = (int) proc.execute(context, request, dataSet);
				

				if(evtBnstReg>0){
					script = "alert('신청등록이 처리되었습니다.'); parent.location.reload();";
				}else{
					script = "alert('신청등록이 처리되지 않았습니다. 다시 시도해 주시기 바랍니다.');";
				}
			}
			
			
			request.setAttribute("script", script);
			
			paramMap.remove("cdhd_grd_seq_no");
			paramMap.remove("sel_pnum");
			paramMap.remove("agree_yn");
	        request.setAttribute("paramMap", paramMap);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}