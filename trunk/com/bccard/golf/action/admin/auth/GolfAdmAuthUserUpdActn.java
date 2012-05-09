/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthUserUpdActn
*   작성자    : (주)미디어포스 조은미
*   내용      : 수정(action)
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfUtil;
//import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfAdminEtt;
//import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.*;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import com.bccard.golf.msg.MsgEtt;
/*************************************************************
 * 글수정 액션
 * @author 
 * @version 2008.12.19 
 ************************************************************/
public class GolfAdmAuthUserUpdActn extends GolfActn {

	public static final String TITLE ="GOLF 운영자 관리 수정PROC";

	/********************************************************************
	* EXECUTE
	* @param context		WaContext 객체.
	* @param request		HttpServletRequest 객체.
	* @param response		HttpServletResponse 객체.
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보.
	******************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) {
		String rtnCode = "";
		String rtnMsg = "";

		try{
			//debug("==== GolfAdmAuthUserUpdActn 수정 start ===");			
			String ins_result = "01";
			RequestParser parser = context.getRequestParser("default", request, response);

			String ip_addr = request.getRemoteAddr();

			//debug("p_idx============" + parser.getParameter("p_idx","") + "/n");

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("name", parser.getParameter("HG_NM", ""));			// 성명
			dataSet.setString("jumin_no", parser.getParameter("JUMIN_NO", ""));		// 주민등록번호
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
			dataSet.setString("p_idx", parser.getParameter("p_idx", ""));		// seq
			dataSet.setString("INDV_INFO_RPES_NM", parser.getParameter("INDV_INFO_RPES_NM", ""));		// 개인정보책임자

			GolfAdmAuthUserUpdDaoProc proc = (GolfAdmAuthUserUpdDaoProc)context.getProc("GolfAdmAuthUserUpdDaoProc");
			int isOk = proc.execute(context, dataSet);
			
			if (isOk > 0  ) {
				rtnCode = "00";
				rtnMsg = "수정에 성공하였습니다.";

				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"USER_DN_DIFFERENT", null);
				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_EXCEPTION", new String[]{contTitle});
				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"JT_RET_ERR", new String[]{contTitle, comm_seqno});
				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"문구입력.");
				//throw new GolfException(msgEtt);
			} else {
				rtnCode = "01"; // 수납이 안된경우
				rtnMsg = "수정에 실패하였습니다.";
			}

			Map paramMap = parser.getParameterMap();	
			//paramMap.put("comm_seqno", comm_seqno);

			request.setAttribute("paramMap", paramMap);
			request.setAttribute("rtnMsg",rtnMsg);
			request.setAttribute("rtnCode",rtnCode);

			//debug("==== GolfAdmAuthUserUpdActn 수정 End ===");
		} catch(Throwable t) {
			//debug("==== GolfAdmAuthUserUpdActn 수정 Error ===");
			return errorHandler(context,request,response,t);
		}finally{
		}
		return super.getActionResponse(context);
	}
}