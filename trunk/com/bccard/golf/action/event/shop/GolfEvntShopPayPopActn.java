/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntShopPayPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 결제 팝업 
*   적용범위  : Golf
*   작성일자  : 2010-03-08
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*20110323  이경희 	보이스캐디 쇼핑
*20110425  이경희 	골프퍼팅3홀컵 + 골프퍼팅매트세트
*20120307 SHIN CHEONG GWI 수정내용 : 보이스캐디 쇼핑--> 골프버디보이스로 변경
***************************************************************************************************/
package com.bccard.golf.action.event.shop;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntShopPayPopActn extends GolfActn{
	
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

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		
		try { 

			// 02.입력값 조회	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = parser.getParameterMap();
			paramMap.put("title", TITLE);
			
			String gds_code			= "";												// 상품코드
			//String gds_code			= parser.getParameter("gds_code", "");			// 상품코드
			//String sgl_lst_itm_code	= parser.getParameter("sgl_lst_itm_code", "");	// 옵션
			int int_atm				= 0;											// 상품금액
			int total_atm			= 0;											// 결제금액
			String gds_nm			= "";											// 상품명

			int productPrice		= parser.getIntParameter("productPrice",0);		// 상품가격
			int qty					= parser.getIntParameter("qty",0);				// 수량
			String userNm 			= parser.getParameter("userNm", "");			// 회원이름
			String flag				= parser.getParameter("flag","");				// 구분

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			//dataSet.setString("gds_code", gds_code);

			// 04.실제 테이블(Proc) 조회
			// 상품 정보 가져오기
			/*
			GolfEvntShopViewDaoProc proc = (GolfEvntShopViewDaoProc)context.getProc("GolfEvntShopViewDaoProc");
			DbTaoResult goodsEtt = (DbTaoResult) proc.execute(context, request, dataSet);
			if (goodsEtt !=null && goodsEtt.isNext() && goodsEtt.size() > 0) {
				goodsEtt.next();		
				int_atm = goodsEtt.getInt("INT_AMT");
				gds_nm = goodsEtt.getString("GDS_NM");
				total_atm = int_atm*qty;
			}
			
			paramMap.put("gds_code", gds_code);
			paramMap.put("gds_nm", gds_nm);
			paramMap.put("sgl_lst_itm_code", sgl_lst_itm_code);
			paramMap.put("qty", qty+"");
			paramMap.put("total_atm", total_atm+"");*/
			
			/*구체적인 쇼핑 프로세스 없이 임시 방편으로 오픈
			 * 차후 확장시 다시 구체화해야함
			 * 따라서 아래 상품 하나만 하드코딩함
			*/
			if (!flag.equals("B")){ 
				/* 2012.03.07 주석
				gds_nm = "보이스캐디";
				gds_code = "2011040101";
				*/
				gds_nm = "골프버디보이스";		// 2012.03.07 추가
				gds_code = "2011040103";
				
				paramMap.put("userNm", userNm);
				paramMap.put("gds_code", gds_code);			
			}else {
				gds_nm = "골프퍼팅3홀컵 + 골프퍼팅매트세트";
				gds_code = "2011040102";
				paramMap.put("userNm", userNm);
				paramMap.put("gds_code", gds_code);	
			}
			
			total_atm = productPrice * qty;
			
			paramMap.put("gds_nm", gds_nm);
			paramMap.put("total_atm", total_atm+"");
			paramMap.put("userNm", userNm);
			
			// 주문코드 가져오기
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			String order_no = addPayProc.getOrderNo(context, dataSet);
			paramMap.put("order_no", order_no);
			
			debug("GolfEvntShopPayPopActn :: order_no : " + order_no + " / int_atm : " + int_atm+ " / qty : " + qty+ " / total_atm : " + total_atm 
					+ " / gds_code : " + gds_code + " / gds_nm : " + gds_nm);

	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
