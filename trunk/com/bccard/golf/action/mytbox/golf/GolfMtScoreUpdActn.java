/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admGrUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����Ƽ�ڽ� > ���ھ� > ����
*   �������  : golf
*   �ۼ�����  : 2009-05-20 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.golf;

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
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.mytbox.golf.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMtScoreUpdActn extends GolfActn{
	
	public static final String TITLE = "���ھ� > ����";

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
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			int seq_NO	 					= parser.getIntParameter("SEQ_NO", 0);
			String green_NM 				= parser.getParameter("GREEN_NM", "").trim();
			String round_YEAR 				= parser.getParameter("ROUND_YEAR", "").trim();
			String round_MONTH 				= parser.getParameter("ROUND_MONTH", "").trim();
			String round_DAY 				= parser.getParameter("ROUND_DAY", "").trim();
			String round_DATE 				= round_YEAR+round_MONTH+round_DAY;
			String curs_NM 				= parser.getParameter("CURS_NM", "").trim();
			String hadc_NUM 				= parser.getParameter("HADC_NUM", "").trim();
			int hole01_SCOR 				= parser.getIntParameter("HOLE01_SCOR", 0);
			int hole02_SCOR 				= parser.getIntParameter("HOLE02_SCOR", 0);
			int hole03_SCOR 				= parser.getIntParameter("HOLE03_SCOR", 0);
			int hole04_SCOR 				= parser.getIntParameter("HOLE04_SCOR", 0);
			int hole05_SCOR 				= parser.getIntParameter("HOLE05_SCOR", 0);
			int hole06_SCOR 				= parser.getIntParameter("HOLE06_SCOR", 0);
			int hole07_SCOR 				= parser.getIntParameter("HOLE07_SCOR", 0);
			int hole08_SCOR 				= parser.getIntParameter("HOLE08_SCOR", 0);
			int hole09_SCOR 				= parser.getIntParameter("HOLE09_SCOR", 0);
			int hole10_SCOR 				= parser.getIntParameter("HOLE10_SCOR", 0);
			int hole11_SCOR 				= parser.getIntParameter("HOLE11_SCOR", 0);
			int hole12_SCOR 				= parser.getIntParameter("HOLE12_SCOR", 0);
			int hole13_SCOR 				= parser.getIntParameter("HOLE13_SCOR", 0);
			int hole14_SCOR 				= parser.getIntParameter("HOLE14_SCOR", 0);
			int hole15_SCOR 				= parser.getIntParameter("HOLE15_SCOR", 0);
			int hole16_SCOR 				= parser.getIntParameter("HOLE16_SCOR", 0);
			int hole17_SCOR 				= parser.getIntParameter("HOLE17_SCOR", 0);
			int hole18_SCOR 				= parser.getIntParameter("HOLE18_SCOR", 0);
			int hit_CNT 					= parser.getIntParameter("HIT_CNT", 0);
			int eg_NUM 						= parser.getIntParameter("EG_NUM", 0);
			int bid_NUM 					= parser.getIntParameter("BID_NUM", 0);
			int par_NUM 					= parser.getIntParameter("PAR_NUM", 0);
			int bog_NUM 					= parser.getIntParameter("BOG_NUM", 0);
			int dob_BOG_NUM 				= parser.getIntParameter("DOB_BOG_NUM", 0);
			int trp_BOG_NUM 				= parser.getIntParameter("TRP_BOG_NUM", 0);
			int etc_BOG_NUM 				= parser.getIntParameter("ETC_BOG_NUM", 0);
			String round_MEMO_CTNT 			= parser.getParameter("ROUND_MEMO_CTNT", "").trim();
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setInt("SEQ_NO", seq_NO);
			dataSet.setString("GREEN_NM", green_NM);
			dataSet.setString("ROUND_DATE", round_DATE);
			dataSet.setString("CURS_NM", curs_NM);
			dataSet.setString("HADC_NUM", hadc_NUM);
			dataSet.setInt("HOLE01_SCOR", hole01_SCOR);
			dataSet.setInt("HOLE02_SCOR", hole02_SCOR);
			dataSet.setInt("HOLE03_SCOR", hole03_SCOR);
			dataSet.setInt("HOLE04_SCOR", hole04_SCOR);
			dataSet.setInt("HOLE05_SCOR", hole05_SCOR);
			dataSet.setInt("HOLE06_SCOR", hole06_SCOR);
			dataSet.setInt("HOLE07_SCOR", hole07_SCOR);
			dataSet.setInt("HOLE08_SCOR", hole08_SCOR);
			dataSet.setInt("HOLE09_SCOR", hole09_SCOR);
			dataSet.setInt("HOLE10_SCOR", hole10_SCOR);
			dataSet.setInt("HOLE11_SCOR", hole11_SCOR);
			dataSet.setInt("HOLE12_SCOR", hole12_SCOR);
			dataSet.setInt("HOLE13_SCOR", hole13_SCOR);
			dataSet.setInt("HOLE14_SCOR", hole14_SCOR);
			dataSet.setInt("HOLE15_SCOR", hole15_SCOR);
			dataSet.setInt("HOLE16_SCOR", hole16_SCOR);
			dataSet.setInt("HOLE17_SCOR", hole17_SCOR);
			dataSet.setInt("HOLE18_SCOR", hole18_SCOR);
			dataSet.setInt("HIT_CNT", hit_CNT);
			dataSet.setInt("EG_NUM", eg_NUM);
			dataSet.setInt("BID_NUM", bid_NUM);
			dataSet.setInt("PAR_NUM", par_NUM);
			dataSet.setInt("BOG_NUM", bog_NUM);
			dataSet.setInt("DOB_BOG_NUM", dob_BOG_NUM);
			dataSet.setInt("TRP_BOG_NUM", trp_BOG_NUM);
			dataSet.setInt("ETC_BOG_NUM", etc_BOG_NUM);
			dataSet.setString("ROUND_MEMO_CTNT", round_MEMO_CTNT);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMtScoreUpdDaoProc proc = (GolfMtScoreUpdDaoProc)context.getProc("GolfMtScoreUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);		        

	        String returnUrlTrue = "GolfMtScoreList.do";
	        String returnUrlFalse = "GolfMtScoreUpdForm.do";
			
			if (editResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "������ ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
	        
			// 05. Return �� ����			
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}

}
