/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCertCheck
*   작성자    : 이경희
*   내용      : TOP골프카드 전용 부킹> 공용카드 인증
*   적용범위  : Golf
*   작성일자  : 2010-11-18
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCertPorc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


public class GolfTopGolfCertActn extends GolfActn{
	

	/**
	 * 
	 */	
	public static final String TITLE = "공용카드 인증 ";

	/***************************************************************************************	
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) 
								throws IOException, ServletException, BaseException {

		String subpage_key = "default";		

		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		String certId = "", certBizNo = "", certJuminNo = "", updateData = ""; //인증여부 확인 플래그
		
		try {
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			certId = parser.getParameter("hId");
			certBizNo = parser.getParameter("hBizNo");
			certJuminNo = parser.getParameter("hJuminNo");
			
			updateData = parser.getParameter("hConfrim");
			
			if(certId != null && ( certId.equals("notNull") ||  certBizNo.equals("notNull") 
					||  certJuminNo.equals("notNull") ||  updateData.equals("OK") )){
				subpage_key = "certIfr";
			}

			// Proc 에 넘겨줄 파라메터
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			// 공용카드 인증
			GolfTopGolfCertPorc proc = (GolfTopGolfCertPorc)context.getProc("GolfTopGolfCertPorc");
			
			//아이디 인증
			if(certId != null && certId.equals("notNull") ){
				
				dataSet.setString("ID", parser.getParameter("id"));				
				dataSet.setString("GUBUN", "ID");
				
				String checkVal = (String)proc.getStateCheck(context, request, dataSet);	
				
				if (checkVal.equals("PRIVATE")){//개인회원
					request.setAttribute("certId", "PRIVATE");
				}else if (checkVal.equals("JIJUNG")){//지정카드
					request.setAttribute("certId", "JIJUNG");
				}else { //공용카드
					int cnt = (int)proc.getCount(context, request, dataSet);				
					if(cnt > 0){
						request.setAttribute("certId", "OK");	
					}
				}
			}
			 
			//사업자등록번호 인증 -- 아이디인증후  수행 (view에서 컨트롤)
			if(certBizNo != null && certBizNo.equals("notNull") ){
				
				dataSet.setString("ID", parser.getParameter("hIdV"));
				dataSet.setString("BIZ", parser.getParameter("bizNo1")+parser.getParameter("bizNo2")+parser.getParameter("bizNo3"));		
				dataSet.setString("GUBUN", "BIZ");

				int cnt = (int)proc.getCount(context, request, dataSet);
				
				if(cnt > 0){
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "OK");
					request.setAttribute("idV", parser.getParameter("hIdV"));
				}else {//사업자등록번호 인증이 안되어도 아이디 인증된 정보는 계속 갖고 있어야 함
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "notNull");
				}
			}	
						
			//주민등록번호 인증 -- 아이디/사업자등록번호 인증  후  수행 (view에서 컨트롤)
			if(certJuminNo != null && certJuminNo.equals("notNull") ){
				
				dataSet.setString("ID", parser.getParameter("hIdV"));
				dataSet.setString("BIZ", parser.getParameter("hBizNoV1")+parser.getParameter("hBizNoV2")+parser.getParameter("hBizNoV3"));
				dataSet.setString("GUBUN", "JUMIN");
			
				int cnt = (int)proc.getCount(context, request, dataSet);
			
				if(cnt > 0){
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "OK");
					request.setAttribute("certJumin", "OK");
					
				}else {//주민등록 인증이 안되어도 아이디/사업자등록 인증된 정보는 계속 갖고 있어야 함
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "OK");
					request.setAttribute("certJumin", "notNull");
				}
				
			}		
			
			//확인버튼 클릭시
			if(updateData != null && updateData.equals("OK") ){
			
				dataSet.setString("ID", parser.getParameter("hIdV"));
				dataSet.setString("JUMIN", parser.getParameter("hJuminNoV1") + parser.getParameter("hJuminNoV2"));
				int cnt = (int) proc.modifyJuminNO(context, request, dataSet);				
				
				if(cnt > 0){
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "OK");
					request.setAttribute("certJumin", "OK");
					request.setAttribute("conf", "OK");
				}
				
			}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
