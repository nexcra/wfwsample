/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpApplicantDetailActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한 레슨이벤트 > 레슨이벤트 상세보기
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.event.special.applicant.GolfEvntSpApplicantDetailDaoProc;
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
public class GolfEvntSpApplicantDetailActn extends GolfActn{
	
	public static final String TITLE = "이벤트라운지 > 특별한 레슨이벤트 > 레슨이벤트 상세보기";
 
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
		String hp_ddd_no = ""; 
		String hp_tel_hno = "";
		String hp_tel_sno = "";
		String userSex = "";
		String email = "";
		String memGrade = "";
		String permission = "";
		int intMemGrade = 0;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try { 
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				 
				userId		= (String)usrEntity.getAccount();
				userSex 	= (String)usrEntity.getSex();
				email 		= (String)usrEntity.getEmail1();
				hp_ddd_no 	= (String)usrEntity.getMobile1();
				hp_tel_hno 	= (String)usrEntity.getMobile2();
				hp_tel_sno 	= (String)usrEntity.getMobile3();
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			String evnt_clss 	= "0003";
			String golf_svc_aplc_clss = "0005";
			String p_idx 		= parser.getParameter("p_idx","");
			String status 		= parser.getParameter("status","");
			String search_clss 	= parser.getParameter("search_clss","");
			String search_word 	= parser.getParameter("search_word","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("evnt_clss",	evnt_clss);
			dataSet.setString("p_idx",		p_idx);
			dataSet.setString("userId",		userId);
			dataSet.setString("golf_svc_aplc_clss",	golf_svc_aplc_clss);
			
			// 04.실제 테이블(Proc) 조회
			GolfEvntSpApplicantDetailDaoProc proc = (GolfEvntSpApplicantDetailDaoProc)context.getProc("GolfEvntSpApplicantDetailDaoProc");
			DbTaoResult boardDetail = (DbTaoResult)proc.execute(context, request,dataSet);
			request.setAttribute("boardDetail", boardDetail);
		 	
			//debug("-----------------------   action : status : "+status);
			
			//사용권한이 있는지 체크
			/*
			GolfEvntSpApplicantInqDaoProc proc_chk = (GolfEvntSpApplicantInqDaoProc)context.getProc("GolfEvntSpApplicantInqDaoProc");
			String evntUseYn = proc_chk.getUseYnChk(context, userId) ;
			
			*/
			String permissionColum = "SP_LESN_EVNT_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			request.setAttribute("evntUseYn",permission );
			
			
			// 05.모든 파라미터값을 맵에 담아 반환한다.	
			
			paramMap.put("p_idx", 		p_idx);
			paramMap.put("status", 		status);
			paramMap.put("intMemGrade", Integer.toString(intMemGrade));
			paramMap.put("userId", 		userId);
			paramMap.put("userSex", 	userSex);
			paramMap.put("email", 		email); 
			paramMap.put("hp_ddd_no", 	hp_ddd_no); 
			paramMap.put("hp_tel_hno", 	hp_tel_hno);
			paramMap.put("hp_tel_sno", 	hp_tel_sno);
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_word", search_word);
			paramMap.put("page_no", 	Long.toString(page_no));
			paramMap.put("img_path",    AppConfig.getAppProperty("IMG_URL_REAL")+"/event");
	        request.setAttribute("paramMap", paramMap); 	
			
		} catch(Throwable t) { 
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
