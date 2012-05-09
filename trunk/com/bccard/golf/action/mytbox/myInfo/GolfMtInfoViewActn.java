/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreGrViewActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 골프장 보기
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.myInfo;

import java.io.IOException;
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.login.CardNhInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentViewDaoProc;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMtInfoViewActn extends GolfActn{
	
	public static final String TITLE = "부킹 골프장 보기";

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
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			String gds_code 				= parser.getParameter("gds_code", "");
			String name 					= parser.getParameter("rcvr_nm", "");
			String zp1 						= parser.getParameter("zp1", "");
			String zp2 						= parser.getParameter("zp2", "");
			String zipaddr 					= parser.getParameter("addr", "");
			String detailaddr 				= parser.getParameter("dtl_addr", "");
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "");
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "");
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "");
			String gds_code_name 			= parser.getParameter("gds_code_name", "");
			String formtarget 				= parser.getParameter("formtarget", "");
			String openerType 				= parser.getParameter("openerType", "");
			if(GolfUtil.empty(openerType)){
				openerType = "U";
			}
			//debug("openerType : " + openerType);
			
			Map paramMap = parser.getParameterMap();
			paramMap.put("title", TITLE);

			paramMap.put("gds_code", gds_code);
			paramMap.put("name", name);
			paramMap.put("zp1", zp1);
			paramMap.put("zp2", zp2);
			paramMap.put("zipaddr", zipaddr);
			paramMap.put("detailaddr", detailaddr);
			paramMap.put("hp_ddd_no", hp_ddd_no);
			paramMap.put("hp_tel_hno", hp_tel_hno);
			paramMap.put("hp_tel_sno", hp_tel_sno);
			paramMap.put("gds_code_name", gds_code_name);

			paramMap.put("formtarget", formtarget);
			paramMap.put("openerType", openerType);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 04.실제 테이블(Proc) 조회
			GolfMtInfoViewDaoProc proc = (GolfMtInfoViewDaoProc)context.getProc("GolfMtInfoViewDaoProc");
			
			DbTaoResult myInfo = proc.execute(context, dataSet, request);
			String payWay = "";		// 결제 방법  yr:연회원, mn:월회원
			try {
				if(myInfo!=null && myInfo.isNext()){
					myInfo.next();
					payWay = myInfo.getString("payWay");
				}
			}catch(Throwable t){}
			
			paramMap.put("payWay", payWay);
			dataSet.setString("payWay", payWay); 
			
			DbTaoResult myCard = proc.execute_card(context, dataSet, request);
			DbTaoResult myList = proc.execute_list(context, dataSet, request);
			
			

			// 골프카드 정보 가져오기, 농협카드 가져오기 추가 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			
			String strCardJoinDate = "";
			String strGolfCardYn = "N";
			String strGolfCardNhYn = "N";
			String topGolfCardYn	= "N";
			String vipCardYn = "N";
			String richCardYn = "N";
			
			if (mbr != null) 
			{	
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				if( cardList.size() > 0 )
				{
					cardInfo = (CardInfoEtt)cardList.get(0);
					strCardJoinDate = cardInfo.getAcctDay();	// 카드가입일
					strGolfCardYn	= "Y";
				}
				
				List cardNhList = mbr.getCardNhInfoList();
				CardNhInfoEtt cardNhInfo = new CardNhInfoEtt();
				
				if( cardNhList!=null && cardNhList.size() > 0 )
				{
					cardNhInfo = (CardNhInfoEtt)cardNhList.get(0);
					strGolfCardNhYn	= "Y";
				}
				
				//탑골프카드 소지여부 체크
				try {
					List topGolfCardList = mbr.getTopGolfCardInfoList();
					if( topGolfCardList!=null && topGolfCardList.size() > 0 )
					{
						for (int i = 0; i < topGolfCardList.size(); i++) 
						{
							
							topGolfCardYn = "Y";
							debug("## 탑골프카드 소지 회원");
						}
					}
					else
					{
						topGolfCardYn = "N";
						debug("## 탑골프카드 미소지");					
					}
				} catch(Throwable t) 
				{
					topGolfCardYn = "N";
					debug("## 탑골프카드 체크 에러");	
				}
				
				//VIP카드 소지여부 체크 2010.09.14 권영만
				String select_grade_no = StrUtil.isNull(mbr.getVipMaxGrade(), ""); // grade ==>  03:e-PT, 12:PT12, 30:다이아몬드, 91:인피니티
				//if (select_grade_no.equals("30")) select_grade_no = "12";	
								
				debug("## VIP카드 소지 체크 시작 | select_grade_no : "+select_grade_no);
				try {
					List cardVipList = mbr.getCardVipInfoList();								
					if( cardVipList!=null && cardVipList.size() > 0 )
					{
						
						if(!"00".equals(select_grade_no))	// 플래티넘 회원일 경우	
						{
							
							for (int i = 0; i < cardVipList.size(); i++) 
							{
								
								vipCardYn = "Y";
								debug("## VIP카드 소지");	
							}
							
							
						}
						else
						{
							vipCardYn = "N";
							debug("## VIP플래티늄 회원 아님");						
						}
						
						
					
					}
					else
					{
						vipCardYn = "N";
						debug("## VIP카드 소지 안함.");	
					}
				} catch(Throwable t) 
				{
					vipCardYn = "N";
					debug("## VIP카드 체크 에러");	
				}
				
				
				//기존 회원이 유료회원인지 체크
				String memCk = "N";

debug("intMemGrade :" + usrEntity.getIntMemGrade());
debug("intCardGrade :" + usrEntity.getIntCardGrade());

				if("Y".equals(vipCardYn))
				{
					GolfMemCardInsDaoProc mem_proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
					dataSet.setString("intMemGrade",  usrEntity.getIntMemGrade()+""); 
					dataSet.setString("intCardGrade", usrEntity.getIntCardGrade()+""); 
					memCk = mem_proc.memCk(context, dataSet, request);											
					
				}
				request.setAttribute("vipMemCk", memCk);			
				
				
				//리치카드 소지여부 체크
				try {
					List richCardList = mbr.getRichCardInfoList();
					if( richCardList!=null && richCardList.size() > 0 )
					{
						for (int i = 0; i < richCardList.size(); i++) 
						{
							
							richCardYn = "Y";
							debug("## 리치카드 소지 회원");
						}
					}
					else
					{
						richCardYn = "N";
						debug("## 리치카드 미소지");					
					}
				} catch(Throwable t) 
				{
					richCardYn = "N";
					debug("## 리치카드 체크 에러");	
				}

			}
			
			boolean smartYn = false;
			String smartGrd = "";
			
			
			//스마트 등급을 소유하고 있는지
			for (int i=0; i < myCard.size(); i++){	
				
				myCard.next();
				
				if ( myCard.getString("RESULT").equals("00")){						
					
					smartGrd = myCard.getString("CARD_SEQ");
					
					if ( smartGrd.equals(AppConfig.getDataCodeProp("0052CODE7"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE8"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE9"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE10"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE11"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE19"))
					){	
						smartYn = true;
					}
				}
					
			}
			
			
			/*스마트등급에 해당되면서 VIP카드 소유한 경우
			 *할인가능 페이지 스킵하도록 함
			 *2011.06.30 Loun.G 최종욱 차장 확인 
			 */
			if (vipCardYn.equals("Y")){
				
				String strCardJoinNo ="";
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				if( cardList.size() > 0 ){				
					cardInfo = (CardInfoEtt)cardList.get(0);
					strCardJoinNo = cardInfo.getJoinNo();	// 제휴코드
					
					//Smart300에 해당 (기업은행)
					if (strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
							||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
							||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub"))){					
						vipCardYn = "N";
					}
				}	
				
				//모든 스마트 등급(오퍼등 월회원 결제등등)
				if (smartYn){
					vipCardYn = "N";
				}
				
			}
			
			System.out.print("### strGolfCardNhYn:"+strGolfCardNhYn+"\n");
			
			

			
			// 선택한 사은품 가져오기
			GolfMemPresentViewDaoProc present_proc = (GolfMemPresentViewDaoProc)context.getProc("GolfMemPresentViewDaoProc");
			DbTaoResult presentView = present_proc.execute(context, dataSet, request);
	        request.setAttribute("presentView", presentView);
			
			
			// 05. Return 값 세팅			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			request.setAttribute("myInfo", myInfo);	
			request.setAttribute("myCard", myCard);		// 카드 정보
			request.setAttribute("myList", myList);		// 업그레이드 등급
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
	        
	        request.setAttribute("strGolfCardYn", strGolfCardYn); 		//골프카드유무
	        request.setAttribute("strCardJoinDate", strCardJoinDate); 	//카드시작일
	        request.setAttribute("strGolfCardNhYn", strGolfCardNhYn); 	//농협카드유무
	        request.setAttribute("topGolfCardYn", topGolfCardYn);		//탑골프카드 유무
	        
	        request.setAttribute("vipCardYn", vipCardYn);
	        request.setAttribute("richCardYn", richCardYn);
	        request.setAttribute("CardGrade", usrEntity.getIntCardGrade()+"");   //카드등급 
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
