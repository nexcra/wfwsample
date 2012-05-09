/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemberShipPopActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ȸ���� �ü�ǥ �μ�
*   �������  : Golf
*   �ۼ�����  : 2009-06-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.lounge;

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
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfMemberShipListDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfMemberShipPopActn extends GolfActn{
	
	public static final String TITLE = "ȸ���� �ü�ǥ �μ�";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String gf_nm = "";
		String price_cd = "";
		String area_cd = "";
		boolean flag1 = false;
		boolean flag2 = false;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			paramMap.remove("s_gf_hole_cd");
			paramMap.remove("s_gf_area_cd");
			
			
			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			String[] gf_price_cd		= parser.getParameterValues("s_gf_price_cd", "");  // ���ݺ�
			String[] gf_area_cd		= parser.getParameterValues("s_gf_area_cd", "");  // ������
			
			for (int i = 0; i < gf_price_cd.length; i++) { 		
				if (gf_price_cd[i] != null && gf_price_cd[i].length() > 0) {
					price_cd += ","+ gf_price_cd[i];
					flag1 = true;
				}
			}
			
			//debug("flag1 ====> "+ flag1);
			
			for (int i = 0; i < gf_area_cd.length; i++) { 		
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					area_cd += ","+ gf_area_cd[i];
					flag2 = true;
				}
			}
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMemberShipListDaoProc proc = (GolfMemberShipListDaoProc)context.getProc("GolfMemberShipListDaoProc");
			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			DbTaoResult membershipListResult = (DbTaoResult) proc.execute(context, request, dataSet, gf_price_cd, gf_area_cd);
			
			// �ڵ� ��ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0021", "Y"); //�����������ڵ�
			
			
			paramMap.put("resultSize", String.valueOf(membershipListResult.size()));
			
			request.setAttribute("membershipListResult", membershipListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			request.setAttribute("coopCpSel", coopCpSel);
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("PriceCd", (flag1 ? price_cd.substring(1,price_cd.length()) : ""));
	        request.setAttribute("AreaCd", (flag2 ? area_cd.substring(1,area_cd.length()) : ""));
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
