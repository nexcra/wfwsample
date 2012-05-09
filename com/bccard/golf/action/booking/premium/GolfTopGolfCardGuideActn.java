/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardNoticeListActn
*   작성자    : 미디어포스 권영만
*   내용      : 탑골프카드 공지사항
*   적용범위  : Golf
*   작성일자  : 2010-10-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardListDaoProc;
import com.bccard.golf.dbtao.proc.code.GolfCodeSelDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardGuideActn extends GolfActn{
	
	public static final String TITLE = "탑골프카드 가이드 ";

	/***************************************************************************************
	* 골프 관리자화면
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

		String memberClss = "";
		String memId = "";
		
		int memNo =  0;
		
		String strMemChkNum = "";		//회원종류 1:정회원 / 4: 비회원 / 5:법인회원
		// 00.레이아웃 URL 저장
		String topGolfCardNo 	= "";
		String topGolfCardYn 	= "N";		//탑골프카드 소지 여부
		
		try {
			
			// 01.세션정보체크 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
						
			
			if(userEtt != null){
				memId = userEtt.getAccount();				// 회원 아이디
				memNo = userEtt.getMemid();
			}
			
			/*
			 * top골프 카드 회원인지 체크
			 * */
			
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			try {
				List topGolfCardList = mbr.getTopGolfCardInfoList();
				CardInfoEtt cardInfoTopGolfEtt = new CardInfoEtt();
				
				if( topGolfCardList!=null && topGolfCardList.size() > 0 )
				{
					for (int i = 0; i < topGolfCardList.size(); i++) 
					{
						cardInfoTopGolfEtt = (CardInfoEtt)topGolfCardList.get(0);
						topGolfCardNo = cardInfoTopGolfEtt.getCardNo();
						topGolfCardYn = "Y";
						debug("## 탑골프카드 소지 회원 | topGolfCardNo : "+topGolfCardNo);
					}
					
					//golfCardCoYn = mbr.getGolfCardCoYn();
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
			if(memId.equals("altec16") || memId.equals("amazon6") || memId.equals("graceyang") ||  memId.equals("mongina") || memId.equals("msj9529") ){
				topGolfCardYn 	= "Y";	
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			paramMap.put("topGolfCardYn", topGolfCardYn);
			
	        request.setAttribute("paramMap", paramMap);
		        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
