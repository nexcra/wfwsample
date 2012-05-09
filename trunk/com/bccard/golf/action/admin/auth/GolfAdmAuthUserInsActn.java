/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthUserInsActn
*   작성자    : (주)미디어포스 조은미
*   내용      : BBS 
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.bccard.golf.common.GolfActn;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.proc.admin.*;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmAuthUserInsActn extends GolfActn  {

	public static final String TITLE="비씨골프 운영자 관리 입력";

	/***************************************************************************************
	* 비씨골프 BBS화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
    public ActionResponse execute( WaContext context, HttpServletRequest request, HttpServletResponse response)
		throws BaseException {

		String rtnMsg = "";
		String rtnCode = "";
		int isOk = 0;

        try{
        	//debug("==== GolfAdmAuthUserInsActn start ===");
			// 정보등록 화면으로
			RequestParser parser = context.getRequestParser("default", request, response);
	    	String ip_addr = request.getRemoteAddr();

			DbTaoDataSet dataSet = new DbTaoDataSet("Golf 운영자 관리 입력");
			dataSet.setString("account", parser.getParameter("ACCOUNT", ""));		// 아이디
			dataSet.setString("passwd", parser.getParameter("PASSWD", ""));			// 암호
			dataSet.setString("jumin_no", parser.getParameter("JUMIN_NO", ""));		// 주민등록번호
			dataSet.setString("name", parser.getParameter("HG_NM", ""));			// 성명
			dataSet.setString("email", parser.getParameter("EMAIL", ""));			// 이메일
			dataSet.setString("com_nm", parser.getParameter("COM_NM", ""));			// 업체명
			dataSet.setString("prs_nm", parser.getParameter("PRS_NM", ""));			// 대표자명
			dataSet.setString("zipcode1", parser.getParameter("ZIPCODE1", ""));		// 우편번호
			dataSet.setString("zipcode2", parser.getParameter("ZIPCODE2", ""));		// 우편번호
			dataSet.setString("zipaddr", parser.getParameter("ZIPADDR", ""));		// 주소
			dataSet.setString("detailaddr", parser.getParameter("DETAILADDR", ""));	// 상세주소
			dataSet.setString("tel1", parser.getParameter("TEL1", ""));				// 전화1
			dataSet.setString("tel2", parser.getParameter("TEL2", ""));				// 전화2
			dataSet.setString("tel3", parser.getParameter("TEL3", ""));				// 전화3
			dataSet.setString("fax1", parser.getParameter("FX_DDD_NO", ""));		// 팩스1
			dataSet.setString("fax2", parser.getParameter("FX_TEL_HNO", ""));		// 팩스2
			dataSet.setString("fax3", parser.getParameter("FX_TEL_SNO", ""));		// 팩스3
			dataSet.setString("hp_tel_no1", parser.getParameter("HP_DDD_NO", ""));	// 핸드폰1
			dataSet.setString("hp_tel_no2", parser.getParameter("HP_TEL_HNO", ""));	// 핸드폰2
			dataSet.setString("hp_tel_no3", parser.getParameter("HP_TEL_SNO", ""));	// 핸드폰3
			dataSet.setString("memo", parser.getParameter("MEMO", ""));				// 메모
			dataSet.setString("ip_addr", ip_addr);									// 접속아이피
			dataSet.setString("INDV_INFO_RPES_NM", parser.getParameter("INDV_INFO_RPES_NM", ""));	// 개인정보책임자성명

			GolfAdmAuthUserInsDaoProc proc1 = (GolfAdmAuthUserInsDaoProc) context.getProc("GolfAdmAuthUserInsDaoProc");
			isOk = proc1.execute(context, dataSet);	
			
			if (isOk > 0  ) {
				rtnMsg = "등록에 성공하였습니다.";
				rtnCode = "00";
			} else if (isOk == -1  ) {
					rtnMsg = "중복되는 주민등록번호가  있습니다.";
					rtnCode = "02";
			} else {
				rtnMsg = "등록에 실패하였습니다.";
				rtnCode = "01";
			}

	    	Map paramMap = parser.getParameterMap();
		    //paramMap.put("rtnMsg", rtnMsg);
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("rtnCode", rtnCode);
			request.setAttribute("rtnMsg", rtnMsg);
		    
		    //debug("==== GolfAdmAuthUserInsActn End ===" + rtnMsg);
        }catch(Throwable t){
        	//debug("==== GolfAdmAuthUserInsActn Error ========" + t);
        	return errorHandler(context,request,response,t);
	    }
        return super.getActionResponse(context);
	}//execute end
}
