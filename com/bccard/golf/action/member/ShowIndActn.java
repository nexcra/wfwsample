/**********************************************************************************
*   클래스명	: ShowIndActn
*   작성자      : 현광준
*   내용        : 개인회원정보 UPDATE action
*   적용범위	: bccard전체
*   작성일자	: 2004.02.3
************************** 수정이력**************************************************
*	일자				버전		수정자			변경사항
* 20040923					임건국		회원사별 뉴스레터 email 수신동의여부 추가
* 20061127					khko			HOST FRAMEWORK 변환에 의한 전문 수정 적용 시작
* 20080201					hklee		온라인 전문변환 8차 (UHL004_Ind_Ret  => BSNINPT(MHL0040R0100)
 * 수정시작일 적용예정일 수정완료일 적용완료일 작성자 변경사항
 * 2008.10.28 2008.10.29 2008.10.28 2008.11.15 조용국 회원은행 이메일 뉴스레터 수신부 변경
 * 2008.12.30 2008.12.31 2008.12.31 2008.12.31 hklee  회원정보 변경 인증방법변경
***********************************************************************************/package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;


/** ****************************************************************************
 * Login Action
 * @version   2004.02.03
 * @author    <A href="mailto:kjhyun@e4net.net">hyun kwang joon</A>
 **************************************************************************** */
public class ShowIndActn extends AbstractAction {

	static final String JoltServiceName = "BSNINPT";
	static final String TSN301 = "MHK3010R0100";		// SMS 회원 정보 조회
	static final String TSN040 = "MHL0040R0100";		// 개인회원 정보 조회
	static final String TSN060 = "MHL0060R0100";

