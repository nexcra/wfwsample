/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntPrimeInsActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 해외프라임 > 신청 
*   적용범위  : Golf
*   작성일자  : 2010-08-16
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
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.prime.GolfEvntPrimeInsDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntPrimeInsActn extends GolfActn{
	
	public static final String TITLE = "이벤트 > 해외프라임 > 신청";

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

		
		try { 
			HttpSession session = request.getSession(true);

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 후처리
			String script = "";
			int addResult = 0;
				
			// 신청정보
			String cdhd_id		= parser.getParameter("cdhd_id","");		// 회원아이디
			String bkg_pe_num	= parser.getParameter("bkg_pe_num", "");	// 성명
			String jumin_no1	= parser.getParameter("jumin_no1", "");		// 주민등록번호1
			String jumin_no2	= parser.getParameter("jumin_no2", "");		// 주민등록번호2
			String jumin_no		= jumin_no1+""+jumin_no2;
			String hp_ddd_no	= parser.getParameter("hp_ddd_no","");		// 연락처1
			String hp_tel_hno	= parser.getParameter("hp_tel_hno","");		// 연락처2
			String hp_tel_sno	= parser.getParameter("hp_tel_sno", "");	// 연락처3
			String ddd_no		= parser.getParameter("ddd_no","");			// 집전화1
			String tel_hno		= parser.getParameter("tel_hno","");		// 집전화2
			String tel_sno		= parser.getParameter("tel_sno", "");		// 집전화3
			String dtl_addr		= parser.getParameter("dtl_addr","");		// 주소
			String lesn_seq_no	= parser.getParameter("lesn_seq_no","");	// 가입멤버십
			String pu_date		= parser.getParameter("pu_date","");		// 회원시작일
			pu_date = GolfUtil.replace(pu_date, ".", "");
			String memo_expl	= parser.getParameter("memo_expl","");		// 기타 요청 사항
			
			// 결제정보
			String order_no		= parser.getParameter("order_no", "");			// 주문코드
			String realPayAmt	= parser.getParameter("realPayAmt", "0");		// 결제 금액
			

			request.getSession().removeAttribute("primeName");
			request.getSession().removeAttribute("primeJumin1");
			request.getSession().removeAttribute("primeJumin2");
			session.setAttribute("primeName",bkg_pe_num);
			session.setAttribute("primeJumin1",jumin_no1);
			session.setAttribute("primeJumin2",jumin_no2);
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("bkg_pe_num", bkg_pe_num);
			dataSet.setString("jumin_no1", jumin_no1);
			dataSet.setString("jumin_no2", jumin_no2);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);			
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);			
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("dtl_addr", dtl_addr);
			dataSet.setString("lesn_seq_no", lesn_seq_no);
			dataSet.setString("pu_date", pu_date);
			dataSet.setString("memo_expl", memo_expl);
			
			dataSet.setString("order_no", order_no);
			dataSet.setString("realPayAmt", realPayAmt);		

			// 04.실제 테이블(Proc) 조회
			GolfEvntPrimeInsDaoProc proc = (GolfEvntPrimeInsDaoProc)context.getProc("GolfEvntPrimeInsDaoProc");
			
			// 신청내역 검사
			int cntEvnt = proc.execute_insYn(context, request, dataSet);
			if(cntEvnt>0){
				script = "alert('이미 신청하셨습니다.');";
			}else{

				// 주문정보 저장
				addResult = proc.execute(context, request, dataSet);	
				debug("GolfEvntPrimeInsDaoProc = addResult : " + addResult);	
				
				if(addResult>0){
					script = "parent.parent.location.href='/app/golfloung/html/event/prime/prime05.jsp';";
				}else{
					script = "alert('신청과정에 오류가 있었습니다. 다시 시도해 주십시오.'); parent.location.href='GolfEvntPrimeInsForm.do';";
				}
			}
				

			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
