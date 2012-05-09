/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntShopListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 리스트 
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.shop;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntShopListActn extends GolfActn{
	
	public static final String TITLE = "쇼핑 리스트";

	/***************************************************************************************
	* 골프 사용자화면
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
			String img_root = AppConfig.getAppProperty("TOP_IMG_URL_MAPPING_DIR");
			String brnd_seq_no = AppConfig.getAppProperty("TOP_BRAND_SEQ");			
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("brnd_seq_no", brnd_seq_no);

			// 04.실제 테이블(Proc) 조회
			GolfEvntShopListDaoProc proc = (GolfEvntShopListDaoProc)context.getProc("GolfEvntShopListDaoProc");		
			
			dataSet.setString("brnd_clss", "001");
			DbTaoResult evntShopListResult1 = (DbTaoResult) proc.execute(context, request, dataSet); 

			dataSet.setString("brnd_clss", "002");
			DbTaoResult evntShopListResult2 = (DbTaoResult) proc.execute(context, request, dataSet); 

			dataSet.setString("brnd_clss", "003");
			DbTaoResult evntShopListResult3 = (DbTaoResult) proc.execute(context, request, dataSet); 

			dataSet.setString("brnd_clss", "004");		
			DbTaoResult evntShopListResult4 = (DbTaoResult) proc.execute(context, request, dataSet); 
					
			
			request.setAttribute("evntShopListResult1", evntShopListResult1);
			request.setAttribute("evntShopListResult2", evntShopListResult2);
			request.setAttribute("evntShopListResult3", evntShopListResult3);
			request.setAttribute("evntShopListResult4", evntShopListResult4);
			
			paramMap.put("img_root", img_root);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
