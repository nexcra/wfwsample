/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : TpAdmFPGoldGiftInqActn
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 > 월프프라임 수정
*   적용범위  : Topn
*   작성일자  : 2010-09-07
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfAdmWorldPrimeUpdActn extends AbstractAction {

	public static final String Title = "월프프라임 수정";
	
	/**
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionResponse
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException,BaseException
	{
		TaoConnection 		con 				= null;
		TaoResult 			result  			= null;
		Map 				paramMap 			= null;
		
		try {
			// form parameter parsing
			RequestParser parser 				= context.getRequestParser("default", request, response);						
			paramMap 							= (Map)request.getAttribute("paramMap");
			if(paramMap == null) paramMap = parser.getParameterMap();
			String actnKey 						= super.getActionKey(context);
			con = context.getTaoConnection("dbtao",null);
			
			String mode							= parser.getParameter("mode", "");
			
			
						
			// Proc 파라메터 설정
			TaoDataSet input 					= new DbTaoDataSet(Title);
			input.setString("actnKey", 			actnKey);
			input.setString("Title", 			Title);
			input.setString("mode",				mode);
			
			// 관리자 로그인 정보
			String adminId = "";
			HttpSession session = request.getSession(true);
			GolfAdminEtt userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){    
				adminId = (String)userEtt.getMemId();        
			}
			input.setString("adminId",			adminId);
			
			// 수정
			if("upd".equals(mode))
			{
				input.setString("pu_date2",		parser.getParameter("pu_date2", ""));
				input.setString("pgrs_yn",		parser.getParameter("pgrs_yn", ""));
				input.setString("payType",		parser.getParameter("payType", ""));
				input.setString("memJoinType",	parser.getParameter("memJoinType", ""));
				input.setString("memo_expl",	parser.getParameter("memo_expl", ""));
				input.setString("aplc_seq_no",	parser.getParameter("aplc_seq_no", ""));
				
				input.setString("callCardNo",	parser.getParameter("callCardNo", ""));
				input.setString("callIns",		parser.getParameter("callIns", ""));
				input.setString("callSttlAmt",	parser.getParameter("callSttlAmt", ""));
				input.setString("pay3Amt",		parser.getParameter("pay3Amt", ""));
				input.setString("pay3Name",		parser.getParameter("pay3Name", ""));
				input.setString("coNm",			parser.getParameter("coNm", ""));
				input.setString("cdhdID",		parser.getParameter("cdhdID", ""));
				
				result = con.execute("admin.event.GolfAdmWorldPrimeUpdDaoProc",input);
				
			}						
			else if("updBooking".equals(mode))
			{
				input.setString("evntPgrsClss",		parser.getParameter("evntPgrsClss", "R"));
				input.setString("mgrMemo",			parser.getParameter("mgrMemo", ""));
				input.setString("note",				parser.getParameter("note", ""));
				input.setString("comp_yn",				parser.getParameter("comp_yn", ""));
				input.setString("comp_num",				parser.getParameter("comp_num", ""));
				input.setString("comp_bkg_pe_nm_1",		parser.getParameter("comp_bkg_pe_nm_1", ""));
				input.setString("comp_bkg_pe_nm_2",		parser.getParameter("comp_bkg_pe_nm_2", ""));
				input.setString("comp_bkg_pe_nm_3",		parser.getParameter("comp_bkg_pe_nm_3", ""));				
				input.setString("cus_rmrk",			parser.getParameter("cus_rmrk", ""));
				input.setString("aplc_seq_no",		parser.getParameter("aplc_seq_no", ""));
				
				input.setString("rsvt_date",		parser.getParameter("rsvt_date", ""));
				input.setString("hadc_num",		parser.getParameter("hadc_num", ""));
				
				result = con.execute("admin.event.GolfAdmWorldPrimeUpdDaoProc",input);
			}
			
			debug("## GolfAdmWorldPrimeUpdActn | mode:"+mode);
			
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));   // 중복전송 체크						
			if("updBooking".equals(mode))
			{
				paramMap.put("aplc_seq_no", parser.getParameter("aplc_seq_no2", ""));
			}
			else
			{
				paramMap.put("aplc_seq_no", parser.getParameter("aplc_seq_no", ""));
			}
			
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);			
			request.setAttribute("mode", mode);


			
			
		} catch (BaseException be) {
			throw be;
		} catch (Throwable t) {
			MsgEtt ett = null;
			if (t instanceof MsgHandler) {
				ett = ((MsgHandler) t).getMsgEtt();
				ett.setTitle(Title);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR, Title, t.getMessage());
			}
		} finally {
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return getActionResponse(context, "default");
	}
	

}
