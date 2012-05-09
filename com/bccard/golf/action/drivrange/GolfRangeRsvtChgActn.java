/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRangeRsvtChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : SKY72�帲���������� ������� ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.drivrange;

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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmCyberBenefitDetailInqDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfRangeRsvtUpdDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfRangeRsvtChgActn extends GolfActn{
	
	public static final String TITLE = "SKY72�帲���������� ������� ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //���̹��Ӵ�
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);

			String rsvt_sql_no	= parser.getParameter("p_idx", "");// �����ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_sql_no);
			dataSet.setString("GF_ID", userId);
			
			//debug("GF_ID =====> "+ userId);
			
			/* 9�� �̺�Ʈ ������ ����
			// ���̹��Ӵ� ��å ��������
			GolfAdmCyberBenefitDetailInqDaoProc proc1 = (GolfAdmCyberBenefitDetailInqDaoProc)context.getProc("GolfAdmCyberBenefitDetailInqDaoProc");
			
			DbTaoResult detailInq1 = (DbTaoResult) proc1.execute(context, request, dataSet);
			
			String drvr_amt = "";
			if (detailInq1 != null && detailInq1.isNext()) {
				detailInq1.first();
				detailInq1.next();
				if (detailInq1.getObject("RESULT").equals("00")) {
					drvr_amt = (String)detailInq1.getString("DV_RG");
				}
			}
			 
			
			dataSet.setString("DRVR_AMT", drvr_amt);
			9�� �̺�Ʈ ������ ���� */
			
			// �̿����� üũ
			//if (isLogin.equals("1") && (intMemGrade < 3 || intCyberMoney > 0)) { // ���ȸ���̻� �� ���̹��Ӵ� 1�� �̻� 

			if (isLogin.equals("1")) { //9���̺�Ʈ ������ Ǯ�����
				
				// 04.���� ���̺�(Proc) ��ȸ
				GolfRangeRsvtUpdDaoProc proc = (GolfRangeRsvtUpdDaoProc)context.getProc("GolfRangeRsvtUpdDaoProc");
				
				//  ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				int editResult = proc.execute(context, request, dataSet);			
				
		        if (editResult == 1) {
					request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "������Ұ� �Ϸ� �Ǿ����ϴ�.");      	
		        } else {
					request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "������Ұ� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        }
				
				// 05. Return �� ����			
				paramMap.put("editResult", String.valueOf(editResult));			
		        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			
			} else {
				subpage_key = "limitReUrl";
			}
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
