/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBnstCompnUpdActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 쇼핑 > 구매 수정 처리
*   적용범위  : golf
*   작성일자  : 2010-03-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.benest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntBnstUpdDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBnstCompnUpdActn extends GolfActn{

	public static final String TITLE = "관리자 > 이벤트 > 베네스트 > 수정 처리";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 02.입력값 조회한다.
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// 수정사항 변수
			String upd_type			= parser.getParameter("upd_type", "");
			String aplc_seq_no		= parser.getParameter("aplc_seq_no", "");
			String seq_no			= parser.getParameter("seq_no", "");
			String cdhd_grd_seq_no	= parser.getParameter("cdhd_grd_seq_no", "");
			String sttl_stat_clss	= parser.getParameter("STTL_STAT_CLSS", "");
			String evnt_pgrs_clss	= parser.getParameter("EVNT_PGRS_CLSS", "");
			String mgr_memo			= parser.getParameter("MGR_MEMO", "");
			int cnt					= parser.getIntParameter("CNT");
			String note				= parser.getParameter("note", "");
			String green_nm			= parser.getParameter("green_nm", "");
			String months			= parser.getParameter("months", "");
			String rsvt_date		= parser.getParameter("RSVT_DATE", "");
			String rsv_time			= parser.getParameter("RSV_TIME", "");
			if(!GolfUtil.empty(rsvt_date)){
				rsvt_date = GolfUtil.replace(rsvt_date, "-", "");
			}
									
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			dataSet.setString("seq_no", seq_no);
			dataSet.setString("cdhd_grd_seq_no", cdhd_grd_seq_no);
			dataSet.setString("sttl_stat_clss", sttl_stat_clss);
			dataSet.setString("evnt_pgrs_clss", evnt_pgrs_clss);
			dataSet.setString("mgr_memo", mgr_memo);
			dataSet.setString("mgr_memo", mgr_memo);
			dataSet.setInt("cnt", cnt);
			dataSet.setString("note", note);
			dataSet.setString("green_nm", green_nm);
			dataSet.setString("months", months);
			dataSet.setString("rsvt_date", rsvt_date);
			dataSet.setString("rsv_time", rsv_time);

			for(int i=1; i<20; i++){
				dataSet.setString("del_yn"+i, parser.getParameter("DEL_YN"+i, ""));
				dataSet.setString("seq_no"+i,  parser.getParameter("SEQ_NO"+i, ""));
				dataSet.setString("bkg_pe_nm"+i,  parser.getParameter("BKG_PE_NM"+i, ""));
				dataSet.setString("cdhd_grd_seq_no"+i,  parser.getParameter("CDHD_GRD_SEQ_NO"+i, ""));
				dataSet.setString("email"+i,  parser.getParameter("EMAIL"+i, ""));
				dataSet.setString("hp_ddd_no"+i,  parser.getParameter("HP_DDD_NO"+i, ""));
				dataSet.setString("hp_tel_hno"+i,  parser.getParameter("HP_TEL_HNO"+i, ""));
				dataSet.setString("hp_tel_sno"+i,  parser.getParameter("HP_TEL_SNO"+i, ""));
			}

			// Proc 파일 정의
			GolfAdmEvntBnstUpdDaoProc proc = (GolfAdmEvntBnstUpdDaoProc)context.getProc("GolfAdmEvntBnstUpdDaoProc");

			// 리턴변수
			int editResult = 0; 
			String script = "";
			String upd_type_str = "";
			String resultMsg = "";
			
			
			if(upd_type.equals("grd")){		// 등급수정 
				editResult = proc.execute_grd(context, request, dataSet);	
				upd_type_str = "등급수정";
			}else if(upd_type.equals("sttl")){	// 결제 상태
				editResult = proc.execute_sttl(context, request, dataSet);		
				upd_type_str = "결제 상태";
			}else if(upd_type.equals("evnt")){	// 참가등록 상태
				editResult = proc.execute_evnt(context, request, dataSet);		
				upd_type_str = "참가등록 상태";
			}else if(upd_type.equals("all")){	// 전체수정
				editResult = proc.execute_all(context, request, dataSet);	
				upd_type_str = "수정";		
			}else if(upd_type.equals("del")){	// 결제 취소
				editResult = proc.execute_del(context, request, dataSet);		
				upd_type_str = "결제 취소";
			}
			
			if (editResult > 0) {
				script = "parent.location.href='admEvntBnstList.do'";
				resultMsg = upd_type_str+"이(가) 정상적으로 처리 되었습니다.";   	
	        } else {
				resultMsg = upd_type_str+"이(가) 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.";		        		
	        }

			request.setAttribute("script", script);
			request.setAttribute("resultMsg", resultMsg);  
			request.setAttribute("returnUrl", "admOrdList.do");
				
			
			// 05. Return 값 세팅
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
