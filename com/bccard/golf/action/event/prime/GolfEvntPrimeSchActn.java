/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntPrimeIns
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 해외프라임 > 검색결과
*   적용범위  : Golf
*   작성일자  : 2010-08-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.prime;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.prime.GolfEvntPrimeSchDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntPrimeSchActn extends GolfActn{ 
	
	public static final String TITLE = "이벤트 > 해외프라임 > 검색결과";

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
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String userId = "";
			if(usrEntity != null) { 
				userId		= (String)usrEntity.getAccount(); 
			}
							

			// 골프이벤트검색
			String bkg_pe_nm					= parser.getParameter("bkg_pe_nm", "");
			String jumin_no1					= parser.getParameter("jumin_no1", "");
			String jumin_no2					= parser.getParameter("jumin_no2", "");
			
			
			String modeType						= parser.getParameter("modeType", "");
			
			
			if("memberCk".equals(modeType))
			{
				// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.) 
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

				dataSet.setString("bkg_pe_nm", bkg_pe_nm);
				dataSet.setString("jumin_no1", jumin_no1);
				dataSet.setString("jumin_no2", jumin_no2);
				
				GolfEvntPrimeSchDaoProc proc = (GolfEvntPrimeSchDaoProc)context.getProc("GolfEvntPrimeSchDaoProc");
				DbTaoResult aplResult = (DbTaoResult) proc.memberCk(context, request, dataSet);
		        request.setAttribute("aplResult", aplResult);
				
				
			}
			else
			{

				request.getSession().removeAttribute("primeName");
				request.getSession().removeAttribute("primeJumin1");
				request.getSession().removeAttribute("primeJumin2");
				session.setAttribute("primeName",bkg_pe_nm);
				session.setAttribute("primeJumin1",jumin_no1);
				session.setAttribute("primeJumin2",jumin_no2);

		        paramMap.put("bkg_pe_nm", bkg_pe_nm);
		        paramMap.put("jumin_no1", jumin_no1);
		        paramMap.put("jumin_no2", jumin_no2);
		        paramMap.put("jumin_no", jumin_no1+""+jumin_no2);
		        paramMap.put("userId", userId);
				
				
				// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.) 
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

				dataSet.setString("bkg_pe_nm", bkg_pe_nm);
				dataSet.setString("jumin_no1", jumin_no1);
				dataSet.setString("jumin_no2", jumin_no2);
				
				// 04.실제 테이블(Proc) 조회

				
				// 주문정보
				String aplc_seq_no = "";
				GolfEvntPrimeSchDaoProc proc = (GolfEvntPrimeSchDaoProc)context.getProc("GolfEvntPrimeSchDaoProc");
				DbTaoResult aplResult = (DbTaoResult) proc.execute(context, request, dataSet);
		        request.setAttribute("aplResult", aplResult);
		        						
				
				// 동반자 정보	        
		        String rsvt_date = "";		// 출발일
		        String hadc_num = "";		// 사용일
		        String rsvt_date2 = "";		// 도착일
		        String rsv_yn = "N";
		        
				DbTaoResult rsvResult = (DbTaoResult) proc.execute_rsv(context, request, dataSet);	
		        request.setAttribute("rsvResult", rsvResult);
		        
		        if(rsvResult.isNext()){
		        	rsvResult.first();
		        	rsvResult.next();
		        	if(rsvResult.getString("RESULT").equals("00")){
			        	rsvt_date = rsvResult.getString("rsvt_date");
			        	hadc_num = rsvResult.getString("hadc_num");
			        	rsvt_date2 = rsvResult.getString("rsvt_date2");
			        	rsv_yn = "Y";
		        	}
		        }
		        
		        paramMap.put("rsvt_date", rsvt_date);
		        paramMap.put("hadc_num", hadc_num);
		        paramMap.put("rsvt_date2", rsvt_date2);
		        paramMap.put("rsv_yn", rsv_yn);
			}
			
			
	        
	        request.setAttribute("modeType", modeType);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
