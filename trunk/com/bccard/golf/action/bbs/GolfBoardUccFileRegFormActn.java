/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardUccFileRegFormActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : UCC 파일 등록 폼
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.bbs;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfBoardUccFileRegFormActn extends GolfActn{
	
	public static final String TITLE = "UCC 파일 등록 폼";

	/***************************************************************************************
	* Golf 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
						
//			----------------------------------------------------------------
//			가비아 연동 JSP소스 
//			파일이름 : sample_mysite_upload.jsp
//			대표이미지 수동선택 - 첫번째 페이지 파일올리기를 위한 샘플 소스
//			----------------------------------------------------------------

//			관리자아이디 - 가비아 아이디가 아닌 동영상호스팅2 관리자 아이디 - [본인의 아이디로 수정하세요]
			String company_id = "bccard";
//			UCC 연동을 위해 받은 인증키 - MyGabia에서 확인이 가능 - [본인의 인증키로 수정하세요]
			String certy_key = "DVPF-7CYW8-FY2C";									
//			Unix TimeStamp - [변경하지 마세요]
			Timestamp certy_time = new Timestamp( System.currentTimeMillis() );

//			md5 암호화 로직
//			====================수정하지 마세요===============================
			String param = company_id + certy_key + certy_time;

			StringBuffer md5 = new StringBuffer(); 

			try { 
				byte[] digest = java.security.MessageDigest.getInstance("MD5").digest(param.getBytes()); 

				for (int i = 0; i < digest.length; i++) { 
					md5.append(Integer.toString((digest[i] & 0xf0) >> 4, 16)); 
					md5.append(Integer.toString(digest[i] & 0x0f, 16)); 
				} 

			} catch(java.security.NoSuchAlgorithmException ne) { 
				ne.printStackTrace(); 
			} 

//			인증보안값 - [변경하지 마세요]
			String certy_value = md5.toString();
//			====================================================================

//			고객님이 관리하는 업로드 할려는 파일에 대한 고유값  - [년원일시분초로 기본세팅이지만 고객님이 생성해도 됩니다]
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddhhmmss");
			java.util.Date currentTime = new java.util.Date();
			String today = formatter.format(currentTime);
//			-------------------------------------------------------------------
			String client_key = company_id +"_"+ today;			

//			파일이 정상적으로 업로드 되었을경우 호출되는 URL - [대표이미지를 선택하는 화면으로 구성되어야 합니다]
			String url_success1 = AppConfig.getAppProperty("URL_REAL")+"/golfUccSelThumb.do";
			url_success1 = url_success1.replaceAll("\\.\\.","");
//			파일 업로드가 실패했을 경우 호출되는 URL - [실패된 에러메세지에 대해서 처리되도록 구성되어야 합니다]
			String url_error1 = AppConfig.getAppProperty("URL_REAL")+"/golfUccFileRegErr.do";
			url_error1 = url_error1.replaceAll("\\.\\.","");
			
//			희망하는 인코딩속도 (단위 : Mbps) 예) 200,400(default),600,800,1000
			String encoding_speed = "400";
//			희망하는 화면크기 (가로 | 세로 - 단위 pixel) 예) 320|240, 400|300(default), 640|480, 720|480
			String encoding_screen = "400|300"; 
//			대표이미지의 자동,수동여부를 결정 - [수동으로 대표이미지를 선택한다면 그대로 두십시요]
			String auto_flag = "M";

//			사용자정의 - 뒤의 숫자만 바꿔서 계속 추가하실수 있습니다.
			String user_string1 = "사용자정의1";
			String user_string2 = "사용자정의2";
			String user_string3 = "사용자정의3";
//			String user_string4 = "사용자정의4";

			String class_code = "";	
//			class_code (분류코드)를 사용할시에는 
//			http://admin.flv.gabia.com/flash_response/get_class.php?company_id=귀사아이디
//			를 호출하셔서 나오는 XML를 파싱해서 사용하시면 됩니다.
//			결과값예제 : sample_mysite_class_xml.jsp

//			분류등록은 MyGabia의 동영상호스팅2 관리툴에서만 가능합니다.
//			생략시 귀사아이디로 등록되어 있는 기본분류로 자동 입력됩니다. 
//			주의 : 잘못된 분류코드가 넘어올시 오류가 리턴되며 등록되지 않습니다. 
			
			// 05. Return 값 세팅
			paramMap.put("certy_value", certy_value);
			paramMap.put("certy_time", certy_time);
			paramMap.put("company_id", company_id);
			paramMap.put("client_key", client_key);
			paramMap.put("url_success1", url_success1);
			paramMap.put("url_error1", url_error1);
			paramMap.put("encoding_speed", encoding_speed);
			paramMap.put("encoding_screen", encoding_screen);
			paramMap.put("auto_flag", auto_flag);
			
			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
