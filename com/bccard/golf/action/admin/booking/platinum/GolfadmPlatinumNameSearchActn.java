/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPlatinumNameSearchActn
*   작성자    : 이정규
*   내용      : 이름으로 정보 결과
*   적용범위  : Golf
*   작성일자  : 2010-09-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.platinum;


import java.io.IOException; 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.platinum.GolfadmPlatinumListDaoProc;
import com.bccard.golf.jolt.JtProcess;


/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfadmPlatinumNameSearchActn extends GolfActn{
	
	public static final String TITLE = "이름으로 정보 가져와 결과 뿌려주기"; 

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
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
			// 01.세션정보체크
			
			// 02.입력값 조회	 	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String sch_type = parser.getParameter("SCH_TYPE","");
			String sch_text = parser.getParameter("SCH_TEXT","");
			//String sch_name = parser.getParameter("SCH_NAME","");
			
			
			debug("sch_text@@@@@@@@@@@@@@@@@@@값 : "+sch_text);
			debug("sch_type@@@@@@@@@@@@@@@@@@@값 : "+sch_type);
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SCH_TYPE", sch_type);
			dataSet.setString("SCH_TEXT", sch_text);
			
			
								
			// 04.실제 테이블(Proc) 조회 - 리스트
			GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");
			DbTaoResult listResult = null;
			
			if(sch_type.equals("SCH_NAME")){
				if(!sch_text.equals(""))
				listResult = (DbTaoResult) proc.searchName(context, request, dataSet);
			}else if(sch_type.equals("SCH_GOLFLOUNG")){
				if(!sch_text.equals(""))
				listResult = (DbTaoResult) proc.searchGolfloung(context, request, dataSet);
			}else if(sch_type.equals("SCH_JUMIN")){
				if(!sch_text.equals("")){}
				//listResult = (DbTaoResult) proc.searchJumin(context, request, dataSet);
					try{
						listResult =checkJoltVip(context, request, sch_text);
					}catch (Throwable t){
						ArrayList card_list = new ArrayList();
					}
				
				
			}
			
			
			//DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result)){
				request.setAttribute("total_cnt", listResult.getString("TOT_CNT"));
			}
			else{
				request.setAttribute("total_cnt", "0");
			}
			
			
			//검색 부분 셋팅
			paramMap.put("page_no",Long.toString(page_no));
			paramMap.put("record_size",Long.toString(record_size));
			request.setAttribute("search_type", listResult.getString("TYPE"));
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("ListResult", listResult);
	        
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
	protected DbTaoResult checkJoltVip(WaContext context, HttpServletRequest request, String text) throws BaseException {

		java.util.Properties properties = new java.util.Properties();

		properties.setProperty("GetVIPCARD", "Y");  					// 필수 로그에 남길 해당 전문의 return RETURN_CODE 키. 설정 안하면 "fml_ret1" 사용
		properties.setProperty("RETURN_CODE", "fml_ret1");		// 만약 특정한 pool 을 사용하는 경우라면.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", text); 	// log 찍어주는 부분에서 주민번호 대체
		DbTaoResult rtnList = null;
		try {

			/** *****************************************************************
			 *Card정보를 읽어오기
			 ***************************************************************** */
			System.out.println("## GolfCtrlServ VIP카드 | 1. VIP카드 Jolt MHL0200R0200 전문 호출 <<<<<<<<<<<<");
						
			String joltFmlTrCode016 = "MHL0200R0200";
			String joltServiceName = "BSNINPT";
			JoltInput jtInput = new JoltInput(joltServiceName);
			jtInput.setServiceName(joltServiceName);
			jtInput.setString("fml_trcode", joltFmlTrCode016);			
			jtInput.setString("fml_arg1",	text);	//주민번호 조회
			
			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult jtResult = jt_pt.call(context, request, jtInput, properties);			
			String retCode = jtResult.getString("fml_ret1").trim();
			
			System.out.println("## retCode : "+retCode+"\n");
			
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
														
					//일반카드는 제외하고
					if(vipGrade.compareTo("00") > 0) {
					
						System.out.println("## VIPCARD vipGrade : "+vipGrade+" | vipMaxGrade : "+vipMaxGrade+"\n");
						
						//CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
						
						
						rtnList.addString("CARD_NO", jtResult.getString("fml_ret3"));	//카드 번호
						rtnList.addString("TYPE", "jumin");
						rtnList.addString("RESULT", "00");
						
//						cardVipInfo.setBankNo(jtResult.getString("fml_ret5"));
//						cardVipInfo.setCardNo(jtResult.getString("fml_ret3"));
//						cardVipInfo.setCardType(jtResult.getString("fml_ret6"));
//						//cardVipInfo.setJoinNo(jtResult.getString("fml_ret8"));
//						//cardVipInfo.setJoinName(jtResult.getString("fml_ret7"));
//						cardVipInfo.setAcctDay(jtResult.getString("fml_ret11"));
//						cardVipInfo.setCardAppType(jtResult.getString("fml_ret17"));
//						cardVipInfo.setExpDate(jtResult.getString("fml_ret13"));
//						cardVipInfo.setAppDate(jtResult.getString("fml_ret14"));
//						cardVipInfo.setLastCardNo(jtResult.getString("fml_ret15"));
//						//cardVipInfo.setSocId(jtResult.getString("fml_ret16"));
//						cardVipInfo.setVipGrade(vipGrade);
						
						//vipCardList.add(cardVipInfo);
						//최고등급만
						/*if( vipMaxGrade.compareTo(vipGrade) < 0) {
							vipMaxGrade = vipGrade;
							vipCardNo		= jtResult.getString("fml_ret3");							
							//info("vipMaxGrade:"+vipMaxGrade);
						}*/
					}
					
				}
				System.out.println("## vipMaxGrade 최종값 : "+vipMaxGrade+"\n");
				
//				ett.setCardVipInfoList(vipCardList);
//				ett.setVipMaxGrade(vipMaxGrade);
//				ett.setVipCardNo(vipCardNo);
				//ett.setVipCardExpDate(vipCardExpDate);
			
			}
			
						

			
		}  catch (TaoException te) {
			//throw getErrorException("LOGIN_ERROR_0003",new String[]{"TOP카드 정보 조회 실패"},te);     // Jolt 처리 에러
		}
		return rtnList;
	}	
}
