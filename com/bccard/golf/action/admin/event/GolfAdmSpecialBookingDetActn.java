/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmSpecialBookingDetActn
*   �ۼ���    : ������ ���弱
*   ����      : ������ > �̺�Ʈ->VIP��ŷ�̺�Ʈ->���������ŷ 
*   �������  : Golf
*   �ۼ�����  : 2009-09-17
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSpecialBookingDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;

/******************************************************************************
* Golf
* @author	������ ���弱
* @version	1.0
******************************************************************************/
public class GolfAdmSpecialBookingDetActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾� ��ŷ �̺�Ʈ ��÷�� ����Ʈ";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request �� ����
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size		= parser.getLongParameter("record_size", 10);		// ����������¼�	
			String green_nm				= parser.getParameter("green_nm","");           //��û�������
			String golf_cmmn_code		= parser.getParameter("golf_cmmn_code","");     //������� �ڵ�
			String grade				= parser.getParameter("grade","");              //ȸ�����
			String sch_reg_aton_st		= parser.getParameter("sch_reg_aton_st","");    //��ȸ ��û���� ������
			String sch_reg_aton_ed		= parser.getParameter("sch_reg_aton_ed","");    //��ȸ ��û���� ������
			String sch_pu_date_st		= parser.getParameter("sch_pu_date_st","");     //��ȸ ��ŷ���� ������
			String sch_pu_date_ed		= parser.getParameter("sch_pu_date_ed","");     //��ȸ ��ŷ���� ������
			String sch_type				= parser.getParameter("sch_type","");           //�˻�Ÿ��
			String search_word			= parser.getParameter("search_word","");        //�˻���
			String aplc_seq_no          = parser.getParameter("aplc_seq_no","");        //�����ȣ


debug("green_nm>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + green_nm);
debug("golf_cmmn_code>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + golf_cmmn_code);
debug("grade>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + grade);
debug("sch_reg_aton_st>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_reg_aton_st);
debug("sch_reg_aton_ed>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_reg_aton_ed);
debug("sch_pu_date_st>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_pu_date_st);
debug("sch_pu_date_ed>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_pu_date_ed);
debug("sch_type>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_type);
debug("search_word>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + search_word);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("golf_cmmn_code",golf_cmmn_code);
			dataSet.setString("grade",grade);
			dataSet.setString("sch_reg_aton_st",sch_reg_aton_st);
			dataSet.setString("sch_reg_aton_ed",sch_reg_aton_ed);
			dataSet.setString("sch_pu_date_st",sch_pu_date_st);
			dataSet.setString("sch_pu_date_ed",sch_pu_date_ed);
			dataSet.setString("sch_type",sch_type);
			dataSet.setString("search_word",search_word);
			dataSet.setString("aplc_seq_no",aplc_seq_no);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmSpecialBookingDaoProc proc = (GolfAdmSpecialBookingDaoProc)context.getProc("GolfAdmSpecialBookingDaoProc");
			GolfEvntBkMMDaoProc proc2 = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");

			DbTaoResult evntMMdetResult = (DbTaoResult) proc.getDetail(context, request, dataSet);

			if(evntMMdetResult.isNext()){
				evntMMdetResult.next();
				String userId = evntMMdetResult.getString("CDHD_ID");            //���̵�
				String intMemGradeNM = evntMMdetResult.getString("GRADE");       //���
				debug("============ GRADE : " + intMemGradeNM);
				
				String intMemGrade = "3";
				if("Champion".equals(intMemGradeNM)){
					intMemGrade = "1";
				}else if("Blue".equals(intMemGradeNM)||"Black".equals(intMemGradeNM)){
					intMemGrade = "2";
				}else if("White".equals(intMemGradeNM)){
					intMemGrade = "0";
				}else { //Gold,NHƼŸ��,NH�÷�Ƽ��,������������
					intMemGrade = "3"; 
				}
				dataSet.setString("intMemGrade",intMemGrade);
				dataSet.setString("userId",userId);				 
				dataSet.setString("cdhd_id",userId);	
			}

			//DbTaoResult evntBkMMInq = proc2.getReserveList(context, dataSet);
			
			DbTaoResult evntRevList = proc2.execute(context, dataSet);
			

			String cnt = "";
			String tot_cnt = "";
			
			/*if(evntBkMMInq.isNext()){
				evntBkMMInq.next();
				cnt = evntBkMMInq.getString("CNT");
				tot_cnt = evntBkMMInq.getString("TOT");
				debug("cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
				debug("tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				paramMap.put("cnt",cnt);
				paramMap.put("tot_cnt",tot_cnt);
			}
			String can_cnt      = String.valueOf(Integer.parseInt(tot_cnt) - Integer.parseInt(cnt));
		*/

			String can_cnt = "0";
			String blockDate = "";
			
			GolfBkBenefitTimesDaoProc proc_count = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult evntMMInq = proc_count.getAdmPreBkEvntBenefit(context, dataSet, request);
			if(evntMMInq.isNext()){
				evntMMInq.next();
				
				tot_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_PMI_NUM"));
				cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_DONE"));
				can_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_MO"));
				blockDate = evntMMInq.getString("blockDate");
				
				debug("Actn : ���Ǽ�: cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
				debug("Actn : �����Ǽ� : can_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + can_cnt);
				debug("Actn : �ѻ���� ���ִ°Ǽ� : tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				debug("Actn : ������ : blockDate>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + blockDate);
			}
			
			paramMap.put("tot_cnt",tot_cnt);
			
			paramMap.put("page_no",String.valueOf(page_no));
			paramMap.put("green_nm",green_nm);			
			paramMap.put("golf_cmmn_code",golf_cmmn_code);
			paramMap.put("grade",grade	);		
			paramMap.put("sch_reg_aton_st",sch_reg_aton_st);	
			paramMap.put("sch_reg_aton_ed",	sch_reg_aton_ed);	
			paramMap.put("sch_pu_date_st",	sch_pu_date_st);	
			paramMap.put("sch_pu_date_ed",sch_pu_date_ed	);	
			paramMap.put("sch_type"	,	sch_type	);	
			paramMap.put("search_word",		search_word);	
			paramMap.put("can_cnt",can_cnt);
			paramMap.put("cnt",cnt);
			paramMap.put("aplc_seq_no",aplc_seq_no);
			
			request.setAttribute("evntMMdetResult", evntMMdetResult);	
			request.setAttribute("evntRevList" , evntRevList);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
