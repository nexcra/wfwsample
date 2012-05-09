/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 가입 > 등록
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemDelFormDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemEvntDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemDelFormActn extends GolfActn{
	
	public static final String TITLE = "가입 > 해지폼";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		String jumin_no ="";
		int intMemGrade =0;
		int intCardGrade = 0;
						
		try {
			// 회원통합테이블 관련 수정사항 진행
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
				jumin_no		= (String)usrEntity.getSocid(); 
				intMemGrade		= (int)usrEntity.getIntMemGrade(); 
				intCardGrade	= (int)usrEntity.getIntCardGrade(); 
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("evntNo", "109" );	
			dataSet.setString("juminNo", jumin_no);	

			String join_chnl = "";
			String affi_firm_nm = "";
			String cancle_able_yn = "";
			String champ_seq_no = "";

			// 04.이벤트 테이블(Proc) 조회			
			GolfMemEvntDaoProc event = (GolfMemEvntDaoProc)context.getProc("GolfMemEvntDaoProc");
			DbTaoResult addEvent = event.execute(context, dataSet, request);
			if (addEvent != null && addEvent.isNext()) {
				addEvent.first();
				addEvent.next();
				String pwin_date = (String) addEvent.getString("PWIN_DATE");
				String end_date = (String) addEvent.getString("END_DATE");
				String to_date = (String) addEvent.getString("TO_DATE");
				
				if( Integer.parseInt(to_date) <= Integer.parseInt(end_date)) {
					cancle_able_yn = "E";
				}
			}	

			// 05.실제 테이블(Proc) 조회			
			GolfMemDelFormDaoProc proc = (GolfMemDelFormDaoProc)context.getProc("GolfMemDelFormDaoProc");
			int cnt = proc.execute(context, dataSet, request);
			
			if (cnt > 0){
				//해지가능
				cancle_able_yn = "Y";
			}else{
				//해지불가
				cancle_able_yn = "N";
			}

			// 05.실제 테이블(Proc) 조회	- 사은품 선택한 챔피언은 탈퇴불가
			if(intMemGrade==1){
				DbTaoResult addChampion = proc.getChamp(context, dataSet, request);
				
				if (addChampion != null && addChampion.isNext()) {
					addChampion.first();
					addChampion.next();
					champ_seq_no = (String) addChampion.getString("SEQ_NO");
					
					if(!GolfUtil.empty(champ_seq_no) && !champ_seq_no.equals("")){
						cancle_able_yn = "C";
						debug("서비스 해지 불가 사유 : 사은품 수령");
					}
				}
			}
			
			// 골프카드회원일 경우 - 탈퇴불가
			String strMemGr = "";
			DbTaoResult cardUser = proc.getCardMem(context, dataSet, request);
			if (cardUser != null && cardUser.isNext()) {
				cardUser.first();
				cardUser.next();
				strMemGr = (String) cardUser.getString("CDHD_SQ2_CTGO");
				
				System.out.print("strMemGr:"+strMemGr); 
				
				if("0005".equals(strMemGr) || "0006".equals(strMemGr))
				{
					cancle_able_yn = "V";
					debug("서비스 해지 불가 사유 : 골프카드 회원");
				}
				
			}
			
			// TM 회원 영화예매권 수령한 사람 - 탈퇴불가
			int tmMovieCnt = proc.getTmMovieCnt(context, dataSet, request);
			if(tmMovieCnt>0){
				cancle_able_yn = "T";
				debug("서비스 해지 불가 사유 : 이벤트 사은품 수령"); 
			}
						
			debug("GolfMemDelFormActn ::: intMemGrade : " + intMemGrade + " / cancle_able_yn : " + cancle_able_yn
					+ " / join_chnl(가입경로) : " + join_chnl);			
			
			// 05. Return 값 세팅			
			paramMap.put("join_chnl", join_chnl);	
			paramMap.put("CANCLE_ABLE_YN", cancle_able_yn);			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
