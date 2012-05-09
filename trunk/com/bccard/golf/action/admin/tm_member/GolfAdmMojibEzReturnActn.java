/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMojibUpdActn.java
*   작성자    : E4NET 은장선
*   내용      : 모집인 수정
*   적용범위  : Golf
*   작성일자  : 2009-09-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.initech.dbprotector.CipherClient;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext; 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.waf.common.DateUtil;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.security.cryptography.Base64Encoder;
import com.bccard.golf.dbtao.proc.admin.tm_member.GolfAdmMojibEzReturnProc;
/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfAdmMojibEzReturnActn extends GolfActn {
	
	public static final String TITLE = "모집인 수정"; 
	/***************************************************************************************
	* 비씨골프 관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		String subpage_key = "default";
		int re_result = 0;
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();		

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);	

			// 리턴 변수
			String script = "";
			String resultMsg = "";
			String returnUrl = "";

			String enc_aspOrderNum			= parser.getParameter("aspOrderNum","");
			String enc_orderNum				= parser.getParameter("orderNum","");
			String enc_result				= parser.getParameter("result","");
			
			String jumin_no1				= parser.getParameter("jumin_no1","");
			String jumin_no2				= parser.getParameter("jumin_no2","");
			String jumin_no = jumin_no1+""+jumin_no2;
			
			String aspOrderNum			= "";
			String orderNum				= "";
			String result				= "";
			

			if(!GolfUtil.empty(enc_aspOrderNum))	aspOrderNum = new String(Base64Encoder.decode(enc_aspOrderNum));
			if(!GolfUtil.empty(enc_orderNum)) 		orderNum 	= new String(Base64Encoder.decode(enc_orderNum));
			if(!GolfUtil.empty(enc_result)) 		result 		= new String(Base64Encoder.decode(enc_result));
			
			if(GolfUtil.empty(result)){
				result = "yes";
			}
			
			if(result.equals("no")){
				script = "alert('이미 취소 되었거나 복지포인트 취소에 오류가 있었습니다.')";
			}else{

				dataSet.setString("aspOrderNum",aspOrderNum);
				dataSet.setString("orderNum",orderNum);
				dataSet.setString("jumin_no",jumin_no);
				
				GolfAdmMojibEzReturnProc proc = new GolfAdmMojibEzReturnProc();
				re_result = (int)proc.updState(context, dataSet);	
				
				if(re_result>0){
					script = "alert('복지포인트가 취소 되었습니다.'); parent.location.reload();";
				}else{
					script = "alert('이미 취소 되었거나 복지포인트 취소에 오류가 있었습니다.')";
				}
			}
			
			
			paramMap.put("result", result+"");
			request.setAttribute("paramMap", paramMap);
			
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}

		return super.getActionResponse(context, subpage_key);
		
	}
}
