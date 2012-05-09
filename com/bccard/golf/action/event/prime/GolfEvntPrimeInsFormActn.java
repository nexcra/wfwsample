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
package com.bccard.golf.action.event.prime;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopViewDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntPrimeInsFormActn extends GolfActn{
	
	public static final String TITLE = "쇼핑 주문 페이지";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		
		String userNm = "";
		String userId = "";
		String juminno = ""; 
		String juminno1 = ""; 
		String juminno2 = ""; 
		String zip_code1 = "";
		String zip_code2 = "";
		String zipaddr = "";
		String detailaddr = "";
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		String phone1 = "";
		String phone2 = "";
		String phone3 = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try { 
			// 01.세션정보체크
			//HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			// 02.입력값 조회		
			//RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 04.실제 테이블(Proc) 조회
			GolfEvntShopViewDaoProc proc = (GolfEvntShopViewDaoProc)context.getProc("GolfEvntShopViewDaoProc");
			
			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				juminno1	= juminno.substring(0, 6);
				juminno2	= juminno.substring(6, 13);
				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
				phone1 		= (String)usrEntity.getPhone1(); 
				phone2 		= (String)usrEntity.getPhone2(); 
				phone3 		= (String)usrEntity.getPhone3(); 
				
				dataSet.setString("userId", userId);
				DbTaoResult memEtt = (DbTaoResult) proc.execute_mem(context, request, dataSet);
				if (memEtt !=null && memEtt.isNext() && memEtt.size() > 0) {
					memEtt.next();							
					zip_code1 = memEtt.getString("ZIP_CODE1");
					zip_code2 = memEtt.getString("ZIP_CODE2");
					zipaddr = memEtt.getString("ZIPADDR");
					detailaddr = memEtt.getString("ZIPADDR") + " " + memEtt.getString("DETAILADDR");
				}

				paramMap.put("userNm", userNm);
				paramMap.put("userId", userId);
				paramMap.put("juminno", juminno);
				paramMap.put("juminno1", juminno1);
				paramMap.put("juminno2", juminno2);
				paramMap.put("mobile1", mobile1);
				paramMap.put("mobile2", mobile2);
				paramMap.put("mobile3", mobile3);
				paramMap.put("phone1", phone1);
				paramMap.put("phone2", phone2);
				paramMap.put("phone3", phone3);
				paramMap.put("zip_code1", zip_code1);
				paramMap.put("zip_code2", zip_code2);
				paramMap.put("zipaddr", zipaddr);
				paramMap.put("detailaddr", detailaddr);
			}

			// 주문코드 가져오기
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			String order_no = addPayProc.getOrderNo(context, dataSet);
			paramMap.put("order_no", order_no);			
			
//			debug("userNm : " + userNm + " / userId : " + userId + " / juminno : " + juminno + " / juminno1 : " + juminno1 + " / juminno2 : " + juminno2
//					+ " / mobile1 : " + mobile1 + " / mobile2 : " + mobile2 + " / mobile3 : " + mobile3
//					+ " / phone1 : " + phone1 + " / phone2 : " + phone2 + " / phone3 : " + phone3
//					+ " / zip_code1 : " + zip_code1 + " / zip_code2 : " + zip_code2 + " / zipaddr : " + zipaddr + " / detailaddr : " + detailaddr + " / order_no : " + order_no);
			

	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
