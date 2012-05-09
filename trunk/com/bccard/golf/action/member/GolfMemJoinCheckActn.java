/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemJoinPopActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ���� �˾�
*   �������  : golf
*   �ۼ�����  : 2009-05-19 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemJoinNocardDaoProc;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemJoinCheckActn extends GolfActn{
	
	public static final String TITLE = "ȸ�� > ���� �̿���";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		String sece_yn = "";
		String cdhd_id = "";
		String isGolfMem = "N";
		int intMemGrade = 0;
		int smartGrd = 0;       // ��ȸ����	
		String memGrade = "";
		String jumin_no_golf = "";	// �ߺ����� ������ ���� �߰�	
		
		String isTmMem = "N";
		String jumin_no = "";
		String join_chnl = "";
		String reJoin = "N";
		
		String isSameJumin = "N";

		String memType = "";
		String memId = "";
		String memSocid = "";
		String userId = "";
		String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
    	String userIP = (String) request.getRemoteAddr();
    	String uurl = (String) request.getParameter("UURL");
    	String userAcount = "";
    	
    	String ctgo_seq = "";		// ȸ����޼� 0: ���� 1:����
    	String end_date = "";		// ����ȸ���Ⱓ ������
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		UcusrinfoEntity ucusrinfo = null;
		Connection con = null;

		UcusrinfoDaoProc proc_user = (UcusrinfoDaoProc) context.getProc("UcusrinfoDao");	

		try {
			HttpSession session = request.getSession();
			con = context.getDbConnection("default", null);	
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			//Object obj = session.getAttribute("SESSION_USER");

			userId = parser.getParameter("accountId", "");	// ȸ�����̵� simijoa81 : vriZE6WbWymLV6pPell2Zw==
			memSocid = parser.getParameter("sso_id", "");
			
			debug("GolfMemJoinCheckActn : userId = " + userId + " / memSocid : " + memSocid);
			
			
			if(!GolfUtil.empty(userId) && !"null".equals(userId)){
debug("STEP1");

		        paramMap.put("userId", userId);
				
		    	session.setAttribute("userID", userId);	
		    	session.setAttribute("userIP", userIP);	
		    	session.setAttribute("UURL", uurl);	
		    	session.setAttribute("SYSID", userId);
	
				ucusrinfo = proc_user.selectByAccount(con, userId);
				session.setAttribute("FRONT_ENTITY", ucusrinfo);
				session.setAttribute("SESSION_USER", ucusrinfo);
						
	
		        UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
				
		        if(usrEntity == null) {
		        	
		        	usrEntity = new UcusrinfoEntity(); 
		        	debug("## GolfCtrlServ | usrEntity null --> ���� �۾� \n");
		        }else{
		        	
		        	userAcount = usrEntity.getAccount();
		        	debug("## GolfCtrlServ | usrEntity not null  \n");
		        } 

		        CookieManager.addCookie(SECode.USER_ID, userId, sso_domain, response);
		        String sso_id = CookieManager.getCookieValue(SECode.USER_ID, request);
		        debug("sso_id : " + sso_id);
		        
				debug("## GolfMemBcJoinEndActn | userId ��ȣȭ�Ȱ� : " + userId + " / sso_domain : " + sso_domain + " / userAcount : " + userAcount);	

		        CookieManager.addCookie(SECode.USER_ID, userId, sso_domain, response);
		        sso_id = CookieManager.getCookieValue(SECode.USER_ID, request);

			}
				
debug("STEP2");
			// 01. ��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);			
			if(userEtt != null){
				memType = userEtt.getMemberClss();
				memId = userEtt.getAccount();
				//memSocid = userEtt.getSocid();			

				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("memSocid", memSocid);
	
				
				// �̹� ������ ȸ������ �˾ƺ��� 
				// ������ ȸ���̸� �ش� ������ ������ ���������� ������.
				// 04.���� ���̺�(Proc) ��ȸ
				GolfMemJoinNocardDaoProc proc = (GolfMemJoinNocardDaoProc)context.getProc("GolfMemJoinNocardDaoProc");
				DbTaoResult noCardResult = proc.execute(context, dataSet, request);	
	
				if (noCardResult != null && noCardResult.isNext()) {
					noCardResult.first();
					noCardResult.next();
					if(noCardResult.getString("RESULT").equals("00")){
						sece_yn = (String) noCardResult.getString("SECE_YN");
						cdhd_id = (String) noCardResult.getString("CDHD_ID");
						intMemGrade = (int) noCardResult.getInt("intMemGrade");
						memGrade = (String) noCardResult.getString("memGrade");
						jumin_no_golf = (String) noCardResult.getString("JUMIN_NO");
						reJoin = (String) noCardResult.getString("REJOIN");
						ctgo_seq = (String) noCardResult.getString("CTGO_SEQ");
						end_date = (String) noCardResult.getString("END_DATE");
						smartGrd =  (int) noCardResult.getInt("smartGrd");
						
						// ���̵�� ������ �ֹε�Ϲ�ȣ�� �ٸ���� ������ ���´�.
						if(!jumin_no_golf.equals(memSocid)){
							//isSameJumin = "Y";
						}
						
						// �̹� ������ ȸ���� ��� ������ �����ش�.
						if(!cdhd_id.equals("")){
							if(!sece_yn.equals("Y") || GolfUtil.empty(sece_yn)){
								isGolfMem = "Y";
								userEtt.setMemGrade(memGrade);
								userEtt.setIntMemGrade((int)intMemGrade);								
							}
						}
					}
				}
				
				// TM ȸ������ �˾ƺ���.
				DbTaoResult noCardTmResult = proc.tm_execute(context, dataSet, request);	
	
				if (noCardTmResult != null && noCardTmResult.isNext()) {
					noCardTmResult.first();
					noCardTmResult.next();
					if(noCardTmResult.getString("RESULT").equals("00")){
						jumin_no = (String) noCardTmResult.getString("JUMIN_NO");
						
						if(!GolfUtil.empty(jumin_no)){
							isTmMem = "Y";
						}
					}
				}
				
				//ž���� ȸ�� �߰�
				//ž����ī�� �������� üũ
				String topGolfCardYn = "N";
				GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
				try {					
					List topGolfCardList = mbr.getTopGolfCardInfoList();
					if( topGolfCardList!=null && topGolfCardList.size() > 0 )
					{
						for (int i = 0; i < topGolfCardList.size(); i++) 
						{
							
							topGolfCardYn = "Y";
							debug("## ž����ī�� ���� ȸ��");
						}
					}
					else
					{
						topGolfCardYn = "N";
						debug("## ž����ī�� �̼���");					
					}
				} catch(Throwable t) 
				{
					topGolfCardYn = "N";
					debug("## ž����ī�� üũ ����");	
				}
				
				
				//��ġī�� �������� üũ
				String richCardYn = "N";
				try {
					List richCardList = mbr.getRichCardInfoList();
					if( richCardList!=null && richCardList.size() > 0 )
					{
						for (int i = 0; i < richCardList.size(); i++) 
						{
							
							richCardYn = "Y";
							debug("## ��ġī�� ���� ȸ��");
						}
					}
					else
					{
						richCardYn = "N";
						debug("## ��ġī�� �̼���");					
					}
				} catch(Throwable t) 
				{
					richCardYn = "N";
					debug("## ��ġī�� üũ ����");	
				}
				
				
				
				
				// �ֹε�� ��ȣ ���, �̹̰����� ȸ���ƴϰ�, TM ȸ���� �ƴҰ�� white ȸ������ ó�� 
				if(!"Y".equals(isSameJumin) && !"Y".equals(isGolfMem) && !"Y".equals(isTmMem) && "N".equals(topGolfCardYn)  && "N".equals(richCardYn) ){
					
					dataSet.setString("payType", "");	
					dataSet.setString("moneyType", "4");	
					dataSet.setString("insType", "");	
					dataSet.setString("payWay", "");
					dataSet.setString("STTL_AMT", "");
					dataSet.setString("CODE_NO", "");
					dataSet.setString("JOIN_CHNL", "0001");
					
					GolfMemInsDaoProc insProc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
					int insResult = insProc.execute(context, dataSet, request);		
					if(insResult>0){
						memGrade = "white";
						intMemGrade = 4;
						userEtt.setMemGrade(memGrade);
						userEtt.setIntMemGrade((int)intMemGrade);
						userEtt.setIntMemberGrade((int)intMemGrade);
					}
				} 		
				
				debug("sece_yn : " + sece_yn + " / cdhd_id : " + cdhd_id + " / isGolfMem : " + isGolfMem + " / intMemGrade : " + intMemGrade 
						+ " / memGrade : " + memGrade + " / isTmMem : " + isTmMem + " / jumin_no : " + jumin_no + " / join_chnl : " + join_chnl
						+ " / jumin_no_golf : " + jumin_no_golf + " / isSameJumin : " + isSameJumin + " / memId : " + memId 
						+ " / memType : " + memType + " / memSocid : " + memSocid); 	
			
			}
			
			paramMap.put("memSocid", memSocid);
			paramMap.put("isGolfMem", isGolfMem);
			paramMap.put("isTmMem", isTmMem);
			paramMap.put("isSameJumin", isSameJumin);
			paramMap.put("reJoin", reJoin);
			paramMap.put("memType", memType);
			paramMap.put("ctgo_seq", ctgo_seq);
			paramMap.put("end_date", end_date);
			paramMap.put("smartGrd", smartGrd+"");		
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
