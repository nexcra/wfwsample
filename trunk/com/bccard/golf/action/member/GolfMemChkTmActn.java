/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemChkCorporationActn
*   작성자     : 미디어포스 임은혜
*   내용        : 회원 > 정회원 전환 > 폼
*   적용범위  : golf 
*   작성일자  : 2009-07-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMemChkTmActn extends GolfActn { 
	
	public static final String TITLE = "TM 메일에서 입장"; 

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		Connection con = null;
		try {
			Map paramMap = BaseAction.getParamToMap(request);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			String str_msg_enc = parser.getParameter("message", "");
			String str_msg = "";
			String strType = "";	// 전송구분값 (Y:로그인, N:탈퇴)
			String strMemId = "";
			String strMemAccount = "";
			String peCk = "";
			
			debug("## GolfMemChkCorporationActn | str_msg_enc:"+str_msg_enc+"\n");
			
			if(!("".equals(str_msg_enc) || str_msg_enc == null)){

//				StringEncrypter sender = new StringEncrypter("GOLF", "TEST");
//				str_msg_enc = sender.encrypt(str_msg_enc);
				
//				debug("## GolfMemChkCorporationActn | str_msg_enc_enc:"+str_msg_enc+"\n");

				/*받는 쪽*/
				StringEncrypter receiver = new StringEncrypter("BCCARD", "GOLF");
				str_msg = receiver.decrypt(str_msg_enc);
				debug("## GolfMemChkCorporationActn | str_msg:"+str_msg+"\n");

				// 전송구분값 추출
				strType = getSubString(str_msg,0,1).trim();
				
				// mem_id값(숫자) 추출
				strMemId = getSubString(str_msg,58,8).trim();
				
				// account값(아이디) 추출  
				strMemAccount = getSubString(str_msg,66,40).trim();	

				debug("## GolfMemChkCorporationActn | strType:" + strType + " | strMemId:" + strMemId + " | strMemAccount:" + strMemAccount + "\n");
				
			}
			
			System.out.print("## GolfMemChkCorporationActn | strType : " + strType + " | strMemId : "+strMemId+" | strMemAccount : "+strMemAccount+"\n");						
			 
			UcusrinfoEntity ucusrinfo = null;			
			
			// mem_id값 있는 경우
			if(!("".equals(strMemId) || strMemId == null)){
				
				UcusrinfoDaoProc proc = (UcusrinfoDaoProc)context.getProc("UcusrinfoDao");
				con = context.getDbConnection("default", null);
				
				ucusrinfo= proc.selectByAccountCot( con, strMemId);	// 법인회원인지 검색	
				
				if(!("".equals(strMemAccount) || strMemAccount == null)){
					peCk = proc.selectPeByCkNum( con, strMemAccount);	// 골프회원인지 검색
				}
				
			}
			
			// 로그인 
			if (strType.equals("Y")){

				// 법인 회원인 경우
				if (ucusrinfo != null) {	
					
					// 법인 회원인 경우 세션 생성
					HttpSession session = request.getSession(true); 
					UcusrinfoEntity usrEntity = (UcusrinfoEntity)session.getAttribute("COEVNT_ENTITY"); // 기본정보  | COEVNT_ENTITY 세션명을 추가했음
					 			
					// 로그인을 안 한 유저인경우 usrEntity가 null값이므로 객체 생성해줘야됨. 5566268
					if(usrEntity == null){	
						usrEntity = new UcusrinfoEntity(); 
						System.out.print("## GolfMemChkCorporationActn | usrEntity null --> 생성 작업 \n");
					}
					  
					usrEntity.setStrEnterCorporation("Y");
					usrEntity.setStrEnterCorporationMemId(strMemId);
					usrEntity.setStrEnterCorporationAccountId(strMemAccount);
					
					//세션굽기
					session.setAttribute("COEVNT_ENTITY", usrEntity);	
					
					debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | strEnterCorporation : " + usrEntity.getStrEnterCorporation());
					debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | strEnterCorporationMemId : " + usrEntity.getStrEnterCorporationMemId());
					
					// 지정카드(6)인 경우
					if("6".equals(ucusrinfo.getStrCoMemType())){					
						
						if("Y".equals(peCk)){
							//골프메인으로 이동
							debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드이고 골프회원인 경우 - 메인으로 이동");
							request.setAttribute("returnUrl", "");	
							request.setAttribute("resultMsg", "");	
							request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
							request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
						}
						else{
							debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드인데 골프회원이 아닌경우 - 가입페이지로 이동");
							request.setAttribute("returnUrl", "");	
							request.setAttribute("resultMsg", "");	
							request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
							request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.						
						}
										
					}
					else{
						debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 공용카드회원인 경우 - 알럿 처리");
						//골프메인으로 이동
						request.setAttribute("returnUrl", "");	
						//20100209 와이즈비스 삭제요청
						//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
						request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
						request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
					}
				}
				else{
					//골프메인으로 이동
					debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 기타");
					request.setAttribute("returnUrl", "");	
					request.setAttribute("resultMsg", "");	
					request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
					request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
				}
  
			}
			// 탈퇴 - 골프회원인 경우만 진행  
			else if (strType.equals("N") && peCk.equals("Y")){
				
				debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 골프회원 탈퇴 처리로 이동 | strMemAccount : " + strMemAccount);
				
				// 회원아이디 암호화
				StringEncrypter sender = new StringEncrypter("BCCARD", "GOLF");
				String strMemAccountEnc = sender.encrypt(strMemAccount);
				
				paramMap.put("memAccount", strMemAccountEnc);	
				request.setAttribute("returnUrl", "/app/golfloung/GolfMemDelOut.do");	
				request.setAttribute("resultMsg", "");	
				request.setAttribute("script", "");	
				request.setAttribute("paramMap", paramMap); 
				 
			}
						  
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
	

	public static String getSubString(String str, int startIndex, int length) { 
		
		byte[] b1 = null; 
		byte[] b2 = null; 
	
		try { 
			if (str == null) { 
				return ""; 
			} 
		
			b1 = str.getBytes(); 
			b2 = new byte[length]; 
		
			if (length > (b1.length - startIndex)) { 
				length = b1.length - startIndex; 
			} 
		
			System.arraycopy(b1, startIndex, b2, 0, length); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	
		return new String(b2); 
		
	}
	
	
}
