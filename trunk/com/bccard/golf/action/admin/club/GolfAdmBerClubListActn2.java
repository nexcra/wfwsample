/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMngClubListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ > ��ü ��ȣȸ ���� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-07-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.club;

import java.io.IOException; 
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.admin.club.GolfAdmBerClubListDaoProc;
/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmBerClubListActn2 extends GolfActn{ 
	  
	public static final String TITLE = "������ > ��ȣȸ ������ ����Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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
			long page_no		= parser.getLongParameter("page_no", 1L);		// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);	// ����������¼�
			String subkey		= parser.getParameter("subkey", "");			// ����޴� ����
			String search_sel	= parser.getParameter("search_sel", "");
			String search_word	= parser.getParameter("search_word", "");
			String search_dt1	= parser.getParameter("search_dt1", "");
			String search_dt2	= parser.getParameter("search_dt2", "");
			String sckd_code	= parser.getParameter("sckd_code", "");
			String scnsl_yn		= parser.getParameter("scnsl_yn", "");
			String sprgs_yn		= parser.getParameter("sprgs_yn", "");
			String scoop_cp_cd	= parser.getParameter("scoop_cp_cd", ""); 		//0001:���������� 0002:��������
			String p_idx		= parser.getParameter("p_idx", ""); 			// ��ȣȸ �Ϸù�ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SEARCH_DT1", search_dt1);
			dataSet.setString("SEARCH_DT2", search_dt2);
			dataSet.setString("SCKD_CODE", sckd_code);
			dataSet.setString("SCNSL_YN", scnsl_yn);
			dataSet.setString("SPRGS_YN", sprgs_yn);
			dataSet.setString("SCOOP_CP_CD", scoop_cp_cd);
			dataSet.setString("p_idx", p_idx);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmBerClubListDaoProc proc = (GolfAdmBerClubListDaoProc)context.getProc("GolfAdmBerClubListDaoProc");
			DbTaoResult maniaListResult = (DbTaoResult) proc.execute(context, request, dataSet);

			//GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");//@ ���� �̾ƿ��� 
			//DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0042", "Y"); //@ ���� �̾ƿ��� 

			paramMap.put("resultSize", String.valueOf(maniaListResult.size()));
			request.setAttribute("maniaListResult", maniaListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			
			
			
			// ��ü 0��  [ 0/0 page] ���� ��������
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
			
			if (maniaListResult != null && maniaListResult.isNext()) {
				maniaListResult.first();
				maniaListResult.next();
				if (maniaListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)maniaListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)maniaListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
			
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));
			
			//05. Return �� ����	
			request.setAttribute("maniaListResult", maniaListResult);
			//request.setAttribute("coopCpSel", coopCpSel);	//@ ���� �̾ƿ��� 
			request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
