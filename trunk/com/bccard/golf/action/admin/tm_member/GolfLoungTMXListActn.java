/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ��ŷ > �����̾� > ������ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.initech.dbprotector.CipherClient;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext; 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.waf.common.DateUtil;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.action.admin.tm_member.GolfLoungTMXListProc;

/******************************************************************************
* Topn
* @author	(��)�̵������ 
* @version	1.0  
******************************************************************************/
public class GolfLoungTMXListActn extends GolfActn{ 
	
	public static final String TITLE = "TMȸ�� ��ȸ > ����";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü.  
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.  
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

    DbTaoResult taoResult = null;
		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ���� 
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();
			
			String action_key = super.getActionKey(context);

			// Request �� ����
			String start_date			= parser.getParameter("start_date", "");
			String end_date			= parser.getParameter("end_date", "");
			String jumin_no			= "";
			String hg_nm			= "";
			String tb_rslt_clss			= parser.getParameter("SCH_STATE", "00");
			String	sch_type		= parser.getParameter("SCH_TYPE", "");
			String	sch_text		= parser.getParameter("SCH_TEXT", "");	
			String	acpt_chnl_clss		= parser.getParameter("acpt_chnl_clss", "1");
			
			long page_no			= parser.getLongParameter("page_no", 1);
			long record_size	= parser.getLongParameter("record_size", 20);
			
			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			
			String	st_gb		= parser.getParameter("ST_GB", "1");
			
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;

			
			
			//if ("".equals(start_date)) start_date= DateUtil.currdate("yyyy-MM-dd");
			//if ("".equals(end_date)) end_date= DateUtil.currdate("yyyy-MM-dd");
			//start_date = GolfUtil.rplc(start_date, "-", "");
			//end_date = GolfUtil.rplc(end_date, "-", "");

			if ("jumin_no".equals(sch_type)) jumin_no=sch_text;
			if ("hg_nm".equals(sch_type)) hg_nm=sch_text;	
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("start_date", sch_date_st); //
			dataSet.setString("end_date", sch_date_ed); //
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("hg_nm", hg_nm); //
			dataSet.setString("tb_rslt_clss", tb_rslt_clss); //
			      
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size); //
			  
			dataSet.setString("acpt_chnl_clss", acpt_chnl_clss);
			dataSet.setString("st_gb", st_gb); //

			
		

			// 04.���� ���̺�(Proc) ��ȸ
			
//		  GolfLoungTMXlistProc proc = (GolfLoungTMXlistProc)context.getProc("GolfLoungTMXlistProc");
	    
			GolfLoungTMXListProc proc = new GolfLoungTMXListProc();
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, dataSet);

			request.setAttribute("ListResult", listResult);
			request.setAttribute("paramMap", paramMap);
				
			taoResult = (DbTaoResult)proc.execute(context, dataSet);	// 
			taoResult.next();
			String sRESULT = taoResult.getString("RESULT");

			if ("00".equals(sRESULT))
				paramMap.put("total_cnt", taoResult.getString("TOT_CNT"));
			else
				paramMap.put("total_cnt", "0");

			paramMap.put("resultSize", String.valueOf(taoResult.size()));

			paramMap.put("start_date", DateUtil.format(sch_date_st,"yyyyMMdd","yyyy-MM-dd"));
			paramMap.put("end_date", DateUtil.format(sch_date_st,"yyyyMMdd","yyyy-MM-dd"));
			paramMap.put("SCH_STATE", tb_rslt_clss);
			paramMap.put("SCH_TYPE", sch_type);
			paramMap.put("SCH_TEXT", sch_text);
			paramMap.put("acpt_chnl_clss", acpt_chnl_clss);
			paramMap.put("ST_GB",st_gb);
			
			request.setAttribute("paramMap",paramMap);
			request.setAttribute("page_no",String.valueOf(page_no));
			request.setAttribute("resultList",taoResult); 
			request.setAttribute("record_size", String.valueOf(record_size));

				        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}