	/** ****************************************************************************
	* Login Action
	* @version   2004.02.03
	* @author    <A href="mailto:kjhyun@e4net.net">hyun kwang joon</A>
	* @param context                WaContext 객체.
	* @param request                HttpServletRequest 객체.
	* @param response               HttpServletResponse 객체.
	* @return ActionResponse        Action 처리후 화면에 디스플레이할 정보.
	**************************************************************************** */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response)
                        throws IOException, ServletException, BaseException {

		String responseKey  = "default";

		ResultException re;
		String titleText = "회원정보변경"; // 서비스 타이틀명
		String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // 버튼
		String goPage = "/app/card/memberActn.do"; // 이동 액션

 		/***** 회원수정 제한시간 (22:00~08:00) => 미사용 
		SimpleDateFormat sdftemp = new SimpleDateFormat("HHmmss", Locale.KOREAN);
		String curdateFullFormat = sdftemp.format(Calendar.getInstance().getTime());
		if ( ( Long.parseLong(curdateFullFormat) < 80000L ) || ( Long.parseLong(curdateFullFormat) > 220000L)){
			ResultException re = new ResultException();
			re.setTitleImage("error");
			re.setTitleText("해당시간오류");
			re.setKey("TIME_ERROR_01");
			re.addButton("javascript:history.back()", "<img src='/images/btn/btn_confirm01.gif' border='0'");
			throw re;
		}
		*****/

		String currentActionKey = getActionKey(context); //getActionKey(servlet, request);

		HttpSession session = request.getSession(true);

		UcusrinfoEntity user = SessionUtil.getFrontUserInfo(request);
		session.setAttribute("actionKey", currentActionKey);

		String reqType = (String)request.getAttribute("gubun");
		String cert_result = (String)request.getAttribute("cert_result");
		String argv3 = "";
		String argv4 = "";
		String argv5 = "/app/" + currentActionKey;
		String url = "";

		if("modify".equals(reqType) || "success".equals(cert_result)) {
			reqType = "modify"; 
			responseKey = "modify";
		} else if("erms_email".equals(reqType)){
			responseKey  = "erms_email_ind";
		} else if("false".equals(cert_result)) {
			// 인증실패 - 오류
			re = new ResultException();
			re.setTitleImage("error");
			re.setTitleText(titleText);
			re.setKey("USERCERT_ERROR");
			re.addButton(goPage, addButton);
			throw re;
		} else {
			RequestParser parser = context.getRequestParser("default",request,response);
			argv3 = parser.getParameter("argv3" , "");
			argv4 = parser.getParameter("argv4" , "");
			argv5 = parser.getParameter("argv5" , "/app/" + currentActionKey);
			url = parser.getParameter("url","");
		}

		IndModifyOnlineProc proc  = (IndModifyOnlineProc)context.getProc("IndModifyOnlineProc");

		String zipcode2[] = new String[2];
		String co_zipcode2[] = new String[2];
		String zipcode = "";
		String co_zipcode = "";
		
		JtProcess jt = new JtProcess();

		try {
			if("1".equals(user.getMemberClss())) {
				JoltInput entity = new JoltInput(JoltServiceName);
				entity.setString("fml_trcode", TSN040);
				entity.setString("fml_arg1", user.getSocid());
	
				java.util.Properties prop = new java.util.Properties();
				prop.setProperty("RETURN_CODE", "fml_ret1");
	
				TaoResult taoResult = null;
				taoResult = jt.call(context, request, entity, prop);
	
				String rtnCode = ""; // 응답코드
				if (taoResult.containsKey("fml_ret1")){
					rtnCode = taoResult.getString("fml_ret1").trim(); // 응답코드
				}
	
				if( !"1".equals(rtnCode) ){
					re = new ResultException();
					re.setTitleImage("error");
					re.setTitleText(titleText);
					re.setKey("UHL004_Ind_Ret_" + rtnCode);
					re.addButton(goPage, addButton);
					throw re;
				}
	
				if (taoResult.containsKey("fml_ret3")){
					zipcode = taoResult.getString("fml_ret3").trim(); // 우편번호
				}
	
	
				if("".equals(zipcode.trim())) {
					zipcode2[0] = "";
					zipcode2[1] = "";
				}else {
					zipcode2 = proc.phoneMobile(zipcode, "-"); // 자택 우편번호분리
				}

				if (taoResult.containsKey("fml_ret8")){
					co_zipcode = taoResult.getString("fml_ret8").trim(); // 근무처 우편번호
				}
	
				if("".equals(co_zipcode.trim())) {
					co_zipcode2[0] = "";
					co_zipcode2[1] = "";
				}else {
					co_zipcode2 = proc.phoneMobile(co_zipcode,"-");// 근무처 우편번호분리
				}
	
				// 가공한 우편번호부터 넣음
				request.setAttribute("zipcode_0",zipcode2[0]);
				request.setAttribute("zipcode_1",zipcode2[1]);
				request.setAttribute("co_zipcode_0",co_zipcode2[0]);
				request.setAttribute("co_zipcode_1",co_zipcode2[1]);
	
				request.setAttribute("NAME",  taoResult.containsKey("fml_ret2")?taoResult.getString("fml_ret2"):"");
				request.setAttribute("zipaddr",taoResult.containsKey("fml_ret16")?taoResult.getString("fml_ret16"):"");
				request.setAttribute("detailaddr",taoResult.containsKey("fml_ret17")?taoResult.getString("fml_ret17"):"");
				request.setAttribute("co_zipaddr",taoResult.containsKey("fml_ret18")?taoResult.getString("fml_ret18"):"");
				request.setAttribute("co_detailaddr",taoResult.containsKey("fml_ret19")?taoResult.getString("fml_ret19"):"");
				request.setAttribute("phone_0",taoResult.containsKey("fml_ret5")?taoResult.getString("fml_ret5"):"");
				request.setAttribute("phone_1",taoResult.containsKey("fml_ret6")?taoResult.getString("fml_ret6"):"");
		   		request.setAttribute("phone_2",taoResult.containsKey("fml_ret7")?taoResult.getString("fml_ret7"):"");
				request.setAttribute("co_phone_0",taoResult.containsKey("fml_ret10")?taoResult.getString("fml_ret10"):"");
				request.setAttribute("co_phone_1",taoResult.containsKey("fml_ret11")?taoResult.getString("fml_ret11"):"");
				request.setAttribute("co_phone_2",taoResult.containsKey("fml_ret12")?taoResult.getString("fml_ret12"):"");
				request.setAttribute("co_name",taoResult.containsKey("fml_ret13")?taoResult.getString("fml_ret13"):"");
				request.setAttribute("position_name",taoResult.containsKey("fml_ret14")?taoResult.getString("fml_ret14"):"");
				request.setAttribute("fml_ret20",taoResult.containsKey("fml_ret20")?taoResult.getString("fml_ret20"):"");
				request.setAttribute("socid",user.getSocid());
	
				// 1113 원장에서 있는 핸드폰 번호로 맞춤
				request.setAttribute("mobile_0",taoResult.getString("fml_ret21"));
				request.setAttribute("mobile_1",taoResult.getString("fml_ret22"));
				request.setAttribute("mobile_2",taoResult.getString("fml_ret23"));
				
				request.setAttribute("homeAddrClcd",taoResult.getString("fml_ret30"));//자택주소구분(1:구 , 2:신)
				request.setAttribute("homeRoadNmCd",taoResult.getString("fml_ret32"));//자택도로명코드
				request.setAttribute("workAddrClcd",taoResult.getString("fml_ret31"));//직장주소구분(1:구 , 2:신)
				request.setAttribute("workRoadCd",taoResult.getString("fml_ret33"));//직장도로명코드
				
			}
			
		} catch(Throwable t) {
			//t.printStackTrace();
			re = new ResultException();
			re.setTitleImage("error");
			re.setTitleText(titleText);
			re.addButton(goPage, addButton);
			re.setKey("SYSTEM_ERROR");
			throw re;
		}

		Hashtable rsHash = null;
		try {
			rsHash = proc.getInfoChange(context,user.getAccount(),user.getMemberClss());
		} catch (Throwable t) {
			re = new ResultException();
			re.setTitleImage("error");
			re.setKey("MODIFY_MEMBER_02");
			re.setTitleText(titleText);
			re.addButton(goPage, addButton);
			re = new ResultException();
			throw re;
		}

		// BC라인쪽에서 외부변경이 들어왔을때...

		try {
			if("1".equals(user.getMemberClss())) {
				/*****************************************************
				  이용대금 청구내역 E-Mail 조회 (뉴스레터 수신 동의 여부) 확인
				******************************************************/
				JoltInput entity2 = new JoltInput(JoltServiceName);
				Properties prop2 = new Properties();
	
				// 2006.11.27. HOST FRAMEWORK 변환에 의한 전문 수정 적용 시작
				entity2.setServiceName(JoltServiceName);
				entity2.setString("fml_trcode", TSN060);
				entity2.setString("fml_arg1", user.getSocid());
				prop2.setProperty("RETURN_CODE", "fml_ret5");
	
				TaoResult taoResult2 = null;
				taoResult2 = jt.call(context, request, entity2, prop2);
	
				String rtnCode2 = ""; // 응답코드
				if (taoResult2.containsKey("fml_ret5")){
					rtnCode2 = taoResult2.getString("fml_ret5").trim();
					request.setAttribute("UHL006", taoResult2);
				}
	
				if( !"00".equals(rtnCode2) ){
					warn("ShowIndActn 이용대금 청구내역 E-Mail 조회 ");
				}
				// 이용대금 청구내역 E-Mail 조회 (뉴스레터 수신 동의 여부)
			}

		} catch(Throwable t) {
			warn("ShowIndActn 이용대금 청구내역 E-Mail 조회 ",t);
		}

		// DB에서 읽은 데이타를 넣음
		request.setAttribute("MEMBER_CLSS",(String)rsHash.get("MEMBER_CLSS"));
		request.setAttribute("SOCID_1",(String)rsHash.get("SOCID_1"));
		request.setAttribute("SOCID_2",(String)rsHash.get("SOCID_2"));
		request.setAttribute("SEX",(String)rsHash.get("SEX"));
		request.setAttribute("ACCOUNT",(String)rsHash.get("ACCOUNT"));
		request.setAttribute("PASSWD",(String)rsHash.get("PASSWD"));
		request.setAttribute("PASSWDQ",(String)rsHash.get("PASSWDQ"));
		request.setAttribute("PASSWDA",(String)rsHash.get("PASSWDA"));
		request.setAttribute("ENAME",(String)rsHash.get("ENAME"));
		request.setAttribute("BIRTH_1",(String)rsHash.get("BIRTH_1"));
		request.setAttribute("BIRTH_2",(String)rsHash.get("BIRTH_2"));
		request.setAttribute("BIRTH_3",(String)rsHash.get("BIRTH_3"));
		request.setAttribute("SOLAR",(String)rsHash.get("SOLAR"));
		request.setAttribute("JOB",(String)rsHash.get("JOB"));
		request.setAttribute("JOBTYPE",(String)rsHash.get("JOBTYPE"));

		request.setAttribute("RECOMM_ACCOUNT",(String)rsHash.get("RECOMM_ACCOUNT"));
		request.setAttribute("ADDRESS",(String)rsHash.get("ADDRESS"));
		request.setAttribute("EMAIL",(String)rsHash.get("EMAIL"));
		request.setAttribute("MAILING",(String)rsHash.get("MAILING"));
		request.setAttribute("CARD_RECV_YN",(String)rsHash.get("CARD_RECV_YN"));
		request.setAttribute("TOUR_RECV_YN",(String)rsHash.get("TOUR_RECV_YN"));
		request.setAttribute("SHOPPING_RECV_YN",(String)rsHash.get("SHOPPING_RECV_YN"));
		request.setAttribute("CTNT_RECV_YN",(String)rsHash.get("CTNT_RECV_YN"));
		request.setAttribute("EMAIL_URL",(String)rsHash.get("EMAIL_URL"));
		request.setAttribute("RECV_YN",(String)rsHash.get("RECV_YN"));
			
		if(!"1".equals(user.getMemberClss())) {
			request.setAttribute("NAME",(String)rsHash.get("NAME"));
			request.setAttribute("socid",(String)rsHash.get("SOCID"));

			String mobile = (String)rsHash.get("MOBILE");
			String mobile2[] = new String[3];
			if("".equals(mobile.trim())) {
				mobile2[0] = "";
				mobile2[1] = "";
				mobile2[2] = "";
			}else {
				mobile2 = proc.phoneMobile(mobile, "-"); // 자택 우편번호분리
			}

			request.setAttribute("mobile_0",mobile2[0]);
			request.setAttribute("mobile_1",mobile2[1]);
			request.setAttribute("mobile_2",mobile2[2]);

			String phone = (String)rsHash.get("PHONE");

			String phone2[] = new String[3];
			if("".equals(phone.trim())) {
				phone2[0] = "";
				phone2[1] = "";
				phone2[2] = "";
			}else {
				phone2 = proc.phoneMobile(phone, "-"); // 자택 우편번호분리
			}

			request.setAttribute("phone_0", phone2[0]);
			request.setAttribute("phone_1", phone2[1]);
	   		request.setAttribute("phone_2", phone2[2]);

			zipcode = (String)rsHash.get("ZIPCODE"); // 우편번호

			if("".equals(zipcode.trim())) {
				zipcode2[0] = "";
				zipcode2[1] = "";
			}else {
				zipcode2 = proc.phoneMobile(zipcode, "-"); // 자택 우편번호분리
			}

	   		request.setAttribute("zipcode_0",zipcode2[0]);
	   		request.setAttribute("zipcode_1",zipcode2[1]);

	   		request.setAttribute("zipaddr",(String)rsHash.get("ZIPADDR"));
	   		request.setAttribute("detailaddr",(String)rsHash.get("DETAILADDR"));
	   		request.setAttribute("addrClss",(String)rsHash.get("ADDR_CLSS"));

		}
		
		// ucusrinfo, identity 데이터 sync. 시작
		IndModifyOnlineProc procModify  = (IndModifyOnlineProc)context.getProc("IndModifyOnlineProc");

		try {
			if("1".equals(user.getmemberClssCard())) {
				procModify.setMemberInfo(context, request);
			}
		} catch(Throwable t) {
			//t.printStackTrace();
			debug("Exception : " + t.getMessage());

		/*****************************************************************************
	         * LOG 를 남기기 위한 작업
	         * REG|ShowIndActn.IndModifyOnlineProc.setMemberInfo(context, request)|회원종류|주민번호|계정|이름|접속IP|시작시간
	        ******************************************************************************/
	        String msg = "REG|ShowIndActn.IndModifyOnlineProc.setMemberInfo(context, request)|1|" + user.getSocid() + "|" + user.getAccount() + "|" + request.getAttribute("NAME") + "|" + request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss") + t.getMessage();

	        //BcLog.memberLog("개인 회원정보 조회 WEB DB Sync. 오류");
	        //BcLog.memberLog(msg);
	        System.out.println("개인 회원정보 조회 WEB DB Sync. 오류");
			System.out.println(msg);

		}

		if("5".equals(user.getMemberClss())) {
			responseKey = "level5";
		}

		if(!"1".equals(user.getMemberClss()) && !"5".equals(user.getMemberClss())) {
			responseKey = "level3";
		}
		
		request.setAttribute("SITE",argv3);
		request.setAttribute("argv3",argv3);	// 사이트구분자
		request.setAttribute("argv4",argv4);	// 사이트구분자
		request.setAttribute("argv5",argv5);	// 사이트구분자
		request.setAttribute("url",url);	// 어디에서 왔는지 확인

		return getActionResponse(context,responseKey);
	}
}

