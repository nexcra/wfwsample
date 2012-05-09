/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLoungPaymentCancelActn.java
*   작성자    : 서비스개발팀 강선영
*   내용      : TM회원 취소(포인트취소 + 승인취소)
*   적용범위  : Golf
*   작성일자  : 2009-07-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.util.Map;
import java.net.InetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.initech.dbprotector.CipherClient;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;


import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.golf.jolt.JtProcess;

import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;


import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfLoungPaymentCancelActn extends GolfActn {
	
	public static final String TITLE = "TM 결제 취소"; 
	/***************************************************************************************
	* 비씨골프 TM 결제 취소 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		DbTaoResult taoResult = null;
		DbTaoResult retResult = null;
		String subpage_key = "default";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();	
		

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			retResult = new DbTaoResult(TITLE);
	
 
			String jumin_no			= parser.getParameter("JUMIN_NO", "");
			String account			= parser.getParameter("CDHD_ID", "");
			String join_chnl			= parser.getParameter("JOIN_CHNL", "");
			String work_date			= parser.getParameter("RECP_DATE", "");
			String sResultCode		= "";
			String ret_code   =  "" ;
			String ret_msg    =  "" ;		
			
			String st_year         	= parser.getParameter("ST_YEAR","");
			String st_month         = parser.getParameter("ST_MONTH","");
			String st_day			= parser.getParameter("ST_DAY","");
			String ed_year         	= parser.getParameter("ED_YEAR","");
			String ed_month         = parser.getParameter("ED_MONTH","");
			String ed_day			= parser.getParameter("ED_DAY","");
			String tag				= parser.getParameter("TAG","1");
			String st_gb			= parser.getParameter("ST_GB","1");
			
			String fromUrl			= parser.getParameter("fromUrl","");		// 호출 URL 
			debug("fromUrl : " + fromUrl);
			
			
			
			
			dataSet.setString("jumin_no", jumin_no); //
			dataSet.setString("account", account); //
			dataSet.setString("join_chnl", join_chnl);
			dataSet.setString("work_date", work_date);
			
			
			GolfLoungPaymentCancelProc proc = new GolfLoungPaymentCancelProc();

			/********************************************************************
				1. 포인트 취소 대상여부 조회 (TBLUGTMCSTMR 여부 확인) - 주민번호, TM처리일자
				2. 유료회원가입테이블 조회 (TBGGOLFCDHD 여부 확인) - 주민번호, 가입경로
				3. 유료회원 등급 확인 (TBGGOLFCDHDGRDMGMT 등급 확인) - 업그레이드 회원 여부 확인
				4. 결제 사용내역 조회 확인 -
			*********************************************************************/
			taoResult = (DbTaoResult)proc.execute(context, dataSet);	// 
			taoResult.next();
			String rRESULT = taoResult.getString("RESULT");

			
			
			//자료가 취소 대상자라면
			if ("00".equals(rRESULT)) {
				request.setAttribute("RESULT", rRESULT);

				String auth_clss	= taoResult.getString("AUTH_CLSS");
				String card_no	= taoResult.getString("CARD_NO");
				String vald_lim	= taoResult.getString("VALD_LIM");
				String auth_no	= taoResult.getString("AUTH_NO");
				String pay_amt = taoResult.getString("AUTH_AMT");
				String tm_buz = taoResult.getString("TM_BUZ");
				
				String hostAddress = InetAddress.getLocalHost().getHostAddress();
				
				//회원탈회 처리만 가능하게
				if ("2".equals(tag)) {
					sResultCode ="정상";
				//승인취소,포인트취소,회원탈회 처리 가능	
				} else {
				
					if ("3".equals(auth_clss))
					{
	
						/********************************************************************
							5. 포인트 취소 전문
						*********************************************************************/
	
						JoltInput entity = null;
	
						entity = new JoltInput();
	
						entity.setServiceName("BSXINPT");		
						
						entity.setString("fml_channel", "WEB"	 );
						entity.setString("fml_sec51" , hostAddress   );
						entity.setString("fml_sec50" , "bcadmin"                    );
						entity.setString("fml_trcode", "MJF6250I0100"               );
						entity.setString("fml_arg1"  , jumin_no					   );			
						entity.setString("fml_arg2"  , auth_no					   );
						entity.setString("fml_arg3"  , "42"			      		   );			
						
						debug("  entity= ["+entity+"]");
	
						JtTransactionProc pgProc = null;
	
						pgProc = (JtTransactionProc)context.getProc("MJF6250I0100");
	
						JoltOutput output = pgProc.execute(context,entity);
	
						debug(output.toString());
	
						ret_code   =  output.getString("fml_ret1");
						ret_msg    =  output.getString("fml_ret2");
	
						if (ret_code.equals("0000"))
						{
							sResultCode ="정상";
						}
	
					} else if (  "1".equals(auth_clss) ||  "2".equals(auth_clss) ) {
						/********************************************************************
							5. 승인 취소 전문
						*********************************************************************/
	 
	
						/********************************************
						* MJF6010R0100 - 01(fml_ret5):정상 
						* 승인 전문 :  for COMMIT  *
						*********************************************/
						GolfPayAuthEtt payEtt = new GolfPayAuthEtt();
	
						String ins_term ="00"; 
						String merno = "765943401"; //가맹점번호 (연회비)
						
						if (tm_buz.equals("03") || tm_buz.equals("13"))	{
							merno = "767445661"; //H&C네트워크 || 윌앤비전
							
						} else 	{
							merno = "767445687"; //트랜스코리아
						}
	
						if ( "1".equals(auth_clss) ) ins_term ="00";
						else if	( "2".equals(auth_clss) ) ins_term ="00"; //복합결제일때 취소시엔 00 을 넘겨야
	
						payEtt.setMerMgmtNo(merno); //가맹점번호
						payEtt.setCardNo(card_no); 
						payEtt.setValid(vald_lim.substring(2,6));			
						payEtt.setAmount(pay_amt);
						payEtt.setInsTerm(ins_term); //할부개월수
						payEtt.setRemoteAddr(hostAddress);
						payEtt.setUseNo(auth_no);
	
						GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
	
						boolean payCancelResult = false;
	
						payCancelResult = payProc.executePayAuthCancel(context, payEtt);
						debug("payCancelResult==========>"+payCancelResult);
	
						ret_code = payEtt.getResCode(); 
						ret_msg = payEtt.getResMsg();
						debug("ret_code==========>"+ret_code);
						debug("ret_msg==========>"+ret_msg);
	
						if (payCancelResult)
						{
							
							sResultCode ="정상";
							
						} else {
	
						}
	
					}
				}	
				debug("sResultCode==========>"+sResultCode+",sResultCode========="+sResultCode);
				if(sResultCode.equals("정상")) {
					
					/********************************************************************
						7. TM결제내역 update
					*********************************************************************/
					int updatecnt = proc.updateTM(context, jumin_no, work_date);
					/********************************************************************
						8. 유료회원 테이블 삭제
					*********************************************************************/
					int deletecnt = proc.deleteGOLFCDHD(context, jumin_no);

					/********************************************************************
						9. 유료회원 등급 테이블 삭제
					*********************************************************************/
					if (!"".equals(account))
					{
						int delete2cnt = proc.deleteGRD(context, account);
					}

					/********************************************************************
						10. 연회비 테이블은 취소내역을 update 함 TBGLUGANLFEECTNT
					*********************************************************************/
					int update2cnt = proc.updateFee(context, jumin_no,auth_no,card_no); 

					retResult.addString("RESULT", "00");


				}else{

					//ret_code ="99";
					//ret_msg ="오류입니다.";

					retResult.addString("RESULT", "01");
									
					if ( "3".equals(auth_clss))	{
						retResult.addString("RESULT_MSG", "포인트 취소시 오류 |fml_ret1|"+ret_code+"|fml_ret2|"+ret_msg);
					} else if (  "1".equals(auth_clss) ||  "2".equals(auth_clss) ) {
						retResult.addString("RESULT_MSG", "카드승인 취소시 오류 |fml_ret1|"+ret_code+"|fml_ret2|"+ret_msg);
					}
					
				}

			} else {
				//오류처리 - Proc에서 메세지 처리 했음
				
				retResult.addString("RESULT",rRESULT);
				String strMsg = taoResult.getString("RESULT_MSG");
				retResult.addString("RESULT_MSG", strMsg);
			}
  
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			paramMap.put("ST_GB",st_gb);
			paramMap.put("TAG",tag);
			
			

			request.setAttribute("fromUrl",fromUrl);
			request.setAttribute("paramMap",paramMap);
			request.setAttribute("resultList",retResult);
			
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		
		return getActionResponse(context, subpage_key);
		
	}
}
 