/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmSpecialBookingUpdActn
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
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;

/******************************************************************************
* Golf
* @author	������ ���弱
* @version	1.0
******************************************************************************/
public class GolfAdmSpecialBookingUpdActn extends GolfActn{
	
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
			//���⼭ ���� ���������� �Ķ����
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size		= parser.getLongParameter("record_size", 10);		// ����������¼�	
			String green_nm				= parser.getParameter("green_nm","");           //��û�������            
			String golf_cmmn_code		= parser.getParameter("golf_cmmn_code","");		//������� �ڵ�           
			String grade				= parser.getParameter("grade","");				//ȸ�����                
			String sch_reg_aton_st		= parser.getParameter("sch_reg_aton_st","");	//��ȸ ��û���� ������    
			String sch_reg_aton_ed		= parser.getParameter("sch_reg_aton_ed","");	//��ȸ ��û���� ������    
			String sch_pu_date_st		= parser.getParameter("sch_pu_date_st","");		//��ȸ ��ŷ���� ������    
			String sch_pu_date_ed		= parser.getParameter("sch_pu_date_ed","");		//��ȸ ��ŷ���� ������    
			String sch_type				= parser.getParameter("sch_type","");			//�˻�Ÿ��                
			String search_word			= parser.getParameter("search_word","");		//�˻���                  
			String aplc_seq_no          = parser.getParameter("aplc_seq_no","");        //�����ȣ
			String cncl_param           = parser.getParameter("cncl_param","");         //���� �����ڵ� 
			String userId               = parser.getParameter("cdhd_id","");            //���̵�
			//�������

			String golf_cmmn_codes		= parser.getParameter("golf_cmmn_codes","");    //�����ڵ�
			String green_nms			= parser.getParameter("green_nms","");          //���� ��û �������
			String dprt_pl_info			= parser.getParameter("dprt_pl_info","");       //����������
			String pu_date              = parser.getParameter("pu_date","");            //��ŷ����
			String pu_time              = parser.getParameter("pu_time","");            //��ŷ�ð�
			String estm_itm_clss        = parser.getParameter("estm_itm_clss","");      //ȸ���� �ڵ�
			String appr_opion           = parser.getParameter("appr_opion","");         //ȸ���� ��
			
			String co_nm           		= parser.getParameter("co_nm","");
			String hp_ddd_no           	= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno           = parser.getParameter("hp_tel_hno","");
			String hp_tel_sno           = parser.getParameter("hp_tel_sno","");
			String teof_date       	    = parser.getParameter("teof_date","");
			String teof_time       	    = parser.getParameter("teof_time","");
			
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
			dataSet.setString("cncl_param",cncl_param);
			dataSet.setString("userId",userId);				
			dataSet.setString("cdhd_id",userId);

			dataSet.setString("golf_cmmn_codes",golf_cmmn_codes);
			dataSet.setString("green_nms",green_nms);
			dataSet.setString("dprt_pl_info",dprt_pl_info);
			dataSet.setString("pu_date",pu_date);
			dataSet.setString("pu_time",pu_time);
			dataSet.setString("estm_itm_clss",estm_itm_clss);				
			dataSet.setString("appr_opion",appr_opion);
			
			dataSet.setString("co_nm",co_nm);
			dataSet.setString("hp_ddd_no",hp_ddd_no);
			dataSet.setString("hp_tel_hno",hp_tel_hno);
			dataSet.setString("hp_tel_sno",hp_tel_sno);
			dataSet.setString("teof_date",teof_date);
			dataSet.setString("teof_time",teof_time);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmSpecialBookingDaoProc proc = (GolfAdmSpecialBookingDaoProc)context.getProc("GolfAdmSpecialBookingDaoProc");
			GolfEvntBkMMDaoProc proc2 = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");

			DbTaoResult evntUpd1    = proc.setUpdateUsr(context, request, dataSet);
			DbTaoResult evntUpd2    = proc.setUpdateEvnt(context, request, dataSet);

			DbTaoResult evntMMdetResult = (DbTaoResult) proc.getDetail(context, request, dataSet);

			if(evntMMdetResult.isNext()){
				evntMMdetResult.next();				
				String intMemGradeNM = evntMMdetResult.getString("GRADE");

				
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
				
			}			

			DbTaoResult evntBkMMInq = proc2.getReserveList(context, dataSet);
			
			DbTaoResult evntRevList = proc2.execute(context, dataSet);
			
 
			String cnt = "";
			String tot_cnt = "";
			
			if(evntBkMMInq != null) {
			
				if(evntBkMMInq.isNext()){
					evntBkMMInq.next();		
					
					try
					{
						cnt = evntBkMMInq.getString("CNT");
						tot_cnt = evntBkMMInq.getString("TOT");
					} catch(Throwable t) {}
					if("".equals(cnt)) cnt = "0";
					if("".equals(tot_cnt)) tot_cnt = "0";
					
					debug("cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
					debug("tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
					paramMap.put("cnt",cnt);
					paramMap.put("tot_cnt",tot_cnt);
				}
			}

			String can_cnt      = String.valueOf(Integer.parseInt(tot_cnt) - Integer.parseInt(cnt));
		
			
			
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
			paramMap.put("update_flag","update");
			
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
