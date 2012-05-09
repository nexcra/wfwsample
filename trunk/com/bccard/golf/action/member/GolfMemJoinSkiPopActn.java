/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemJoinSkyPopActn
*   작성자    : (주)미디어포스 진현구
*   내용      : 회원 > 스키판매권 이벤트 작업
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfMemJoinSkiPopActn extends GolfActn{
	public static final String TITLE = "회원 > 스키판권 구매";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
        ResultException re;
		String subpage_key = "default";
		TaoConnection con = null;
		
		try {
			String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // 버튼
			String goPage = "/app/card/memberActn.do"; // 이동 액션
			int topPoint = 0;					// 포인트
			String golfPointComma = "";			// 컴마있는 포인트
	        int nMonth = 0;						// 현재 월
	        int nDay = 0; 						// 현재 일
	        String golfDate = "";				// 출력 일자
	        String flag = "NO";
			String memClass = "";
			String account = "";
			// 00.레이아웃 URL 저장
			String layout = super.getActionParam(context, "layout");
			request.setAttribute("layout", layout);

			String action_key = super.getActionKey(context);
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			String openerType 				= parser.getParameter("openerType", "").trim();
			String openerTypeRe 			= parser.getParameter("openerTypeRe", "").trim();
			//String money					= parser.getParameter("money", "1"); //1 :챔피온 2:블루 3:골드
			//String realPayAmt				= parser.getParameter("realPayAmt", "0"); 

			
			String code 					= parser.getParameter("code", "");
			String evnt_no 					= parser.getParameter("evnt_no", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");
			String cupn_clss 				= parser.getParameter("cupn_clss", "");
			//-- 2009.11.12 추가 
			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 			= parser.getParameter("pmgds_pym_yn", "N");		// Y:경품지급 N:경품미지급
			
			debug("openerType =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + openerType);
			debug("openerTypeRe =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + openerTypeRe);
			debug("code =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + code);
			debug("evnt_no =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + evnt_no);
			debug("cupn_ctnt =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_ctnt);
			debug("cupn_amt =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_amt);
			debug("cupn_type =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + cupn_type);
			debug("pmgds_pym_yn =`=`=`==`=`=`=`=`=`=`=`=`=`= : " + pmgds_pym_yn);

			if(openerType.equals("")){
				openerType = openerTypeRe;
			}
			paramMap.put("openerType", openerType);
			 
			// 01. 세션정보체크
			//debug("========= GolfMemJoinSkyPopActn =========> ");
			HttpSession session	= request.getSession(true);	
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request); 
			
			if( userEtt == null ){
				/*re = new ResultException();
				re.setTitleImage("error");
				re.setTitleText(TITLE);
				re.setKey("UHL004_Ind_Ret");
				re.addButton(goPage, addButton);
				throw re;*/
			} else {
				memClass = userEtt.getmemberClssCard();
				account = userEtt.getAccount();
				
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("account", account);

				con = context.getTaoConnection("dbtao", null);
				TaoResult result = con.execute("member.GolfMemSkiSaleAuthDaoProc", dataSet);

				String affinm="";
				if(result.isNext()) {
					result.next();

					if("00".equals(result.getString("RESULT"))) {
						affinm = result.getString("AFFI_FIRM_NM");
						debug("=============== " + affinm);
						if("SKI".equals(affinm)) {
							flag = "OK";
						}
					}
				}

				// 결제 jsp 에서 사용
				Random rand = new Random();
			    String st = String.valueOf( rand.nextInt(99999999) );
			    session.setAttribute("ParameterManipulationProtectKey",st);
				paramMap.put("ParameterManipulationProtectKey", st);
				paramMap.put("flag",		flag);

				debug("주민등록번호 : " + userEtt.getSocid()); 

				//GolfPointInfoResetJtProc resetProc = (GolfPointInfoResetJtProc)context.getProc("GolfPointInfoResetJtProc");
				//try
				//{
				//	if("1".equals(userEtt.getMemberClss())) {
				//		TopPointInfoEtt pointInfo = resetProc.getTopPointInfoEtt(context, request , userEtt.getSocid());
				//		topPoint = pointInfo.getTopPoint().getPoint();					
				//	}
				//}
				//catch(Throwable ignore) {}

				//topPoint = 50000;
				golfPointComma = GolfUtil.comma(topPoint+"");
		        GregorianCalendar today = new GregorianCalendar ( );
		        nMonth = today.get ( today.MONTH ) + 1;
		        nDay = today.get ( today.DAY_OF_MONTH ); 
				golfDate = nMonth+"월 "+nDay+"일";

				paramMap.put("actionKey", action_key);
				paramMap.put("golfPoint", topPoint+"");
				paramMap.put("golfPointComma", golfPointComma);
				paramMap.put("golfDate", golfDate);
				paramMap.put("userNM", userEtt.getName());	
				paramMap.put("code", code);
				paramMap.put("evnt_no", evnt_no);
				paramMap.put("cupn_ctnt", cupn_ctnt);
				paramMap.put("cupn_amt", cupn_amt); 
				paramMap.put("cupn_clss", cupn_clss);
				//-- 2009.11.12 추가 
				paramMap.put("cupn_type", cupn_type); 
				paramMap.put("pmgds_pym_yn", pmgds_pym_yn);
			}


			paramMap.put("account", account);

			//request.setAttribute("presentView", presentView);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		}finally{
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
