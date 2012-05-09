/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntTmMovieCpnPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > TM 영화 예매권 이벤트 > 쿠폰번호 출력 팝업
*   적용범위  : Golf
*   작성일자  : 2010-03-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntMkMemberInsActn
*   작성자    : 이정규
*   내용      : 쿠폰등록
*   적용범위  : golf
*   작성일자  : 2010-09-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardComtInsDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntMkMemberProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfEvntMkMemberInsActn extends GolfActn{
	
	public static final String TITLE = "쿠폰 등록 처리";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		request.setAttribute("layout", layout);
		String user_id ="";

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				user_id		= (String)usrEntity.getAccount(); 
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String jumin_no = parser.getParameter("jumin_no", "");
			String hg_nm = parser.getParameter("hg_nm", "");
			String mer_no = parser.getParameter("mer_no", "");
			String cupn_no = "";
			debug("jumin_no = "+jumin_no);
			debug("hg_nm = "+hg_nm);
			debug("mer_no = "+mer_no); 
			int result_cnt = 0;
			String script = "";
			int addResult =0;
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("JUMIN_NO", jumin_no);
			dataSet.setString("HG_NM", hg_nm);
			dataSet.setString("MER_NO", mer_no);
			
			GolfEvntMkMemberProc proc = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
			debug("PROC~~~~");
			//03-01 출력 쿠폰 번호 가져와서
			DbTaoResult couponNum = (DbTaoResult) proc.getCouponNum(context, request, dataSet);	//쿠폰번호 가져오기
			debug("couponNum~~~~~");
			if (couponNum != null && couponNum.isNext()) {
				couponNum.next();
				if(couponNum.getString("RESULT").equals("00")){
					debug("CUPNNO = "+couponNum.getString("CUPN_NO"));
					cupn_no = couponNum.getString("CUPN_NO");
					dataSet.setString("CUPN_NO", cupn_no);
				}
			}
			
			if(!"".equals(cupn_no) && cupn_no != null)
			{
				//03-02 신청 갯수 알아보기
				result_cnt = proc.getCuponCnt(context, mer_no, jumin_no);
				
				debug("## result_cnt:"+result_cnt);
				if(result_cnt>=2){
					debug("이미 2회 출력되서 발급안됨");
					script = "alert('고객님은 2회 출력을 소진하셨습니다 ');";
				}else{
					debug("발급처리시작");
					
					// 04.이벤트 테이블 등록
					addResult = proc.getMkInsCupon(context, request, dataSet);
					
					request.setAttribute("returnUrl", reUrl);
					
			        if (addResult == 1) {
			        	//발급완료후 상태가 Y로 변경
			        	int updateMarking = proc.updateMarking(context, mer_no, cupn_no);
			        	
						request.setAttribute("resultMsg", "");      	
			        } else {
						request.setAttribute("resultMsg", "정상처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
			        }
				}
			
			}
			else
			{
				debug("오류 쿠폰번호 못가져옴");
				script = "alert('고객님은 2회 발급 받으셨습니다. \\n\\n감사합니다.');";
			}
			
			// 04. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
	        request.setAttribute("script", script);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}

