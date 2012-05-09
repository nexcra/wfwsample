/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBkExcelActn
*   �ۼ���    : ���񽺰����� ���ϻ�� ������
*   ����      : ������ ��ŷ �̺�Ʈ ����Ʈ ���� ��ȸ
*   �������  : Golf
*   �ۼ�����  : 2009-09-01
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
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBkListDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBkExcelActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ �̺�Ʈ ����Ʈ ���� ��ȸ";

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
		
		String key = getActionKey(context);

		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
		
			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String sgr_nm			= parser.getParameter("sgr_nm", "");
			String sevent_yn		= parser.getParameter("sevent_yn", "");
			String sbkps_sdate		= parser.getParameter("sbkps_sdate", "");
			String sbkps_edate		= parser.getParameter("sbkps_edate", "");
			String sreg_sdate		= parser.getParameter("sreg_sdate", "");
			String sreg_edate		= parser.getParameter("sreg_edate", "");

			String sort			= parser.getParameter("SORT", "0001"); //0001:�����̾� 0002:��3��ŷ
			
			sbkps_sdate = sbkps_sdate.length() == 10 ? DateUtil.format(sbkps_sdate, "yyyy-MM-dd", "yyyyMMdd"): "";
			sbkps_edate = sbkps_edate.length() == 10 ? DateUtil.format(sbkps_edate, "yyyy-MM-dd", "yyyyMMdd"): "";
			sreg_sdate = sreg_sdate.length() == 10 ? DateUtil.format(sreg_sdate, "yyyy-MM-dd", "yyyyMMdd"): "";
			sreg_edate = sreg_edate.length() == 10 ? DateUtil.format(sreg_edate, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SGR_NM", sgr_nm);
			dataSet.setString("SEVENT_YN", sevent_yn);
			dataSet.setString("SBKPS_SDATE", sbkps_sdate);
			dataSet.setString("SBKPS_EDATE", sbkps_edate);
			dataSet.setString("SREG_SDATE", sreg_sdate+"000000");
			dataSet.setString("SREG_EDATE", sreg_edate+"235959");
			dataSet.setString("SORT", sort);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntBkListDaoProc proc = (GolfAdmEvntBkListDaoProc)context.getProc("GolfAdmEvntBkListDaoProc");
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
			DbTaoResult evntPreBkTimeListResult = (DbTaoResult) proc.execute(context, request, dataSet, key);
			DbTaoResult titimeGreenListResult = (DbTaoResult) proc2.execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(evntPreBkTimeListResult.size()));
			
			request.setAttribute("evntPreBkTimeListResult", evntPreBkTimeListResult);
			request.setAttribute("titimeGreenListResult", titimeGreenListResult);
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
