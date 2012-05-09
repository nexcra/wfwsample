/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 가입 > 등록
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.member.*;

import com.bccard.golf.user.entity.UcusrinfoEntity;


/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemPresentActn extends GolfActn{
	
	public static final String TITLE = "회원 > 서비스 가입 > 챔피언 사은품 선택등록";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		int result = 0;
		String script = "";
				
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String gds_code 				= parser.getParameter("gds_code", "").trim();
			String rcvr_nm 					= parser.getParameter("rcvr_nm", "").trim();
			String zp1 						= parser.getParameter("zp1", "").trim();
			String zp2 						= parser.getParameter("zp2", "").trim();
			String addr 					= parser.getParameter("addr", "").trim();
			String dtl_addr					= parser.getParameter("dtl_addr", "").trim();
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "").trim();
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "").trim();
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "").trim();

			String call_actionKey 			= parser.getParameter("call_actionKey", "");
			String code 					= parser.getParameter("code", "");
			String evnt_no 					= parser.getParameter("evnt_no", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");
			String cupn_clss 				= parser.getParameter("cupn_clss", "");

		
			String openerType 				= parser.getParameter("openerTypeRe", "").trim();
			debug("=`=`=`=`=`=`= GolfMemPresentActn : openerType : " + openerType);
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.) 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("gds_code", gds_code);	
			dataSet.setString("rcvr_nm", rcvr_nm);	
			dataSet.setString("zp", zp1+""+zp2);	
			dataSet.setString("addr", addr);		
			dataSet.setString("dtl_addr", dtl_addr);	
			dataSet.setString("hp_ddd_no", hp_ddd_no);	
			dataSet.setString("hp_tel_hno", hp_tel_hno);	
			dataSet.setString("hp_tel_sno", hp_tel_sno);

			// 04.실제 테이블(Proc) 조회
			GolfMemPresentDaoProc proc = (GolfMemPresentDaoProc)context.getProc("GolfMemPresentDaoProc");			// 회원탈퇴 프로세스
			result = proc.execute(context, dataSet, request);
			//debug("=========result : " + result);



			String returnUrlTrue = call_actionKey  + ".do" ;
			String returnUrlFalse = "GolfMemPresentForm.do";
			if(openerType.equals("U")){	// 업그레이드
				returnUrlTrue = "GolfMtGradeUpdPop.do";
			}
				        
			if (result>0) {
				//script = "pop('GolfMemJoinPop.do','CyberMoney',660,693); self.close();";
				//request.setAttribute("resultMsg", "사은품이 신청되었습니다. Champion 연회비를 결제해주시기 바랍니다.");
				//script = "opener.location.reload(); window.close()"; 
				//request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);
	        } else {
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");
				//script = "opener.location.reload(); window.close()"; 
				//request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlFalse);
	        }	
			
			// 05. Return 값 세팅
			paramMap.put("idx", "1");
			paramMap.put("code", code);
			paramMap.put("evnt_no", evnt_no);
			paramMap.put("cupn_ctnt", cupn_ctnt);
			paramMap.put("cupn_amt", cupn_amt); 
			paramMap.put("cupn_clss", cupn_clss);

			paramMap.put("actionKey", call_actionKey);
			paramMap.put("call_actionKey", call_actionKey);

	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
