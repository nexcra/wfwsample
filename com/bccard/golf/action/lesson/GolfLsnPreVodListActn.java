/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfLsnVodListActn
*   작성자	: (주)미디어포스 
*   내용		: 사용자 > 레슨 > 프리미엄 동영상 > 리스트
*   적용범위	: Golf
*   작성일자	: 2009-12-07
************************** 수정이력 ****************************************************************
*    일자     작성자   변경사항
*  20110304  이경희   [http://www.bccard.com/->VIP서비스/골프/골프VIP동영상]접근시 Full동영상제공
*  20110512  이경희   [http://golfloung.familykorail.com/-> Home > 교양마당 > 골프레슨 > 동영상레슨] 접속시 Full동영상제공
***************************************************************************************************/
package com.bccard.golf.action.lesson;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.Base64Encoder;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.security.cryptography.StringEncrypter;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.lesson.GolfLsnVodListDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.initech.eam.nls.CookieManager;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfLsnPreVodListActn extends GolfActn{ 
	
	public static final String TITLE = "사용자 > 레슨 > 프리미엄 동영상 > 리스트";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userId = "";
		String isLogin = ""; 
		String isLoginEnc = "";		// 로그인유무 암호화 - 전골프에 보낼 값 
		String isView = "";			// 영상보기 권한 유무 
		String isViewEnc = "";		// 영상보기 권한 유무 암호화 - 전골프에 보낼 값 
		String script = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		String in = request.getAttribute("actnKey").toString();
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId = (String)usrEntity.getAccount(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "Y";
			} else {
				isLogin = "N";
				isView = "N";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lesson");

			// Request 값 저장
			long page_no			= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size		= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");

			String svod_clss		= parser.getParameter("svod_clss", "0001"); //0001:해외유명프로레슨 0002:단계별스윙레슨 0003:숏게임레슨 0004:상황별레슨 0005:효과적인연습방법
			String svod_lsn_clss	= parser.getParameter("svod_lsn_clss", "0001"); //0001:월드그레이트티쳐스 0002:마이클아담스..	

			paramMap.put("svod_clss", svod_clss);
			paramMap.put("svod_lsn_clss", svod_lsn_clss);
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SVOD_CLSS", svod_clss);
			dataSet.setString("SVOD_LSN_CLSS", svod_lsn_clss);
			dataSet.setString("PRE_YN", "Y");	// 프리미엄동영상구분 (Y:프리미엄동영상, N:일반동영상)

			// 실제 테이블(Proc) 조회
			GolfLsnVodListDaoProc proc = (GolfLsnVodListDaoProc)context.getProc("GolfLsnVodListDaoProc");
			DbTaoResult lsnVodListResult = (DbTaoResult) proc.execute(context, request, dataSet);
	
			// 전체 0건  [ 0/0 page] 형식 가져오기
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
				
			if (lsnVodListResult != null && lsnVodListResult.isNext()) {
				lsnVodListResult.first();
				lsnVodListResult.next();
				if (lsnVodListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)lsnVodListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)lsnVodListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
		
		
			//  Full동영상 제공 
			if (in.substring(in.length()- 4, in.length()).equals("InBC") 
					|| in.substring(in.length()- 8, in.length()).equals("InKorail")){
				
				/*
				 * 전골프  동영상을 1분제공이 아닌 Full제공을 하기 위해선  isLogin, isView 의 값이 'Y'이고, 
				 * 동영상 seq와 사용자 아이디를 넘겨 줘야함 
				 * 여기서 isLogin, isView, 사용자아이디(MID)는 암호화하여 전송
				 * ~*.jsp의 MID는 가상의 아이디로 임의 할당		
				 * 가상아이디는 암호화된 모든 아이디를 할당 할 수 있음		 
				*/				
				
				isLogin = "Y";
				isView = "Y";
				
				/* 
				 *[ http://www.bccard.com/->VIP서비스/골프/VIP 동영상 ]에서 접속시
				*/
				if (in.substring(in.length()- 4, in.length()).equals("InBC") ) {
					info("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP서비스/골프/골프 VIP동영상'에서 접속 " );
				}
				
				/*
				 * [아래 Note는 코레일에서 골프 메뉴 클릭시 접속되는 URL]
				 * 
				 * 1. 개발기로  접속시 (211.181.255.164 , 포트 : 13300  ->개발기 웹서버 )
				 *  http://golfloung.familykorail.com:13300/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/familykorail.jsp
				 * 		1) 개발기 테스트시에는 로컬의 hosts파일에 다음과 같이 추가 한다
				 * 			->211.181.255.164	golfloung.familykorail.com					
				 * 
				 * 2. 운영기로  접속시 (211.181.254.13)
				 *   http://golfloung.familykorail.com/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/familykorail.jsp
				 *  
				 *  
				 *  3. WCP가 decrypt 된 sample
				 *   -> domain=www.familykorail.com&userno=0000003&username=테스터3&logintime=20110512131647&user_level=1&fuserno=1111113&userck=f31c147335274c56d801f833d3c26a70
				 *   
				 *  4. 코레일 테스트 아이디 -> www.familykorail.com -> testok/1234 
				 *///코레일 VIP동영상 제공
				if (in.substring(in.length()- 8, in.length()).equals("InKorail") ) {
					
					info("## "+this.getClass().getName()+ " | 'http://golfloung.familykorail.com/ -> '에서 접속 " );
					
					//코레일 쿠키 get
					String wcp = getValue("WCP", request);					
					wcp = GolfUtil.sqlInjectionFilter(wcp);
	
					//secret key 할당
					String key 	= "esoft_cp_asp_key";
					String iv 	= "esoft_initial_iv";
					StringEncrypter encrypter = new StringEncrypter(key, iv);
					
					/*
					 * 복호화(decrypt)시 Tip
					 * 아래와 같은 Exception 발생은 encrypt시와 decrypt시에 비밀키(secret key)가 맞지 않아서 발생
					 * 'key'를  확인해 볼 것
					 * javax.crypto.BadPaddingException: Given final block not properly padded
					 */  
					String dWCP = encrypter.decrypt(wcp);	
					//String dWCP = "domain=www.familykorail.com&userno=&username=테스터3&logintime=20110512131647&user_level=1&fuserno=1111113&userck=f31c147335274c56d801f833d3c26a70";
					StringTokenizer st = new StringTokenizer(dWCP,"&");
					
					boolean usrLevVeri = false; //코레일 사용자 권한  ( user_level 값이 1과 5이상  값만 해당 )
					boolean usrLoginVeri = false; // 코레일 사용자 로그인 유무
					
					String korailLev = "", korailId = "", korailUsrName = "";
					
					while (st.hasMoreTokens()) {
						 
						String tokenValue = st.nextToken();
						
						//잠시 동안만 로그 출력 (코레일 안정화시까지)
						debug(" ## st.nextToken( : "+tokenValue);
						
						int idx = tokenValue.indexOf('=');
						
						String strKey =  StrUtil.isNull(tokenValue.substring(0,idx),"");
						String strVal =  StrUtil.isNull(tokenValue.substring(idx+1),"");	
						
						//코레일 사용자 권한 체크
						if (strKey.equals("user_level") ){							
							if ( !strVal.equals("")){								
								if ( Integer.parseInt(strVal) == 1 || Integer.parseInt(strVal) >= 5 ){								
									usrLevVeri = true;
									korailLev = strVal;
								}
							}
						}						
						
						//코레일 사용자 로그인 유무  체크
						if (strKey.equals("userno") ){							
							if ( !strVal.equals("")){
								usrLoginVeri = true;
								korailId = strVal;							
							}							
						}
						
						//코레일 사용자 성명
						if (strKey.equals("username") ){
							if ( !strVal.equals("")){	
								korailUsrName = strVal;
							}
						}
						 
					}
					
					//usrLevVeri = false;
					if ( usrLevVeri == true ){
						info("## "+this.getClass().getName()+" | '코레일 사용자 권한 : ["+korailLev+"] 접속가능함 ; id(userno) : ["+korailId+"], 성명 : ["+korailUsrName+"]" );
					}else {
						//접근 불가
						info("## "+this.getClass().getName()+" | '코레일 사용자 권한  없음: ["+korailLev+"] 접속불가;  id(userno) : ["+korailId+"], 성명 : ["+korailUsrName+"]" );
						script = "alert('접근권한이 없습니다.');parent.parent.location.href='http://www.familykorail.com/';";
						request.setAttribute("script", script);
						subpage_key = "korail";
					}
					
					//usrLoginVeri = false;
					if ( usrLoginVeri == true ){
						info("## "+this.getClass().getName()+" | '코레일 사용자 로그인 함; 코레일 접속 id(userno) : ["+korailId+"], 성명 : ["+korailUsrName+"]"  );
					}else {
						
						info("## "+this.getClass().getName()+" | '코레일 사용자 로그인  하지 않음; 코레일 접속 id(userno) : ["+korailId+"], 성명 : ["+korailUsrName+"]"  );
						
						//로그인이 안되어 있을 경우 로그인을 하고 원하는 페이지로 넘어가도록; 로그인 리다이렉션
						String current_url = "http://golfloung.familykorail.com/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/familykorail.jsp";
						script = "alert('로그인 후 서비스 이용이 가능합니다.'); parent.parent.location.href='http://www.familykorail.com/login.php?backurl="+new String(Base64Encoder.encode(current_url.getBytes()))+"';";
						request.setAttribute("script", script);
						subpage_key = "korail";
						
					}
					
					usrLevVeri = false;
					usrLoginVeri = false;
					

				}
				/*//2012.01.02 09시 일자로 서비스 중지
				 *
				 * [아래 Note는 신한금융지주에서  골프 메뉴 클릭시 접속되는 URL]
				 * 
				 * 1. 개발기로  접속시 (211.181.255.164 , 포트 : 13300  ->개발기 웹서버 )
				 *  http://golf.waf.co.kr:13300/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/shinhan_waf.jsp
				 * 		1) 개발기 테스트시에는 로컬의 hosts파일에 다음과 같이 추가 한다
				 * 			->211.181.255.164	golf.waf.co.kr 				
				 * 
				 * 2. 운영기로  접속시 (211.181.254.13)
				 *   http://golf.waf.co/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/shinhan_waf.jsp
				 *  
				 *  
				 *  3. WCP가 decrypt 된 sample
				 *   -> domain=golf.waf.co&waf_no=1111&waf_name=테스터
				 *   
				 *  4. 테스트 아이디 ->  신한금융지주는 테스트 아이디를 제공하지 않음 아래  String dWCP 주석을 풀고 테스트, 
				 *                     신한금융지주 운영  테스트시는 이소프트에 연락하여 테스트 아이디 받아야함
				 *   
				 ///신한금융지주 VIP동영상 제공				 * 
				else if (in.substring(in.length()- 9, in.length()).equals("InShinHan") ) {
					
					info("## "+this.getClass().getName()+ " | 'http://health.waf.co.kr -> '에서 접속 " );
					
					//신한금융지주  쿠키 get
					String wcp = getValue("WCP", request);					
					wcp = GolfUtil.sqlInjectionFilter(wcp);

	
					//secret key 할당
					String key 	= "!shinhanway12345";
					String iv 	= "1234567890abcde!";

					StringEncrypter encrypter = new StringEncrypter(key, iv);
					 
					String dWCP = encrypter.decrypt(wcp);
					//String dWCP = "domain=golf.waf.co&waf_no=1111&waf_name=테스터";
					StringTokenizer st = new StringTokenizer(dWCP,"&");
					
					boolean usrLevVeri = false; //코레일 사용자 권한  ( user_level 값이 1과 5이상  값만 해당 )
					boolean usrLoginVeri = false; // 코레일 사용자 로그인 유무
							
					String shinhanId = "";
					
					while (st.hasMoreTokens()) {
						 
						String tokenValue = st.nextToken();
						
						int idx = tokenValue.indexOf('=');
						
						String strKey =  StrUtil.isNull(tokenValue.substring(0,idx),"");
						String strVal =  StrUtil.isNull(tokenValue.substring(idx+1),"");	
						
						debug(" ## strKey : "+strKey + ", strVal : " + strVal);
						
						//코레일 사용자 로그인 유무  체크
						if (strKey.equals("waf_no") ){							
							if ( !strVal.equals("")){
								usrLoginVeri = true;
								shinhanId = strVal;							
							}							
						}
						
					}
					
					if ( usrLoginVeri == true ){
						info("## "+this.getClass().getName()+" | '신한 금융지주  로그인 함; 신한 금융지주  접속 id(userno) : ["+shinhanId+"]"  );
					}else {
						
						info("## "+this.getClass().getName()+" | '신한 금융지주 로그인  하지 않음; 신한 금융지주  접속 id(userno) : ["+shinhanId+"]"  );
						
						//로그인이 안되어 있을 경우 로그인을 하고 원하는 페이지로 넘어가도록; 로그인 리다이렉션
						script = "alert('로그인 후 서비스 이용이 가능합니다.'); parent.parent.location.href='http://www.waf.co.kr/member/login.do';";
						 
						request.setAttribute("script", script);
						subpage_key = "shinhan";
						
					}
					
					usrLoginVeri = false;

				}				
				*/
				
			}else {
				
				// 접근권한 조회	
				String permissionColum = "";
				if(isLogin.equals("Y")){
					try{
						permissionColum = "PMI_LESN_" + String.valueOf(Integer.parseInt(svod_clss)) + "_APO_YN";
						GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
						DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);
	
						permissionView.next();
						if(permissionView.getString("RESULT").equals("00")){
							isView = permissionView.getString("LIMT_YN");
						}else{
							isView = "N";
						}										
					}
					catch(Throwable t){
						isView = "N";
					}
				}
				
				debug("## GolfLsnPreVodListActn | userId : " + userId + " | isLogin : " + isLogin + " | isView : " + isView + " | permissionColum : " + permissionColum + "\n");
			
			}			
			
			// 전골프에 보낼 값 암호화
			StringEncrypter sender = new StringEncrypter("adf34alkjdf", "efef897akdjfkl");
			isLoginEnc = sender.encrypt(isLogin); 
			isViewEnc = sender.encrypt(isView); 

			paramMap.put("isLoginEnc", isLoginEnc);
			paramMap.put("isViewEnc", isViewEnc);	
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));				
			paramMap.put("resultSize", String.valueOf(lsnVodListResult.size()));
			
			request.setAttribute("lsnVodListResult", lsnVodListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	       	        
		} catch(Throwable t) {
			
			if (in.substring(in.length()- 8, in.length()).equals("InKorail") ) {
				
				//에러 발생시 코레일 에러페이지로 리다이렉션				
				debug(TITLE, t);
				script = "location.href='http://www.familykorail.com/error/error_500.html';";
				request.setAttribute("script", script);
				subpage_key = "korail";
				
			}else {	
				throw new GolfException(TITLE, t);				
			}
			
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
	public String getValue(String key, HttpServletRequest request) {		
		return CookieManager.getCookieValue(key, request);
	}	
	
	
}
