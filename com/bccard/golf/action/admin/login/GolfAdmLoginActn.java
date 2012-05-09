/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLoginActn
*   작성자    : (주)미디어포스
*   내용      : 포인트 관리자 로그인 프로세스
*   적용범위  : Golf
*   작성일자  : 2009-03-19
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*20110325   이경희 	 master권한에 leekh 추가 (차후에 하드코딩 제거하자)
***************************************************************************************************/
 
package com.bccard.golf.action.admin.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfElecAauthProcess;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.GolfAdmLoginlnqDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmLoginActn extends GolfActn {
	
	public static final String TITLE = "비씨골프  관리자 ID/PW 검증";

	/***************************************************************************************
	* 비씨탑포인트관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체.  
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	 * @throws BaseException 
	***************************************************************************************/
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws BaseException {		

		HttpSession session = null;  
		GolfAdminEtt userEtt = null;
		String subpage_key = "default";
		boolean isPsssOk =false;
		String rtnMsg = ""; 
		
		String mem_id = null;
		String user_nm = null;
		String user_dn = null;
		String user_nm_yn = null;
		String adm_clss = null;
		
		GolfElecAauthProcess elecAath = new GolfElecAauthProcess();//공인인증검증
		String validCert = "false";
		String clientAuth = "false";
		String[] semiCertVal = new String[2]; 		
		
		try {
			
			//1.세션정보체크
			session = request.getSession();
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				return getActionResponse(context, "admin");
			}			
			
			semiCertVal = elecAath.semiCert(request, response);
			
			validCert = semiCertVal[0];
			clientAuth = semiCertVal[1];
			
			if (validCert.equals("false")){
				rtnMsg = "올바른 인증서가 아닙니다.";
			}
			
			if (clientAuth.equals("false")){
				debug("인증이 필요없는 페이지");				
			}

			debug (" ### validCert : " + validCert +", clientAuth : " + clientAuth);
			
			if (validCert.equals("true") && clientAuth.equals("true")){
				
				RequestParser parser = context.getRequestParser("standard", request, response);
				String jumin_no = parser.getParameter("jumin_no1","") + parser.getParameter("jumin_no2","");
	
				GolfAdmLoginlnqDaoProc proc = (GolfAdmLoginlnqDaoProc)context.getProc("GolfAdmLoginlnqDaoProc");
				
//				if(!elecAath.isValidCert(request, response, jumin_no, "3")){ 
//					
//					elecAath.setValidCertMsg(session);					
//					String sessionMsg = (String)session.getAttribute("validCertMsg");
//					request.setAttribute("msg", sessionMsg);				
//					
//					return super.getActionResponse(context, "default");	
//					
//				}			
				
				String userDn = "";//elecAath.getUserDn();
	
				//3. 정보테이블에서 사용자 DN값으로 조회
				DbTaoDataSet dataset = new DbTaoDataSet(TITLE);
	            dataset.setString("userDn", userDn );
	            dataset.setString("jumin_no", jumin_no );
	           
				// 4. 등록한 DN이 다른경우 업데이트 해준다. 
	            int dBuserDnChg = proc.getDbUserDn(context, dataset);
				//debug("================>dBuserDnChg : " + dBuserDnChg);
	            
	            TaoResult tUser = proc.execute(context, dataset);
	            
				if(tUser.isFirst()) tUser.next();
			
				if ("01".equals(tUser.getString("RESULT"))){
					rtnMsg = "등록된 사용자가 아닙니다.";
				} else { 
									
					mem_id = tUser.getString("ACCOUNT");
					
					if(mem_id.equals("admin") ||  mem_id.equals("zeil")|| mem_id.equals("j_yoonho") || mem_id.equals("hoyeon0721")
							|| mem_id.equals("judy")|| mem_id.equals("eundeng2") || mem_id.equals("bccard")|| mem_id.equals("gungom")  
							|| mem_id.equals("steve2") || mem_id.equals("mongina")|| mem_id.equals("leekh")){					
						adm_clss = "master";
					}else{
						adm_clss = "default";
					}
					user_nm = tUser.getString("NAME");
					user_dn = tUser.getString("AZT_CFT_PROOF_VAL");
					user_nm_yn = tUser.getString("NM_YN");
								
					if(GolfUtil.empty(user_dn)){			
						// 값이 없으면 인서트 -> 성명 조회 -> 로그인
						if(!user_nm_yn.equals("0")){			
							isPsssOk = true;
						}else{	
							rtnMsg = "관리자 성명 인증 실패";
						}
					}else{			
						if(!user_nm_yn.equals("0")){			
							isPsssOk = true;
						}else{
							rtnMsg = "관리자 성명 인증 실패";
						}
					}
								
				}
				
				//4. 로그인 성공시에  세션만들기
				if (isPsssOk) {
					
					userEtt = new GolfAdminEtt();
					userEtt.setMemNo("1");
					userEtt.setMemId(mem_id);
					userEtt.setMemNm(user_nm);
					userEtt.setLogin(true); 
					userEtt.setAdm_clss(adm_clss);
					session.setAttribute("SESSION_ADMIN", userEtt);
					session.setMaxInactiveInterval(2400);
	
					subpage_key = "admin";
					
				} else {	
					MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,rtnMsg);
					throw new GolfException(msgEtt);
				}				
			}
			
		} catch(Exception t) {	
			t.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
		}		
		
		request.setAttribute("msg", rtnMsg);
		return getActionResponse(context, subpage_key);
		
    }
	
}