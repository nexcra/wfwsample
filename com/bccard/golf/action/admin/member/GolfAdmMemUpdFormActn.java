/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdFormActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 폼
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.ChkChgSocIdException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemMgmtInqDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemUpdFormDaoProc;
import com.bccard.golf.jolt.JtProcess;
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
public class GolfAdmMemUpdFormActn extends GolfActn{

	public static final String TITLE = "관리자 > 어드민관리 > 회원관리 > 회원보기";
	private static final String BSNINPT = "BSNINPT";					// 프레임웍 조회서비스
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {

			// 회원통합테이블 관련 수정사항 진행
			TaoResult cardinfo_pt = null;
			String resultCode_pt = "";

			boolean existsData = false;
			
			// 카드 관련 변수 설정
			String strGlofCardYn = "N";
			String strCardDate = "";
			String cardType = "";
			String joinNo = ""; 
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String cdhd_ID			= parser.getParameter("CDHD_ID", "");
			String socID			= "";
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CDHD_ID", cdhd_ID);
					
			GolfAdmMemUpdFormDaoProc proc = (GolfAdmMemUpdFormDaoProc)context.getProc("GolfAdmMemUpdFormDaoProc");
			
			// 주민번호 가져오기
			//socID = proc.getSocid(context, dataSet);
			Hashtable rsHash = null;
			rsHash = proc.getSocid(context, dataSet);
			//DbTaoResult socIDResult = proc.getSocid(context, dataSet);
			
			String memClss = (String)rsHash.get("MEMBER_CLSS");
			socID = (String)rsHash.get("SOCID");
			debug("## socID : "+ socID + " ## memClss : " + memClss);
			dataSet.setString("memClss", memClss);
			dataSet.setString("socID", socID);			
			
			
			try{
				if ("1".equals(memClss)) {
					System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 전문 호출 <<<<<<<<<<<<");
					JoltInput cardInput_pt = new JoltInput(BSNINPT);
					cardInput_pt.setServiceName(BSNINPT);
					cardInput_pt.setString("fml_trcode", "MHL0230R0100");
					cardInput_pt.setString("fml_arg1", "1");	// 1.주민번호 2.사업자번호 3.전체(지정자주민번호+사업자)
					cardInput_pt.setString("fml_arg2", socID);	// 주민번호
					//cardInput_pt.setString("fml_arg2", "6002041090498");	// 임시테스트용
					cardInput_pt.setString("fml_arg3", " ");	// 사업자번호
					cardInput_pt.setString("fml_arg4", "1");	// 1.개인 2.기업
					JtProcess jt_pt = new JtProcess();
					java.util.Properties prop_pt = new java.util.Properties();
					prop_pt.setProperty("RETURN_CODE","fml_ret1");
					
		
					
					do {
						cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
				
						resultCode_pt = cardinfo_pt.getString("fml_ret1");
						debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`resultCode_pt ::  " + resultCode_pt);
						
		
						if ( !"00".equals(resultCode_pt) && !"02".equals(resultCode_pt) ) {		// 00 정상, 02 다음조회 있음
							//cardType 	= "전문 오작동 카드종류 알 수 없음. Error Code : " + resultCode_pt;
							throw new ChkChgSocIdException(cdhd_ID, socID, "02");
	
						}else{
							while( cardinfo_pt.isNext() ) {
								if(!existsData){
									existsData = true;
								}
	
								cardinfo_pt.next();
														
								cardType 	= cardinfo_pt.getString("fml_ret4");	//카드종류 1:골프카드 / 2:PT카드 / 3:일반카드
	
		//						- 상품명 :  나의 알파 플래티늄골프카드 / 제휴코드
		//						 ㅇ 나의알파플래티늄골프_캐쉬백     / 030478
		//						 ㅇ 나의알파플래티늄골프_아시아나  / 030481
		//						 ㅇ 나의알파플래티늄골프_대한항공  / 030494
								
								//임시로 제거 2009.08.25 권영만 실테스트시는 주석제거해야됨
								if("1".equals(cardType)){
									if("030478".equals(joinNo) || "030481".equals(joinNo) || "030494".equals(joinNo)  || "030698".equals(joinNo) || "031189".equals(joinNo) || "031176".equals(joinNo) ){
		
		
										strGlofCardYn = "Y";
										strCardDate	= cardinfo_pt.getString("fml_ret12");	//카드등록일자		
										
		
										debug(" Ret4 | 카드종류 : "+ cardinfo_pt.getString("fml_ret4"));
										debug(" Ret7 | 카드이름 : "+ cardinfo_pt.getString("fml_ret7"));
										debug(" Ret8 | 제휴코드 : "+ cardinfo_pt.getString("fml_ret8"));
										debug(" Ret12 | 카드등록일자 : "+ cardinfo_pt.getString("fml_ret12"));
										debug(" Ret13 | 서비스시작일자 : "+ cardinfo_pt.getString("fml_ret13"));
										debug(" Ret14 | 서비스제공구분 : "+ cardinfo_pt.getString("fml_ret14"));
		
									}
								}
							}
						}
					} while ("02".equals(resultCode_pt));
				}

			}catch (Throwable t){
				
			}
			
			// 04.실제 테이블(Proc) 조회
			DbTaoResult updFormResult = proc.execute(context, dataSet);
			
	        	
	        // 회원멤버등급 데이터 가져오기
			DbTaoResult gradeListInq = (DbTaoResult)proc.execute_grade(context, request);
			
	        // 연회비 환급여부 가져오기
			DbTaoResult payInq = (DbTaoResult)proc.execute_pay(context, request, dataSet);
			
			// 히스토리 리스트 조회 - 멤버십 회원 변경내역을 가져와서 뿌려준다.
			DbTaoResult historyResult = proc.execute_history(context, request, dataSet);

			// JSP 페이지로 내려보낼 맵설정
			request.setAttribute("UpdFormResult", updFormResult);	
			request.setAttribute("GradeListInq", gradeListInq);		
			request.setAttribute("PayInq", payInq);	
			request.setAttribute("historyResult", historyResult);
	        request.setAttribute("strGlofCardYn", strGlofCardYn);
	        request.setAttribute("strCardDate", strCardDate);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
