/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ���� > ���
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.sky.GolfBkSkyTimeRsInsDaoProc;
import com.bccard.golf.dbtao.proc.booking.sky.GolfBkSkyTimeRsViewDaoProc;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;

import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0  
******************************************************************************/
public class GolfMembershipActn extends GolfActn{
	
	public static final String TITLE = "���� > ���";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String rejoin_YN = "";
		String memGrade = "";
		int intMemGrade = 0;
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);		 	
				 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String rsvt_SQL_NO			= parser.getParameter("RSVT_SQL_NO", "");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_SQL_NO);
					
			String strMemChk = "";	// 1:����, 5:����
			String join_date = "";
			String upd_pay = "";
			String join_chnl = "";
			
			// 04.���� ���̺�(Proc)	
			if(usrEntity != null) {		
				GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
				DbTaoResult addResult = proc.reJoinExecute(context, dataSet, request);
				if (addResult != null && addResult.isNext()) {
					addResult.first();
					addResult.next();
					rejoin_YN = (String) addResult.getObject("RESULT");
					memGrade = (String) addResult.getObject("memGrade");
					intMemGrade = (int) addResult.getInt("intMemGrade");
					join_date = (String) addResult.getObject("join_date");
					upd_pay = (String) addResult.getObject("upd_pay");
					join_chnl = (String) addResult.getObject("join_chnl");
				}
				debug("rejoin_YN => " + rejoin_YN+"memGrade => " + memGrade+"intMemGrade => " + intMemGrade);
				
				strMemChk = usrEntity.getStrMemChkNum();
			}
			 
			debug("strMemChk:"+strMemChk);
			

			String strGolfCardYn = "N";
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			if (mbr != null) 
			{	
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				if( cardList.size() > 0 )
				{
					cardInfo = (CardInfoEtt)cardList.get(0);
					strGolfCardYn	= "Y";
				}

			}
			System.out.print("### strGolfCardYn:"+strGolfCardYn);
			
						
			request.setAttribute("strGolfCardYn", strGolfCardYn); 		//����ī������
			
			paramMap.put("REJOIN_YN", rejoin_YN);
			paramMap.put("memGrade", memGrade);
			paramMap.put("intMemGrade", intMemGrade+"");
			paramMap.put("join_date", join_date);
			paramMap.put("upd_pay", upd_pay);
			paramMap.put("join_chnl", join_chnl);
			
			// 05. Return �� ����
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
	        request.setAttribute("strMemChk", strMemChk); 
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
