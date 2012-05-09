/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkCheckJjListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 > 제주그린피 확인
*   적용범위  : Golf
*   작성일자  : 2009-05-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.check;

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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.login.CardNhInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.check.*;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfBkCheckNmListActn extends GolfActn{
	
	public static final String TITLE = "제주그린피 확인";

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
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("LISTTYPE", "");
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			// 골프카드 정보 가져오기, 농협카드 가져오기 추가 
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
				if("Y".equals(vipCardYn))
				{
					GolfMemCardInsDaoProc mem_proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
					dataSet.setString("intMemGrade", usrEntity.getIntMemGrade()+""); 
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
			request.setAttribute("strGolfCardYn", strGolfCardYn); 		//골프카드유무
		    request.setAttribute("strCardJoinDate", strCardJoinDate); 	//카드시작일
	        request.setAttribute("strGolfCardNhYn", strGolfCardNhYn); 	//농협카드유무
	        request.setAttribute("topGolfCardYn", topGolfCardYn);		//탑골프카드 유무	        
	        request.setAttribute("vipCardYn", vipCardYn);
	        request.setAttribute("richCardYn", richCardYn);
						
			

			// 04.실제 테이블(Proc) 조회 - 나의 부킹 전체 정보
			GolfBkCheckAllViewDaoProc proc = (GolfBkCheckAllViewDaoProc)context.getProc("GolfBkCheckAllViewDaoProc");
			DbTaoResult allView = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("AllView", allView);
							
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
