/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 부킹 > 프리미엄 > 골프장 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfAdmMemStatsBkActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 어드민관리 > 회원관리 > 부킹통계";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			String type	= parser.getParameter("type", "nm");
			
			String type_nm	= "";
			if("nm".equals(type)){
				type_nm = "일반부킹";
			}else if("par3".equals(type)){
				type_nm = "파3 부킹";
			}else if("range".equals(type)){
				type_nm = "SKY72 드림골프레인지";
			}else if("duns".equals(type)){
				type_nm = "SKY72 드림듄스";
			}else if("jeju".equals(type)){
				type_nm = "제주골프할인";
			}else if("ls".equals(type)){
				type_nm = "레슨";
			}else if("vip".equals(type)){
				type_nm = "VIP 부킹";
			}else if("gr".equals(type)){
				type_nm = "그린피할인";
			}
			paramMap.put("type",type);
			paramMap.put("type_nm",type_nm);
			
			// 02.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("type", type);
			dataSet.setString("type_nm", type_nm);

			// 등급-연령
			GolfAdmMemStatsBkDaoProc proc = (GolfAdmMemStatsBkDaoProc)context.getProc("GolfAdmMemStatsBkDaoProc");
			DbTaoResult resultSet = (DbTaoResult) proc.execute(context, request, dataSet);

			request.setAttribute("resultSet", resultSet);
			request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
