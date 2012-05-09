/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.mania;

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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.mania.GolfAdmManiaUpdDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmManiaChgActn extends GolfActn{
	
	public static final String TITLE = "������ �����帮�������ν�û���� ����ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				admin_id	= (String)userEtt.getMemId();		
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			long seq_no	= parser.getLongParameter("p_idx", 0L);				// �Ϸù�ȣ
			if(seq_no == 0) seq_no	= parser.getLongParameter("cidx", 0L); 	// ��������/��� �����϶� ��ȣ��ó��
			String str_userid 	= parser.getParameter("cdhd_id","");
			String scoop_cp_cd 	= parser.getParameter("scoop_cp_cd", ""); 	// 0001:���������� 0002:��������
			String prize_yn		= parser.getParameter("prize_yn", ""); 		// ��������
			String start_area 	= parser.getParameter("start_area", "");	// ������
			String pu_date 		= parser.getParameter("pu_date", "");		// �Ⱦ���¥
			String start_hh 	= parser.getParameter("start_hh", "");		// �Ⱦ��ð� ��
			String start_mi 	= parser.getParameter("start_mi", "");		// �Ⱦ��ð� ��
			String tee_date 	= parser.getParameter("tee_date", "");		// Ƽ������¥
			String end_hh 		= parser.getParameter("end_hh", "");		// Ƽ�����ð� ��
			String end_mi 		= parser.getParameter("end_mi", "");		// Ƽ�����ð� ��
			String gf_nm 		= parser.getParameter("gf_nm", "");			// �������
			String car_type_code= parser.getParameter("ckd_code", "");		// ����
			String take_no 		= parser.getParameter("take_no", "");		// �����ο�
			String couns_yn 	= parser.getParameter("couns_yn", "");		// ��㿩��
			String sttl_amt		= parser.getParameter("sttl_amt", ""); 		// �����ݾ�
			
			String zp1			= parser.getParameter("zp1", ""); 			// �����ȣ
			String zp2			= parser.getParameter("zp2", ""); 			// �����ȣ
			String zipaddr		= parser.getParameter("zipaddr", ""); 		// �ּ�
			String detailaddr	= parser.getParameter("detailaddr", ""); 	// ���ּ�
			String subkey		= parser.getParameter("subkey", "");		
		
			pu_date = pu_date.length() == 10 ? DateUtil.format(pu_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			tee_date = tee_date.length() == 10 ? DateUtil.format(tee_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setLong("RECV_NO", seq_no);
			dataSet.setString("PRIZE_YN", prize_yn);
			
			dataSet.setString("STR_PLC", start_area);
			dataSet.setString("PIC_DATE", pu_date);
			dataSet.setString("START_HH", start_hh);
			dataSet.setString("START_MI", start_mi);
			dataSet.setString("TOFF_DATE", tee_date);
			dataSet.setString("END_HH", end_hh);
			dataSet.setString("END_MI", end_mi);
			dataSet.setString("GCC_NM", gf_nm);
			dataSet.setString("CKD_CODE", car_type_code);
			dataSet.setString("TK_PRS", take_no);
			dataSet.setString("CNSL_YN", couns_yn);
			dataSet.setString("STTL_AMT", sttl_amt);
			dataSet.setString("ZP1", zp1);
			dataSet.setString("ZP2", zp2);
			dataSet.setString("ADDR", zipaddr);
			dataSet.setString("ADDR2", detailaddr);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmManiaUpdDaoProc proc = (GolfAdmManiaUpdDaoProc)context.getProc("GolfAdmManiaUpdDaoProc");
			
			// ���������� ��û ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet);			
			
	        if (editResult == 1) {
				
	        	
	        	if (scoop_cp_cd.equals("0003")) {
	        		
	        		request.setAttribute("returnUrl", "admMagazineChgForm.do");
	        		request.setAttribute("resultMsg", "������������ ��û ���α׷� ������ ���������� ó�� �Ǿ����ϴ�."); 	

	        	}else{
	        		if (!GolfUtil.isNull(prize_yn)) { 	//���࿩�� (����/���) ������
	        			request.setAttribute("returnUrl", "admManiaList.do");
	        			
	        		}
	        		else{								//��㿩�� (�Ϸ�/���) ������
	        			request.setAttribute("returnUrl", "admManiaChgForm.do");
	        		}
	        		request.setAttribute("resultMsg", "���������� ��û ���α׷� ������ ���������� ó�� �Ǿ����ϴ�."); 
	        	}
	        
	        } else {
				request.setAttribute("returnUrl", "admManiaInq.do");
				request.setAttribute("resultMsg", "���� ��û ���α׷� ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����		
	        paramMap.put("p_idx", String.valueOf(seq_no));
	        paramMap.put("cdhd_id", str_userid);
	        paramMap.put("subkey", subkey);
			paramMap.put("scoop_cp_cd", scoop_cp_cd); 
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
