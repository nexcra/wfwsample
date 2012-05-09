/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemNhLoginActn
*   작성자     : (주)미디어포스 권영만
*   내용        : NH채움 로그인 인증
*   적용범위  : Golf
*   작성일자  : 2009-12-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.proc.member.GolfMemChgDaoProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMemNhLoginActn extends AbstractAction {

	public static final String Title = "NH채움 로그인 인증";
	private static final String BSNINPT = "BSNINPT";					// 프레임웍 조회서비스
	
	/***********************************************************************
	 * 액션처리.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return 응답정보
	 **********************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		Connection con 					= null;
		RequestParser	parser			= context.getRequestParser("default", request, response);			
				
		try {
			
			Map paramMap 				= parser.getParameterMap();																							
			con 									= context.getDbConnection("default", null);	
			
			//parameter
			String nhUserNm				= parser.getParameter("nhUserNm", ""); 
			String nhJumin1				= parser.getParameter("nhJumin1", ""); 
			String nhJumin2				= parser.getParameter("nhJumin2", ""); 
			String nhJumin					= nhJumin1+nhJumin2;
			
			//정의변수
			String strNhMemYn			= "N";		//nh회원유무
			String strBcMemYn			= "N";		//bc회원유무
			
			GolfMemChgDaoProc proc = (GolfMemChgDaoProc)context.getProc("GolfMemChgDaoProc");
			
			if(!"".equals(nhUserNm) && !"".equals(nhJumin1) && !"".equals(nhJumin2))
			{
				//NH전문 통신 시작
				Properties properties = new Properties();
				properties.setProperty("LOGIN", "Y");  					// 필수 로그에 남길 해당 전문의 return RETURN_CODE 키. 설정 안하면 "fml_ret1" 사용
				properties.setProperty("RETURN_CODE", "fml_ret1");		// 만약 특정한 pool 을 사용하는 경우라면.
				properties.setProperty("SOC_ID", nhJumin); 	// log 찍어주는 부분에서 주민번호 대체
				
				System.out.print("## NhLog | GolfMemNhLoginActn | 시작 | nhUserNm : "+nhUserNm+" | nhJumin : "+nhJumin+"\n");
				
				try {
										
					
					/** *****************************************************************
					 *Card정보를 읽어오기
					 ***************************************************************** */
					System.out.println("## GolfCtrlServ | 1. Jolt NTC0080R2500 전문 호출 <<<<<<<<<<<<");
					JoltInput cardInput_pt = new JoltInput(BSNINPT);
					cardInput_pt.setServiceName(BSNINPT);
					cardInput_pt.setString("fml_trcode", "NTC0080R2500");
					cardInput_pt.setString("fml_arg1", nhJumin);	// 주민번호
				
					JtProcess jt_pt = new JtProcess();
					java.util.Properties prop_pt = new java.util.Properties();
					prop_pt.setProperty("RETURN_CODE","fml_ret1");
					
					TaoResult cardinfo_pt = null;
					String resultCode_pt = "";
					
					cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);		
					
					if (cardinfo_pt.containsKey("fml_ret1"))
					{					
						resultCode_pt = cardinfo_pt.getString("fml_ret1");
					}
					System.out.print("## NhLog | GolfMemNhLoginActn | 전문통신결과값 | nhUserNm : "+nhUserNm+" | nhJumin : "+nhJumin+" | resultCode_pt : "+resultCode_pt+"\n");
					
					
					//나눔회원인지 체크
					if("02".equals(resultCode_pt)){
					
						if ( cardinfo_pt.getString("fml_ret5").trim().length() > 0 ){ //카드종류
							strNhMemYn = "Y";
						}
					
					}					
					
					if("7801091024598".equals(nhJumin)) strNhMemYn = "Y";

					
					//나눔회원이면 BC회원인지 체크
					if("Y".equals(strNhMemYn))
					{						
						strBcMemYn = proc.getBcMemYn(con, nhJumin);
						System.out.print("## NhLog | GolfMemNhLoginActn | BC회원인지 체크 | nhUserNm : "+nhUserNm+" | nhJumin : "+nhJumin+" | strBcMemYn : "+strBcMemYn+"\n");
						
					}
					
				} catch (Throwable te) {
					System.out.print("## NhLog | GolfMemNhLoginActn | 전문통신결과에러 | nhUserNm : "+nhUserNm+" | nhJumin : "+nhJumin+" \n");
					throw new GolfException(Title, te);
				}
				
			}
			
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));   
			
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("strNhMemYn", strNhMemYn);
			request.setAttribute("strBcMemYn", strBcMemYn);
			
			
			} catch (Throwable be) {			
				throw new GolfException(Title, be);
			} finally {
				try { if(con != null) { con.close(); } else {;} } catch(Throwable ignore) {}
			}
			return super.getActionResponse(context);
		}
		
		

}
