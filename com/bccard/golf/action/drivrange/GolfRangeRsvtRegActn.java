/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRangeRsvtRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : SKY72�帲���������� �����û ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.drivrange;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfRangeRsvtInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfRangeRsvtRegActn extends GolfActn{
	
	public static final String TITLE = "SKY72�帲���������� �����û ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		String drgf_apo_yn = "";
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		String email1 = ""; 
		String permission = "";
		String drgf_limt_yn = "";
		
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
				email1 	= (String)usrEntity.getEmail1(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);

			long rsvttime_sql_no = parser.getLongParameter("rsvt_time", 0L);  // ���డ��ƼŸ���Ϸù�ȣ
			String rsvt_clss = nowYear+"D"; // ���౸����
			//String gf_id = parser.getParameter("gf_id", "manse");	// ���̵�
			String hp_ddd_no = parser.getParameter("hp_ddd_no", "");	// �޴���ȭDDD��ȣ
			String hp_tel_hno = parser.getParameter("hp_tel_hno", "");	// �޴���ȭ����ȣ
			String hp_tel_sno = parser.getParameter("hp_tel_sno", "");	// �޴���ȭ�Ϸù�ȣ
			
			String rntB = parser.getParameter("rntB", "");	// ó������ : �⺻(001), ���̹��Ӵ�����(002), ��������(000)
			String drvr_amt = parser.getParameter("drvr_amt", ""); //���̹��Ӵ� �����ݾ�
			String s_year = parser.getParameter("s_year", "");
			String s_month = parser.getParameter("s_month", "");
			String s_day = parser.getParameter("s_day", "");
			String regDate 	= parser.getParameter("regDate", "");
			String sch_gr 	= parser.getParameter("SCH_GR_SEQ_NO","");
			String intBkGrade 	= parser.getParameter("intBkGrade","");
						
			debug("## GolfRangeRsvtRegActn ID : "+userId+" | ���� ��¥ : "+regDate);
			
			s_month = "00" + s_month;
			s_month = s_month.substring(s_month.length()-2);
		
			debug("## GolfRangeRsvtRegActn ��¥ : "+s_year + s_month + s_day );
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("RSVTTIME_SQL_NO", rsvttime_sql_no);
			dataSet.setString("RSVT_CLSS", rsvt_clss);
			dataSet.setString("USERID", userId);
			dataSet.setString("HP_DDD_NO", hp_ddd_no);
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			dataSet.setString("RNTB", rntB);
			dataSet.setString("DRVR_AMT", drvr_amt);
			dataSet.setString("BK_DATE", s_year + s_month + s_day);
			dataSet.setString("regDate", regDate);			
			dataSet.setString("SCH_GR_SEQ_NO", sch_gr);
			dataSet.setString("BKGRADE", intBkGrade);
			
			/*
			// ����ȸ������ ��������
			GolfBenefitInqDaoProc proc2 = (GolfBenefitInqDaoProc)context.getProc("GolfBenefitInqDaoProc");
			DbTaoResult benefitInq = (DbTaoResult) proc2.execute(context, dataSet);
			
			String drgf_limt_yn = "";
			String cupn_prn_num = "";
			String drvr_apo_yn = "";
			String drgf_yr_able_num = "";
			String drgf_mo_able_num = "";
			String eths_apo_yn = "";
			
			if (benefitInq != null && benefitInq.isNext()) {
				benefitInq.first();
				benefitInq.next();
				if (benefitInq.getObject("RESULT").equals("00")) {
					drgf_limt_yn = (String)benefitInq.getString("DRGF_LIMT_YN"); // �帲�������ѿ���(Y:���ٰ��� N:���ٺҰ�)
					drgf_apo_yn = (String)benefitInq.getString("DRGF_APO_YN");  //�帲���� ��������(Y:���ٰ��� N:���ٺҰ�)
					cupn_prn_num = (String)benefitInq.getString("CUPN_PRN_NUM"); // �����μ�Ƚ��
					drvr_apo_yn = (String)benefitInq.getString("DRVR_APO_YN"); // ��Ÿ����̺����������ٿ���(Y:���ٰ��� N:���ٺҰ�)
					drgf_yr_able_num = (String)benefitInq.getString("DRGF_YR_ABLE_NUM"); // �帲�����Ⱑ��Ƚ��
					drgf_mo_able_num = (String)benefitInq.getString("DRGF_MO_ABLE_NUM"); // �帲����������Ƚ��
					eths_apo_yn = (String)benefitInq.getString("ETHS_APO_YN"); // �������ֺ��������ٿ���(Y:���ٰ��� N:���ٺҰ�)
				}
			}
			
			*/
			// 01.���ٱ��� üũ
			String permissionColum = "DRGF_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
			}else{
				permission = "N";
			}
			// 02.�������ð������� 
			GolfBkBenefitTimesDaoProc proc_benefit = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult benefit = proc_benefit.getDrivingSkyBenefit(context, dataSet, request);
			
			if(benefit.isNext()){
				benefit.next();
				drgf_limt_yn = (String)benefit.getString("DRGF_LMT_YN"); // �帲�������ѿ���(Y:���ٰ��� N:���ٺҰ�)
				drgf_apo_yn = (String)benefit.getString("DRGF_APO_YN");  //�帲���� ��������(Y:���ٰ��� N:���ٺҰ�)
				
			}
			
//			if("Y".equals(permission)){
			// �̿����� üũ
			// if (isLogin.equals("1") && (intMemGrade < 3 || intCyberMoney > 0)) { // ���ȸ���̻� �� ���̹��Ӵ� 1�� �̻� ����
			//if (isLogin.equals("1") && permission.equals("Y") && (drgf_limt_yn.equals("Y") || intCyberMoney > 0)) { // ���ȸ���̻� �� ���̹��Ӵ� 1�� �̻� ����
				
				// 04.���� ���̺�(Proc) ��ȸ
				GolfRangeRsvtInsDaoProc proc = (GolfRangeRsvtInsDaoProc)context.getProc("GolfRangeRsvtInsDaoProc");
				
				// ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				int addResult = proc.execute(context, request, dataSet);
				
				debug("GolfRangeRsvtRegActn :: addResult : " + addResult + " / permission : " + permission + " / drgf_limt_yn : " + drgf_limt_yn 
						+ " / drgf_apo_yn : " + drgf_apo_yn + " / addResult : " + addResult);
				
				if (addResult == 0) {
					request.setAttribute("returnUrl", "golfRangeRsvtRegForm.do");
					request.setAttribute("resultMsg", "�̹� �����û �Ǿ��ֽ��ϴ�.");      	
		        } else if (addResult == 1) {
		        	request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "������ �Ϸ�Ǿ����ϴ�.");      	
		        } else if (addResult == 2) {
		        	request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "��û���ڷ� �̹� ������ �ϼ̽��ϴ�. 1�� 1ȸ ���� �Ͻ� �� �ֽ��ϴ�.");  
		        } else if (addResult == 3) {
		        	request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "������ �����Ǿ����ϴ�.");  
				} else {
					request.setAttribute("returnUrl", "golfRangeRsvtRegForm.do");
					request.setAttribute("resultMsg", "���� ����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        }
				
				// 05. Return �� ����			
				paramMap.put("addResult", String.valueOf(addResult));			
		        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
		        
//			} else {
//				subpage_key = "limitReUrl";
//			}
			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
