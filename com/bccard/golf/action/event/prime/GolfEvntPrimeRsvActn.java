/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntPrimeIns
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 결제처리 
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.prime;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.prime.GolfEvntPrimeRsvDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntPrimeRsvActn extends GolfActn{
	
	public static final String TITLE = "쇼핑 리스트";

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

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
		
		try { 

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
							

			// 골프이벤트신청

			String mod							= parser.getParameter("mod","ins");
			int aplc_seq_no						= parser.getIntParameter("aplc_seq_no",0);
			String cdhd_id						= parser.getParameter("cdhd_id","");
			String cdhd_non_cdhd_clss			= parser.getParameter("cdhd_non_cdhd_clss","");
			String cdhd_grd_seq_no				= parser.getParameter("cdhd_grd_seq_no","");
			String bkg_pe_nm					= parser.getParameter("bkg_pe_nm", "");
			String bkg_pe_nm_eng				= parser.getParameter("bkg_pe_nm_eng", "");
			String jumin_no1					= parser.getParameter("jumin_no1", "");
			String jumin_no2					= parser.getParameter("jumin_no2","");
			String hp_ddd_no					= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno					= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno					= parser.getParameter("hp_tel_sno","");
			String ddd_no						= parser.getParameter("ddd_no","");
			String tel_hno						= parser.getParameter("tel_hno","");
			String tel_sno						= parser.getParameter("tel_sno","");
			String rsvt_date					= parser.getParameter("rsvt_date","");
			String hadc_num						= parser.getParameter("hadc_num","");
			String note							= parser.getParameter("note",""); 
			String mgr_memo						= parser.getParameter("mgr_memo","");	
			String cus_rmrk						= parser.getParameter("cus_rmrk","");
			int comp_num						= parser.getIntParameter("comp_num",0);	
			
			rsvt_date = GolfUtil.replace(rsvt_date, ".", "");
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("mod", mod);
			dataSet.setInt("aplc_seq_no", aplc_seq_no);
			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("cdhd_non_cdhd_clss", cdhd_non_cdhd_clss);
			dataSet.setString("cdhd_grd_seq_no", cdhd_grd_seq_no);
			dataSet.setString("bkg_pe_nm", bkg_pe_nm);
			dataSet.setString("bkg_pe_nm_eng", bkg_pe_nm_eng);
			dataSet.setString("jumin_no1", jumin_no1);
			dataSet.setString("jumin_no2", jumin_no2);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);			
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);			
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("rsvt_date", rsvt_date);
			dataSet.setString("hadc_num", hadc_num);
			dataSet.setString("note", note);
			dataSet.setString("mgr_memo", mgr_memo);
			dataSet.setString("cus_rmrk", cus_rmrk);
			dataSet.setInt("comp_num", comp_num);
			for(int i=1; i<4; i++){
				dataSet.setString("comp_bkg_pe_nm_"+i, parser.getParameter("comp_bkg_pe_nm_"+i,""));
			}
			
			// 04.실제 테이블(Proc) 조회

			int addResult = 0;
			String script = "";
			String msg = "";
			String msg_url = "GolfEvntPrimeSchForm.do";
			
			// 주문정보 저장
			GolfEvntPrimeRsvDaoProc proc = (GolfEvntPrimeRsvDaoProc)context.getProc("GolfEvntPrimeRsvDaoProc");
			addResult = proc.execute_rsv(context, request, dataSet);	// 1 time	
			debug("GolfEvntPrimeRsvDaoProc = addResult : " + addResult);	
			
			if(addResult>0){
				if(mod.equals("ins")){
					msg = "예약이 정상적으로 접수되었습니다.";
					msg_url = "GolfEvntPrimeRsvForm.do";
				}else if(mod.equals("upd")){
					msg = "예약상태 수정이 정상적으로 처리되었습니다.";
				}else if(mod.equals("del")){
					msg = "예약취소가 정상적으로 접수되었습니다.";
				}
				
				script = "alert('"+msg+"'); parent.location.href='/app/golfloung/GolfEvntPrimeSchForm.do'";
			}else{
				script = "alert('처리과정에 오류가 있었습니다. 다시 시도해 주시기 바랍니다.'); parent.location.href='"+msg_url+"'";
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
