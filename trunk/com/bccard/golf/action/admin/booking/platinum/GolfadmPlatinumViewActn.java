/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPlatinumViewActn
*   작성자    : 이정규
*   내용      : 관리자 > 부킹 > 플래티넘 리스트 >  상세보기(수정폼)
*   적용범위  : golf
*   작성일자  : 2010-09-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.platinum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfAdmBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.platinum.GolfadmPlatinumListDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.sky.*;
import com.bccard.golf.jolt.JtProcess;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfadmPlatinumViewActn extends GolfActn{
	
	public static final String TITLE = "관리자 부킹 골프장 수정 폼"; 

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
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
			int int_able = 0;
			int int_done = 0;
			int int_can = 0;
			String memGrade = "";
			// 01.세션정보체크
			GolfUserEtt ett = new GolfUserEtt();
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String golf_svc_rsvt_no		= parser.getParameter("GOLF_SVC_RSVT_NO", "");
			String cdhd_id 			= parser.getParameter("CDHD_ID","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GOLF_SVC_RSVT_NO"	,golf_svc_rsvt_no);
			dataSet.setString("CDHD_ID"		,cdhd_id);
			
			// 04.실제 테이블(Proc) 조회
			GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");
			DbTaoResult bkView = proc.getPlatinumView(context, dataSet);			//상세보기 -수정
			
			/// 04-1. 하단 목록 조회
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", 10L);
			dataSet.setString("CDHD_ID", cdhd_id);

			DbTaoResult listResult = (DbTaoResult) proc.executeJuminList(context, request, dataSet);
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result))
				request.setAttribute("total_cnt", listResult.getString("TOT_CNT"));
			else
				request.setAttribute("total_cnt", "0");
			
			request.setAttribute("ListResult", listResult);
			request.setAttribute("BkView", bkView);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
	        
	        /* 카드 번호 받아오기*/
	        try{
				checkJoltVip(context, request,ett, cdhd_id);
			}catch (Throwable t){
				ArrayList card_list = new ArrayList();
				ett.setCardVipInfoList(card_list);
			}
			List listVip = ett.getCardVipInfoList();
			//System.out.println("## GolfCtrlServ | 회원 VIP카드정보통신 결과 ID : "+ listVip.size()+"\n");
			List lgCardList = new ArrayList();	
			
			String select_grade_no = "";
			String newCardNo = "";	//카드 암호화
			String selCardNo="";	//카드 + 회원사 +등급
			String realCardNo="";	//실제 카드 번호
			/**/
			if(ett != null)
			{
				select_grade_no = ett.getVipMaxGrade();
				
				debug("## VIP카드 소지 체크 시작 | select_grade_no : "+select_grade_no);
				
								
				if( listVip!=null && listVip.size() > 0 )
				{
					
					if(!"00".equals(select_grade_no))	// 플래티넘 회원일 경우	
					{
						
						for (int i = 0; i < listVip.size(); i++) 
						{
							try { 
							
								CardVipInfoEtt record = (CardVipInfoEtt)listVip.get(i);
								String cardNo 		= StrUtil.isNull((String)record.getCardNo(), ""); 
								String cardType 		= StrUtil.isNull((String)record.getCardType(), "");
								String bankNo 		= StrUtil.isNull((String)record.getBankNo(), "");
								
								CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
								
								try{
								
									
									if(!"".equals(cardNo))
									{
										//newCardNo = cardNo.substring(0, 4)+"-"+cardNo.substring(4, 8)+"-"+cardNo.substring(8, 12)+"-"+cardNo.substring(12, 16);
										newCardNo = cardNo.substring(0, 4)+"-"+cardNo.substring(4, 8)+"-****-"+cardNo.substring(12, 16);
										selCardNo = cardNo + "/" +cardType +"/" + bankNo;
										realCardNo = cardNo;
										
									}
									cardVipInfo.setCardNo(newCardNo);	//카드번호 ****
									cardVipInfo.setCardType(realCardNo);	//카드번호 
									cardVipInfo.setCardNm(selCardNo);	//카드번호 + / + 등급 + / + 회원번호
									cardVipInfo.setCardAppType(cardNo);
									
									//cardVipInfo.setCardType(cardType);	//카드번호
									//cardVipInfo.setBankNo(bankNo);	//카드번호
								
								}catch(Throwable t){}
								
								lgCardList.add(cardVipInfo);
								
							} catch(Throwable t) {}
							//vipCardYn = "Y";
						}
					}
					else{
						debug("## VIP플래티늄 회원 아님");						
					}
					
				}
				else{
					debug("## VIP카드 소지 안함.");	
				}
			//검색 부분 셋팅
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("realNewCardNo", realCardNo);	//실제카드번호
	        request.setAttribute("lgCardList", lgCardList);
			}
	        
	        
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
	/**
	 * VIP카드 소지 여부 조회
	 * @param 	context		WaContext 객체
	 * @param 	request	HttpServletRequest
	 * @param 	ett			사용자정보 Entity
	 * @return 	void
	 * @TODO	VIP카드 체크
	 */
	protected void checkJoltVip(WaContext context, HttpServletRequest request,GolfUserEtt ett, String text) throws BaseException {

		java.util.Properties properties = new java.util.Properties();

		properties.setProperty("LOGIN", "Y");  					// 필수 로그에 남길 해당 전문의 return RETURN_CODE 키. 설정 안하면 "fml_ret1" 사용
		properties.setProperty("RETURN_CODE", "fml_ret1");		// 만약 특정한 pool 을 사용하는 경우라면.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", text); 	// log 찍어주는 부분에서 주민번호 대체
		try {
			/** *****************************************************************
			 *Card정보를 읽어오기
			 ***************************************************************** */
			debug("## GolfCtrlServ VIP카드 | 1. VIP카드 Jolt MHL0200R0200 전문 호출 <<<<<<<<<<<<");
						
			String joltFmlTrCode016 = "MHL0200R0200";
			String joltServiceName = "BSNINPT";
			JoltInput jtInput = new JoltInput(joltServiceName);
			jtInput.setServiceName(joltServiceName);
			jtInput.setString("fml_trcode", joltFmlTrCode016);			
			jtInput.setString("fml_arg1",	text);	//1.주민 2.사업자 3.전체
			//jtInput.setString("fml_arg2",	text);	//주민번호
			//jtInput.setString("fml_arg3",	"");	//
			//jtInput.setString("fml_arg4",	"1");	//개인
			
			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult jtResult = jt_pt.call(context, request, jtInput, properties);			
			String retCode = jtResult.getString("fml_ret1").trim();
			
			debug("## retCode : "+retCode+"\n");
			
			String vipGrade 	= "";
			String vipMaxGrade	= ""; 
			String vipCardNo	= "";
			
			
			// 일반카드 또는 카드 없음
			if( retCode.equals("01")) {
			    //"PT회원이 아님 처리";
			}
			else if( !retCode.equals("00")) {
				//기타 다른 사유일경우 Skip 처리함;	
			}
			else if( retCode.equals("00")) { //PT카드 소지자일경우
			
				ArrayList vipCardList = new ArrayList();
				
				
				while( jtResult.isNext() ) 
				{
					jtResult.next();
					vipGrade = jtResult.getString("fml_ret9");					
					debug("@@@@@card_no : "+jtResult.getString("fml_ret3")+"  /  " +jtResult.getString("fml_ret9") + "  /  " +jtResult.getString("fml_ret6"));
					
					//일반카드는 제외하고
					if(vipGrade.compareTo("00") > 0) {
						
						if(jtResult.getString("fml_ret6").equals("1") || jtResult.getString("fml_ret6").equals("3")) { //1:본인 2:가족 3:지정
						
							CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
							
							
							cardVipInfo.setCardNo(jtResult.getString("fml_ret3"));
							cardVipInfo.setCardType(jtResult.getString("fml_ret9"));		//카드 등급
							cardVipInfo.setBankNo(jtResult.getString("fml_ret5"));			//회원사번호
							
							//cardVipInfo.addString("RESULT", "00");
							
							vipCardList.add(cardVipInfo);
							
						}
						
					}
					
				}
				ett.setCardVipInfoList(vipCardList);
				//ett.setVipCardExpDate(vipCardExpDate);
			
			}
				
		} catch (TaoException te) {
			//throw getErrorException("LOGIN_ERROR_0003",new String[]{"TOP카드 정보 조회 실패"},te);     // Jolt 처리 에러
		}

	}	
}
