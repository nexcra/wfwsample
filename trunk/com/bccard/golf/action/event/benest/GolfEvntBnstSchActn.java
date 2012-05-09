/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntShopListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 리스트 
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
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
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstSchDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntBnstSchActn extends GolfActn{
	
	public static final String TITLE = "가평베네스트 검색";

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

		// 리턴함수
		String resultMsg = "";
		String returnUrl = "";

		
		try { 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String jumin_no1					= parser.getParameter("jumin_no1", "");
			String jumin_no2					= parser.getParameter("jumin_no2","");
			String hp_ddd_no					= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno					= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno					= parser.getParameter("hp_tel_sno","");
			
			
			debug(" / jumin_no1 : " + jumin_no1 + " / jumin_no2 : " + jumin_no2 + " / hp_ddd_no : " + hp_ddd_no
					 + " / hp_tel_hno : " + hp_tel_hno + " / hp_tel_sno : " + hp_tel_sno);
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("jumin_no", jumin_no1+jumin_no2);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			
			
			int evtBnstRegCnt = 0;			// 이벤트 등록 갯수
			String evnt_pgrs_clss = "";		// 이벤트 진행구분 코드 R:신청, A:대기, P:결제진행, B:확정, C:예약취소, E:결제취소
			String aplc_seq_no = "";		// 신청일련번호

			// 04.실제 테이블(Proc) 조회
			GolfEvntBnstSchDaoProc proc = (GolfEvntBnstSchDaoProc)context.getProc("GolfEvntBnstSchDaoProc");
			DbTaoResult evtBnstReg = (DbTaoResult) proc.execute(context, request, dataSet);
			

			resultMsg = "";
			returnUrl = "GolfEvntBnstSchForm.do";  
 
			debug("aplc_seq_no : " + aplc_seq_no + " / resultMsg : " + resultMsg + " / returnUrl : " + returnUrl);
			

			paramMap.put("juminno1", jumin_no1);
			paramMap.put("juminno2", jumin_no2);
			paramMap.put("mobile1", hp_ddd_no);
			paramMap.put("mobile2", hp_tel_hno);
			paramMap.put("mobile3", hp_tel_sno);
	        request.setAttribute("evtBnstReg", evtBnstReg);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
