/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpApplicantInqActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한 레슨이벤트 > 레슨이벤트 목록
*   적용범위	: golf
*   작성일자	: 2009-07-07
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.special.applicant;

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
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.event.special.applicant.GolfEvntSpApplicantInqDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/ 
public class GolfEvntSpApplicantInqActn extends GolfActn{
	
	public static final String TITLE = "이벤트라운지 > 특별한 레슨이벤트 > 레슨이벤트 목록";
 
	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체.  
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		int intMemGrade = 0;
		String evntUseYn = "";
		 
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try { 
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
				intMemGrade = usrEntity.getIntMemGrade();
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			String evnt_clss 	= "0003";
			String search_clss 	= parser.getParameter("search_clss","");
			String search_word 	= parser.getParameter("search_word","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no",    		page_no);
			dataSet.setString("evnt_clss",		evnt_clss);
			dataSet.setString("search_clss",	search_clss);
			dataSet.setString("search_word",	search_word);
			dataSet.setString("userId",			userId);
			
		
			// 04.실제 테이블(Proc) 조회
			GolfEvntSpApplicantInqDaoProc proc = (GolfEvntSpApplicantInqDaoProc)context.getProc("GolfEvntSpApplicantInqDaoProc");
			DbTaoResult boardInq = (DbTaoResult)proc.execute(context, request,dataSet);
			request.setAttribute("boardInq", boardInq);
			
			//사용권한이 있는지 체크 
			//String evntUseYn = proc.getUseYnChk(context, userId) ;
			
			String permissionColum = "SP_LESN_EVNT_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				evntUseYn = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				evntUseYn = "N";
			}
			request.setAttribute("evntUseYn",evntUseYn );
			
			// 05.모든 파라미터값을 맵에 담아 반환한다.		
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_word", search_word);
			paramMap.put("page_no", 	Long.toString(page_no));
	        request.setAttribute("paramMap", paramMap); 	
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}

}
