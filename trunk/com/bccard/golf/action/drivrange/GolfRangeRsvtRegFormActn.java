/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRangeRsvtRegFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      :  SKY72�帲���������� �����û
*   �������  : golf
*   �ۼ�����  : 2009-06-11
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmCyberBenefitDetailInqDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfRangeRsvtInqDaoProc;
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
public class GolfRangeRsvtRegFormActn extends GolfActn{
	
	public static final String TITLE = "SKY72�帲���������� �����û";

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
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		String email1 = ""; 
		String permission = "";
		String rntB = "000";

		String mobile = "";
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); //�̸�
				memClss		= (String)usrEntity.getMemberClss(); //��޹�ȣ
				userId		= (String)usrEntity.getAccount(); //���̵�
				juminno 	= (String)usrEntity.getSocid(); //�ֹι�ȣ
				memGrade 	= (String)usrEntity.getMemGrade(); //���
				intMemGrade	= (int)usrEntity.getIntMemGrade(); //��޹�ȣ
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //���̹��Ӵ�
				email1 	= (String)usrEntity.getEmail1(); 
				
				mobile 	= (String)usrEntity.getMobile(); 
				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
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
			String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
			String nowDay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			
			// Request �� ����
			String search_yn	= parser.getParameter("search_yn", "N");
			String year			= parser.getParameter("s_year", nowYear);
			String month		= parser.getParameter("s_month", nowMonth);
			String day			= parser.getParameter("s_day", nowDay);
			String sch_gr 		= parser.getParameter("SCH_GR_SEQ_NO","");
			
			String date = year +"�� "+ month +"�� "+ day +"��";
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("USERID", userId);
			
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
			paramMap.put("intMemGrade", String.valueOf(intMemGrade));
			paramMap.put("drvr_amt", drvr_amt);
			//paramMap.put("intCyberMoney", String.valueOf(intCyberMoney));
			
			
			//--------------- ���⼭���� 2009.11.05 ���� ���� Start -----------------------//
			int drgf_yr_done = 0;
			int drgf_mo_done = 0;
			int intBkGrade = 0;
			
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
			
			//���ٱ����� ������ �ش� ��� ������ GET 
			 if("Y".equals(permission)){
			 
				// 02.�������ð�������
				GolfBkBenefitTimesDaoProc proc_benefit = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
				DbTaoResult benefit = proc_benefit.getDrivingSkyBenefit(context, dataSet, request);
				
				if(benefit.isNext()){
					benefit.next();
					drgf_yr_done = benefit.getInt("DRGF_BOKG_YR"); // �帲���� ���� ��Ƚ�� 
					drgf_mo_done = benefit.getInt("DRGF_BOKG_MO"); // �帲���� ���� ��Ƚ��
					intBkGrade = benefit.getInt("intBkGrade");  //����� ���
					intCyberMoney = benefit.getInt("CY_MONEY");  //���̹��Ӵ�					
				}
				debug("�帲���������� :: drgf_yr_done : " + drgf_yr_done + " / drgf_mo_done : " + drgf_mo_done + " / intCyberMoney : " + intCyberMoney 
						 + " / drvr_amt : " + drvr_amt + " / permission : " + permission + " / intBkGrade : " + intBkGrade);
				
				//- �帲���������� :: drgf_yr_done : 0 / drgf_mo_done : 0 / intCyberMoney : 0 / drvr_amt : 5000 / permission : Y / intBkGrade : 0
			
				//���Ƚ���� ����������
				if(drgf_yr_done > 0 && drgf_mo_done > 0 ){ debug ("##-------------1");
					rntB = "001";
				}else{
					//���Ƚ���� ����, ���̹� �Ӵϰ�������
					if (intCyberMoney < Integer.parseInt(drvr_amt)){debug ("##-------------2");
						rntB = "000";	
					}else{debug ("##-------------3");
						rntB = "002";
					}
				}
				
				paramMap.put("rntB", rntB);
				
				
				dataSet.setString("SORT", AppConfig.getDataCodeProp("DrivingRange"));
				dataSet.setString("DrivR", AppConfig.getDataCodeProp("DrivingRangeClss"));
				
				// 04.���� ���̺�(Proc) ��ȸ - ������
				GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
				DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
				request.setAttribute("TitimeGreenList", titimeGreenList);	
				
				if(sch_gr.equals("")){
					if (titimeGreenList != null && titimeGreenList.isNext()) {
						titimeGreenList.first();
						titimeGreenList.next();
						sch_gr =  Integer.toString(titimeGreenList.getInt("SEQ_NO"));
					}
				}			
			
				paramMap.put("SCH_GR_SEQ_NO", sch_gr);
				
				dataSet.setString("SCH_GR_SEQ_NO", sch_gr);
					
				
				//���̺���ȸ : ��ī��27 �帲���� ������ ���డ�� ��� 
				GolfRangeRsvtInqDaoProc proc = (GolfRangeRsvtInqDaoProc)context.getProc("GolfRangeRsvtInqDaoProc");
				DbTaoResult rangersvtInq = proc.execute(context, dataSet);
				
				// 05. Return �� ����			

				paramMap.put("mobile1", mobile1);
				paramMap.put("mobile2", mobile2);
				paramMap.put("mobile3", mobile3);
				paramMap.put("search_yn", search_yn);
				paramMap.put("s_year", year);
				paramMap.put("s_month", month);
				paramMap.put("s_day", day);
				paramMap.put("s_date", date);				
				paramMap.put("permission", permission);
				paramMap.put("intCyberMoney",Integer.toString(intCyberMoney));
				paramMap.put("drgf_yr_done", Integer.toString(drgf_yr_done));
				paramMap.put("drgf_mo_done", Integer.toString(drgf_mo_done));
				paramMap.put("intBkGrade", Integer.toString(intBkGrade));
				paramMap.put("regDate", year+month+day);
				
				request.setAttribute("rangersvtInqResult",rangersvtInq);	
			
				
			//���ٱ����� ������ ���� ������ �̵�
			}else{
				subpage_key = "limitReUrl";
			}
			
			 request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			 
			 
			 
			//--------------- ���⼭���� 2009.11.05 ���� ����  End -----------------------//
			
			/*   //2009.11.05 ���հ��������� ����
			// ����ȸ������ ��������
			GolfBenefitInqDaoProc proc2 = (GolfBenefitInqDaoProc)context.getProc("GolfBenefitInqDaoProc");
			
			DbTaoResult benefitInq = (DbTaoResult) proc2.execute(context, dataSet);
			
			
			
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

//			if (intMemGrade == 1) { // Champion ��� ������ ���ٰ���
//				rntB = "001";
				
//			} else {	
				if (intCyberMoney < Integer.parseInt(drvr_amt)){ //���̹��ӴϷ� �Ұ��ɽ�
					if (drgf_limt_yn.equals("Y") && GolfUtil.isNull(drgf_yr_able_num) && GolfUtil.isNull(drgf_mo_able_num)){ //������ ���ٰ���
						rntB = "001";
						
					} else if (drgf_limt_yn.equals("Y") && (!GolfUtil.isNull(drgf_yr_able_num) || !GolfUtil.isNull(drgf_mo_able_num))){ //���ٰ���(��������)
						// ���� ���� ���ɿ���
						GolfRangeRsvtSelDaoProc proc3 = (GolfRangeRsvtSelDaoProc)context.getProc("GolfRangeRsvtSelDaoProc");
						DbTaoResult rangersvtSel = (DbTaoResult) proc3.execute(context, dataSet);
						
						String rsvt_year_cnt = "";
						String rsvt_month_cnt = "";
						if (rangersvtSel != null && rangersvtSel.isNext()) {
							rangersvtSel.first();
							rangersvtSel.next();
							if (rangersvtSel.getObject("RESULT").equals("00")) {
								rsvt_year_cnt = (String)rangersvtSel.getString("RSVT_YEAR_CNT");
								rsvt_month_cnt = (String)rangersvtSel.getString("RSVT_MONTH_CNT");
							}
						}
						debug("---------------  rsvt_year_cnt : "+rsvt_year_cnt +" / rsvt_month_cnt : "+rsvt_month_cnt);
						
						if (Integer.parseInt(rsvt_year_cnt) < Integer.parseInt(drgf_yr_able_num)){
							if (Integer.parseInt(rsvt_month_cnt) < Integer.parseInt(drgf_mo_able_num)){
								rntB = "001";
							}
						}
					} else {
						rntB = "000";
					}
				} else { //���̹��ӴϷ� ���ɽ�
					rntB = "002";
				}
//			}
			*/
			
			
			
			//paramMap.put("userId", userId);
			//paramMap.put("intCyberMoney", String.valueOf(intCyberMoney));
			//paramMap.put("email1", email1);
			
			
			
			// �̿����� üũ
			//if (isLogin.equals("1") && (intMemGrade < 3 || intCyberMoney > 0)) { // ���ȸ���̻� �� ���̹��Ӵ� 1�� �̻� ����
			//if (isLogin.equals("1") && permission.equals("Y") &&  (drgf_limt_yn.equals("Y") || intCyberMoney > 0)) { // ���ȸ���̻� �� ���̹��Ӵ� 1�� �̻� ����
				//�α���üũ // �帲���� �������� //�帲�������ѿ���//���̹��Ӵϱݾ�
				
			   
			
			//} else {
				//subpage_key = "limitReUrl";
			//}
			    
			   
			    
			    
			    
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
