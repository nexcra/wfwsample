/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPracListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ����̺�������/��ũ�� ��������
*   �������  : Golf
*   �ۼ�����  : 2009-06-13
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

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.category.GolfCateSelInqDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfBenefitInqDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfPracPopListDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfCupnPrintSelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfPracListActn extends GolfActn{
	
	public static final String TITLE = "����̺�������/��ũ�� ��������";

	/***************************************************************************************
	* ���� �����ȭ��
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
		String permission = "";
		String cupn_prn_num = "0";
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		boolean flag = false;
		String rntB = "000";
		
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
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/drivrange");
			
			
			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			String sido	= parser.getParameter("s_sido", "");		// ����
			String gugun	= parser.getParameter("s_gugun", "");		// ��������
			String dong	= parser.getParameter("s_dong", "");		// ������
			//String exec_type_cd	= parser.getParameter("s_exec_type_cd", "");		// ����
			
			//debug("exec_type_cd ====> "+ exec_type_cd);
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			dataSet.setString("SIDO", sido);
			dataSet.setString("GUGUN", gugun);
			dataSet.setString("DONG", dong);
			//dataSet.setString("EXEC_TYPE_CD", exec_type_cd);
			
			dataSet.setString("USERID", userId);
			
			// 01.���ٱ��� üũ : 2009.11.05 �۹̼� �������� ����
			String permissionColum = "DRVR_APO_YN";
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
				cupn_prn_num = (String)benefit.getString("CUPN_PRN_NUM"); // �����μ�Ƚ��
			}
			debug(">>>>>>>>>>>>>>>>  cupn_prn_num : "+cupn_prn_num); 
			
			
			debug(">>>>>>>>>>>>>>>  permission : "+permission);
			if(isLogin.equals("1") && permission.equals("Y") ){
				flag = true;
			} else {
				rntB = "002";
			}
			
			/*   //2009.11.05 �۹̼� �������� ����
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
					cupn_prn_num = (String)benefitInq.getString("CUPN_PRN_NUM"); // �����μ�Ƚ��
					drvr_apo_yn = (String)benefitInq.getString("DRVR_APO_YN"); // ��Ÿ����̺����������ٿ���(Y:���ٰ��� N:���ٺҰ�)
					drgf_yr_able_num = (String)benefitInq.getString("DRGF_YR_ABLE_NUM"); // �帲�����Ⱑ��Ƚ��
					drgf_mo_able_num = (String)benefitInq.getString("DRGF_MO_ABLE_NUM"); // �帲����������Ƚ��
					eths_apo_yn = (String)benefitInq.getString("ETHS_APO_YN"); // �������ֺ��������ٿ���(Y:���ٰ��� N:���ٺҰ�)
				}
			}
			
			//if (exec_type_cd.equals("0001")){ //��Ÿ ����̺�������
				//if (isLogin.equals("1") && intMemGrade < 4) { // �췮ȸ�� �̻�����
				if (isLogin.equals("1") && drvr_apo_yn.equals("Y")) { // �췮ȸ�� �̻�����
					flag = true;
				} else {
					rntB = "002";
				}
			/*
			} else if (exec_type_cd.equals("0002")){ //��ũ�� ���� 
				if (isLogin.equals("1")) { // ��üȸ�� ����
					flag = true;
					
					if (!GolfUtil.isNull(cupn_prn_num)){ // ���� Ƚ�� ����
						// ���� Ƚ�� ���� ���ɿ���
						GolfCupnPrintSelDaoProc proc3 = (GolfCupnPrintSelDaoProc)context.getProc("GolfCupnPrintSelDaoProc");
						DbTaoResult cupnprintSel = (DbTaoResult) proc3.execute(context, dataSet);
						
						String cupn_year_cnt = "";
						if (cupnprintSel != null && cupnprintSel.isNext()) {
							cupnprintSel.first();
							cupnprintSel.next();
							if (cupnprintSel.getObject("RESULT").equals("00")) {
								cupn_year_cnt = (String)cupnprintSel.getString("CUPN_YEAR_CNT");
							}
						}
						
						if (Integer.parseInt(cupn_prn_num) < Integer.parseInt(cupn_year_cnt)){
							rntB = "001";
						}
					}
				}
			}
			*/
			paramMap.put("rntB", rntB);
			paramMap.put("cupn_prn_num", cupn_prn_num);
			paramMap.put("isLogin", isLogin);
			
			
			// �̿����� üũ
			//if (flag) {
				
				// 04.�������� ����Ʈ ��������
				GolfPracPopListDaoProc proc = (GolfPracPopListDaoProc)context.getProc("GolfPracPopListDaoProc");
				DbTaoResult pracListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				// ��ü 0��  [ 0/0 page] ���� ��������
				long totalRecord = 0L;
				long currPage = 0L;
				long totalPage = 0L;
				
				if (pracListResult != null && pracListResult.isNext()) {
					pracListResult.first();
					pracListResult.next();
					if (pracListResult.getObject("RESULT").equals("00")) {
						totalRecord = Long.parseLong((String)pracListResult.getString("TOTAL_CNT"));
						currPage = Long.parseLong((String)pracListResult.getString("CURR_PAGE"));
						totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
					}
				}
				
				
				paramMap.put("totalRecord", String.valueOf(totalRecord));
				paramMap.put("currPage", String.valueOf(currPage));
				paramMap.put("totalPage", String.valueOf(totalPage));
				paramMap.put("resultSize", String.valueOf(pracListResult.size()));
				
				request.setAttribute("pracListResult", pracListResult);
				request.setAttribute("record_size", String.valueOf(record_size));
				request.setAttribute("paramMap", paramMap);
			
			//} else {
			//	subpage_key = "limitReUrl";
			//}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
